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

package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;

/**
 * @author v_wblwyan
 * @date 2019-4-18
 */
public class QueryMetaColumnRequest {

  private static final int DEFAULT_START_INDEX = 0;
  private static final int DEFAULT_PAGE_SIZE = 5;

  private int page;
  private int size;
  @JsonProperty("cluster_name")
  private String clusterName;
  @JsonProperty("db_name")
  private String dbName;
  @JsonProperty("table_name")
  private String tableName;

  public QueryMetaColumnRequest() {
    page = DEFAULT_START_INDEX;
    size = DEFAULT_PAGE_SIZE;
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

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
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

  public void checkRequest() throws UnExpectedRequestException {
    CommonChecker.checkString(this.clusterName, "clusterName");
    CommonChecker.checkString(this.dbName, "dbName");
    CommonChecker.checkString(this.tableName, "tableName");
  }

  @Override
  public String toString() {
    return "QueryMetaColumnRequest{" +
            "page=" + page +
            ", size=" + size +
            ", clusterName='" + clusterName + '\'' +
            ", dbName='" + dbName + '\'' +
            ", tableName='" + tableName + '\'' +
            '}';
  }
}
