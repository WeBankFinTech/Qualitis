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
import com.webank.wedatasphere.qualitis.entity.User;

/**
 * @author howeye
 */
public class UserResponse {

    @JsonProperty("user_id")
    private Long userId;
    private String username;
    @JsonProperty("chinese_name")
    private String chineseName;
    @JsonProperty("department")
    private String departmentName;

    public UserResponse() {
        // Default Constructor
    }

    public UserResponse(User user) {
        this.userId = user.getId();
        this.username = user.getUserName();
        this.chineseName = user.getChineseName();
        this.departmentName = user.getDepartment() != null ? user.getDepartment().getName() : "";
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

    @Override
    public String toString() {
        return "UserResponse{" +
            "userId=" + userId +
            ", username='" + username + '\'' +
            ", chineseName='" + chineseName + '\'' +
            ", departmentName='" + departmentName + '\'' +
            '}';
    }
}
