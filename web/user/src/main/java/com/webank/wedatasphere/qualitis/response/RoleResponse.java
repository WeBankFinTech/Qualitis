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
import com.webank.wedatasphere.qualitis.constant.RoleTypeEnum;
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
    @JsonProperty("zn_name")
    private String znName;
    @JsonProperty("role_type")
    private Integer roleType;
    @JsonProperty("role_type_name")
    private String roleTypeName;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("modify_user")
    private String modifyUser;
    @JsonProperty("modify_time")
    private String modifyTime;

    public RoleResponse() {
        // Default Constructor
    }

    public RoleResponse(Role role) {
        this.roleId = role.getId();
        this.roleName = role.getName();
        this.departmentName = role.getDepartment() == null ? "" : role.getDepartment().getName();
        this.znName = role.getZnName();
        this.roleType = role.getRoleType();
        if (role.getRoleType() == null) {
            this.roleTypeName = "";
        } else {
            this.roleTypeName = (role.getRoleType().toString()).equals(RoleTypeEnum.SYSTEM_ROLE.getCode().toString()) ? RoleTypeEnum.SYSTEM_ROLE.getMessage() : RoleTypeEnum.POSITION_ROLE.getMessage();
        }
        this.createTime = role.getCreateTime();
        this.createUser = role.getCreateUser();
        this.modifyTime = role.getModifyTime();
        this.modifyUser = role.getModifyUser();
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

    public String getZnName() {
        return znName;
    }

    public void setZnName(String znName) {
        this.znName = znName;
    }

    public Integer getRoleType() {
        return roleType;
    }

    public void setRoleType(Integer roleType) {
        this.roleType = roleType;
    }

    public String getRoleTypeName() {
        return roleTypeName;
    }

    public void setRoleTypeName(String roleTypeName) {
        this.roleTypeName = roleTypeName;
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
        return "RoleResponse{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", departmentName='" + departmentName + '\'' +
                '}';
    }
}
