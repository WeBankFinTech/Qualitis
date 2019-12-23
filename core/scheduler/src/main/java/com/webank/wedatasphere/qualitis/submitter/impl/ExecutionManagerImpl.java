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

package com.webank.wedatasphere.qualitis.submitter.impl;

import com.webank.wedatasphere.qualitis.bean.*;
import com.webank.wedatasphere.qualitis.client.AbstractJobSubmitter;
import com.webank.wedatasphere.qualitis.config.TaskExecuteLimitConfig;
import com.webank.wedatasphere.qualitis.constant.TaskStatusEnum;
import com.webank.wedatasphere.qualitis.converter.TemplateConverterFactory;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.dao.repository.TaskDataSourceRepository;
import com.webank.wedatasphere.qualitis.dao.repository.TaskRuleSimpleRepository;
import com.webank.wedatasphere.qualitis.divider.TaskDividerFactory;
import com.webank.wedatasphere.qualitis.bean.DataQualityJob;
import com.webank.wedatasphere.qualitis.bean.DataQualityTask;
import com.webank.wedatasphere.qualitis.entity.*;
import com.webank.wedatasphere.qualitis.exception.*;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.exception.JobSubmitException;
import com.webank.wedatasphere.qualitis.bean.JobSubmitResult;
import com.webank.wedatasphere.qualitis.bean.RuleTaskDetail;
import com.webank.wedatasphere.qualitis.bean.TaskSubmitResult;
import com.webank.wedatasphere.qualitis.exception.ArgumentException;
import com.webank.wedatasphere.qualitis.submitter.ExecutionManager;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Component
public class ExecutionManagerImpl implements ExecutionManager {

    @Autowired
    private TemplateConverterFactory templateConverterFactory;

    @Autowired
    private AbstractJobSubmitter abstractJobSubmitter;

    @Autowired
    private ClusterInfoDao clusterInfoDao;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private TaskRuleSimpleRepository taskRuleSimpleRepository;

    @Autowired
    private TaskDataSourceRepository taskDataSourceRepository;

    @Autowired
    private TaskExecuteLimitConfig taskExecuteLimitConfig;

