package com.b2wdigital.vegas.ingest.transform

import com.b2wdigital.vegas.ingest.Definitions.TransformerDefinition
import org.apache.spark.rdd.RDD
import play.api.libs.json._

class FlatMap() extends Transformer {

  override def transform(rdd: RDD[String]) : RDD[String] = {

    // TBD : protect for NPE
    return rdd.map(Json.parse(_)).map(JsFlattener(_)).map(Json.stringify(_))

  }
}

object FlatMap {
  val ID = "flatMap"
}

object JsFlattener {


  def apply(js: JsValue): JsValue = flatten(js).foldLeft(JsObject(Nil))(_++_.as[JsObject])

  def flatten(js: JsValue, prefix: String = ""): Seq[JsValue] = {

    js match {
      case JsBoolean(x) => Seq(Json.obj(prefix -> x))
      case JsNumber(x) => Seq(Json.obj(prefix -> x))
      case JsString(x) => Seq(Json.obj(prefix -> x))
      case JsArray(seq) => seq.zipWithIndex.flatMap{ case (x, i) => flatten(x, concat(prefix, i.toString)) }
      case x: JsObject => {
        x.fieldSet.toSeq.flatMap{ case (key, values) => flatten(values, concat(prefix, key)) }
      }
      case _ => Seq(Json.obj(prefix -> JsNull))
    }

  }

  def concat(prefix: String, key: String): String = if(prefix.nonEmpty) prefix + "_" + key else key

}