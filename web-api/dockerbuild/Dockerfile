FROM registry.b2w.io/centos:6.8

# open ssh
RUN yum update -y && yum install -y \
    openssh \
    && yum clean all

# JAVA 8
RUN yum install -y java-1.8.0-openjdk-devel.x86_64 \
	bash \
	ca-certificates-java \
	sudo \
	ca-certificates \
	openssh-clients \
	&& yum clean all

#MAVEN
RUN yum install -y wget && \
  wget http://www-eu.apache.org/dist/maven/maven-3/3.5.2/binaries/apache-maven-3.5.2-bin.tar.gz \
    --directory-prefix=/opt && \
  tar -zxvf /opt/apache-maven-3.5.2-bin.tar.gz -C /opt && \
  ls -l /opt/apache-maven-3.5.2

ENV M2_HOME /opt/apache-maven-3.5.2/
ENV PATH $M2_HOME/bin:$PATH

