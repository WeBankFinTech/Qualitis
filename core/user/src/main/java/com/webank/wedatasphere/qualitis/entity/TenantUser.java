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

package com.webank.wedatasphere.qualitis.entity;

import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author allenzhou
 */
@Entity
@Table(name = "qualitis_auth_tenant")
public class TenantUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "tenant_name", length = 20)
    private String tenantName;

    @OneToMany(mappedBy = "tenantUser", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<UserTenantUser> userTenantUsers;

    @OneToMany(mappedBy = "tenantUser", fetch = FetchType.EAGER)
    private Set<ServiceInfo> serviceInfos;
    @OneToMany(mappedBy = "tenantUser", fetch = FetchType.EAGER)
    private Set<Department> depts;

    @Column(name = "create_user")
    private String createUser;

    @Column(name = "create_time")
    private String createTime;

    @Column(name = "modify_user")
    private String modifyUser;

    @Column(name = "modify_time")
    private String modifyTime;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public List<UserTenantUser> getUserTenantUsers() {
        return userTenantUsers;
    }

    public void setUserTenantUsers(List<UserTenantUser> userTenantUsers) {
        this.userTenantUsers = userTenantUsers;
    }

    public Set<ServiceInfo> getServiceInfos() {
        return serviceInfos;
    }

    public void setServiceInfos(Set<ServiceInfo> serviceInfos) {
        this.serviceInfos = serviceInfos;
    }

    public Set<Department> getDepts() {
        return depts;
    }

    public void setDepts(Set<Department> depts) {
        this.depts = depts;
    }
}
