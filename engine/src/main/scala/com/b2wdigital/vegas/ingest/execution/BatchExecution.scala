package com.b2wdigital.vegas.ingest.execution

import com.b2wdigital.vegas.ingest.Definitions.{BatchExecutionDefinition, IngestionDefinition}
import com.b2wdigital.vegas.ingest.exception.IngestionException
import com.b2wdigital.vegas.ingest.input.DataInput
import com.b2wdigital.vegas.ingest.output.DataOutput
import com.b2wdigital.vegas.ingest.recovery._
import com.b2wdigital.vegas.ingest.transform.Transformer
import grizzled.slf4j.Logger
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import play.api.libs.json._


class BatchExecution(ingestionDefinition : IngestionDefinition,recoveryManager : RecoveryManager) extends Execution {

  lazy val logger = Logger(getClass())

  logger.info("starting batch execution definition parsing ...")
  val batchDefinition = Json.fromJson[BatchExecutionDefinition](ingestionDefinition.execution.args).get
  logger.info("finished batch execution definition parsing ...")

  val conf = new SparkConf().setMaster(batchDefinition.master).setAppName(ingestionDefinition.name)
  val sc = SparkContext.getOrCreate(conf)

  val transformers = Transformer(ingestionDefinition)

  val dataInput = DataInput(ingestionDefinition, sc,recoveryManager)
  logger.info("successfully created DataInput...")

  val dataOutput = DataOutput(ingestionDefinition,sc)
  logger.info("successfully created DataOutput...")


  override def run = {

    dataInput.read(lambda,errorHandler)

  }

  def lambda(data : RDD[String]) = {

    val transformedData = transformers.transform(data)
    dataOutput.write(transformedData)
  }

  def errorHandler(e : IngestionException,recoveryManager: RecoveryManager) = {

    logger.info("starting rollback for last ingestion...")
    recoveryManager.rollback(e.context)
    logger.info("finished rollback for last ingestion...")

    logger.info(e)
  }

}
