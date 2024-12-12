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

package com.webank.wedatasphere.qualitis.project.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import java.util.List;

/**
 * @author allenzhou
 */
public class AuthorizeProjectUserRequest {

    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("project_user")
    private String projectUser;
    @JsonProperty("project_permissions")
    private List<Integer> projectPermissions;

    public AuthorizeProjectUserRequest() {
        // Default Constructor
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<Integer> getProjectPermissions() {
        return projectPermissions;
    }

    public void setProjectPermissions(List<Integer> projectPermissions) {
        this.projectPermissions = projectPermissions;
    }

    public String getProjectUser() {
        return projectUser;
    }

    public void setProjectUser(String projectUser) {
        this.projectUser = projectUser;
    }

    public static void checkRequest(AuthorizeProjectUserRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkObject(request.getProjectId(), "project ID");
        CommonChecker.checkCollections(request.getProjectPermissions(), "project permissions");
    }
}
