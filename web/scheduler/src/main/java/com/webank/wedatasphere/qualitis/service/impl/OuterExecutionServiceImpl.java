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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Name;
import com.alibaba.druid.util.JdbcConstants;
import com.webank.wedatasphere.qualitis.bean.LogResult;
import com.webank.wedatasphere.qualitis.bean.TaskSubmitResult;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.config.SystemKeyConfig;
import com.webank.wedatasphere.qualitis.constant.AlarmConfigStatusEnum;
import com.webank.wedatasphere.qualitis.constant.ApplicationCommentEnum;
import com.webank.wedatasphere.qualitis.constant.ApplicationStatusEnum;
import com.webank.wedatasphere.qualitis.constant.InvokeTypeEnum;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constant.TaskStatusEnum;
import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.dao.SystemConfigDao;
import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.dao.TaskResultDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.repository.TaskDataSourceRepository;
import com.webank.wedatasphere.qualitis.dao.repository.TaskRuleSimpleRepository;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.entity.SystemConfig;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.TaskDataSource;
import com.webank.wedatasphere.qualitis.entity.TaskResult;
import com.webank.wedatasphere.qualitis.entity.TaskRuleAlarmConfig;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserProxyUser;
import com.webank.wedatasphere.qualitis.exception.ArgumentException;
import com.webank.wedatasphere.qualitis.exception.BothNullDatasourceException;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.ConvertException;
import com.webank.wedatasphere.qualitis.exception.DataQualityTaskException;
import com.webank.wedatasphere.qualitis.exception.DataSourceMoveException;
import com.webank.wedatasphere.qualitis.exception.DataSourceOverSizeException;
import com.webank.wedatasphere.qualitis.exception.JobKillException;
import com.webank.wedatasphere.qualitis.exception.JobSubmitException;
import com.webank.wedatasphere.qualitis.exception.LeftNullDatasourceException;
import com.webank.wedatasphere.qualitis.exception.LogPartialException;
import com.webank.wedatasphere.qualitis.exception.NoPartitionException;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.RightNullDatasourceException;
import com.webank.wedatasphere.qualitis.exception.RuleVariableNotFoundException;
import com.webank.wedatasphere.qualitis.exception.RuleVariableNotSupportException;
import com.webank.wedatasphere.qualitis.exception.SystemConfigException;
import com.webank.wedatasphere.qualitis.exception.TaskNotExistException;
import com.webank.wedatasphere.qualitis.exception.TaskTypeException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.job.MonitorManager;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.constant.RuleConstraintEnum;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserColumnByCsRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserTableByCsIdRequest;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.CsTableInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.PartitionStatisticsInfo;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableStatisticsInfo;
import com.webank.wedatasphere.qualitis.parser.HiveSqlParser;
import com.webank.wedatasphere.qualitis.parser.LocaleParser;
import com.webank.wedatasphere.qualitis.project.constant.EventTypeEnum;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.dao.ProjectUserDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.service.ProjectEventService;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.request.ApplicationSubmitRequest;
import com.webank.wedatasphere.qualitis.request.DataSourceExecutionRequest;
import com.webank.wedatasphere.qualitis.request.GeneralExecutionRequest;
import com.webank.wedatasphere.qualitis.request.GetTaskLogRequest;
import com.webank.wedatasphere.qualitis.request.GroupExecutionRequest;
import com.webank.wedatasphere.qualitis.request.ProjectExecutionRequest;
import com.webank.wedatasphere.qualitis.request.RuleListExecutionRequest;
import com.webank.wedatasphere.qualitis.response.ApplicationProjectResponse;
import com.webank.wedatasphere.qualitis.response.ApplicationResultResponse;
import com.webank.wedatasphere.qualitis.response.ApplicationTaskResponse;
import com.webank.wedatasphere.qualitis.response.ApplicationTaskSimpleResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceMapping;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.util.UnitTransfer;
import com.webank.wedatasphere.qualitis.service.OuterExecutionService;
import com.webank.wedatasphere.qualitis.submitter.ExecutionManager;
import com.webank.wedatasphere.qualitis.submitter.impl.ExecutionManagerImpl;
import com.webank.wedatasphere.qualitis.util.DateExprReplaceUtil;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

/**
 * @author howeye
 */
@Service
public class OuterExecutionServiceImpl implements OuterExecutionService {
    @Autowired
    private TaskResultDao taskResultDao;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private RuleGroupDao ruleGroupDao;
    @Autowired
    private ApplicationDao applicationDao;
    @Autowired
    private ProjectUserDao projectUserDao;
    @Autowired
    private ClusterInfoDao clusterInfoDao;
    @Autowired
    private SystemConfigDao systemConfigDao;
    @Autowired
    private RuleDataSourceDao ruleDataSourceDao;
    @Autowired
    private TaskRuleSimpleRepository taskRuleSimpleRepository;
    @Autowired
    private TaskDataSourceRepository taskDataSourceRepository;

    @Autowired
    private MonitorManager monitorManager;
    @Autowired
    private ExecutionManager executionManager;

    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectEventService projectEventService;
    @Autowired
    private OuterExecutionService outerExecutionService;
    @Autowired
    private SystemKeyConfig systemKeyConfig;
    @Autowired
    private MetaDataClient metaDataClient;

    @Autowired
    private LinkisConfig linkisConfig;
    @Autowired
    private LocaleParser localeParser;

    @Context
    private HttpServletRequest httpServletRequest;

    private static final FastDateFormat TASK_TIME_FORMAT = FastDateFormat.getInstance("yyyyMMddHHmmssSSS");
    private static final String USERNAME_FORMAT_PLACEHOLDER = "${USERNAME}";
    private static final String PARTITION = "partition";
    private static final String RUN_DATE = "run_date";

    private static final String DEFAULT_NODE_NAME = "qualitis_0000";
    private static final String NULL_TABLE_SIZE = "0B";
    private static final Integer ORIGINAL_INDEX = -1;
    private static final String AND = "and";


    private static final Logger LOGGER = LoggerFactory.getLogger(OuterExecutionServiceImpl.class);

    public OuterExecutionServiceImpl(@Context HttpServletRequest httpRequest) {
        this.httpServletRequest = httpRequest;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> generalExecution(GeneralExecutionRequest request)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Generator application information
        GeneralExecutionRequest.checkRequest(request);
        if (request.getDyNamicPartitionPrefix() == null) {
            request.setDyNamicPartitionPrefix("");
        }
        if (request.getProjectId() != null) {
            ProjectExecutionRequest projectExecutionRequest = new ProjectExecutionRequest();
            BeanUtils.copyProperties(request, projectExecutionRequest);
            return projectExecution(projectExecutionRequest, InvokeTypeEnum.BDP_CLIENT_API_INVOKE.getCode());
        } else if (CollectionUtils.isNotEmpty(request.getRuleList())) {
            RuleListExecutionRequest ruleListExecutionRequest = new RuleListExecutionRequest();
            BeanUtils.copyProperties(request, ruleListExecutionRequest);
            return ruleListExecution(ruleListExecutionRequest, InvokeTypeEnum.BDP_CLIENT_API_INVOKE.getCode());
        } else if (request.getGroupId() != null) {
            GroupExecutionRequest groupExecutionRequest = new GroupExecutionRequest();
            BeanUtils.copyProperties(request, groupExecutionRequest);
            return groupExecution(groupExecutionRequest, InvokeTypeEnum.FLOW_API_INVOKE.getCode());
        }  else if (StringUtils.isNotBlank(request.getProjectName())) {
            Project projectInDb = projectDao.findByNameAndCreateUser(request.getProjectName(), request.getCreateUser());
            if (projectInDb == null) {
                List<Integer> permissions = new ArrayList<>();
                permissions.add(ProjectUserPermissionEnum.CREATOR.getCode());
                permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
                List<Project> projects = projectUserDao.findByUsernameAndPermission(request.getExecutionUser(), permissions);
                if (CollectionUtils.isEmpty(projects)) {
                    throw new UnExpectedRequestException("Pick project has no permissions.");
                }
                List<Project> execProject = projects.stream().filter(project -> project.getName().equals(request.getProjectName())).collect(
                    Collectors.toList());
                if (CollectionUtils.isEmpty(execProject)) {
                    throw new UnExpectedRequestException("Pick project has no permissions.");
                } else if (execProject.size() > 1) {
                    throw new UnExpectedRequestException("Pick project has dubplicate names.");
                }
                projectInDb = execProject.iterator().next();
            }
            if (CollectionUtils.isNotEmpty(request.getRuleNameList())) {
                List<Long> ruleIds = new ArrayList<>(request.getRuleNameList().size());
                for (String ruleName : request.getRuleNameList()) {
                    Rule rule = ruleDao.findByProjectAndRuleName(projectInDb, ruleName);
                    ruleIds.add(rule.getId());
                }
                request.setRuleList(ruleIds);
                RuleListExecutionRequest ruleListExecutionRequest = new RuleListExecutionRequest();
                BeanUtils.copyProperties(request, ruleListExecutionRequest);
                return ruleListExecution(ruleListExecutionRequest, InvokeTypeEnum.BDP_CLIENT_API_INVOKE.getCode());
            } else {
                request.setProjectId(projectInDb.getId());
                ProjectExecutionRequest projectExecutionRequest = new ProjectExecutionRequest();
                BeanUtils.copyProperties(request, projectExecutionRequest);
                return projectExecution(projectExecutionRequest, InvokeTypeEnum.BDP_CLIENT_API_INVOKE.getCode());
            }
        } else if (StringUtils.isNotBlank(request.getCluster())) {
            DataSourceExecutionRequest dataSourceExecutionRequest = new DataSourceExecutionRequest();
            BeanUtils.copyProperties(request, dataSourceExecutionRequest);
            return dataSourceExecution(dataSourceExecutionRequest);
        } else {
            LOGGER.error("Can not resolve the request: " + request);
            throw new UnExpectedRequestException("{&CAN_NOT_RESOLVE_THE_REQUEST}");
        }

    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> projectExecution(ProjectExecutionRequest request, Integer invokeCode)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        LOGGER.info("Execute application by project. project_id: {}", request.getProjectId());
        // Check Arguments
        ProjectExecutionRequest.checkRequest(request);
        String executionUser = request.getExecutionUser();
        String loginUser = getLoginUser(httpServletRequest, request.getCreateUser(), request.getAsync());

        LOGGER.info("Qualitis execution user: {}", executionUser);
        LOGGER.info("Qualitis login or create user: {}", loginUser);
        // Check existence of project
        Project projectInDb = projectDao.findById(request.getProjectId());
        if (null == projectInDb) {
            throw new UnExpectedRequestException("Project_id " + request.getProjectId() + " {&DOES_NOT_EXIST}");
        }
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.OPERATOR.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);
        checkPermissionCreateUserProxyExecuteUser(request.getCreateUser(), request.getExecutionUser());
        // Find all rule group
        List<RuleGroup> ruleGroups = ruleGroupDao.findByProjectId(projectInDb.getId());
        if (CollectionUtils.isEmpty(ruleGroups)) {
            throw new UnExpectedRequestException("{&NO_RULE_CAN_BE_EXECUTED}");
        }
        LOGGER.info("Succeed to find rule group list from project[id={}], rule group[{}]", projectInDb.getId(), Arrays.toString(ruleGroups.toArray()));
        // Parse partition and run date from execution parameters.
        StringBuffer partition = new StringBuffer();
        StringBuffer runDate = new StringBuffer();

