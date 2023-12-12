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

import com.google.common.collect.Lists;
import com.webank.wedatasphere.qualitis.constant.TaskStatusEnum;
import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.dao.TaskDataSourceDao;
import com.webank.wedatasphere.qualitis.dao.TaskResultDao;
import com.webank.wedatasphere.qualitis.entity.*;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.TaskCheckResultResponse;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.scheduled.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.service.TaskService;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.commons.lang3.time.DateUtils;
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

    @Autowired
    private RuleDao ruleDao;

    private FastDateFormat PRINT_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    @Override
    public GeneralResponse<TaskCheckResultResponse> getTaskDetail(Long taskId) throws UnExpectedRequestException {
        Task taskInDb = taskDao.findById(taskId);

        if (taskInDb == null) {
            throw new UnExpectedRequestException("Job id [" + taskId + "] {&DOES_NOT_EXIST}");
        }

        List<TaskRuleAlarmConfig> distinct;
        Set<TaskRuleSimple> taskRuleSimples = new HashSet<>(taskInDb.getTaskRuleSimples().size());
        for (TaskRuleSimple taskRuleSimple : taskInDb.getTaskRuleSimples()) {
            distinct = taskRuleSimple.getTaskRuleAlarmConfigList().stream().distinct().collect(Collectors.toList());
            taskRuleSimple.setTaskRuleAlarmConfigList(distinct);
            taskRuleSimples.add(taskRuleSimple);
        }
        taskInDb.setTaskRuleSimples(taskRuleSimples);

        // Find single table verification rules
        List<TaskRuleSimple> singleRuleIds = taskInDb.getTaskRuleSimples().stream().filter(taskRuleSimple ->
                taskRuleSimple.getRuleType().equals(RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode())).collect(Collectors.toList());
        // Find all datasources of single table verification rules, and save it in the map
        Map<TaskRuleSimple, List<TaskDataSource>> singleRuleDataSourceMap = new HashMap<>(singleRuleIds.size());
        for (TaskRuleSimple taskRuleSimple : singleRuleIds) {
            singleRuleDataSourceMap.put(taskRuleSimple, taskDataSourceDao.findByTaskAndRuleId(taskInDb, taskRuleSimple.getRuleId()));
        }

        // Find custom table verification rules
        List<TaskRuleSimple> customRuleIds = taskInDb.getTaskRuleSimples().stream().filter(taskRuleSimple ->
            taskRuleSimple.getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())).collect(Collectors.toList());
        // Find all datasources of custom table verification rules, and save it in the map
        Map<TaskRuleSimple, List<TaskDataSource>> customRuleDataSourceMap = new HashMap<>(customRuleIds.size());
        for (TaskRuleSimple taskRuleSimple : customRuleIds) {
            customRuleDataSourceMap.put(taskRuleSimple, taskDataSourceDao.findByTaskAndRuleId(taskInDb, taskRuleSimple.getRuleId()));
        }

        // Find multi-table verification rules
        List<TaskRuleSimple> multiRuleIds = taskInDb.getTaskRuleSimples().stream().filter(taskRuleSimple ->
                taskRuleSimple.getRuleType().equals(RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode())).collect(Collectors.toList());
        // Find all datasources of multi-table verification rules, and save it in the map
        Map<TaskRuleSimple, List<TaskDataSource>> multiRuleDataSourceMap = new HashMap<>(multiRuleIds.size());
        for (TaskRuleSimple taskRuleSimple : multiRuleIds) {
            multiRuleDataSourceMap.put(taskRuleSimple, taskDataSourceDao.findByTaskAndRuleId(taskInDb, taskRuleSimple.getRuleId()));
        }

        // Find file table verification rules
        List<TaskRuleSimple> fileRuleIds = taskInDb.getTaskRuleSimples().stream().filter(taskRuleSimple ->
            taskRuleSimple.getRuleType().equals(RuleTypeEnum.FILE_TEMPLATE_RULE.getCode())).collect(Collectors.toList());
        // Find all datasources of file table verification rules, and save it in the map
        Map<TaskRuleSimple, List<TaskDataSource>> fileRuleDataSourceMap = new HashMap<>(fileRuleIds.size());
        for (TaskRuleSimple taskRuleSimple : fileRuleIds) {
            fileRuleDataSourceMap.put(taskRuleSimple, taskDataSourceDao.findByTaskAndRuleId(taskInDb, taskRuleSimple.getRuleId()));
        }

        List<Long> allRuleIds = new ArrayList<>();
        for (TaskRuleSimple taskRuleSimple : taskInDb.getTaskRuleSimples()) {
            allRuleIds.add(taskRuleSimple.getRuleId());
        }
        List<TaskResult> allTaskResult = taskResultDao.findByApplicationIdAndRuleIn(taskInDb.getApplication().getId(), allRuleIds);
        Map<Long, List<TaskResult>> allResultMap = new HashMap<>(allTaskResult.size());
        for (TaskResult taskResult : allTaskResult) {
            if (allResultMap.get(taskResult.getRuleId()) != null) {
                allResultMap.get(taskResult.getRuleId()).add(taskResult);
            } else {
                List<TaskResult> taskResults = new ArrayList<>(1);
                taskResults.add(taskResult);
                allResultMap.put(taskResult.getRuleId(), taskResults);
            }
        }

        TaskCheckResultResponse taskCheckResultResponse = new TaskCheckResultResponse(taskInDb, singleRuleDataSourceMap, customRuleDataSourceMap
            , multiRuleDataSourceMap, fileRuleDataSourceMap, allResultMap);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_TASK_DETAIL}", taskCheckResultResponse);
    }

    @Override
    public Long getExecutingTaskNumber(int dataRangeInHours) {
        String startBeginTime = PRINT_TIME_FORMAT.format(DateUtils.addHours(new Date(), dataRangeInHours));
        String endBeginTime = PRINT_TIME_FORMAT.format(new Date());
        List<Integer> executingStatusList = Lists.newArrayListWithExpectedSize(4);
        executingStatusList.add(TaskStatusEnum.SCHEDULED.getCode());
        executingStatusList.add(TaskStatusEnum.SUBMITTED.getCode());
        executingStatusList.add(TaskStatusEnum.INITED.getCode());
        executingStatusList.add(TaskStatusEnum.RUNNING.getCode());
        return taskDao.countExecutingTaskNumber(startBeginTime, endBeginTime, executingStatusList);
    }
}
