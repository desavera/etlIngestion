package com.b2wdigital.vegas.ingest.output

import com.b2wdigital.vegas.ingest.Definitions.{S3Definition, IngestionDefinition}
import com.b2wdigital.vegas.ingest.Definitions.KafkaDirectStreamDefinition
import com.b2wdigital.vegas.ingest.{Utils, Validations}
import com.b2wdigital.vegas.ingest.exception.{JsonNotFlatException, JsonSchemaException, PartitionNotFoundException}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SaveMode}
import play.api.libs.json._
import scala.util.control.NonFatal
import com.amazonaws._
import com.amazonaws.services.s3.model.{AmazonS3Exception, ObjectMetadata,PutObjectRequest}
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.auth.BasicAWSCredentials


class AWSS3(ingestionDefinition : IngestionDefinition, sparkContext : SparkContext) extends DataOutput {

  // TODO : this is for BF2017 only !
  val inputDefinition : KafkaDirectStreamDefinition = Json.fromJson[KafkaDirectStreamDefinition](ingestionDefinition.data_input.args).get
  val outputDefinition : S3Definition = Json.fromJson[S3Definition](ingestionDefinition.data_output.args).get

  val topic = inputDefinition.topic
  val metadata: ObjectMetadata = new ObjectMetadata();

  val s3 = new AmazonS3Client(new BasicAWSCredentials(outputDefinition.key,outputDefinition.secret));

  override def write(rdd : RDD[String]) = {
   
    val inputStream = new RDDAsInputStream(rdd)
    s3.putObject(new PutObjectRequest(outputDefinition.bucketName,"vegas-ingest-" + topic,inputStream, metadata));
  }
}

object AWSS3 {

  val ID = "awss3"
}
