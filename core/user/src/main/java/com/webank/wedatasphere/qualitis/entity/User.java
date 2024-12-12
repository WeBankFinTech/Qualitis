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

import org.springframework.boot.json.JacksonJsonParser;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author howeye
 */
@Entity
@Table(name = "qualitis_auth_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 30, unique = true)
    private String username;
    @Column(length = 64)
    private String password;
    private String chineseName;
    @ManyToOne(fetch = FetchType.EAGER)
    private Department department;
    @Column(name = "user_config_json", columnDefinition = "MEDIUMTEXT")
    private String userConfigJson;
    @Transient
    private Map<String, Object> userConfigMap;
    @Column(name="department")
    private String departmentName;
    /**
     * 科室编码
     */
    @Column(name="sub_department_code")
    private Long subDepartmentCode;

    @Column(name = "create_user")
    private String createUser;

    @Column(name = "create_time")
    private String createTime;

    @Column(name = "modify_user")
    private String modifyUser;

    @Column(name = "modify_time")
    private String modifyTime;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "qualitis_auth_user_role",
            joinColumns = {@JoinColumn(name="user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private Set<Role> roles;

    @OneToMany(mappedBy = "user")
    private List<UserRole> userRoles;

    @OneToMany(mappedBy = "user")
    private List<UserSpecPermission> userSpecPermissions;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "qualitis_auth_user_permission",
            joinColumns = {@JoinColumn(name="user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id", referencedColumnName = "id")})
    private Set<Permission> specPermissions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<UserProxyUser> userProxyUsers;

    public User() {
        // Default Constructor
    }

    public String getUserConfigJson() {
        return userConfigJson;
    }

    public void setUserConfigJson(String userConfigJson) {
        this.userConfigJson = userConfigJson;
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

    public Long getSubDepartmentCode() {
        return subDepartmentCode;
    }

    public void setSubDepartmentCode(Long subDepartmentCode) {
        this.subDepartmentCode = subDepartmentCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Permission> getSpecPermissions() {
        return specPermissions;
    }

    public void setSpecPermissions(Set<Permission> specPermissions) {
        this.specPermissions = specPermissions;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public List<UserSpecPermission> getUserSpecPermissions() {
        return userSpecPermissions;
    }

    public void setUserSpecPermissions(List<UserSpecPermission> userSpecPermissions) {
        this.userSpecPermissions = userSpecPermissions;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Set<UserProxyUser> getUserProxyUsers() {
        return userProxyUsers;
    }

    public void setUserProxyUsers(Set<UserProxyUser> userProxyUsers) {
        this.userProxyUsers = userProxyUsers;
    }

    public Map<String, Object> getUserConfigMap() {
        if (this.userConfigMap == null && this.userConfigJson != null) {
            this.userConfigMap = new JacksonJsonParser().parseMap(this.userConfigJson);
        } else {
            this.userConfigMap = new HashMap<>();
        }
        return this.userConfigMap;
    }

}
