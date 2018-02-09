package com.b2winc.vegas.ingest.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class IngestionClient extends BaseClient {

	private final String serviceNameRoot = "/api";
	private final String serviceNameDefinitions = "/api/ingestion-definitions";
	private RestTemplate restTemplate = new RestTemplate();

	private IngestionClient() {
	}

        private String getHostRoot() {
                return super.hostAddress + super.serviceVersion + this.serviceNameRoot;
        }

	private String getHostDefinitions() {
		return super.hostAddress + super.serviceVersion + this.serviceNameDefinitions;
	}

	/**
	 * A client to access the Ingestion ws service
	 * 
	 * @param hostAddress
	 *            - The server address. Example: https://myserver:8080/api
	 * @param serviceVersion
	 *            - The ingestion ws service version running on the server. Example:
	 *            v1.00
	 */
	@Autowired
	public IngestionClient(@Value("${ingestion.api.url:http://ingestion-api.atlas.b2w/}") String hostAddress,
			@Value("${ingestion.api.version:v2}") String serviceVersion) {
		this.hostAddress = hostAddress;
		this.serviceVersion = "/" + serviceVersion;
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getHostRoot());
		restTemplate.getForObject(builder.build().encode().toUri(), String.class);
	}

	private java.net.URI getCompleteUri(UriComponentsBuilder builder){
		return builder.build().encode().toUri();
	}

	public String getIngestionDefinition(Integer jobId) {
		String id = jobId.toString();
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(
				getHostDefinitions()).queryParam("ingestion_id", id);
		return restTemplate.getForObject(getCompleteUri(builder), String.class);
	}
}
