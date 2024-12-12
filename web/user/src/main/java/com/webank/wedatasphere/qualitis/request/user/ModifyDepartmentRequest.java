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

package com.webank.wedatasphere.qualitis.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author howeye
 */
public class ModifyDepartmentRequest {
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("department_name")
    private String departmentName;
    @JsonProperty("department")
    private long department;
    @JsonProperty("user_config_json")
    private String userConfigJson;
    @JsonProperty("department_sub_id")
    private long departmentSubId;
    @JsonProperty("position_zh")
    private String positionZh;
    @JsonProperty("position_en")
    private String positionEn;

    @JsonProperty("chinese_name")
    private String chineseName;

    public ModifyDepartmentRequest() {
        // Do nothing.
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getUserConfigJson() {
        return userConfigJson;
    }

    public void setUserConfigJson(String userConfigJson) {
        this.userConfigJson = userConfigJson;
    }

    public long getDepartmentSubId() {
        return departmentSubId;
    }

    public void setDepartmentSubId(long departmentSubId) {
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

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
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
}
