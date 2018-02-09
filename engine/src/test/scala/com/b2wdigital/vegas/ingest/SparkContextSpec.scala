package com.b2wdigital.vegas.ingest

import com.b2wdigital.vegas.ingest.Definitions.{IngestionDefinition, IngestionDescriptor, KafkaDirectStreamDefinition}
import com.b2wdigital.vegas.ingest.Definitions.{HiveTableDefinition, KafkaDirectStreamDefinition, StreamExecutionDefinition}

import play.api.libs.json._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

/*
 * A unit testing for any Spark dependent integration test.
 */
class SparkContextSpec extends FlatSpec with MockFactory with Matchers with BeforeAndAfter {

  val master = "local[2]"
  val appName = "example-spark"
  var sc: SparkContext = _

  before {

    val conf = new SparkConf()
      .setMaster(master)
      .setAppName(appName)
    sc = new SparkContext(conf)
  }
  
  after {

    if (sc != null) {
      sc.stop()
    }
  }

  "A SparContext" should "be created" in {
      
    assert(sc.isInstanceOf[SparkContext])
    
  } 
}

