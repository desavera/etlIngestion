package com.b2wdigital.vegas.ingest


import org.apache.spark.streaming.kafka.HasOffsetRanges
import org.apache.spark.streaming.kafka.OffsetRange
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag


class MockableEventsRDD(topic : String, rdd : RDD[MockableEvent]) extends HasOffsetRanges {
  
  // TODO : loop over the rdd and create the fake offset ranges
  val partition = 0
  val first = 1
  val count = 0

  var array:ArrayBuffer[OffsetRange] = new ArrayBuffer

  var last = 1
  rdd.collect.foreach { 
    data => 

      val size = data.acolumn.size 
      val offsetRange = OffsetRange(topic,partition,last,last + size)
      
      array += offsetRange 

      last = last + size + 1
  }

  override def offsetRanges: Array[OffsetRange] = {

    return array.toArray

  }
}
