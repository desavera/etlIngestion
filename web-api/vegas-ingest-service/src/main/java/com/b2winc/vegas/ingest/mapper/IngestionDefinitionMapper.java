package com.b2winc.vegas.ingest.mapper;

import com.b2winc.vegas.ingest.model.domain.IngestionDefinitionBody;
import com.b2winc.vegas.ingest.model.domain.IngestionDefinition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.b2winc.vegas.ingest.dto.IngestionDefinitionDTO;

@Component
public class IngestionDefinitionMapper {

    private ObjectMapper mapper;

    public IngestionDefinitionMapper(){
        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public IngestionDefinitionDTO ingestionDefinitionToIngestionDefinitionDTO(
            IngestionDefinition ingestionDefinition) throws JsonProcessingException {

        IngestionDefinitionDTO dto = new IngestionDefinitionDTO();
        dto.setId(ingestionDefinition.getId());
        dto.setName(ingestionDefinition.getName());
        dto.setDefinitionJson(mapper.writeValueAsString(
                ingestionDefinition.getDefinitionJson()));

        return dto;
    }

    public IngestionDefinition ingestionDefinitionDTOToIngestionDefinition(
            IngestionDefinitionDTO dto) throws IOException{

        IngestionDefinition ingestionDefinition = new IngestionDefinition();
        ingestionDefinition.setId(dto.getId());
        ingestionDefinition.setName(dto.getName());
        ingestionDefinition.setDefinitionJson(mapper.readValue(
                dto.getDefinitionJson(), IngestionDefinitionBody.class));

        return ingestionDefinition;

    }

    public List<IngestionDefinition> ingestionDefinitionDTOsToIngestionDefinition(
            List<IngestionDefinitionDTO> dtos) {

        return dtos.stream().map(dto -> {
                    try {
                            return ingestionDefinitionDTOToIngestionDefinition(dto);
                    } catch (IOException e){
                            throw new UncheckedIOException(e);
                    }
                }
        ).collect(Collectors.toList());

    }

    public List<IngestionDefinitionDTO> ingestionDefinitionToIngestionDefinitionDTOs(
            List<IngestionDefinition> ingestionDefinitions) {

        return ingestionDefinitions.stream().map(ingestionDefinition -> {
                      try{
                          return ingestionDefinitionToIngestionDefinitionDTO(
                                  ingestionDefinition);
                      } catch(JsonProcessingException je) {
                          throw new UncheckedIOException(je);
                      }
                }
        ).collect(Collectors.toList());

    }
}