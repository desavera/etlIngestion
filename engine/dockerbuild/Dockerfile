FROM registry.b2w.io/centos:6.8

### SPARK 1.6.1 ###
RUN curl -LO http://archive.apache.org/dist/spark/spark-1.6.1/spark-1.6.1-bin-hadoop2.6.tgz

ENV SPARK_HOME /usr/lib/spark
ENV PATH $PATH:/usr/lib/spark/bin

RUN tar -zxf spark-*.tgz \
    && mkdir -p $SPARK_HOME \
    && mv spark-*/* $SPARK_HOME \
    && rm -rf spark-* \
    && cd $SPARK_HOME \
    && mv conf/log4j.properties.template conf/log4j.properties \
    && sed -i 's/log4j.rootCategory=INFO/log4j.rootCategory=ERROR/' conf/log4j.properties

#JAVA
RUN yum install -y java-1.8.0-openjdk-devel.x86_64 \
	ca-certificates-java \
  ca-certificates

#SBT
RUN curl https://bintray.com/sbt/rpm/rpm | tee /etc/yum.repos.d/bintray-sbt-rpm.repo \
  && yum install -y sbt

ADD spark-conf/* $SPARK_HOME/conf/
