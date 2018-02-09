package com.b2wdigital.vegas.ingest.output

import com.b2wdigital.vegas.ingest.Definitions.{HiveTableDefinition, IngestionDefinition}
import com.b2wdigital.vegas.ingest.{Utils, Validations}
import com.b2wdigital.vegas.ingest.exception.{JsonNotFlatException, JsonSchemaException, PartitionNotFoundException}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SaveMode}
import play.api.libs.json._
import scala.util.control.NonFatal

class HiveTable(ingestionDefinition : IngestionDefinition, sparkContext : SparkContext) extends DataOutput {

  val outputDefinition : HiveTableDefinition = Json.fromJson[HiveTableDefinition](ingestionDefinition.data_output.args).get
  var hiveContext = new HiveContext(sparkContext)
  /*
  this is for a partitioned table
   */
  hiveContext.setConf("hive.exec.dynamic.partition", "true")
  hiveContext.setConf("hive.exec.dynamic.partition.mode", "nonstrict")

  override def write(rdd : RDD[String]) = {

    val tableSchema = hiveContext.sql("select * from " + outputDefinition.getTableLocation()).limit(1).schema

    if(!tableSchema.exists(sf => sf.name == outputDefinition.partitionColumn))
      throw new PartitionNotFoundException()

    val rowRdd = rdd.map{ value =>

      validateFlatted(value)

      Validations.validateSchema(value, tableSchema)

      createRow(value, tableSchema)
    }

    hiveContext
        .createDataFrame(rowRdd, tableSchema)
        .write.mode(SaveMode.Append)
        .partitionBy(outputDefinition.partitionColumn)
        .insertInto(outputDefinition.getTableLocation())


  }

  def validateFlatted(value: String) = {

    val json = Json.parse(value).asInstanceOf[JsObject]

    json.fields.foreach { field =>
      if (field._2.isInstanceOf[JsObject] || field._2.isInstanceOf[JsArray])
        throw new JsonNotFlatException
    }
  }

  def createRow(value: String, tableSchema: StructType): Row = {

    val json = Json.parse(value).asInstanceOf[JsObject]

    Row.fromSeq(tableSchema.map{ structField =>

      Utils.cast(
        (json \ structField.name)
        .getOrElse(null) match {
          case x:JsString => x.as[String]
          case other => if(other != null) other.toString() else ""
        }, structField.dataType
      )

    })

  }

  /**
    * This method should be used only for unit tests.
    * @param hiveContext
    */
  def setHiveContextForTest(hiveContext : HiveContext) = { this.hiveContext = hiveContext }

}

object HiveTable {

  val ID = "hive"

}
