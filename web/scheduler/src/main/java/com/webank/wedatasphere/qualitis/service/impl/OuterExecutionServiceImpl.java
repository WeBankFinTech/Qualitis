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

import com.webank.wedatasphere.qualitis.config.SystemKeyConfig;
import com.webank.wedatasphere.qualitis.constant.*;
import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.dao.SystemConfigDao;
import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.parser.LocaleParser;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.request.*;
import com.webank.wedatasphere.qualitis.entity.*;
import com.webank.wedatasphere.qualitis.exception.*;
import com.webank.wedatasphere.qualitis.job.MonitorManager;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.response.*;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.service.OuterExecutionService;
import com.webank.wedatasphere.qualitis.exception.JobSubmitException;
import com.webank.wedatasphere.qualitis.submitter.ExecutionManager;
import com.webank.wedatasphere.qualitis.submitter.impl.ExecutionManagerImpl;
import com.webank.wedatasphere.qualitis.bean.LogResult;
import com.webank.wedatasphere.qualitis.bean.TaskSubmitResult;
import com.webank.wedatasphere.qualitis.constant.ApplicationStatusEnum;
import com.webank.wedatasphere.qualitis.constant.InvokeTypeEnum;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.response.ApplicationTaskResponse;
import com.webank.wedatasphere.qualitis.response.ApplicationTaskSimpleResponse;
import com.webank.wedatasphere.qualitis.parser.LocaleParser;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.request.GroupExecutionRequest;
import com.webank.wedatasphere.qualitis.request.ProjectExecutionRequest;
import com.webank.wedatasphere.qualitis.request.RuleListExecutionRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Service
public class OuterExecutionServiceImpl implements OuterExecutionService {

    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private ApplicationDao applicationDao;
    @Autowired
    private ExecutionManager executionManager;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private RuleDataSourceDao ruleDataSourceDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private MonitorManager monitorManager;
    @Autowired
    private ClusterInfoDao clusterInfoDao;
    @Autowired
    private OuterExecutionService outerExecutionService;
    @Autowired
    private LocaleParser localeParser;
    @Autowired
    private RuleGroupDao ruleGroupDao;
    @Autowired
    private SystemConfigDao systemConfigDao;
    @Autowired
    private SystemKeyConfig systemKeyConfig;

    @Context
    private HttpServletRequest httpRequest;

    private static final FastDateFormat TASK_TIME_FORMAT = FastDateFormat.getInstance("yyyyMMddHHmmssSSS");
    private static final String USERNAME_FORMAT_PLACEHOLDER = "${USERNAME}";
    private static final Logger LOGGER = LoggerFactory.getLogger(OuterExecutionServiceImpl.class);

    public OuterExecutionServiceImpl(@Context HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public GeneralResponse<?> generalExecution(GeneralExecutionRequest request) throws UnExpectedRequestException {
        // Generator application information
        GeneralExecutionRequest.checkRequest(request);
        Date date = new Date();
        Application newApplication = generateApplicationInfo(request.getCreateUser(), request.getExecutionUser(), date);

        try {
            if (request.getProjectId() != null) {
                ProjectExecutionRequest projectExecutionRequest = new ProjectExecutionRequest();
                BeanUtils.copyProperties(request, projectExecutionRequest);
                return projectExecution(projectExecutionRequest, newApplication, date);
            } else if (request.getRuleList() != null && !request.getRuleList().isEmpty()) {
                RuleListExecutionRequest ruleListExecutionRequest = new RuleListExecutionRequest();
                BeanUtils.copyProperties(request, ruleListExecutionRequest);
                return ruleListExecution(ruleListExecutionRequest, newApplication, date);
            } else if (request.getGroupId() != null) {
                GroupExecutionRequest groupExecutionRequest = new GroupExecutionRequest();
                BeanUtils.copyProperties(request, groupExecutionRequest);
                return groupExecution(groupExecutionRequest, newApplication, date);
            } else if (request.getCluster() != null) {
                DataSourceExecutionRequest dataSourceExecutionRequest = new DataSourceExecutionRequest();
                BeanUtils.copyProperties(request, dataSourceExecutionRequest);
                return dataSourceExecution(dataSourceExecutionRequest, newApplication, date);
            } else {
                throw new UnExpectedRequestException("{&CAN_NOT_RESOLVE_THE_REQUEST}, request: " + request);
            }
        } catch (UnExpectedRequestException e) {
            newApplication.setStatus(ApplicationStatusEnum.ARGUMENT_NOT_CORRECT.getCode());
            String exceptionMessage = localeParser.replacePlaceHolderByLocale(ExceptionUtils.getStackTrace(e), httpRequest.getHeader("Content-Language"));
            newApplication.setExceptionMessage(exceptionMessage);
            newApplication.setFinishTime(ExecutionManagerImpl.PRINT_TIME_FORMAT.format(new Date()));
            applicationDao.saveApplication(newApplication);
            LOGGER.info("Succeed to set application status to [{}], application_id: {}", newApplication.getStatus(), newApplication.getId());
            throw new UnExpectedRequestException(e.getMessage());
        }
    }

    @Override
    public GeneralResponse<?> getApplicationStatus(String applicationId) throws UnExpectedRequestException {
        // Find application by applicationId
        Application application = applicationDao.findById(applicationId);
        if (application == null) {
            throw new UnExpectedRequestException("Application_id {&DOES_NOT_EXIST}");
        }
        LOGGER.info("Succeed to find application. application: {}", application);

        List<Task> tasks = taskDao.findByApplication(application);
        ApplicationTaskResponse response = new ApplicationTaskResponse(tasks);

        LOGGER.info("Succeed to get application status. response: {}", response);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_APPLICATION_STATUS}", response);
    }

