package com.b2wdigital.vegas.ingest.output

import com.b2wdigital.vegas.ingest.Definitions.{IngestionDefinition}
import com.b2wdigital.vegas.ingest.{Utils, Validations}
import com.b2wdigital.vegas.ingest.exception.{JsonNotFlatException, JsonSchemaException, PartitionNotFoundException}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import play.api.libs.json._
import scala.util.control.NonFatal

class Console(ingestionDefinition : IngestionDefinition, sparkContext : SparkContext) extends DataOutput {


  override def write(rdd : RDD[String]) = {

    rdd.collect.foreach(println)

  }
}

object Console {

  val ID = "console"

}
