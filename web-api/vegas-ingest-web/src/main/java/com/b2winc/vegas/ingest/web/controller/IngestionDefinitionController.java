package com.b2winc.vegas.ingest.web.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;

import com.b2winc.vegas.ingest.model.domain.IngestionDefinition;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.b2winc.vegas.ingest.dto.IngestionDefinitionDTO;
import com.b2winc.vegas.ingest.mapper.IngestionDefinitionMapper;
import com.b2winc.vegas.ingest.service.IngestionDefinitionService;

@RestController
@RequestMapping("/api")
public class IngestionDefinitionController {

    @Autowired
    IngestionDefinitionService service;

    @Autowired
    IngestionDefinitionMapper mapper;

    /**
     * POST /ingestion-definitions : Create a new ingestionDefinition.
     *
     * @param ingestionDefinitionDTO the ingestionDefinitionDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new
     *         ingestionDefinitionDTO, or with status 400 (Bad Request) if the ingestionDefinition
     *         has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     * @throws JSONException
     * @throws IOException
     * @throws ClientProtocolException
     */
    @RequestMapping(value = "/ingestion-definitions", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IngestionDefinition> createIngestionDefinition(
            @Valid @RequestBody IngestionDefinition ingestionDefinition)
            throws URISyntaxException, ClientProtocolException, IOException, JSONException {
        if (ingestionDefinition.getId() != null) {
            return ResponseEntity.badRequest().headers(new HttpHeaders()).body(null);
        }
        IngestionDefinition result = service.saveAndCreateJob(ingestionDefinition);
        return ResponseEntity.created(new URI("/api/ingestion-definitions/"))
                .headers(new HttpHeaders()).body(result);
    }

    /**
     * GET /ingestion-definitions : get all the ingestionDefinitions.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of ingestionDefinitiones in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/ingestion-definitions", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<IngestionDefinitionDTO> getAllIngestionDefinitions() throws URISyntaxException {
        return service.findAll();
    }

    /**
     * GET /ingestion-definitions/:id : get the "id" ingestionDefinition.
     *
     * @param id the id of the ingestionDefinitionDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the ingestionDefinitionDTO, or
     *         with status 404 (Not Found)
     */
    @RequestMapping(value = "/ingestion-definitions/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public IngestionDefinitionDTO getIngestionDefinition(@PathVariable Long id) {
        return service.findOne(id);
    }

    /**
     * PUT /ingestion-definitions : Updates an existing ingestionDefinition.
     *
     * @param ingestionDefinitionDTO the ingestionDefinitionDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated
     *         ingestionDefinitionDTO, or with status 400 (Bad Request) if the
     *         ingestionDefinitionDTO is not valid, or with status 500 (Internal Server Error) if
     *         the ingestionDefinitionDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     * @throws JSONException
     * @throws IOException
     * @throws ClientProtocolException
     */
    @RequestMapping(value = "/ingestion-definitions", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IngestionDefinitionDTO> updateIngestionDefinition(
            @Valid @RequestBody IngestionDefinition ingestionDefinition)
            throws URISyntaxException, ClientProtocolException, IOException, JSONException {
        IngestionDefinitionDTO result;
        if (ingestionDefinition.getId() == null) {
            result = mapper.ingestionDefinitionToIngestionDefinitionDTO(
                createIngestionDefinition(ingestionDefinition).getBody());
        } else {
            result = mapper.ingestionDefinitionToIngestionDefinitionDTO(service.saveAndUpdateJob(ingestionDefinition));
        }
        return ResponseEntity.ok().headers(new HttpHeaders()).body(result);
    }

    /**
     * DELETE /ingestion-definitions/:id : delete the "id" ingestionDefinition.
     *
     * @param id the id of the ingestiondefinitionDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     * @throws IOException
     * @throws ClientProtocolException
     */
    @RequestMapping(value = "/ingestion-definitions/{id}", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteIngestionDefinition(@PathVariable Long id)
            throws ClientProtocolException, IOException {
        service.delete(id);
        return ResponseEntity.ok().headers(new HttpHeaders()).body(null);
    }
}
