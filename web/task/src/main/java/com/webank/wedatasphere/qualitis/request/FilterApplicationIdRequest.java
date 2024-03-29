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

package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

/**
 * @author v_wblwyan
 * @date 2018-12-12
 */
public class FilterApplicationIdRequest {
    @JsonProperty("application_id")
    private String applicationId;
    @JsonProperty("filter_status")
    private Integer filterStatus;

    private Integer page;
    private Integer size;

    @JsonProperty("task_page")
    private Integer taskPage;
    @JsonProperty("task_size")
    private Integer taskSize;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getFilterStatus() {
        return filterStatus;
    }

    public void setFilterStatus(Integer filterStatus) {
        this.filterStatus = filterStatus;
    }

    public Integer getTaskPage() {
        return taskPage;
    }

    public void setTaskPage(Integer taskPage) {
        this.taskPage = taskPage;
    }

    public Integer getTaskSize() {
        return taskSize;
    }

    public void setTaskSize(Integer taskSize) {
        this.taskSize = taskSize;
    }

    public static void checkRequest(FilterApplicationIdRequest request) throws UnExpectedRequestException {
        com.webank.wedatasphere.qualitis.project.request.CommonChecker.checkObject(request, "request");
        CommonChecker.checkString(request.getApplicationId(), "application_id");

        CommonChecker.checkObject(request.getPage(), "page");
        CommonChecker.checkObject(request.getSize(), "size");

        if (request.getFilterStatus() != null) {
            CommonChecker.checkObject(request.getTaskPage(), "Task page");
            CommonChecker.checkObject(request.getTaskSize(), "Task size");
        }
    }
}
