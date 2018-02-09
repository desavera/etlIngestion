package com.b2winc.vegas.ingest.model.domain;

public class IngestionDefinition {

    private Long id;

    private String name;

    private IngestionDefinitionBody definitionJson;

    public Long getId() {
        return id;
    }
    public IngestionDefinitionBody getDefinitionJson() {
        return definitionJson;
    }
    public String getName() {
        return name;
    }

    public void setDefinitionJson(IngestionDefinitionBody definitionJson) {
        this.definitionJson = definitionJson;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

}
