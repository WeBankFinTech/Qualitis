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

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.webank.wedatasphere.qualitis.EngineTypeEnum;
import com.webank.wedatasphere.qualitis.bean.*;
import com.webank.wedatasphere.qualitis.checkalert.entity.CheckAlert;
import com.webank.wedatasphere.qualitis.client.AbstractJobSubmitter;
import com.webank.wedatasphere.qualitis.config.ImsConfig;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.config.TaskDataSourceConfig;
import com.webank.wedatasphere.qualitis.config.TaskExecuteLimitConfig;
import com.webank.wedatasphere.qualitis.constant.*;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.converter.TemplateConverterFactory;
import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.dao.TaskResultDao;
import com.webank.wedatasphere.qualitis.dao.repository.TaskDataSourceRepository;
import com.webank.wedatasphere.qualitis.dao.repository.TaskRuleSimpleRepository;
import com.webank.wedatasphere.qualitis.divider.TaskDividerFactory;
import com.webank.wedatasphere.qualitis.entity.*;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.JobKillException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserColumnByCsRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserTableByCsIdRequest;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.CsTableInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.PartitionStatisticsInfo;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableStatisticsInfo;
import com.webank.wedatasphere.qualitis.parser.LocaleParser;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.constant.FileOutputUnitEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateFileTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.ExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDatasourceEnvDao;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceEnv;
import com.webank.wedatasphere.qualitis.rule.util.UnitTransfer;
import com.webank.wedatasphere.qualitis.scheduled.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.submitter.ExecutionManager;
import com.webank.wedatasphere.qualitis.util.DateExprReplaceUtil;
import com.webank.wedatasphere.qualitis.util.FilePassUtil;
import com.webank.wedatasphere.qualitis.util.map.CustomObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Sets;
import org.jetbrains.annotations.NotNull;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
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
    @Autowired
    private RuleDatasourceEnvDao ruleDatasourceEnvDao;

    @Value("${execution.task.parallelNum:50}")
    private Integer parallelNum;

    @Value("${intellect.check.project_name}")
    private String intellectCheckProjectName;

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
            , String startupParam, String setFlag, Map<String, String> execParams, StringBuilder runDate, StringBuilder runToday, StringBuilder splitBy, Map<Long, List<Map<String, Object>>> dataSourceMysqlConnect
            , String tenantUserName, List<String> leftCols, List<String> rightCols, List<String> comelexCols, String createUser) throws Exception {

        String csId = null;
        List<Rule> rulesWithCsId = rules.stream().filter(rule -> StringUtils.isNotEmpty(rule.getCsId())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(rulesWithCsId)) {
            csId = rulesWithCsId.iterator().next().getCsId();
        }
        // Check if cluster supported
        LOGGER.info("Start to collect rule to clusters");
        Map<String, List<Rule>> clusterNameMap = getRuleCluster(rules, application);
        LOGGER.info("Succeed to classify rules by cluster, cluster map: {}", clusterNameMap);
        if (StringUtils.isNotBlank(cluster)) {
            LOGGER.info("When pick up a cluster, these datasources of rules must be from one cluster. Now start to put into the specify cluster.\n");
            putAllRulesIntoSpecifyCluster(clusterNameMap, cluster);
            LOGGER.info("Success to put into the specify cluster.\n");
        }
        List<TaskSubmitResult> taskSubmitResults = new ArrayList<>();
        for (String clusterName : clusterNameMap.keySet()) {
            Integer datasourceIndex = null;
            List<Rule> clusterRules = clusterNameMap.get(clusterName);
            if (clusterName.contains(SpecCharEnum.EQUAL.getValue())) {
                clusterName = clusterName.split(SpecCharEnum.EQUAL.getValue())[0];
            }
            if (clusterName.contains(SpecCharEnum.PERIOD_NO_ESCAPE.getValue())) {
                String[] infos = clusterName.split(SpecCharEnum.PERIOD.getValue());
                datasourceIndex = Integer.valueOf(infos[1]);
                clusterName = infos[0];
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
            List<DataQualityTask> tasks = TaskDividerFactory.getDivider().divide(clusterInfo.getClusterName(), datasourceIndex, clusterRules, application.getId(), createTime, partition.toString()
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
            if (StringUtils.isNotBlank(setFlag)) {
                LOGGER.info("Set flag from http request entity: {}", setFlag);
                lastSetFlag.append(lastSetFlag != null && lastSetFlag.length() > 0 ? SpecCharEnum.DIVIDER.getValue() + setFlag : setFlag);
            }
            if (tmpExecParams != null && tmpExecParams.length() > 0) {
                lastSetFlag.append(lastSetFlag != null && lastSetFlag.length() > 0 ? SpecCharEnum.DIVIDER.getValue() + tmpExecParams.deleteCharAt(tmpExecParams.length() - 1).toString() : tmpExecParams.deleteCharAt(tmpExecParams.length() - 1).toString());
            }

            // Convert tasks into job
            List<DataQualityJob> jobList = new ArrayList<>();
            for (DataQualityTask task : tasks) {
                DataQualityJob job = templateConverterFactory.getConverter(task).convert(task, date, lastSetFlag != null && lastSetFlag.length() > 0 ? lastSetFlag.toString() : setFlag, execParams, runDate.toString()
                        , runToday.toString(), clusterInfo.getClusterType(), dataSourceMysqlConnect, user, leftCols, rightCols, comelexCols, createUser, application.getProjectId());
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
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&SUCCESS_TO_KILL_TASK}", results.size());
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
                        , clusterInfo.getLinkisAddress(), clusterName, taskId, job.getStartupParam(), job.getEngineReuse() == null ? true : job.getEngineReuse().booleanValue(), tenantUserName);
            }  else if (EngineTypeEnum.TRINO_ENGINE.getMessage().equals(job.getEngineType())) {
                // Submit trino task
                result = abstractJobSubmitter.submitTrinoJobNew(code, linkisConfig.getEngineName(), StringUtils.isNotBlank(proxy) ? proxy : user
                        , clusterInfo.getLinkisAddress(), clusterName, taskId, job.getStartupParam(), job.getEngineReuse() == null ? true : job.getEngineReuse().booleanValue(), tenantUserName);
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

        for (Iterator<DataQualityJob> iterator = jobList.iterator(); iterator.hasNext(); ) {
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

                for (int index = 0; index < batch; index++) {
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
        Map<String, List<Rule>> clusterNameNewMap = new HashMap<>(clusterNameMap.keySet().size());
        for (String clusterName : clusterNameMap.keySet()) {
            if (clusterName.contains(SpecCharEnum.PERIOD_NO_ESCAPE.getValue())) {
                String realClusterName = clusterName.split(SpecCharEnum.PERIOD.getValue())[0];
                List<Rule> rules = clusterNameMap.get(clusterName);
                clusterName = clusterName.replaceFirst(realClusterName, cluster);

                clusterNameNewMap.put(clusterName, rules);
            } else {
                allRules.addAll(clusterNameMap.get(clusterName));
            }

        }
        clusterNameMap.clear();
        if (CollectionUtils.isNotEmpty(allRules)) {
            clusterNameMap.put(cluster, allRules);
        }
        clusterNameMap.putAll(clusterNameNewMap);
    }

    @Override
    public TaskSubmitResult executeFileRule(List<Rule> fileRules, String submitTime, Application application, String user, String clusterName
            , StringBuilder runDate, StringBuilder runToday, Map<Long, Map<String, Object>> ruleReplaceInfo) throws Exception {
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
                List<TaskResult> taskResultInDbs = saveTaskRusult(fullSize, Integer.parseInt(partitionNum + ""), application, submitTime, rule, rule.getAlarmConfigs(), runDate.toString(), runToday.toString());
                successRule = modifyTaskStatus(taskRuleSimple.getTaskRuleAlarmConfigList(), taskInDb, taskResultInDbs, successRule);
            } else {
                PartitionStatisticsInfo result;
                try {
                    String proxyUser = ruleDataSource.getProxyUser();
                    String filter = ruleDataSource.getFilter();
                    if (StringUtils.isNotBlank(runDate.toString())) {
                        filter = filter.replace("${run_date}", runDate);
//                        filter = filter.replace("${run_date_std}", runDate);
                    }
                    if (StringUtils.isNotBlank(runToday.toString())) {
                        filter = filter.replace("${run_today}", runDate);
//                        filter = filter.replace("${run_today_std}", runDate);
                    }
                    result = metaDataClient.getPartitionStatisticsInfo(StringUtils.isNotBlank(clusterName) ? clusterName : ruleDataSource.getClusterName()
                            , ruleDataSource.getDbName(), ruleDataSource.getTableName(), filterToPartitionPath(DateExprReplaceUtil.replaceFilter(new Date(), filter)), StringUtils.isNotBlank(proxyUser) ? proxyUser : user);
                } catch (RestClientException e) {
                    LOGGER.error("Failed to get table statistics with linkis api.", e);
                    throw new UnExpectedRequestException("{&FAILED_TO_GET_DATASOURCE_INFO}");
                }

                if (result == null) {
                    throw new UnExpectedRequestException("{&FAILED_TO_GET_DATASOURCE_INFO}");
                }

                String fullSize = result.getPartitionSize();
                List<TaskResult> taskResultInDbs = saveTaskRusult(fullSize, 0, application, submitTime
                        , rule, rule.getAlarmConfigs(), runDate.toString(), runToday.toString());
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
    public TaskSubmitResult executeTableStructureRule(List<Rule> tableStructures, String submitTime, Application application, String user,
                                                      String clusterName, StringBuilder runDate, StringBuilder runToday, Map<Long, Map<String, Object>> ruleReplaceInfo, String nodeName) throws Exception {
        LOGGER.info("Start to execute table structure rule task and save check result.");
        Task taskInDb = taskDao.save(new Task(application, submitTime, TaskStatusEnum.SUBMITTED.getCode()));
        Set<TaskDataSource> taskDataSources = new HashSet<>(tableStructures.size());
        Set<TaskRuleSimple> taskRuleSimples = new HashSet<>(tableStructures.size());
        int totalRules = tableStructures.size();
        int successRule = 0;
        for (Rule rule : tableStructures) {
            if (rule.getAbortOnFailure() != null) {
                taskInDb.setAbortOnFailure(rule.getAbortOnFailure());
            } else {
                taskInDb.setAbortOnFailure(Boolean.FALSE);
            }

            for (Map.Entry<Long, Map<String, Object>> entry : ruleReplaceInfo.entrySet()) {
                Long key = entry.getKey();
                Map<String, Object> value = entry.getValue();
                if (key.equals(rule.getId()) && value.containsKey(QualitisConstants.QUALITIS_UNION_WAY)) {
                    rule.setUnionWay(MapUtils.getInteger(value, QualitisConstants.QUALITIS_UNION_WAY));
                }
            }

            TaskRuleSimple taskRuleSimple = new TaskRuleSimple(rule, taskInDb, ruleReplaceInfo, false);
            taskRuleSimples.add(taskRuleSimpleRepository.save(taskRuleSimple));
            String hiveTemp = null;
            List<String> leftMultipleDataSource = Lists.newArrayList();
            List<String> rightMultipleDataSource = Lists.newArrayList();
            List<TaskResult> taskResultInDbs = Lists.newArrayList();

            for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
                // check ruleDataSourceEnvs is it unfilled
                if (CollectionUtils.isEmpty(ruleDataSource.getRuleDataSourceEnvs())) {
                    List<RuleDataSourceEnv> ruleDataSourceEnvs = ruleDatasourceEnvDao.findByDataSourceId(ruleDataSource.getId());
                    if (CollectionUtils.isNotEmpty(ruleDataSourceEnvs)) {
                        ruleDataSource.setRuleDataSourceEnvs(ruleDataSourceEnvs);
                    }
                }

                taskDataSources.add(taskDataSourceRepository.save(new TaskDataSource(ruleDataSource, taskInDb)));
                ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(StringUtils.isNotBlank(clusterName) ? clusterName : ruleDataSource.getClusterName());
                if (clusterInfo == null) {
                    throw new ClusterInfoNotConfigException(clusterName + " {&DOES_NOT_EXIST}");
                }

                if (CollectionUtils.isEmpty(ruleDataSource.getRuleDataSourceEnvs())) {
                    List<ColumnInfoDetail> columnInfo;
                    //hive
                    if (StringUtils.isNotBlank(rule.getCsId())) {
                        columnInfo = checkDatasourceInContext(ruleDataSource, clusterName, user, nodeName, rule.getCsId());
                        if (CollectionUtils.isEmpty(columnInfo)) {
                            throw new UnExpectedRequestException("cs table info cannot find context service the same rule data source table!");
                        }
                    } else {
                        columnInfo = metaDataClient.getColumnInfo(clusterInfo.getClusterName(), ruleDataSource.getDbName(),
                                ruleDataSource.getTableName(), StringUtils.isNotBlank(ruleDataSource.getProxyUser()) ? ruleDataSource.getProxyUser() : user);
                    }

                    if (columnInfo == null || CollectionUtils.isEmpty(columnInfo)) {
                        throw new UnExpectedRequestException("Table[" + ruleDataSource.getTableName() + "] {&RULE_DATASOURCE_BE_MOVED}");
                    }
                    if (!metaDataClient.fieldExist(ruleDataSource.getColName(), columnInfo, null)) {
                        throw new UnExpectedRequestException("Table[" + ruleDataSource.getTableName() + ":" + ruleDataSource.getColName() + "] {&RULE_DATASOURCE_BE_MOVED}");
                    }

                    //先以属性一升序,再进行属性二降序
                    List<ColumnInfoDetail> resultColumnInfo = columnInfo.stream().sorted(Comparator.comparing(ColumnInfoDetail::getFieldName).
                            thenComparing(ColumnInfoDetail::getDataType, Comparator.reverseOrder())).collect(Collectors.toList());

                    String montage = resultColumnInfo.stream().map(e -> e.getFieldName())
                            .collect(Collectors.joining());
                    if (StringUtils.isBlank(hiveTemp)) {
                        hiveTemp = montage;
                    } else {
                        taskResultInDbs = handleTaskResult(application, submitTime, rule, rule.getAlarmConfigs(), runDate.toString(), runToday.toString(), hiveTemp.equals(montage) ? 0 + "" : 1 + "");
                    }
                } else {
                    //mysql tdql 多环境下，是否需要多次校验表结构是否存在
                    for (RuleDataSourceEnv ruleDataSourceEnv : ruleDataSource.getRuleDataSourceEnvs()) {
                        DataInfo<ColumnInfoDetail> cols = metaDataClient.getColumnsByDataSourceName(linkisConfig.getDatasourceCluster(),
                                linkisConfig.getDatasourceAdmin(), ruleDataSource.getLinkisDataSourceName(), ruleDataSource.getDbName(), ruleDataSource.getTableName(), ruleDataSourceEnv.getEnvId());

                        String montage = getSplicingTables(ruleDataSource, cols);
                        //left data source
                        leftDataSource(leftMultipleDataSource, ruleDataSource, montage);
                        //right data source
                        rightDataSource(rightMultipleDataSource, ruleDataSource, montage, 1);
                    }

                }
            }

            //TODO 跨源 表结构比对  例如：tdsql varchar  hive  String
            //1.字段名称
            //2.类型匹配

            // 1.左表： hive  右表：hive  result默认赋值
            //   左表： hive   右表：mysql  tdsql
            // 2.左表：mysql,tdsql  右表：hive
            //   左表： mysql,tdsql   右表：mysql  tdsql
            if (StringUtils.isNotBlank(hiveTemp) && CollectionUtils.isEmpty(leftMultipleDataSource)
                    && CollectionUtils.isEmpty(rightMultipleDataSource)) {
                LOGGER.info("Both the left and right table data source types are hive.");
            } else if (StringUtils.isNotBlank(hiveTemp) && CollectionUtils.isNotEmpty(rightMultipleDataSource)) {
                taskResultInDbs = handleAggregationSwitch(submitTime, application, runDate, runToday, rule, hiveTemp, rightMultipleDataSource);
            } else if (CollectionUtils.isNotEmpty(leftMultipleDataSource) && StringUtils.isNotBlank(hiveTemp)) {
                taskResultInDbs = handleAggregationSwitch(submitTime, application, runDate, runToday, rule, hiveTemp, leftMultipleDataSource);
            } else if (CollectionUtils.isNotEmpty(leftMultipleDataSource) && CollectionUtils.isNotEmpty(rightMultipleDataSource)) {
                List<Integer> anotherTempResult = Lists.newArrayList();
                List<TaskResult> taskResultList = Lists.newArrayList();
                if (UnionWayEnum.CALCULATE_AFTER_COLLECT.getCode().equals(rule.getUnionWay())) {
                    int leftSize = leftMultipleDataSource.stream().collect(Collectors.toSet()).size();
                    int rightSize = rightMultipleDataSource.stream().collect(Collectors.toSet()).size();
                    boolean left = leftSize > 1 ? true : false;
                    boolean right = rightSize > 1 ? true : false;
                    if (left || right) {
                        taskResultList.addAll(handleTaskResult(application, submitTime, rule, rule.getAlarmConfigs(), runDate.toString(), runToday.toString(), 1 + ""));
                    }

                    if (1 == leftSize && 1 == rightSize) {
                        Optional<String> leftOptional = leftMultipleDataSource.stream().collect(Collectors.toSet()).stream().findFirst();
                        Optional<String> rightOptional = rightMultipleDataSource.stream().collect(Collectors.toSet()).stream().findFirst();
                        String leftGetResult = leftOptional.isPresent() ? leftOptional.get() : "";
                        String rightGetResult = rightOptional.isPresent() ? rightOptional.get() : "";

                        String lastResult = leftGetResult.equals(rightGetResult) ? 0 + "" : 1 + "";
                        taskResultList.addAll(handleTaskResult(application, submitTime, rule, rule.getAlarmConfigs(), runDate.toString(), runToday.toString(), lastResult));
                    }
                } else {
                    for (String left : leftMultipleDataSource) {
                        comparisonTableIsNotConsistent(submitTime, application, runDate, runToday, rule, rightMultipleDataSource, anotherTempResult, taskResultList, left);
                    }
                    if (CollectionUtils.isNotEmpty(anotherTempResult)) {
                        Integer additive = anotherTempResult.stream().reduce(Integer::sum).orElse(0);
                        taskResultList.addAll(handleTaskResult(application, submitTime, rule, rule.getAlarmConfigs(), runDate.toString(), runToday.toString(), additive.toString()));
                    }
                }
                taskResultInDbs.addAll(taskResultList);
            }

            successRule = modifyTaskStatus(taskRuleSimple.getTaskRuleAlarmConfigList(), taskInDb, taskResultInDbs, successRule);

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
        LOGGER.info("Finished to execute table structure rule task and save check result.");
        TaskSubmitResult taskSubmitResult = new TaskSubmitResult();
        taskSubmitResult.setApplicationId(application.getId());
        taskSubmitResult.setClusterName(clusterName);
        return taskSubmitResult;
    }

    private void comparisonTableIsNotConsistent(String submitTime, Application application, StringBuilder runDate, StringBuilder runToday, Rule rule, List<String> rightMultipleDataSource, List<Integer> anotherTempResult, List<TaskResult> taskResultList, String left) throws UnExpectedRequestException {
        Set<String> collectResult = Sets.newHashSet();
        for (String right : rightMultipleDataSource) {
            String temp = left.equals(right) ? 0 + "" : 1 + "";
            if (UnionWayEnum.NO_COLLECT_CALCULATE.getCode().equals(rule.getUnionWay())) {
                taskResultList.addAll(handleTaskResult(application, submitTime, rule, rule.getAlarmConfigs(), runDate.toString(), runToday.toString(), temp));
            } else if (UnionWayEnum.COLLECT_AFTER_CALCULATE.getCode().equals(rule.getUnionWay())) {
                anotherTempResult.add(Integer.parseInt(temp));
            } else if (UnionWayEnum.CALCULATE_AFTER_COLLECT.getCode().equals(rule.getUnionWay())) {
                collectResult.add(right);
            }
        }

        // 先聚合再计算
        if (CollectionUtils.isNotEmpty(collectResult)) {
            String result = collectResult.stream().anyMatch(item -> !item.equals(left)) ? 1 + "" : 0 + "";
            taskResultList.addAll(handleTaskResult(application, submitTime, rule, rule.getAlarmConfigs(), runDate.toString(), runToday.toString(), result));
        }

    }

    private List<TaskResult> handleAggregationSwitch(String submitTime, Application application, StringBuilder runDate, StringBuilder runToday, Rule rule, String hiveTemp, List<String> rightMultipleDataSource) throws UnExpectedRequestException {
        List<Integer> tempResult = Lists.newArrayList();
        List<TaskResult> taskResults = Lists.newArrayList();
        comparisonTableIsNotConsistent(submitTime, application, runDate, runToday, rule, rightMultipleDataSource, tempResult, taskResults, hiveTemp);
        if (CollectionUtils.isNotEmpty(tempResult)) {
            Integer additive = tempResult.stream().reduce(Integer::sum).orElse(0);
            taskResults.addAll(handleTaskResult(application, submitTime, rule, rule.getAlarmConfigs(), runDate.toString(), runToday.toString(), additive.toString()));
        }
        return taskResults;
    }

    private List<ColumnInfoDetail> checkDatasourceInContext(RuleDataSource ruleDataSource, String clusterName, String userName
            , String nodeName, String csId) throws Exception {
        List<ColumnInfoDetail> columnInfos = Lists.newArrayList();
        GetUserTableByCsIdRequest getUserTableByCsIdRequest = new GetUserTableByCsIdRequest();
        getUserTableByCsIdRequest.setClusterName(StringUtils.isNotBlank(clusterName) ? clusterName : ruleDataSource.getClusterName());
        getUserTableByCsIdRequest.setLoginUser(userName);
        getUserTableByCsIdRequest.setCsId(csId);
        if (StringUtils.isBlank(nodeName)) {
            getUserTableByCsIdRequest.setNodeName(ruleDataSource.getRule().getNodeName());
        } else {
            getUserTableByCsIdRequest.setNodeName(nodeName);
        }
        DataInfo<CsTableInfoDetail> csTableInfoDetails;
        try {
            csTableInfoDetails = metaDataClient.getTableByCsId(getUserTableByCsIdRequest);
        } catch (Exception e) {
            throw new UnExpectedRequestException("Table[" + ruleDataSource.getTableName() + "] {&RULE_DATASOURCE_BE_MOVED}");
        }
        if (csTableInfoDetails.getTotalCount() == 0) {
            LOGGER.info("Cannot find context service table with existed rules!");
            throw new UnExpectedRequestException("Table[" + ruleDataSource.getTableName() + "] {&RULE_DATASOURCE_BE_MOVED}");
        }
        for (CsTableInfoDetail csTableInfoDetail : csTableInfoDetails.getContent()) {
            if (csTableInfoDetail.getTableName().equals(ruleDataSource.getTableName())) {
                GetUserColumnByCsRequest getUserColumnByCsRequest = new GetUserColumnByCsRequest();
                getUserColumnByCsRequest.setClusterName(StringUtils.isNotBlank(clusterName) ? clusterName : ruleDataSource.getClusterName());
                getUserColumnByCsRequest.setContextKey(csTableInfoDetail.getContextKey());
                getUserColumnByCsRequest.setLoginUser(userName);
                getUserColumnByCsRequest.setCsId(csId);
                List<ColumnInfoDetail> columnInfo = metaDataClient.getColumnByCsId(getUserColumnByCsRequest).getContent();
                if (ruleDataSource.getRule().getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
                    continue;
                }
                columnInfos.addAll(columnInfo);
            }
        }
        return columnInfos;
    }

    @NotNull
    private String getSplicingTables(RuleDataSource ruleDataSource, DataInfo<ColumnInfoDetail> cols) throws UnExpectedRequestException {
        if (cols == null || CollectionUtils.isEmpty(cols.getContent())) {
            throw new UnExpectedRequestException("Table[" + ruleDataSource.getTableName() + "] {&RULE_DATASOURCE_BE_MOVED}");
        }
        if (!metaDataClient.fieldExist(ruleDataSource.getColName(), cols.getContent(), null)) {
            throw new UnExpectedRequestException("Table[" + ruleDataSource.getTableName() + "] {&RULE_DATASOURCE_BE_MOVED}");
        }
        //先以属性一升序,再进行属性二降序
        List<ColumnInfoDetail> resultColumnInfo = cols.getContent().stream().sorted(Comparator.comparing(ColumnInfoDetail::getFieldName).
                thenComparing(ColumnInfoDetail::getDataType, Comparator.reverseOrder())).collect(Collectors.toList());

        return resultColumnInfo.stream().map(e -> e.getFieldName())
                .collect(Collectors.joining());
    }

    private void rightDataSource(List<String> multipleDataSource, RuleDataSource ruleDataSource, String montage, int i) {
        if (null != ruleDataSource.getDatasourceIndex() && ruleDataSource.getDatasourceIndex().equals(i)) {
            multipleDataSource.add(montage);
        }
    }

    private void leftDataSource(List<String> leftMultipleDataSource, RuleDataSource ruleDataSource, String montage) {
        rightDataSource(leftMultipleDataSource, ruleDataSource, montage, 0);
    }

    @Override
    public List<TaskSubmitResult> executeFileRuleWithShell(List<Rule> fileRules, String submitTime, Application application, String user, String clusterName
            , String runDate, String runToday, Map<Long, Map<String, Object>> ruleReplaceInfo, String startupParam, Boolean engineReuse, String engineType) throws Exception {

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
                if (StringUtils.isNotBlank(runToday)) {
                    runDate = runToday;
                    Date runRealDate = handleTimeFormatting(runDate);
                    runDate = runRealDate.getTime() + "";
                } else {
                    runDate = "-1";
                }
            } else {
                Date runRealDate = handleTimeFormatting(runDate);
                runDate = runRealDate.getTime() + "";
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
            jobCodes.add("hive_query=\"describe formatted " + ruleDataSource.getDbName() + SpecCharEnum.PERIOD_NO_ESCAPE.getValue() + ruleDataSource.getTableName() + ";\"");
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

            JobSubmitResult result = abstractJobSubmitter.submitShellJobNew(code, linkisConfig.getEngineName(), application.getExecuteUser(), clusterInfo.getLinkisAddress(), clusterInfo.getClusterName(), savedTask.getId(), job.getStartupParam(), job.getEngineReuse() == null ? true : job.getEngineReuse().booleanValue(), application.getTenantUserName());

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

    private Date handleTimeFormatting(String runDate) throws UnExpectedRequestException {
        Date runRealDate = null;
        try {
            if (runDate.contains(SpecCharEnum.MINUS.getValue())) {
                runRealDate = new SimpleDateFormat("yyyy-MM-dd").parse(runDate);
            } else {
                runRealDate = new SimpleDateFormat("yyyyMMdd").parse(runDate);
            }
        } catch (ParseException e) {
            String errorMsg = "Parse date string with run date failed. Exception message: " + e.getMessage();
            LOGGER.error(errorMsg);
            throw new UnExpectedRequestException(errorMsg);
        }
        return runRealDate;
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
            for (String tmpCol : columns.subList(QualitisConstants.COMMON_ARRAY_INDEX_O, columns.size() <= QualitisConstants.DEFAULT_CONTENT_COLUMN_LENGTH ? columns.size() : QualitisConstants.DEFAULT_CONTENT_COLUMN_LENGTH)) {
                selectPart.add(tmpCol);
                contentTitle.add(tmpCol);
            }
        }

        jobCodes.add("import sys.process._");
        jobCodes.add("import scala.util.parsing.json._");

        String realContent = "\"" + linkisConfig.getCheckAlertTemplate().replace("qualitis_check_alert_topic", currentCheckAlert.getTopic()).replace("qualitis_check_alert_time", saveApplication.getSubmitTime()).replace("qualitis_check_alert_project_info", currentCheckAlert.getProject().getName() + SpecCharEnum.COLON.getValue() + currentCheckAlert.getWorkFlowName() + SpecCharEnum.COLON.getValue() + currentCheckAlert.getNodeName()) + "\"";

        jobCodes.add("var alertContent = " + realContent);
        String filter = StringUtils.isNotEmpty(currentCheckAlert.getFilter()) ? currentCheckAlert.getFilter() : "true";

        filter = DateExprReplaceUtil.replaceFilter(new Date(), filter);
        jobCodes.add("val alertTableArr = spark.sql(\"SELECT " + StringUtils.join(selectPart, SpecCharEnum.COMMA.getValue())
                + " FROM " + currentCheckAlert.getAlertTable() + " WHERE " + currentCheckAlert.getAlertCol() + " = 1 AND " + filter + "\").collect()");
        jobCodes.add("var alertTableContent = \"\"");
        jobCodes.add("for(ele <- alertTableArr) {alertTableContent = alertTableContent.concat(ele.mkString(\" | \")).concat(\"\\n\")}");
        jobCodes.add("if (alertTableContent.length > 0) {");
        jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_default_content\", alertTableContent)");
        jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_default_receiver\", \"" + currentCheckAlert.getDefaultReceiver() + "\")");
        jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_default_title\", \"" + StringUtils.join(contentTitle, "|") + "\")");
        jobCodes.add("} else {");
        jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_default_content\", \"\")");
        jobCodes.add("alertContent = alertContent.replaceAll(\"\\\\[告警人\\\\] qualitis_check_alert_default_receiver\", \"\")");

        jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_default_title\", \"\")");
        jobCodes.add("}");

        if (StringUtils.isNotEmpty(currentCheckAlert.getAdvancedAlertCol())) {
            jobCodes.add("val majorAlertTableArr = spark.sql(\"SELECT " + StringUtils.join(selectPart, SpecCharEnum.COMMA.getValue())
                    + " FROM " + currentCheckAlert.getAlertTable() + " WHERE " + currentCheckAlert.getAdvancedAlertCol() + " = 1 AND " + filter + "\").collect()");
            jobCodes.add("var majorAlertTableContent = \"\"");
            jobCodes.add("for(ele <- majorAlertTableArr) {majorAlertTableContent = majorAlertTableContent.concat(ele.mkString(\" | \")).concat(\"\\n\")}");
            jobCodes.add("if (majorAlertTableContent.length > 0) {");
            jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_advanced_content\", majorAlertTableContent)");
            jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_advanced_receiver\", \"" + (StringUtils.isNotEmpty(currentCheckAlert.getAdvancedReceiver()) ? currentCheckAlert.getAdvancedReceiver() : "") + "\")");
            jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_advanced_title\", \"" + StringUtils.join(contentTitle, "|") + "\")");
            // CURL IMS
            if (StringUtils.isNotEmpty(currentCheckAlert.getAdvancedReceiver())) {
                Map<String, Object> jsonAdvancedMap = new HashMap<>();
                jsonAdvancedMap.put("userAuthKey", imsConfig.getUserAuthKey());
                List<Map<String, Object>> alertList = new ArrayList<>();
                Map<String, Object> advancedAlertMap = new HashMap<>();
                advancedAlertMap.put("alert_way", currentCheckAlert.getAdvancedAlertWays());
                advancedAlertMap.put("alert_title", "【业务运维关注】Qualitis Check Alert");
                advancedAlertMap.put("ci_type_id", 55);
                advancedAlertMap.put("sub_system_id", imsConfig.getSystemId());
                advancedAlertMap.put("alert_level", currentCheckAlert.getAdvancedAlertLevel().toString());
                advancedAlertMap.put("alert_obj", dbAndTables[0] + "[" + dbAndTables[1] + "]");
                advancedAlertMap.put("alert_info", "qualitis_check_alert_Advanced");
                Map<String, Object> advancedAlertReceiverMap = extractAlertReceivers(currentCheckAlert.getAdvancedReceiver());
                advancedAlertMap.put("alert_reciver", advancedAlertReceiverMap.get("alert_reciver"));
                if (advancedAlertReceiverMap.containsKey("erp_group_id")) {
                    advancedAlertMap.put("erp_group_id", advancedAlertReceiverMap.get("erp_group_id").toString());
                    advancedAlertMap.put("alert_way", currentCheckAlert.getAdvancedAlertWays() + SpecCharEnum.COMMA.getValue() + AlertWayEnum.ERP.getCode());
                }
                alertList.add(advancedAlertMap);
                jsonAdvancedMap.put("alertList", alertList);
                String jsonAdvancedValue = "val jsonAdvancedValue = " + "\"" +CustomObjectMapper.transObjectToJson(jsonAdvancedMap).replace("\"", "\\\"") + "\"";
                jobCodes.add(jsonAdvancedValue);
                jobCodes.add("val realJsonAdvancedValue = jsonAdvancedValue.replaceAll(\"qualitis_check_alert_Advanced\", alertContent)");
                jobCodes.add("val jsonAdvancedCmd = Seq(\"curl\", \"-H\", \"'Content-Type: application/json'\",\"-d\", s\"$realJsonAdvancedValue\",\"" + imsConfig.getUrl() + imsConfig.getSendAlarmPath() + "\")");
                jobCodes.add("val advancedResponse = jsonAdvancedCmd.!!");
                jobCodes.add("val code = JSON.parseFull(advancedResponse).get.asInstanceOf[Map[String, Object]].get(\"resultCode\").get.toString");
                jobCodes.add("if (! \"0.0\".equals(code)) throw new RuntimeException(\"Failed to send ims alarm. Return non-zero code from IMS.\")");
            }
            jobCodes.add("} else {");
            jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_advanced_content\", \"\")");
            jobCodes.add("alertContent = alertContent.replaceAll(\"\\\\[高等级告警人\\\\] qualitis_check_alert_advanced_receiver\", \"\")");

            jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_advanced_title\", \"\")");
            jobCodes.add("}");
        } else {
            jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_advanced_content\", \"\")");
            jobCodes.add("alertContent = alertContent.replaceAll(\"\\\\[高等级告警人\\\\] qualitis_check_alert_advanced_receiver\", \"\")");

            jobCodes.add("alertContent = alertContent.replaceAll(\"qualitis_check_alert_advanced_title\", \"\")");
        }
        // CURL IMS
        jobCodes.add("if (alertTableContent.length > 0) {");
        Map<String, Object> jsonDefaultMap = new HashMap<>();
        jsonDefaultMap.put("userAuthKey", imsConfig.getUserAuthKey());
        List<Map<String, Object>> alertList = new ArrayList<>();
        Map<String, Object> defaultAlertMap = new HashMap<>();
        defaultAlertMap.put("alert_way", currentCheckAlert.getDefaultAlertWays());
        defaultAlertMap.put("alert_title", "【业务运维关注】Qualitis Check Alert");
        defaultAlertMap.put("ci_type_id", 55);
        defaultAlertMap.put("sub_system_id", imsConfig.getSystemId());
        defaultAlertMap.put("alert_level", currentCheckAlert.getDefaultAlertLevel().toString());
        defaultAlertMap.put("alert_obj", dbAndTables[0] + "[" + dbAndTables[1] + "]");
        defaultAlertMap.put("alert_info", "qualitis_check_alert_info");
        Map<String, Object> defaultAlertReceiverMap = extractAlertReceivers(currentCheckAlert.getDefaultReceiver());
        defaultAlertMap.put("alert_reciver", defaultAlertReceiverMap.get("alert_reciver"));
        if (defaultAlertReceiverMap.containsKey("erp_group_id")) {
            defaultAlertMap.put("erp_group_id", defaultAlertReceiverMap.get("erp_group_id").toString());
            defaultAlertMap.put("alert_way", currentCheckAlert.getDefaultAlertWays() + SpecCharEnum.COMMA.getValue() + AlertWayEnum.ERP.getCode());
        }
        alertList.add(defaultAlertMap);
        jsonDefaultMap.put("alertList", alertList);
        String jsonDefaultValue = "val jsonDefaultValue = " + "\"" +CustomObjectMapper.transObjectToJson(jsonDefaultMap).replace("\"", "\\\"") + "\"";
        jobCodes.add(jsonDefaultValue);
        jobCodes.add("val realJsonDefaultValue = jsonDefaultValue.replaceAll(\"qualitis_check_alert_info\", alertContent)");
        jobCodes.add("val jsonDefaultCmd = Seq(\"curl\", \"-H\", \"'Content-Type: application/json'\",\"-d\", s\"$realJsonDefaultValue\",\"" + imsConfig.getUrl() + imsConfig.getSendAlarmPath() + "\")");
        jobCodes.add("val defaultResponse = jsonDefaultCmd.!!");
        jobCodes.add("val code = JSON.parseFull(defaultResponse).get.asInstanceOf[Map[String, Object]].get(\"resultCode\").get.toString");
        jobCodes.add("if (! \"0.0\".equals(code)) throw new RuntimeException(\"Failed to send ims alarm. Return non-zero code from IMS.\")");
        jobCodes.add("}");


        String code = String.join("\n", job.getJobCode());
        LOGGER.info("Succeed to convert check alert into actual scala code. code: {}", code);

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

    private Map<String, Object> extractAlertReceivers(String alertReceiver) {
        Iterable<String> defaultReceiverIterable = Splitter.on(SpecCharEnum.COMMA.getValue()).omitEmptyStrings().trimResults().split(alertReceiver);
        AtomicReference<String> erpGroupId = new AtomicReference<>();
        List<String> defaultReceivers = new ArrayList<>();
        defaultReceiverIterable.forEach(item -> {
            if (item.startsWith(SpecCharEnum.LEFT_BRACKET.getValue()) && item.endsWith(SpecCharEnum.RIGHT_BRACKET.getValue())) {
                erpGroupId.set(item.replace(SpecCharEnum.LEFT_BRACKET.getValue(), StringUtils.EMPTY).replace(SpecCharEnum.RIGHT_BRACKET.getValue(), StringUtils.EMPTY));
            } else {
                defaultReceivers.add(item);
            }
        });
        Map<String, Object> resultMap = Maps.newHashMapWithExpectedSize(2);
        if (erpGroupId.get() != null) {
            resultMap.put("erp_group_id", erpGroupId.get());
        }
        if (CollectionUtils.isNotEmpty(defaultReceivers)) {
            resultMap.put("alert_reciver", Joiner.on(SpecCharEnum.COMMA.getValue()).join(defaultReceivers));
        }
        return resultMap;
    }

    private List<TaskResult> handleTaskResult(Application application, String submitTime, Rule rule, Set<AlarmConfig> alarmConfig, String runDate, String runToday, String result) throws UnExpectedRequestException {
        List<TaskResult> taskResults = new ArrayList<>();
        AlarmConfig currentAlarmConfig = alarmConfig.stream().iterator().next();

        RuleMetric ruleMetric = currentAlarmConfig.getRuleMetric();
        TaskResult taskResult;
        if (StringUtils.isNotBlank(runToday) && StringUtils.isBlank(runDate)) {
            runDate = runToday;
        }
        if (StringUtils.isNotBlank(runDate)) {
            TaskResult existTaskResult = taskResultDao.find(runDate, rule.getId(), null != ruleMetric ? ruleMetric.getId() : null);
            taskResult = getTaskResult(runDate, existTaskResult);
        } else {
            taskResult = new TaskResult();
        }
        taskResult.setApplicationId(application.getId());
        taskResult.setCreateTime(submitTime);
        taskResult.setRuleId(rule.getId());
        taskResult.setSaveResult(true);
        taskResult.setRuleMetricId(null != ruleMetric ? ruleMetric.getId() : null);
        taskResult.setResultType("int");
        taskResult.setValue(result);

        taskResults.add(taskResultDao.saveTaskResult(taskResult));
        return taskResults;
    }

    @NotNull
    private TaskResult getTaskResult(String runDate, TaskResult existTaskResult) throws UnExpectedRequestException {
        TaskResult taskResult;
        if (existTaskResult != null) {
            taskResult = existTaskResult;
        } else {
            taskResult = new TaskResult();
            Date runRealDate = handleTimeFormatting(runDate);
            taskResult.setRunDate(runRealDate.getTime());
        }
        return taskResult;
    }

    private List<TaskResult> saveTaskRusult(String fullSize, int partitionsNum, Application application, String submitTime, Rule rule,
                                            Set<AlarmConfig> alarmConfig, String runDate, String runToday) throws UnExpectedRequestException {
        double number = 0;
        String unit = "B";
        if (!QualitisConstants.NULL_TABLE_SIZE.equals(fullSize)) {
            number = Double.parseDouble(fullSize.split(" ")[0]);
            unit = fullSize.split(" ")[1];
        }
        // Save task result.
        List<TaskResult> taskResults = new ArrayList<>();

        AlarmConfig currentAlarmConfig = alarmConfig.stream().iterator().next();

        RuleMetric ruleMetric = currentAlarmConfig.getRuleMetric();
        if (ruleMetric == null) {
            throw new UnExpectedRequestException("File rule metric {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        TaskResult taskResult;
        if (StringUtils.isNotBlank(runToday) && StringUtils.isBlank(runDate)) {
            runDate = runToday;
        }
        if (StringUtils.isNotBlank(runDate)) {
            TaskResult existTaskResult = taskResultDao.find(runDate, rule.getId(), ruleMetric.getId());

            taskResult = getTaskResult(runDate, existTaskResult);
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
            TaskResult taskResult;
            if (null != alarmConfig.getRuleMetric()) {
                taskResult = taskResults.stream()
                        .filter(taskResultInDb -> taskResultInDb.getRuleMetricId().equals(alarmConfig.getRuleMetric().getId())).iterator().next();
            } else {
                taskResult = taskResults.iterator().next();
            }

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
            List<Long> ruleIdList = ruleList.stream().map(taskRule -> taskRule.getRuleId()).collect(Collectors.toList());
            Task task = new Task(application, createTime, TaskStatusEnum.SUBMITTED.getCode(), Double.parseDouble("0"), clusterInfo.getClusterName(), clusterInfo.getLinkisAddress());
            Boolean abortOnFailure = Boolean.FALSE;
            for (Rule rule : rules) {
                if (CollectionUtils.isNotEmpty(ruleIdList) && ! ruleIdList.contains(rule.getId())) {
                    continue;
                }
                if (rulePlaceInfo.get(rule.getId()) == null) {
                    continue;
                }
                if (rulePlaceInfo.get(rule.getId()).get(QualitisConstants.QUALITIS_UNION_WAY) != null) {
                    rule.setUnionWay((Integer) rulePlaceInfo.get(rule.getId()).get(QualitisConstants.QUALITIS_UNION_WAY));
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
            taskRule.setTemplateEnName(rule.getTemplate().getEnName());
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
            taskRule.setTaskRuleDataSourceList(getTaskRuleDataSourceBean(rule, dataQualityTask.getIndex()));
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

    private List<TaskRuleDataSource> getTaskRuleDataSourceBean(Rule rule, Integer index) {
        List<TaskRuleDataSource> taskRuleDataSources = new ArrayList<>();
        for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
            if (StringUtils.isBlank(ruleDataSource.getTableName())) {
                continue;
            }
            if (index != null && !index.equals(ruleDataSource.getDatasourceIndex())) {
                continue;
            }
            TaskRuleDataSource taskRuleDataSource = new TaskRuleDataSource();
            taskRuleDataSource.setClusterName(ruleDataSource.getClusterName());
            taskRuleDataSource.setDatabaseName(ruleDataSource.getDbName());
            taskRuleDataSource.setTableName(ruleDataSource.getTableName());
            taskRuleDataSource.setFilter(ruleDataSource.getFilter());
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

    private Map<String, List<Rule>> getRuleCluster(List<Rule> rules, Application application) {
        Map<String, List<Rule>> clusterNameMap = new HashMap<>(8);
        for (Rule rule : rules) {
            Set<String> clusterNameSet = rule.getRuleDataSources().stream()
                    .filter(ruleDataSource -> StringUtils.isNotEmpty(ruleDataSource.getClusterName()) && !QualitisConstants.ORIGINAL_INDEX.equals(ruleDataSource.getDatasourceIndex()))
                    .map(ruleDataSource -> {
                        if (ruleDataSource.getDatasourceIndex() != null && QualitisConstants.isTableRowsConsistency(rule.getTemplate().getEnName())) {
                            return ruleDataSource.getClusterName() + SpecCharEnum.PERIOD_NO_ESCAPE.getValue() + ruleDataSource.getDatasourceIndex();
                        } else {
                            return ruleDataSource.getClusterName();
                        }
                    }).collect(Collectors.toSet());

            if (CollectionUtils.isNotEmpty(clusterNameSet) && clusterNameSet.size() >= 2) {
                application.setClusterName(clusterNameSet.stream().map(clusterName -> {
                    if (clusterName.contains(SpecCharEnum.PERIOD_NO_ESCAPE.getValue())) {
                        return clusterName.split(SpecCharEnum.PERIOD.getValue())[0];
                    } else {
                        return clusterName;
                    }
                }).collect(Collectors.joining(SpecCharEnum.COMMA.getValue())));
                applicationDao.saveApplication(application);
            }

            for (String cluster : clusterNameSet) {
                LOGGER.info("Rule ID: {}, name: {} will be submit to cluster: {}", rule.getId(), rule.getName(), cluster);
                if (StringUtils.isNotEmpty(rule.getExecutionParametersName()) && !cluster.contains(SpecCharEnum.PERIOD_NO_ESCAPE.getValue())) {
                    cluster = cluster + SpecCharEnum.EQUAL.getValue() + rule.getExecutionParametersName();
                }
                if (!clusterNameMap.containsKey(cluster)) {
                    List<Rule> tmp = new ArrayList<>();
                    tmp.add(rule);
                    clusterNameMap.put(cluster, tmp);
                } else {
                    clusterNameMap.get(cluster).add(rule);
                }
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
