package com.b2wdigital.vegas.ingest

import com.b2wdigital.vegas.ingest.Definitions.{BatchExecutionDefinition, IngestionDefinition}
import com.b2wdigital.vegas.ingest.exception.IngestionException
import com.b2wdigital.vegas.ingest.input.{DataInput, KafkaBatchConsumer, ZookeeperPersistency}
import com.b2wdigital.vegas.ingest.execution.Execution
import com.b2wdigital.vegas.ingest.recovery._
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import play.api.libs.json._


/*
 * a unit testing for a Batch Execution.
 */
class MockableBatchExecution(ingestionDefinition : IngestionDefinition,sc : SparkContext) extends Execution {


  // TODO : implement the same to StreamConsumer or make Recovery part of Execution
  val recoveryDefinition = ingestionDefinition.recovery

  val zkHost = recoveryDefinition.host
  val zkPath = recoveryDefinition.path
  val zkSessionTimeout = recoveryDefinition.sessionTimeout
  val zkConnectionTimeout = recoveryDefinition.connectionTimeout

  val recoveryManager = new KafkaRecoveryManager(new ZookeeperPersistency(zkHost,zkPath,zkSessionTimeout,zkConnectionTimeout))

  val dataInput = new KafkaBatchConsumer(ingestionDefinition, sc , recoveryManager)

  var lastDataIngested:RDD[String] = _

  override def run = {
    dataInput.read(lambda,errorHandler)
  }

  def lambda(data : RDD[String]) = {
    lastDataIngested = data
  }

  def errorHandler(e : IngestionException,recoveryManager: RecoveryManager) = {
    recoveryManager.rollback(e.context)
  }

}