    @Override
    public GeneralResponse<?> getTaskLog(GetTaskLogRequest request) throws UnExpectedRequestException {
        // Check Arguments
        GetTaskLogRequest.checkRequest(request);

        Task task = taskDao.findByRemoteTaskId(request.getTaskId());
        if (task == null) {
            throw new UnExpectedRequestException("Task_id {&DOES_NOT_EXIST}");
        }

        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(request.getClusterId());
        if (clusterInfo == null) {
            throw new UnExpectedRequestException("cluster : [" + request.getClusterId() + "] {&DOES_NOT_EXIST}");
        }

        String executeUser = task.getApplication().getExecuteUser();

        // Check if user has permissions proxying execution user
        checkPermissionCreateUserProxyExecuteUser(request.getCreateUser(), executeUser);

        // Check if user has permissions to view this task
        if (!request.getCreateUser().equals(task.getApplication().getCreateUser())) {
            throw new UnExpectedRequestException(
                    String.format("User: %s {&HAS_NO_PERMISSION_ACCESSING} task: %s", request.getCreateUser(), request.getTaskId()));
        }

        LogResult logResult;
        try {
            logResult = monitorManager.getTaskPartialLog(request.getTaskId(), 0, executeUser, clusterInfo.getLinkisAddress(), clusterInfo.getClusterName());
        } catch (LogPartialException | ClusterInfoNotConfigException e) {
            throw new UnExpectedRequestException(e.getMessage());
        }

        LOGGER.info("Succeed to get log of the task. task_id: {}, cluster_id: {}", request.getTaskId(), request.getClusterId());
        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_TASK_LOG}", logResult.getLog());
    }


    @Override
    public Application generateApplicationInfo(String createUser, String executionUser, Date date) {
        String nonce = RandomStringUtils.randomNumeric(6);
        String applicationId = generateTaskId(date, nonce);
        LOGGER.info("Succeed to generate application_id: {}", applicationId);
        String submitTime = ExecutionManagerImpl.PRINT_TIME_FORMAT.format(date);

        Application application = new Application();
        application.setId(applicationId);
        application.setSubmitTime(submitTime);
        application.setInvokeType(InvokeTypeEnum.API_INVOKE.getCode());
        application.setCreateUser(createUser);
        application.setExecuteUser(executionUser);
        return applicationDao.saveApplication(application);
    }

    private GeneralResponse<?> groupExecution(GroupExecutionRequest request, Application newApplication, Date date) throws UnExpectedRequestException {
        LOGGER.info("Execute application by group. group_id: {}", request.getGroupId());
        // Check Arguments
        GroupExecutionRequest.checkRequest(request);

        // Check existence of project
        RuleGroup ruleGroupInDb = ruleGroupDao.findById(request.getGroupId());
        if (null == ruleGroupInDb) {
            throw new UnExpectedRequestException("Group_id " + request.getGroupId() + " {&DOES_NOT_EXIST}");
        }
        LOGGER.info("Succeed to find rule group. group_id: {}", ruleGroupInDb.getId());

        Project projectInDb = projectDao.findById(ruleGroupInDb.getProjectId());
        checkPermission(request.getCreateUser(), request.getExecutionUser(), Collections.singletonList(projectInDb));

        // Find all rules
        List<Rule> rules = ruleDao.findByRuleGroup(ruleGroupInDb);
        List<Long> ruleIdList = rules.stream().map(Rule::getId).collect(Collectors.toList());
        LOGGER.info("Succeed to get rules: rule_id: {}", ruleIdList);

        return submitRules(rules, request.getPartition(), request.getExecutionUser(), newApplication, date);
    }

    @Override
    public GeneralResponse<?> projectExecution(ProjectExecutionRequest request, Application newApplication, Date date) throws UnExpectedRequestException {
        LOGGER.info("Execute application by project. project_id: {}", request.getProjectId());
        // Check Arguments
        ProjectExecutionRequest.checkRequest(request);

        // Check existence of project
        Project projectInDb = projectDao.findById(request.getProjectId());
        if (null == projectInDb) {
            throw new UnExpectedRequestException("Project_id " + request.getProjectId() + " {&DOES_NOT_EXIST}");
        }
        LOGGER.info("Succeed to find project. project_id: {}", projectInDb.getId());
        checkPermission(request.getCreateUser(), request.getExecutionUser(), Collections.singletonList(projectInDb));

        // Find all rules
        List<Rule> rules = new ArrayList<>(projectInDb.getRules());
        List<Long> ruleIdList = rules.stream().map(Rule::getId).collect(Collectors.toList());
        LOGGER.info("Succeed to get rules: rule_id: {}", ruleIdList);

        return submitRules(rules, request.getPartition(), request.getExecutionUser(), newApplication, date);
    }

    @Override
    public GeneralResponse<?> ruleListExecution(RuleListExecutionRequest request, Application newApplication, Date date) throws UnExpectedRequestException {
        LOGGER.info("Execute application by rules. rule_ids: {}", request.getRuleList());
        // Check Arguments
        RuleListExecutionRequest.checkRequest(request);

        // Check the existence of rule
        List<Rule> rules = ruleDao.findByIds(request.getRuleList());
        List<Long> ruleIds = rules.stream().map(Rule::getId).distinct().collect(Collectors.toList());
        List<Long> notExistRules = request.getRuleList().stream().filter(l -> !ruleIds.contains(l)).collect(Collectors.toList());
        if (!notExistRules.isEmpty()) {
            throw new UnExpectedRequestException("The ids of rule: " + notExistRules.toString() + " {&DOES_NOT_EXIST}");
        }
        LOGGER.info("Succeed to find all rules.");

        List<Project> projects = rules.stream().map(Rule::getProject).collect(Collectors.toList());
        List<Long> projectIds = projects.stream().map(Project::getId).collect(Collectors.toList());
        LOGGER.info("Succeed to find projects of rules. project_id: {}", projectIds);

        checkPermission(request.getCreateUser(), request.getExecutionUser(), projects);

        return submitRules(rules, request.getPartition(), request.getExecutionUser(), newApplication, date);
    }

    @Override
    public GeneralResponse<?> getApplicationResult(String applicationId) throws UnExpectedRequestException {
        // Find application by applicationId
        Application applicationInDb = applicationDao.findById(applicationId);
        if (applicationInDb == null) {
            throw new UnExpectedRequestException(String.format("ApplicationId: %s {&DOES_NOT_EXIST}", applicationId));
        }

        Integer passNum = 0;
        Integer failedNum = 0;
        Integer notPassNum = 0;
        StringBuilder passMessage = new StringBuilder();
        StringBuilder failedMessage = new StringBuilder();
        StringBuilder notPassMessage = new StringBuilder();

        passMessage.append("The rules following below are pass(以下规则已通过校验): [");
        failedMessage.append("The rules following below are failed(以下规则已失败): [");
        notPassMessage.append("The rules following below are failed(以下规则未通过校验): [");

        // Find all tasks by application
        List<Task> tasks = taskDao.findByApplication(applicationInDb);
        // Find task rule simple in all tasks
        for (Task task : tasks) {
            // Add failed num, pass num if task status equals to Succeed, Failed
            if (task.getStatus().equals(TaskStatusEnum.FAILED.getCode())) {
                failedNum += task.getTaskRuleSimples().size();
                generateFailedMessage(task, failedMessage);
            } else if (task.getStatus().equals(TaskStatusEnum.PASS_CHECKOUT.getCode())) {
                passNum += task.getTaskRuleSimples().size();
                generatePassMessage(task, passMessage);
            } else if (task.getStatus().equals(TaskStatusEnum.FAIL_CHECKOUT.getCode())) {
                // Find not pass task if task status equals to failed_checkout
                for (TaskRuleSimple taskRuleSimple : task.getTaskRuleSimples()) {
                    Boolean isPass = true;
                    for (TaskRuleAlarmConfig taskRuleAlarmConfig : taskRuleSimple.getTaskRuleAlarmConfigList()) {
                        if (taskRuleAlarmConfig.getStatus().equals(AlarmConfigStatusEnum.NOT_PASS.getCode())) {
                            isPass = false;
                            break;
                        }
                    }

                    if (isPass) {
                        passNum++;
                        passMessage.append(taskRuleSimple.getRuleName()).append(", ");
                    } else {
                        notPassNum++;
                        notPassMessage.append(taskRuleSimple.getRuleName()).append(", ");
                    }
                }
            } else {
                throw new UnExpectedRequestException(String.format("ApplicationId: %s {&DOES_NOT_FINISHED_YET}", applicationId));
            }
        }

        passMessage.append("]").append(System.lineSeparator());
        failedMessage.append("]").append(System.lineSeparator());
        notPassMessage.append("]").append(System.lineSeparator());

        String resultMessage = passMessage.toString() + failedMessage.toString() + notPassMessage.toString();

        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_APPLICATION_RESULT}",
                new ApplicationResultResponse(passNum, failedNum, notPassNum, resultMessage));
    }

    private void generatePassMessage(Task task, StringBuilder stringBuilder) {
        for (TaskRuleSimple taskRuleSimple : task.getTaskRuleSimples()) {
            stringBuilder.append(taskRuleSimple.getRuleName()).append(", ");
        }
    }

    private void generateFailedMessage(Task task, StringBuilder stringBuilder) {
        for (TaskRuleSimple taskRuleSimple : task.getTaskRuleSimples()) {
            stringBuilder.append(taskRuleSimple.getRuleName()).append(", ");
        }
    }

    private GeneralResponse<?> dataSourceExecution(DataSourceExecutionRequest request, Application newApplication, Date date) throws UnExpectedRequestException {
        LOGGER.info("Execute application by datasource. cluster: {}, database: {}, table: {}", request.getCluster(), request.getDatabase(), request.getTable());
        // Check Arguments
        DataSourceExecutionRequest.checkRequest(request);

        // Find all projects by user
        List<Long> projectIds = getAssociateProject(request.getCreateUser()).stream().map(Project::getId).distinct().collect(Collectors.toList());

        // Find all rule datasources by project
        List<RuleDataSource> ruleDataSources = new ArrayList<>();
        for (Long projectId : projectIds) {
            ruleDataSources.addAll(ruleDataSourceDao.findByProjectId(projectId));
        }

        List<Rule> rules = ruleDataSources.stream().filter(r ->
                r.getClusterName().equals(request.getCluster()) && r.getDbName().equals(request.getDatabase()) && r.getTableName().equals(request.getTable())
        ).map(RuleDataSource::getRule).filter(rule -> {
            if (!request.getCrossTable()) {
                return rule.getRuleDataSources().size() == 1;
            }
            return true;
        }).collect(Collectors.toList());
        List<Long> ruleIds = rules.stream().map(Rule::getId).collect(Collectors.toList());
        LOGGER.info("Succeed to find all rules of datasource. rule_id: {}", ruleIds);

        return submitRules(rules, request.getPartition(), request.getExecutionUser(), newApplication, date);
    }

    private List<Project> getAssociateProject(String createUser) {
        return projectDao.findByCreateUser(createUser);
    }

    /**
     * Generate jobs by rules and submit jobs to likins
     * @param rules
     * @param partition
     * @return
     * @throws UnExpectedRequestException
     */
    private GeneralResponse<?> submitRules(List<Rule> rules, String partition, String executionUser, Application newApplication, Date date) throws UnExpectedRequestException {
        if (rules == null || rules.isEmpty()) {
            throw new UnExpectedRequestException("{&NO_RULE_CAN_BE_EXECUTED}");
        }
        ApplicationTaskSimpleResponse response = null;
        try {
            response = outerExecutionService.commonExecution(rules, partition, executionUser, newApplication, date);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            newApplication.setStatus(ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode());
            newApplication.setExceptionMessage(ExceptionUtils.getStackTrace(e));
            newApplication.setFinishTime(ExecutionManagerImpl.PRINT_TIME_FORMAT.format(new Date()));
            applicationDao.saveApplication(newApplication);
            LOGGER.info("Succeed to set application status to [{}], application_id: {}", newApplication.getStatus(), newApplication.getId());
            return new GeneralResponse<>("500", e.getMessage(), null);
        }

        LOGGER.info("Succeed to dispatch task. response: {}", response);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_DISPATCH_TASK}", response);
    }


    @Override
    public ApplicationTaskSimpleResponse commonExecution(List<Rule> rules, String partition, String executionUser, Application newApplication, Date date)
        throws RuleVariableNotSupportException, JobSubmitException, RuleVariableNotFoundException, ArgumentException,
        ConvertException, DataQualityTaskException, TaskTypeException, ExecutionException, InterruptedException,
        ClusterInfoNotConfigException, SystemConfigException {
        // current user
        String username = executionUser;

        // Generate database name, submitTime
        String submitTime = ExecutionManagerImpl.PRINT_TIME_FORMAT.format(date);
        String database = generateDatabase(username);
        LOGGER.info("Succeed to generate database_name: {}", database);

        // Save application
        newApplication.setInvokeType(InvokeTypeEnum.API_INVOKE.getCode());
        newApplication.setRuleSize(rules.size());
        newApplication.setSavedDb(database);
        Application saveApplication = applicationDao.saveApplication(newApplication);

        // Submit job
        List<TaskSubmitResult> taskSubmitResults = null;
        taskSubmitResults = executionManager.submitApplication(saveApplication.getId(), rules, submitTime, username, database, partition, date, saveApplication);
        LOGGER.info("Succeed to submit application. result: {}", taskSubmitResults);

        saveApplication.setTotalTaskNum(taskSubmitResults.size());
        Application applicationInDb = applicationDao.saveApplication(saveApplication);
        LOGGER.info("Succeed to save application. application: {}", applicationInDb);

        return new ApplicationTaskSimpleResponse(taskSubmitResults);
    }

    private void checkPermission(String createUser, String executeUser, List<Project> projects) throws UnExpectedRequestException {
        checkCreateUser(createUser, projects);

        checkPermissionCreateUserProxyExecuteUser(createUser, executeUser);
    }

    private void checkPermissionCreateUserProxyExecuteUser(String createUser, String executeUser) throws UnExpectedRequestException {
        if (createUser.equals(executeUser)) {
            LOGGER.info("CreateUser is equals to execute user, pass permission, user: {}", createUser);
            return;
        }

        User userInDb = userDao.findByUsername(createUser);
        if (userInDb == null) {
            throw new UnExpectedRequestException("create user: "+ createUser + " {&DOES_NOT_EXIST}");
        }

        if (CollectionUtils.isEmpty(userInDb.getUserProxyUsers())) {
            throw new UnExpectedRequestException("Create user: " + createUser + " {&HAS_NO_PERMISSION_PROXYING_EXECUTE_USER}: " + executeUser);
        }

        for (UserProxyUser userProxyUser :userInDb.getUserProxyUsers()) {
            if (userProxyUser.getProxyUser().getProxyUserName().equals(executeUser)) {
                LOGGER.info("CreateUser has permission proxying execute user, pass permission, user: {}, execute user: {}",
                        createUser, executeUser);
                return;
            }
        }
        throw new UnExpectedRequestException("Create user: " + createUser + " {&HAS_NO_PERMISSION_PROXYING_EXECUTE_USER}: " + executeUser);
    }

    private void checkCreateUser(String createUser, List<Project> projects) throws UnExpectedRequestException {
        for (Project project : projects) {
            LOGGER.info("Checking permission of create_user, project_id: {}", project.getId());
            if (!project.getCreateUser().equals(createUser)) {
                throw new UnExpectedRequestException("User: [" + createUser + "] {&HAS_NO_PERMISSION_TO_ACCESS} project: id [" + project.getId() + "] name [" + project.getName() + "]");
            }
            LOGGER.info("Pass permission, project_id: {}", project.getId());
        }
    }

    private String generateDatabase(String username) throws SystemConfigException {
        SystemConfig systemConfigInDb = systemConfigDao.findByKeyName(systemKeyConfig.getSaveDatabasePattern());

        if (systemConfigInDb == null || StringUtils.isBlank(systemConfigInDb.getValue())) {
            throw new SystemConfigException(String.format("System Config: %s, can not be null or empty", systemKeyConfig.getSaveDatabasePattern()));
        }

        return systemConfigInDb.getValue().replace(USERNAME_FORMAT_PLACEHOLDER, username);
    }

    private String generateTaskId(Date date, String nonce) {
        return "QUALITIS" + TASK_TIME_FORMAT.format(date) + "_" + nonce;
    }
}
