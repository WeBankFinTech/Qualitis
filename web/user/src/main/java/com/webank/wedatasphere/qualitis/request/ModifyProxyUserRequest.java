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

package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public class ModifyProxyUserRequest {

    @JsonProperty("proxy_user_id")
    private Long proxyUserId;
    @JsonProperty("proxy_user_name")
    private String proxyUserName;
    @JsonProperty("department_info")
    private List<DepartmentInfo> departmentInfo;
    @JsonProperty("user_config_json")
    private String userConfigJson;
    @JsonProperty("department")
    private long department;

    public ModifyProxyUserRequest() {
        // Default Constructor
    }

    public long getDepartment() {
        return department;
    }

    public void setDepartment(long department) {
        this.department = department;
    }

    public List<DepartmentInfo> getDepartmentInfo() {
        return departmentInfo;
    }

    public void setDepartmentInfo(List<DepartmentInfo> departmentInfo) {
        this.departmentInfo = departmentInfo;
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

    public static void checkRequest(ModifyProxyUserRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }

        if (request.getProxyUserId() == null) {
            throw new UnExpectedRequestException("ProxyUser id {&CAN_NOT_BE_NULL_OR_EMPTY}, request: " + request);
        }

        if (StringUtils.isBlank(request.getProxyUserName())) {
            throw new UnExpectedRequestException("ProxyUser name {&CAN_NOT_BE_NULL_OR_EMPTY}, request: " + request);
        }
        if (StringUtils.isNotBlank(request.getUserConfigJson())) {
            try {
                new ObjectMapper().readValue(request.getUserConfigJson(), Map.class);
            } catch (IOException e) {
                throw new UnExpectedRequestException("Error json format: user_config_json");
            }
        }
    }

    @Override
    public String toString() {
        return "ModifyProxyUserRequest{" +
                "proxyUserId=" + proxyUserId +
                ", proxyUserName='" + proxyUserName + '\'' +
                ", departmentInfo=" + departmentInfo +
                ", userConfigJson='" + userConfigJson + '\'' +
                ", department=" + department +
                '}';
    }
}
