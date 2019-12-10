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
public class ModifyClusterInfoRequest {

    @JsonProperty("cluster_info_id")
    private Long clusterInfoId;
    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("cluster_type")
    private String clusterType;
    @JsonProperty("meta_store_address")
    private String metaStoreAddress;
    @JsonProperty("hive_server2_address")
    private String hiveServer2Address;
    @JsonProperty("linkis_address")
    private String linkisAddress;
    @JsonProperty("linkis_token")
    private String linkisToken;
    public ModifyClusterInfoRequest() {
        // Default Constructor
    }

    public Long getClusterInfoId() {
        return clusterInfoId;
    }

    public void setClusterInfoId(Long clusterInfoId) {
        this.clusterInfoId = clusterInfoId;
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

    public String getMetaStoreAddress() {
        return metaStoreAddress;
    }

    public void setMetaStoreAddress(String metaStoreAddress) {
        this.metaStoreAddress = metaStoreAddress;
    }

    public String getHiveServer2Address() {
        return hiveServer2Address;
    }

    public void setHiveServer2Address(String hiveServer2Address) {
        this.hiveServer2Address = hiveServer2Address;
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
}
