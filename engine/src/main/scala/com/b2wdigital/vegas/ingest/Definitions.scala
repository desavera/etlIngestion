package com.b2wdigital.vegas.ingest

import play.api.libs.json._

object Definitions {

  case class DataInputDefinition(atype : String, args : JsObject)
  object DataInputDefinition{implicit val dataInputFormat = Json.format[DataInputDefinition]}

  case class TransformerDefinition(atype : String, args : JsObject)
  object TransformerDefinition{implicit val transformersFormat = Json.format[TransformerDefinition]}

  case class JavaScriptTransformerDefinition(scripts: Seq[String])
  object JavaScriptTransformerDefinition{implicit val transformersFormat = Json.format[JavaScriptTransformerDefinition]}

  case class DataOutputDefinition(atype : String, args : JsObject)
  object DataOutputDefinition{implicit val dataOutputFormat = Json.format[DataOutputDefinition]}

  case class CheckpointDefinition(atype : String, args : JsObject)
  object CheckpointDefinition{implicit val checkpointFormat = Json.format[CheckpointDefinition]}

  case class ExecutionDefinition(atype : String, args : JsObject)
  object ExecutionDefinition{implicit val executionFormat = Json.format[ExecutionDefinition]}

  case class StreamExecutionDefinition(master : String,directory : String,cron : String,interval : String,bucketSize : String)
  object StreamExecutionDefinition{implicit val streamFormat = Json.format[StreamExecutionDefinition]}

  case class BatchExecutionDefinition(master : String,cron : String,bucketSize : String )
  object BatchExecutionDefinition{implicit val batchFormat = Json.format[BatchExecutionDefinition]}

  case class KafkaDirectStreamDefinition(brokers : String, groupId : String, topic : String)
  object KafkaDirectStreamDefinition{implicit val kafkaFormat = Json.format[KafkaDirectStreamDefinition]}

  case class KafkaBatchDefinition(brokers : String, groupId : String, topic : String)
  object KafkaBatchDefinition{implicit val kafkaFormat = Json.format[KafkaBatchDefinition]}

  case class HdfsDefinition(base_dir : String)
  object HdfsDefinition{implicit val hdfsFormat = Json.format[HdfsDefinition]}

  case class ZookeeperDefinition(serverName : String, host : String, path : String,sessionTimeout : Int,connectionTimeout : Int)
  object ZookeeperDefinition{implicit val zookeeperFormat = Json.format[ZookeeperDefinition]}

  case class HiveTableDefinition(database : String, table : String, partitionColumn: String) {
    def getTableLocation(): String = { database + "." + table }
  }

  object HiveTableDefinition{implicit val hiveFormat = Json.format[HiveTableDefinition]}

  case class S3Definition(bucketName : String,key : String, secret : String)
  object S3Definition{implicit val s3Format = Json.format[S3Definition]}

  // TODO make an args for MemMapPersistency other types of Persistency
  //case class RecoveryDefinition(mode : String, persistency : ZookeeperDefinition)
  case class RecoveryDefinition(mode : String, host : String, path : String,sessionTimeout : Int,connectionTimeout : Int)
  object RecoveryDefinition{implicit val recoveryFormat = Json.format[RecoveryDefinition]}

  case class IngestionDefinition(name : String,
                                 account : String,
                                 revision : String,
                                 data_input : DataInputDefinition,
                                 transformers : Seq[TransformerDefinition],
                                 data_output : DataOutputDefinition,
                                 execution : ExecutionDefinition,
                                 recovery : RecoveryDefinition)

  object IngestionDefinition{implicit val ingestionFormat = Json.format[IngestionDefinition]}

  case class IngestionDescriptor(id : Int,name : String,definitionJson : String)
  object IngestionDescriptor{implicit val descFormat = Json.format[IngestionDescriptor]}
}
