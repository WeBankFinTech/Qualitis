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
import java.util.List;

/**
 * @author howeye
 */
public class GroupListExecutionRequest {
    @JsonProperty("rule_group_list")
    private List<GroupExecutionRequest> groupExecutionRequests;
    @JsonProperty("execution_param")
    private String executionParam;
    @JsonProperty("execution_user")
    private String executionUser;
    @JsonProperty("fps_file_id")
    private String fpsFileId;
    @JsonProperty("fps_hash")
    private String fpsHashValue;

    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("startup_param_name")
    private String startupParamName;
    @JsonProperty("set_flag")
    private String setFlag;
    @JsonProperty("dynamic_partition_bool")
    private boolean dyNamicPartition;
    @JsonProperty("dynamic_partition_prefix")
    private String dyNamicPartitionPrefix;

    @JsonProperty("split_by")
    private String splitBy;

    @JsonProperty("engine_reuse")
    private Boolean engineReuse;

    public GroupListExecutionRequest() {
    }

    public GroupListExecutionRequest(List<GroupExecutionRequest> groupExecutionRequests) {
        this.groupExecutionRequests = groupExecutionRequests;
    }

    public List<GroupExecutionRequest> getGroupExecutionRequests() {
        return groupExecutionRequests;
    }

    public void setGroupExecutionRequests(
        List<GroupExecutionRequest> groupExecutionRequests) {
        this.groupExecutionRequests = groupExecutionRequests;
    }

    public String getFpsFileId() {
        return fpsFileId;
    }

    public void setFpsFileId(String fpsFileId) {
        this.fpsFileId = fpsFileId;
    }

    public String getFpsHashValue() {
        return fpsHashValue;
    }

    public void setFpsHashValue(String fpsHashValue) {
        this.fpsHashValue = fpsHashValue;
    }

    public String getExecutionParam() {
        return executionParam;
    }

    public void setExecutionParam(String executionParam) {
        this.executionParam = executionParam;
    }

    public String getExecutionUser() {
        return executionUser;
    }

    public void setExecutionUser(String executionUser) {
        this.executionUser = executionUser;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getStartupParamName() {
        return startupParamName;
    }

    public void setStartupParamName(String startupParamName) {
        this.startupParamName = startupParamName;
    }

    public String getSetFlag() {
        return setFlag;
    }

    public void setSetFlag(String setFlag) {
        this.setFlag = setFlag;
    }

    public boolean getDyNamicPartition() {
        return dyNamicPartition;
    }

    public void setDyNamicPartition(boolean dyNamicPartition) {
        this.dyNamicPartition = dyNamicPartition;
    }

    public String getDyNamicPartitionPrefix() {
        return dyNamicPartitionPrefix;
    }

    public void setDyNamicPartitionPrefix(String dyNamicPartitionPrefix) {
        this.dyNamicPartitionPrefix = dyNamicPartitionPrefix;
    }

    public String getSplitBy() {
        return splitBy;
    }

    public void setSplitBy(String splitBy) {
        this.splitBy = splitBy;
    }

    public Boolean getEngineReuse() {
        return engineReuse;
    }

    public void setEngineReuse(Boolean engineReuse) {
        this.engineReuse = engineReuse;
    }
}
