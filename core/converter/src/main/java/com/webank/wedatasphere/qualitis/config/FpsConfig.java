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
 * @author allenzhou
 */
@Configuration
public class FpsConfig {
    @Value("${linkis.api.prefix}")
    private String prefix;
    @Value("${linkis.api.submitJob}")
    private String submitJob;
    @Value("${linkis.api.submitJobNew}")
    private String submitJobNew;
    @Value("${linkis.api.status}")
    private String status;
    @Value("${linkis.spark.application.name}")
    private String appName;
    @Value("${linkis.fps.hdfs_prefix}")
    private String hdfsPrefix;
    @Value("${linkis.fps.file_system}")
    private String fileSystem;

    @Value("${linkis.spark.application.reparation}")
    private Integer reparation;

    @Value("${linkis.lightweight_query}")
    private Boolean lightweightQuery;

    @Value("${linkis.fps.application.engine.version}")
    private String engineVersion;

    @Value("${linkis.fps.application.name}")
    private String fpsApplicationName;

    @Value("${linkis.fps.request.max_retries}")
    private int maxRetries = 10;
    @Value("${linkis.fps.request.total_wait_duration}")
    private int totalWaitDuration = 10 * 1000;

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getTotalWaitDuration() {
        return totalWaitDuration;
    }

    public void setTotalWaitDuration(int totalWaitDuration) {
        this.totalWaitDuration = totalWaitDuration;
    }

    public String getHdfsPrefix() {
        return hdfsPrefix;
    }

    public void setHdfsPrefix(String hdfsPrefix) {
        this.hdfsPrefix = hdfsPrefix;
    }

    public String getFileSystem() {
        return fileSystem;
    }

    public void setFileSystem(String fileSystem) {
        this.fileSystem = fileSystem;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSubmitJob() {
        return submitJob;
    }

    public void setSubmitJob(String submitJob) {
        this.submitJob = submitJob;
    }

    public String getSubmitJobNew() {
        return submitJobNew;
    }

    public void setSubmitJobNew(String submitJobNew) {
        this.submitJobNew = submitJobNew;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Integer getReparation() {
        return reparation;
    }

    public void setReparation(Integer reparation) {
        this.reparation = reparation;
    }

    public Boolean getLightweightQuery() {
        return lightweightQuery;
    }

    public void setLightweightQuery(Boolean lightweightQuery) {
        this.lightweightQuery = lightweightQuery;
    }

    public String getEngineVersion() {
        return engineVersion;
    }

    public void setEngineVersion(String engineVersion) {
        this.engineVersion = engineVersion;
    }

    public String getFpsApplicationName() {
        return fpsApplicationName;
    }

    public void setFpsApplicationName(String fpsApplicationName) {
        this.fpsApplicationName = fpsApplicationName;
    }
}
