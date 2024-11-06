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
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
    @Column(length = 128)
    private String name;
    @Column(name = "cn_name", length = 128)
    private String cnName;
    @Column(length = 1700)
    private String description;

    @OneToMany(mappedBy = "project", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<ProjectUser> projectUsers;

    @OneToMany(mappedBy = "project", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<ProjectLabel> projectLabels;

    @Column(name = "create_user", length = 50)
    private String createUser;
    /**
     * Full name, such as: Tom(chinese_name)
     */
    @Column(name = "create_user_full_name", length = 100)
    private String createUserFullName;

    @Column(name = "department", length = 50)
    private String department;

    @Column(name = "sub_system_id")
    private String subSystemId;

    @Column(name = "sub_system_name")
    private String subSystemName;

    @Column(name = "create_time", length = 25)
    private String createTime;

    @Column(name = "modify_user", length = 50)
    private String modifyUser;
    /**
     * Full name, such as: Tom(chinese_name)
     */
    @Column(name = "modify_user_full_name", length = 100)
    private String modifyUserFullName;

    @Column(name = "modify_time", length = 25)
    private String modifyTime;

    @Column(name = "project_type")
    private Integer projectType;

    @Column(name = "git_repo")
    private String gitRepo;

    @Column(name = "git_type")
    private Integer gitType;

    @Column(name = "git_branch")
    private String gitBranch;

    @Column(name = "git_root_dir")
    private String gitRootDir;

    @Column(name = "run_status")
    private Integer runStatus;

    public Project() {
    }

    public Project(String projectName, String cnName, String description, String username, String chineseName, String department, String createTime) {
        this.name = projectName;
        this.cnName = cnName;
        this.description = description;
        this.createUser = username;
        this.createUserFullName = username + "(" + (StringUtils.isNotEmpty(chineseName) ? chineseName : "") + ")";
        this.department = department;
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

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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

    public String getModifyUserFullName() {
        return modifyUserFullName;
    }

    public void setModifyUserFullName(String modifyUserFullName) {
        this.modifyUserFullName = modifyUserFullName;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getSubSystemId() {
        return subSystemId;
    }

    public void setSubSystemId(String subSystemId) {
        this.subSystemId = subSystemId;
    }

    public String getSubSystemName() {
        return subSystemName;
    }

    public void setSubSystemName(String subSystemName) {
        this.subSystemName = subSystemName;
    }

    public String getGitRepo() {
        return gitRepo;
    }

    public void setGitRepo(String gitRepo) {
        this.gitRepo = gitRepo;
    }

    public Integer getGitType() {
        return gitType;
    }

    public void setGitType(Integer gitType) {
        this.gitType = gitType;
    }

    public String getGitBranch() {
        return gitBranch;
    }

    public void setGitBranch(String gitBranch) {
        this.gitBranch = gitBranch;
    }

    public String getGitRootDir() {
        return gitRootDir;
    }

    public void setGitRootDir(String gitRootDir) {
        this.gitRootDir = gitRootDir;
    }

    public Integer getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(Integer runStatus) {
        this.runStatus = runStatus;
    }

    @Override
    public String toString() {
        return "Project{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", cnName='" + cnName + '\'' +
            ", description='" + description + '\'' +
            ", createUser='" + createUser + '\'' +
            ", createUserFullName='" + createUserFullName + '\'' +
            ", userDepartment='" + department + '\'' +
            ", subSystemId=" + subSystemId +
            ", subSystemName='" + subSystemName + '\'' +
            ", createTime='" + createTime + '\'' +
            ", modifyUser='" + modifyUser + '\'' +
            ", modifyTime='" + modifyTime + '\'' +
            ", projectType=" + projectType +
            ", gitRepo='" + gitRepo + '\'' +
            ", gitType=" + gitType +
            ", gitBranch='" + gitBranch + '\'' +
            ", gitRootDir='" + gitRootDir + '\'' +
            '}';
    }
}
