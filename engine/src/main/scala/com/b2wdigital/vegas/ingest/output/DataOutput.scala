package com.b2wdigital.vegas.ingest.output

import com.b2wdigital.vegas.ingest.Definitions.IngestionDefinition
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

trait DataOutput extends Serializable {

  def write(rdd : RDD[String])

}

object DataOutput {



  def apply(definition:IngestionDefinition, sparkContext: SparkContext) : DataOutput = {

    /*
      here goes the known output data types list
    */
    definition.data_output.atype match {

      case HiveTable.ID => new HiveTable(definition, sparkContext)

      case Console.ID => new Console(definition, sparkContext)

      case Hdfs.ID => new Hdfs(definition, sparkContext)

      case AWSS3.ID => new AWSS3(definition, sparkContext)


    }
  }
}
