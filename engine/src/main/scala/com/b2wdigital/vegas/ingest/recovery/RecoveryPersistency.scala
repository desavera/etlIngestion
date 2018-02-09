package com.b2wdigital.vegas.ingest.recovery

import com.b2wdigital.vegas.ingest.Definitions.IngestionDefinition

trait RecoveryPersistency {

  def save(key: String, value : String)
  def load(key : String) : Option[String]

}

