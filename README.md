# VEGAS-INGEST

Data ingest thin layer to ensure B2W ingestion pipelines are generic and not
tied to any specific external tool.

# Modules in the repository

 - **engine** (*Scala*) => The ingest engine component. It's responsible for
 interpreting ingestion-definitions and orchestrating the pipeline execution
 and the integration with external tools.
 - **web-api** (*Java*) => The RESTFul web-api to allow other systems to
 interact with the engine.

# Quickstart

// TODO

# Running with docker for development

The entire stack can be run using docker containers. The main
`docker-compose.yml` file is in the root folder of this repository, and it has
services that allow you to build and run all vegas-ingest components.

Before running the service, first build the images:

    docker-compose build

# Tips for Kafka usage :

### Set local kafka instance for testing

If kafka will be an input for testing, these steps are needed to produce some data in a kafka topic.

### Create topic

        kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test

### List topics to confirm

    kafka-topics.sh --list --zookeeper localhost:2181

### Produce some payloads

    kafka-console-producer.sh --broker-list localhost:9092 --topic test
   
And input some json payloads

    {"acolumn":"ioereoireu","partition_date":"12/12/12"}
        {"acolumn":"teste","partition_date":"12/12/12"}
        {"acolumn":"lorem ipsum","partition_date":"12/12/12"}

### Consume to confirm if everything is ok

    kafka-console-consumer.sh --zookeeper localhost:2181 --topic test --from-beginning

### Checking the offsets status

    kafka-run-class.sh kafka.tools.DumpLogSegments --deep-iteration --print-data-log --files /tmp/kafka-logs/test-0/00000000000000000000.log


## Main docker-compose services

Build the web-api source code:

    docker-compose run web-api.build

Run the web-api

    docker-compose up web-api

Run the web-api with debug enabled

    docker-compose up web-api.debug
 
Use the port `5088` to remote connect your debugger (IDE).

Run the CDH stack (necessary to use hadoop and spark):

    docker-compose up cdh

Build the engine source code:

    docker-compose run engine.build

Run the engine to perform an ingestion:

    docker-compose run -e JOB_ID=<INGESTION ID FROM API> -e DEBUG_MODE=<BOOLEAN> engine.cli
 
The DEBUG_MODE variable is optional.

Use the port `5097` to remote connect your debugger (IDE).

## For Deployment

The deployment will take place at Atlas Platform and should point to the proper version and run out from the building container. The version MUST BE PASSED as an argument !

So , from the web-api directory of the Vegas Ingest API repo :

    cd web-api
    bin/vegas-ingestion-api.deploy [WAR BALL VERSION TAG]

This will create build the whole package and deploy the image at the Atlas Platform.


## Issues with cached artifacts

There might be issues with cached artifacts, mainly when existing classes are
removed within new branches. It's possible to clean all existing artifacts, for
example, web-api artifacts, running:

    docker-compose run web-api mvn clean -f /opt/code

## Requirements

- Docker
- Docker-Compose (compatible with version 2)

## Changelog

// TODO
