#!/bin/bash

echo "=== STARTING KAFKA ==="
echo "starting zookeeper"
/opt/kafka_2.10-0.8.2.1/bin/zookeeper-server-start.sh -daemon /opt/kafka_2.10-0.8.2.1/config/zookeeper.properties
#sed -i 's/-Xmx1G -Xms1G/-Xmx100M -Xms100M/' /opt/kafka_2.10-0.8.2.1/bin/kafka-server-start.sh
echo "starting kafka-server"
/opt/kafka_2.10-0.8.2.1/bin/kafka-server-start.sh -daemon /opt/kafka_2.10-0.8.2.1/config/server.properties


if [[ $1 == "-d" ]]; then
  while true; do sleep 1000; done
fi