    public static final FastDateFormat PRINT_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionManagerImpl.class);

    /**
     * Submit job to linkis
     */
    @Override
    public List<TaskSubmitResult> submitApplication(String applicationId, List<Rule> rules, String createTime, String user, String database, String partition, Date date, Application application) throws ArgumentException,
            TaskTypeException, ConvertException, DataQualityTaskException, RuleVariableNotSupportException, RuleVariableNotFoundException, JobSubmitException, ClusterInfoNotConfigException, ExecutionException, InterruptedException {

        // Check if cluster supported
        LOGGER.info("Start to collect rule to clusters");
        Map<String, List<Rule>> clusterNameMap = getRuleCluster(rules);
        LOGGER.info("Succeed to classify rules by cluster, cluster map: {}", clusterNameMap);

        LOGGER.info("Start to check cluster config.");
        for (String clusterName : clusterNameMap.keySet()) {
            ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(clusterName);
            if (clusterInfo == null) {
                throw new ClusterInfoNotConfigException("Failed to find cluster: " + clusterName + " configuration");
            }
        }
        LOGGER.info("Succeed to pass the check of cluster config. All cluster of rules are configured");

        List<TaskSubmitResult> taskSubmitResults = new ArrayList<>();
        for (String clusterName : clusterNameMap.keySet()) {
            List<Rule> clusterRules = clusterNameMap.get(clusterName);
            ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(clusterName);

            // Divide rule into tasks
            List<DataQualityTask> tasks = TaskDividerFactory.getDivider().divide(clusterRules, applicationId, createTime, partition, date,
                    database, taskExecuteLimitConfig.getTaskExecuteRuleSize());
            LOGGER.info("Succeed to divide application into tasks. result: {}", tasks);

            // Save divided tasks
            saveDividedTask(tasks, clusterInfo, rules, application, createTime);

            // Convert tasks into job
            List<DataQualityJob> jobList = new ArrayList<>();
            for (DataQualityTask task : tasks) {
                DataQualityJob job = templateConverterFactory.getConverter(task).convert(task);
                jobList.add(job);
                List<Long> ruleIdList = task.getRuleTaskDetails().stream().map(r -> r.getRule().getId()).collect(Collectors.toList());
                LOGGER.info("Succeed to convert rule_id: {} into code. code: {}", ruleIdList, job.getJobCode());
            }
            LOGGER.info("Succeed to convert all template into codes. codes: {}", jobList);

            // Submit job to linkis
            List<JobSubmitResult> submitResults = new ArrayList<>();
            for (DataQualityJob job : jobList) {
                String code = String.join("\n", job.getJobCode());
                Long taskId = job.getTaskId();
                submitResults.add(abstractJobSubmitter.submitJob(code, user, clusterInfo.getLinkisAddress(), clusterName, taskId));
            }

            // rewrite task_remote_id
            for (JobSubmitResult jobSubmitResult : submitResults) {
                Task taskInDb = taskDao.findById(jobSubmitResult.getTaskId());
                taskInDb.setTaskRemoteId(jobSubmitResult.getTaskRemoteId());
                taskDao.save(taskInDb);
                taskSubmitResults.add(new TaskSubmitResult(application.getId(), jobSubmitResult.getTaskRemoteId(), clusterInfo.getClusterName()));
            }

        }

        return taskSubmitResults;
    }

    private void saveDividedTask(List<DataQualityTask> dataQualityTasks, ClusterInfo clusterInfo, List<Rule> rules, Application application, String createTime) {
        for (DataQualityTask dataQualityTask : dataQualityTasks) {
            List<TaskRule> ruleList = getRule(rules, dataQualityTask);
            Task task = new Task(application, createTime, TaskStatusEnum.SUBMITTED.getCode(), clusterInfo.getClusterName(), clusterInfo.getLinkisAddress());
            Task taskInDb = taskDao.save(task);
            LOGGER.info("Succeed to save task. task_id: {}", taskInDb.getId());
            saveJobRuleSimpleAndJobDataSource(ruleList, taskInDb);
            dataQualityTask.setTaskId(taskInDb.getId());
        }
    }

    /**
     * Save task rule simple and rule datasource
     * @param ruleList
     * @param task
     */
    private void saveJobRuleSimpleAndJobDataSource(List<TaskRule> ruleList, Task task) {
        for (TaskRule rule : ruleList) {
            if (rule.getChildRuleId() != null) {
                // Save parent task rule simple
                TaskRuleSimple parentRuleSimple = taskRuleSimpleRepository.save(new TaskRuleSimple(rule, task, true, null));
                // Save child task rule simple
                TaskRuleSimple childRuleSimple = taskRuleSimpleRepository.save(new TaskRuleSimple(rule, task, false, parentRuleSimple));
                task.getTaskRuleSimples().add(parentRuleSimple);
            } else {
                TaskRuleSimple taskRuleSimple = taskRuleSimpleRepository.save(new TaskRuleSimple(rule, task));
                LOGGER.info("Succeed to save task_rule: task_rule_id: {}, rule_name: {}", taskRuleSimple.getId(), taskRuleSimple.getRuleName());
                task.getTaskRuleSimples().add(taskRuleSimple);
            }
            for (TaskRuleDataSource ruleDataSource : rule.getTaskRuleDataSourceList()) {
                TaskDataSource taskDataSource = taskDataSourceRepository.save(new TaskDataSource(ruleDataSource, task));
                LOGGER.info("Succeed to save task_datasource: task_datasource_id: {}, cluster: {}, database: {}, table: {}",
                        taskDataSource.getId(), taskDataSource.getClusterName(), taskDataSource.getDatabaseName(), taskDataSource.getTableName());
                task.getTaskDataSources().add(taskDataSource);
            }
        }
    }

    /**
     * Get task rule
     * @param source
     * @param dataQualityTask
     * @return
     */
    private List<TaskRule> getRule(List<Rule> source, DataQualityTask dataQualityTask) {
        List<TaskRule> result = new ArrayList<>();
        for (RuleTaskDetail ruleTaskDetail : dataQualityTask.getRuleTaskDetails()) {
            TaskRule taskRule = new TaskRule();
            Rule rule = source.stream().filter(r -> r.getId().equals(ruleTaskDetail.getRule().getId())).collect(Collectors.toList()).get(0);
            taskRule.setRuleId(rule.getId());
            taskRule.setRuleType(rule.getRuleType());
            taskRule.setRuleName(rule.getName());
            taskRule.setMidTableName(ruleTaskDetail.getMidTableName());
            taskRule.setProjectId(ruleTaskDetail.getRule().getProject().getId());
            taskRule.setProjectName(ruleTaskDetail.getRule().getProject().getName());
            taskRule.setProjectCreator(ruleTaskDetail.getRule().getProject().getCreateUser());
            if (rule.getChildRule() != null) {
                taskRule.setChildRuleId(rule.getChildRule().getId());
                taskRule.setChildRuleType(rule.getChildRule().getRuleType());
                taskRule.setChildTaskRuleDataSourceList(getTaskRuleDataSourceBean(rule.getChildRule()));
                taskRule.setChildTaskRuleAlarmConfigsBeans(getTaskRuleAlarmConfigBean(rule.getChildRule()));
            }

            taskRule.setTaskRuleDataSourceList(getTaskRuleDataSourceBean(rule));
            taskRule.setTaskRuleAlarmConfigBeans(getTaskRuleAlarmConfigBean(rule));
            result.add(taskRule);
        }
        return result;
    }

    private List<TaskRuleAlarmConfigBean> getTaskRuleAlarmConfigBean(Rule rule) {
        List<TaskRuleAlarmConfigBean> taskRuleAlarmConfigBeans = new ArrayList<>();
        for (AlarmConfig alarmConfig : rule.getAlarmConfigs()) {
            TaskRuleAlarmConfigBean taskRuleAlarmConfigBean = new TaskRuleAlarmConfigBean();
            taskRuleAlarmConfigBean.setCheckTemplate(alarmConfig.getCheckTemplate());
            taskRuleAlarmConfigBean.setCompareType(alarmConfig.getCompareType());
            taskRuleAlarmConfigBean.setOutputName(alarmConfig.getTemplateOutputMeta().getOutputName());
            taskRuleAlarmConfigBean.setThreshold(alarmConfig.getThreshold());
            taskRuleAlarmConfigBeans.add(taskRuleAlarmConfigBean);
        }
        return taskRuleAlarmConfigBeans;
    }

    private List<TaskRuleDataSource> getTaskRuleDataSourceBean(Rule rule) {
        List<TaskRuleDataSource> taskRuleDataSources = new ArrayList<>();
        for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
            TaskRuleDataSource taskRuleDataSource = new TaskRuleDataSource();
            taskRuleDataSource.setClusterName(ruleDataSource.getClusterName());
            taskRuleDataSource.setDatabaseName(ruleDataSource.getDbName());
            taskRuleDataSource.setTableName(ruleDataSource.getTableName());
            taskRuleDataSource.setDatasourceIndex(ruleDataSource.getDatasourceIndex());
            taskRuleDataSource.setColName(ruleDataSource.getColName());
            taskRuleDataSource.setRuleId(rule.getId());
            taskRuleDataSources.add(taskRuleDataSource);
        }
        return taskRuleDataSources;
    }

    private Map<String, List<Rule>> getRuleCluster(List<Rule> rules) {
        Map<String, List<Rule>> clusterNameMap = new HashMap<>(8);
        for (Rule rule : rules) {
            Map<String, Integer> clusterCount = new HashMap<>(2);
            String maxClusterName = null;
            Integer maxClusterCount = 0;
            for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
                String clusterName = ruleDataSource.getClusterName();
                if (clusterCount.containsKey(clusterName)) {
                    clusterCount.put(clusterName, clusterCount.get(clusterName) + 1);
                } else {
                    clusterCount.put(clusterName, 1);
                }

                if (clusterCount.get(clusterName) > maxClusterCount) {
                    maxClusterCount = clusterCount.get(clusterName);
                    maxClusterName = clusterName;
                }
            }

            LOGGER.info("rule: rule_id: {}, rule_name: {} will be submit to cluster: {}", rule.getId(), rule.getName(), maxClusterName);
            if (!clusterNameMap.containsKey(maxClusterName)) {
                List<Rule> tmp = new ArrayList<>();
                tmp.add(rule);
                clusterNameMap.put(maxClusterName, tmp);
            } else {
                clusterNameMap.get(maxClusterName).add(rule);
            }
        }
        return clusterNameMap;
    }

}
