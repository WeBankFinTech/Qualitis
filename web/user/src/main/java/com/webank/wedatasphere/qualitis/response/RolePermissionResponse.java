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
import com.webank.wedatasphere.qualitis.entity.RolePermission;

/**
 * @author howeye
 */
public class RolePermissionResponse extends BaseResponse {

    private String uuid;
    @JsonProperty("role_id")
    private long roleId;
    @JsonProperty("permission_id")
    private long permissionId;
    @JsonProperty("role_name")
    private String roleName;
    @JsonProperty("permission_name")
    private String permissionName;

    public RolePermissionResponse() {
        // Default Constructor
    }

    public RolePermissionResponse(RolePermission rolePermission) {
        this.uuid = rolePermission.getId();
        this.roleId = rolePermission.getRole().getId();
        this.permissionId = rolePermission.getPermission().getId();
        this.roleName = rolePermission.getRole().getName();
        this.permissionName = rolePermission.getPermission().getMethod() + " " + rolePermission.getPermission().getUrl();
        this.createTime = rolePermission.getCreateTime();
        this.createUser = rolePermission.getCreateUser();
        this.modifyUser = rolePermission.getModifyUser();
        this.modifyTime = rolePermission.getModifyTime();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(long permissionId) {
        this.permissionId = permissionId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    @Override
    public String toString() {
        return "RolePermissionResponse{" +
                "uuid='" + uuid + '\'' +
                ", roleId=" + roleId +
                ", permissionId=" + permissionId +
                '}';
    }
}
