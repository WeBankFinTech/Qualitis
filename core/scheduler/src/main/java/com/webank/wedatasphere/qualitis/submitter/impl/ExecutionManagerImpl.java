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

import com.webank.wedatasphere.qualitis.EngineTypeEnum;
import com.webank.wedatasphere.qualitis.bean.DataQualityJob;
import com.webank.wedatasphere.qualitis.bean.DataQualityTask;
import com.webank.wedatasphere.qualitis.bean.JobKillResult;
import com.webank.wedatasphere.qualitis.bean.JobSubmitResult;
import com.webank.wedatasphere.qualitis.bean.RuleTaskDetail;
import com.webank.wedatasphere.qualitis.bean.TaskRule;
import com.webank.wedatasphere.qualitis.bean.TaskRuleAlarmConfigBean;
import com.webank.wedatasphere.qualitis.bean.TaskRuleDataSource;
import com.webank.wedatasphere.qualitis.bean.TaskSubmitResult;
import com.webank.wedatasphere.qualitis.checkalert.entity.CheckAlert;
import com.webank.wedatasphere.qualitis.client.AbstractJobSubmitter;
import com.webank.wedatasphere.qualitis.config.ImsConfig;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.config.TaskDataSourceConfig;
import com.webank.wedatasphere.qualitis.config.TaskExecuteLimitConfig;
import com.webank.wedatasphere.qualitis.constant.AlarmConfigStatusEnum;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constant.TaskStatusEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.converter.TemplateConverterFactory;
import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.dao.TaskResultDao;
import com.webank.wedatasphere.qualitis.dao.repository.TaskDataSourceRepository;
import com.webank.wedatasphere.qualitis.dao.repository.TaskRuleSimpleRepository;
import com.webank.wedatasphere.qualitis.divider.TaskDividerFactory;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.TaskDataSource;
import com.webank.wedatasphere.qualitis.entity.TaskResult;
import com.webank.wedatasphere.qualitis.entity.TaskRuleAlarmConfig;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.JobKillException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.response.table.PartitionStatisticsInfo;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableStatisticsInfo;
import com.webank.wedatasphere.qualitis.parser.LocaleParser;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.constant.FileOutputUnitEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateFileTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.ExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.util.UnitTransfer;
import com.webank.wedatasphere.qualitis.submitter.ExecutionManager;
import com.webank.wedatasphere.qualitis.util.DateExprReplaceUtil;
import com.webank.wedatasphere.qualitis.util.FilePassUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Component
public class ExecutionManagerImpl implements ExecutionManager {

    @Autowired
    private TemplateConverterFactory templateConverterFactory;

    @Autowired
    private ExecutionParametersDao executionParametersDao;

    @Autowired
    private AbstractJobSubmitter abstractJobSubmitter;

    @Autowired
    private ClusterInfoDao clusterInfoDao;

    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    private TaskResultDao taskResultDao;

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private TaskRuleSimpleRepository taskRuleSimpleRepository;

    @Autowired
    private TaskDataSourceRepository taskDataSourceRepository;

    @Autowired
    private TaskExecuteLimitConfig taskExecuteLimitConfig;

    @Autowired
    private TaskDataSourceConfig taskDataSourceConfig;

    @Autowired
    private MetaDataClient metaDataClient;

    @Autowired
    private LinkisConfig linkisConfig;

    @Autowired
    private ImsConfig imsConfig;
    @Autowired
    private LocaleParser localeParser;

    @Value("${execution.task.parallelNum:50}")
    private Integer parallelNum;

