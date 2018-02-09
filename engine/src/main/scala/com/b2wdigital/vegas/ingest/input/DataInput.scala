package com.b2wdigital.vegas.ingest.input


import com.b2wdigital.vegas.ingest.Definitions.IngestionDefinition
import com.b2wdigital.vegas.ingest.recovery._
import com.b2wdigital.vegas.ingest.exception.IngestionException

import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext
import org.apache.spark.streaming.StreamingContext

trait DataInput {

  def read (lambda : (RDD[String]) => Unit,errorHandler : (IngestionException,RecoveryManager) => Unit)
}

object DataInput {

  def apply(definition : IngestionDefinition, context : StreamingContext,recoveryManager: RecoveryManager) : DataInput = {

    /*
      here goes the known input data types list for Streamings
    */
    definition.data_input.atype match {

      case KafkaStreamConsumer.ID => new KafkaStreamConsumer(definition,context,recoveryManager.asInstanceOf[KafkaRecoveryManager])
      case _ => throw new IllegalArgumentException("no DataInput known as : " + definition.data_input.atype)

    }
  }

  def apply(definition : IngestionDefinition, context : SparkContext,recoveryManager : RecoveryManager) : DataInput = {

    /*
      here goes the known input data types list for Streamings
    */
    definition.data_input.atype match {

      case KafkaBatchConsumer.ID =>

        // TODO : implement the same to StreamConsumer or make Recovery part of Execution
        new KafkaBatchConsumer(definition, context , recoveryManager.asInstanceOf[KafkaRecoveryManager])


      case Hdfs.ID => new Hdfs(definition, context)
      case _ => throw new IllegalArgumentException("no DataInput known as : " + definition.data_input.atype)

    }
  }

  def apply(definition:IngestionDefinition) : DataInput = {

    /*
      here goes the known input data types list for Batches
    */
    throw new IllegalArgumentException("no DataInput known as : " + definition.data_input.atype)
  }

}
