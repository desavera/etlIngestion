package com.b2winc.vegas.ingest.model.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class IngestionDefinitionBody {

  private String account;
  private String name;
  private String revision;
  @JsonProperty("data_input")
  private DataInput dataInput;
  private List<Transformer> transformers;
  @JsonProperty("data_output")
  private DataOutput dataOutput;
  private Execution execution;
  private Recovery recovery;

  public Recovery getRecovery() {
    return recovery;
  }

  public void setRecovery(Recovery recovery) {
    this.recovery = recovery;
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRevision() {
    return revision;
  }

  public void setRevision(String revision) {
    this.revision = revision;
  }

  public DataInput getDataInput() {
    return dataInput;
  }

  public void setDataInput(DataInput dataInput) {
    this.dataInput = dataInput;
  }

  public List<Transformer> getTransformers() {
    return transformers;
  }

  public void setTransformers(
      List<Transformer> transformers) {
    this.transformers = transformers;
  }

  public DataOutput getDataOutput() {
    return dataOutput;
  }

  public void setDataOutput(DataOutput dataOutput) {
    this.dataOutput = dataOutput;
  }

  public Execution getExecution() {
    return execution;
  }

  public void setExecution(Execution execution) {
    this.execution = execution;
  }
}
