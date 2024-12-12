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

/**
 * @author howeye
 */
public class UserRoleResponse {

    private String uuid;
    @JsonProperty("user_id")
    private long userId;
    @JsonProperty("role_id")
    private long roleId;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("role_name")
    private String roleName;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("modify_user")
    private String modifyUser;
    @JsonProperty("modify_time")
    private String modifyTime;


    public UserRoleResponse() {
        // Default Constructor
    }

    public UserRoleResponse(UserRole userRole) {
        this.uuid = userRole.getId();
        this.userId = userRole.getUser().getId();
        this.roleId = userRole.getRole().getId();
        this.userName = userRole.getUser().getUsername();
        this.roleName = userRole.getRole().getName();
        this.createTime = userRole.getCreateTime();
        this.createUser = userRole.getCreateUser();
        this.modifyTime = userRole.getModifyTime();
        this.modifyUser = userRole.getModifyUser();
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
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
