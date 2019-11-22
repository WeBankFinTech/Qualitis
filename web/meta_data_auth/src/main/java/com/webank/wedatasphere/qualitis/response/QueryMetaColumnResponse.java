/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.entity.MetaDataColumn;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author v_wblwyan
 * @date 2019-4-18
 */
public class QueryMetaColumnResponse {

  @JsonProperty("cluster_name")
  private String clusterName;

  @JsonProperty("db_name")
  private String dbName;

  @JsonProperty("table_name")
  private String tableName;

  private long total;
  private List<MetaColumnResponse> columns;

  public QueryMetaColumnResponse() {
    // Default Constructor
  }

  public QueryMetaColumnResponse(List<MetaDataColumn> metaDataColumns, long total) {
    if (CollectionUtils.isNotEmpty(metaDataColumns)) {
      MetaDataColumn metaDataColumn = metaDataColumns.get(0);
      this.clusterName = metaDataColumn.getMetaDataTable()
                                       .getMetaDataDb()
                                       .getMetaDataCluster()
                                       .getClusterName();
      this.dbName = metaDataColumn.getMetaDataTable().getMetaDataDb().getDbName();
      this.tableName = metaDataColumn.getMetaDataTable().getTableName();
      columns = new ArrayList<>();
      for (MetaDataColumn column : metaDataColumns) {
        columns.add(new MetaColumnResponse(column.getColumnName(), column.getColumnType()));
      }
    }
    this.total = total;
  }

  public String getClusterName() {
    return clusterName;
  }

  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }

  public String getDbName() {
    return dbName;
  }

  public void setDbName(String dbName) {
    this.dbName = dbName;
  }

  public long getTotal() {
    return total;
  }

  public void setTotal(long total) {
    this.total = total;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public List<MetaColumnResponse> getColumns() {
    return columns;
  }

  public void setColumns(List<MetaColumnResponse> columns) {
    this.columns = columns;
  }

  class MetaColumnResponse {
    @JsonProperty("column_name")
    private String columnName;

    @JsonProperty("column_type")
    private String columnType;

    public MetaColumnResponse(String columnName, String columnType) {
      this.columnName = columnName;
      this.columnType = columnType;
    }

    public String getColumnName() {
      return columnName;
    }

    public void setColumnName(String columnName) {
      this.columnName = columnName;
    }

    public String getColumnType() {
      return columnType;
    }

    public void setColumnType(String columnType) {
      this.columnType = columnType;
    }
  }
}
