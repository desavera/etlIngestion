package com.b2winc.vegas.ingest.model.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

public class DataOutput {

  private String atype;
  @JsonTypeInfo(use = Id.NAME, property = "atype", include = As.EXTERNAL_PROPERTY)
  @JsonSubTypes(value = {
      @JsonSubTypes.Type(value = DataOutputArgsHive.class, name = "hive"),
      @JsonSubTypes.Type(value = DataOutputArgsHdfs.class, name = "hdfs")
  })
  private DataOutputArgs args;

  public String getAtype() {
    return atype;
  }

  public void setAtype(String atype) {
    this.atype = atype;
  }

  public DataOutputArgs getArgs() {
    return args;
  }

  public void setArgs(DataOutputArgs args) {
    this.args = args;
  }
}
