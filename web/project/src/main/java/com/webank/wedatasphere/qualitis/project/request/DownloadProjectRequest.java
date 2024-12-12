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
 * @author howeye
 */
public class DownloadProjectRequest {

    @JsonProperty("project_ids")
    private List<Long> projectId;

    public DownloadProjectRequest() {
        // Default Constructor
    }

    public List<Long> getProjectId() {
        return projectId;
    }

    public void setProjectId(List<Long> projectId) {
        this.projectId = projectId;
    }

    public static void checkRequest(DownloadProjectRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("");
        }
        if (request.getProjectId() == null) {
            throw new UnExpectedRequestException("Project ids {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }
}
