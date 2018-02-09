package com.b2winc.vegas.ingest.repository;

import com.b2winc.vegas.ingest.dto.IngestionDefinitionDTO;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the IngestionDefinition entity.
 */
public interface IngestionDefinitionRepository extends JpaRepository<IngestionDefinitionDTO, Long> {

}
