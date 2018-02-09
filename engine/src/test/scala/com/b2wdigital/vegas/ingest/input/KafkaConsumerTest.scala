package com.b2wdigital.vegas.ingest.input

import com.b2wdigital.vegas.ingest.Definitions.BatchExecutionDefinition
import com.b2wdigital.vegas.ingest._
import com.b2wdigital.vegas.ingest.Definitions.{IngestionDefinition, IngestionDescriptor}
import com.b2wdigital.vegas.ingest.IngestEngine.OptionMap
import com.b2wdigital.vegas.ingest.exception.IngestionException
import com.b2wdigital.vegas.ingest.recovery.{KafkaRecoveryManager, RecoveryManager}
import net.manub.embeddedkafka._
import play.api.libs.json._
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.spark.rdd.RDD

import scala.reflect.io.Directory


/*
 * A unit testing for KafkaStreamer using MemoryPeristency for simplicity. 
 */
object BatchFromBeginningDefinition extends IngestionDefinitionSuite {

  def options() : OptionMap = {

     Main.nextOption(Map(),List("--def-file","src/test/resources/data/kafka-batch-integration-test-beginning.def"))
  }
}

object BatchFromLastOnDefinition extends IngestionDefinitionSuite {

  def options() : OptionMap = {

     Main.nextOption(Map(),List("--def-file","src/test/resources/data/kafka-batch-integration-test-last-on.def"))
  }
}

object BatchFromLastInDefinition extends IngestionDefinitionSuite {

  def options() : OptionMap = {

     Main.nextOption(Map(),List("--def-file","src/test/resources/data/kafka-batch-integration-test-last-in.def"))
  }
}

class KafkaConsumerTest extends IntegrationTestSpec with EmbeddedKafka {

  implicit val config = EmbeddedKafkaConfig(kafkaPort = 9092, zooKeeperPort = 2181)
  implicit val serilizer = new StringSerializer

  val message1 = "blablabla1"
  val message2 = "blablabla2"
  val message3 = "blablabla3"
  val message4 = "blablabla4"
  val topic = "test"

  var lastDataIngested:RDD[String] = _

  "A Batch" should "consume the correct number and order of messages" in {

    withRunningKafka {

      /*
       * FROM BEGINNING
       */

      // bucketSize = 2 for beginning...
      publishToKafka(topic, message1)
      publishToKafka(topic, message2)

      val beginningExecutor = new MockableBatchExecution(BatchFromBeginningDefinition.ingestionDefinition, sc)

      // this will basically consume the messages in the queue according to bucketSize
      beginningExecutor.run

      // test for bucket size validation
      val batchDefinition = Json.fromJson[BatchExecutionDefinition](BatchFromBeginningDefinition.ingestionDefinition.execution.args).get
      assert(beginningExecutor.lastDataIngested.collect().size === batchDefinition.bucketSize.toInt)

      // test for message data correct fetch
      assert(beginningExecutor.lastDataIngested.collect()(0) === message1)
      assert(beginningExecutor.lastDataIngested.collect()(1) === message2)

      /*
       * FROM LAST ON - the last message is message2 and it should NOT be consumed !
       */

      // bucketSize = 1 for last on...
      publishToKafka(topic,message3)

      val lastOnExecutor = new MockableBatchExecution(BatchFromLastOnDefinition.ingestionDefinition,sc)

      // this will basically consume the messages in the queue according to bucketSize
      lastOnExecutor.run

      // test for message data correct fetch
      assert(lastOnExecutor.lastDataIngested.collect()(0) === message3)

      /*
       * FROM LAST IN - the last message is message3 and it should be consumed AGAIN !
       */

      // bucketSize = 1 for last in...
      publishToKafka(topic,message4)

      val lastInExecutor = new MockableBatchExecution(BatchFromLastInDefinition.ingestionDefinition,sc)

      // this will basically consume the messages in the queue according to bucketSize
      lastInExecutor.run

      // test for message data correct fetch
      assert(lastInExecutor.lastDataIngested.collect()(0) === message3)
    }

    // TODO : Streamer
  }
}
