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

import com.webank.wedatasphere.qualitis.project.constant.ProjectTransportTypeEnum;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author howeye
 */
public class DownloadProjectRequest {

    @JsonProperty("project_ids")
    private List<Long> projectId;

    @JsonProperty("download_type")
    private Integer downloadType;

    @JsonProperty("diff_variables")
    private List<DiffVariableRequest> diffVariableRequestList;

    public DownloadProjectRequest() {
        // Default Constructor
    }

    public List<Long> getProjectId() {
        return projectId;
    }

    public void setProjectId(List<Long> projectId) {
        this.projectId = projectId;
    }

    public Integer getDownloadType() {
        return downloadType;
    }

    public void setDownloadType(Integer downloadType) {
        this.downloadType = downloadType;
    }

    public List<DiffVariableRequest> getDiffVariableRequestList() {
        return diffVariableRequestList;
    }

    public void setDiffVariableRequestList(List<DiffVariableRequest> diffVariableRequestList) {
        this.diffVariableRequestList = diffVariableRequestList;
    }

    public static void checkRequest(DownloadProjectRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("Request {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        if (CollectionUtils.isEmpty(request.getProjectId())) {
            throw new UnExpectedRequestException("Project {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        if (request.getDownloadType() == null) {
            throw new UnExpectedRequestException("Download type {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }
}
