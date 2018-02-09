package com.b2wdigital.vegas.ingest.recovery

import grizzled.slf4j.Logger


case class MemMapPersistency extends RecoveryPersistency {


  lazy val logger = Logger(getClass())

  private val memMap = scala.collection.mutable.Map[String,String]()


  def save(key : String, value : String) : Unit = {

    logger.info("MemMapPersistency SAVE key: " +  key + "value: " + value + "\n")
    memMap.put(key,value)
  }

  def load(key : String) : Option[String]  = {

    logger.info("MemMapPersistency LOAD key: " +  key + "\n")

    for ((k,v) <- memMap) {

      if (k.startsWith(key)) {

        return Some(v)
      }
    }

    logger.error("MemMapPersistency LOAD key: " +  key + " not found... \n")

    return None

  }

  def remove(key : String) = {

    logger.info("MemMapPersistency REMOVE key: " +  key + "\n")
    memMap.remove(key)
  }
}

