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
import com.webank.wedatasphere.qualitis.entity.ProxyUser;
import com.webank.wedatasphere.qualitis.entity.ProxyUser;
import com.webank.wedatasphere.qualitis.entity.ProxyUserDepartment;
import com.webank.wedatasphere.qualitis.util.map.CustomObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
public class AddProxyUserResponse extends BaseResponse {
    @JsonProperty("proxy_user_id")
    private Long proxyUserId;
    @JsonProperty("proxy_user_name")
    private String proxyUserName;
    @JsonProperty("proxy_user_members_num")
    private Long proxyUserMembersNum;
    @JsonProperty("department_name")
    private List<String> departmentName;
    @JsonProperty("user_config_json")
    private String userConfigJson;
    @JsonProperty("department")
    private long department;

    public AddProxyUserResponse() {
        // Default Constructor
    }

    public AddProxyUserResponse(ProxyUser proxyUser) {
        this.proxyUserId = proxyUser.getId();
        this.proxyUserName = proxyUser.getProxyUserName();
        if (proxyUser.getProxyUserDepartment() != null) {
            this.departmentName = proxyUser.getProxyUserDepartment().stream().map(ProxyUserDepartment::getDepartment).collect(Collectors.toList());
        }
        if (proxyUser.getDepartment() != null) {
            this.department = proxyUser.getDepartment().getId();
        }
        this.userConfigJson = proxyUser.getUserConfigJson();
        this.createTime = proxyUser.getCreateTime();
        this.createUser = proxyUser.getCreateUser();
        this.modifyUser = proxyUser.getModifyUser();
        this.modifyTime = proxyUser.getModifyTime();
    }

    public long getDepartment() {
        return department;
    }

    public List<String> getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(List<String> departmentName) {
        this.departmentName = departmentName;
    }

    public void setDepartment(long department) {
        this.department = department;
    }

    public String getUserConfigJson() {
        return userConfigJson;
    }

    public void setUserConfigJson(String userConfigJson) {
        this.userConfigJson = userConfigJson;
    }

    public Long getProxyUserId() {
        return proxyUserId;
    }

    public void setProxyUserId(Long proxyUserId) {
        this.proxyUserId = proxyUserId;
    }

    public String getProxyUserName() {
        return proxyUserName;
    }

    public void setProxyUserName(String proxyUserName) {
        this.proxyUserName = proxyUserName;
    }

    public Long getProxyUserMembersNum() {
        return proxyUserMembersNum;
    }

    public void setProxyUserMembersNum(Long proxyUserMembersNum) {
        this.proxyUserMembersNum = proxyUserMembersNum;
    }
}
