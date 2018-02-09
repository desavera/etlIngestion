/*
this implementation follows some concepts from :
https://thehoard.blog/how-kafkas-storage-internals-work-3a29b02e026
 */

package com.b2wdigital.vegas.ingest.input

import com.b2wdigital.vegas.ingest.Definitions.{IngestionDefinition, KafkaDirectStreamDefinition, StreamExecutionDefinition,RecoveryDefinition}
import com.b2wdigital.vegas.ingest.recovery._
import com.b2wdigital.vegas.ingest.exception.IngestionException
import com.b2wdigital.vegas.ingest.exception.NoCheckedContextException
import kafka.serializer.StringDecoder
import kafka.common.TopicAndPartition
import kafka.message.MessageAndMetadata
import org.apache.spark.streaming._
import org.apache.spark.streaming.{kafka, _}
import org.apache.spark.streaming.kafka._
import org.apache.spark.streaming.dstream.{DStream, InputDStream, ReceiverInputDStream}
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext
import play.api.libs.json._
import grizzled.slf4j.Logger

import scala.util.control.NonFatal


class KafkaStreamConsumer(ingestionDefinition : IngestionDefinition, context : StreamingContext,recoveryManager : KafkaRecoveryManager) extends DataInput {


  lazy val logger = Logger(getClass())

  logger.info("starting kafka direct stream definition parsing ...")
  val inputDefinition = Json.fromJson[KafkaDirectStreamDefinition](ingestionDefinition.data_input.args).get
  val recoveryDefinition = ingestionDefinition.recovery
  logger.info("finished kafka direct stream definition parsing ...")

  val mode = recoveryDefinition.mode
  val zkHost = recoveryDefinition.host

  val topic = inputDefinition.topic

  // TODO : the bucket size limits params should go at SparkContext !
  var kafka_params = Map[String,String]("metadata.broker.list" -> inputDefinition.brokers,
                                        "group.id" -> inputDefinition.groupId,
                                        "zookeeper.connect" -> zkHost)

  logger.info("creating kafka direct stream ...")

  val inputDStream = createDStream()

  logger.info("successfully created kafka direct stream ...")

  def createDStream(): InputDStream[(String, String)] = {

    mode match {

      case "from_last_on" =>

        val messageHandler = (mmd: MessageAndMetadata[String, String]) => (mmd.key,mmd.message)

        val fromCursor = recoveryManager.recover(topic,false)(KafkaRecoveryManager.FROM_BUFFER_CURSOR).asInstanceOf[Map[TopicAndPartition,Long]]

        KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder
          , (String, String)](context, kafka_params, fromCursor, messageHandler)

      case "from_last_in" =>

        val messageHandler = (mmd: MessageAndMetadata[String, String]) => (mmd.key,mmd.message)

        val fromCursor = recoveryManager.recover(topic,true)(KafkaRecoveryManager.FROM_BUFFER_CURSOR).asInstanceOf[Map[TopicAndPartition,Long]]

        KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder
          , (String, String)](context, kafka_params, fromCursor, messageHandler)

      case "from_beginning" =>

        kafka_params +=  ("auto.offset.reset" -> "smallest")

        KafkaUtils.createDirectStream[String,String,StringDecoder,StringDecoder](context,kafka_params,Set[String](inputDefinition.topic))

      case "from_now_on" =>

        kafka_params += ("auto.offset.reset" -> "largest")

        KafkaUtils.createDirectStream[String,String,StringDecoder,StringDecoder](context,kafka_params,Set[String](inputDefinition.topic))

      case _ =>

        logger.error("ERROR : no matching RecoveryMode for : " + mode)
        throw new IllegalArgumentException(mode)
    }
  }

  override def read (lambda : (RDD[String]) => Unit,errorHandler : (IngestionException,RecoveryManager) => Unit) = {

    logger.info("starting chunk data consume from kafka queue...")

    inputDStream.foreachRDD((rdd, time) => {

      logger.info("about to ingest an rdd of size : " + rdd.count())

      if (rdd.count() > 0) {


        /*
        WRITE AHEAD logging strategy... saves the last sucessful context available
         */
        var checkedContext = false
        var lastSucessfulContext:Option[RecoveryContext] = None

        try {

          /*
          an Exception might happen due to a failure in the RecoveryManager...
           */
          lastSucessfulContext = Some(recoveryManager.recoverContext(kafka_params,topic,context.sparkContext,true))

        } catch {

          // NoCheckedContextException ignored...
          case NonFatal(e) =>
              logger.info("a first ingestion not checked yet...")
        }

        try {


          logger.info("starting context save")
          recoveryManager.checkpoint(new RecoveryContext() += (KafkaRecoveryManager.RDD -> rdd))
          logger.info("finished context save")

          checkedContext = true

          val msg = rdd.map(_._2)

          /*
          an Exception might happen due to a failure in the Pipeline...
           */

          // calls the lambda function for the pipeline follow up
          logger.info("starting data processing pipeline")
          lambda(msg)
          logger.info("finished data processing pipeline")

        } catch {

          // TBD : the Fatal ones...
          case NonFatal(e) =>

            if (checkedContext) {

              logger.info("starting NonFatal error handling...")
              errorHandler(new IngestionException(e.getMessage,lastSucessfulContext),recoveryManager)
              logger.info("finished NonFatal error handling...")

            }
        }
      } else

        logger.info("no chunk data consumed from kafka queue...")

    })

    logger.info("finished chunk data consume from kafka queue...")

  }
}

object KafkaStreamConsumer {

  val ID = "kafka"

}