        Map<String, String> execParamMap = new HashMap<>(5);
        parseExecParams(partition, runDate, request.getExecutionParam(), execParamMap);

        ApplicationProjectResponse applicationProjectResponse = new ApplicationProjectResponse();
        List<ApplicationSubmitRequest> applicationSubmitRequests = new ArrayList<>(ruleGroups.size());

        for (RuleGroup ruleGroup : ruleGroups) {
            // Find all rules
            List<Rule> rules = ruleDao.findByRuleGroup(ruleGroup);
            if (CollectionUtils.isEmpty(rules)) {
                ruleGroupDao.delete(ruleGroup);
                continue;
            }
            LOGGER.info("Succeed to get rules from rule group[id={}].", ruleGroup.getId());
            List<Long> ruleIds = rules.stream().map(Rule::getId).collect(Collectors.toList());
            try {
                // Dynamic partition.
                submitRulesWithDynamicPartition(applicationSubmitRequests, projectInDb.getId(), ruleGroup.getId(), rules, ruleIds, executionUser
                    , request.getDyNamicPartition(), request.getClusterName(), partition, request.getDyNamicPartitionPrefix());

                if (CollectionUtils.isEmpty(ruleIds)) {
                    continue;
                }
            } catch (ResourceAccessException e) {
                generateAbnormalApplicationInfo(request.getProjectId(), ruleGroup.getId(), request.getCreateUser(), executionUser
                    , new Date(), invokeCode, partition.toString(), request.getStartupParamName(), request.getExecutionParam(), e, ApplicationCommentEnum.METADATA_ISSUES.getCode()
                    , ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
                LOGGER.error("One group execution[id={}] of the project execution start failed!", ruleGroup.getId());
            } catch (NoPartitionException e) {
                generateAbnormalApplicationInfo(request.getProjectId(), ruleGroup.getId(), request.getCreateUser(), executionUser
                    , new Date(), invokeCode, partition.toString(), request.getStartupParamName(), request.getExecutionParam(), e, ApplicationCommentEnum.METADATA_ISSUES.getCode()
                    , ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
                LOGGER.error("One group execution[id={}] of the project execution start failed!", ruleGroup.getId());
            } catch (Exception e) {
                generateAbnormalApplicationInfo(request.getProjectId(), ruleGroup.getId(), request.getCreateUser(), executionUser
                    , new Date(), invokeCode, partition.toString(), request.getStartupParamName(), request.getExecutionParam(), e, ApplicationCommentEnum.UNKNOWN_ERROR_ISSUES.getCode()
                    , ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
                LOGGER.error("One group execution[id={}] of the project execution start failed!", ruleGroup.getId());
            }

            applicationSubmitRequests.add(new ApplicationSubmitRequest(projectInDb.getId(), ruleGroup.getId(), ruleIds, partition));

        }
        for (ApplicationSubmitRequest applicationSubmitRequest : applicationSubmitRequests) {
            GeneralResponse<?> generalResponse = outerExecutionService.submitRules(applicationSubmitRequest.getRuleIds(), applicationSubmitRequest.getPartition()
                , loginUser, executionUser, DEFAULT_NODE_NAME, projectInDb.getId(), applicationSubmitRequest.getRuleGroupId(), request.getStartupParamName()
                , request.getClusterName(), request.getSetFlag(), execParamMap, request.getExecutionParam(), runDate, invokeCode);
            applicationProjectResponse.getApplicationTaskSimpleResponses().add((ApplicationTaskSimpleResponse) generalResponse.getData());
        }
        // Record project event.
        projectEventService.record(projectInDb.getId(), loginUser, "submit project execution", ".", EventTypeEnum.SUBMIT_PROJECT.getCode());
        return new GeneralResponse<>("200", "{&SUCCEED_TO_DISPATCH_TASK}", applicationProjectResponse);
    }

    private void generateAbnormalApplicationInfo(Long projectId, Long ruleGroupId, String createUser, String executionUser, Date date
        , Integer invokeCode, String partition, String startupParams, String executionParams, Exception e, Integer applicationCommentCode, Integer applicationStatusCode, List<Rule> rules) {
        Application newApplication = outerExecutionService.generateApplicationInfo(createUser, executionUser, date, invokeCode);
        newApplication.setPartition(partition);
        newApplication.setProjectId(projectId);
        newApplication.setRuleGroupId(ruleGroupId);
        newApplication.setStartupParam(startupParams);
        newApplication.setExecutionParam(executionParams);
        catchAndSolve(e, applicationCommentCode, applicationStatusCode, rules, newApplication);
    }

    private void submitRulesWithDynamicPartition(List<ApplicationSubmitRequest> applicationSubmitRequests, Long projectId, Long ruleGroupId
        , List<Rule> rules, List<Long> ruleIds, String executionUser, boolean dynamicPartition, String clusterName, StringBuffer partition
        , String dynamicPartitionPrefix) throws UnExpectedRequestException, NoPartitionException {

        if (dynamicPartition) {
            for (Rule rule : rules) {
                RuleDataSource ruleDataSource = rule.getRuleDataSources().iterator().next();
                if (ruleDataSource == null) {
                    throw new UnExpectedRequestException("Rule datasource has been broken.");
                }
                if (RuleTypeEnum.CUSTOM_RULE.getCode().equals(rule.getRuleType())) {
                    throw new UnExpectedRequestException(RuleTypeEnum.CUSTOM_RULE.getMessage() + " {&IS_NOT_SUPPORT}");
                }
                List<Map> currentPartitionMap;
                try {
                    TableStatisticsInfo tableStatisticsInfo = metaDataClient.getTableStatisticsInfo(StringUtils.isBlank(clusterName) ? ruleDataSource.getClusterName() : clusterName
                        , ruleDataSource.getDbName(), ruleDataSource.getTableName(), executionUser);
                    currentPartitionMap = tableStatisticsInfo.getPartitions();
                    if (StringUtils.isNotEmpty(dynamicPartitionPrefix)) {
                        metaDataClient.getPartitionStatisticsInfo(StringUtils.isBlank(clusterName) ? ruleDataSource.getClusterName() : clusterName
                            , ruleDataSource.getDbName(), ruleDataSource.getTableName(), filterToPartitionPath(dynamicPartitionPrefix), executionUser);
                    }
                } catch (MetaDataAcquireFailedException e) {
                    LOGGER.error("Dynamic submit failed.", e);
                    throw new NoPartitionException(ruleDataSource.getDbName() + "." + ruleDataSource.getTableName() + "{&HAS_NO_PARTITIONS_TO_BE_EXECUTE}");
                }

                List<String> partitionList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(currentPartitionMap)) {
                    ruleIds.remove(rule.getId());

                    getPartitionListAndSubmit(currentPartitionMap, partitionList, rule, partition, filterToPartitionPath(dynamicPartitionPrefix)
                        , applicationSubmitRequests, projectId, ruleGroupId);
                } else {
                    throw new NoPartitionException(ruleDataSource.getDbName() + "." + ruleDataSource.getTableName() + "{&HAS_NO_PARTITIONS_TO_BE_EXECUTE}");
                }
            }
        }
    }

    private void getPartitionListAndSubmit(List<Map> partitions, List<String> partitionList, Rule rule, StringBuffer partition
        , String dynamicPartitionPrefix, List<ApplicationSubmitRequest> applicationSubmitRequests, Long projectId, Long ruleGroupId) throws UnExpectedRequestException {

        partitionPathRecursive(partitions, "", partitionList, dynamicPartitionPrefix);
        // Submit with partiton one by one.
        for (String subPartition : partitionList) {
            StringBuffer subPartitionBuffer = new StringBuffer(subPartition);
            List<Long> oneRuleId = new ArrayList<>(1);
            oneRuleId.add(rule.getId());
            if (partition.length() > 0) {
                subPartitionBuffer.append(" ").append(AND).append(" ").append(partition);
            }
            applicationSubmitRequests.add(new ApplicationSubmitRequest(projectId, ruleGroupId, oneRuleId, subPartitionBuffer));
//            GeneralResponse<?> generalResponse = outerExecutionService.submitRules(oneRuleId, subPartitionBuffer, loginUser, executionUser
//                , DEFAULT_NODE_NAME, projectId, ruleGroupId, "", "", startupParamName, clusterName, setFlag, execParams
//                , "partition:" + subPartitionBuffer.toString(), runDate, invokeCode);
//            applicationTaskSimpleResponses.add((ApplicationTaskSimpleResponse) generalResponse.getData());
//            if (HttpStatus.INTERNAL_SERVER_ERROR.value() == Integer.parseInt(generalResponse.getCode())) {
//                LOGGER.error("One group execution[id={}] of the project execution start failed!", ruleGroupId);
//            }
        }
    }

    private void partitionPathRecursive(List<Map> partitions, String pathPartition, List<String> partitionList, String dynamicPartitionPrefix)
        throws UnExpectedRequestException {
        if (CollectionUtils.isEmpty(partitions)) {
            int lastAndIndex = pathPartition.lastIndexOf(" " + AND);
            String realPartition = pathPartition.substring(0, lastAndIndex);

            if (filterToPartitionPath(realPartition).startsWith(dynamicPartitionPrefix)) {
                partitionList.add(realPartition);
                return;
            } else {
                throw new UnExpectedRequestException("{&PARTITION_ORDER_ERROR}");
            }
        }
        for (Map partitionMap : partitions) {
            String currentPartitionName = (String) partitionMap.get("name");
            String currentPartitionPath = (String) partitionMap.get("partitionPath");
            String currentPartitionPathWithSlash = currentPartitionPath + "/";
            String dynamicPartitionPrefixSlash = dynamicPartitionPrefix + "/";
            boolean hitPrefix = currentPartitionPathWithSlash.startsWith(dynamicPartitionPrefixSlash) || dynamicPartitionPrefixSlash.startsWith(currentPartitionPathWithSlash);
            if (StringUtils.isNotBlank(dynamicPartitionPrefix) && ! hitPrefix) {
                continue;
            }
            String partitionName = currentPartitionName.split("=")[0];
            String value = currentPartitionName.split("=")[1];
            LOGGER.info("Curent partition: " + currentPartitionName);
            List<Map> childrenPartition = (List<Map>) partitionMap.get("childrens");

            if (CollectionUtils.isNotEmpty(childrenPartition)) {
                LOGGER.info("Curent partition: " + currentPartitionName);
                partitionPathRecursive(childrenPartition, pathPartition + partitionName + "=" + "'" + value + "'" + " " + AND + " "
                    , partitionList, dynamicPartitionPrefix);
            } else {
                partitionPathRecursive(null, pathPartition + partitionName + "=" + "'" + value + "'" + " " + AND + " "
                    , partitionList, dynamicPartitionPrefix);
            }
        }
    }

    @Override
    public GeneralResponse<?> groupExecution(GroupExecutionRequest request, Integer invokeCode)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        LOGGER.info("Execute application by group. group_id: {}", request.getGroupId());
        LOGGER.info("Execute application by nodename. nodename: {}", request.getNodeName());
        // Check Arguments
        GroupExecutionRequest.checkRequest(request);
        String loginUser = getLoginUser(httpServletRequest, request.getCreateUser(), request.getAsync());
        // Check existence of project
        RuleGroup ruleGroupInDb = ruleGroupDao.findById(request.getGroupId());
        if (null == ruleGroupInDb) {
            throw new UnExpectedRequestException("Group_id " + request.getGroupId() + " {&DOES_NOT_EXIST}");
        }
        LOGGER.info("Succeed to find rule group. group_id: {}", ruleGroupInDb.getId());

        Project projectInDb = projectDao.findById(ruleGroupInDb.getProjectId());
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.OPERATOR.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);
        checkPermissionCreateUserProxyExecuteUser(request.getCreateUser(), request.getExecutionUser());
        // Find all rules
        List<Rule> rules = ruleDao.findByRuleGroup(ruleGroupInDb);
        if (CollectionUtils.isEmpty(rules)) {
            throw new UnExpectedRequestException("{&NO_RULE_CAN_BE_EXECUTED}");
        }
        List<Long> ruleIds = rules.stream().map(Rule::getId).collect(Collectors.toList());

