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
import org.apache.commons.lang.StringUtils;

/**
 * @author allenzhou
 */
public class AddUserTenantUserRequest {

    @JsonProperty("username")
    private String username;
    @JsonProperty("tenant_user_name")
    private String tenantUserName;

    public AddUserTenantUserRequest() {
        // Default Constructor
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTenantUserName() {
        return tenantUserName;
    }

    public void setTenantUserName(String tenantUserName) {
        this.tenantUserName = tenantUserName;
    }

    public static void checkRequest(AddUserTenantUserRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        if (StringUtils.isBlank(request.getUsername())) {
            throw new UnExpectedRequestException("Username {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        if (StringUtils.isBlank(request.getTenantUserName())) {
            throw new UnExpectedRequestException("TtenantUser name {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }

    @Override
    public String toString() {
        return "AddUserTtenantUserRequest{" +
                "username='" + username + '\'' +
                ", tenantUserName='" + tenantUserName + '\'' +
                '}';
    }
}
