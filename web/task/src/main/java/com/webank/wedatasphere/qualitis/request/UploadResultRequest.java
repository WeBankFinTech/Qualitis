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
import java.util.List;

/**
 * @author howeye
 */
public class UploadResultRequest {

    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("database_name")
    private String databaseName;
    @JsonProperty("table_name")
    private String tableName;
    @JsonProperty("start_time")
    private String startTime;
    @JsonProperty("end_time")
    private String endTime;
    @JsonProperty("status_conditions")
    private List<Integer> status;
    @JsonProperty("hdfs_path")
    private String hdfsPath;

    public UploadResultRequest() {}

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<Integer> getStatus() {
        return status;
    }

    public void setStatus(List<Integer> status) {
        this.status = status;
    }

    public String getHdfsPath() {
        return hdfsPath;
    }

    public void setHdfsPath(String hdfsPath) {
        this.hdfsPath = hdfsPath;
    }

    public static void checkRequest(UploadResultRequest request) throws UnExpectedRequestException {
        CommonChecker.checkString(request.getClusterName(), "Cluster Name");
        CommonChecker.checkString(request.getDatabaseName(), "Database Name");

        CommonChecker.checkString(request.getStartTime(), "Start Time");
        CommonChecker.checkString(request.getEndTime(), "End Time");

        CommonChecker.checkCollections(request.getStatus(), "Status Conditions");
        CommonChecker.checkString(request.getHdfsPath(), "HDFS Path");
    }
}
