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

import com.webank.wedatasphere.qualitis.bean.DataQualityJob;
import com.webank.wedatasphere.qualitis.bean.DataQualityTask;
import com.webank.wedatasphere.qualitis.bean.JobKillResult;
import com.webank.wedatasphere.qualitis.bean.JobSubmitResult;
import com.webank.wedatasphere.qualitis.bean.RuleTaskDetail;
import com.webank.wedatasphere.qualitis.bean.TaskRule;
import com.webank.wedatasphere.qualitis.bean.TaskRuleAlarmConfigBean;
import com.webank.wedatasphere.qualitis.bean.TaskRuleDataSource;
import com.webank.wedatasphere.qualitis.bean.TaskSubmitResult;
import com.webank.wedatasphere.qualitis.client.AbstractJobSubmitter;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.config.TaskExecuteLimitConfig;
import com.webank.wedatasphere.qualitis.constant.AlarmConfigStatusEnum;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constant.TaskStatusEnum;
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
import com.webank.wedatasphere.qualitis.exception.ArgumentException;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.ConvertException;
import com.webank.wedatasphere.qualitis.exception.DataQualityTaskException;
import com.webank.wedatasphere.qualitis.exception.JobKillException;
import com.webank.wedatasphere.qualitis.exception.JobSubmitException;
import com.webank.wedatasphere.qualitis.exception.RuleVariableNotFoundException;
import com.webank.wedatasphere.qualitis.exception.RuleVariableNotSupportException;
import com.webank.wedatasphere.qualitis.exception.TaskTypeException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.response.table.PartitionStatisticsInfo;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableStatisticsInfo;
import com.webank.wedatasphere.qualitis.parser.LocaleParser;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.constant.FileOutputNameEnum;
import com.webank.wedatasphere.qualitis.rule.constant.FileOutputUnitEnum;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.util.UnitTransfer;
import com.webank.wedatasphere.qualitis.submitter.ExecutionManager;
import com.webank.wedatasphere.qualitis.util.DateExprReplaceUtil;
import com.webank.wedatasphere.qualitis.util.FilePassUtil;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

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
    private MetaDataClient metaDataClient;

    @Autowired
    private LinkisConfig linkisConfig;
    @Autowired
    private LocaleParser localeParser;

    private HttpServletRequest httpServletRequest;

    public static final FastDateFormat PRINT_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionManagerImpl.class);

    private static final String LINKIS_ONE_VERSION = "1.0";

    private static final String AND = "and";

    public ExecutionManagerImpl(@Context HttpServletRequest httpServletRequest) { this.httpServletRequest = httpServletRequest; }
    /**
     * Submit job to linkis
     */
    @Override
    public List<TaskSubmitResult> submitApplication(List<Rule> rules, String nodeName, String createTime, String user, String database
        , StringBuffer partition, Date date, Application application, String cluster, String startupParam, String setFlag, Map<String, String> execParams
        , StringBuffer runDate, Map<Long, Map> dataSourceMysqlConnect) throws ArgumentException, TaskTypeException, ConvertException, DataQualityTaskException
        , RuleVariableNotSupportException, RuleVariableNotFoundException, JobSubmitException, ClusterInfoNotConfigException, IOException, UnExpectedRequestException, MetaDataAcquireFailedException {

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
            if (StringUtils.isNotBlank(cluster)) {
                clusterName = cluster;
            }
            ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(clusterName);
            LOGGER.info("Start to check cluster config.");
            if (clusterInfo == null) {
                throw new ClusterInfoNotConfigException(clusterName + " {&DOES_NOT_EXIST}");
            }
            LOGGER.info("Succeed to pass the check of cluster config. All cluster of rules are configured");

            // Divide rule into tasks
            List<DataQualityTask> tasks = TaskDividerFactory.getDivider().divide(clusterRules, application.getId(), createTime, partition.toString(), date,
                    database, user, taskExecuteLimitConfig.getTaskExecuteRuleSize());
            LOGGER.info("Succeed to divide application into tasks. result: {}", tasks);

            // Save divided tasks
            saveDividedTask(tasks, clusterInfo, rules, application, createTime);

            // Convert tasks into job
            List<DataQualityJob> jobList = new ArrayList<>();
            for (DataQualityTask task : tasks) {
                DataQualityJob job = templateConverterFactory.getConverter(task).convert(task, date, setFlag, execParams, runDate.toString()
                    , clusterInfo.getClusterType(), dataSourceMysqlConnect);
                job.setUser(task.getUser());
                jobList.add(job);
                List<Long> ruleIdList = task.getRuleTaskDetails().stream().map(r -> r.getRule().getId()).collect(Collectors.toList());
                LOGGER.info("Succeed to convert rule_id: {} into code. code: {}", ruleIdList, job.getJobCode());
            }
            LOGGER.info("Succeed to convert all template into codes. codes: {}", jobList);

            // Submit job to linkis
            List<JobSubmitResult> submitResults = new ArrayList<>();
            for (DataQualityJob job : jobList) {
                String code = String.join("\n", job.getJobCode());
                String proxy = job.getUser();
                Long taskId = job.getTaskId();
                // Compatible with new and old submission interfaces.
                JobSubmitResult result = null;
                boolean engineReUse = false;
                if (StringUtils.isNotBlank(startupParam)) {
                    String[] startupParams = startupParam.split(SpecCharEnum.DIVIDER.getValue());

                    for (String param : startupParams) {
                        if (StringUtils.isEmpty(param)) {
                            continue;
                        }
                        String[] paramStrs = param.split("=");
                        if (paramStrs.length < 2) {
                            continue;
                        }
                        String key = paramStrs[0];
                        String value = paramStrs[1];
                        if ("engine_reuse".equals(key)) {
                            if ("true".equals(value)) {
                                engineReUse = true;
                                startupParam = startupParam.replace("engine_reuse=true", "");
                            } else {
                                engineReUse = false;
                                startupParam = startupParam.replace("engine_reuse=false", "");
                            }
                            break;
                        }

                    }
                }
                if (clusterInfo.getClusterType().endsWith(LINKIS_ONE_VERSION)) {
                    result = abstractJobSubmitter.submitJobNew(code, linkisConfig.getEngineName(), StringUtils.isNotBlank(proxy) ? proxy : user
                        , clusterInfo.getLinkisAddress(), clusterName, taskId, csId, nodeName, StringUtils.isNotBlank(startupParam) ? startupParam : job.getStartupParam(), engineReUse);
                } else {
                    result = abstractJobSubmitter.submitJob(code, linkisConfig.getEngineName(), StringUtils.isNotBlank(proxy) ? proxy : user
                        , clusterInfo.getLinkisAddress(), clusterName, taskId, csId, nodeName, StringUtils.isNotBlank(startupParam) ? startupParam : job.getStartupParam());
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
            // Rewrite task remote ID.
            rewriteTaskRemoteInfo(submitResults, taskSubmitResults, application.getId(), clusterInfo.getClusterName());

        }

        return taskSubmitResults;
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
    public TaskSubmitResult executeFileRule(List<Rule> fileRules, String submitTime, Application application, String user, String clusterName, StringBuffer runDate)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        LOGGER.info("Start to execute file rule task and save check result.");
        Task taskInDb = taskDao.save(new Task(application, submitTime, TaskStatusEnum.SUBMITTED.getCode()));
        Set<TaskDataSource> taskDataSources = new HashSet<>(fileRules.size());
        Set<TaskRuleSimple> taskRuleSimples = new HashSet<>(fileRules.size());
        int totalRules = fileRules.size();
        int successRule = 0;
        for (Rule rule : fileRules) {
            if (rule.getAbortOnFailure() != null) {
                taskInDb.setAbortOnFailure(rule.getAbortOnFailure());
            }
            TaskRuleSimple taskRuleSimple = new TaskRuleSimple(rule, taskInDb, httpServletRequest.getHeader("Content-Language"));
            taskRuleSimples.add(taskRuleSimpleRepository.save(taskRuleSimple));

            RuleDataSource ruleDataSource = rule.getRuleDataSources().iterator().next();
            taskDataSources.add(taskDataSourceRepository.save(new TaskDataSource(ruleDataSource, taskInDb)));

            // Check rule datasource: 1) table 2) partition.
            if (StringUtils.isEmpty(ruleDataSource.getFilter())) {
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
                List<TaskResult> taskResultInDbs = saveTaskRusult(fullSize, Double.parseDouble(result.getTableFileCount() + ""), application, submitTime
                    , rule, rule.getAlarmConfigs(), runDate.toString());
                successRule = modifyTaskStatus(taskRuleSimple.getTaskRuleAlarmConfigList(), taskInDb, taskResultInDbs, successRule);
            } else {
                PartitionStatisticsInfo result;
                try {
                    String proxyUser = ruleDataSource.getProxyUser();
                    result = metaDataClient.getPartitionStatisticsInfo(StringUtils.isNotBlank(clusterName) ? clusterName : ruleDataSource.getClusterName()
                        , ruleDataSource.getDbName(), ruleDataSource.getTableName(), filterToPartitionPath(DateExprReplaceUtil.replaceFilter(new Date(), ruleDataSource.getFilter())),  StringUtils.isNotBlank(proxyUser) ? proxyUser : user);
                } catch (RestClientException e) {
                    LOGGER.error("Failed to get table statistics with linkis api.", e);
                    throw new UnExpectedRequestException("{&FAILED_TO_GET_DATASOURCE_INFO}");
                }

                if (result == null) {
                    throw new UnExpectedRequestException("{&FAILED_TO_GET_DATASOURCE_INFO}");
                }

                String fullSize = result.getPartitionSize();
                List<TaskResult> taskResultInDbs = saveTaskRusult(fullSize, Double.parseDouble(result.getPartitionChildCount() + ""), application, submitTime
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
        taskInDb.setEndTime(ExecutionManagerImpl.PRINT_TIME_FORMAT.format(new Date()));
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
    public GeneralResponse<?> killApplication(Application applicationInDb, String user)
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
            task.setEndTime(ExecutionManagerImpl.PRINT_TIME_FORMAT.format(new Date()));
            taskDao.save(task);
        }
        return new GeneralResponse<>("200", "{&SUCCESS_TO_KILL_TASK}", results.size());
    }

    private List<TaskResult> saveTaskRusult(String fullSize, double fileCount, Application application, String submitTime, Rule rule,
        Set<AlarmConfig> alarmConfig, String runDate) throws UnExpectedRequestException {
        double number = Double.parseDouble(fullSize.split(" ")[0]);
        String unit = fullSize.split(" ")[1];
        // Save task result.
        List<TaskResult> taskResults = new ArrayList<>();

        List<Integer> fileOutputNames = alarmConfig.stream().map(AlarmConfig::getFileOutputName).distinct().collect(Collectors.toList());

        for (Integer fileOutputName : fileOutputNames) {
            AlarmConfig currentAlarmConfig = alarmConfig.stream().filter(alarmConfigSetting -> alarmConfigSetting.getFileOutputName().equals(fileOutputName)).iterator().next();
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
            taskResult.setRuleMetricId(ruleMetric.getId());

            if (fileOutputName.equals(FileOutputNameEnum.FILE_COUNT.getCode())) {
                taskResult.setResultType("int");
                taskResult.setValue(fileCount + "");
            } else if (fileOutputName.equals(FileOutputNameEnum.DIR_SIZE.getCode())) {
                String alarmConfigUnit = FileOutputUnitEnum.fileOutputUnit(currentAlarmConfig.getFileOutputUnit());
                taskResult.setResultType(alarmConfigUnit);
                taskResult.setValue(UnitTransfer.alarmconfigToTaskResult(number, alarmConfigUnit, unit.toUpperCase()) + "");
            } else {
                throw new UnExpectedRequestException("Unknown file output name.");
            }
            taskResults.add(taskResultDao.saveTaskResult(taskResult));
        }

        return taskResults;
    }

    private int modifyTaskStatus(List<TaskRuleAlarmConfig> alarmConfigs, Task taskInDb, List<TaskResult> taskResults, int successRule) {
        boolean rulePass = true;
        for (TaskRuleAlarmConfig alarmConfig : alarmConfigs) {
            TaskResult taskResult = taskResults.stream().filter(taskResultInDb -> taskResultInDb.getRuleMetricId().equals(alarmConfig.getRuleMetric().getId())).iterator().next();
            if (! FilePassUtil.pass(alarmConfig, taskResult, taskResultDao)) {
                rulePass = false;
                alarmConfig.setStatus(AlarmConfigStatusEnum.NOT_PASS.getCode());
                if (alarmConfig.getDeleteFailCheckResult() != null && alarmConfig.getDeleteFailCheckResult()) {
                    taskResult.setSaveResult(false);
                    taskResultDao.saveTaskResult(taskResult);
                }
            } else {
                alarmConfig.setStatus(AlarmConfigStatusEnum.PASS.getCode());
            }
        }
        if (rulePass) {
            successRule ++;
        } else if (taskInDb.getAbortOnFailure() != null && taskInDb.getAbortOnFailure()) {
            taskInDb.setStatus(TaskStatusEnum.FAILED.getCode());
        } else {
            taskInDb.setStatus(TaskStatusEnum.FAIL_CHECKOUT.getCode());
            taskInDb.setProgress(Double.parseDouble("1"));
        }
        return successRule;
    }

    private void saveDividedTask(List<DataQualityTask> dataQualityTasks, ClusterInfo clusterInfo, List<Rule> rules, Application application, String createTime) {
        for (DataQualityTask dataQualityTask : dataQualityTasks) {
            List<TaskRule> ruleList = getRule(rules, dataQualityTask);
            Task task = new Task(application, createTime, TaskStatusEnum.SUBMITTED.getCode(), Double.parseDouble("0"), clusterInfo.getClusterName(), clusterInfo.getLinkisAddress());
            Boolean abortOnFailure = false;
            for (Rule rule : rules) {
                if (rule.getAbortOnFailure()) {
                    abortOnFailure = true;
                    break;
                }
            }
            task.setAbortOnFailure(abortOnFailure);
            task.setTaskProxyUser(dataQualityTask.getUser());
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
            taskRule.setRuleGroupName(rule.getRuleGroup().getRuleGroupName());
            taskRule.setRuleType(rule.getRuleType());
            taskRule.setRuleName(rule.getName());
            taskRule.setCnName(rule.getCnName());
            taskRule.setRuleDetail(rule.getDetail());
            taskRule.setTemplateName(rule.getTemplate().getName());
            taskRule.setMidTableName(ruleTaskDetail.getMidTableName());
            taskRule.setProjectId(ruleTaskDetail.getRule().getProject().getId());
            taskRule.setProjectName(ruleTaskDetail.getRule().getProject().getName());
            taskRule.setProjectCnName(ruleTaskDetail.getRule().getProject().getCnName());
            taskRule.setProjectCreator(ruleTaskDetail.getRule().getProject().getCreateUser());
            if (rule.getChildRule() != null) {
                taskRule.setChildRuleId(rule.getChildRule().getId());
                taskRule.setChildRuleType(rule.getChildRule().getRuleType());
                taskRule.setChildTaskRuleDataSourceList(getTaskRuleDataSourceBean(rule.getChildRule()));
                taskRule.setChildTaskRuleAlarmConfigsBeans(getTaskRuleAlarmConfigBean(rule.getChildRule()));
            }

            taskRule.setTaskRuleDataSourceList(getTaskRuleDataSourceBean(rule));
            taskRule.setTaskRuleAlarmConfigBeans(getTaskRuleAlarmConfigBean(rule));
            taskRule.setDeleteFailCheckResult(rule.getDeleteFailCheckResult());
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
            taskRuleAlarmConfigBean.setRuleMetric(alarmConfig.getRuleMetric());
            taskRuleAlarmConfigBean.setUploadRuleMetricValue(alarmConfig.getUploadRuleMetricValue());
            taskRuleAlarmConfigBean.setUploadAbnormalValue(alarmConfig.getUploadAbnormalValue());
            taskRuleAlarmConfigBean.setDeleteFailCheckResult(alarmConfig.getDeleteFailCheckResult());
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

    private String filterToPartitionPath(String filter) {
        List<String> partitionPath = new ArrayList<>();
        String[] conditions = filter.split(AND);
        for (String condition : conditions) {
            condition = condition.trim().replace("'", "");
            partitionPath.add("/" + condition);
        }
        return StringUtils.join(partitionPath.toArray());
    }

}
