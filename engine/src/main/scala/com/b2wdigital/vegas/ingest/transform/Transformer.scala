package com.b2wdigital.vegas.ingest.transform

import com.b2wdigital.vegas.ingest.Definitions.IngestionDefinition
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ListBuffer


trait Transformer extends Serializable {

  def transform(rdd : RDD[String]) : RDD[String]
}

object Transformer {

  def apply(definition:IngestionDefinition) : Transformer = {

    /*
      here goes the known data transform types list
    */

    // TODO : ordering the transformations...
    val transformers = ListBuffer[Transformer]()
    definition.transformers.map { transformerDefinition =>

      transformerDefinition.atype match {
        case FlatMap.ID => transformers += new FlatMap()
        case JavaScriptTransformer.ID => transformers += new JavaScriptTransformer(transformerDefinition)
        case _ => throw new IllegalArgumentException("no Transform known as : " + transformerDefinition.atype)
      }

    }

    new Transformers(transformers.toSeq)
  }
}

