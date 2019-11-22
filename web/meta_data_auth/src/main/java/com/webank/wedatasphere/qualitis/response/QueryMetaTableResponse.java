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
import com.webank.wedatasphere.qualitis.entity.MetaDataTable;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author v_wblwyan
 * @date 2019-4-18
 */
public class QueryMetaTableResponse {

  @JsonProperty("cluster_name")
  private String clusterName;

  @JsonProperty("db_name")
  private String dbName;

  private long total;
  private List<MetaTableResponse> tables;

  public QueryMetaTableResponse() {
    // Default Constructor
  }

  public QueryMetaTableResponse(List<MetaDataTable> metaDataTables, long total) {
    if (CollectionUtils.isNotEmpty(metaDataTables)) {
      MetaDataTable metaDataTable = metaDataTables.get(0);
      this.clusterName = metaDataTable.getMetaDataDb().getMetaDataCluster().getClusterName();
      this.dbName = metaDataTable.getMetaDataDb().getDbName();
      tables = new ArrayList<>();
      for (MetaDataTable data : metaDataTables) {
        tables.add(new MetaTableResponse(data.getTableName()));
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

  public List<MetaTableResponse> getTables() {
    return tables;
  }

  public void setTables(List<MetaTableResponse> tables) {
    this.tables = tables;
  }

  class MetaTableResponse {
    @JsonProperty("table_name")
    private String tableName;

    MetaTableResponse(String tableName) {
      this.tableName = tableName;
    }

    public String getTableName() {
      return tableName;
    }

    public void setTableName(String tableName) {
      this.tableName = tableName;
    }
  }
}
