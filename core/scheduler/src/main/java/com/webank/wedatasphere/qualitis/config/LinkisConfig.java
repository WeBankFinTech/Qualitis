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

package com.webank.wedatasphere.qualitis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author howeye
 */
@Configuration
public class LinkisConfig {

    @Value("${linkis.api.prefix}")
    private String prefix;

    @Value("${linkis.api.submitJob}")
    private String submitJob;

    @Value("${linkis.api.runningLog}")
    private String runningLog;

    @Value("${linkis.api.finishLog}")
    private String finishLog;

    @Value("${linkis.api.status}")
    private String status;

    @Value("${linkis.log.maskKey}")
    private String logMaskKey;


    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getLogMaskKey() {
        return logMaskKey;
    }

    public void setLogMaskKey(String logMaskKey) {
        this.logMaskKey = logMaskKey;
    }

    public String getSubmitJob() {
        return submitJob;
    }

    public void setSubmitJob(String submitJob) {
        this.submitJob = submitJob;
    }

    public String getRunningLog() {
        return runningLog;
    }

    public void setRunningLog(String runningLog) {
        this.runningLog = runningLog;
    }

    public String getFinishLog() {
        return finishLog;
    }

    public void setFinishLog(String finishLog) {
        this.finishLog = finishLog;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
