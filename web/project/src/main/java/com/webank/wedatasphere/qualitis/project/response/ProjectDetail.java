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

package com.webank.wedatasphere.qualitis.project.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import java.util.Set;

/**
 * @author howeye
 */
public class ProjectDetail {

    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("cn_name")
    private String cnName;
    @JsonProperty("project_name")
    private String projectName;
    private String description;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("sub_system_id")
    private String subSystemId;
    @JsonProperty("sub_system_name")
    private String subSystemName;
    @JsonProperty("project_label")
    private Set<String> projectLabels;
    @JsonProperty("git_repo")
    private String gitRepo;
    @JsonProperty("git_type")
    private Integer gitType;
    @JsonProperty("git_branch")
    private String gitBranch;
    @JsonProperty("git_root_dir")
    private String gitRootDir;

    public ProjectDetail() {
        // Default Constructor
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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Set<String> getProjectLabels() {
        return projectLabels;
    }

    public void setProjectLabels(Set<String> projectLabels) {
        this.projectLabels = projectLabels;
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

    @Override
    public String toString() {
        return "ProjectDetail{" +
                "projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
