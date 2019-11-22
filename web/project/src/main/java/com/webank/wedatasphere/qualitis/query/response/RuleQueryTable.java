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

package com.webank.wedatasphere.qualitis.query.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author v_wblwyan
 * @date 2018-11-1
 */
public class RuleQueryTable {

  @JsonIgnore
  private String clusterName;

  @JsonIgnore
  private String dbName;

  @JsonProperty("table_name")
  private String tableName;

  private List<String> cols;

  public RuleQueryTable() {
  }

  public RuleQueryTable(RuleQueryCol data) {
    BeanUtils.copyProperties(data, this);
    cols = new ArrayList<>();
    cols.add(data.getColName());
  }

  public void addRuleQueryCol(RuleQueryCol data) {
    if (!cols.contains(data.getColName())) {
      cols.add(data.getColName());
    }
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

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public List<String> getCols() {
    return cols;
  }

  public void setCols(List<String> cols) {
    this.cols = cols;
  }

}
