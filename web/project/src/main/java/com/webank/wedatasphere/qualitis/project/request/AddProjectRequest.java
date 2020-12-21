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
import java.util.Set;

/**
 * @author howeye
 */
public class AddProjectRequest {

    @JsonProperty("project_name")
    private String projectName;
    private String description;
    private String username;

    @JsonProperty("project_label")
    private Set<String> projectLabels;

    public AddProjectRequest() {
        // Default Constructor
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<String> getProjectLabels() {
        return projectLabels;
    }

    public void setProjectLabels(Set<String> projectLabels) {
        this.projectLabels = projectLabels;
    }

    public static void checkRequest(AddProjectRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkString(request.getProjectName(), "project name");

        CommonChecker.checkStringLength(request.getProjectName(), 50, "project name");
        CommonChecker.checkStringLength(request.getDescription(), 500, "project description");
    }
}
