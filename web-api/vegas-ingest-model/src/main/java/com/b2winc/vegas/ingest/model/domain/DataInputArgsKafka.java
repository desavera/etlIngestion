package com.b2winc.vegas.ingest.model.domain;

public class DataInputArgsKafka extends DataInputArgs {

  private String brokers;
  private String groupId;
  private String topic;

  public String getBrokers() {
    return brokers;
  }

  public void setBrokers(String brokers) {
    this.brokers = brokers;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }
}
