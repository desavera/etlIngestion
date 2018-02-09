package com.b2wdigital.vegas.ingest.output

import java.text.SimpleDateFormat
import java.time.Instant
import java.util
import java.util.Date

import com.b2wdigital.vegas.ingest.Definitions.IngestionDefinition
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.collection.mutable

class Hdfs(ingestionDefinition : IngestionDefinition, sparkContext: SparkContext) extends DataOutput {

  override def write(rdd : RDD[String]) = {

    val date = new SimpleDateFormat("yyyy-MM-dd").format(new Date())

    //get first element
    val prefixElement : Any = ingestionDefinition.data_output.args.productElement(0)
    //transform to map
    val prefixMap : mutable.LinkedHashMap[String, String] = prefixElement.asInstanceOf[mutable.LinkedHashMap[String, String]]
    //get the value
    val prefix = s"${prefixMap.get("base_dir").get}".replace("\"", "")

   
    saveAsTextFile(
      s"${prefix}/${ingestionDefinition.account}",
      s"ingestion-${ingestionDefinition.name}_$date",
      rdd
    )
  }

  def saveAsTextFile[T](hdfsServer: String, fileName: String, rdd: RDD[String]) = {

    val hadoopConfig = new Configuration()
    val hdfs = FileSystem.get(hadoopConfig)

    val path = new Path(hdfsServer)

    //Check if the path exists
    if (!hdfs.exists(path)) {
      hdfs.mkdirs(path)
    }

    rdd.saveAsTextFile(s"$hdfsServer/$fileName/${Instant.now().toEpochMilli}/")
  }

}

object Hdfs {
  val ID = "hdfs"
}
