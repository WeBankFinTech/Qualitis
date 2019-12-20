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

package com.webank.wedatasphere.qualitis.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

/**
 * @author howeye
 */
@Entity
@Table(name = "qualitis_config_cluster_info")
public class ClusterInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("cluster_info_id")
    private long id;

    @Column(name = "cluster_name", length = 100)
    @JsonProperty("cluster_name")
    private String clusterName;
    @Column(name = "cluster_type", length = 100)
    @JsonProperty("cluster_type")
    private String clusterType;
    @Column(length = 100, name = "linkis_address")
    @JsonProperty("linkis_address")
    private String linkisAddress;
    @Column(name = "linkis_token", length = 500)
    @JsonProperty("linkis_token")
    private String linkisToken;

    public ClusterInfo() {
        // Default Constructor
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
