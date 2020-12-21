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

package com.webank.wedatasphere.qualitis.project.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import java.util.Set;

/**
 * @author howeye
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 170)
    private String name;
    @Column(length = 1700)
    private String description;

    @OneToMany(mappedBy = "project", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private Set<ProjectUser> projectUsers;

    @OneToMany(mappedBy = "project", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private Set<ProjectLabel> projectLabels;

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<Rule> rules;

    @Column(name = "create_user", length = 50)
    private String createUser;
    /**
     * Full name, such as: Tom(chinese_name)
     */
    @Column(name = "create_user_full_name", length = 50)
    private String createUserFullName;

    @Column(name = "user_department", length = 50)
    private String userDepartment;

    @Column(name = "create_time", length = 25)
    private String createTime;

    @Column(name = "modify_user", length = 50)
    private String modifyUser;

    @Column(name = "modify_time", length = 25)
    private String modifyTime;

    @Column(name = "project_type")
    private Integer projectType;

    public Project() {
    }

    public Project(String projectName, String description, String username, String chineseName, String department) {
        this.name = projectName;
        this.description = description;
        this.createUser = username;
        this.createUserFullName = username + "(" + chineseName + ")";
        this.userDepartment = department;
    }

    public Project(String projectName, String description, String username, String chineseName, String department, String createTime) {
        this.name = projectName;
        this.description = description;
        this.createUser = username;
        this.createUserFullName = username + "(" + chineseName + ")";
        this.userDepartment = department;
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<ProjectUser> getProjectUsers() {
        return projectUsers;
    }

    public void setProjectUsers(Set<ProjectUser> projectUsers) {
        this.projectUsers = projectUsers;
    }

    public Set<ProjectLabel> getProjectLabels() {
        return projectLabels;
    }

    public void setProjectLabels(Set<ProjectLabel> projectLabels) {
        this.projectLabels = projectLabels;
    }

    public Set<Rule> getRules() {
        return rules;
    }

    public void setRules(Set<Rule> rules) {
        this.rules = rules;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateUserFullName() {
        return createUserFullName;
    }

    public void setCreateUserFullName(String createUserFullName) {
        this.createUserFullName = createUserFullName;
    }

    public String getUserDepartment() {
        return userDepartment;
    }

    public void setUserDepartment(String userDepartment) {
        this.userDepartment = userDepartment;
    }

    public Integer getProjectType() {
        return projectType;
    }

    public void setProjectType(Integer projectType) {
        this.projectType = projectType;
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
        return "Project{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", createUser='" + createUser + '\'' +
            ", createUserFullName='" + createUserFullName + '\'' +
            ", userDepartment='" + userDepartment + '\'' +
            '}';
    }
}
