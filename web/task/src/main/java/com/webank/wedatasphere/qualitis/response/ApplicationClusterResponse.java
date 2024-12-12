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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public class ApplicationClusterResponse {

    @JsonProperty("cluster_name")
    private String clusterName;
    private List<ApplicationDatabaseResponse> database;

    @JsonIgnore
    private Map<String, ApplicationDatabaseResponse> map;

    public ApplicationClusterResponse(String clusterName) {
        this.clusterName = clusterName;
        this.database = new ArrayList<>();
        this.map = new HashMap<>();
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public List<ApplicationDatabaseResponse> getDatabase() {
        return database;
    }

    public void setDatabase(List<ApplicationDatabaseResponse> database) {
        this.database = database;
    }

    public Map<String, ApplicationDatabaseResponse> getMap() {
        return map;
    }

    public void setMap(Map<String, ApplicationDatabaseResponse> map) {
        this.map = map;
    }
}
