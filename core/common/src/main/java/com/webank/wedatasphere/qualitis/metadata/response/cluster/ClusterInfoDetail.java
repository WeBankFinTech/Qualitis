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

package com.webank.wedatasphere.qualitis.metadata.response.cluster;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author howeye
 */
public class ClusterInfoDetail {
    @JsonProperty("source_type")
    private String sourceType;
    @JsonProperty("cluster_name")
    private String clusterType;

    public ClusterInfoDetail() {
        // Default Constructor
    }

    public ClusterInfoDetail(String clusterName) {
        this.clusterType = clusterName;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getClusterType() {
        return clusterType;
    }

    public void setClusterType(String clusterType) {
        this.clusterType = clusterType;
    }

    @Override
    public String toString() {
        return "ClusterInfoDetail{" +
                ", sourceType='" + sourceType + '\'' +
                ", clusterType='" + clusterType + '\'' +
                '}';
    }
}
