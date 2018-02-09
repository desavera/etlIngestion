package com.b2winc.vegas.ingest.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.b2winc.vegas.ingest.dto.IngestionDefinitionDTO;
import com.b2winc.vegas.ingest.mapper.IngestionDefinitionMapper;
import com.b2winc.vegas.ingest.model.domain.IngestionDefinition;
import com.b2winc.vegas.ingest.repository.IngestionDefinitionRepository;


@Service
public class IngestionDefinitionService {

    @Autowired
    private IngestionDefinitionRepository ingestionDefinitionRepository;

    @Autowired
    private IngestionDefinitionMapper mapper;

    /**
     * Get one ingestionDefinition by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public IngestionDefinitionDTO findOne(Long id) {

        IngestionDefinitionDTO ingestionDefinitionDTO = ingestionDefinitionRepository.findOne(id);
        if (ingestionDefinitionDTO == null)
            return null;
        return ingestionDefinitionDTO;
    }

    /**
     * Delete the ingestionDefinition by id.
     * 
     * @param id the id of the entity
     * @throws IOException
     * @throws ClientProtocolException
     */
    public void delete(Long id) throws ClientProtocolException, IOException {
        IngestionDefinitionDTO ingestionDefinitionDTO = findOne(id);
        if (ingestionDefinitionDTO == null)
            return;
        ingestionDefinitionRepository.delete(id);

    }

    /**
     * Save a ingestionDefinition and create a Job .
     * 
     * @param IngestionDefinition the entity to save
     * @return the persisted entity
     * @throws JSONException
     * @throws IOException
     * @throws ClientProtocolException
     * @throws URISyntaxException
     */
    public IngestionDefinition saveAndCreateJob(IngestionDefinition ingestionDefinition)
            throws IOException, JSONException, URISyntaxException
    {
        IngestionDefinitionDTO ingestionDefinitionDTO =
                mapper.ingestionDefinitionToIngestionDefinitionDTO(ingestionDefinition);

        ingestionDefinitionDTO = ingestionDefinitionRepository.save(ingestionDefinitionDTO);

        this.save(ingestionDefinitionDTO);
        return mapper.ingestionDefinitionDTOToIngestionDefinition(ingestionDefinitionDTO);
    }

    /**
     * Update a ingestionDefinition and update a Job .
     * 
     * @param IngestionDefinition the entity to update
     * @return the persisted entity
     * @throws JSONException
     * @throws IOException
     * @throws ClientProtocolException
     * @throws URISyntaxException
     */
    public IngestionDefinition saveAndUpdateJob(IngestionDefinition ingestionDefinition)
            throws IOException, JSONException, URISyntaxException {

        IngestionDefinitionDTO ingestionDefinitionDTO =
                mapper.ingestionDefinitionToIngestionDefinitionDTO(ingestionDefinition);
        ingestionDefinitionDTO = ingestionDefinitionRepository.save(ingestionDefinitionDTO);

        return mapper.ingestionDefinitionDTOToIngestionDefinition(ingestionDefinitionDTO);

    }

    /**
     * Save a ingestionDefinition.
     * 
     * @param IngestionDefinition the entity to save
     * @return the persisted entity
     */
    public IngestionDefinition save(IngestionDefinition ingestionDefinition) throws IOException {
        IngestionDefinitionDTO ingestionDefinitionDTO =
                mapper.ingestionDefinitionToIngestionDefinitionDTO(ingestionDefinition);

        ingestionDefinitionDTO = ingestionDefinitionRepository.save(ingestionDefinitionDTO);

        return mapper.ingestionDefinitionDTOToIngestionDefinition(ingestionDefinitionDTO);
    }

    /**
     * Save a ingestionDefinitionDTO.
     *
     * @param IngestionDefinitionDTO the entity to save
     * @return the persisted entity
     */
    public IngestionDefinitionDTO save(IngestionDefinitionDTO ingestionDefinitionDTO) {
        return ingestionDefinitionRepository.save(ingestionDefinitionDTO);
    }

    /**
     * Get all the ingestion-definitions.
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<IngestionDefinitionDTO> findAll() {
        return ingestionDefinitionRepository.findAll();
    }

}