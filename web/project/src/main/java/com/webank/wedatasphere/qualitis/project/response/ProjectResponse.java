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
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectLabel;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
public class ProjectResponse {
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("project_name")
    private String projectName;
    @JsonProperty("cn_name")
    private String cnName;
    private String description;
    @JsonProperty("project_label")
    private Set<String> projectLabel;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("modify_user")
    private String modifyUser;
    @JsonProperty("modify_time")
    private String modifyTime;
    @JsonProperty("sub_system_name")
    private String subSystemName;
    @JsonProperty("sub_system_id")
    private Long subSystemId;

    public ProjectResponse() {
    }

    public ProjectResponse(Project project) {
        this.projectId = project.getId();
        this.cnName = project.getCnName();
        this.projectName = project.getName();
        this.description = project.getDescription();
        this.createTime = project.getCreateTime();
        this.createUser = project.getCreateUser();
        this.modifyUser = project.getModifyUser();
        this.modifyTime = project.getModifyTime();
        this.subSystemName = project.getSubSystemName();
        this.subSystemId = project.getSubSystemId();
        Set<ProjectLabel> labelSet = project.getProjectLabels();
        if (CollectionUtils.isNotEmpty(labelSet)) {
            this.projectLabel = labelSet.stream().map(ProjectLabel::getLabelName).collect(Collectors.toSet());
        }
    }

    public ProjectResponse(Map<String, Object> project) {
        this.projectId = (Long) project.get("project_id");
        this.projectName = (String) project.get("project_name");
    }

    public Long getSubSystemId() {
        return subSystemId;
    }

    public void setSubSystemId(Long subSystemId) {
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

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getProjectLabel() {
        return projectLabel;
    }

    public void setProjectLabel(Set<String> projectLabel) {
        this.projectLabel = projectLabel;
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
        return "ProjectResponse{" +
            "projectId=" + projectId +
            ", projectName='" + projectName + '\'' +
            ", projectLabel=" + projectLabel +
            ", createTime='" + createTime + '\'' +
            ", modifyUser='" + modifyUser + '\'' +
            ", modifyTime='" + modifyTime + '\'' +
            '}';
    }
}
