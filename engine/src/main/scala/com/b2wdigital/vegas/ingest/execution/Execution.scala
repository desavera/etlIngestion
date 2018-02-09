package com.b2wdigital.vegas.ingest.execution

import com.b2wdigital.vegas.ingest.Definitions.IngestionDefinition
import com.b2wdigital.vegas.ingest.recovery.RecoveryManager

trait Execution {

  // the execute method
  def run
}

object Execution {

  def apply(definition : IngestionDefinition,recoveryManager : RecoveryManager) : Execution = {

    definition.execution.atype match {

      case "stream" => new StreamingExecution(definition,recoveryManager)
      case "batch" => new BatchExecution(definition,recoveryManager)
      case _ => throw new IllegalArgumentException("no Execution known as : " + definition.execution.atype)
    }

  }
}