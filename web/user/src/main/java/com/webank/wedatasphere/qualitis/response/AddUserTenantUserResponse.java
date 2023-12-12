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
import com.webank.wedatasphere.qualitis.entity.UserTenantUser;

/**
 * @author allenzhou
 */
public class AddUserTenantUserResponse {

    @JsonProperty("user_tenant_user_id")
    private Long userTenantUserId;
    @JsonProperty("username")
    private String username;
    @JsonProperty("tenant_user_name")
    private String tenantUserName;

    public AddUserTenantUserResponse() {
        // Default Constructor
    }

    public AddUserTenantUserResponse(UserTenantUser userTenantUser) {
        this.userTenantUserId = userTenantUser.getId();
        this.username = userTenantUser.getUser().getUsername();
        this.tenantUserName = userTenantUser.getTenantUser().getTenantName();
    }

    public Long getUserTenantUserId() {
        return userTenantUserId;
    }

    public void setUserTenantUserId(Long userTenantUserId) {
        this.userTenantUserId = userTenantUserId;
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
}
