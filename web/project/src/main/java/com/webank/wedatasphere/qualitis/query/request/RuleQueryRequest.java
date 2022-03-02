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

package com.webank.wedatasphere.qualitis.query.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;

/**
 *
 * @author v_wblwyan
 * @date 2018-11-1
 */
public class RuleQueryRequest {

  private String cluster;
  @JsonProperty("datasource_id")
  private Long datasourceId;
  private String db;
  private String table;
  private String column;
  @JsonProperty("user_type")
  private Integer[] userType;
  private String user;

  private int page;
  private int size;

  public RuleQueryRequest() {
    this.page = 0;
    this.size = 15;
  }

  public RuleQueryRequest(String user) {
    this.user = user;
  }

  public String getCluster() {
    return cluster;
  }

  public void setCluster(String cluster) {
    this.cluster = cluster;
  }

  public Long getDatasourceId() {
    return datasourceId;
  }

  public void setDatasourceId(Long datasourceId) {
    this.datasourceId = datasourceId;
  }

  public String getDb() {
    return db;
  }

  public void setDb(String db) {
    this.db = db;
  }

  public String getTable() {
    return table;
  }

  public void setTable(String table) {
    this.table = table;
  }

  public Integer[] getUserType() {
    return userType;
  }

  public void setUserType(Integer[] userType) {
    this.userType = userType;
  }

  public String getColumn() {
    return column;
  }

  public void setColumn(String column) {
    this.column = column;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public void checkRequest() throws UnExpectedRequestException {
    if (cluster == null || "".equals(cluster)
       || table == null || "".equals(table)) {
      throw new UnExpectedRequestException("Params of {&REQUEST_CAN_NOT_BE_NULL}");
    }
  }
}