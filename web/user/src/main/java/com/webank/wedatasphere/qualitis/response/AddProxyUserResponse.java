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
import com.webank.wedatasphere.qualitis.entity.ProxyUser;
import com.webank.wedatasphere.qualitis.entity.ProxyUser;

/**
 * @author howeye
 */
public class AddProxyUserResponse {

    @JsonProperty("proxy_user_id")
    private Long proxyUserId;
    @JsonProperty("proxy_user_name")
    private String proxyUserName;

    public AddProxyUserResponse() {
        // Default Constructor
    }

    public AddProxyUserResponse(ProxyUser proxyUser) {
        this.proxyUserId = proxyUser.getId();
        this.proxyUserName = proxyUser.getProxyUserName();
    }

    public Long getProxyUserId() {
        return proxyUserId;
    }

    public void setProxyUserId(Long proxyUserId) {
        this.proxyUserId = proxyUserId;
    }

    public String getProxyUserName() {
        return proxyUserName;
    }

    public void setProxyUserName(String proxyUserName) {
        this.proxyUserName = proxyUserName;
    }
}
