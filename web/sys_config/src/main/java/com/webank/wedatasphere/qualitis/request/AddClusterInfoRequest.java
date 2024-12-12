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

/**
 * @author howeye
 */
public class AddClusterInfoRequest {

    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("cluster_type")
    private String clusterType;
    @JsonProperty("linkis_address")
    private String linkisAddress;
    @JsonProperty("linkis_token")
    private String linkisToken;
    @JsonProperty("data_size_limit")
    private String dataSizeLimit;
    @JsonProperty("hive_urn")
    private String hiveUrn;
    @JsonProperty("wtss_json")
    private String wtssJson;
    @JsonProperty("jobserver_json")
    private String jobserverJson;

    public AddClusterInfoRequest() {
        // Default Constructor
    }

    public String getDataSizeLimit() {
        return dataSizeLimit;
    }

    public void setDataSizeLimit(String dataSizeLimit) {
        this.dataSizeLimit = dataSizeLimit;
    }

    public String getHiveUrn() {
        return hiveUrn;
    }

    public void setHiveUrn(String hiveUrn) {
        this.hiveUrn = hiveUrn;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getClusterType() {
        return clusterType;
    }

    public void setClusterType(String clusterType) {
        this.clusterType = clusterType;
    }

    public String getLinkisAddress() {
        return linkisAddress;
    }

    public void setLinkisAddress(String linkisAddress) {
        this.linkisAddress = linkisAddress;
    }

    public String getLinkisToken() {
        return linkisToken;
    }

    public void setLinkisToken(String linkisToken) {
        this.linkisToken = linkisToken;
    }

    public String getWtssJson() {
        return wtssJson;
    }

    public void setWtssJson(String wtssJson) {
        this.wtssJson = wtssJson;
    }

    public String getJobserverJson() {
        return jobserverJson;
    }

    public void setJobserverJson(String jobserverJson) {
        this.jobserverJson = jobserverJson;
    }

    @Override
    public String toString() {
        return "AddClusterInfoRequest{" +
                "clusterName='" + clusterName + '\'' +
                ", clusterType='" + clusterType + '\'' +
                ", linkisAddress='" + linkisAddress + '\'' +
                ", linkisToken='" + linkisToken + '\'' +
                '}';
    }
}
