package com.b2winc.vegas.ingest.model.domain;

public class Transformer {

  private String atype;
  private TransformerArgs args;

  public TransformerArgs getArgs() {
    return args;
  }

  public void setArgs(TransformerArgs args) {
    this.args = args;
  }

  public String getAtype() {
    return atype;
  }

  public void setAtype(String atype) {
    this.atype = atype;
  }

}
