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
import org.apache.commons.lang.StringUtils;

/**
 * @author howeye
 */
public class ModifyProxyUserRequest {

    @JsonProperty("proxy_user_id")
    private Long proxyUserId;
    @JsonProperty("proxy_user_name")
    private String proxyUserName;

    public ModifyProxyUserRequest() {
        // Default Constructor
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

    public static void checkRequest(ModifyProxyUserRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }

        if (request.getProxyUserId() == null) {
            throw new UnExpectedRequestException("ProxyUser id {&CAN_NOT_BE_NULL_OR_EMPTY}, request: " + request);
        }

        if (StringUtils.isBlank(request.getProxyUserName())) {
            throw new UnExpectedRequestException("ProxyUser name {&CAN_NOT_BE_NULL_OR_EMPTY}, request: " + request);
        }
    }

    @Override
    public String toString() {
        return "ModifyProxyUserRequest{" +
                "proxyUserId=" + proxyUserId +
                ", proxyUserName='" + proxyUserName + '\'' +
                '}';
    }
}
