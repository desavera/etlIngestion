package com.b2wdigital.vegas.ingest.input

import com.b2wdigital.vegas.ingest.Definitions.{IngestionDefinition, HdfsDefinition}
import com.b2wdigital.vegas.ingest.recovery._
import com.b2wdigital.vegas.ingest.exception.IngestionException
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import play.api.libs.json._
import grizzled.slf4j.Logger
import scala.io.Source
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.sql.Timestamp
import java.io._

class Hdfs(ingestionDefinition : IngestionDefinition, context : SparkContext) extends DataInput {


  lazy val logger = Logger(getClass())

  logger.info("starting Hdfs definition parsing ...")
  val inputDefinition = Json.fromJson[HdfsDefinition](ingestionDefinition.data_input.args).get
  logger.info("finished Hdfs definition parsing ...")

  val baseDir = inputDefinition.base_dir

  override def read(lambda : (RDD[String]) => Unit,errorHandler : (IngestionException,RecoveryManager) => Unit) = {

    logger.info("starting chunk data consume from HDFS...")

    // this is for the table partition
    val currentTime = System.currentTimeMillis()
    val format = new SimpleDateFormat("yyyy-MM-dd")
    val dt_dia = format.format(currentTime)

    val data = context.wholeTextFiles("hdfs:///" + baseDir + "*.json")
    val files = data.map { case (filename, content) => filename}

    files.collect.foreach( filename => {

        // TBD : errorHandling...
        val rdd = context.textFile(filename);
        lambda(rdd)
    })


    logger.info("finished chunk data consume from HDFS...")
  }
}

object Hdfs {

  val ID = "hdfs"

}
