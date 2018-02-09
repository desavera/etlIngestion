package com.b2wdigital.vegas.ingest.recovery

import scala.util.control.NonFatal
import kafka.common.TopicAndPartition
import kafka.serializer.StringDecoder
import org.apache.spark.streaming.kafka.HasOffsetRanges
import org.apache.spark.streaming.kafka.{KafkaUtils, OffsetRange}
import org.apache.spark.SparkContext
import grizzled.slf4j.Logger
import com.b2wdigital.vegas.ingest.exception.NoCheckedContextException

/*

 This component is responsible for managing the recovery process of a Kafka context. It basically consists of two major functionalities of a save (i.e. checkpoint) and load (i.e. recover). The recover can happen in two formats (Offsets and RecoveryContext). A third extra funcionality of a rollback is also provided that basically overwrites a context by calling the save method.

*/

object KafkaRecoveryManager {

  /* 
   Key values just for clarity in order to make a difference of the recover method return type.
  */
  final val RDD = """this is the RDD ingested"""
  final val FROM_BUFFER_CURSOR = """this is the list of topic + partition + offsets for all ingested TopicAndPartitions so far"""
}

case class KafkaRecoveryManager(persist : RecoveryPersistency) extends RecoveryManager {

  lazy val logger = Logger(getClass())

  // the SAVE method
  def checkpoint(context : RecoveryContext) = {

    val offsets:Option[Array[OffsetRange]] = Some(context(KafkaRecoveryManager.RDD).asInstanceOf[HasOffsetRanges].offsetRanges)

    var key:String = ""
    var value = new StringBuilder

    if (offsets.isDefined) {

      for(i <- 0 until offsets.get.length) {

        val topic = offsets.get(i).topic
        val partition = offsets.get(i).partition
        val from = offsets.get(i).fromOffset
        val until = offsets.get(i).untilOffset 

        key = topic 

        val partitionAndOffset = partition.toString + ':' + until.toString

        value ++= partitionAndOffset + '|'

      }

      logger.info("writing to persistency value : " + value)

      persist.save(key,value.toString)

    } else logger.error("ERROR : problems extracting the offset information for checkpoint ...")

  }

  // the LOAD method for a full RecoveryContext holding OFFSETs
  def recover(key : String,inclusive : Boolean = false) : RecoveryContext = {

    logger.info("recovering context for : " + key)
    new RecoveryContext() += (KafkaRecoveryManager.FROM_BUFFER_CURSOR -> recoverOffsets(key,inclusive))
  }


  // the LOAD method for the full RecoveryContext holding a RDD
  def recoverContext(kafka_params : Map[String,String],topic : String,context : SparkContext,inclusive : Boolean) : RecoveryContext = {

    val recovered = recoverOffsets(topic,inclusive)

    if (recovered.size == 0)
       throw new NoCheckedContextException
 
    // TBD : implement for multiple partitions...
    val (topicAndPartition,until) =  recovered.head

    val partition = topicAndPartition.partition

    // this represets a single offset actually
    val offsetRanges = Array(OffsetRange(topic,partition,until - 1,until))

    val checkedRDD = KafkaUtils.createRDD[String, String, StringDecoder, StringDecoder](context, kafka_params,offsetRanges)

    new RecoveryContext() += (KafkaRecoveryManager.RDD -> checkedRDD)
  }

  // a generic LOAD method for offsets
  def recoverOffsets(key : String, inclusive : Boolean) : scala.collection.immutable.Map[TopicAndPartition,Long] = {

    val checkedOffsets = scala.collection.mutable.Map[TopicAndPartition,Long]()

    val allPartitionsAndOffsets:Option[String] = persist.load(key)
 
    if (allPartitionsAndOffsets.isDefined) {

        for (partitionAndOffset <- allPartitionsAndOffsets.get.split('|')) {

          val topic = key
          val partition = partitionAndOffset.split(':')(0)

          /*
          these are the checked successfully persisted OFFSETs ... so in a recovery context
           we should start from the NEXT OFFSETs (i.e. +1)
           TBD : what if it does not exist ???
           */
          var next_flag = 1
          if (inclusive) next_flag = 0

          val until = (partitionAndOffset.split(':')(1)).toLong + next_flag

          checkedOffsets += (new TopicAndPartition(topic, partition.toInt) -> until.toLong)

        }
    }

    checkedOffsets.toMap

  }

  // the OVERWRITE (SAVE OVER) method
  def rollback(context : Option[RecoveryContext]) = {

    logger.info("starting context rollback...")
    if (context.isDefined)
      checkpoint(context.get)
    else
      logger.info("no ingestion context available for rollback...")

    logger.info("finished context rollback...")
  }

}
