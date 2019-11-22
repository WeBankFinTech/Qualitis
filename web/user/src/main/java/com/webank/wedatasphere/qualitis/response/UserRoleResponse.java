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
import com.webank.wedatasphere.qualitis.entity.UserRole;
import com.webank.wedatasphere.qualitis.entity.UserRole;

/**
 * @author howeye
 */
public class UserRoleResponse {

    private String uuid;
    @JsonProperty("user_id")
    private long userId;
    @JsonProperty("role_id")
    private long roleId;

    public UserRoleResponse() {
        // Default Constructor
    }

    public UserRoleResponse(UserRole userRole) {
        this.uuid = userRole.getId();
        this.userId = userRole.getUser().getId();
        this.roleId = userRole.getRole().getId();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "UserRoleResponse{" +
                "uuid='" + uuid + '\'' +
                ", userId=" + userId +
                ", roleId=" + roleId +
                '}';
    }
}
