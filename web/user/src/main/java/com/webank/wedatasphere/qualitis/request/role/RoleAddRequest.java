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

package com.webank.wedatasphere.qualitis.request.role;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author howeye
 */
public class RoleAddRequest {

    @JsonProperty("role_name")
    private String roleName;
    @JsonProperty("department_name")
    private String departmentName;
    @JsonProperty("role_type")
    private Integer roleType;
    @JsonProperty("zn_name")
    private String znName;

    public RoleAddRequest() {
        // Default Constructor
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

    public Integer getRoleType() {
        return roleType;
    }

    public void setRoleType(Integer roleType) {
        this.roleType = roleType;
    }

    public String getZnName() {
        return znName;
    }

    public void setZnName(String znName) {
        this.znName = znName;
    }

    @Override
    public String toString() {
        return "RoleAddRequest{" +
                "roleName='" + roleName + '\'' +
                '}';
    }
}
