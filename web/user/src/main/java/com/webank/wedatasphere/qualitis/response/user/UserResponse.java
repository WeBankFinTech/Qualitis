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

package com.webank.wedatasphere.qualitis.response.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.constant.RoleTypeEnum;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.util.map.CustomObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import javax.persistence.Column;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
public class UserResponse {

    @JsonProperty("user_id")
    private Long userId;
    private String username;
    @JsonProperty("chinese_name")
    private String chineseName;
    @JsonProperty("department_name")
    private String departmentName;
    @JsonProperty("user_config_json")
    private String userConfigJson;
    @JsonProperty("department")
    private long department;
    @JsonProperty("position_zh")
    private String positionZh;
    @JsonProperty("position_en")
    private String positionEn;
    @JsonProperty("department_code")
    private String departmentCode;
    @JsonProperty("department_sub_id")
    private Long departmentSubId;

    @JsonProperty("create_user")
    private String createUser;

    @JsonProperty("create_time")
    private String createTime;

    @JsonProperty("modify_user")
    private String modifyUser;

    @JsonProperty("modify_time")
    private String modifyTime;


    public UserResponse() {
        // Default Constructor
    }

    public UserResponse(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.chineseName = user.getChineseName();
        this.departmentName = user.getDepartmentName();
        this.userConfigJson = user.getUserConfigJson();
        if (user.getDepartment() != null) {
            this.department = user.getDepartment().getId();
            this.departmentCode = user.getDepartment().getDepartmentCode();
        }
        if (CollectionUtils.isNotEmpty(user.getRoles())) {
            List<Role> roles = user.getRoles().stream().filter(item -> item.getRoleType() != null).filter(item -> item.getRoleType().toString().equals(RoleTypeEnum.POSITION_ROLE.getCode().toString())).collect(Collectors.toList());
            this.positionZh = CollectionUtils.isNotEmpty(roles) ? roles.get(0).getZnName() : "";
            this.positionEn = CollectionUtils.isNotEmpty(roles) ? roles.get(0).getName() : "";
        } else {
            this.positionZh = "";
            this.positionEn = "";
        }
        this.departmentSubId = user.getSubDepartmentCode();
        this.createUser = user.getCreateUser();
        this.createTime = user.getCreateTime();
        this.modifyUser = user.getModifyUser();
        this.modifyTime = user.getModifyTime();
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public Long getDepartmentSubId() {
        return departmentSubId;
    }

    public void setDepartmentSubId(Long departmentSubId) {
        this.departmentSubId = departmentSubId;
    }

    public long getDepartment() {
        return department;
    }

    public void setDepartment(long department) {
        this.department = department;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getUserConfigJson() {
        return userConfigJson;
    }

    public void setUserConfigJson(String userConfigJson) {
        this.userConfigJson = userConfigJson;
    }

    public String getPositionZh() {
        return positionZh;
    }

    public void setPositionZh(String positionZh) {
        this.positionZh = positionZh;
    }

    public String getPositionEn() {
        return positionEn;
    }

    public void setPositionEn(String positionEn) {
        this.positionEn = positionEn;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", chineseName='" + chineseName + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", userConfigJson='" + userConfigJson + '\'' +
                ", department=" + department +
                '}';
    }
}
