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

package com.webank.wedatasphere.qualitis.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
public class DataQualityJob {
    private List<String> jobCode;
    private Long taskId;
    private String user;
    private String startupParam;

    public DataQualityJob() {
        jobCode = new ArrayList<>();
    }

    public List<String> getJobCode() {
        return jobCode;
    }

    public void setJobCode(List<String> jobCode) {
        this.jobCode = jobCode;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getStartupParam() {
        return startupParam;
    }

    public void setStartupParam(String startupParam) {
        this.startupParam = startupParam;
    }

    @Override
    public String toString() {
        return "DataQualityJob{" +
                "jobCode=" + jobCode +
                ", taskId=" + taskId +
                '}';
    }
}
