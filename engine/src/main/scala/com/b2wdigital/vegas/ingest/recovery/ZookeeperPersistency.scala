package com.b2wdigital.vegas.ingest.input

import com.b2wdigital.vegas.ingest.recovery.RecoveryPersistency

import org.I0Itec.zkclient.ZkClient

// TBD : a Kafka independent ZkUtils...
import kafka.utils.ZkUtils

import org.apache.log4j.Logger

class ZookeeperPersistency (zkHost : String,zkPath : String,sessionTimeout : Int, connectionTimeout : Int) extends RecoveryPersistency {

  val logger = Logger.getLogger("ZookeeperPersistency")

  val zkClient = new ZkClient(zkHost, sessionTimeout, connectionTimeout)

  def save(key : String, value : String) : Unit = {

    val data = key + ':' + value
    logger.info("Writing to Zookeeper zkClient= "+zkClient+" zkHosts= "+zkHost+" zkPath= "+zkPath+" data: "+ data)
    ZkUtils.updatePersistentPath(zkClient, zkPath + key, value)

  }

  def load(key : String) : Option[String] = {

    logger.info("Reading data from Zookeeper")
    val (data,_) = ZkUtils.readDataMaybeNull(zkClient, zkPath + key)

    if (data.isDefined)
      return data

    logger.warn("error : no zookeeper entry available for key... " + key)
    return None

  }

}