        StringBuffer runDate = new StringBuffer();
        StringBuffer partition = new StringBuffer();
        Map<String, String> execParamMap = new HashMap<>(5);
        parseExecParams(partition, runDate, request.getExecutionParam(), execParamMap);

        List<ApplicationSubmitRequest> applicationSubmitRequests = new ArrayList<>();
        // Construct dynamic partition.
        try {
            submitRulesWithDynamicPartition(applicationSubmitRequests, projectInDb.getId(), ruleGroupInDb.getId(), rules, ruleIds, request.getExecutionUser()
                , request.getDyNamicPartition(), request.getClusterName(), partition, request.getDyNamicPartitionPrefix());
        } catch (ResourceAccessException e) {
            generateAbnormalApplicationInfo(projectInDb.getId(), request.getGroupId(), request.getCreateUser(), request.getExecutionUser()
                , new Date(), invokeCode, partition.toString(), request.getStartupParamName(), request.getExecutionParam(), e, ApplicationCommentEnum.METADATA_ISSUES.getCode()
                , ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
            LOGGER.error("One group execution[id={}] of the project execution start failed!", request.getGroupId());
        } catch (NoPartitionException e) {
            generateAbnormalApplicationInfo(projectInDb.getId(), request.getGroupId(), request.getCreateUser(), request.getExecutionUser()
                , new Date(), invokeCode, partition.toString(), request.getStartupParamName(), request.getExecutionParam(), e, ApplicationCommentEnum.METADATA_ISSUES.getCode()
                , ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
            LOGGER.error("One group execution[id={}] of the project execution start failed!", request.getGroupId());
        } catch (Exception e) {
            generateAbnormalApplicationInfo(projectInDb.getId(), request.getGroupId(), request.getCreateUser(), request.getExecutionUser()
                , new Date(), invokeCode, partition.toString(), request.getStartupParamName(), request.getExecutionParam(), e, ApplicationCommentEnum.UNKNOWN_ERROR_ISSUES.getCode()
                , ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
            LOGGER.error("One group execution[id={}] of the project execution start failed!", request.getGroupId());
        }
        if (CollectionUtils.isEmpty(ruleIds)) {
            return new GeneralResponse<>("200", "{&SUCCEED_TO_DISPATCH_TASK}", null);
        }

        GeneralResponse<ApplicationTaskSimpleResponse> generalResponse = (GeneralResponse<ApplicationTaskSimpleResponse>) outerExecutionService.submitRules(ruleIds
            , partition, loginUser, request.getExecutionUser(), request.getNodeName(), projectInDb.getId(), ruleGroupInDb.getId(), request.getStartupParamName()
            , request.getClusterName(), request.getSetFlag(), execParamMap, request.getExecutionParam(), runDate, invokeCode);
        // Record project event.
        projectEventService.record(projectInDb.getId(), loginUser, "submit group execution", "group[name="
            + ruleGroupInDb.getRuleGroupName() + "].", EventTypeEnum.SUBMIT_PROJECT.getCode());
        return generalResponse;
    }

    private void parseExecParams(StringBuffer partition, StringBuffer runDate, String execParams, Map<String, String> execParamMap) {
        if (StringUtils.isNotBlank(execParams)) {
            String[] execParamStrs = execParams.split(SpecCharEnum.DIVIDER.getValue());
            for (String str : execParamStrs) {
                String[] strs = str.split(SpecCharEnum.COLON.getValue());
                String execParamKey = strs[0];
                String execParamValue = strs[1];

                if (PARTITION.equals(execParamKey)) {
                    if (partition.length() == 0) {
                        partition.append(execParamValue);
                    } else {
                        partition.append(" " + AND + " ").append(execParamValue);
                    }
                    continue;
                } else if (RUN_DATE.equals(execParamKey)) {
                    if (runDate.length() == 0) {
                        runDate.append(execParamValue);
                    }
                    continue;
                }
                execParamMap.put(str.split(SpecCharEnum.COLON.getValue())[0], str.split(SpecCharEnum.COLON.getValue())[1]);
            }
        }
    }

    @Override
    public GeneralResponse<?> ruleListExecution(RuleListExecutionRequest request, Integer invokeCode)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        LOGGER.info("Execute application by rules. rule_ids: {}", request.getRuleList());
        // Check Arguments.
        RuleListExecutionRequest.checkRequest(request);
        String loginUser = getLoginUser(httpServletRequest, request.getCreateUser(), request.getAsync());
        // Check the existence of rule.
        List<Rule> rules = ruleDao.findByIds(request.getRuleList());
        if (CollectionUtils.isEmpty(rules)) {
            throw new UnExpectedRequestException("{&NO_RULE_CAN_BE_EXECUTED}");
        }
        List<Long> ruleIds = rules.stream().map(Rule::getId).distinct().collect(Collectors.toList());
        // Check rule exists on the request.
        List<Long> notExistRules = request.getRuleList().stream().filter(ruleId -> ! ruleIds.contains(ruleId)).collect(Collectors.toList());

        if (! notExistRules.isEmpty()) {
            throw new UnExpectedRequestException("The ids of rule: " + notExistRules.toString() + " {&DOES_NOT_EXIST}");
        }
        LOGGER.info("Succeed to find all rules.");
        checkPermissionCreateUserProxyExecuteUser(request.getCreateUser(), request.getExecutionUser());
        StringBuffer runDate = new StringBuffer();
        StringBuffer partition = new StringBuffer();
        Map<String, String> execParamMap = new HashMap<>(5);
        parseExecParams(partition, runDate, request.getExecutionParam(), execParamMap);

        ApplicationProjectResponse applicationProjectResponse = new ApplicationProjectResponse();
        List<ApplicationSubmitRequest> applicationSubmitRequests = new ArrayList<>(rules.size());
        List<Project> projects = rules.stream().map(Rule::getProject).distinct().collect(Collectors.toList());

        GeneralResponse<ApplicationTaskSimpleResponse> generalResponse;
        // Check permissions of projects.
        for (Project projectInDb : projects) {
            List<Integer> permissions = new ArrayList<>();
            permissions.add(ProjectUserPermissionEnum.OPERATOR.getCode());
            projectService.checkProjectPermission(projectInDb, loginUser, permissions);

            List<Rule> currentRules = rules.stream().filter(rule -> rule.getProject().getId().equals(projectInDb.getId())).collect(Collectors.toList());
            List<RuleGroup> currentRuleGroups = currentRules.stream().map(Rule::getRuleGroup).distinct().collect(Collectors.toList());

            for (RuleGroup ruleGroup : currentRuleGroups) {
                List<Rule> currentRulesOfGroup = currentRules.stream().filter(rule -> rule.getRuleGroup().getRuleGroupName().equals(ruleGroup.getRuleGroupName()))
                    .distinct().collect(Collectors.toList());
                List<Long> currentRuleIds = currentRulesOfGroup.stream().map(Rule::getId).distinct().collect(Collectors.toList());
                // Dynamic partition.
                try {
                    submitRulesWithDynamicPartition(applicationSubmitRequests, projectInDb.getId(), ruleGroup.getId(), currentRulesOfGroup, currentRuleIds
                        , request.getExecutionUser(), request.getDyNamicPartition(), request.getClusterName(), partition, request.getDyNamicPartitionPrefix());
                    if (CollectionUtils.isEmpty(currentRuleIds)) {
                        continue;
                    }
                } catch (ResourceAccessException e) {
                    // Record submit failed applicatoin.
                    generateAbnormalApplicationInfo(projectInDb.getId(), ruleGroup.getId(), request.getCreateUser(), request.getExecutionUser()
                        , new Date(), invokeCode, partition.toString(), request.getStartupParamName(), request.getExecutionParam(), e, ApplicationCommentEnum.METADATA_ISSUES.getCode()
                        , ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
                    LOGGER.error("One group execution[id={}] of the rule list execution start failed!", ruleGroup.getId());
                } catch (NoPartitionException e) {
                    // Record submit failed applicatoin.
                    generateAbnormalApplicationInfo(projectInDb.getId(), ruleGroup.getId(), request.getCreateUser(), request.getExecutionUser()
                        , new Date(), invokeCode, partition.toString(), request.getStartupParamName(), request.getExecutionParam(), e, ApplicationCommentEnum.METADATA_ISSUES.getCode()
                        , ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
                    LOGGER.error("One group execution[id={}] of the rule list execution start failed!", ruleGroup.getId());
                } catch (Exception e) {
                    // Record submit failed applicatoin.
                    generateAbnormalApplicationInfo(projectInDb.getId(), ruleGroup.getId(), request.getCreateUser(), request.getExecutionUser()
                        , new Date(), invokeCode, partition.toString(), request.getStartupParamName(), request.getExecutionParam(), e, ApplicationCommentEnum.METADATA_ISSUES.getCode()
                        , ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
                    LOGGER.error("One group execution[id={}] of the rule list execution start failed!", ruleGroup.getId());
                }
                applicationSubmitRequests.add(new ApplicationSubmitRequest(projectInDb.getId(), ruleGroup.getId(), currentRuleIds, partition));

            }

            projectEventService.record(projectInDb.getId(), loginUser, "submit rule list execution", "rule name["
                + Arrays.toString(currentRules.toArray()) + "].", EventTypeEnum.SUBMIT_PROJECT.getCode());
        }
        for (ApplicationSubmitRequest applicationSubmitRequest : applicationSubmitRequests) {
            generalResponse = (GeneralResponse<ApplicationTaskSimpleResponse>) outerExecutionService.submitRules(applicationSubmitRequest.getRuleIds()
                , applicationSubmitRequest.getPartition(), loginUser, request.getExecutionUser(), DEFAULT_NODE_NAME, applicationSubmitRequest.getProjectId()
                , applicationSubmitRequest.getRuleGroupId(),request.getStartupParamName(), request.getClusterName(), request.getSetFlag(), execParamMap, request.getExecutionParam()
                , runDate, invokeCode);
            applicationProjectResponse.getApplicationTaskSimpleResponses().add(generalResponse.getData());
        }

        return new GeneralResponse<>("200", "{&SUCCEED_TO_DISPATCH_TASK}", applicationProjectResponse);
    }

    private String getLoginUser(HttpServletRequest httpServletRequest, String createUser, boolean async) {
        if (async) {
            return createUser;
        }
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        if (StringUtils.isBlank(loginUser)) {
            return createUser;
        }
        return loginUser;
    }

    @Override
    public GeneralResponse<?> dataSourceExecution(DataSourceExecutionRequest request)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        LOGGER.info("Execute application by datasource. cluster: {}, database: {}, table: {}", request.getCluster(), request.getDatabase(), request.getTable());
        // Check Arguments.
        DataSourceExecutionRequest.checkRequest(request);
        String loginUser = getLoginUser(httpServletRequest, request.getCreateUser(), request.getAsync());
        // Find all rule datasources by user.
        List<RuleDataSource> ruleDataSources = new ArrayList<>();
        ruleDataSources.addAll(ruleDataSourceDao.findDatasourcesByUser(loginUser, request.getCluster(), request.getDatabase(), request.getTable()));

        List<Rule> rules = ruleDataSources.stream().filter(r ->
            r.getClusterName().equals(request.getCluster())
                && r.getDbName().equals(request.getDatabase())
                && r.getTableName().equals(request.getTable())
        ).map(RuleDataSource::getRule).distinct().filter(rule -> {
            if (! request.getCrossTable()) {
                return rule.getRuleDataSources().size() == 1;
            }
            return true;
        }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(rules)) {
            throw new UnExpectedRequestException("{&NO_RULE_CAN_BE_EXECUTED}");
        }
        checkPermissionCreateUserProxyExecuteUser(request.getCreateUser(), request.getExecutionUser());
        ApplicationProjectResponse applicationProjectResponse = new ApplicationProjectResponse();
        List<ApplicationSubmitRequest> applicationSubmitRequests = new ArrayList<>();
        StringBuffer partition = new StringBuffer();
        StringBuffer runDate = new StringBuffer();
        Map<String, String> execParamMap = new HashMap<>(5);
        parseExecParams(partition, runDate, request.getExecutionParam(), execParamMap);
        List<Project> projects = rules.stream().map(Rule::getProject).distinct().collect(Collectors.toList());

        for (Project projectInDb : projects) {
            // Check permissions of project
            List<Integer> permissions = new ArrayList<>();
            permissions.add(ProjectUserPermissionEnum.OPERATOR.getCode());
            projectService.checkProjectPermission(projectInDb, loginUser, permissions);

            List<Rule> currentRules = rules.stream().filter(rule -> rule.getProject().getId().equals(projectInDb.getId()))
                .collect(Collectors.toList());
            List<RuleGroup> currentRuleGroups = currentRules.stream().map(Rule::getRuleGroup).distinct().collect(Collectors.toList());

            for (RuleGroup ruleGroup : currentRuleGroups) {
                List<Rule> currentRulesOfGroup = currentRules.stream()
                    .filter(rule -> rule.getRuleGroup().getRuleGroupName().equals(ruleGroup.getRuleGroupName()))
                    .distinct().collect(Collectors.toList());
                List<Long> currentRuleIds = currentRulesOfGroup.stream().map(Rule::getId).distinct().collect(Collectors.toList());
                LOGGER.info("Succeed to find current rules of one group with datasource. rule_id: {}", currentRuleIds);

                // Dynamic partition.
                try {
                    submitRulesWithDynamicPartition(applicationSubmitRequests, projectInDb.getId(), ruleGroup.getId(), rules, currentRuleIds, request.getExecutionUser()
                        , request.getDyNamicPartition(), request.getClusterName(), partition, request.getDyNamicPartitionPrefix());
                } catch (ResourceAccessException e) {
                    // Record submit failed applicatoin.
                    generateAbnormalApplicationInfo(projectInDb.getId(), ruleGroup.getId(), request.getCreateUser(), request.getExecutionUser()
                        , new Date(), InvokeTypeEnum.BDP_CLIENT_API_INVOKE.getCode(), partition.toString(), request.getStartupParamName(), request.getExecutionParam()
                        , e, ApplicationCommentEnum.METADATA_ISSUES.getCode(), ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
                    LOGGER.error("One group execution[id={}] of the datasource execution start failed!", ruleGroup.getId());
                } catch (NoPartitionException e) {
                    // Record submit failed applicatoin.
                    generateAbnormalApplicationInfo(projectInDb.getId(), ruleGroup.getId(), request.getCreateUser(), request.getExecutionUser()
                        , new Date(), InvokeTypeEnum.BDP_CLIENT_API_INVOKE.getCode(), partition.toString(), request.getStartupParamName(), request.getExecutionParam()
                        , e, ApplicationCommentEnum.METADATA_ISSUES.getCode(), ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
                    LOGGER.error("One group execution[id={}] of the datasource execution start failed!", ruleGroup.getId());
                } catch (Exception e) {
                    // Record submit failed applicatoin.
                    generateAbnormalApplicationInfo(projectInDb.getId(), ruleGroup.getId(), request.getCreateUser(), request.getExecutionUser()
                        , new Date(), InvokeTypeEnum.BDP_CLIENT_API_INVOKE.getCode(), partition.toString(), request.getStartupParamName(), request.getExecutionParam()
                        , e, ApplicationCommentEnum.UNKNOWN_ERROR_ISSUES.getCode(), ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
                    LOGGER.error("One group execution[id={}] of the datasource execution start failed!", ruleGroup.getId());
                }

                if (CollectionUtils.isNotEmpty(currentRuleIds)) {
                    applicationSubmitRequests.add(new ApplicationSubmitRequest(request.getJobId(), projectInDb.getId(), ruleGroup.getId(),currentRuleIds, partition));
                }
            }
            projectEventService.record(projectInDb.getId(), loginUser, "submit datasource execution", "rule name["
                    + Arrays.toString(rules.stream().filter(rule -> rule.getProject().getId().equals(projectInDb.getId())).toArray()) + "]."
                , EventTypeEnum.SUBMIT_PROJECT.getCode());
        }
        GeneralResponse<ApplicationTaskSimpleResponse> generalResponse;
        for (ApplicationSubmitRequest applicationSubmitRequest : applicationSubmitRequests) {
            if (StringUtils.isNotBlank(request.getJobId())) {
                applicationSubmitRequest.setJobId(request.getJobId());
            }
            generalResponse = (GeneralResponse<ApplicationTaskSimpleResponse>) outerExecutionService.submitRules(applicationSubmitRequest.getRuleIds()
                , applicationSubmitRequest.getPartition(), loginUser, request.getExecutionUser(), DEFAULT_NODE_NAME, applicationSubmitRequest.getProjectId()
                , applicationSubmitRequest.getRuleGroupId(), request.getStartupParamName(), request.getClusterName(), request.getSetFlag(), execParamMap, request.getExecutionParam()
                , runDate, InvokeTypeEnum.BDP_CLIENT_API_INVOKE.getCode());
            applicationProjectResponse.getApplicationTaskSimpleResponses().add(generalResponse.getData());
        }
        return new GeneralResponse<>("200", "{&SUCCEED_TO_DISPATCH_TASK}", applicationProjectResponse);
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
        ApplicationTaskResponse response = new ApplicationTaskResponse(application, tasks);

        LOGGER.info("Succeed to get application status. response: {}", response);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_APPLICATION_STATUS}", response);
    }

    @Override
    public GeneralResponse<?> getApplicationDynamicStatus(String applicationId) throws UnExpectedRequestException {
        // Find application by applicationId
        Application application = applicationDao.findById(applicationId);
        if (application == null) {
            throw new UnExpectedRequestException("Application_id {&DOES_NOT_EXIST}");
        }
        LOGGER.info("Succeed to find application. application: {}", application);

        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_APPLICATION_STATUS}", new ApplicationTaskResponse(application));
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

    @Override
    public GeneralResponse<?> getTaskLog(GetTaskLogRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check Arguments
        GetTaskLogRequest.checkRequest(request);

        Task task = taskDao.findByRemoteTaskIdAndClusterName(request.getTaskId(), request.getClusterId());
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
        if (! request.getCreateUser().equals(task.getApplication().getCreateUser())) {
            throw new UnExpectedRequestException(
                    String.format("User: %s {&HAS_NO_PERMISSION_TO_ACCESS} task: %s", request.getCreateUser(), request.getTaskId()), 403);
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
    public GeneralResponse<?> killApplication(String applicationId, String executionUser)
        throws JobKillException, UnExpectedRequestException, ClusterInfoNotConfigException, PermissionDeniedRequestException {
        LOGGER.info("Qualitis appjoint user: {}", executionUser);
        Application applicationInDb = applicationDao.findById(applicationId);

        if (applicationInDb == null) {
            throw new UnExpectedRequestException(String.format("Application ID: %s {&DOES_NOT_EXIST}", applicationId));
        }
        Integer status = applicationInDb.getStatus();
        if (status.equals(ApplicationStatusEnum.SUBMITTED.getCode()) || status.equals(ApplicationStatusEnum.RUNNING.getCode()) ||
            status.equals(ApplicationStatusEnum.SUCCESSFUL_CREATE_APPLICATION.getCode())) {
            String realExecutionUser = applicationInDb.getExecuteUser();
            checkPermissionCreateUserProxyExecuteUser(executionUser, realExecutionUser);
            return executionManager.killApplication(applicationInDb, realExecutionUser);
        } else {
            return new GeneralResponse<>("400", "{&APPLICATION_IS_FINISHED}", null);
        }
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

    /**
     * Generate jobs by rules and submit jobs to linkis.
     *
     * @param ruleIds
     * @param partition
     * @param createUser
     * @param executionUser
     * @param nodeName
     * @param projectId
     * @param ruleGroupId
     * @param execParams
     * @param runDate
     * @param invokeCode
     * @return
     * @throws UnExpectedRequestException
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> submitRules(List<Long> ruleIds, StringBuffer partition, String createUser, String executionUser, String nodeName
        , Long projectId, Long ruleGroupId, String startupParam, String clusterName, String setFlag, Map<String, String> execParams, String execParamStr
        , StringBuffer runDate, Integer invokeCode) {
        // Get rule eneity.
        List<Rule> rules = ruleDao.findByIds(ruleIds);
        // Init application basic info.
        Date date = new Date();
        Application newApplication = outerExecutionService.generateApplicationInfo(createUser, executionUser, date, invokeCode);
        newApplication.setProjectId(projectId);
        newApplication.setRuleGroupId(ruleGroupId);
        newApplication.setPartition(partition.toString());

        newApplication.setClusterName(clusterName);
        newApplication.setStartupParam(startupParam);
        newApplication.setExecutionParam(execParamStr);

        newApplication.setSetFlag(setFlag);

        ApplicationTaskSimpleResponse response;
        try {
            response = outerExecutionService.commonExecution(rules, partition, executionUser, nodeName, startupParam, clusterName, setFlag, execParams
                , newApplication, date, runDate);
        } catch (BothNullDatasourceException e) {
            catchAndSolve(e, ApplicationCommentEnum.BOTH_NULL_ISSUES.getCode(), ApplicationStatusEnum.FINISHED.getCode(), rules, newApplication);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (LeftNullDatasourceException e) {
            catchAndSolve(e, ApplicationCommentEnum.LEFT_NULL_DATA_ISSUES.getCode(), ApplicationStatusEnum.NOT_PASS.getCode(), rules, newApplication);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (RightNullDatasourceException e) {
            catchAndSolve(e, ApplicationCommentEnum.RIGHT_NULL_DATA_ISSUES.getCode(), ApplicationStatusEnum.NOT_PASS.getCode(), rules, newApplication);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (MetaDataAcquireFailedException e) {
            catchAndSolve(e, ApplicationCommentEnum.METADATA_ISSUES.getCode(), ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules, newApplication);
            return new GeneralResponse<>("500", "{&THE_CHECK_FIELD_HAS_BEEN_MODIFIED}", null);
        } catch (DataSourceMoveException e) {
            catchAndSolve(e, ApplicationCommentEnum.PERMISSION_ISSUES.getCode(), ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules, newApplication);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (DataSourceOverSizeException e) {
            catchAndSolve(e, ApplicationCommentEnum.PERMISSION_ISSUES.getCode(), ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules, newApplication);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (ParseException e) {
            catchAndSolve(e, ApplicationCommentEnum.GRAMMAR_ISSUES.getCode(), ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules, newApplication);
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "{&PARSE_SQL_FAILED_PLEASE_CHECKOUT_YOUR_CUSTOM_SQL}", null);
        } catch (Exception e) {
            catchAndSolve(e, ApplicationCommentEnum.UNKNOWN_ERROR_ISSUES.getCode(), ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules, newApplication);
            return new GeneralResponse<>("500", e.getMessage(), null);
        }
        LOGGER.info("Succeed to dispatch task. response: {}", response);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_DISPATCH_TASK}", response);
    }

    private void catchAndSolve(Exception e, Integer commentCode, Integer statusCode, List<Rule> rules, Application newApplication) {
        LOGGER.error(e.getMessage(), e);
        newApplication.setApplicationComment(commentCode);
        newApplication.setStatus(statusCode);
        // Record rules info and datasource info.
        StringBuffer info = new StringBuffer();
        newApplication.setFinishTime(ExecutionManagerImpl.PRINT_TIME_FORMAT.format(new Date()));

        for (Rule rule : rules) {
            info.append("Rule[").append(rule.getName()).append("; ");
            info.append("datasource: ").append(rule.getRuleDataSources().stream()
                .filter(ruleDataSource -> StringUtils.isNotBlank(ruleDataSource.getDbName()) && StringUtils.isNotBlank(ruleDataSource.getTableName()))
                .map(ruleDataSource -> ruleDataSource.getDbName() + "." + ruleDataSource.getTableName() + " ")
                .collect(Collectors.joining()));
        }
        newApplication.setExceptionMessage(info.append("]\n").append(ExceptionUtils.getStackTrace(e)).toString());

        if (CollectionUtils.isNotEmpty(rules)) {
            Project currentProject = rules.iterator().next().getProject();
            newApplication.setProjectName(StringUtils.isNotBlank(currentProject.getCnName()) ? currentProject.getCnName() : currentProject.getName());

            newApplication.setRuleGroupName(rules.iterator().next().getRuleGroup().getRuleGroupName());
            newApplication.setRuleDatesource(rules.stream().map(Rule::getRuleDataSources).flatMap(ruleDataSources -> ruleDataSources.stream())
                .filter(ruleDataSource -> StringUtils.isNotBlank(ruleDataSource.getDbName()) && StringUtils.isNotBlank(ruleDataSource.getTableName()))
                .map(ruleDataSource -> ruleDataSource.getDbName() + "." + ruleDataSource.getTableName() + " ")
                .collect(Collectors.joining()));
        }
        applicationDao.saveApplication(newApplication);

        LOGGER.info("Succeed to set application status to [{}], application_id: [{}]", newApplication.getStatus(), newApplication.getId());
    }

    @Override
    public Application generateApplicationInfo(String createUser, String executionUser, Date date, Integer invokeCode) {
        String nonce = UuidGenerator.generateRandom(6);
        String applicationId = generateTaskId(date, nonce);
        LOGGER.info("Succeed to generate application_id: {}", applicationId);
        String submitTime = ExecutionManagerImpl.PRINT_TIME_FORMAT.format(date);

        Application application = new Application();
        application.setId(applicationId);
        application.setSubmitTime(submitTime);
        application.setInvokeType(invokeCode);
        application.setCreateUser(createUser);
        application.setExecuteUser(executionUser);
        return applicationDao.saveApplication(application);
    }

    private String generateTaskId(Date date, String nonce) {
        return "QUALITIS" + TASK_TIME_FORMAT.format(date) + "_" + nonce;
    }

    @Override
    public ApplicationTaskSimpleResponse commonExecution(List<Rule> rules, StringBuffer partition, String executionUser, String nodeName, String startupParam
        , String clusterName, String setFlag, Map<String, String> execParams, Application newApplication, Date date, StringBuffer runDate)
        throws RuleVariableNotSupportException, JobSubmitException, RuleVariableNotFoundException, ArgumentException, ConvertException, DataQualityTaskException, TaskTypeException, ClusterInfoNotConfigException, SystemConfigException, UnExpectedRequestException, MetaDataAcquireFailedException, IOException, TaskNotExistException, SemanticException, ParseException, DataSourceMoveException, DataSourceOverSizeException, org.apache.hadoop.hive.ql.parse.ParseException, BothNullDatasourceException, RightNullDatasourceException, LeftNullDatasourceException, java.text.ParseException {
        // current user
        String userName = executionUser;

        // Generate database name.
        String database = generateDatabase(userName);
        LOGGER.info("Succeed to generate database_name: {}", database);

        // Save application
        newApplication.setRuleSize(rules.size());
        newApplication.setSavedDb(database);
        Application saveApplication = applicationDao.saveApplication(newApplication);

        List<TaskSubmitResult> taskSubmitResults = new ArrayList<>();
        List<Rule> fileRules = new ArrayList<>();
        Map<Long, Map> dataSourceMysqlConnect = new HashMap<>(2);
        for (Iterator<Rule> iterator = rules.iterator(); iterator.hasNext();) {
            Rule currentRule = iterator.next();
            if (currentRule.getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
                // Replace with execution parameter and parse datasource to save.
                customReSaveDateSource(currentRule, execParams, clusterName, date);
            }
            List<Map<String, String>> mappingCols = new ArrayList<>();
            getMappingCols(currentRule, mappingCols);
            // Check datasource before submit job.
            try {
                checkDatasource(currentRule, userName, partition, mappingCols, nodeName, clusterName, dataSourceMysqlConnect);
            } catch (BothNullDatasourceException e) {
                Task taskInDb = taskDao.save(new Task(newApplication, newApplication.getSubmitTime(), TaskStatusEnum.SUCCEED.getCode()));
                taskInDb.setClusterName(clusterName);
                TaskRuleSimple taskRuleSimple = new TaskRuleSimple(currentRule, taskInDb);

                Set<TaskDataSource> taskDataSources = new HashSet<>(fileRules.size());
                Set<TaskRuleSimple> taskRuleSimples = new HashSet<>(fileRules.size());

                taskRuleSimples.add(taskRuleSimpleRepository.save(taskRuleSimple));

                for (RuleDataSource ruleDataSource : currentRule.getRuleDataSources()) {
                    taskDataSources.add(taskDataSourceRepository.save(new TaskDataSource(ruleDataSource, taskInDb)));
                }

                taskInDb.setEndTime(ExecutionManagerImpl.PRINT_TIME_FORMAT.format(new Date()));
                taskInDb.setTaskDataSources(taskDataSources);
                taskInDb.setTaskRuleSimples(taskRuleSimples);
                TaskResult taskResult = new TaskResult();

                taskResult.setApplicationId(newApplication.getId());
                taskResult.setCreateTime(newApplication.getSubmitTime());
                taskResult.setRuleId(currentRule.getId());
                taskResult.setResultType("Long");
                taskResult.setValue(0 + "");

                taskResultDao.saveTaskResult(taskResult);
                taskDao.save(taskInDb);
                iterator.remove();

                if (! iterator.hasNext()) {
                    throw e;
                }
                continue;
            }
            if (currentRule.getRuleType().equals(RuleTypeEnum.FILE_TEMPLATE_RULE.getCode())) {
                fileRules.add(currentRule);
                LOGGER.info("Succeed to find file rule. Rule: {}", currentRule.getId() + " " + currentRule.getName());
                iterator.remove();
            }
        }
        String submitTime = ExecutionManagerImpl.PRINT_TIME_FORMAT.format(date);
        // General task.
        if (! rules.isEmpty()) {
            taskSubmitResults.addAll(executionManager.submitApplication(rules, nodeName, submitTime, userName, database,
                partition, date, saveApplication, clusterName, startupParam, setFlag, execParams, runDate, dataSourceMysqlConnect));
        }
        // Execute file rule task and save task result.
        if (! fileRules.isEmpty()) {
            taskSubmitResults.add(executionManager.executeFileRule(fileRules, submitTime, saveApplication, userName, clusterName, runDate));
        }
        saveApplication.setTotalTaskNum(taskSubmitResults.size());
        LOGGER.info("Succeed to submit application. result: {}", taskSubmitResults);
        Application applicationInDb = applicationDao.saveApplication(saveApplication);
        LOGGER.info("Succeed to save application. application: {}", applicationInDb);
        return new ApplicationTaskSimpleResponse(taskSubmitResults);
    }

    private void customReSaveDateSource(Rule currentRule, Map<String, String> execParams, String clusterName, Date date)
        throws UnExpectedRequestException, SemanticException, ParseException {
        String midTableAction = currentRule.getTemplate().getMidTableAction();
        if (StringUtils.isNotBlank(currentRule.getWhereContent())) {
            midTableAction = midTableAction.replace("${filter}", currentRule.getWhereContent());
        }
        // Replace placeholder with key and value for sql parse check.
        for (String key : execParams.keySet()) {
            midTableAction = midTableAction.replace("${" + key + "}", execParams.get(key));
        }
        midTableAction = DateExprReplaceUtil.replaceRunDate(date, midTableAction);
        // Save datasource in first execution(Only possible data sources related to fps).
        boolean firstExecution = CollectionUtils.isEmpty(currentRule.getRuleDataSources().stream().filter(ruleDataSource -> ! ORIGINAL_INDEX.equals(ruleDataSource.getDatasourceIndex())).collect(
            Collectors.toSet()));
        if (! firstExecution) {
            return;
        }
        RuleDataSource originalRuleDataSource = currentRule.getRuleDataSources().stream()
            .filter(ruleDataSource -> ORIGINAL_INDEX.equals(ruleDataSource.getDatasourceIndex())).iterator().next();
        if (StringUtils.isEmpty(clusterName)) {
            clusterName = originalRuleDataSource.getClusterName();
        }
        Map<String, List<String>> dbAndTables = new HashMap<>(2);
        // 0.16.0 for mysql oracle, hive parse, use druid.
        LOGGER.info("Parse sql: " + midTableAction);
        List<RuleMetric> ruleMetrics = currentRule.getAlarmConfigs().stream().map(AlarmConfig::getRuleMetric).distinct().collect(Collectors.toList());
        Set<String> ruleMetricNames = ruleMetrics.stream().map(RuleMetric::getName).collect(Collectors.toSet());
        for (String ruleMetricName : ruleMetricNames) {
            String cleanRuleMetricName = ruleMetricName.replace("-", "_");
            midTableAction = midTableAction.replace(ruleMetricName, cleanRuleMetricName);
        }
        LOGGER.info("Parse sql after replace rule metric names: " + midTableAction);
        if (originalRuleDataSource.getLinkisDataSourceId() != null) {
            DbType dbType =  JdbcConstants.MYSQL;
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            List<SQLStatement> stmtList = SQLUtils.parseStatements(midTableAction, dbType);

            for (int i = 0; i < stmtList.size(); i++) {
                SQLStatement stmt = stmtList.get(i);
                stmt.accept(visitor);
                Map<Name, TableStat> tableStatMap = visitor.getTables();

                dbAndTables = toDbAndTables(dbAndTables, tableStatMap);
            }
        } else {
            HiveSqlParser hiveSqlParser = new HiveSqlParser();
            dbAndTables = hiveSqlParser.checkSelectSqlAndGetDbAndTable(midTableAction);
        }

        LOGGER.info("Db and tables: " + dbAndTables.toString());
        for (String db : dbAndTables.keySet()) {
            List<RuleDataSource> ruleDataSources = new ArrayList<>();
            for (String table : dbAndTables.get(db)) {
                RuleDataSource ruleDataSource = new RuleDataSource();
                ruleDataSource.setClusterName(clusterName);
                ruleDataSource.setProjectId(currentRule.getProject().getId());
                ruleDataSource.setDbName(db.toLowerCase());
                ruleDataSource.setRule(currentRule);
                ruleDataSource.setTableName(table);
                ruleDataSource.setProxyUser(originalRuleDataSource.getProxyUser());
                ruleDataSource.setLinkisDataSourceId(originalRuleDataSource.getLinkisDataSourceId());
                ruleDataSource.setLinkisDataSourceName(originalRuleDataSource.getLinkisDataSourceName());
                ruleDataSource.setLinkisDataSourceVersionId(originalRuleDataSource.getLinkisDataSourceVersionId());
                ruleDataSources.add(ruleDataSource);
            }
            Set<RuleDataSource> ruleDataSourcesExist = currentRule.getRuleDataSources();
            ruleDataSourcesExist.addAll(ruleDataSourceDao.saveAllRuleDataSource(ruleDataSources).stream().collect(Collectors.toSet()));
            currentRule.setRuleDataSources(ruleDataSourcesExist);
            ruleDao.saveRule(currentRule);
        }
    }

    private Map<String, List<String>> toDbAndTables(Map<String, List<String>> dbAndTableMap, Map<Name, TableStat> tableStatMap) {
        for (Name dbAndTable : tableStatMap.keySet()) {
            String[] dbAndTables = dbAndTable.getName().split("\\.");
            String db = dbAndTables[0];
            String table = dbAndTables[1];
            if (dbAndTableMap.containsKey(db)) {
                dbAndTableMap.get(db).add(table);
            } else {
                List<String> tableList = new ArrayList<>();
                tableList.add(table);
                dbAndTableMap.put(db, tableList);
            }
        }
        return dbAndTableMap;
    }

    private void checkDatasource(Rule currentRule, String userName, StringBuffer partition, List<Map<String, String>> mappingCols, String nodeName
        , String clusterName, Map<Long, Map> dataSourceMysqlConnect) throws UnExpectedRequestException, MetaDataAcquireFailedException, DataSourceOverSizeException, DataSourceMoveException, BothNullDatasourceException, LeftNullDatasourceException, RightNullDatasourceException {
        // For multi source rule to check tables' size before submit.
        List<Double> datasourceSizeList = new ArrayList<>(currentRule.getRuleDataSources().size());
        for (RuleDataSource ruleDataSource : currentRule.getRuleDataSources()) {
            Map<String, String> mappingCol = null;
            if (ORIGINAL_INDEX.equals(ruleDataSource.getDatasourceIndex())) {
                continue;
            }
            if (ruleDataSource.getDatasourceIndex() != null && mappingCols.get(ruleDataSource.getDatasourceIndex()).size() > 0) {
                mappingCol = mappingCols.get(ruleDataSource.getDatasourceIndex());
            }
            if (ruleDataSource.getLinkisDataSourceId() != null) {
                LOGGER.info("Start to solve relationship datasource info.");
                checkRdmsSqlMetaInfo(StringUtils.isNotBlank(clusterName) ? clusterName : ruleDataSource.getClusterName(), userName, ruleDataSource, mappingCol);
                GeneralResponse<Map> dataSourceInfoDetail = metaDataClient.getDataSourceInfoDetail(StringUtils.isNotBlank(clusterName) ? clusterName : ruleDataSource.getClusterName(), userName, ruleDataSource.getLinkisDataSourceId(), ruleDataSource.getLinkisDataSourceVersionId());
                GeneralResponse<Map> dataSourceConnectParams = metaDataClient.getDataSourceConnectParams(StringUtils.isNotBlank(clusterName) ? clusterName : ruleDataSource.getClusterName(), userName, ruleDataSource.getLinkisDataSourceId(), ruleDataSource.getLinkisDataSourceVersionId());
                Map connectParamsReal = (Map) dataSourceConnectParams.getData().get("connectParams");
                if (connectParamsReal.size() == 0) {
                    throw new UnExpectedRequestException("{&THE_DATASOURCE_IS_NOT_DEPLOYED}");
                }
                Map connectParams = (Map) ((Map) dataSourceInfoDetail.getData().get("info")).get("connectParams");
                String dataType =(String) ((Map) ((Map) dataSourceInfoDetail.getData().get("info")).get("dataSourceType")).get("name");
                connectParams.put("dataType", dataType);
                dataSourceMysqlConnect.put(ruleDataSource.getId(), connectParams);
                continue;
            }

            // Parse filter fields.
            List<String> filterFields = getFilterFields(partition.toString());
            if (StringUtils.isNotBlank(ruleDataSource.getDbName()) && ! ruleDataSource.getDbName().equals(RuleConstraintEnum.CUSTOM_DATABASE_PREFIS.getValue())) {
                // Get actual fields info.
                List<ColumnInfoDetail> cols = metaDataClient.getColumnInfo(StringUtils.isNotBlank(clusterName) ? clusterName : ruleDataSource.getClusterName()
                    , ruleDataSource.getDbName(), ruleDataSource.getTableName(), userName);
                if (CollectionUtils.isEmpty(cols)) {
                    throw new DataSourceMoveException("Table[" + ruleDataSource.getTableName() + "]. {&RULE_DATASOURCE_BE_MOVED}");
                }
                // Get actual partition fields.
                List<String> partitionFields = cols.stream().filter(ColumnInfoDetail::getPartitionField).map(ColumnInfoDetail::getFieldName)
                    .collect(Collectors.toList());
                // Check filter fields.
                boolean partitionTable = CollectionUtils.isNotEmpty(partitionFields);
                if (partitionTable && partition.length() > 0) {
                    for (String filter : filterFields) {
                        if (! partitionFields.contains(filter)) {
                            throw new UnExpectedRequestException("Table[" + ruleDataSource.getTableName() + "]. {&THE_CHECK_FIELD_DOES_NOT_EXIST_IN_PARTITIONS}[" + filter + "]");
                        }
                    }
                    // Check partition size.
                    PartitionStatisticsInfo partitionStatisticsInfo = metaDataClient.getPartitionStatisticsInfo(StringUtils.isNotBlank(clusterName) ? clusterName : ruleDataSource.getClusterName()
                        , ruleDataSource.getDbName(), ruleDataSource.getTableName(), filterToPartitionPath(partition.toString()), userName);
                    String fullSize = partitionStatisticsInfo.getPartitionSize();

                    ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(StringUtils.isNotBlank(clusterName) ? clusterName : ruleDataSource.getClusterName());
                    if (clusterInfo != null && StringUtils.isNotBlank(clusterInfo.getSkipDataSize()) && StringUtils.isNotBlank(fullSize)) {
                        double number = 0;
                        String unit = "B";
                        if (! "0B".equals(fullSize)) {
                            number = Double.parseDouble(fullSize.split(" ")[0]);
                            unit = fullSize.split(" ")[1];
                        }
                        datasourceSizeList.add(number);
                        String[] skipDataSize = clusterInfo.getSkipDataSize().split(" ");
                        double res = UnitTransfer.alarmconfigToTaskResult(number, skipDataSize[1], unit);
                        LOGGER.info("Check datasource[" + fullSize + "] if or not oversize with system config[" + clusterInfo.getSkipDataSize() + "]");
                        if (res > Double.parseDouble(skipDataSize[0])) {
                            throw new DataSourceOverSizeException("Table[" + ruleDataSource.getTableName() + "]. {&TABLE_IS_OVERSIZE_WITH_SYSTEM_CONFIG}:[" + clusterInfo.getSkipDataSize() + "]");
                        }
                    }

                } else {
                    // Check table size.
                    TableStatisticsInfo tableStatisticsInfo = metaDataClient.getTableStatisticsInfo(StringUtils.isNotBlank(clusterName) ? clusterName : ruleDataSource.getClusterName()
                        , ruleDataSource.getDbName(), ruleDataSource.getTableName(), userName);
                    String fullSize = tableStatisticsInfo.getTableSize();
                    if (NULL_TABLE_SIZE.equals(fullSize)) {
                        throw new DataSourceMoveException("Table[" + ruleDataSource.getTableName() + "] {&RULE_DATASOURCE_BE_MOVED}");
                    }

                    ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(StringUtils.isNotBlank(clusterName) ? clusterName : ruleDataSource.getClusterName());
                    if (clusterInfo != null && StringUtils.isNotBlank(clusterInfo.getSkipDataSize()) && StringUtils.isNotBlank(fullSize)) {
                        LOGGER.info("Check datasource[" + fullSize + "] if or not oversize with system config[" + clusterInfo.getSkipDataSize() + "]");
                        double number = 0;
                        String unit = "B";
                        if (! "0B".equals(fullSize)) {
                            number = Double.parseDouble(fullSize.split(" ")[0]);
                            unit = fullSize.split(" ")[1];
                        }
                        datasourceSizeList.add(number);
                        String[] skipDataSize = clusterInfo.getSkipDataSize().split(" ");
                        double res = UnitTransfer.alarmconfigToTaskResult(number, skipDataSize[1], unit);
                        if (res > Double.parseDouble(skipDataSize[0])) {
                            throw new DataSourceOverSizeException("Table[" + ruleDataSource.getTableName() + "] is oversize with system config:[" + clusterInfo.getSkipDataSize() + "]");
                        }
                    }
                    partition.delete(0, partition.length());
                }
                if (currentRule.getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
                    continue;
                }
                if (! metaDataClient.fieldExist(ruleDataSource.getColName(), cols, mappingCol)) {
                    throw new DataSourceMoveException("Table[" + ruleDataSource.getTableName() + "] {&RULE_DATASOURCE_BE_MOVED}");
                }
            } else if (StringUtils.isNotBlank(currentRule.getCsId())){
                checkDatasourceInContextService(ruleDataSource, mappingCol, clusterName, userName, nodeName, currentRule.getCsId());
            }
        }
        if (CollectionUtils.isNotEmpty(datasourceSizeList) && currentRule.getRuleType().equals(RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode())) {
            double left = datasourceSizeList.get(0);
            double right = datasourceSizeList.get(1);
            LOGGER.info("Current multi source rule left table size number[{}], right table size number[{}]", left, right);
            if (left == 0 && right == 0) {
                throw new BothNullDatasourceException("{&BOTH_SIDE_ARE_NULL}");
            } else if (left == 0) {
                throw new LeftNullDatasourceException("{&ONE_SIDE_ARE_NULL}");
            } else if (right == 0) {
                throw new RightNullDatasourceException("{&ONE_SIDE_ARE_NULL}");
            }
        }
    }

    private void checkRdmsSqlMetaInfo(String clusterName, String userName, RuleDataSource ruleDataSource, Map<String, String> mappingCol)
        throws UnExpectedRequestException, MetaDataAcquireFailedException, DataSourceMoveException {
        DataInfo<ColumnInfoDetail> cols = metaDataClient.getColumnsByDataSource(clusterName, userName, ruleDataSource.getLinkisDataSourceId(), ruleDataSource.getDbName(), ruleDataSource.getTableName());
        if (cols == null || CollectionUtils.isEmpty(cols.getContent())) {
            throw new DataSourceMoveException("Table[" + ruleDataSource.getTableName() + "] {&RULE_DATASOURCE_BE_MOVED}");
        }
        if (! metaDataClient.fieldExist(ruleDataSource.getColName(), cols.getContent(), mappingCol)) {
            throw new DataSourceMoveException("Table[" + ruleDataSource.getTableName() + "] {&RULE_DATASOURCE_BE_MOVED}");
        }
    }

    private void checkDatasourceInContextService(RuleDataSource ruleDataSource, Map<String, String> mappingCol, String clusterName, String userName
        , String nodeName, String csId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        GetUserTableByCsIdRequest getUserTableByCsIdRequest = new GetUserTableByCsIdRequest();
        getUserTableByCsIdRequest.setClusterName(StringUtils.isNotBlank(clusterName) ? clusterName : ruleDataSource.getClusterName());
        getUserTableByCsIdRequest.setLoginUser(userName);
        getUserTableByCsIdRequest.setCsId(csId);
        if (StringUtils.isBlank(nodeName)) {
            getUserTableByCsIdRequest.setNodeName(RuleConstraintEnum.DEFAULT_NODENAME.getValue());
        } else {
            getUserTableByCsIdRequest.setNodeName(nodeName);
        }
        DataInfo<CsTableInfoDetail> csTableInfoDetails = metaDataClient.getTableByCsId(getUserTableByCsIdRequest);
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
                List<ColumnInfoDetail> cols = metaDataClient.getColumnByCsId(getUserColumnByCsRequest).getContent();
                if (ruleDataSource.getRule().getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
                    continue;
                }
                if (! metaDataClient.fieldExist(ruleDataSource.getColName(), cols, mappingCol)) {
                    throw new UnExpectedRequestException("Table[" + ruleDataSource.getTableName() + ":" + ruleDataSource.getColName() + "] {&RULE_DATASOURCE_BE_MOVED}");
                }
            }
        }

    }

    private String filterToPartitionPath(String filter) {
        List<String> partitionPath = new ArrayList<>();
        String[] conditions = filter.split(" " + AND + " ");
        for (String condition : conditions) {
            if (StringUtils.isBlank(condition)) {
                continue;
            }
            condition = condition.trim().replace("'", "");
            partitionPath.add("/" + condition);
        }
        return StringUtils.join(partitionPath.toArray());
    }

    private List<String> getFilterFields(String filters) {
        List<String> filterFields = new ArrayList<>();
        if (StringUtils.isNotBlank(filters)) {
            filters = filters.toLowerCase().trim();
            if (filters.contains(" " + AND + " ")) {
                List<String> subPartition = Arrays.asList(filters.split(" " + AND + " "));
                for (String sub : subPartition) {
                    sub = sub.trim();
                    String filterField = sub.substring(0, sub.indexOf("="));
                    filterFields.add(filterField);
                }
            } else {
                String filterField = filters.substring(0, filters.indexOf("="));
                filterFields.add(filterField);
            }
        }

        return filterFields;
    }

    private void getMappingCols(Rule currentRule, List<Map<String, String>> mappingCols) {
        Map<String, String> leftCols = new LinkedHashMap<>();
        Map<String, String> rightCols = new LinkedHashMap<>();
        for (RuleDataSourceMapping ruleDataSourceMapping : currentRule.getRuleDataSourceMappings()) {
            if (StringUtils.isBlank(ruleDataSourceMapping.getLeftColumnNames())
                || StringUtils.isBlank(ruleDataSourceMapping.getLeftColumnTypes())
                || StringUtils.isBlank(ruleDataSourceMapping.getRightColumnNames())
                || StringUtils.isBlank(ruleDataSourceMapping.getRightColumnTypes())) {
                break;
            }
            String[] leftJoinCols = ruleDataSourceMapping.getLeftColumnNames().split(",");
            String[] leftJoinTypes = ruleDataSourceMapping.getLeftColumnTypes().split("\\|");
            for (int i = 0; i < leftJoinCols.length; i ++) {
                leftCols.put(leftJoinCols[i].split("\\.")[1], leftJoinTypes[i]);
            }
            String[] rightJoinCols = ruleDataSourceMapping.getRightColumnNames().split(",");
            String[] rightJoinTypes = ruleDataSourceMapping.getRightColumnTypes().split("\\|");
            for (int i = 0; i < rightJoinCols.length; i ++) {
                rightCols.put(rightJoinCols[i].split("\\.")[1], rightJoinTypes[i]);
            }
        }
        mappingCols.add(leftCols);
        mappingCols.add(rightCols);
    }

    private String generateDatabase(String username) throws SystemConfigException {
        SystemConfig systemConfigInDb = systemConfigDao.findByKeyName(systemKeyConfig.getSaveDatabasePattern());

        if (systemConfigInDb == null || StringUtils.isBlank(systemConfigInDb.getValue())) {
            throw new SystemConfigException(String.format("System Config: %s, can not be null or empty", systemKeyConfig.getSaveDatabasePattern()));
        }

        return systemConfigInDb.getValue().replace(USERNAME_FORMAT_PLACEHOLDER, username);
    }

    private void checkPermissionCreateUserProxyExecuteUser(String createUser, String executeUser)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        User userInDb = userDao.findByUsername(createUser);
        if (userInDb == null) {
            throw new UnExpectedRequestException("Create user: "+ createUser + " {&DOES_NOT_EXIST}");
        }

        if (createUser.equals(executeUser)) {
            LOGGER.info("CreateUser is equals to execute user, pass permission, user: {}", createUser);
            return;
        }

        if (CollectionUtils.isEmpty(userInDb.getUserProxyUsers())) {
            throw new PermissionDeniedRequestException("Create user: " + createUser + " {&HAS_NO_PERMISSION_PROXYING_EXECUTE_USER}: " + executeUser, 403);
        }

        for (UserProxyUser userProxyUser :userInDb.getUserProxyUsers()) {
            if (userProxyUser.getProxyUser().getProxyUserName().equals(executeUser)) {
                LOGGER.info("Create user has permission proxying execute user, pass permission, user: {}, execute user: {}",
                        createUser, executeUser);
                return;
            }
        }
        throw new PermissionDeniedRequestException("Create user: " + createUser + " {&HAS_NO_PERMISSION_PROXYING_EXECUTE_USER}: " + executeUser, 403);
    }

}
