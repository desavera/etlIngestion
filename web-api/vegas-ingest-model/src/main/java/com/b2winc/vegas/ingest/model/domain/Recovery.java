package com.b2winc.vegas.ingest.model.domain;

public class Recovery {

  private String mode;
  private String host;
  private String path;
  private Long sessionTimeout;
  private Long connectionTimeout;

  public String getMode() {
    return mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Long getSessionTimeout() {
    return sessionTimeout;
  }

  public void setSessionTimeout(Long sessionTimeout) {
    this.sessionTimeout = sessionTimeout;
  }

  public Long getConnectionTimeout() {
    return connectionTimeout;
  }

  public void setConnectionTimeout(Long connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }
}
