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
import java.util.List;

/**
 * @author howeye
 */
public class ProjectUserResponse {

    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("project_name")
    private String projectName;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("authorized_user")
    private String authorizedUser;
    @JsonProperty("permission")
    private List<Integer> permissions;

    public ProjectUserResponse() {
        // Default Constructor
    }

    public ProjectUserResponse(String projectName, String createUser, String authorIzedUser) {
        this.projectName = projectName;
        this.createUser = createUser;
        this.authorizedUser = authorIzedUser;
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

    public String getAuthorizedUser() {
        return authorizedUser;
    }

    public void setAuthorizedUser(String authorizedUser) {
        this.authorizedUser = authorizedUser;
    }

    public List<Integer> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Integer> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "ProjectUserResponse{" +
            "projectId=" + projectId +
            ", projectName='" + projectName + '\'' +
            ", createUser='" + createUser + '\'' +
            ", authorIzedUser='" + authorizedUser + '\'' +
            '}';
    }
}
