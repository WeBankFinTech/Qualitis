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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Set;

/**
 * @author howeye
 */
public class GetAllClusterResponse<T> {

    @JsonProperty("optional_clusters")
    private Set<String> optionalClusters;
    private long total;
    private List<T> data;

    public GetAllClusterResponse() {
        // Default Constructor
    }

    public Set<String> getOptionalClusters() {
        return optionalClusters;
    }

    public void setOptionalClusters(Set<String> optionalClusters) {
        this.optionalClusters = optionalClusters;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
