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
import java.util.Set;

/**
 * @author howeye
 */
public class ProjectDetail {

    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("project_name")
    private String projectName;
    private String description;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("project_label")
    private Set<String> projectLabels;

    public ProjectDetail() {
        // Default Constructor
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

    @Override
    public String toString() {
        return "ProjectDetail{" +
                "projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
