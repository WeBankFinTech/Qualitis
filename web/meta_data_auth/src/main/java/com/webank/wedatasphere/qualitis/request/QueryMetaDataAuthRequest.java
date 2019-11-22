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
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;

/**
 * @author howeye
 */
public class QueryMetaDataAuthRequest {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 5;

    private String username;
    @JsonProperty("is_org")
    private Boolean isOrg;
    private Integer page;
    private Integer size;

    public QueryMetaDataAuthRequest() {
        page = DEFAULT_PAGE;
        size = DEFAULT_SIZE;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Boolean getIsOrg() {
        return isOrg;
    }

    public void setIsOrg(Boolean isOrg) {
        this.isOrg = isOrg;
    }

    public static void checkRequest(QueryMetaDataAuthRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
        CommonChecker.checkString(request.getUsername(), "Username");
        CommonChecker.checkObject(request.getIsOrg(), "Org");
    }

    @Override
    public String toString() {
        return "QueryMetaDataAuthRequest{" +
                "username='" + username + '\'' +
                ", isOrg=" + isOrg +
                ", page=" + page +
                ", size=" + size +
                '}';
    }
}
