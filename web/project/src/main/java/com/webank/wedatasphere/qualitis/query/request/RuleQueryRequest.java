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

/**
 *
 * @author v_wblwyan
 * @date 2018-11-1
 */
public class RuleQueryRequest {

  private String cluster;
  private String db;
  private String table;
  @JsonProperty("user_type")
  private Integer[] userType;
  private String user;

  public RuleQueryRequest() {
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

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

}