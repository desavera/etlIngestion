package com.b2wdigital.vegas.ingest

import com.b2wdigital.vegas.ingest.Definitions.{IngestionDefinition, IngestionDescriptor}
import com.b2wdigital.vegas.ingest.IngestEngine.OptionMap
import org.scalatest.FunSuite

import play.api.libs.json._

/*
 * A base class for IngestionDefinition input based unit testing. 
 */
trait CommandLineOptions {

   def options() : OptionMap
}

abstract class IngestionDefinitionSuite extends FunSuite with CommandLineOptions {

    val descriptor = IngestEngine.readIngestionDescriptor(options())

    val ingestionDescriptor = Json.fromJson[IngestionDescriptor](Json.parse(descriptor)).get
    val definition = ingestionDescriptor.definitionJson
    val ingestionDefinition = Json.fromJson[IngestionDefinition](Json.parse(definition)).get
}
