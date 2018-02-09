package com.b2wdigital.vegas.ingest.execution

import com.b2wdigital.vegas.ingest.Definitions.{IngestionDefinition, StreamExecutionDefinition}
import com.b2wdigital.vegas.ingest.input.DataInput
import com.b2wdigital.vegas.ingest.output.DataOutput
import com.b2wdigital.vegas.ingest.transform.Transformer
import com.b2wdigital.vegas.ingest.recovery._
import com.b2wdigital.vegas.ingest.exception.IngestionException

import grizzled.slf4j.Logger
import play.api.libs.json._

import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.SparkConf
import org.apache.spark.streaming.Seconds

import scala.util.control.NonFatal


class StreamingExecution(ingestionDefinition : IngestionDefinition,recoveryManager : RecoveryManager) extends Execution {

  lazy val logger = Logger(getClass())

  logger.info("starting stream execution definition parsing ...")
  val streamDefinition = Json.fromJson[StreamExecutionDefinition](ingestionDefinition.execution.args).get
  logger.info("finished stream execution definition parsing ...")

  val transformers = Transformer(ingestionDefinition)

  /*
  we are going on a NO CHECKPOINT strategy so createOnError should be TRUE
   */
  logger.info("creating StreamingContext...")
  val context = StreamingContext.getOrCreate(streamDefinition.directory,
                                             createStreamingContextFunction _,
                                             new org.apache.hadoop.conf.Configuration,
                                             true)

  logger.info("successfully created StreamingContext...")

  var dataInput = DataInput(ingestionDefinition,context,recoveryManager)

  logger.info("successfully created DataInput...")

  val dataOutput = DataOutput(ingestionDefinition,context.sparkContext)

  logger.info("successfully created DataOutput...")


  override def run = {

    dataInput.read(lambda,errorHandler)

    // Start the computation
    context.start()
    context.awaitTermination()
  }

  def lambda(data : RDD[String]) = {

    logger.info("collection data n rows consumed from topic : " + data.count())

    logger.info("starting data transform...")
    val transformedData = transformers.transform(data)
    logger.info("finished data transform...")

    logger.info("starting data output...")
    dataOutput.write(transformedData)
    logger.info("finished data output...")

  }

  def errorHandler(e : IngestionException,recoveryManager: RecoveryManager) = {

    logger.info("starting rollback for last ingestion...")
    recoveryManager.rollback(e.context)
    logger.info("finished rollback for last ingestion...")

    logger.info(e.message)
  }


  def createStreamingContextFunction() : StreamingContext = {

    val conf = new SparkConf().setMaster(streamDefinition.master).setAppName(ingestionDefinition.name)
    conf.set("spark.streaming.backpressure.enabled","true")
    conf.set("spark.streaming.receiver.maxRate",streamDefinition.bucketSize.toString)
    conf.set("spark.streaming.kafka.maxRatePerPartition",streamDefinition.bucketSize.toString)

    new StreamingContext(conf, Seconds(streamDefinition.interval.toLong))
  }
}
