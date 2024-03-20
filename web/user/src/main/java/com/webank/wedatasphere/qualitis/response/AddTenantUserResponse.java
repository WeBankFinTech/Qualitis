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
import com.webank.wedatasphere.qualitis.entity.TenantUser;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.Column;

/**
 * @author allenzhou
 */
public class AddTenantUserResponse {
    @JsonProperty("tenant_user_id")
    private Long tenantUserId;
    @JsonProperty("tenant_user_name")
    private String tenantUserName;
    @JsonProperty("tenant_user_members_num")
    private Long tenantUserMembersNum;

    @JsonProperty("tenant_user_depts")
    private List<Long> deptIds;
    @JsonProperty("tenant_user_services")
    private List<Long> services;

    @JsonProperty("tenant_user_depts_name")
    private List<String> deptNames;
    @JsonProperty("tenant_user_services_name")
    private List<String> serviceNames;

    @JsonProperty("create_user")
    private String createUser;

    @JsonProperty("create_time")
    private String createTime;

    @JsonProperty("modify_user")
    private String modifyUser;

    @JsonProperty("modify_time")
    private String modifyTime;

    public AddTenantUserResponse() {
        // Default Constructor
    }

    public AddTenantUserResponse(TenantUser tenantUser) {
        this.tenantUserId = tenantUser.getId();
        this.tenantUserName = tenantUser.getTenantName();

        if (CollectionUtils.isNotEmpty(tenantUser.getDepts())) {
            deptIds = tenantUser.getDepts().stream().map(department -> department.getId()).collect(Collectors.toList());
            deptNames = tenantUser.getDepts().stream().map(department -> department.getName()).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(tenantUser.getServiceInfos())) {
            services = tenantUser.getServiceInfos().stream().map(serviceInfo -> serviceInfo.getId()).collect(Collectors.toList());
            serviceNames = tenantUser.getServiceInfos().stream().map(serviceInfo -> serviceInfo.getIp()).collect(Collectors.toList());
        }
        this.createUser = tenantUser.getCreateUser();
        this.createTime = tenantUser.getCreateTime();
        this.modifyUser = tenantUser.getModifyUser();
        this.modifyTime = tenantUser.getModifyTime();
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


    public Long getTenantUserId() {
        return tenantUserId;
    }

    public void setTenantUserId(Long tenantUserId) {
        this.tenantUserId = tenantUserId;
    }

    public String getTenantUserName() {
        return tenantUserName;
    }

    public void setTenantUserName(String tenantUserName) {
        this.tenantUserName = tenantUserName;
    }

    public Long getTenantUserMembersNum() {
        return tenantUserMembersNum;
    }

    public void setTenantUserMembersNum(Long tenantUserMembersNum) {
        this.tenantUserMembersNum = tenantUserMembersNum;
    }

    public List<Long> getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(List<Long> deptIds) {
        this.deptIds = deptIds;
    }

    public List<Long> getServices() {
        return services;
    }

    public void setServices(List<Long> services) {
        this.services = services;
    }

    public List<String> getDeptNames() {
        return deptNames;
    }

    public void setDeptNames(List<String> deptNames) {
        this.deptNames = deptNames;
    }

    public List<String> getServiceNames() {
        return serviceNames;
    }

    public void setServiceNames(List<String> serviceNames) {
        this.serviceNames = serviceNames;
    }
}
