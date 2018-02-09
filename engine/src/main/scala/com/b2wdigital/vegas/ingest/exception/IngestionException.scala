package com.b2wdigital.vegas.ingest.exception

import com.b2wdigital.vegas.ingest.recovery.RecoveryContext

case class IngestionException(message : String,context : Option[RecoveryContext]) extends Exception(message : String) {

}
