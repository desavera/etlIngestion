package com.b2winc.vegas.ingest.model.domain;

public class DataOutputArgsHive extends DataOutputArgs{

  private String database;
  private String table;
  private String partitionColumn;

  public String getDatabase() {
    return database;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  public String getTable() {
    return table;
  }

  public void setTable(String table) {
    this.table = table;
  }

  public String getPartitionColumn() {
    return partitionColumn;
  }

  public void setPartitionColumn(String partitionColumn) {
    this.partitionColumn = partitionColumn;
  }
}
