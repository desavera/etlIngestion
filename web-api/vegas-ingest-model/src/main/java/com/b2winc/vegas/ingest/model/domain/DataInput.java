package com.b2winc.vegas.ingest.model.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

public class DataInput {

  private String atype;
  @JsonTypeInfo(use = Id.NAME, property = "atype", include = As.EXTERNAL_PROPERTY)
  @JsonSubTypes(value = {
      @JsonSubTypes.Type(value = DataInputArgsKafka.class, name = "kafka")
  })
  private DataInputArgs args;

  public String getAtype() {
    return atype;
  }

  public void setAtype(String atype) {
    this.atype = atype;
  }

  public DataInputArgs getArgs() {
    return args;
  }

  public void setArgs(DataInputArgs args) {
    this.args = args;
  }
}
