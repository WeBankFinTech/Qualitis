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
public class RuleQueryCluster {

  @JsonProperty("cluster_name")
  private String clusterName;

  private List<RuleQueryDb> dbs;

  @JsonIgnore
  private Map<String, RuleQueryDb> dataMap = new HashMap<>();

  public RuleQueryCluster() {
  }

  public RuleQueryCluster(RuleQueryDb data) {
    BeanUtils.copyProperties(data, this);
    String ds = String.format("%s.%s", clusterName, data.getDbName());
    dbs = new ArrayList<>();
    dbs.add(data);
    dataMap.put(ds, data);
  }

  public void addRuleQueryDb(RuleQueryDb data) {
    dbs.add(data);
    dataMap.put(String.format("%s.%s", data.getClusterName(), data.getDbName()), data);
  }

  public String getClusterName() {
    return clusterName;
  }

  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }

  public List<RuleQueryDb> getDbs() {
    return dbs;
  }

  public void setDbs(List<RuleQueryDb> dbs) {
    this.dbs = dbs;
  }

  public Map<String, RuleQueryDb> getDataMap() {
    return dataMap;
  }

  public void setDataMap(Map<String, RuleQueryDb> dataMap) {
    this.dataMap = dataMap;
  }
}
