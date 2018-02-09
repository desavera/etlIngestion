package com.b2winc.vegas.ingest.model.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

public class Execution {

  private String atype;
  @JsonTypeInfo(use = Id.NAME, property = "atype", include = As.EXTERNAL_PROPERTY)
  @JsonSubTypes(value = {
      @JsonSubTypes.Type(value = ExecutionArgsStream.class, name = "stream"),
      @JsonSubTypes.Type(value = ExecutionArgsBatch.class, name = "batch")
  })
  private ExecutionArgs args;

  public String getAtype() {
    return atype;
  }

  public void setAtype(String atype) {
    this.atype = atype;
  }

  public ExecutionArgs getArgs() {
    return args;
  }

  public void setArgs(ExecutionArgs args) {
    this.args = args;
  }
}
