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

package com.webank.wedatasphere.qualitis.metadata.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 根据 DataSphereStudio提供的接口，封装查询上游表字段的请求。
 * cs：包含context service ID 和表的 context key。
 *
 * @author allenzhou
 */
public class GetUserColumnByCsRequest {
    private static final int DEFAULT_START_INDEX = 0;
    private static final int DEFAULT_PAGE_SIZE = 20;

    @JsonProperty("start_index")
    private Integer startIndex;
    @JsonProperty("page_size")
    private Integer pageSize;
    @JsonProperty("cs_id")
    private String csId;
    @JsonProperty("context_key")
    private String contextKey;
    @JsonProperty("cluster_name")
    private String clusterName;

    private String loginUser;

    public GetUserColumnByCsRequest() {
        startIndex = DEFAULT_START_INDEX;
        pageSize = DEFAULT_PAGE_SIZE;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getCsId() {
        return csId;
    }

    public void setCsId(String csId) {
        this.csId = csId;
    }

    public String getContextKey() {
        return contextKey;
    }

    public void setContextKey(String contextKey) {
        this.contextKey = contextKey;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }
}
