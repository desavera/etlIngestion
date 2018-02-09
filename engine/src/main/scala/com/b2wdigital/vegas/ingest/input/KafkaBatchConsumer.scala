/*
this implementation follows some concepts from :
https://thehoard.blog/how-kafkas-storage-internals-work-3a29b02e026
https://www.programcreek.com/scala/kafka.common.TopicAndPartition
https://www.programcreek.com/scala/kafka.consumer.SimpleConsumer
https://gist.github.com/ashrithr/5811266
 */

package com.b2wdigital.vegas.ingest.input

import com.b2wdigital.vegas.ingest.Definitions.{IngestionDefinition, KafkaBatchDefinition, BatchExecutionDefinition, RecoveryDefinition}
import com.b2wdigital.vegas.ingest.recovery._
import com.b2wdigital.vegas.ingest.exception.IngestionException
import com.b2wdigital.vegas.ingest.exception.NoCheckedContextException
import kafka.serializer.StringDecoder
import kafka.common.TopicAndPartition
import kafka.message.MessageAndMetadata
import kafka.consumer.SimpleConsumer;
import kafka.api.OffsetRequest
import kafka.api.OffsetFetchResponse
import kafka.api.OffsetFetchRequest
import kafka.api.PartitionOffsetRequestInfo
import kafka.common.OffsetMetadataAndError
import kafka.common.ErrorMapping
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.kafka.{OffsetRange,HasOffsetRanges,KafkaUtils}
import org.apache.kafka.clients.consumer._


import org.apache.spark.SparkContext
import play.api.libs.json._
import grizzled.slf4j.Logger

import scala.util.control.NonFatal
import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer

import java.util.Date


class KafkaBatchConsumer(ingestionDefinition : IngestionDefinition, context: SparkContext, recoveryManager:KafkaRecoveryManager) extends DataInput {

  lazy val logger = Logger(getClass())

  logger.info("starting kafka batch definition parsing ...")
  val batchDefinition = Json.fromJson[BatchExecutionDefinition](ingestionDefinition.execution.args).get
  val inputDefinition = Json.fromJson[KafkaBatchDefinition](ingestionDefinition.data_input.args).get
  val recoveryDefinition = ingestionDefinition.recovery
  val mode = recoveryDefinition.mode

  // by default partition ZERO is the initial partition to any topic...
  val initialPartition = 0

  // TODO : get this from RecoveryManager ...
  val zkHost = recoveryDefinition.host

  logger.info("finished kafka batch definition parsing ...")

  val topic = inputDefinition.topic
  var kafka_params = Map[String,String]("metadata.broker.list" -> inputDefinition.brokers,"group.id" -> inputDefinition.groupId,"zookeeper.connect" -> zkHost)

  override def read(lambda : (RDD[String]) => Unit,errorHandler : (IngestionException,RecoveryManager) => Unit) = {

    logger.info("starting chunk data consume from kafka queue...")

    var rdd = KafkaUtils.createRDD[String, String, StringDecoder, StringDecoder](context, kafka_params,getOffsetRanges())

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
          lastSucessfulContext = Some(recoveryManager.recoverContext(kafka_params,topic,context,false))

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


    logger.info("finished chunk data consume from kafka queue...")
  }

  def getOffsetRanges (): Array[OffsetRange] = {

    var recoveredCtx = new RecoveryContext()

    mode match {

      case "from_last_on" =>

        recoveredCtx = recoveryManager.recoverContext(kafka_params,topic,context,false)

      case "from_last_in" =>

        recoveredCtx = recoveryManager.recoverContext(kafka_params,topic,context,true)

      case "from_beginning" =>
    
        // fetch the first offset for partition ZERO of the topic ...
        // TODO : what if it does not have ZERO as the earliest ???
        // TODO : what if retention has deleted from several partitions... what is the earliest ?
        val earliestOffset = getEarliestOffset()

        return Array(OffsetRange(topic, initialPartition, earliestOffset,earliestOffset + batchDefinition.bucketSize.toInt)) 

      case _ =>

        logger.error("ERROR : no matching RecoveryMode for : " + mode)
        throw new IllegalArgumentException(mode)
    }


    // for the last in/on recovery modes...

    val recoveredOffsets:Array[OffsetRange] = recoveredCtx(KafkaRecoveryManager.RDD).asInstanceOf[HasOffsetRanges].offsetRanges
    var ingestionOffsets:ArrayBuffer[OffsetRange] = new ArrayBuffer(recoveredOffsets.length)

    for(i <- 0 until recoveredOffsets.length) {

      val topic = recoveredOffsets(i).topic
      val partition = recoveredOffsets(i).partition
      val from = recoveredOffsets(i).fromOffset
      val until = from + batchDefinition.bucketSize.toInt 

      ingestionOffsets += OffsetRange(topic,partition,from,until)
    }

    return ingestionOffsets.toArray
  }

  def getEarliestOffset() : Long = {

    val timeout = 10000
    val bufferSize = 100000        

    val host = inputDefinition.brokers.split(":")(0)
    val port = inputDefinition.brokers.split(":")(1).toInt

    val groupId = "vegas-ingest"

    val consumer = new SimpleConsumer(host,port,timeout,bufferSize,"offsetfetcher");

    val topicAndPartition = new TopicAndPartition(topic, initialPartition)

    // a single message is enough to indicate and fetch what would be the first offset to be fetched
    val request = OffsetRequest(Map(topicAndPartition -> PartitionOffsetRequestInfo(OffsetRequest.EarliestTime,1)))

    val offsets = consumer.getOffsetsBefore(request).partitionErrorAndOffsets(topicAndPartition).offsets

    return offsets.head
  }
}

object KafkaBatchConsumer {

  val ID = "kafka"

}