    private HttpServletRequest httpServletRequest;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionManagerImpl.class);

    public ExecutionManagerImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * Submit job to linkis     execParams(已经把页面表单和执行参数模板的--->执行变脸合并在一起了)
     */
    @Override
    public List<TaskSubmitResult> submitApplication(List<Rule> rules, String nodeName, String createTime, String user
            , Map<Long, Map<String, Object>> ruleReplaceInfo, StringBuilder partition, Date date, Application application, String cluster
            , String startupParam, String setFlag, Map<String, String> execParams, StringBuilder runDate, StringBuilder splitBy, Map<Long, List<Map<String, Object>>> dataSourceMysqlConnect
            , String tenantUserName, List<String> leftCols, List<String> rightCols, List<String> comelexCols, String createUser) throws Exception {

        String csId = rules.iterator().next().getCsId();
        // Check if cluster supported
        LOGGER.info("Start to collect rule to clusters");
        Map<String, List<Rule>> clusterNameMap = getRuleCluster(rules);
        LOGGER.info("Succeed to classify rules by cluster, cluster map: {}", clusterNameMap);
        if (StringUtils.isNotBlank(cluster)) {
            LOGGER.info("When pick up a cluster, these datasources of rules must be from one cluster. Now start to put into the specify cluster.\n");
            putAllRulesIntoSpecifyCluster(clusterNameMap, cluster);
            LOGGER.info("Success to put into the specify cluster.\n");
        }
        List<TaskSubmitResult> taskSubmitResults = new ArrayList<>();
        for (String clusterName : clusterNameMap.keySet()) {
            List<Rule> clusterRules = clusterNameMap.get(clusterName);

            if (clusterName.contains(SpecCharEnum.EQUAL.getValue())) {
                clusterName = clusterName.split(SpecCharEnum.EQUAL.getValue())[0];
            }
            if (StringUtils.isNotBlank(cluster)) {
                clusterName = cluster;
            }
            LOGGER.info("Start to check cluster config.");
            ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(clusterName);
            if (clusterInfo == null) {
                throw new ClusterInfoNotConfigException(clusterName + " {&DOES_NOT_EXIST}");
            }
            LOGGER.info("Succeed to pass the check of cluster config. All cluster of rules are configured");

            // Divide rule into tasks
            List<DataQualityTask> tasks = TaskDividerFactory.getDivider().divide(clusterRules, application.getId(), createTime, partition.toString()
                    , date, ruleReplaceInfo, dataSourceMysqlConnect, user, taskExecuteLimitConfig.getTaskExecuteRuleSize(), splitBy.toString(), startupParam);
            LOGGER.info("Succeed to divide application into tasks. result: {}", tasks);

            // Save divided tasks
            saveDividedTask(tasks, clusterInfo, rules, ruleReplaceInfo, application, createTime);

            // 处理执行变量包含spark.sql.前缀的  执行参数模板
            StringBuilder tmpExecParams = new StringBuilder();
            for (Map.Entry<String, String> map : execParams.entrySet()) {
                if (map.getKey().startsWith("spark.sql.")) {
                    tmpExecParams.append(map.getKey() + SpecCharEnum.EQUAL.getValue() + execParams.get(map.getKey())).append(SpecCharEnum.DIVIDER.getValue());
                }
            }

            // 合并 + 去重(OuterExecutionServiceImpl类ruleReplaceInfo方法，有对执行参数模板的ExecutionParam(执行参数)与页面录入执行参数做比较(相同key保证表单参数优先))
            StringBuilder lastSetFlag = new StringBuilder();
            if(StringUtils.isNotBlank(setFlag)){
                LOGGER.info("Set flag from http request entity: {}", setFlag);
                lastSetFlag.append(lastSetFlag != null && lastSetFlag.length() > 0 ? SpecCharEnum.DIVIDER.getValue() + setFlag : setFlag);
            }
            if(tmpExecParams != null && tmpExecParams.length() > 0){
                lastSetFlag.append(lastSetFlag != null && lastSetFlag.length() > 0 ? SpecCharEnum.DIVIDER.getValue() + tmpExecParams.deleteCharAt(tmpExecParams.length() - 1).toString() : tmpExecParams.deleteCharAt(tmpExecParams.length() - 1).toString());
            }

            // Convert tasks into job
            List<DataQualityJob> jobList = new ArrayList<>();
            for (DataQualityTask task : tasks) {
                DataQualityJob job = templateConverterFactory.getConverter(task).convert(task, date, lastSetFlag != null && lastSetFlag.length() > 0 ? lastSetFlag.toString() : setFlag, execParams, runDate.toString()
                        , clusterInfo.getClusterType(), dataSourceMysqlConnect, user, leftCols, rightCols, comelexCols, createUser);
                job.setUser(task.getUser());
                jobList.add(job);

                List<Long> ruleIdList = task.getRuleTaskDetails().stream().map(r -> r.getRule().getId()).collect(Collectors.toList());
                LOGGER.info("Succeed to convert rule id: {} into code. code: {}", ruleIdList, job.getJobCode());
            }
            LOGGER.info("Succeed to convert all template into codes. codes: {}", jobList);

            // Submit job to linkis
            List<JobSubmitResult> submitResults = new ArrayList<>();
            handleDataQualityJob(nodeName, user, application, csId, taskSubmitResults, clusterName, clusterInfo, jobList, submitResults, tenantUserName);

            // Rewrite task remote ID.
            rewriteTaskRemoteInfo(submitResults, taskSubmitResults, application.getId(), clusterInfo.getClusterName());
        }

        return taskSubmitResults;
    }

    @Override
    public GeneralResponse<Integer> killApplication(Application applicationInDb, String user)
        throws JobKillException, UnExpectedRequestException, ClusterInfoNotConfigException {
        List<Task> tasks = taskDao.findByApplication(applicationInDb);
        List<JobKillResult> results = new ArrayList<>();
        if (tasks == null || tasks.isEmpty()) {
            throw new UnExpectedRequestException("Sub tasks {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        for (Task task : tasks) {
            ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(task.getClusterName());
            if (clusterInfo == null) {
                throw new ClusterInfoNotConfigException("Failed to find cluster id: " + task.getClusterName() + " configuration");
            }
            results.add(abstractJobSubmitter.killJob(user, clusterInfo.getClusterName(), task));
            task.setStatus(TaskStatusEnum.CANCELLED.getCode());
            task.setEndTime(QualitisConstants.PRINT_TIME_FORMAT.format(new Date()));
            taskDao.save(task);
        }
        return new GeneralResponse<>("200", "{&SUCCESS_TO_KILL_TASK}", results.size());
    }

    private void handleDataQualityJob(String nodeName, String user, Application application, String csId,
                                      List<TaskSubmitResult> taskSubmitResults, String clusterName, ClusterInfo clusterInfo, List<DataQualityJob> jobList,
                                      List<JobSubmitResult> submitResults, String tenantUserName) throws Exception {
        LOGGER.info("If the result set is out of specification, it is split into groups of 50");
        // 1. 拆分原任务的一部分代码； 2. 生成 task 一系列信息； 3. 绑定提交结果（回写执行中间件任务信息）
        splitParalellTasks(jobList);
        for (DataQualityJob job : jobList) {
            int index = job.getJobCode().indexOf("Qualitis System Code Dividing Line");
            while (index != -1) {
                job.getJobCode().remove(index);
                index = job.getJobCode().indexOf("Qualitis System Code Dividing Line");
            }

            String code = String.join("\n", job.getJobCode());
            Long taskId = job.getTaskId();
            String proxy = job.getUser();

            JobSubmitResult result;
            if (EngineTypeEnum.SPARK_ENGINE.getMessage().equals(job.getEngineType())) {
                result = abstractJobSubmitter.submitJobNew(code, linkisConfig.getEngineName(), StringUtils.isNotBlank(proxy) ? proxy : user
                        , clusterInfo.getLinkisAddress(), clusterName, taskId, csId, nodeName, job.getStartupParam(), job.getEngineReuse(),
                        tenantUserName);
            } else if (EngineTypeEnum.DEFAULT_ENGINE.getMessage().equals(job.getEngineType())) {
                // Submit shell task
                result = abstractJobSubmitter.submitShellJobNew(code, linkisConfig.getEngineName(), StringUtils.isNotBlank(proxy) ? proxy : user
                        , clusterInfo.getLinkisAddress(), clusterName, taskId, job.getStartupParam(), tenantUserName);
            } else {
                throw new UnExpectedRequestException("Unknown engine type.");
            }
            if (result != null) {
                submitResults.add(result);
            } else {
                Task taskInDb = taskDao.findById(taskId);
                taskInDb.setStatus(TaskStatusEnum.TASK_NOT_EXIST.getCode());
                taskDao.save(taskInDb);
                taskSubmitResults.add(new TaskSubmitResult(application.getId(), null, clusterInfo.getClusterName()));
            }
        }
    }

    private void splitParalellTasks(List<DataQualityJob> jobList) {
        List<DataQualityJob> parallelJobList = new ArrayList<>();

        for (Iterator<DataQualityJob> iterator = jobList.iterator(); iterator.hasNext();) {
            DataQualityJob currentJob = iterator.next();
            if (currentJob.getResultNum() != null && currentJob.getResultNum() > parallelNum && currentJob.getJobCode().contains("Qualitis System Code Dividing Line")) {
                List<String> allSql = new ArrayList<>();
                for (String line : currentJob.getJobCode()) {
                    allSql.add(line);
                }

                int dividingLineFirstIndex = allSql.indexOf("Qualitis System Code Dividing Line");
                int dividingLineLastIndex = allSql.lastIndexOf("Qualitis System Code Dividing Line");
                int batch = currentJob.getResultNum() / parallelNum + 1;

                Task task = taskDao.findById(currentJob.getTaskId());

                for (int index = 0; index < batch; index ++) {
                    // Copy task
                    Task parallelTask = new Task();
                    BeanUtils.copyProperties(task, parallelTask);

                    parallelTask.setId(null);
                    parallelTask.setTaskRuleSimples(null);
                    parallelTask.setTaskDataSources(null);
                    Task savedParallelTask = taskDao.save(parallelTask);

                    Set<TaskRuleSimple> taskRuleSimples = new HashSet<>();
                    for (TaskRuleSimple taskRuleSimple : task.getTaskRuleSimples()) {
                        TaskRuleSimple tmpTaskRuleSimple = new TaskRuleSimple();
                        BeanUtils.copyProperties(taskRuleSimple, tmpTaskRuleSimple);

                        tmpTaskRuleSimple.setId(null);
                        tmpTaskRuleSimple.setTask(savedParallelTask);
                        tmpTaskRuleSimple.setTaskRuleAlarmConfigList(null);

                        List<TaskRuleAlarmConfig> taskRuleAlarmConfigList = new ArrayList<>();
                        for (TaskRuleAlarmConfig taskRuleAlarmConfig : taskRuleSimple.getTaskRuleAlarmConfigList()) {
                            TaskRuleAlarmConfig tmpTaskRuleAlarmConfig = new TaskRuleAlarmConfig();
                            BeanUtils.copyProperties(taskRuleAlarmConfig, tmpTaskRuleAlarmConfig);

                            tmpTaskRuleAlarmConfig.setId(null);
                            tmpTaskRuleAlarmConfig.setTaskRuleSimple(tmpTaskRuleSimple);

                            taskRuleAlarmConfigList.add(tmpTaskRuleAlarmConfig);
                        }

                        tmpTaskRuleSimple.setTaskRuleAlarmConfigList(taskRuleAlarmConfigList);
                        taskRuleSimples.add(tmpTaskRuleSimple);
                    }
                    savedParallelTask.setTaskRuleSimples(taskRuleSimpleRepository.saveAll(taskRuleSimples).stream().collect(Collectors.toSet()));

                    Set<TaskDataSource> taskDataSources = new HashSet<>();
                    for (TaskDataSource taskDataSource : task.getTaskDataSources()) {
                        TaskDataSource tmptaskDataSource = new TaskDataSource();
                        BeanUtils.copyProperties(taskDataSource, tmptaskDataSource);

                        tmptaskDataSource.setId(null);
                        tmptaskDataSource.setTask(savedParallelTask);

                        taskDataSources.add(tmptaskDataSource);
                    }
                    savedParallelTask.setTaskDataSources(taskDataSourceRepository.saveAll(taskDataSources).stream().collect(Collectors.toSet()));
                    // Copy job code
                    DataQualityJob parallelJob = new DataQualityJob();
                    // Clear
                    currentJob.setJobCode(new ArrayList<>(dividingLineFirstIndex));

                    BeanUtils.copyProperties(currentJob, parallelJob);
                    parallelJob.setTaskId(savedParallelTask.getId());

                    parallelJob.getJobCode().addAll(allSql.subList(0, dividingLineFirstIndex));

                    int start = dividingLineFirstIndex + 1 + index * parallelNum * 4;
                    int end = dividingLineLastIndex > dividingLineFirstIndex + 1 + (index + 1) * parallelNum * 4 ? dividingLineFirstIndex + 1 + (index + 1) * parallelNum * 4 : dividingLineLastIndex;

                    parallelJob.getJobCode().addAll(allSql.subList(start, end));

                    int startSave = dividingLineLastIndex + 1 + index * parallelNum;
                    int endSave = allSql.size() > dividingLineLastIndex + 1 + (index + 1) * parallelNum ? dividingLineLastIndex + 1 + (index + 1) * parallelNum : allSql.size();

                    parallelJob.getJobCode().addAll(allSql.subList(startSave, endSave));
                    parallelJobList.add(parallelJob);
                }
                taskDao.delete(task);
                iterator.remove();
            }
        }
        jobList.addAll(parallelJobList);
    }

    private void rewriteTaskRemoteInfo(List<JobSubmitResult> submitResults, List<TaskSubmitResult> taskSubmitResults, String id, String clusterName) {
        for (JobSubmitResult jobSubmitResult : submitResults) {
            Task taskInDb = taskDao.findById(jobSubmitResult.getTaskId());
            taskInDb.setTaskRemoteId(jobSubmitResult.getTaskRemoteId());
            taskInDb.setTaskExecId(jobSubmitResult.getTaskExecId());
            taskDao.save(taskInDb);
            taskSubmitResults.add(new TaskSubmitResult(id, jobSubmitResult.getTaskRemoteId(), clusterName));
        }
    }

    private void putAllRulesIntoSpecifyCluster(Map<String, List<Rule>> clusterNameMap, String cluster) {
        List<Rule> allRules = new ArrayList<>();
        for (List<Rule> ruleList : clusterNameMap.values()) {
            allRules.addAll(ruleList);
        }
        clusterNameMap.clear();
        clusterNameMap.put(cluster, allRules);
    }

    @Override
    public TaskSubmitResult executeFileRule(List<Rule> fileRules, String submitTime, Application application, String user, String clusterName
        , StringBuilder runDate, Map<Long, Map<String, Object>> ruleReplaceInfo) throws Exception {
        LOGGER.info("Start to execute file rule task and save check result.");
        Task taskInDb = taskDao.save(new Task(application, submitTime, TaskStatusEnum.SUBMITTED.getCode()));
        Set<TaskDataSource> taskDataSources = new HashSet<>(fileRules.size());
        Set<TaskRuleSimple> taskRuleSimples = new HashSet<>(fileRules.size());
        int totalRules = fileRules.size();
        int successRule = 0;
        for (Rule rule : fileRules) {
            if (rule.getAbortOnFailure() != null) {
                taskInDb.setAbortOnFailure(rule.getAbortOnFailure());
            } else {
                taskInDb.setAbortOnFailure(Boolean.FALSE);
            }
            TaskRuleSimple taskRuleSimple = new TaskRuleSimple(rule, taskInDb, ruleReplaceInfo, true);
            taskRuleSimples.add(taskRuleSimpleRepository.save(taskRuleSimple));

            RuleDataSource ruleDataSource = rule.getRuleDataSources().iterator().next();
            taskDataSources.add(taskDataSourceRepository.save(new TaskDataSource(ruleDataSource, taskInDb)));

            // Check rule datasource: 1) table 2) partition.
            if (StringUtils.isEmpty(ruleDataSource.getFilter()) || "true".equals(ruleDataSource.getFilter())) {
                TableStatisticsInfo result;
                try {
                    String proxyUser = ruleDataSource.getProxyUser();
                    result = metaDataClient.getTableStatisticsInfo(StringUtils.isNotBlank(clusterName) ? clusterName : ruleDataSource.getClusterName()
                            , ruleDataSource.getDbName(), ruleDataSource.getTableName(), StringUtils.isNotBlank(proxyUser) ? proxyUser : user);
                } catch (RestClientException e) {
                    LOGGER.error("Failed to get table statistics with linkis api.", e);
                    throw new UnExpectedRequestException("{&FAILED_TO_GET_DATASOURCE_INFO}");
                }

                if (result == null) {
                    throw new UnExpectedRequestException("{&FAILED_TO_GET_DATASOURCE_INFO}");
                }

                String fullSize = result.getTableSize();
                int partitionNum = result.getPartitions() == null ? 0 : result.getPartitions().size();
                List<TaskResult> taskResultInDbs = saveTaskRusult(fullSize, Integer.parseInt(partitionNum + ""), application, submitTime, rule, rule.getAlarmConfigs(), runDate.toString());
                successRule = modifyTaskStatus(taskRuleSimple.getTaskRuleAlarmConfigList(), taskInDb, taskResultInDbs, successRule);
            } else {
                PartitionStatisticsInfo result;
                try {
                    String proxyUser = ruleDataSource.getProxyUser();
                    result = metaDataClient.getPartitionStatisticsInfo(StringUtils.isNotBlank(clusterName) ? clusterName : ruleDataSource.getClusterName()
                            , ruleDataSource.getDbName(), ruleDataSource.getTableName(), filterToPartitionPath(DateExprReplaceUtil.replaceFilter(new Date(), ruleDataSource.getFilter())), StringUtils.isNotBlank(proxyUser) ? proxyUser : user);
                } catch (RestClientException e) {
                    LOGGER.error("Failed to get table statistics with linkis api.", e);
                    throw new UnExpectedRequestException("{&FAILED_TO_GET_DATASOURCE_INFO}");
                }

                if (result == null) {
                    throw new UnExpectedRequestException("{&FAILED_TO_GET_DATASOURCE_INFO}");
                }

                String fullSize = result.getPartitionSize();
                List<TaskResult> taskResultInDbs = saveTaskRusult(fullSize, 0, application, submitTime
                        , rule, rule.getAlarmConfigs(), runDate.toString());
                successRule = modifyTaskStatus(taskRuleSimple.getTaskRuleAlarmConfigList(), taskInDb, taskResultInDbs, successRule);
            }

            if (taskInDb.getStatus().equals(TaskStatusEnum.FAILED.getCode())) {
                break;
            }
        }
        taskInDb.setTaskDataSources(taskDataSources);
        taskInDb.setTaskRuleSimples(taskRuleSimples);
        // Update task status
        taskInDb.setEndTime(QualitisConstants.PRINT_TIME_FORMAT.format(new Date()));
        if (totalRules == successRule) {
            taskInDb.setStatus(TaskStatusEnum.PASS_CHECKOUT.getCode());
            taskInDb.setProgress(Double.parseDouble("1"));
        }
        taskDao.save(taskInDb);
        LOGGER.info("Finished to execute file rule task and save check result.");
        TaskSubmitResult taskSubmitResult = new TaskSubmitResult();
        taskSubmitResult.setApplicationId(application.getId());
        taskSubmitResult.setClusterName(clusterName);
        return taskSubmitResult;
    }

    @Override
    public List<TaskSubmitResult> executeFileRuleWithShell(List<Rule> fileRules, String submitTime, Application application, String user, String clusterName
        , String runDate, Map<Long, Map<String, Object>> ruleReplaceInfo, String startupParam, Boolean engineReuse, String engineType) throws Exception {

        List<TaskSubmitResult> taskSubmitResults = new ArrayList<>(fileRules.size());

        for (Rule rule : fileRules) {
            DataQualityJob job = new DataQualityJob();
            LOGGER.info("Start to execute file rule {} with shell.", rule.getName());

            RuleDataSource ruleDataSource = rule.getRuleDataSources().iterator().next();
            ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(StringUtils.isNotEmpty(clusterName) ? clusterName : ruleDataSource.getClusterName());
            Task taskInDb = taskDao.save(new Task(application, submitTime, TaskStatusEnum.SUBMITTED.getCode(), Double.parseDouble("0"), clusterInfo.getClusterName(), clusterInfo.getLinkisAddress()));

            Set<TaskDataSource> taskDataSources = new HashSet<>(QualitisConstants.ONLY_ONE_GROUP);
            taskDataSources.add(taskDataSourceRepository.save(new TaskDataSource(ruleDataSource, taskInDb)));

            if (StringUtils.isEmpty(runDate)) {
                runDate = "-1";
            }

            // In history, rule's abort property is useful.
            if (rule.getAbortOnFailure() == null) {
                taskInDb.setAbortOnFailure(Boolean.FALSE);
            } else {
                taskInDb.setAbortOnFailure(rule.getAbortOnFailure());
            }

            Set<TaskRuleSimple> taskRuleSimples = new HashSet<>(QualitisConstants.ONLY_ONE_GROUP);

            TaskRuleSimple taskRuleSimple = new TaskRuleSimple(rule, taskInDb, ruleReplaceInfo, true);
            taskRuleSimples.add(taskRuleSimpleRepository.save(taskRuleSimple));

            taskInDb.setTaskDataSources(taskDataSources);
            taskInDb.setTaskRuleSimples(taskRuleSimples);
            Task savedTask = taskDao.save(taskInDb);

            List<String> jobCodes = job.getJobCode();
            LOGGER.info("Start to convert file rule into actual shell code.");
            jobCodes.add("hive_query=\"describe formatted " + ruleDataSource.getDbName() + SpecCharEnum.PERIOD_NO_ESCAPE.getValue()+ ruleDataSource.getTableName() + ";\"");
            if (StringUtils.isNotEmpty(ruleDataSource.getFilter())) {
                jobCodes.add("partition_location=\"" + SpecCharEnum.SLASH.getValue() + ruleDataSource.getFilter() + "\"");
            } else {
                jobCodes.add("partition_location=\"\"");
            }

            jobCodes.add("hive_location=$(hive -e \"${hive_query}\" | grep \"^Location:\" | awk -F'\\t' '{print $2}' | tr -d '[:space:]')");
            jobCodes.add("count_value=$(hdfs dfs -count ${hive_location}${partition_location} | awk '{print $2}')");
            jobCodes.add("MYSQL=\"" + taskDataSourceConfig.getMysqlsec() + "\"");
            String ruleVersion = rule.getWorkFlowVersion() == null ? "" : rule.getWorkFlowVersion();
            List<Long> ruleMetricIds = rule.getAlarmConfigs().stream().filter(alarmConfig -> alarmConfig.getRuleMetric() != null).map(alarmConfig -> alarmConfig.getRuleMetric().getId()).collect(Collectors.toList());
            String currentRuleMetricId = "-1";
            if (CollectionUtils.isNotEmpty(ruleMetricIds)) {
                currentRuleMetricId = ruleMetricIds.iterator().next().toString();
            }
            jobCodes.add("sql" + "=\"INSERT INTO qualitis_application_task_result (application_id, create_time, result_type, rule_id, value, rule_metric_id, run_date, version) VALUES('" + application.getId() + "', STR_TO_DATE(DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s'), '%Y-%m-%d %H:%i:%s'), 'Long', " + rule.getId() + ", $count_value" + ", " + currentRuleMetricId + ", " + runDate.toString() + ", '" + ruleVersion + "');\"");
            jobCodes.add("result=\"$($MYSQL -e\"$sql" + "\")\"");
            String code = String.join("\n", job.getJobCode());
            LOGGER.info("Finished to convert file rule into actual shell code.");

            job.setTaskId(savedTask.getId());
            job.setStartupParam(startupParam);
            job.setEngineReuse(engineReuse);
            job.setEngineType(engineType);

            JobSubmitResult result = abstractJobSubmitter.submitShellJobNew(code, linkisConfig.getEngineName(), application.getExecuteUser(), clusterInfo.getLinkisAddress(), clusterInfo.getClusterName(), savedTask.getId(), job.getStartupParam(), application.getTenantUserName());

            if (result != null) {
                savedTask.setTaskRemoteId(result.getTaskRemoteId());
                savedTask.setTaskExecId(result.getTaskExecId());
                taskDao.save(savedTask);

                taskSubmitResults.add(new TaskSubmitResult(application.getId(), result.getTaskRemoteId(), clusterInfo.getClusterName()));
            } else {
                savedTask.setStatus(TaskStatusEnum.TASK_NOT_EXIST.getCode());
                taskDao.save(savedTask);

                taskSubmitResults.add(new TaskSubmitResult(application.getId(), null, clusterInfo.getClusterName()));
            }

            LOGGER.info("Finished to execute file rule {} with shell.", rule.getName());
        }

        return taskSubmitResults;
    }

    @Override
    public TaskSubmitResult executeCheckAlert(ClusterInfo clusterInfo, String[] dbAndTables, List<String> columns, CheckAlert currentCheckAlert
        , Application saveApplication, String startupParam, Boolean engineReuse, String engineType) throws Exception {

        Task task = new Task(saveApplication, saveApplication.getSubmitTime(), TaskStatusEnum.SUBMITTED.getCode(), Double.parseDouble("0")
            , clusterInfo.getClusterName(), clusterInfo.getLinkisAddress());

        Task savedTask = taskDao.save(task);
        LOGGER.info("Succeed to save task. Task ID: {}", savedTask.getId());

        Set<TaskDataSource> taskDataSources = new HashSet<>();
        Set<TaskRuleSimple> taskRuleSimples = new HashSet<>();

        taskRuleSimples.add(taskRuleSimpleRepository.save(new TaskRuleSimple(currentCheckAlert, savedTask)));
        taskDataSources.add(taskDataSourceRepository.save(new TaskDataSource(currentCheckAlert, savedTask)));

        savedTask.setTaskDataSources(taskDataSources);
        savedTask.setTaskRuleSimples(taskRuleSimples);

        TaskResult taskResult = new TaskResult();
        taskResult.setCreateTime(saveApplication.getSubmitTime());
        taskResult.setApplicationId(saveApplication.getId());
        taskResult.setRuleId(currentCheckAlert.getId());
        taskResultDao.saveTaskResult(taskResult);

        taskDao.save(savedTask);

        DataQualityJob job = new DataQualityJob();

        List<String> jobCodes = job.getJobCode();

        LOGGER.info("Start to convert check alert into actual scala code.");

        List<String> selectPart = new ArrayList<>();
        List<String> contentTitle = new ArrayList<>();

        if (StringUtils.isNotEmpty(currentCheckAlert.getContentCols())) {
            String[] selectColStrs = currentCheckAlert.getContentCols().split(SpecCharEnum.DIVIDER.getValue());

            for (String selectColStr : selectColStrs) {
                String[] colAndAlias = selectColStr.split(SpecCharEnum.COLON.getValue());
                selectPart.add(colAndAlias[0] + " AS " + colAndAlias[1]);
                contentTitle.add(colAndAlias[1]);
            }
        } else {
            for(String tmpCol : columns.subList(QualitisConstants.COMMON_ARRAY_INDEX_O, columns.size() <= QualitisConstants.DEFAULT_CONTENT_COLUMN_LENGTH ? columns.size() : QualitisConstants.DEFAULT_CONTENT_COLUMN_LENGTH)) {
                selectPart.add(tmpCol);
                contentTitle.add(tmpCol);
            }
        }

        jobCodes.add("import sys.process._");
        jobCodes.add("import scala.util.parsing.json._");

        String realContent = "\"" + linkisConfig.getCheckAlertTemplate().replace("qualitis_check_alert_topic", currentCheckAlert.getTopic()).replace("qualitis_check_alert_time", saveApplication.getSubmitTime()).replace("qualitis_check_alert_project_info", currentCheckAlert.getProject().getName() + SpecCharEnum.COLON.getValue() + currentCheckAlert.getWorkFlowName() + SpecCharEnum.COLON.getValue() + currentCheckAlert.getNodeName()) + "\"" ;

        jobCodes.add("var alertContent = " + realContent);
        String filter = StringUtils.isNotEmpty(currentCheckAlert.getFilter()) ? currentCheckAlert.getFilter() : "true";

        filter = DateExprReplaceUtil.replaceFilter(new Date(), filter);
        jobCodes.add("val alertTableArr = spark.sql(\"SELECT " + StringUtils.join(selectPart, SpecCharEnum.COMMA.getValue())
            + " FROM " + currentCheckAlert.getAlertTable() + " WHERE " + currentCheckAlert.getAlertCol() + " = 1 AND " + filter + "\").collect()");
        jobCodes.add("var alertTableContent = \"\"");
        jobCodes.add("for(ele <- alertTableArr) {alertTableContent = alertTableContent.concat(ele.mkString(\" | \")).concat(\"\\n\")}");
        jobCodes.add("if (alertTableContent.length > 0) {");
        jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_info_content\", alertTableContent)");
        jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_info_receiver\", \"" + currentCheckAlert.getInfoReceiver() + "\")");
        jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_info_title\", \"" + StringUtils.join(contentTitle, "|") + "\")");
        jobCodes.add("} else {");
        jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_info_content\", \"\")");
        jobCodes.add("alertContent = alertContent.replaceAll(\"\\\\[告警人\\\\] qualitis_check_alert_info_receiver\", \"\")");

        jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_info_title\", \"\")");
        jobCodes.add("}");

        if (StringUtils.isNotEmpty(currentCheckAlert.getMajorAlertCol())) {
            jobCodes.add("val majorAlertTableArr = spark.sql(\"SELECT " + StringUtils.join(selectPart, SpecCharEnum.COMMA.getValue())
                + " FROM " + currentCheckAlert.getAlertTable() + " WHERE " + currentCheckAlert.getMajorAlertCol() + " = 1 AND " + filter + "\").collect()");
            jobCodes.add("var majorAlertTableContent = \"\"");
            jobCodes.add("for(ele <- majorAlertTableArr) {majorAlertTableContent = majorAlertTableContent.concat(ele.mkString(\" | \")).concat(\"\\n\")}");
            jobCodes.add("if (majorAlertTableContent.length > 0) {");
            jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_major_content\", majorAlertTableContent)");
            jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_major_receiver\", \"" + (StringUtils.isNotEmpty(currentCheckAlert.getMajorReceiver()) ? currentCheckAlert.getMajorReceiver() : "") + "\")");
            jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_major_title\", \"" + StringUtils.join(contentTitle, "|") + "\")");
            // CURL IMS
            if (StringUtils.isNotEmpty(currentCheckAlert.getMajorReceiver())) {
                jobCodes.add("val jsonMajorValue = " + "\"{\\\"userAuthKey\\\":\\\"" + imsConfig.getUserAuthKey() + "\\\",\\\"alertList\\\":[{\\\"alert_way\\\":\\\"" + imsConfig.getAlertWay() + "\\\",\\\"alert_title\\\":\\\"Qualitis Check Alert\\\",\\\"ci_type_id\\\":55,\\\"sub_system_id\\\":" + imsConfig.getSystemId() +",\\\"alert_reciver\\\":\\\"" + currentCheckAlert.getMajorReceiver() + "\\\",\\\"alert_level\\\":2,\\\"alert_obj\\\":\\\"" + dbAndTables[0] + "[" + dbAndTables[1] + "]" + "\\\",\\\"alert_info\\\":\\\"qualitis_check_alert_Major\\\"}]}\"");
                jobCodes.add("val realJsonMajorValue = jsonMajorValue.replaceAll(\"qualitis_check_alert_Major\", alertContent)");
                jobCodes.add("val jsonMajorCmd = Seq(\"curl\", \"-H\", \"'Content-Type: application/json'\",\"-d\", s\"$realJsonMajorValue\",\"" + imsConfig.getUrl() + imsConfig.getSendAlarmPath() + "\")");
                jobCodes.add("val majorResponse = jsonMajorCmd.!!");
                jobCodes.add("val code = JSON.parseFull(majorResponse).get.asInstanceOf[Map[String, Object]].get(\"resultCode\").get.toString");
                jobCodes.add("if (! \"0.0\".equals(code)) throw new RuntimeException(\"Failed to send ims alarm. Return non-zero code from IMS.\")");
            }
            jobCodes.add("} else {");
            jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_major_content\", \"\")");
            jobCodes.add("alertContent = alertContent.replaceAll(\"\\\\[高等级告警人\\\\] qualitis_check_alert_major_receiver\", \"\")");

            jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_major_title\", \"\")");
            jobCodes.add("}");
        } else {
            jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_major_content\", \"\")");
            jobCodes.add("alertContent = alertContent.replaceAll(\"\\\\[高等级告警人\\\\] qualitis_check_alert_major_receiver\", \"\")");

            jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_major_title\", \"\")");
        }
        // CURL IMS
        jobCodes.add("if (alertTableContent.length > 0) {");
        jobCodes.add("val jsonInfoValue = " + "\"{\\\"userAuthKey\\\":\\\"" + imsConfig.getUserAuthKey() + "\\\",\\\"alertList\\\":[{\\\"alert_way\\\":\\\"" + imsConfig.getAlertWay() + "\\\",\\\"alert_title\\\":\\\"Qualitis Check Alert\\\",\\\"ci_type_id\\\":55,\\\"sub_system_id\\\":" + imsConfig.getSystemId() + ",\\\"alert_reciver\\\":\\\"" + currentCheckAlert.getInfoReceiver() + "\\\",\\\"alert_level\\\":5,\\\"alert_obj\\\":\\\"" + dbAndTables[0] + "[" + dbAndTables[1] + "]" + "\\\",\\\"alert_info\\\":\\\"qualitis_check_alert_info\\\"}]}\"");
        jobCodes.add("val realJsonInfoValue = jsonInfoValue.replaceAll(\"qualitis_check_alert_info\", alertContent)");
        jobCodes.add("val jsonInfoCmd = Seq(\"curl\", \"-H\", \"'Content-Type: application/json'\",\"-d\", s\"$realJsonInfoValue\",\"" + imsConfig.getUrl() + imsConfig.getSendAlarmPath() + "\")");
        jobCodes.add("val infoResponse = jsonInfoCmd.!!");
        jobCodes.add("val code = JSON.parseFull(infoResponse).get.asInstanceOf[Map[String, Object]].get(\"resultCode\").get.toString");
        jobCodes.add("if (! \"0.0\".equals(code)) throw new RuntimeException(\"Failed to send ims alarm. Return non-zero code from IMS.\")");
        jobCodes.add("}");


        String code = String.join("\n", job.getJobCode());
        LOGGER.info("Succeed to convert check alert into actual scala code.");

        job.setTaskId(savedTask.getId());
        job.setStartupParam(startupParam);
        job.setEngineReuse(engineReuse);
        job.setEngineType(engineType);

        JobSubmitResult result = abstractJobSubmitter.submitJobNew(code, linkisConfig.getEngineName(), saveApplication.getExecuteUser()
            , clusterInfo.getLinkisAddress(), clusterInfo.getClusterName(), savedTask.getId(), "", "", job.getStartupParam(), job.getEngineReuse()
            , saveApplication.getTenantUserName());

        if (result != null) {
            savedTask.setTaskRemoteId(result.getTaskRemoteId());
            savedTask.setTaskExecId(result.getTaskExecId());
            taskDao.save(savedTask);
            return new TaskSubmitResult(saveApplication.getId(), result.getTaskRemoteId(), clusterInfo.getClusterName());
        } else {
            savedTask.setStatus(TaskStatusEnum.TASK_NOT_EXIST.getCode());
            taskDao.save(savedTask);
            return new TaskSubmitResult(saveApplication.getId(), null, clusterInfo.getClusterName());
        }

    }

    private List<TaskResult> saveTaskRusult(String fullSize, int partitionsNum, Application application, String submitTime, Rule rule,
                                            Set<AlarmConfig> alarmConfig, String runDate) throws UnExpectedRequestException {
        double number = Double.parseDouble(fullSize.split(" ")[0]);
        String unit = fullSize.split(" ")[1];
        // Save task result.
        List<TaskResult> taskResults = new ArrayList<>();

        AlarmConfig currentAlarmConfig = alarmConfig.stream().iterator().next();

        RuleMetric ruleMetric = currentAlarmConfig.getRuleMetric();
        if (ruleMetric == null) {
            throw new UnExpectedRequestException("File rule metric {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        TaskResult taskResult;
        if (StringUtils.isNotBlank(runDate)) {
            TaskResult existTaskResult = taskResultDao.find(runDate, rule.getId(), ruleMetric.getId());

            if (existTaskResult != null) {
                taskResult = existTaskResult;
            } else {
                taskResult = new TaskResult();
                Date runRealDate = null;
                try {
                    runRealDate = new SimpleDateFormat("yyyyMMdd").parse(runDate);
                } catch (ParseException e) {
                    String errorMsg = "Parse date string with run date failed. Exception message: " + e.getMessage();
                    LOGGER.error(errorMsg);
                    throw new UnExpectedRequestException(errorMsg);
                }
                taskResult.setRunDate(runRealDate.getTime());
            }
        } else {
            taskResult = new TaskResult();
        }
        taskResult.setApplicationId(application.getId());
        taskResult.setCreateTime(submitTime);
        taskResult.setRuleId(rule.getId());
        taskResult.setSaveResult(true);
        taskResult.setRuleMetricId(ruleMetric.getId());

        if (currentAlarmConfig.getTemplateOutputMeta().getOutputName().equals(TemplateFileTypeEnum.DIRECTORY_CAPACITY.getMessage())) {
            String alarmConfigUnit = FileOutputUnitEnum.MB.getMessage();

            if (currentAlarmConfig.getFileOutputUnit() != null) {
                alarmConfigUnit = FileOutputUnitEnum.fileOutputUnit(currentAlarmConfig.getFileOutputUnit());
            }

            taskResult.setResultType(alarmConfigUnit);
            taskResult.setValue(UnitTransfer.alarmconfigToTaskResult(number, alarmConfigUnit, unit.toUpperCase()) + "");
        } else if (currentAlarmConfig.getTemplateOutputMeta().getOutputName().equals(TemplateFileTypeEnum.NUMBER_PARTITIONS.getMessage())) {
            taskResult.setResultType("int");
            taskResult.setValue(partitionsNum + "");
        }
        taskResults.add(taskResultDao.saveTaskResult(taskResult));

        return taskResults;
    }

    private int modifyTaskStatus(List<TaskRuleAlarmConfig> alarmConfigs, Task taskInDb, List<TaskResult> taskResults, int successRule) {
        boolean rulePass = true;
        for (TaskRuleAlarmConfig alarmConfig : alarmConfigs) {
            TaskResult taskResult = taskResults.stream().filter(taskResultInDb -> taskResultInDb.getRuleMetricId().equals(alarmConfig.getRuleMetric().getId())).iterator().next();
            if (!FilePassUtil.pass(alarmConfig, taskResult, taskResultDao)) {
                rulePass = false;
                alarmConfig.setStatus(AlarmConfigStatusEnum.NOT_PASS.getCode());
                if (alarmConfig.getDeleteFailCheckResult() != null && alarmConfig.getDeleteFailCheckResult()) {
                    taskResult.setSaveResult(false);
                    taskResultDao.saveTaskResult(taskResult);
                } else {
                    taskResult.setSaveResult(true);
                    taskResultDao.saveTaskResult(taskResult);
                }
            } else {
                taskResult.setSaveResult(true);
                taskResultDao.saveTaskResult(taskResult);
                alarmConfig.setStatus(AlarmConfigStatusEnum.PASS.getCode());
            }
        }
        if (rulePass) {
            successRule++;
        } else if (taskInDb.getAbortOnFailure() != null && taskInDb.getAbortOnFailure()) {
            taskInDb.setStatus(TaskStatusEnum.FAILED.getCode());
        } else {
            taskInDb.setStatus(TaskStatusEnum.FAIL_CHECKOUT.getCode());
            taskInDb.setProgress(Double.parseDouble("1"));
        }
        return successRule;
    }

    private void saveDividedTask(List<DataQualityTask> dataQualityTasks, ClusterInfo clusterInfo, List<Rule> rules, Map<Long, Map<String, Object>> rulePlaceInfo
            , Application application, String createTime) {

        for (DataQualityTask dataQualityTask : dataQualityTasks) {
            List<TaskRule> ruleList = getRule(rules, rulePlaceInfo, dataQualityTask);
            Task task = new Task(application, createTime, TaskStatusEnum.SUBMITTED.getCode(), Double.parseDouble("0"), clusterInfo.getClusterName(), clusterInfo.getLinkisAddress());
            Boolean abortOnFailure = Boolean.FALSE;
            for (Rule rule : rules) {
                if (rulePlaceInfo.get(rule.getId()) == null) {
                    continue;
                }
                if (rulePlaceInfo.get(rule.getId()).get("union_all_save") != null) {
                    rule.setUnionAll((Boolean) rulePlaceInfo.get(rule.getId()).get("union_all_save"));
                }
                if (Boolean.FALSE.equals(abortOnFailure) && rulePlaceInfo.get(rule.getId()).get("qualitis_abort_on_failure") != null) {
                    abortOnFailure = (Boolean) rulePlaceInfo.get(rule.getId()).get("qualitis_abort_on_failure");
                }
            }
            task.setAbortOnFailure(abortOnFailure);
            task.setTaskProxyUser(dataQualityTask.getUser());

            Task taskInDb = taskDao.save(task);
            LOGGER.info("Succeed to save task. Task ID: {}", taskInDb.getId());
            saveJobRuleSimpleAndJobDataSource(ruleList, taskInDb, rulePlaceInfo);
            dataQualityTask.setTaskId(taskInDb.getId());
        }
    }

    /**
     * Save task rule simple and rule datasource
     *
     * @param ruleList
     * @param task
     * @param rulePlaceInfo
     */
    private void saveJobRuleSimpleAndJobDataSource(List<TaskRule> ruleList, Task task, Map<Long, Map<String, Object>> rulePlaceInfo) {
        for (TaskRule rule : ruleList) {
            TaskRuleSimple taskRuleSimple = taskRuleSimpleRepository.save(new TaskRuleSimple(rule, task, rulePlaceInfo));
            LOGGER.info("Succeed to save task_rule: task rule id: {}, rule_name: {}", taskRuleSimple.getId(), taskRuleSimple.getRuleName());
            task.getTaskRuleSimples().add(taskRuleSimple);
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
     *
     * @param source
     * @param ruleReplaceInfo
     * @param dataQualityTask
     * @return
     */
    private List<TaskRule> getRule(List<Rule> source, Map<Long, Map<String, Object>> ruleReplaceInfo, DataQualityTask dataQualityTask) {
        List<TaskRule> result = new ArrayList<>();
        for (RuleTaskDetail ruleTaskDetail : dataQualityTask.getRuleTaskDetails()) {
            TaskRule taskRule = new TaskRule();
            Rule rule = source.stream().filter(r -> r.getId().equals(ruleTaskDetail.getRule().getId())).collect(Collectors.toList()).get(0);
            taskRule.setRuleId(rule.getId());
            taskRule.setRuleName(rule.getName());
            taskRule.setCnName(rule.getCnName());
            taskRule.setRuleType(rule.getRuleType());
            taskRule.setRuleDetail(rule.getDetail());
            taskRule.setTemplateName(rule.getTemplate().getName());
            taskRule.setMidTableName(ruleTaskDetail.getMidTableName());
            taskRule.setRuleGroupName(rule.getRuleGroup().getRuleGroupName());
            taskRule.setProjectId(ruleTaskDetail.getRule().getProject().getId());
            taskRule.setProjectName(ruleTaskDetail.getRule().getProject().getName());
            taskRule.setProjectCnName(ruleTaskDetail.getRule().getProject().getCnName());
            taskRule.setProjectCreator(ruleTaskDetail.getRule().getProject().getCreateUser());
            if (ruleReplaceInfo.get(rule.getId()) != null && ruleReplaceInfo.get(rule.getId()).keySet().contains(QualitisConstants.QUALITIS_ALERT_LEVEL)) {
                taskRule.setAlertReceiver((String) ruleReplaceInfo.get(rule.getId()).get(QualitisConstants.QUALITIS_ALERT_RECEIVERS));
                taskRule.setAlertLevel((Integer) ruleReplaceInfo.get(rule.getId()).get(QualitisConstants.QUALITIS_ALERT_LEVEL));
            } else if (rule.getAlert() != null && rule.getAlert()) {
                taskRule.setAlertReceiver(rule.getAlertReceiver());
                taskRule.setAlertLevel(rule.getAlertLevel());
            }
            taskRule.setTaskRuleAlarmConfigBeans(getTaskRuleAlarmConfigBean(rule, ruleReplaceInfo));
            taskRule.setTaskRuleDataSourceList(getTaskRuleDataSourceBean(rule));
            taskRule.setDeleteFailCheckResult(rule.getDeleteFailCheckResult());
            result.add(taskRule);
        }
        return result;
    }

    private List<TaskRuleAlarmConfigBean> getTaskRuleAlarmConfigBean(Rule rule, Map<Long, Map<String, Object>> ruleReplaceInfo) {
        List<TaskRuleAlarmConfigBean> taskRuleAlarmConfigBeans = new ArrayList<>();
        for (AlarmConfig alarmConfig : rule.getAlarmConfigs()) {
            TaskRuleAlarmConfigBean taskRuleAlarmConfigBean = new TaskRuleAlarmConfigBean();
            taskRuleAlarmConfigBean.setCheckTemplate(alarmConfig.getCheckTemplate());
            taskRuleAlarmConfigBean.setCompareType(alarmConfig.getCompareType());
            taskRuleAlarmConfigBean.setThreshold(alarmConfig.getThreshold());
            taskRuleAlarmConfigBean.setRuleMetric(alarmConfig.getRuleMetric());
            taskRuleAlarmConfigBean.setOutputName(alarmConfig.getTemplateOutputMeta().getOutputName());
            if (ruleReplaceInfo.get(rule.getId()) != null && ruleReplaceInfo.get(rule.getId()).keySet().contains(QualitisConstants.QUALITIS_UPLOAD_ABNORMAL_VALUE)) {
                taskRuleAlarmConfigBean.setUploadAbnormalValue((Boolean) ruleReplaceInfo.get(rule.getId()).get(QualitisConstants.QUALITIS_UPLOAD_ABNORMAL_VALUE));
            } else {
                taskRuleAlarmConfigBean.setUploadAbnormalValue(alarmConfig.getUploadAbnormalValue());
            }
            if (ruleReplaceInfo.get(rule.getId()) != null && ruleReplaceInfo.get(rule.getId()).keySet().contains(QualitisConstants.QUALITIS_UPLOAD_RULE_METRIC_VALUE)) {
                taskRuleAlarmConfigBean.setUploadRuleMetricValue((Boolean) ruleReplaceInfo.get(rule.getId()).get(QualitisConstants.QUALITIS_UPLOAD_RULE_METRIC_VALUE));
            } else {
                taskRuleAlarmConfigBean.setUploadRuleMetricValue(alarmConfig.getUploadRuleMetricValue());
            }
            if (ruleReplaceInfo.get(rule.getId()) != null && ruleReplaceInfo.get(rule.getId()).keySet().contains(QualitisConstants.QUALITIS_DELETE_FAIL_CHECK_RESULT)) {
                taskRuleAlarmConfigBean.setDeleteFailCheckResult((Boolean) ruleReplaceInfo.get(rule.getId()).get(QualitisConstants.QUALITIS_DELETE_FAIL_CHECK_RESULT));
            } else {
                taskRuleAlarmConfigBean.setDeleteFailCheckResult(alarmConfig.getDeleteFailCheckResult());
            }
            taskRuleAlarmConfigBeans.add(taskRuleAlarmConfigBean);
        }
        return taskRuleAlarmConfigBeans;
    }

    private List<TaskRuleDataSource> getTaskRuleDataSourceBean(Rule rule) {
        List<TaskRuleDataSource> taskRuleDataSources = new ArrayList<>();
        for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
            if (StringUtils.isBlank(ruleDataSource.getTableName())) {
                continue;
            }
            TaskRuleDataSource taskRuleDataSource = new TaskRuleDataSource();
            taskRuleDataSource.setClusterName(ruleDataSource.getClusterName());
            taskRuleDataSource.setDatabaseName(ruleDataSource.getDbName());
            taskRuleDataSource.setTableName(ruleDataSource.getTableName());
            taskRuleDataSource.setSubSystemId(ruleDataSource.getSubSystemId());
            taskRuleDataSource.setDatasourceType(ruleDataSource.getDatasourceType());
            taskRuleDataSource.setDatasourceIndex(ruleDataSource.getDatasourceIndex());
            taskRuleDataSource.setColName(ruleDataSource.getColName());
            taskRuleDataSource.setProjectId(rule.getProject().getId());
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
                if (StringUtils.isBlank(ruleDataSource.getTableName())) {
                    continue;
                }
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

            LOGGER.info("Rule: id: {}, name: {} will be submit to cluster: {}", rule.getId(), rule.getName(), maxClusterName);
            if (StringUtils.isNotEmpty(rule.getExecutionParametersName())) {
                maxClusterName = maxClusterName + SpecCharEnum.EQUAL.getValue() + rule.getExecutionParametersName();
            }
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

    private String filterToPartitionPath(String filter) {
        List<String> partitionPath = new ArrayList<>();
        String[] conditions = filter.split(QualitisConstants.AND);
        for (String condition : conditions) {
            condition = condition.trim().replace("'", "");
            partitionPath.add("/" + condition);
        }
        return StringUtils.join(partitionPath.toArray());
    }

}
