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
import java.util.HashSet;
import java.util.Set;

/**
 * @author howeye
 */
public class ProjectResponse {
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("project_name")
    private String projectName;
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

    public ProjectResponse() {
    }

    public ProjectResponse(Project project) {
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.description = project.getDescription();
        this.createTime = project.getCreateTime();
        this.createUser = project.getCreateUser();
        this.modifyUser = project.getModifyUser();
        this.modifyTime = project.getModifyTime();
        Set<ProjectLabel> labelSet = project.getProjectLabels();
        if (labelSet != null && ! labelSet.isEmpty()) {
            Set<String> labels = new HashSet<>();
            for (ProjectLabel projectLabel : labelSet) {
                labels.add(projectLabel.getLabelName());
            }
            this.projectLabel = labels;
        }
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
