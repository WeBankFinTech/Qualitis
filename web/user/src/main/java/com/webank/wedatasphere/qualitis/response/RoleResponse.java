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
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.Role;

/**
 * @author howeye
 */
public class RoleResponse {
    @JsonProperty("role_id")
    private long roleId;
    @JsonProperty("role_name")
    private String roleName;
    @JsonProperty("department_name")
    private String departmentName;

    public RoleResponse() {
        // Default Constructor
    }

    public RoleResponse(Role role) {
        this.roleId = role.getId();
        this.roleName = role.getName();
        this.departmentName = role.getDepartment() == null ? "" :role.getDepartment().getName();
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    @Override
    public String toString() {
        return "RoleResponse{" +
            "roleId=" + roleId +
            ", roleName='" + roleName + '\'' +
            ", departmentName='" + departmentName + '\'' +
            '}';
    }
}
