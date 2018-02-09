package com.b2winc.vegas.ingest.model.domain;

public class ExecutionArgsBatch extends ExecutionArgs {

    private String master;
    private String cron;
    private String bucketSize;

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getBucketSize() {
        return bucketSize;
    }

    public void setBucketSize(String bucketSize) {
        this.bucketSize = bucketSize;
    }
}
