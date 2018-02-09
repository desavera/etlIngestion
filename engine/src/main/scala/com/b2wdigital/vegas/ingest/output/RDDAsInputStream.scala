package com.b2wdigital.vegas.ingest.output

import org.apache.spark.rdd.RDD

case class RDDAsInputStream(private val rdd: RDD[String]) extends java.io.InputStream {

  var bytes = rdd.flatMap(_.getBytes("UTF-8")).toLocalIterator

  def read(): Int = {
    if(bytes.hasNext) bytes.next.toInt
      else -1
  }

  override def markSupported(): Boolean = false
}
