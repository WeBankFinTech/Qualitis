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

package com.webank.wedatasphere.qualitis.service.impl;

import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.dao.TaskDataSourceDao;
import com.webank.wedatasphere.qualitis.dao.TaskResultDao;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.TaskDataSource;
import com.webank.wedatasphere.qualitis.entity.TaskResult;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.TaskCheckResultResponse;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.service.TaskService;
import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.dao.TaskDataSourceDao;
import com.webank.wedatasphere.qualitis.dao.TaskResultDao;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.TaskDataSource;
import com.webank.wedatasphere.qualitis.entity.TaskResult;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.TaskCheckResultResponse;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private TaskResultDao taskResultDao;

    @Autowired
    private TaskDataSourceDao taskDataSourceDao;

    @Override
    public GeneralResponse<?> getTaskDetail(Integer taskId) throws UnExpectedRequestException {
        Task taskInDb = taskDao.findByRemoteTaskId(taskId);
        if (taskInDb == null) {
            throw new UnExpectedRequestException("{&TASK_ID} [" + taskId + "] {&DOES_NOT_EXIST}");
        }

        // Find single table verification rules
        List<TaskRuleSimple> singleRuleIds = taskInDb.getTaskRuleSimples().stream().filter(taskRuleSimple ->
                !taskRuleSimple.getRuleType().equals(RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode())).collect(Collectors.toList());
        // Find all datasources of single table verification rules, and save it in the map
        Map<TaskRuleSimple, List<TaskDataSource>> singleRuleDataSourceMap = new HashMap<>(singleRuleIds.size());
        for (TaskRuleSimple taskRuleSimple : singleRuleIds) {
            singleRuleDataSourceMap.put(taskRuleSimple, taskDataSourceDao.findByTaskAndRuleId(taskInDb, taskRuleSimple.getRuleId()));
        }

        // Find multi-table verification rules
        List<TaskRuleSimple> multiRuleIds = taskInDb.getTaskRuleSimples().stream().filter(taskRuleSimple ->
                taskRuleSimple.getRuleType().equals(RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode())).collect(Collectors.toList());
        // Find all datasources of multi-table verification rules, and save it in the map
        Map<TaskRuleSimple, List<TaskDataSource>> multiRuleDataSourceMap = new HashMap<>(multiRuleIds.size());
        for (TaskRuleSimple taskRuleSimple : multiRuleIds) {
            multiRuleDataSourceMap.put(taskRuleSimple, taskDataSourceDao.findByTaskAndRuleId(taskInDb, taskRuleSimple.getRuleId()));
        }

        List<Long> allRuleIds = new ArrayList<>();
        for (TaskRuleSimple taskRuleSimple : taskInDb.getTaskRuleSimples()) {
            allRuleIds.add(taskRuleSimple.getRuleId());
            if (taskRuleSimple.getChildRuleSimple() != null) {
                allRuleIds.add(taskRuleSimple.getChildRuleSimple().getRuleId());
            }
        }
        List<TaskResult> allTaskResult = taskResultDao.findByApplicationIdAndRuleIdIn(taskInDb.getApplication().getId(), allRuleIds);
        Map<Long, TaskResult> allResultMap = new HashMap<>(allTaskResult.size());
        for (TaskResult taskResult : allTaskResult) {
            allResultMap.put(taskResult.getRuleId(), taskResult);
        }

        TaskCheckResultResponse taskCheckResultResponse = new TaskCheckResultResponse(taskInDb, singleRuleDataSourceMap, multiRuleDataSourceMap, allResultMap);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_TASK_DETAIL}", taskCheckResultResponse);
    }
}
