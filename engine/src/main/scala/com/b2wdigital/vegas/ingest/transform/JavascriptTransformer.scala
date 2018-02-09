package com.b2wdigital.vegas.ingest.transform

import javax.script._

import com.b2wdigital.vegas.ingest.Definitions.{JavaScriptTransformerDefinition, TransformerDefinition}
import org.apache.spark.rdd.RDD
import play.api.libs.json.Json
import grizzled.slf4j.Logger

class JavaScriptTransformer(transformerDefinition : TransformerDefinition) extends Transformer {

  lazy val logger = Logger(getClass())

  logger.info("starting JavaScriptTransformer definition parsing ...")
  val jsTransformerDefinition : JavaScriptTransformerDefinition = Json.fromJson[JavaScriptTransformerDefinition](transformerDefinition.args).get
  logger.info("finished JavaScriptTransformer definition parsing ...")

  jsTransformerDefinition.scripts.foreach(JavaScriptTransformer.engine.eval(_))
  JavaScriptTransformer.engine.eval(JavaScriptTransformer.internalTransformFunction)

  override def transform(rdd: RDD[String]) : RDD[String] = {

    rdd.map(v => {
      JavaScriptTransformer.invocable.invokeFunction("internalTransform", v).toString
    })

  }
}

object JavaScriptTransformer {

  val ID = "javaScript"

  val engine = new ScriptEngineManager(getClass.getClassLoader).getEngineByName("nashorn")
  val invocable = JavaScriptTransformer.engine.asInstanceOf[Invocable]

  val internalTransformFunction = "var internalTransform = function(dataAsString) { var dataAsJson = JSON.parse(dataAsString); return JSON.stringify(transform(dataAsJson));}"

}
