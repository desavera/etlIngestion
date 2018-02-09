package com.b2winc.vegas.ingest.client;

public class BaseClient {
	
	protected String hostAddress;
	protected String serviceVersion;
	
	public void setHostAddress(String hostAddress){
		this.hostAddress = hostAddress;
	}
	
	public void setServiceVersion(String serviceVersion){
		this.serviceVersion = serviceVersion;
	}

}
