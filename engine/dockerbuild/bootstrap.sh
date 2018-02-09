#!/bin/bash

rm -f /tmp/*.pid

if [ ! -d /data/nn/current ]; then
		echo "=== FORMATING NAMENODE ==="
		sh -c '/bin/echo -e "yes" | service hadoop-hdfs-namenode init'
		" == CLEAN DATANODE CACHE DIR == "
		rm -rf /var/lib/hadoop-hdfs/cache/hdfs/dfs/data

    echo "=== STARTING NAMENODE ==="
    service hadoop-hdfs-namenode start
    service hadoop-httpfs start
    echo "=== STARTING DATANODE ==="
    service hadoop-hdfs-datanode start

    hdfs dfs -mkdir /user
    hdfs dfs -mkdir /user/impala
    hdfs dfs -mkdir /user/hive
    hdfs dfs -chmod -R 777 /user
    hdfs dfs -mkdir /tmp
    hdfs dfs -chmod 777 /tmp
    #sudo -iu hdfs hdfs dfs -chown impala:impala /user/impala
    #sudo -iu hdfs hdfs dfs -chown impala:impala /user/hive
else
    echo "=== STARTING NAMENODE ==="
    service hadoop-hdfs-namenode start
    service hadoop-httpfs start
    echo "=== STARTING DATANODE ==="
    service hadoop-hdfs-datanode start
fi

echo "=== STARTING YARN ==="
service hadoop-yarn-resourcemanager start

echo "=== VERIFICANDO EXISTENCIA DOS METADADOS ==="
SCHEMA=`$HIVE_HOME/bin/schematool -dbType mysql -info | grep "schemaTool completed"`

# Verifica se o schema já está inicializa, caso negativo o inicializa
if [[ ! $SCHEMA ]]; then
  echo "=== CRIANDO METADADOS ==="
  $HIVE_HOME/bin/schematool -dbType mysql -initSchema
else
  echo "=== METADADOS JÁ EXISTENTES ==="
fi

echo "=== STARTING HIVE ==="
service hive-server2 start

echo "=== STARTING SSH ==="
service sshd start

echo "=== STARTING ZOOKEEPER ==="
cd $KAFKA_HOME
bin/zookeeper-server-start.sh config/zookeeper.properties &

echo "=== STARTING KAFKA ==="
bin/kafka-server-start.sh config/server.properties &

if [[ $1 == "-bash" ]]; then
  /bin/bash
fi

while true; do sleep 1000; done
