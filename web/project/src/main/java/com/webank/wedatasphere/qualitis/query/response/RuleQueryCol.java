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
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;

import org.springframework.beans.BeanUtils;

/**
 * @author v_wblwyan
 * @date 2018-11-1
 */
public class RuleQueryCol {

  @JsonIgnore
  private Long ruleId;

  @JsonIgnore
  private Long projectId;

  @JsonIgnore
  private String clusterName;

  @JsonIgnore
  private String dbName;

  @JsonIgnore
  private String tableName;

  @JsonProperty("col_name")
  private String colName;

  public RuleQueryCol() {
  }

  public RuleQueryCol(RuleDataSource ds) {
    BeanUtils.copyProperties(ds, this);
    if (ds.getRule() != null) {
      this.ruleId = ds.getRule().getId();
    }
  }

  public Long getRuleId() {
    return ruleId;
  }

  public void setRuleId(Long ruleId) {
    this.ruleId = ruleId;
  }

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
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

  public String getColName() {
    return colName;
  }

  public void setColName(String colName) {
    this.colName = colName;
  }
}
