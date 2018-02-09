package com.b2wdigital.vegas.ingest

import Definitions._
import com.b2wdigital.vegas.ingest.execution.Execution
import com.b2wdigital.vegas.ingest.recovery.RecoveryManager
import grizzled.slf4j.Logger

import scala.io.Source
import play.api.libs.json._

import sys.process._


/*
a generic definition for a data ingestion job processing. We are hereby
supporting both Streaming and Batch jobs or whatever kind of combination that
will actually be defined from the IngestionDescription instance
*/

object IngestEngine {

  type OptionMap = Map[Symbol, Any]

  lazy val logger = Logger(getClass())

  def process(options: OptionMap) {


    var descriptor = """a descriptor can be loaded either by the API or from a file..."""

    if (options.contains('defapi))

      descriptor = fetchIngestionDescriptor(options)
    else
      descriptor = readIngestionDescriptor(options)


    logger.info("starting ingestion descriptor parsing ...")
    val ingestionDescriptor = Json.fromJson[IngestionDescriptor](Json.parse(descriptor)).get
    logger.info("finished ingestion descriptor parsing ...")

    val definition = ingestionDescriptor.definitionJson

    logger.info("starting ingestion definition parsing ...")
    val ingestionDefinition = Json.fromJson[IngestionDefinition](Json.parse(definition)).get
    logger.info("finished ingestion definition parsing ...")

    val recoveryManager = RecoveryManager(ingestionDefinition)

    val execution = Execution(ingestionDefinition, recoveryManager)

    logger.info("starting processing of job... ")
    execution.run
    logger.info("finished processing of job... ")

  }

  def readIngestionDescriptor(options: OptionMap): String = {

    val filename = options('deffile).asInstanceOf[String]
    Source.fromFile(filename).getLines.next
  }

  def fetchIngestionDescriptor(options: OptionMap): String = {

    val jobId = options('jobid).asInstanceOf[Int]
    val apiURL = options('defapi).asInstanceOf[String]

    val MaxTimeout = 2
    val MaxRetry = 4
    val RetryDelay = 1
    val MaxRetryTimeout = 5

    /*
    * CURL behaviour for retry is :
    *
    * If a transient error is returned when curl tries to perform a transfer, it will retry this number of times before  giving
    * up.  Setting the number to 0 makes curl do no retries (which is the default). Transient error means either: a timeout, an
    * FTP 4xx response code or an HTTP 5xx response code.
    * TODO : a higher level network access...
    */
    // RUNs a CURL to get SUGGESTED API calculations
    val curl = "curl --max-time " + MaxTimeout + "  --retry " + MaxRetry + " --retry-delay " + RetryDelay + " --retry-max-time " + MaxRetryTimeout + " http://" + apiURL + "/api/ingestion-definitions/" + jobId.toString

    curl !!
  }
}
