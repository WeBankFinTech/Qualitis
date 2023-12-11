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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author v_wblwyan
 * @date 2018-11-1
 */
public class RuleQueryDb {

  @JsonIgnore
  private String clusterName;

  @JsonProperty("db_name")
  private String dbName;

  private List<RuleQueryTable> tables;

  @JsonIgnore
  private Map<String, RuleQueryTable> dataMap = new HashMap<>();

  public RuleQueryDb() {
  }

  public RuleQueryDb(RuleQueryTable data) {
    BeanUtils.copyProperties(data, this);
    String ds = String.format("%s.%s.%s", clusterName, dbName, data.getTableName());
    tables = new ArrayList<>();
    tables.add(data);
    dataMap.put(ds, data);
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

  public List<RuleQueryTable> getTables() {
    return tables;
  }

  public void setTables(List<RuleQueryTable> tables) {
    this.tables = tables;
  }

  public Map<String, RuleQueryTable> getDataMap() {
    return dataMap;
  }

  public void setDataMap(Map<String, RuleQueryTable> dataMap) {
    this.dataMap = dataMap;
  }
}
