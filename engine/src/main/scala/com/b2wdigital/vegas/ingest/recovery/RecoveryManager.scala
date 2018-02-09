package com.b2wdigital.vegas.ingest.recovery

import com.b2wdigital.vegas.ingest.Definitions.IngestionDefinition
import com.b2wdigital.vegas.ingest.input.ZookeeperPersistency

trait RecoveryManager {

  def checkpoint(context : RecoveryContext)
  def recover(key : String,inclusive : Boolean) : RecoveryContext
  def rollback(context : Option[RecoveryContext])
}


object RecoveryManager {


  // TODO this is a Kafka context strictly
  def apply(definition : IngestionDefinition) : RecoveryManager = {

    val recoveryDefinition = definition.recovery

    val zkHost = recoveryDefinition.host
    val zkPath = recoveryDefinition.path
    val zkSessionTimeout = recoveryDefinition.sessionTimeout
    val zkConnectionTimeout = recoveryDefinition.connectionTimeout

    new KafkaRecoveryManager(new ZookeeperPersistency(zkHost,zkPath,zkSessionTimeout,zkConnectionTimeout))
  }

}




