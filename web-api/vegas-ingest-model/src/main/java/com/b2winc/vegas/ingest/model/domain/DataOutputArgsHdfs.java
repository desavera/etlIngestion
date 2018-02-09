package com.b2winc.vegas.ingest.model.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataOutputArgsHdfs extends DataOutputArgs{

  @JsonProperty("base_dir")
  private String baseDir;

  public String getBaseDir() {
    return baseDir;
  }

  public void setBaseDir(String baseDir) {
    this.baseDir = baseDir;
  }
}
