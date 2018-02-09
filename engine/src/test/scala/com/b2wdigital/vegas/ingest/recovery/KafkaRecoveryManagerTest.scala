package com.b2wdigital.vegas.ingest.recovery

import com.b2wdigital.vegas.ingest.SparkContextSpec
import com.b2wdigital.vegas.ingest.MockableEvent
import com.b2wdigital.vegas.ingest.MockableEventsRDD

import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

import kafka.common.TopicAndPartition
import org.apache.spark.{SparkConf, SparkContext}

/*
 * A unit testing for KafkaRecovery using MemoryPeristency for simplicity. 
 */
class KafkaRecoveryManagerTest extends SparkContextSpec {

  // leave ZooKeeper for the integrated tests
  val recoveryManager = new KafkaRecoveryManager(new MemMapPersistency)
  val topic = "test_topic"
  val partition = 0

  "A checkpoint" should "be saved" in {

    // mocks a couple of events
    val events = sc.parallelize(List[MockableEvent](new MockableEvent("bla","1/1/2018"),
       new MockableEvent("blabla","2/1/2018"),
       new MockableEvent("blablabla","3/1/2018")))

    val rdd = new MockableEventsRDD(topic,events)

    recoveryManager.checkpoint(new RecoveryContext() += (KafkaRecoveryManager.RDD -> rdd))
  }

  "The recovery context" should "return the expected offsets" in {

    val topicAndPartition = new TopicAndPartition(topic,partition)
    // exclusive
    val exclusiveOffset = recoveryManager.recover(topic)(KafkaRecoveryManager.FROM_BUFFER_CURSOR).asInstanceOf[Map[TopicAndPartition,Long]].get(topicAndPartition)
    //for ((k,v) <- exclusiveCtx) printf("key: %s, value: %s\n", k, v)

    // inclusive
    val inclusiveOffset = recoveryManager.recover(topic,true)(KafkaRecoveryManager.FROM_BUFFER_CURSOR).asInstanceOf[Map[TopicAndPartition,Long]].get(topicAndPartition)
    //for ((k,v) <- inclusiveCtx) printf("key: %s, value: %s\n", k, v)

    assert(exclusiveOffset.get === 22)
    assert(inclusiveOffset.get === 21)
  }
}

