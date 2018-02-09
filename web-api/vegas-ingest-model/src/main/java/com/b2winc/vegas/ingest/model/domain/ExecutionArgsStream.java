package com.b2winc.vegas.ingest.model.domain;

public class ExecutionArgsStream extends ExecutionArgs{

  private String master;
  private String directory;
  private String cron;
  private String interval;
  private String bucketSize;

  public String getBucketSize() {
    return bucketSize;
  }

  public void setBucketSize(String bucketSize) {
    this.bucketSize = bucketSize;
  }

  public String getMaster() {
    return master;
  }

  public void setMaster(String master) {
    this.master = master;
  }

  public String getDirectory() {
    return directory;
  }

  public void setDirectory(String directory) {
    this.directory = directory;
  }

  public String getCron() {
    return cron;
  }

  public void setCron(String cron) {
    this.cron = cron;
  }

  public String getInterval() {
    return interval;
  }

  public void setInterval(String interval) {
    this.interval = interval;
  }
}
