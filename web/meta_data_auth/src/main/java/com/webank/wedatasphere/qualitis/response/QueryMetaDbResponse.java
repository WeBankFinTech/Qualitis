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
import com.webank.wedatasphere.qualitis.entity.MetaDataDb;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author v_wblwyan
 * @date 2019-4-18
 */
public class QueryMetaDbResponse<T> {

  @JsonProperty("cluster_name")
  private String clusterName;
  private long total;
  private List<MetaDbResponse> dbs;

  public QueryMetaDbResponse() {
    // Default Constructor
  }

  public QueryMetaDbResponse(List<MetaDataDb> metaDataDbs, long total) {
    if (CollectionUtils.isNotEmpty(metaDataDbs)) {
      this.clusterName = metaDataDbs.get(0).getMetaDataCluster().getClusterName();
      dbs = new ArrayList<>();
      for (MetaDataDb data : metaDataDbs) {
        dbs.add(new MetaDbResponse(data.getDbName()));
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

  public long getTotal() {
    return total;
  }

  public void setTotal(long total) {
    this.total = total;
  }

  public List<MetaDbResponse> getDbs() {
    return dbs;
  }

  public void setDbs(List<MetaDbResponse> dbs) {
    this.dbs = dbs;
  }

  class MetaDbResponse {
    @JsonProperty("db_name")
    private String dbName;

    MetaDbResponse(String dbName) {
      this.dbName = dbName;
    }

    public String getDbName() {
      return dbName;
    }

    public void setDbName(String dbName) {
      this.dbName = dbName;
    }
  }
}
