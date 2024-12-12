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

package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.TaskDataSource;
import com.webank.wedatasphere.qualitis.entity.TaskResult;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;

import java.util.*;

/**
 * @author howeye
 */
public class TaskCheckResultResponse {

    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("saved_db")
    private String savedDb;
    @JsonProperty("check_datasource")
    private CheckDataSourceResponse checkDataSourceResponse;
    
    public TaskCheckResultResponse() {
    }

    public TaskCheckResultResponse(Task task, Map<TaskRuleSimple, List<TaskDataSource>> singleRuleDataSourceMap,
        Map<TaskRuleSimple, List<TaskDataSource>> customRuleDataSourceMap,
        Map<TaskRuleSimple, List<TaskDataSource>> multiRuleDataSourceMap,
        Map<TaskRuleSimple, List<TaskDataSource>> fileRuleDataSourceMap,
        Map<Long, List<TaskResult>> taskResultMap) {
        this.clusterName = task.getClusterName();
        this.savedDb = task.getApplication().getSavedDb();
        this.checkDataSourceResponse = new CheckDataSourceResponse();
        List<CheckSingleDataSourceResponse> single = new ArrayList<>();
        List<CheckCustomDataSourceResponse> custom = new ArrayList<>();
        List<CheckMultipleDataSourceResponse> multiple = new ArrayList<>();
        List<CheckFileDataSourceResponse> file = new ArrayList<>();
        // 根据单表数据源进行划分TaskRuleSimple
        Map<String, List<TaskDataSource>> partitionedByDbAndTable = partitionByDbAndTable(singleRuleDataSourceMap.values());
        Map<Long, TaskRuleSimple> taskRuleSimpleMap = getTaskRuleSimpleMap(singleRuleDataSourceMap);
        for (List<TaskDataSource> taskDataSources : partitionedByDbAndTable.values()) {
            TaskDataSource taskDataSource = taskDataSources.get(0);
            single.add(new CheckSingleDataSourceResponse(taskDataSource.getDatabaseName(), taskDataSource.getTableName(), taskDataSources, taskRuleSimpleMap, taskResultMap));
        }
        this.checkDataSourceResponse.setSingle(single);

        // 根据自定义规则的复杂语句解析响应结果
        Map<Long, TaskRuleSimple> taskRuleSimpleCustomMap = getTaskRuleSimpleMap(customRuleDataSourceMap);
        for (TaskRuleSimple taskRuleSimple : customRuleDataSourceMap.keySet()) {
            custom.add(new CheckCustomDataSourceResponse(customRuleDataSourceMap.get(taskRuleSimple), taskRuleSimpleCustomMap, taskResultMap));
        }
        this.checkDataSourceResponse.setCustom(custom);

        // 根据跨表数据源进行划分TaskRuleSimple
        Map<String, List<Map<TaskRuleSimple, List<TaskDataSource>>>> partitionedMultiRule = partitionMultiRule(multiRuleDataSourceMap);
        for (List<Map<TaskRuleSimple, List<TaskDataSource>>> taskDataSourcesList : partitionedMultiRule.values()) {
            // 同一堆
            multiple.add(new CheckMultipleDataSourceResponse(taskDataSourcesList, taskResultMap));
        }
        this.checkDataSourceResponse.setMultiple(multiple);

        // 根据文件表数据源进行划分TaskRuleSimple
        Map<String, List<TaskDataSource>> partitionedFileByDbAndTable = partitionByDbAndTable(fileRuleDataSourceMap.values());
        Map<Long, TaskRuleSimple> taskFileRuleSimpleMap = getTaskRuleSimpleMap(fileRuleDataSourceMap);
        for (List<TaskDataSource> taskDataSources : partitionedFileByDbAndTable.values()) {
            TaskDataSource taskDataSource = taskDataSources.get(0);
            file.add(new CheckFileDataSourceResponse(taskDataSource.getDatabaseName(), taskDataSource.getTableName(), taskDataSources, taskFileRuleSimpleMap, taskResultMap));
        }
        this.checkDataSourceResponse.setFile(file);
    }

    private Map<String, List<Map<TaskRuleSimple, List<TaskDataSource>>>> partitionMultiRule(Map<TaskRuleSimple, List<TaskDataSource>> multiRuleDataSourceMap) {
        Map<String, List<Map<TaskRuleSimple, List<TaskDataSource>>>> result = new HashMap<>(8);
        for (TaskRuleSimple taskRuleSimple : multiRuleDataSourceMap.keySet()) {
            List<TaskDataSource> taskDataSources = multiRuleDataSourceMap.get(taskRuleSimple);
            String sourceDb = null;
            String sourceTable = null;
            String targetDb = null;
            String targetTable = null;
            for (TaskDataSource taskDataSource : taskDataSources) {
                if (taskDataSource.getDatasourceIndex() == 0) {
                    sourceDb = taskDataSource.getDatabaseName();
                    sourceTable = taskDataSource.getTableName();
                } else {
                    targetDb = taskDataSource.getDatabaseName();
                    targetTable = taskDataSource.getTableName();
                }
            }
            String key = sourceDb + "." + sourceTable + "." + targetDb + "." + targetTable;
            if (result.containsKey(key)) {
                List<Map<TaskRuleSimple, List<TaskDataSource>>> tmp = result.get(key);
                tmp.add(Collections.singletonMap(taskRuleSimple, taskDataSources));
            } else {
                List<Map<TaskRuleSimple, List<TaskDataSource>>> tmp = new ArrayList<>();
                tmp.add(Collections.singletonMap(taskRuleSimple, taskDataSources));
                result.put(key, tmp);
            }
        }
        return result;
    }

    private Map<String, List<TaskDataSource>> partitionByDbAndTable(Collection<List<TaskDataSource>> taskDataSources) {
        Map<String, List<TaskDataSource>> map = new HashMap<>(8);
        for (List<TaskDataSource> taskDataSourceList : taskDataSources) {
            for (TaskDataSource taskDataSource : taskDataSourceList) {
                String key = taskDataSource.getDatabaseName() + "." + taskDataSource.getTableName();
                if (map.containsKey(key)) {
                    map.get(key).add(taskDataSource);
                } else {
                    List<TaskDataSource> tmp = new ArrayList<>();
                    tmp.add(taskDataSource);
                    map.put(key, tmp);
                }
            }
        }
        return map;
    }

    private Map<Long, TaskRuleSimple> getTaskRuleSimpleMap(Map<TaskRuleSimple, List<TaskDataSource>> ruleDataSourceMap) {
        Map<Long, TaskRuleSimple> map = new HashMap<>(ruleDataSourceMap.keySet().size());
        for (TaskRuleSimple taskRuleSimple : ruleDataSourceMap.keySet()) {
            map.put(taskRuleSimple.getRuleId(), taskRuleSimple);
        }
        return map;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getSavedDb() {
        return savedDb;
    }

    public void setSavedDb(String savedDb) {
        this.savedDb = savedDb;
    }

    public CheckDataSourceResponse getCheckDataSourceResponse() {
        return checkDataSourceResponse;
    }

    public void setCheckDataSourceResponse(CheckDataSourceResponse checkDataSourceResponse) {
        this.checkDataSourceResponse = checkDataSourceResponse;
    }

    @Override
    public String toString() {
        return "TaskCheckResultResponse{" +
                "clusterName='" + clusterName + '\'' +
                ", savedDb='" + savedDb + '\'' +
                ", checkDataSourceResponse=" + checkDataSourceResponse +
                '}';
    }
}
