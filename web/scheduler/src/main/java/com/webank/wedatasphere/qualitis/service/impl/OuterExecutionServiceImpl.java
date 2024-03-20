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
import com.alibaba.excel.support.ExcelTypeEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.webank.wedatasphere.qualitis.EngineTypeEnum;
import com.webank.wedatasphere.qualitis.bean.LogResult;
import com.webank.wedatasphere.qualitis.bean.TaskSubmitResult;
import com.webank.wedatasphere.qualitis.checkalert.dao.CheckAlertDao;
import com.webank.wedatasphere.qualitis.checkalert.dao.repository.CheckAlertWhiteListRepository;
import com.webank.wedatasphere.qualitis.checkalert.entity.CheckAlert;
import com.webank.wedatasphere.qualitis.checkalert.entity.CheckAlertWhiteList;
import com.webank.wedatasphere.qualitis.client.impl.ImsAlarmClient;
import com.webank.wedatasphere.qualitis.concurrent.RuleContext;
import com.webank.wedatasphere.qualitis.concurrent.RuleContextManager;
import com.webank.wedatasphere.qualitis.config.ImsConfig;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.config.SystemKeyConfig;
import com.webank.wedatasphere.qualitis.constant.*;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.constants.WhiteListTypeEnum;
import com.webank.wedatasphere.qualitis.dao.*;
import com.webank.wedatasphere.qualitis.dao.repository.TaskDataSourceRepository;
import com.webank.wedatasphere.qualitis.dto.SubmitRuleBaseInfo;
import com.webank.wedatasphere.qualitis.entity.*;
import com.webank.wedatasphere.qualitis.exception.*;
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
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.dao.ProjectUserDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.request.*;
import com.webank.wedatasphere.qualitis.response.*;
import com.webank.wedatasphere.qualitis.rule.constant.GroupTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateDataSourceTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.ExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.entity.*;
import com.webank.wedatasphere.qualitis.rule.service.*;
import com.webank.wedatasphere.qualitis.rule.util.UnitTransfer;
import com.webank.wedatasphere.qualitis.scheduled.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.service.OuterExecutionService;
import com.webank.wedatasphere.qualitis.service.TaskService;
import com.webank.wedatasphere.qualitis.submitter.ExecutionManager;
import com.webank.wedatasphere.qualitis.util.*;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Service
public class OuterExecutionServiceImpl implements OuterExecutionService {
    @Autowired
    private ImsAlarmClient imsAlarmClient;
    @Autowired
    private ImsConfig imsConfig;
    @Autowired
    private AlarmInfoDao alarmInfoDao;
    @Autowired
    private CheckAlertDao checkAlertDao;
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
    private ServiceInfoDao serviceInfoDao;
    @Autowired
    private SystemConfigDao systemConfigDao;
    @Autowired
    private RuleDataSourceDao ruleDataSourceDao;
    @Autowired
    private GatewayJobInfoDao gatewayJobInfoDao;
    @Autowired
    private ExecutionParametersDao executionParametersDao;
    @Autowired
    private AbnormalDataRecordInfoDao abnormalDataRecordInfoDao;
    @Autowired
    private CheckAlertWhiteListRepository checkAlertWhiteListRepository;
    @Autowired
    private TaskDataSourceRepository taskDataSourceRepository;
    @Autowired
    private TaskRuleSimpleDao taskRuleSimpleDao;
    @Autowired
    private RuleMetricDao ruleMetricDao;

    @Autowired
    private MonitorManager monitorManager;
    @Autowired
    private ExecutionManager executionManager;

    @Autowired
    private FpsService fpsService;
    @Autowired
    private ProjectService projectService;
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
    @Autowired
    private RuleDataSourceService ruleDataSourceService;

    @Autowired
    private TaskService taskService;

    @Value("${task.create_and_submit.limit_size:1000}")
    private Long thresholdValue;

//    @Autowired
//    private ApplicationCommentDao applicationCommentDao;

    @Context
    private HttpServletRequest httpServletRequest;

    private static SecureRandom secureRandom = new SecureRandom();

    private static final FastDateFormat TASK_TIME_FORMAT = FastDateFormat.getInstance("yyyyMMddHHmmssSSS");

    private static final String USERNAME_FORMAT_PLACEHOLDER = "${USERNAME}";
    private static final String PARTITION = "partition";
    private static final String RUN_DATE = "run_date";
    private static final String SPLIT_BY = "split_by";
    private static final String ENGINE_REUSE = "engine_reuse";
    private static final Gson GSON = new Gson();

    private static final String EXECUTION_PARAM = "execution_param";
    private static final String SET_FLAG = "set_flag";
    private static final String FPS_ID = "fps_id";
    private static final String FPS_HASH = "fps_hash";

    private static final String NULL_TABLE_SIZE = "0B";
    private static final Integer ORIGINAL_INDEX = -1;
    private static final String AND = "and";
    private static final Integer DATASOURCE_SIZE = 2;

    private static final Map<Integer, Integer> ERR_CODE_TYPE = new HashMap<Integer, Integer>(1);

    static {
        ERR_CODE_TYPE.put(50079, 14);
    }

    @Autowired
    private ApplicationCommentDao applicationCommentDao;
    private static final List<ApplicationComment> APPLICATION_COMMENT_LIST = Lists.newArrayList();

    @PostConstruct
    public void init() {
        List<ApplicationComment> allApplicationComment = applicationCommentDao.findAllApplicationComment();
        if (CollectionUtils.isNotEmpty(allApplicationComment)) {
            APPLICATION_COMMENT_LIST.addAll(allApplicationComment);
        }

    }


    private static final Logger LOGGER = LoggerFactory.getLogger(OuterExecutionServiceImpl.class);

    public OuterExecutionServiceImpl(@Context HttpServletRequest httpRequest) {
        this.httpServletRequest = httpRequest;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse generalExecution(GeneralExecutionRequest request, String loginUser) throws UnExpectedRequestException, PermissionDeniedRequestException {
        Boolean exceedTaskSize = taskService.getExecutingTaskNumber(-24) >= thresholdValue;
        if (exceedTaskSize) {
            return new GeneralResponse<>("5001", "Number of task exceeded limit", null);
        }
        // Generator application information
        GeneralExecutionRequest.checkRequest(request);
        if (request.getDyNamicPartitionPrefix() == null) {
            request.setDyNamicPartitionPrefix("");
        }
        if (request.getProjectId() != null) {
            ProjectExecutionRequest projectExecutionRequest = new ProjectExecutionRequest();
            BeanUtils.copyProperties(request, projectExecutionRequest);
            return projectExecution(projectExecutionRequest, InvokeTypeEnum.BDP_CLIENT_API_INVOKE.getCode(), loginUser);
        } else if (CollectionUtils.isNotEmpty(request.getRuleList())) {
            RuleListExecutionRequest ruleListExecutionRequest = new RuleListExecutionRequest();
            BeanUtils.copyProperties(request, ruleListExecutionRequest);
            return ruleListExecution(ruleListExecutionRequest, InvokeTypeEnum.BDP_CLIENT_API_INVOKE.getCode(), loginUser);
        } else if (request.getGroupId() != null) {
            GroupExecutionRequest groupExecutionRequest = new GroupExecutionRequest();
            BeanUtils.copyProperties(request, groupExecutionRequest);
            return groupExecution(groupExecutionRequest, InvokeTypeEnum.FLOW_API_INVOKE.getCode(), loginUser);
        } else if (StringUtils.isNotBlank(request.getProjectName())) {
            Project projectInDb = projectDao.findByNameAndCreateUser(request.getProjectName(), request.getCreateUser());
            if (projectInDb == null) {
                List<Integer> permissions = new ArrayList<>();
                permissions.add(ProjectUserPermissionEnum.CREATOR.getCode());
                permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
                List<Project> projects = projectUserDao.findByUsernameAndPermissions(request.getExecutionUser(), permissions);
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
                return ruleListExecution(ruleListExecutionRequest, InvokeTypeEnum.BDP_CLIENT_API_INVOKE.getCode(), loginUser);
            } else if (StringUtils.isNotEmpty(request.getRuleGroupName())) {
                RuleGroup ruleGroupInDb = ruleGroupDao.findLatestVersionByRuleGroupNameAndProjectId(request.getRuleGroupName(), projectInDb.getId());
                if (ruleGroupInDb == null) {
                    throw new UnExpectedRequestException("Pick group does not exist.");
                }
                GroupExecutionRequest groupExecutionRequest = new GroupExecutionRequest();

                request.setGroupId(ruleGroupInDb.getId());
                BeanUtils.copyProperties(request, groupExecutionRequest);
                return groupExecution(groupExecutionRequest, InvokeTypeEnum.BDP_CLIENT_API_INVOKE.getCode(), loginUser);
            } else {
                request.setProjectId(projectInDb.getId());
                ProjectExecutionRequest projectExecutionRequest = new ProjectExecutionRequest();
                BeanUtils.copyProperties(request, projectExecutionRequest);
                return projectExecution(projectExecutionRequest, InvokeTypeEnum.BDP_CLIENT_API_INVOKE.getCode(), loginUser);
            }
        } else if (StringUtils.isNotBlank(request.getCluster())) {
            DataSourceExecutionRequest dataSourceExecutionRequest = new DataSourceExecutionRequest();
            BeanUtils.copyProperties(request, dataSourceExecutionRequest);
            return dataSourceExecution(dataSourceExecutionRequest, loginUser);
        } else {
            LOGGER.error("Can not resolve the request: " + request);
            throw new UnExpectedRequestException("{&CAN_NOT_RESOLVE_THE_REQUEST}");
        }

    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse projectExecution(ProjectExecutionRequest request, Integer invokeCode, String landUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException {
        LOGGER.info("Execute application by project. project_id: {}", request.getProjectId());
        // Check Arguments
        ProjectExecutionRequest.checkRequest(request);
        String executionUser = request.getExecutionUser();
        String loginUser = getLoginUser(landUser, request.getCreateUser(), request.getAsync());

        LOGGER.info("Execute parameter entry. execution_param: {}", request.getExecutionParam());
        // Parse set flag in execution parameters, such as: qualitis.spark.set.xx=xx
        Map<String, Object> resultMaps = handleSetFlagParameters(request.getExecutionParam());
        if (!resultMaps.isEmpty()) {
            if (resultMaps.get(EXECUTION_PARAM) != null) {
                request.setExecutionParam(resultMaps.get(EXECUTION_PARAM).toString());
            }
            if (resultMaps.get(SET_FLAG) != null && StringUtils.isBlank(request.getSetFlag())) {
                request.setSetFlag(resultMaps.get(SET_FLAG).toString());
            } else if (resultMaps.get(SET_FLAG) != null && StringUtils.isNotBlank(request.getSetFlag())) {
                StringBuilder tmpSetFlag = new StringBuilder();
                String[] setStrs = request.getSetFlag().split(SpecCharEnum.DIVIDER.getValue());
                for (String setStr : setStrs) {
                    if (setStr.startsWith("spark.sql.")) {
                        tmpSetFlag.append(setStr.replace("spark.sql.", "")).append(SpecCharEnum.DIVIDER.getValue());
                    }
                }

                if (tmpSetFlag != null && tmpSetFlag.length() > 0) {
                    request.setSetFlag(tmpSetFlag.deleteCharAt(tmpSetFlag.length() - 1).toString() + SpecCharEnum.DIVIDER.getValue() + resultMaps.get("set_flag").toString());
                } else {
                    request.setSetFlag(request.getSetFlag() + SpecCharEnum.DIVIDER.getValue() + resultMaps.get("set_flag").toString());
                }

            }
        }
        LOGGER.info("set_flag: {}", request.getSetFlag());

        //Parse fpsFileId、fpsHashValue in executionParam
        Map<String, Object> multipleParameterMaps = handleFpsIdAndValueParameters(request.getExecutionParam(), request.getFpsFileId(), request.getFpsHashValue());
        if (!multipleParameterMaps.isEmpty()) {
            if (multipleParameterMaps.get(EXECUTION_PARAM) != null) {
                request.setExecutionParam(multipleParameterMaps.get(EXECUTION_PARAM).toString());
            }
            if (multipleParameterMaps.get(FPS_ID) != null) {
                request.setFpsFileId(multipleParameterMaps.get(FPS_ID).toString());
            }
            if (multipleParameterMaps.get(FPS_HASH) != null) {
                request.setFpsHashValue(multipleParameterMaps.get(FPS_HASH).toString());
            }

        }
        LOGGER.info("fps_file_id: {}", request.getFpsFileId());
        LOGGER.info("fps_hash: {}", request.getFpsHashValue());

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
        // Parse partition and run date and split by from execution parameters.
        StringBuilder partition = new StringBuilder();
        StringBuilder runDate = new StringBuilder();
        StringBuilder splitBy = new StringBuilder();

        Map<String, String> execParamMap = new HashMap<>(5);
        //alone split by parameter
        StringBuilder specialSplitBy = new StringBuilder();
        StringBuilder specialEngineReuse = new StringBuilder();
        StringBuilder lastExecutionParam = new StringBuilder();
        if (StringUtils.isNotBlank(request.getSplitBy()) && !request.getExecutionParam().contains(SPLIT_BY)) {
            specialSplitBy.append(SPLIT_BY + SpecCharEnum.COLON.getValue() + request.getSplitBy());
        }
        if (request.getEngineReuse() != null && !request.getExecutionParam().contains(ENGINE_REUSE)) {
            specialEngineReuse.append(ENGINE_REUSE + SpecCharEnum.COLON.getValue() + request.getEngineReuse());
        }
        lastExecutionParam.append(StringUtils.isNotEmpty(request.getExecutionParam()) ? request.getExecutionParam() : "");
        if (specialSplitBy != null && specialSplitBy.length() > 0) {
            lastExecutionParam.append(StringUtils.isNotBlank(lastExecutionParam.toString()) ? SpecCharEnum.DIVIDER.getValue() + specialSplitBy.toString() : specialSplitBy.toString());
        }
        if (specialEngineReuse != null && specialEngineReuse.length() > 0) {
            lastExecutionParam.append(StringUtils.isNotBlank(lastExecutionParam.toString()) ? SpecCharEnum.DIVIDER.getValue() + specialEngineReuse.toString() : specialEngineReuse.toString());
        }

        parseExecParams(partition, runDate, splitBy, lastExecutionParam != null && lastExecutionParam.length() > 0 ? lastExecutionParam.toString() : "", execParamMap);

        ApplicationProjectResponse applicationProjectResponse = new ApplicationProjectResponse();
        List<ApplicationSubmitRequest> applicationSubmitRequests = new ArrayList<>(ruleGroups.size());

        submitRulesFromRuleGroups(request, invokeCode, executionUser, projectInDb, ruleGroups, partition, applicationSubmitRequests);
        // Save gateway job info in new transaction.
        if (StringUtils.isNotBlank(request.getJobId())) {
            LOGGER.info("There is submitting from bdp-client with job:[{}]", request.getJobId());
            outerExecutionService.saveGatewayJobInfo(request.getJobId(), applicationSubmitRequests.size());
        }
        for (Iterator<ApplicationSubmitRequest> iterator = applicationSubmitRequests.iterator(); iterator.hasNext(); ) {
            ApplicationSubmitRequest applicationSubmitRequest = iterator.next();

            if (StringUtils.isNotBlank(request.getJobId())) {
                applicationSubmitRequest.setJobId(request.getJobId());
            }

            try {
                if (StringUtils.isNotEmpty(applicationSubmitRequest.getClusterName()) && StringUtils.isNotEmpty(applicationSubmitRequest.getDatabase()) && StringUtils.isNotEmpty(applicationSubmitRequest.getTable())) {
                    if (checkSkipPartitionCreateTime(applicationSubmitRequest.getClusterName(), applicationSubmitRequest.getDatabase(), applicationSubmitRequest.getTable(), applicationSubmitRequest, executionUser, request.getStartTime(), request.getEndTime())) {
                        LOGGER.info("{} partition[{}] is not in time range, remove it", applicationSubmitRequest.getDatabase() + SpecCharEnum.PERIOD_NO_ESCAPE.getValue() + applicationSubmitRequest.getTable(), applicationSubmitRequest.getPartition().toString());
                        iterator.remove();
                        continue;
                    } else {
                        LOGGER.info("{} partition[{}] is in time range, submit it", applicationSubmitRequest.getDatabase() + SpecCharEnum.PERIOD_NO_ESCAPE.getValue() + applicationSubmitRequest.getTable(), applicationSubmitRequest.getPartition().toString());
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Check partition create time failed, should not execute.");
            }
            GeneralResponse<?> generalResponse = outerExecutionService.submitRulesAndUpdateRule(applicationSubmitRequest.getJobId(), applicationSubmitRequest.getRuleIds(), applicationSubmitRequest.getPartition(), loginUser, executionUser, QualitisConstants.DEFAULT_NODE_NAME
                    , projectInDb.getId(), applicationSubmitRequest.getRuleGroupId(), request.getFpsFileId(), request.getFpsHashValue(), request.getStartupParamName()
                    , request.getClusterName(), request.getSetFlag(), execParamMap, request.getExecutionParam(), runDate, splitBy, invokeCode, null
                    , projectInDb.getSubSystemId(), applicationSubmitRequest.getPartitionFullSize(), request.getEngineReuse());
            applicationProjectResponse.getApplicationTaskSimpleResponses().add((ApplicationTaskSimpleResponse) generalResponse.getData());
        }
        return new GeneralResponse<>("200", "{&SUCCEED_TO_DISPATCH_TASK}", applicationProjectResponse);
    }

    private void submitRulesFromRuleGroups(ProjectExecutionRequest request, Integer invokeCode, String executionUser, Project projectInDb, List<RuleGroup> ruleGroups, StringBuilder partition, List<ApplicationSubmitRequest> applicationSubmitRequests) throws UnExpectedRequestException {
        for (RuleGroup ruleGroup : ruleGroups) {
            // Find all rules
            List<Rule> rules = ruleDao.findByRuleGroup(ruleGroup);
            if (CollectionUtils.isEmpty(rules) && !GroupTypeEnum.CHECK_ALERT_GROUP.getCode().equals(ruleGroup.getType())) {
                ruleGroupDao.delete(ruleGroup);
                continue;
            }
            LOGGER.info("Succeed to get rules from rule group[id={}].", ruleGroup.getId());
            List<Long> ruleIds = rules.stream().filter(o -> checkRuleEnable(o)).map(Rule::getId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(ruleIds)) {
                LOGGER.warn("{&THERE_IS_NO_RULE_IN_OPEN_STATE}");
                continue;
            }
            try {
                // Dynamic partition.
                submitRulesWithDynamicPartition(applicationSubmitRequests, projectInDb.getId(), ruleGroup.getId(), rules, ruleIds, executionUser
                        , request.getDyNamicPartition(), request.getClusterName(), partition, request.getDyNamicPartitionPrefix());

                if (CollectionUtils.isEmpty(ruleIds)) {
                    continue;
                }
            } catch (ResourceAccessException e) {
                List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.METADATA_ISSUES.getCode().toString())).collect(Collectors.toList());
                Integer applicationCommentCode = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

                generateAbnormalApplicationInfo(request.getJobId(), request.getProjectId(), ruleGroup.getId(), request.getCreateUser(), executionUser
                        , new Date(), invokeCode, partition.toString(), request.getStartupParamName(), request.getExecutionParam(), e, applicationCommentCode
                        , ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
                LOGGER.error("One group execution[id={}] of the project execution start failed!", ruleGroup.getId());
            } catch (NoPartitionException e) {
                List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.METADATA_ISSUES.getCode().toString())).collect(Collectors.toList());
                Integer applicationCommentCode = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

                generateAbnormalApplicationInfo(request.getJobId(), request.getProjectId(), ruleGroup.getId(), request.getCreateUser(), executionUser
                        , new Date(), invokeCode, partition.toString(), request.getStartupParamName(), request.getExecutionParam(), e, applicationCommentCode
                        , ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
                LOGGER.error("One group execution[id={}] of the project execution start failed!", ruleGroup.getId());
            } catch (JobSubmitException e) {
                Integer commentCode = ERR_CODE_TYPE.get(e.getErrCode());
                List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.UNKNOWN_ERROR_ISSUES.getCode().toString())).collect(Collectors.toList());
                Integer code = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

                // Record submit failed applicatoin.
                generateAbnormalApplicationInfo(request.getJobId(), request.getProjectId(), ruleGroup.getId(), request.getCreateUser(), executionUser
                        , new Date(), invokeCode, partition.toString(), request.getStartupParamName(), request.getExecutionParam(), e, commentCode != null ? commentCode : code
                        , ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
                LOGGER.error("One group execution[id={}] of the project execution start failed!", ruleGroup.getId());
            } catch (Exception e) {
                List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.UNKNOWN_ERROR_ISSUES.getCode().toString())).collect(Collectors.toList());
                Integer code = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

                generateAbnormalApplicationInfo(request.getJobId(), request.getProjectId(), ruleGroup.getId(), request.getCreateUser(), executionUser
                        , new Date(), invokeCode, partition.toString(), request.getStartupParamName(), request.getExecutionParam(), e, code
                        , ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
                LOGGER.error("One group execution[id={}] of the project execution start failed!", ruleGroup.getId());
            }

            applicationSubmitRequests.add(new ApplicationSubmitRequest(projectInDb.getId(), ruleGroup.getId(), ruleIds, partition));

        }
    }

    private Boolean checkRuleEnable(Rule rule) {
        if (rule.getEnable() != null) {
            return rule.getEnable();
        } else {
            return true;
        }

    }

    private void generateAbnormalApplicationInfo(String jobId, Long projectId, Long ruleGroupId, String createUser, String executionUser, Date date
            , Integer invokeCode, String partition, String startupParams, String executionParams, Exception e, Integer applicationCommentCode, Integer applicationStatusCode, List<Rule> rules) {
        Application newApplication;
        try {
            newApplication = outerExecutionService.generateApplicationInfo(createUser, executionUser, date, invokeCode, jobId);
        } catch (UnExpectedRequestException exception) {
            LOGGER.error(e.getMessage(), exception);
            return;
        }
        newApplication.setPartition(partition);
        newApplication.setProjectId(projectId);
        newApplication.setRuleGroupId(ruleGroupId);
        newApplication.setStartupParam(startupParams);
        newApplication.setExecutionParam(executionParams);
        catchAndSolve(e, applicationCommentCode, applicationStatusCode, rules, newApplication);
    }

    private void submitRulesWithDynamicPartition(List<ApplicationSubmitRequest> applicationSubmitRequests, Long projectId, Long ruleGroupId, List<Rule> rules
            , List<Long> ruleIds, String executionUser, boolean dynamicPartition, String clusterName, StringBuilder partition, String dynamicPartitionPrefix) throws Exception {

        if (dynamicPartition) {
            for (Rule rule : rules) {
                RuleDataSource ruleDataSource = rule.getRuleDataSources().iterator().next();
                if (ruleDataSource == null) {
                    throw new UnExpectedRequestException("Rule datasource has been broken.");
                }
                if (RuleTypeEnum.CUSTOM_RULE.getCode().equals(rule.getRuleType())) {
                    throw new UnExpectedRequestException(RuleTypeEnum.CUSTOM_RULE.getMessage() + " {&IS_NOT_SUPPORT}");
                }
                List<Map<String, Object>> currentPartitionMap;
                try {
                    TableStatisticsInfo tableStatisticsInfo = metaDataClient.getTableStatisticsInfo(StringUtils.isBlank(clusterName) ? ruleDataSource.getClusterName() : clusterName, ruleDataSource.getDbName(), ruleDataSource.getTableName(), executionUser);
                    currentPartitionMap = tableStatisticsInfo.getPartitions();
                    if (StringUtils.isNotEmpty(dynamicPartitionPrefix)) {
                        metaDataClient.getPartitionStatisticsInfo(StringUtils.isBlank(clusterName) ? ruleDataSource.getClusterName() : clusterName, ruleDataSource.getDbName(), ruleDataSource.getTableName(), filterToPartitionPath(dynamicPartitionPrefix), executionUser);
                    }
                } catch (MetaDataAcquireFailedException e) {
                    LOGGER.error("Dynamic submit failed.", e);
                    LOGGER.error(e.getMessage(), e);
                    throw new NoPartitionException(ruleDataSource.getDbName() + "." + ruleDataSource.getTableName() + "{&HAS_NO_PARTITIONS_TO_BE_EXECUTE}");
                }

                List<String> partitionList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(currentPartitionMap)) {
                    ruleIds.remove(rule.getId());
                    LOGGER.info("Start to concat full partition path, db:{}, table:{}", ruleDataSource.getDbName(), ruleDataSource.getTableName());
                    getPartitionListAndSubmit(currentPartitionMap, partitionList, rule, partition, filterToPartitionPath(dynamicPartitionPrefix), applicationSubmitRequests, projectId, ruleGroupId, StringUtils.isBlank(clusterName) ? ruleDataSource.getClusterName() : clusterName, ruleDataSource.getDbName(), ruleDataSource.getTableName());
                    LOGGER.info("Finish to concat full partition path, db:{}, table:{}", ruleDataSource.getDbName(), ruleDataSource.getTableName());
                } else {
                    throw new NoPartitionException(ruleDataSource.getDbName() + "." + ruleDataSource.getTableName() + "{&HAS_NO_PARTITIONS_TO_BE_EXECUTE}");
                }
            }
        }
    }

    private boolean checkSkipPartitionCreateTime(String clusterName, String dbName, String tableName, ApplicationSubmitRequest request, String executionUser, String startTime, String endTime) throws java.text.ParseException {
        boolean skipPartition = false;
        Long createTime = null;
        String partitionFuleSize = null;
        try {
            PartitionStatisticsInfo response = metaDataClient.getPartitionStatisticsInfo(clusterName, dbName, tableName, filterToPartitionPath(request.getPartition().toString()), executionUser);
            createTime = response.getModificationTime();
            partitionFuleSize = response.getPartitionSize();
        } catch (Exception e) {
            LOGGER.error("Check partition create time failed, should not execute.");
        }

        if (createTime == null) {
            return true;
        }

        request.setPartitionFullSize(partitionFuleSize);

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            long startMillSeconds = format.parse(startTime).getTime();
            long endMillSeconds = format.parse(endTime).getTime();
            if (createTime.longValue() < startMillSeconds || createTime > endMillSeconds) {
                skipPartition = true;
            }
        } else if (StringUtils.isNotEmpty(startTime)) {
            long startMillSeconds = format.parse(startTime).getTime();
            if (createTime.longValue() < startMillSeconds) {
                skipPartition = true;
            }
        } else if (StringUtils.isNotEmpty(endTime)) {
            long endMillSeconds = format.parse(endTime).getTime();
            if (createTime.longValue() > endMillSeconds) {
                skipPartition = true;
            }
        } else {
            if (StringUtils.isEmpty(linkisConfig.getStartTime())) {
                return false;
            }
            long startMillSeconds = format.parse(linkisConfig.getStartTime()).getTime();
            if (createTime.longValue() < startMillSeconds) {
                skipPartition = true;
            }
        }

        return skipPartition;
    }

    private void getPartitionListAndSubmit(List<Map<String, Object>> partitions, List<String> partitionList, Rule rule, StringBuilder partition
            , String dynamicPartitionPrefix, List<ApplicationSubmitRequest> applicationSubmitRequests, Long projectId, Long ruleGroupId, String clusterName, String database, String table) throws UnExpectedRequestException {

        partitionPathRecursive(partitions, "", partitionList, dynamicPartitionPrefix);
        // Submit with partiton one by one.
        for (String subPartition : partitionList) {
            StringBuilder subPartitionBuffer = new StringBuilder(subPartition);
            List<Long> oneRuleId = new ArrayList<>(1);
            oneRuleId.add(rule.getId());
            if (partition.length() > 0) {
                subPartitionBuffer.append(" ").append(AND).append(" ").append(partition);
            }
            applicationSubmitRequests.add(new ApplicationSubmitRequest(projectId, ruleGroupId, oneRuleId, subPartitionBuffer, clusterName, database, table));
        }
    }

    private void partitionPathRecursive(List<Map<String, Object>> partitions, String pathPartition, List<String> partitionList, String dynamicPartitionPrefix) throws UnExpectedRequestException {
        if (CollectionUtils.isEmpty(partitions)) {
            int lastAndIndex = pathPartition.lastIndexOf(" " + AND);
            String realPartition = pathPartition.substring(0, lastAndIndex);

            if (filterToPartitionPath(realPartition).startsWith(dynamicPartitionPrefix)) {
                LOGGER.info("Current full partition: " + realPartition);
                partitionList.add(realPartition);
                return;
            } else {
                throw new UnExpectedRequestException("{&PARTITION_ORDER_ERROR}");
            }
        }
        for (Map<String, Object> partitionMap : partitions) {
            String currentPartitionName = (String) partitionMap.get("name");
            String currentPartitionPath = (String) partitionMap.get("partitionPath");
            String currentPartitionPathWithSlash = currentPartitionPath + "/";
            String dynamicPartitionPrefixSlash = dynamicPartitionPrefix + "/";
            boolean hitPrefix = currentPartitionPathWithSlash.startsWith(dynamicPartitionPrefixSlash) || dynamicPartitionPrefixSlash.startsWith(currentPartitionPathWithSlash);
            if (StringUtils.isNotBlank(dynamicPartitionPrefix) && !hitPrefix) {
                continue;
            }
            String partitionName = currentPartitionName.split("=")[0];
            String value = currentPartitionName.split("=")[1];
            List<Map<String, Object>> childrenPartition = (List<Map<String, Object>>) partitionMap.get("childrens");

            if (CollectionUtils.isNotEmpty(childrenPartition)) {
                partitionPathRecursive(childrenPartition, pathPartition + partitionName + "=" + "'" + value + "'" + " " + AND + " "
                        , partitionList, dynamicPartitionPrefix);
            } else {
                partitionPathRecursive(null, pathPartition + partitionName + "=" + "'" + value + "'" + " " + AND + " "
                        , partitionList, dynamicPartitionPrefix);
            }
        }
    }

    @Override
    public GeneralResponse groupExecution(GroupExecutionRequest request, Integer invokeCode, String landUser) throws UnExpectedRequestException, PermissionDeniedRequestException {
        GroupExecutionRequest.checkRequest(request);
        LOGGER.info("Group execution request: {}", request.toString());
        String loginUser = getLoginUser(landUser, request.getCreateUser(), request.getAsync());

        LOGGER.info("Execute parameter entry. execution_param: {}", request.getExecutionParam());
        // Parse set flag in execution parameters, such as: qualitis.spark.set.xx=xx
        Map<String, Object> resultMaps = handleSetFlagParameters(request.getExecutionParam());
        if (!resultMaps.isEmpty()) {
            if (resultMaps.get(EXECUTION_PARAM) != null) {
                request.setExecutionParam(resultMaps.get(EXECUTION_PARAM).toString());
            }
            if (resultMaps.get(SET_FLAG) != null && StringUtils.isBlank(request.getSetFlag())) {
                request.setSetFlag(resultMaps.get(SET_FLAG).toString());
            } else if (resultMaps.get(SET_FLAG) != null && StringUtils.isNotBlank(request.getSetFlag())) {
                StringBuilder tmpSetFlag = new StringBuilder();
                String[] setStrs = request.getSetFlag().split(SpecCharEnum.DIVIDER.getValue());
                for (String setStr : setStrs) {
                    if (setStr.startsWith("spark.sql.")) {
                        tmpSetFlag.append(setStr.replace("spark.sql.", "")).append(SpecCharEnum.DIVIDER.getValue());
                    }
                }

                if (tmpSetFlag != null && tmpSetFlag.length() > 0) {
                    request.setSetFlag(tmpSetFlag.deleteCharAt(tmpSetFlag.length() - 1).toString() + SpecCharEnum.DIVIDER.getValue() + resultMaps.get("set_flag").toString());
                } else {
                    request.setSetFlag(request.getSetFlag() + SpecCharEnum.DIVIDER.getValue() + resultMaps.get("set_flag").toString());
                }

            }
        }
        LOGGER.info("set_flag: {}", request.getSetFlag());

        //Parse fpsFileId、fpsHashValue in executionParam
        Map<String, Object> multipleParameterMaps = handleFpsIdAndValueParameters(request.getExecutionParam(), request.getFpsFileId(), request.getFpsHashValue());
        if (!multipleParameterMaps.isEmpty()) {
            if (multipleParameterMaps.get(EXECUTION_PARAM) != null) {
                request.setExecutionParam(multipleParameterMaps.get(EXECUTION_PARAM).toString());
            }
            if (multipleParameterMaps.get(FPS_ID) != null) {
                request.setFpsFileId(multipleParameterMaps.get(FPS_ID).toString());
            }
            if (multipleParameterMaps.get(FPS_HASH) != null) {
                request.setFpsHashValue(multipleParameterMaps.get(FPS_HASH).toString());
            }

        }

        LOGGER.info("fps_file_id: {}", request.getFpsFileId());
        LOGGER.info("fps_hash: {}", request.getFpsHashValue());

        // Check existence of project
        RuleGroup ruleGroupInDb = ruleGroupDao.findById(request.getGroupId());
        if (null == ruleGroupInDb) {
            throw new UnExpectedRequestException("Group ID " + request.getGroupId() + " {&DOES_NOT_EXIST}");
        }
        Project projectInDb = projectDao.findById(ruleGroupInDb.getProjectId());
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.OPERATOR.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);
        checkPermissionCreateUserProxyExecuteUser(request.getCreateUser(), request.getExecutionUser());

        // Handle check alert logic branch.
        if (GroupTypeEnum.CHECK_ALERT_GROUP.getCode().equals(ruleGroupInDb.getType())) {
            GeneralResponse<ApplicationTaskSimpleResponse> generalResponse = outerExecutionService.handleCheckAlert(request.getExecutionUser(), ruleGroupInDb, projectInDb, request.getExecutionParam());
            return generalResponse;
        }
        // Find all rules
        List<Rule> rules = ruleDao.findByRuleGroup(ruleGroupInDb);
        if (CollectionUtils.isEmpty(rules)) {
            throw new UnExpectedRequestException("{&NO_RULE_CAN_BE_EXECUTED}");
        }
        rules = rules.stream().filter(rule -> checkRuleEnable(rule)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(rules)) {
            throw new UnExpectedRequestException("{&THERE_IS_NO_RULE_IN_OPEN_STATE}");
        }
        List<Long> ruleIds = rules.stream().map(Rule::getId).collect(Collectors.toList());

        // Parse partition and run date and split by from execution parameters.
        Map<String, String> execParamMap = new HashMap<>(8);
        StringBuilder partition = new StringBuilder();
        StringBuilder runDate = new StringBuilder();
        StringBuilder splitBy = new StringBuilder();

        //alone split by parameter
        StringBuilder specialSplitBy = new StringBuilder();
        StringBuilder specialEngineReuse = new StringBuilder();
        StringBuilder lastExecutionParam = new StringBuilder();
        if (StringUtils.isNotBlank(request.getSplitBy()) && !request.getExecutionParam().contains(SPLIT_BY)) {
            specialSplitBy.append(SPLIT_BY + SpecCharEnum.COLON.getValue() + request.getSplitBy());
        }
        if (request.getEngineReuse() != null && !request.getExecutionParam().contains(ENGINE_REUSE)) {
            specialEngineReuse.append(ENGINE_REUSE + SpecCharEnum.COLON.getValue() + request.getEngineReuse());
        }
        lastExecutionParam.append(StringUtils.isNotEmpty(request.getExecutionParam()) ? request.getExecutionParam() : "");
        if (specialSplitBy != null && specialSplitBy.length() > 0) {
            lastExecutionParam.append(StringUtils.isNotBlank(lastExecutionParam.toString()) ? SpecCharEnum.DIVIDER.getValue() + specialSplitBy.toString() : specialSplitBy.toString());
        }
        if (specialEngineReuse != null && specialEngineReuse.length() > 0) {
            lastExecutionParam.append(StringUtils.isNotBlank(lastExecutionParam.toString()) ? SpecCharEnum.DIVIDER.getValue() + specialEngineReuse.toString() : specialEngineReuse.toString());
        }

        parseExecParams(partition, runDate, splitBy, lastExecutionParam != null && lastExecutionParam.length() > 0 ? lastExecutionParam.toString() : "", execParamMap);

        if (CollectionUtils.isEmpty(ruleIds)) {
            return new GeneralResponse<>("200", "{&SUCCEED_TO_DISPATCH_TASK}", null);
        }
        // Save gateway job info in new transaction.
        if (StringUtils.isNotBlank(request.getJobId())) {
            LOGGER.info("There is submitting from bdp-client with job:[{}]", request.getJobId());
            outerExecutionService.saveGatewayJobInfo(request.getJobId(), QualitisConstants.ONLY_ONE_GROUP);
        }

        GeneralResponse<ApplicationTaskSimpleResponse> generalResponse = outerExecutionService.submitRulesAndUpdateRule(request.getJobId(), ruleIds
                , partition, loginUser, request.getExecutionUser(), request.getNodeName(), projectInDb.getId(), ruleGroupInDb.getId(), request.getFpsFileId(), request.getFpsHashValue()
                , request.getStartupParamName(), request.getClusterName(), request.getSetFlag(), execParamMap, request.getExecutionParam(), runDate, splitBy, invokeCode, null
                , projectInDb.getSubSystemId(), null, request.getEngineReuse());
        return generalResponse;
    }


    private Map<String, Object> handleFpsIdAndValueParameters(String executionParam, String fpsFileId, String fpsHashValue) {
        //原执行变量的fpsFileId和fpsHashValue是单独做处理的，0.25.0版本是把用户配置该两参数放到executionParam
        //处理fps_id
        Map<String, Object> maps = Maps.newHashMap();
        if (StringUtils.isNotEmpty(executionParam) && executionParam.contains(FPS_ID) && StringUtils.isEmpty(fpsFileId)) {
            StringBuilder fpsId = new StringBuilder();
            StringBuilder tmpExecParams = new StringBuilder();
            String[] setStrs = executionParam.split(SpecCharEnum.DIVIDER.getValue());
            for (String str : setStrs) {
                if (str.startsWith(FPS_ID)) {
                    fpsId.append(str.replace(FPS_ID, "").replace(SpecCharEnum.COLON.getValue(), ""));
                } else {
                    tmpExecParams.append(str).append(SpecCharEnum.DIVIDER.getValue());
                }
            }

            if (StringUtils.isNotEmpty(fpsId.toString())) {
                maps.put(FPS_ID, fpsId.toString());
            }

            if (StringUtils.isNotEmpty(tmpExecParams.toString())) {
                maps.put(EXECUTION_PARAM, tmpExecParams.deleteCharAt(tmpExecParams.length() - 1).toString());
            }
        }

        //处理fps_hash
        if (!maps.isEmpty()) {
            if (StringUtils.isNotEmpty(maps.get(EXECUTION_PARAM).toString()) && maps.get(EXECUTION_PARAM).toString().contains(FPS_HASH) && StringUtils.isEmpty(fpsHashValue)) {
                StringBuilder fpsHash = new StringBuilder();
                StringBuilder tmpExecParams = new StringBuilder();
                String[] setStrs = maps.get(EXECUTION_PARAM).toString().split(SpecCharEnum.DIVIDER.getValue());
                for (String str : setStrs) {
                    if (str.startsWith(FPS_HASH)) {
                        fpsHash.append(str.replace(FPS_HASH, "").replace(SpecCharEnum.COLON.getValue(), ""));
                    } else {
                        tmpExecParams.append(str).append(SpecCharEnum.DIVIDER.getValue());
                    }
                }

                if (StringUtils.isNotEmpty(fpsHash.toString())) {
                    maps.put(FPS_HASH, fpsHash.toString());
                }

                if (StringUtils.isNotEmpty(tmpExecParams.toString())) {
                    maps.put(EXECUTION_PARAM, tmpExecParams.deleteCharAt(tmpExecParams.length() - 1).toString());
                }
            }

        } else {
            if (StringUtils.isNotEmpty(executionParam) && executionParam.contains(FPS_HASH) && StringUtils.isEmpty(fpsHashValue)) {
                StringBuilder fpsHash = new StringBuilder();
                StringBuilder tmpExecParams = new StringBuilder();
                String[] setStrs = executionParam.split(SpecCharEnum.DIVIDER.getValue());
                for (String str : setStrs) {
                    if (str.startsWith(FPS_HASH)) {
                        fpsHash.append(str.replace(FPS_HASH, "").replace(SpecCharEnum.COLON.getValue(), ""));
                    } else {
                        tmpExecParams.append(str).append(SpecCharEnum.DIVIDER.getValue());
                    }
                }

                if (StringUtils.isNotEmpty(fpsHash.toString())) {
                    maps.put(FPS_HASH, fpsHash.toString());
                }

                if (StringUtils.isNotEmpty(tmpExecParams.toString())) {
                    maps.put(EXECUTION_PARAM, tmpExecParams.deleteCharAt(tmpExecParams.length() - 1).toString());
                }
            }

        }

        return maps;
    }


    private Map<String, Object> handleSetFlagParameters(String executionParam) {
        Map<String, Object> maps = Maps.newHashMap();

        if (StringUtils.isNotEmpty(executionParam) && executionParam.contains(QualitisConstants.SPARK_SET_FLAG)) {
            StringBuilder setFlag = new StringBuilder();
            StringBuilder tmpExecParams = new StringBuilder();
            String[] setStrs = executionParam.split(SpecCharEnum.DIVIDER.getValue());
            for (String str : setStrs) {
                if (str.startsWith(QualitisConstants.SPARK_SET_FLAG)) {
                    setFlag.append(str.replace(QualitisConstants.SPARK_SET_FLAG, "").replace(SpecCharEnum.COLON.getValue(), SpecCharEnum.EQUAL.getValue())).append(SpecCharEnum.DIVIDER.getValue());
                } else {
                    tmpExecParams.append(str).append(SpecCharEnum.DIVIDER.getValue());
                }
            }
            if (StringUtils.isNotEmpty(setFlag.toString())) {
                maps.put(SET_FLAG, setFlag.deleteCharAt(setFlag.length() - 1).toString());
            }
            if (StringUtils.isNotEmpty(tmpExecParams.toString())) {
                maps.put(EXECUTION_PARAM, tmpExecParams.deleteCharAt(tmpExecParams.length() - 1).toString());
            }
            LOGGER.info("Parsed set flag: " + maps.get(SET_FLAG).toString());
            if (maps.get(EXECUTION_PARAM) != null) {
                LOGGER.info("Remainning exexution param is: " + maps.get("execution_param").toString());
            } else {
                LOGGER.info("Remainning exexution param is: " + null);
            }

        }
        return maps;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse handleCheckAlert(String executionUser, RuleGroup ruleGroupInDb, Project projectInDb, String executionParam) throws UnExpectedRequestException, PermissionDeniedRequestException {
        List<CheckAlert> checkAlertsInDb = checkAlertDao.findByRuleGroup(ruleGroupInDb);
        if (CollectionUtils.isEmpty(checkAlertsInDb)) {
            throw new UnExpectedRequestException("Check alert in group[" + ruleGroupInDb.getRuleGroupName() + "] {&DOES_NOT_EXIST}");
        }

        String cluster = linkisConfig.getBdapCheckAlertCluster();
        CheckAlert currentCheckAlert = checkAlertsInDb.iterator().next();
        LOGGER.info("{} start to submit check alert rule[{}]", executionUser, currentCheckAlert.toString());

        // Use a white list to check the user and table
        CheckAlertWhiteList checkAlertWhiteList = checkAlertWhiteListRepository.checkWhiteList(cluster + SpecCharEnum.PERIOD_NO_ESCAPE.getValue() + currentCheckAlert.getAlertTable(), WhiteListTypeEnum.CHECK_ALERT_TABLE.getCode(), executionUser);

        if (checkAlertWhiteList == null) {
            throw new PermissionDeniedRequestException("Check alert execution denied because incompatible white list");
        }

        Map<String, String> execParamMap = new HashMap<>(checkAlertsInDb.size());
        parseExecParams(new StringBuilder(), new StringBuilder(), new StringBuilder(), executionParam, execParamMap);

        String startupParam = "";
        Boolean engineReuse = Boolean.TRUE;
        if (CollectionUtils.isNotEmpty(execParamMap.keySet())) {
            if (execParamMap.containsKey(QualitisConstants.QUALITIS_CLUSTER_NAME)) {
                cluster = execParamMap.get(QualitisConstants.QUALITIS_CLUSTER_NAME);
            }
            if (execParamMap.containsKey(QualitisConstants.QUALITIS_STARTUP_PARAM)) {
                startupParam = execParamMap.get(QualitisConstants.QUALITIS_STARTUP_PARAM).replace(SpecCharEnum.COMMA.getValue(), SpecCharEnum.DIVIDER.getValue());
            }
            if (execParamMap.containsKey(QualitisConstants.QUALITIS_ENGINE_REUSE)) {
                engineReuse = Boolean.parseBoolean(execParamMap.get(QualitisConstants.QUALITIS_ENGINE_REUSE).toLowerCase());
            }
        }

        Application newApplication = generateApplicationInfo(currentCheckAlert.getCreateUser(), executionUser, new Date(), InvokeTypeEnum.FLOW_API_INVOKE.getCode(), "");
        newApplication.setRuleSize(checkAlertsInDb.size());
        newApplication.setClusterName(cluster);
        newApplication.setEngineReuse(engineReuse);
        newApplication.setProjectId(projectInDb.getId());
        newApplication.setExecutionParam(executionParam);
        newApplication.setProjectName(projectInDb.getName());
        newApplication.setRuleGroupId(ruleGroupInDb.getId());
        newApplication.setNodeName(currentCheckAlert.getNodeName());

        Application saveApplication = applicationDao.saveApplication(newApplication);
        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(saveApplication.getClusterName());
        if (clusterInfo == null) {
            throw new UnExpectedRequestException("Cluster : [" + saveApplication.getClusterName() + "] {&DOES_NOT_EXIST}");
        }

        boolean accept = checkLinkisAccept(newApplication, clusterInfo.getClusterName(), executionUser);
        if (!accept) {
            return new GeneralResponse<>("200", "Add pending application successfully.",
                    new ApplicationTaskSimpleResponse(newApplication.getId()));
        }
        String[] dbAndTables = currentCheckAlert.getAlertTable().split(SpecCharEnum.PERIOD.getValue());
        List<String> columns;
        try {
            List<ColumnInfoDetail> columnInfoDetails = metaDataClient.getColumnInfo(clusterInfo.getClusterName(), dbAndTables[0], dbAndTables[1], executionUser);
            columns = columnInfoDetails.stream().map(columnInfoDetail -> columnInfoDetail.getFieldName()).collect(Collectors.toList());
        } catch (Exception e) {
            throw new UnExpectedRequestException("{&RULE_DATASOURCE_BE_MOVED}");
        }
        if (!columns.contains(currentCheckAlert.getAlertCol()) || (StringUtils.isNotEmpty(currentCheckAlert.getMajorAlertCol()) && !columns.contains(currentCheckAlert.getMajorAlertCol()))) {
            throw new UnExpectedRequestException("{&RULE_DATASOURCE_BE_MOVED}");
        }

        List<TaskSubmitResult> taskSubmitResults = new ArrayList<>();
        try {
            taskSubmitResults.add(executionManager.executeCheckAlert(clusterInfo, dbAndTables, columns, currentCheckAlert, saveApplication, startupParam, engineReuse, EngineTypeEnum.SPARK_ENGINE.getMessage()));
        } catch (Exception e) {
            List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.UNKNOWN_ERROR_ISSUES.getCode().toString())).collect(Collectors.toList());
            Integer code = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

            // Print and save abnormal application.
            catchAndSolve(e, code, ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), currentCheckAlert, newApplication);
            return new GeneralResponse<>("500", e.getMessage(), null);
        }

        saveApplication.setTotalTaskNum(taskSubmitResults.size());
        LOGGER.info("Succeed to submit application. Result: {}", taskSubmitResults);
        Application applicationInDb = applicationDao.saveApplication(saveApplication);
        LOGGER.info("Succeed to save application. Application: {}", applicationInDb);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_DISPATCH_TASK}", new ApplicationTaskSimpleResponse(taskSubmitResults));
    }

    private void parseExecParams(StringBuilder partition, StringBuilder runDate, StringBuilder splitBy, String execParams, Map<String, String> execParamMap) {
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
                } else if (SPLIT_BY.equals(execParamKey)) {
                    if (splitBy.length() == 0) {
                        splitBy.append(execParamValue);
                    }
                    continue;
                }
                execParamMap.put(str.split(SpecCharEnum.COLON.getValue())[0], str.split(SpecCharEnum.COLON.getValue())[1]);
            }
        }
    }

    private void parseExecParams(String exectionParam, Map<String, String> execParamMap) {
        if (StringUtils.isNotBlank(exectionParam)) {
            String[] execParamStrs = exectionParam.split(SpecCharEnum.DIVIDER.getValue());
            for (String str : execParamStrs) {
                execParamMap.put(str.split(SpecCharEnum.COLON.getValue())[0], str.split(SpecCharEnum.COLON.getValue())[1]);
            }
        }
    }

    @Override
    public GeneralResponse ruleListExecution(RuleListExecutionRequest request, Integer invokeCode, String landUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException {
        LOGGER.info("Execute application by rules. rule_ids: {}", request.getRuleList());
        // Check Arguments.
        RuleListExecutionRequest.checkRequest(request);
        String loginUser = getLoginUser(landUser, request.getCreateUser(), request.getAsync());
        LOGGER.info("Execute parameter entry. execution_param: {}", request.getExecutionParam());

        // Parse set flag in execution parameters, such as: qualitis.spark.set.xx=xx
        Map<String, Object> resultMaps = handleSetFlagParameters(request.getExecutionParam());
        if (!resultMaps.isEmpty()) {
            if (resultMaps.get(EXECUTION_PARAM) != null) {
                request.setExecutionParam(resultMaps.get(EXECUTION_PARAM).toString());
            }
            if (resultMaps.get(SET_FLAG) != null && StringUtils.isBlank(request.getSetFlag())) {
                request.setSetFlag(resultMaps.get(SET_FLAG).toString());
            } else if (resultMaps.get(SET_FLAG) != null && StringUtils.isNotBlank(request.getSetFlag())) {
                StringBuilder tmpSetFlag = new StringBuilder();
                String[] setStrs = request.getSetFlag().split(SpecCharEnum.DIVIDER.getValue());
                for (String setStr : setStrs) {
                    if (setStr.startsWith("spark.sql.")) {
                        tmpSetFlag.append(setStr.replace("spark.sql.", "")).append(SpecCharEnum.DIVIDER.getValue());
                    }
                }

                if (tmpSetFlag != null && tmpSetFlag.length() > 0) {
                    request.setSetFlag(tmpSetFlag.deleteCharAt(tmpSetFlag.length() - 1).toString() + SpecCharEnum.DIVIDER.getValue() + resultMaps.get("set_flag").toString());
                } else {
                    request.setSetFlag(request.getSetFlag() + SpecCharEnum.DIVIDER.getValue() + resultMaps.get("set_flag").toString());
                }

            }
        }
        LOGGER.info("set_flag: {}", request.getSetFlag());

        //Parse fpsFileId、fpsHashValue in executionParam
        Map<String, Object> multipleParameterMaps = handleFpsIdAndValueParameters(request.getExecutionParam(), request.getFpsFileId(), request.getFpsHashValue());
        if (!multipleParameterMaps.isEmpty()) {
            if (multipleParameterMaps.get(EXECUTION_PARAM) != null) {
                request.setExecutionParam(multipleParameterMaps.get(EXECUTION_PARAM).toString());
            }
            if (multipleParameterMaps.get(FPS_ID) != null) {
                request.setFpsFileId(multipleParameterMaps.get(FPS_ID).toString());
            }
            if (multipleParameterMaps.get(FPS_HASH) != null) {
                request.setFpsHashValue(multipleParameterMaps.get(FPS_HASH).toString());
            }

        }
        LOGGER.info("fps_file_id: {}", request.getFpsFileId());
        LOGGER.info("fps_hash: {}", request.getFpsHashValue());

        // Check the existence of rule.
        List<Rule> rules = request.getExecutableRuleList();
        if (CollectionUtils.isEmpty(rules)) {
            rules = ruleDao.findByIds(request.getRuleList());
        }
        if (CollectionUtils.isEmpty(rules)) {
            throw new UnExpectedRequestException("{&NO_RULE_CAN_BE_EXECUTED}");
        }
        List<Long> ruleIds = rules.stream().map(Rule::getId).distinct().collect(Collectors.toList());
        // Check rule exists on the request.
        List<Long> notExistRules = request.getRuleList().stream().filter(ruleId -> !ruleIds.contains(ruleId)).collect(Collectors.toList());

        if (!notExistRules.isEmpty()) {
            throw new UnExpectedRequestException("The ids of rule: " + notExistRules.toString() + " {&DOES_NOT_EXIST}");
        }
        LOGGER.info("Succeed to find all rules.");
        checkPermissionCreateUserProxyExecuteUser(request.getCreateUser(), request.getExecutionUser());
        // Parse partition and run date and split by from execution parameters.
        StringBuilder partition = new StringBuilder();
        StringBuilder runDate = new StringBuilder();
        StringBuilder splitBy = new StringBuilder();

        Map<String, String> execParamMap = new HashMap<>(5);
        //alone split by parameter
        StringBuilder specialSplitBy = new StringBuilder();
        StringBuilder specialEngineReuse = new StringBuilder();
        StringBuilder lastExecutionParam = new StringBuilder();
        if (StringUtils.isNotBlank(request.getSplitBy()) && !request.getExecutionParam().contains(SPLIT_BY)) {
            specialSplitBy.append(SPLIT_BY + SpecCharEnum.COLON.getValue() + request.getSplitBy());
        }
        if (request.getEngineReuse() != null && !request.getExecutionParam().contains(ENGINE_REUSE)) {
            specialEngineReuse.append(ENGINE_REUSE + SpecCharEnum.COLON.getValue() + request.getEngineReuse());
        }
        lastExecutionParam.append(StringUtils.isNotEmpty(request.getExecutionParam()) ? request.getExecutionParam() : "");
        if (specialSplitBy != null && specialSplitBy.length() > 0) {
            lastExecutionParam.append(StringUtils.isNotBlank(lastExecutionParam.toString()) ? SpecCharEnum.DIVIDER.getValue() + specialSplitBy.toString() : specialSplitBy.toString());
        }
        if (specialEngineReuse != null && specialEngineReuse.length() > 0) {
            lastExecutionParam.append(StringUtils.isNotBlank(lastExecutionParam.toString()) ? SpecCharEnum.DIVIDER.getValue() + specialEngineReuse.toString() : specialEngineReuse.toString());
        }

        parseExecParams(partition, runDate, splitBy, lastExecutionParam != null && lastExecutionParam.length() > 0 ? lastExecutionParam.toString() : "", execParamMap);

        ApplicationProjectResponse applicationProjectResponse = new ApplicationProjectResponse();
        List<ApplicationSubmitRequest> applicationSubmitRequests = new ArrayList<>(rules.size());
        List<Project> projects = rules.stream().map(Rule::getProject).distinct().collect(Collectors.toList());

        GeneralResponse<ApplicationTaskSimpleResponse> generalResponse;
        checkProject(request, invokeCode, loginUser, rules, partition, applicationSubmitRequests, projects);
        // Save gateway job info in new transaction.
        if (StringUtils.isNotBlank(request.getJobId())) {
            LOGGER.info("There is submitting from bdp-client with job:[{}]", request.getJobId());
            outerExecutionService.saveGatewayJobInfo(request.getJobId(), applicationSubmitRequests.size());
        }
        for (Iterator<ApplicationSubmitRequest> iterator = applicationSubmitRequests.iterator(); iterator.hasNext(); ) {
            ApplicationSubmitRequest applicationSubmitRequest = iterator.next();
            if (StringUtils.isNotBlank(request.getJobId())) {
                applicationSubmitRequest.setJobId(request.getJobId());
            }
            try {
                boolean skip = checkSkipPartitionCreateTime(applicationSubmitRequest.getClusterName(), applicationSubmitRequest.getDatabase(), applicationSubmitRequest.getTable(), applicationSubmitRequest, request.getExecutionUser(), request.getStartTime(), request.getEndTime());
                if (skip && StringUtils.isNotEmpty(applicationSubmitRequest.getClusterName()) && StringUtils.isNotEmpty(applicationSubmitRequest.getDatabase()) && StringUtils.isNotEmpty(applicationSubmitRequest.getTable())) {
                    iterator.remove();
                    continue;
                }
            } catch (Exception e) {
                LOGGER.error("Check partition create time failed, should not execute.");
            }
            List<Long> currentRuleIds = applicationSubmitRequest.getRuleIds();
            List<Rule> currentRules = rules.stream().filter(rule -> currentRuleIds.contains(rule.getId())).collect(Collectors.toList());
            SubmitRuleBaseInfo submitRuleBaseInfo = SubmitRuleBaseInfo.build(applicationSubmitRequest.getJobId()
                    , applicationSubmitRequest.getPartition(), loginUser, request.getExecutionUser(), QualitisConstants.DEFAULT_NODE_NAME
                    , applicationSubmitRequest.getProjectId(), applicationSubmitRequest.getRuleGroupId(), request.getFpsFileId(), request.getFpsHashValue(), request.getStartupParamName()
                    , request.getClusterName(), request.getSetFlag(), execParamMap, request.getExecutionParam(), runDate, splitBy, invokeCode, null
                    , projects.iterator().next().getSubSystemId(), applicationSubmitRequest.getPartitionFullSize(), request.getEngineReuse());

            if (Objects.nonNull(request.getRuleContext())) {
                updateRuleConsistentWithContext(request.getRuleContext(), currentRules);
            } else {
                updateFilterToRuleDataSource(currentRules);
            }

            generalResponse = outerExecutionService.submitRules(currentRules, submitRuleBaseInfo);
            applicationProjectResponse.getApplicationTaskSimpleResponses().add(generalResponse.getData());

        }

        return new GeneralResponse<>("200", "{&SUCCEED_TO_DISPATCH_TASK}", applicationProjectResponse);
    }

    private void updateRuleConsistentWithContext(RuleContext ruleContext, List<Rule> rules) {
        for (Rule rule : rules) {
            if (RuleContextManager.checkIfLatest(ruleContext)) {
                LOGGER.info("Ready to update ruleDataSource, ruleContext: {}", ruleContext);
                updateFilterToRuleDataSource(Arrays.asList(rule));
            }
        }
    }

    private void checkProject(RuleListExecutionRequest request, Integer invokeCode, String loginUser, List<Rule> rules, StringBuilder partition, List<ApplicationSubmitRequest> applicationSubmitRequests, List<Project> projects) throws PermissionDeniedRequestException, UnExpectedRequestException {
        // Check permissions of projects.
        for (Project projectInDb : projects) {
            List<Integer> permissions = new ArrayList<>();
            permissions.add(ProjectUserPermissionEnum.OPERATOR.getCode());
            projectService.checkProjectPermission(projectInDb, loginUser, permissions);

            List<Rule> currentRules = rules.stream().filter(o -> checkRuleEnable(o)).filter(rule -> rule.getProject().getId().equals(projectInDb.getId())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(currentRules)) {
                LOGGER.warn("{&THERE_IS_NO_RULE_IN_OPEN_STATE}");
                continue;
            }
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
                    List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.METADATA_ISSUES.getCode().toString())).collect(Collectors.toList());
                    Integer code = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

                    // Record submit failed applicatoin.
                    generateAbnormalApplicationInfo(request.getJobId(), projectInDb.getId(), ruleGroup.getId(), request.getCreateUser(), request.getExecutionUser()
                            , new Date(), invokeCode, partition.toString(), request.getStartupParamName(), request.getExecutionParam(), e, code
                            , ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
                    LOGGER.error("One group execution[id={}] of the rule list execution start failed!", ruleGroup.getId());
                } catch (NoPartitionException e) {
                    List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.METADATA_ISSUES.getCode().toString())).collect(Collectors.toList());
                    Integer code = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

                    // Record submit failed applicatoin.
                    generateAbnormalApplicationInfo(request.getJobId(), projectInDb.getId(), ruleGroup.getId(), request.getCreateUser(), request.getExecutionUser()
                            , new Date(), invokeCode, partition.toString(), request.getStartupParamName(), request.getExecutionParam(), e, code
                            , ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
                    LOGGER.error("One group execution[id={}] of the rule list execution start failed!", ruleGroup.getId());
                } catch (JobSubmitException e) {
                    Integer commentCode = ERR_CODE_TYPE.get(e.getErrCode());
                    List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.UNKNOWN_ERROR_ISSUES.getCode().toString())).collect(Collectors.toList());
                    Integer code = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

                    // Record submit failed applicatoin.
                    generateAbnormalApplicationInfo(request.getJobId(), projectInDb.getId(), ruleGroup.getId(), request.getCreateUser(), request.getExecutionUser()
                            , new Date(), invokeCode, partition.toString(), request.getStartupParamName(), request.getExecutionParam(), e, commentCode != null ? commentCode : code
                            , ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
                    LOGGER.error("One group execution[id={}] of the datasource execution start failed!", ruleGroup.getId());
                } catch (Exception e) {
                    List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.METADATA_ISSUES.getCode().toString())).collect(Collectors.toList());
                    Integer code = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

                    // Record submit failed applicatoin.
                    generateAbnormalApplicationInfo(request.getJobId(), projectInDb.getId(), ruleGroup.getId(), request.getCreateUser(), request.getExecutionUser()
                            , new Date(), invokeCode, partition.toString(), request.getStartupParamName(), request.getExecutionParam(), e, code
                            , ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
                    LOGGER.error("One group execution[id={}] of the rule list execution start failed!", ruleGroup.getId());
                }
                applicationSubmitRequests.add(new ApplicationSubmitRequest(request.getJobId(), projectInDb.getId(), ruleGroup.getId(), currentRuleIds, partition));

            }
        }
    }

    private String getLoginUser(String loginUser, String createUser, boolean async) {
        if (async) {
            return createUser;
        }

        if (StringUtils.isBlank(loginUser)) {
            return createUser;
        }
        return loginUser;
    }

    @Override
    public GeneralResponse dataSourceExecution(DataSourceExecutionRequest request, String landUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException {
        LOGGER.info("Execute application by datasource. cluster: {}, database: {}, table: {}", request.getCluster(), request.getDatabase(), request.getTable());
        // Check Arguments.
        DataSourceExecutionRequest.checkRequest(request);
        String loginUser = getLoginUser(landUser, request.getCreateUser(), request.getAsync());

        // Parse set flag in execution parameters, such as: qualitis.spark.set.xx=xx
        Map<String, Object> resultMaps = handleSetFlagParameters(request.getExecutionParam());
        if (!resultMaps.isEmpty()) {
            if (resultMaps.get(EXECUTION_PARAM) != null) {
                request.setExecutionParam(resultMaps.get(EXECUTION_PARAM).toString());
            }
            if (resultMaps.get(SET_FLAG) != null && StringUtils.isBlank(request.getSetFlag())) {
                request.setSetFlag(resultMaps.get(SET_FLAG).toString());
            } else if (resultMaps.get(SET_FLAG) != null && StringUtils.isNotBlank(request.getSetFlag())) {
                StringBuilder tmpSetFlag = new StringBuilder();
                String[] setStrs = request.getSetFlag().split(SpecCharEnum.DIVIDER.getValue());
                for (String setStr : setStrs) {
                    if (setStr.startsWith("spark.sql.")) {
                        tmpSetFlag.append(setStr.replace("spark.sql.", "")).append(SpecCharEnum.DIVIDER.getValue());
                    }
                }

                if (tmpSetFlag != null && tmpSetFlag.length() > 0) {
                    request.setSetFlag(tmpSetFlag.deleteCharAt(tmpSetFlag.length() - 1).toString() + SpecCharEnum.DIVIDER.getValue() + resultMaps.get("set_flag").toString());
                } else {
                    request.setSetFlag(request.getSetFlag() + SpecCharEnum.DIVIDER.getValue() + resultMaps.get("set_flag").toString());
                }

            }
        }

        // Find all rule datasources by user.
        List<RuleDataSource> ruleDataSources = new ArrayList<>();
        ruleDataSources.addAll(ruleDataSourceDao.findDatasourcesByUser(loginUser, request.getCluster(), request.getDatabase(), request.getTable()));

        List<Rule> rules = ruleDataSources.stream().filter(r ->
                r.getClusterName().equals(request.getCluster())
                        && r.getDbName().equals(request.getDatabase())
                        && r.getTableName().equals(request.getTable())
        ).map(RuleDataSource::getRule).distinct().filter(rule -> {
            if (!request.getCrossTable()) {
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
        // Parse partition and run date and split by from execution parameters.
        StringBuilder partition = new StringBuilder();
        StringBuilder runDate = new StringBuilder();
        StringBuilder splitBy = new StringBuilder();

        Map<String, String> execParamMap = new HashMap<>(5);
        //alone split by parameter
        StringBuilder specialSplitBy = new StringBuilder();
        StringBuilder specialEngineReuse = new StringBuilder();
        StringBuilder lastExecutionParam = new StringBuilder();
        if (StringUtils.isNotBlank(request.getSplitBy()) && !request.getExecutionParam().contains(SPLIT_BY)) {
            specialSplitBy.append(SPLIT_BY + SpecCharEnum.COLON.getValue() + request.getSplitBy());
        }
        if (request.getEngineReuse() != null && !request.getExecutionParam().contains(ENGINE_REUSE)) {
            specialEngineReuse.append(ENGINE_REUSE + SpecCharEnum.COLON.getValue() + request.getEngineReuse());
        }
        lastExecutionParam.append(StringUtils.isNotEmpty(request.getExecutionParam()) ? request.getExecutionParam() : "");
        if (specialSplitBy != null && specialSplitBy.length() > 0) {
            lastExecutionParam.append(StringUtils.isNotBlank(lastExecutionParam.toString()) ? SpecCharEnum.DIVIDER.getValue() + specialSplitBy.toString() : specialSplitBy.toString());
        }
        if (specialEngineReuse != null && specialEngineReuse.length() > 0) {
            lastExecutionParam.append(StringUtils.isNotBlank(lastExecutionParam.toString()) ? SpecCharEnum.DIVIDER.getValue() + specialEngineReuse.toString() : specialEngineReuse.toString());
        }

        parseExecParams(partition, runDate, splitBy, lastExecutionParam != null && lastExecutionParam.length() > 0 ? lastExecutionParam.toString() : "", execParamMap);
        List<Project> projects = rules.stream().map(Rule::getProject).distinct().collect(Collectors.toList());

        handleProjectData(request, loginUser, rules, applicationSubmitRequests, partition, projects);
        // Save gateway job info in new transaction.
        if (StringUtils.isNotBlank(request.getJobId())) {
            LOGGER.info("There is submitting from bdp-client with job:[{}]", request.getJobId());
            outerExecutionService.saveGatewayJobInfo(request.getJobId(), applicationSubmitRequests.size());
        }
        GeneralResponse<ApplicationTaskSimpleResponse> generalResponse;
        for (Iterator<ApplicationSubmitRequest> iterator = applicationSubmitRequests.iterator(); iterator.hasNext(); ) {
            ApplicationSubmitRequest applicationSubmitRequest = iterator.next();
            if (StringUtils.isNotBlank(request.getJobId())) {
                applicationSubmitRequest.setJobId(request.getJobId());
            }
            try {
                boolean skip = checkSkipPartitionCreateTime(applicationSubmitRequest.getClusterName(), applicationSubmitRequest.getDatabase(), applicationSubmitRequest.getTable(), applicationSubmitRequest, request.getExecutionUser(), request.getStartTime(), request.getEndTime());
                if (skip && StringUtils.isNotEmpty(applicationSubmitRequest.getClusterName()) && StringUtils.isNotEmpty(applicationSubmitRequest.getDatabase()) && StringUtils.isNotEmpty(applicationSubmitRequest.getTable())) {
                    iterator.remove();
                    continue;
                }
            } catch (Exception e) {
                LOGGER.error("Check partition create time failed, should not execute.");
            }
            generalResponse = outerExecutionService.submitRulesAndUpdateRule(applicationSubmitRequest.getJobId()
                    , applicationSubmitRequest.getRuleIds()
                    , applicationSubmitRequest.getPartition(), loginUser, request.getExecutionUser(), QualitisConstants.DEFAULT_NODE_NAME, applicationSubmitRequest.getProjectId()
                    , applicationSubmitRequest.getRuleGroupId(), request.getFpsFileId(), request.getFpsHashValue(), request.getStartupParamName(), request.getClusterName()
                    , request.getSetFlag(), execParamMap, request.getExecutionParam(), runDate, splitBy, InvokeTypeEnum.BDP_CLIENT_API_INVOKE.getCode(), null
                    , projects.iterator().next().getSubSystemId(), applicationSubmitRequest.getPartitionFullSize(), request.getEngineReuse());
            applicationProjectResponse.getApplicationTaskSimpleResponses().add(generalResponse.getData());
        }
        return new GeneralResponse<>("200", "{&SUCCEED_TO_DISPATCH_TASK}", applicationProjectResponse);
    }

    private void handleProjectData(DataSourceExecutionRequest request, String loginUser, List<Rule> rules, List<ApplicationSubmitRequest> applicationSubmitRequests, StringBuilder partition, List<Project> projects) throws PermissionDeniedRequestException {
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
                LOGGER.info("Succeed to find current rules of one group with datasource, rule id: {}", currentRuleIds);

                // Dynamic partition.
                try {
                    submitRulesWithDynamicPartition(applicationSubmitRequests, projectInDb.getId(), ruleGroup.getId(), rules, currentRuleIds, request.getExecutionUser()
                            , request.getDyNamicPartition(), request.getClusterName(), partition, request.getDyNamicPartitionPrefix());
                } catch (ResourceAccessException e) {
                    List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.METADATA_ISSUES.getCode().toString())).collect(Collectors.toList());
                    Integer code = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

                    // Record submit failed applicatoin.
                    generateAbnormalApplicationInfo(request.getJobId(), projectInDb.getId(), ruleGroup.getId(), request.getCreateUser(), request.getExecutionUser()
                            , new Date(), InvokeTypeEnum.BDP_CLIENT_API_INVOKE.getCode(), partition.toString(), request.getStartupParamName(), request.getExecutionParam()
                            , e, code, ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
                    LOGGER.error("One group execution[id={}] of the datasource execution start failed!", ruleGroup.getId());
                } catch (NoPartitionException e) {
                    List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.METADATA_ISSUES.getCode().toString())).collect(Collectors.toList());
                    Integer code = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

                    // Record submit failed applicatoin.
                    generateAbnormalApplicationInfo(request.getJobId(), projectInDb.getId(), ruleGroup.getId(), request.getCreateUser(), request.getExecutionUser()
                            , new Date(), InvokeTypeEnum.BDP_CLIENT_API_INVOKE.getCode(), partition.toString(), request.getStartupParamName(), request.getExecutionParam()
                            , e, code, ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
                    LOGGER.error("One group execution[id={}] of the datasource execution start failed!", ruleGroup.getId());
                } catch (JobSubmitException e) {
                    Integer commentCode = ERR_CODE_TYPE.get(e.getErrCode());
                    List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.UNKNOWN_ERROR_ISSUES.getCode().toString())).collect(Collectors.toList());
                    Integer code = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

                    // Record submit failed applicatoin.
                    generateAbnormalApplicationInfo(request.getJobId(), projectInDb.getId(), ruleGroup.getId(), request.getCreateUser(), request.getExecutionUser()
                            , new Date(), InvokeTypeEnum.BDP_CLIENT_API_INVOKE.getCode(), partition.toString(), request.getStartupParamName(), request.getExecutionParam()
                            , e, commentCode != null ? commentCode : code, ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
                    LOGGER.error("One group execution[id={}] of the datasource execution start failed!", ruleGroup.getId());
                } catch (Exception e) {
                    List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.UNKNOWN_ERROR_ISSUES.getCode().toString())).collect(Collectors.toList());
                    Integer code = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

                    // Record submit failed applicatoin.
                    generateAbnormalApplicationInfo(request.getJobId(), projectInDb.getId(), ruleGroup.getId(), request.getCreateUser(), request.getExecutionUser()
                            , new Date(), InvokeTypeEnum.BDP_CLIENT_API_INVOKE.getCode(), partition.toString(), request.getStartupParamName(), request.getExecutionParam()
                            , e, code, ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules);
                    LOGGER.error("One group execution[id={}] of the datasource execution start failed!", ruleGroup.getId());
                }

                if (CollectionUtils.isNotEmpty(currentRuleIds)) {
                    applicationSubmitRequests.add(new ApplicationSubmitRequest(request.getJobId(), projectInDb.getId(), ruleGroup.getId(), currentRuleIds, partition));
                }
            }
        }
    }

    @Override
    public GeneralResponse<ApplicationTaskResponse> getApplicationStatus(String applicationId) throws UnExpectedRequestException {
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
    public GeneralResponse<ApplicationTaskResponse> getApplicationDynamicStatus(String applicationId) throws UnExpectedRequestException {
        // Find application by applicationId
        Application application = applicationDao.findById(applicationId);
        if (application == null) {
            throw new UnExpectedRequestException("Application_id {&DOES_NOT_EXIST}");
        }
        LOGGER.info("Succeed to find application. application: {}", application);

        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_APPLICATION_STATUS}", new ApplicationTaskResponse(application));
    }

    @Override
    public GeneralResponse<ApplicationResultResponse> getApplicationSummary(String applicationId) throws UnExpectedRequestException {
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
    public GeneralResponse<List<ApplicationResultValueResponse>> getApplicationResultValue(String applicationId, String executionUser) throws UnExpectedRequestException {
        if (StringUtils.isEmpty(applicationId) || StringUtils.isEmpty(executionUser)) {
            LOGGER.warn("Get application result value with empty string from outer.");
            throw new UnExpectedRequestException("Application ID or execution user {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        // Find application by applicationId
        Application applicationInDb = applicationDao.findById(applicationId);
        if (applicationInDb == null) {
            throw new UnExpectedRequestException(String.format("Application: %s {&DOES_NOT_EXIST} in db", applicationId));
        }
        if (!executionUser.equals(applicationInDb.getExecuteUser())) {
            throw new UnExpectedRequestException("Application has not been executed by the execution user");
        }
        LOGGER.info("Start to get result value. Application: {}", applicationInDb.toString());

        List<TaskResult> taskResults = taskResultDao.findByApplicationId(applicationId);
        if (CollectionUtils.isEmpty(taskResults)) {
            return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_APPLICATIONS_BUT_FIND_NO_RESULTS}", null);
        }
        List<ApplicationResultValueResponse> applicationResultValueResponses = new ArrayList<>(taskResults.size());

        for (TaskResult taskResult : taskResults) {
            ApplicationResultValueResponse applicationResultValueResponse = new ApplicationResultValueResponse(taskResult);

            TaskRuleSimple taskRuleSimple = taskRuleSimpleDao.findByApplicationAndRule(applicationId, taskResult.getRuleId());
            if (taskRuleSimple != null) {
                applicationResultValueResponse.setRuleName(taskRuleSimple.getRuleName());
            }

            if (taskResult.getRuleMetricId() != null) {
                RuleMetric ruleMetric = ruleMetricDao.findById(taskResult.getRuleMetricId());
                if (ruleMetric != null) {
                    applicationResultValueResponse.setRuleMetricName(ruleMetric.getName());
                }
            }

            applicationResultValueResponses.add(applicationResultValueResponse);
        }

        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_APPLICATION_RESULT}", applicationResultValueResponses);
    }

    @Override
    public GeneralResponse<String> getTaskLog(GetTaskLogRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
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
        if (!request.getCreateUser().equals(task.getApplication().getCreateUser())) {
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
    public GeneralResponse<String> getApplicationLog(String applicationId) throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check Arguments
        Application applicationInDb = applicationDao.findById(applicationId);
        if (applicationInDb == null) {
            throw new UnExpectedRequestException("Application {&DOES_NOT_EXIST}");
        }

        if (ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode().equals(applicationInDb.getStatus())) {
            return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_TASK_LOG}", applicationInDb.getExceptionMessage());
        }
        List<Task> tasks = taskDao.findByApplication(applicationInDb);
        StringBuilder log = new StringBuilder();
        if (CollectionUtils.isNotEmpty(tasks)) {
            for (Task task : tasks) {
                String executeUser = task.getApplication().getExecuteUser();

                // Check if user has permissions proxying execution user
                checkPermissionCreateUserProxyExecuteUser(applicationInDb.getCreateUser(), executeUser);

                ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(task.getClusterName());
                if (clusterInfo == null) {
                    throw new UnExpectedRequestException("Cluster : [" + task.getClusterName() + "] {&DOES_NOT_EXIST}");
                }
                LogResult logResult;
                try {
                    logResult = monitorManager.getTaskPartialLog(task.getTaskRemoteId(), 0, executeUser, clusterInfo.getLinkisAddress(), clusterInfo.getClusterName());
                } catch (LogPartialException | ClusterInfoNotConfigException e) {
                    throw new UnExpectedRequestException(e.getMessage());
                }

                if (logResult != null && StringUtils.isNotEmpty(logResult.getLog())) {
                    log.append(logResult.getLog()).append("\n");
                }

                LOGGER.info("Succeed to get log of the task. task id: {}, task remote id: {}", task.getId(), task.getTaskRemoteId());
            }
        }


        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_TASK_LOG}", log.toString());
    }

    @Override
    public GeneralResponse<Integer> killApplication(String applicationId, String loginUser)
            throws JobKillException, UnExpectedRequestException, ClusterInfoNotConfigException, PermissionDeniedRequestException {
        LOGGER.info("{} to kill {}", loginUser, applicationId);
        Application applicationInDb = applicationDao.findById(applicationId);

        if (applicationInDb == null) {
            throw new UnExpectedRequestException(String.format("Application ID: %s {&DOES_NOT_EXIST}", applicationId));
        }
        Integer status = applicationInDb.getStatus();
        if (status.equals(ApplicationStatusEnum.SUBMITTED.getCode()) || status.equals(ApplicationStatusEnum.RUNNING.getCode()) ||
                status.equals(ApplicationStatusEnum.SUCCESSFUL_CREATE_APPLICATION.getCode())) {
            Project projectInDb = projectDao.findById(applicationInDb.getProjectId());
            // Check permissions of project
            List<Integer> permissions = new ArrayList<>();
            permissions.add(ProjectUserPermissionEnum.OPERATOR.getCode());
            projectService.checkProjectPermission(projectInDb, loginUser, permissions);
            String realExecutionUser = applicationInDb.getExecuteUser();
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

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<ApplicationTaskSimpleResponse> submitRulesAndUpdateRule(String jobId, List<Long> ruleIds, StringBuilder partition, String createUser, String executionUser
            , String nodeName, Long projectId, Long ruleGroupId, String fpsFileId, String fpsHashValue, String startupParam, String clusterName
            , String setFlag, Map<String, String> execParams, String execParamStr, StringBuilder runDate, StringBuilder splitBy, Integer invokeCode
            , Application pendingApplication, Long subSystemId, String partitionFullSize, Boolean engineReuse) {
        List<Rule> rules = ruleDao.findByIds(ruleIds);
        updateFilterToRuleDataSource(rules);
        return commonSubmitRules(jobId, rules, partition, createUser, executionUser, nodeName, projectId, ruleGroupId, fpsFileId, fpsHashValue, startupParam, clusterName, setFlag, execParams, execParamStr, runDate, splitBy, invokeCode, pendingApplication, subSystemId, partitionFullSize, engineReuse);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<ApplicationTaskSimpleResponse> submitRules(List<Rule> rules, SubmitRuleBaseInfo submitRuleBaseInfo) {
        return commonSubmitRules(submitRuleBaseInfo.getJobId(), rules, submitRuleBaseInfo.getPartition(), submitRuleBaseInfo.getCreateUser(), submitRuleBaseInfo.getExecutionUser()
                , submitRuleBaseInfo.getNodeName(), submitRuleBaseInfo.getProjectId(), submitRuleBaseInfo.getRuleGroupId(), submitRuleBaseInfo.getFpsFileId(), submitRuleBaseInfo.getFpsHashValue()
                , submitRuleBaseInfo.getStartupParam(), submitRuleBaseInfo.getClusterName(), submitRuleBaseInfo.getSetFlag(), submitRuleBaseInfo.getExecParams(), submitRuleBaseInfo.getExecParamStr()
                , submitRuleBaseInfo.getRunDate(), submitRuleBaseInfo.getSplitBy(), submitRuleBaseInfo.getInvokeCode(), submitRuleBaseInfo.getPendingApplication(), submitRuleBaseInfo.getSubSystemId()
                , submitRuleBaseInfo.getPartitionFullSize(), submitRuleBaseInfo.getEngineReuse());
    }

    /**
     * Generate jobs by rules and submit jobs to linkis.
     *
     * @param jobId
     * @param rules
     * @param partition
     * @param createUser
     * @param executionUser
     * @param nodeName
     * @param projectId
     * @param ruleGroupId
     * @param fpsFileId
     * @param fpsHashValue
     * @param execParams
     * @param runDate
     * @param splitBy
     * @param invokeCode
     * @param pendingApplication
     * @param subSystemId
     * @param partitionFullSize
     * @return
     * @throws UnExpectedRequestException
     */
    private GeneralResponse<ApplicationTaskSimpleResponse> commonSubmitRules(String jobId, List<Rule> rules, StringBuilder partition, String createUser, String executionUser
            , String nodeName, Long projectId, Long ruleGroupId, String fpsFileId, String fpsHashValue, String startupParam, String clusterName
            , String setFlag, Map<String, String> execParams, String execParamStr, StringBuilder runDate, StringBuilder splitBy, Integer invokeCode
            , Application pendingApplication, Long subSystemId, String partitionFullSize, Boolean engineReuse) {
        Date date = new Date();
        Application newApplication;
        Set<String> clusterNames = rules.stream().map(Rule::getRuleDataSources).flatMap(ruleDataSources -> ruleDataSources.stream()).map(RuleDataSource::getClusterName).collect(
                Collectors.toSet());
        if (pendingApplication != null) {
            newApplication = pendingApplication;
            clusterName = newApplication.getClusterName();
        } else {
            try {
                newApplication = outerExecutionService.generateApplicationInfo(createUser, executionUser, date, invokeCode, jobId);
            } catch (UnExpectedRequestException e) {
                LOGGER.error(e.getMessage(), e);
                return new GeneralResponse<>("500", e.getMessage(), null);
            }
            newApplication.setProjectName(rules.stream().map(rule -> rule.getProject().getName()).distinct().collect(Collectors.joining(SpecCharEnum.COMMA.getValue())));
            List<Long> ruleIds = rules.stream().map(Rule::getId).distinct().collect(Collectors.toList());
            setApplicationBaseInfo(ruleIds, partition, nodeName, projectId, ruleGroupId, fpsFileId, fpsHashValue, startupParam, clusterName, setFlag, execParams, execParamStr, runDate, splitBy, subSystemId, newApplication, engineReuse);
        }

        // Check linkis how many tasks can be accepted, if 0, skip this submit.
        if (linkisConfig.getUnDoneSwitch() != null && linkisConfig.getUnDoneSwitch()) {
            if (StringUtils.isNotBlank(clusterName)) {
                boolean accept = checkLinkisAccept(newApplication, clusterName, executionUser);
                if (!accept) {
                    return new GeneralResponse<>("200", "Add pending application successfully.",
                            new ApplicationTaskSimpleResponse(newApplication.getId()));
                }
            } else {
                // Rules must divide into clusters and check.
                for (String currentClusterName : clusterNames) {
                    boolean accept = checkLinkisAccept(newApplication, currentClusterName, executionUser);
                    if (!accept) {
                        return new GeneralResponse<>("200", "Add pending application successfully.",
                                new ApplicationTaskSimpleResponse(newApplication.getId()));
                    }
                }
            }
        }

        ApplicationTaskSimpleResponse response;
        try {
            response = outerExecutionService.commonExecution(rules, partition, executionUser, nodeName, fpsFileId, fpsHashValue, startupParam
                    , clusterName, setFlag, execParams, newApplication, date, runDate, splitBy, partitionFullSize, engineReuse, createUser);
        } catch (BothNullDatasourceException e) {
            List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.BOTH_NULL_ISSUES.getCode().toString())).collect(Collectors.toList());
            Integer code = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

            catchAndSolve(e, code, ApplicationStatusEnum.FINISHED.getCode(), rules, newApplication);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (LeftNullDatasourceException e) {
            List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.LEFT_NULL_DATA_ISSUES.getCode().toString())).collect(Collectors.toList());
            Integer code = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

            catchAndSolve(e, code, ApplicationStatusEnum.NOT_PASS.getCode(), rules, newApplication);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (RightNullDatasourceException e) {
            List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.RIGHT_NULL_DATA_ISSUES.getCode().toString())).collect(Collectors.toList());
            Integer code = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

            catchAndSolve(e, code, ApplicationStatusEnum.NOT_PASS.getCode(), rules, newApplication);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (MetaDataAcquireFailedException e) {
            List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.METADATA_ISSUES.getCode().toString())).collect(Collectors.toList());
            Integer code = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

            catchAndSolve(e, code, ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules, newApplication);
            return new GeneralResponse<>("500", "{&THE_CHECK_FIELD_HAS_BEEN_MODIFIED}", null);
        } catch (DataSourceMoveException e) {
            List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.PERMISSION_ISSUES.getCode().toString())).collect(Collectors.toList());
            Integer code = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

            catchAndSolve(e, code, ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules, newApplication);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (DataSourceOverSizeException e) {
            List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.PERMISSION_ISSUES.getCode().toString())).collect(Collectors.toList());
            Integer code = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

            catchAndSolve(e, code, ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules, newApplication);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (ParseException e) {
            List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.GRAMMAR_ISSUES.getCode().toString())).collect(Collectors.toList());
            Integer code = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

            catchAndSolve(e, code, ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules, newApplication);
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "{&PARSE_SQL_FAILED_PLEASE_CHECKOUT_YOUR_CUSTOM_SQL}", null);
        } catch (JobSubmitException e) {
            Integer commentCode = ERR_CODE_TYPE.get(e.getErrCode());
            List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.UNKNOWN_ERROR_ISSUES.getCode().toString())).collect(Collectors.toList());
            Integer code = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

            catchAndSolve(e, commentCode != null ? commentCode : code, ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules, newApplication);
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(ApplicationCommentEnum.UNKNOWN_ERROR_ISSUES.getCode().toString())).collect(Collectors.toList());
            Integer code = CollectionUtils.isNotEmpty(collect) ? collect.get(0).getCode() : null;

            catchAndSolve(e, code, ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode(), rules, newApplication);
            return new GeneralResponse<>("500", e.getMessage(), null);
        }
        LOGGER.info("Succeed to dispatch task. response: {}", response);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_DISPATCH_TASK}", response);
    }

    private void updateFilterToRuleDataSource(List<Rule> rules) {
        for (Rule rule : rules) {
            if (StringUtils.isNotBlank(rule.getExecutionParametersName())) {
                ExecutionParameters executionParametersInDb = executionParametersDao.findByNameAndProjectId(rule.getExecutionParametersName(), rule.getProject().getId());

                if (Boolean.TRUE.equals(executionParametersInDb.getSpecifyFilter())) {
                    if (RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode().equals(rule.getRuleType()) && StringUtils.isNotEmpty(executionParametersInDb.getFilter())) {
                        rule.getRuleDataSources().forEach((ruleDataSource) -> {
                            ruleDataSource.setFilter(executionParametersInDb.getFilter());
                        });
                        ArrayList<RuleDataSource> pendingUpdateList = Lists.newArrayList(rule.getRuleDataSources());
                        ruleDataSourceDao.saveAllRuleDataSource(pendingUpdateList);
                    } else if (RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode().equals(rule.getRuleType())) {
                        rule.getRuleDataSources().forEach((ruleDataSource) -> {
                            if (QualitisConstants.LEFT_INDEX.equals(ruleDataSource.getDatasourceIndex()) && StringUtils.isNotEmpty(executionParametersInDb.getSourceTableFilter())) {
                                ruleDataSource.setFilter(executionParametersInDb.getSourceTableFilter());
                            }
                            if (QualitisConstants.RIGHT_INDEX.equals(ruleDataSource.getDatasourceIndex()) && StringUtils.isNotEmpty(executionParametersInDb.getTargetTableFilter())) {
                                ruleDataSource.setFilter(executionParametersInDb.getTargetTableFilter());
                            }
                        });
                        ArrayList<RuleDataSource> pendingUpdateList = Lists.newArrayList(rule.getRuleDataSources());
                        ruleDataSourceDao.saveAllRuleDataSource(pendingUpdateList);
                    }

                }

            }

        }
    }

    private void setApplicationBaseInfo(List<Long> ruleIds, StringBuilder partition, String nodeName, Long projectId, Long ruleGroupId, String fpsFileId, String fpsHashValue, String startupParam, String clusterName, String setFlag, Map<String, String> execParams, String execParamStr, StringBuilder runDate, StringBuilder splitBy, Long subSystemId, Application newApplication, Boolean engineReuse) {
        newApplication.setProjectId(projectId);
        newApplication.setRuleGroupId(ruleGroupId);
        newApplication.setPartition(partition.toString());

        newApplication.setClusterName(clusterName);
        newApplication.setStartupParam(startupParam);
        newApplication.setExecutionParam(execParamStr);

        newApplication.setSetFlag(setFlag);
        newApplication.setFpsFileId(fpsFileId);
        newApplication.setFpsHashValue(fpsHashValue);
        newApplication.setRunDate(runDate.toString());

        newApplication.setNodeName(nodeName);
        newApplication.setEngineReuse(engineReuse);
        newApplication.setSubSystemId(subSystemId);
        newApplication.setSplitBy(splitBy.toString());
        newApplication.setExecutionParamJson(GSON.toJson(execParams));
        newApplication.setRuleIds(Arrays.toString(ruleIds.toArray()));
    }

    private boolean checkLinkisAccept(Application newApplication, String clusterName, String executionUser) {
        try {
            int undoneTask = metaDataClient.getUndoneTaskTotal(clusterName, executionUser);
            if (linkisConfig.getMaxUnDone() - undoneTask <= 0) {
                if (!ApplicationStatusEnum.SUBMIT_PENDING.getCode().equals(newApplication.getStatus())) {
                    newApplication.setStatus(ApplicationStatusEnum.SUBMIT_PENDING.getCode());
                    applicationDao.saveApplication(newApplication);
                }
                LOGGER.info("Add pending application successfully. Qualitis application ID[{}]", newApplication.getId());
                return false;
            } else {
                LOGGER.info("Submitting application. Qualitis application ID[{}]", newApplication.getId());
                return true;
            }
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    private void catchAndSolve(Exception e, Integer commentCode, Integer statusCode, CheckAlert checkAlert, Application newApplication) {
        LOGGER.error(e.getMessage(), e);
        newApplication.setStatus(statusCode);
        newApplication.setApplicationComment(commentCode);

        // Record checkalert info and datasource info.
        StringBuilder info = new StringBuilder();
        info.append("Checkalert[ ").append(checkAlert.toString()).append("] ");
        newApplication.setExceptionMessage(info.append(ExceptionUtils.getStackTrace(e)).toString());

        // 初始化失败任务必告警
        if (ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode().equals(newApplication.getStatus())) {
            AlarmUtil.sendInitFailedMessage(newApplication, checkAlert, imsConfig, imsAlarmClient, alarmInfoDao);
        }

        applicationDao.saveApplication(newApplication);
        LOGGER.info("Succeed to set application status to [{}], application ID: [{}]", newApplication.getStatus(), newApplication.getId());
    }

    private void catchAndSolve(Exception e, Integer commentCode, Integer statusCode, List<Rule> rules, Application newApplication) {
        LOGGER.error(e.getMessage(), e);
        newApplication.setStatus(statusCode);
        newApplication.setApplicationComment(commentCode);

        // Record rules info and datasource info.
        StringBuilder info = new StringBuilder();
        newApplication.setFinishTime(QualitisConstants.PRINT_TIME_FORMAT.format(new Date()));
        for (Rule rule : rules) {
            info.append("Rule[ ").append(rule.getName()).append("; ");
            info.append("datasource: ").append(rule.getRuleDataSources().stream()
                    .filter(ruleDataSource -> StringUtils.isNotBlank(ruleDataSource.getDbName()) && StringUtils.isNotBlank(ruleDataSource.getTableName()))
                    .map(ruleDataSource -> ruleDataSource.getDbName() + "." + ruleDataSource.getTableName() + " ")
                    .collect(Collectors.joining())).append("]\n");
            List<RuleMetric> ruleMetrics = rule.getAlarmConfigs().stream().map(AlarmConfig::getRuleMetric).filter(ruleMetric -> ruleMetric != null).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(ruleMetrics) && rule.getAlert() != null && rule.getAlert()) {
                RuleMetric ruleMetric = ruleMetrics.iterator().next();

                int execNum = 1;
                int alarmNum = 0;
                long currentTime = System.currentTimeMillis();
                int subSystemId = QualitisConstants.SUB_SYSTEM_ID;

                if (ruleMetric.getSubSystemId() != null) {
                    subSystemId = ruleMetric.getSubSystemId();
                }
                String departmentName = ruleMetric.getDevDepartmentName();
                String datasourceType = TemplateDataSourceTypeEnum.getMessage(rule.getRuleDataSources().iterator().next().getDatasourceType());
                for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
                    if (StringUtils.isBlank(ruleDataSource.getDbName()) || StringUtils.isBlank(ruleDataSource.getTableName())) {
                        continue;
                    }
                    String nowDate = QualitisConstants.PRINT_DATE_FORMAT.format(new Date(currentTime));
                    AbnormalDataRecordInfo abnormalDataRecordInfoExists = abnormalDataRecordInfoDao.findByPrimary(rule.getId(), ruleDataSource.getDbName(), ruleDataSource.getTableName(), nowDate);
                    String standardRuleName = "DQM-" + rule.getProject().getName() + "-" + rule.getName() + "-" + rule.getTemplate().getName();
                    String standardRuleDetail = rule.getProject().getName() + "-" + rule.getName() + "-" + rule.getTemplate().getName();
                    if (abnormalDataRecordInfoExists == null) {
                        AbnormalDataRecordInfo abnormalDataRecordInfo = new AbnormalDataRecordInfo(rule.getId(), standardRuleName, datasourceType
                                , ruleDataSource.getDbName(), ruleDataSource.getTableName(), departmentName, subSystemId, execNum, alarmNum);
                        abnormalDataRecordInfo.setRuleDetail(StringUtils.isEmpty(rule.getDetail()) ? standardRuleDetail : rule.getDetail());

                        abnormalDataRecordInfo.setRecordDate(nowDate);
                        abnormalDataRecordInfo.setRecordTime(QualitisConstants.PRINT_TIME_FORMAT.format(currentTime));

                        abnormalDataRecordInfoDao.save(abnormalDataRecordInfo);
                    } else {
                        abnormalDataRecordInfoExists.setRuleName(standardRuleName);
                        abnormalDataRecordInfoExists.setRuleDetail(StringUtils.isEmpty(rule.getDetail()) ? standardRuleDetail : rule.getDetail());
                        abnormalDataRecordInfoExists.setRecordTime(QualitisConstants.PRINT_TIME_FORMAT.format(currentTime));
                        abnormalDataRecordInfoExists.setExecuteNum(abnormalDataRecordInfoExists.getExecuteNum() + execNum);
                        abnormalDataRecordInfoExists.setEventNum(abnormalDataRecordInfoExists.getEventNum() + alarmNum);
                        abnormalDataRecordInfoExists.setDepartmentName(departmentName);
                        abnormalDataRecordInfoExists.setDatasource(datasourceType);
                        abnormalDataRecordInfoExists.setSubSystemId(subSystemId);
                        abnormalDataRecordInfoDao.save(abnormalDataRecordInfoExists);
                    }
                }
            }

        }
        newApplication.setExceptionMessage(info.append(ExceptionUtils.getStackTrace(e)).toString());

        if (CollectionUtils.isNotEmpty(rules)) {
            String ruleNames = rules.stream().map(rule -> rule.getName()).distinct().collect(Collectors.joining(SpecCharEnum.COMMA.getValue()));

            newApplication.setRuleNames(ruleNames);
            newApplication.setProjectName(rules.stream().map(rule -> StringUtils.isNotEmpty(rule.getProject().getCnName()) ? rule.getProject().getCnName() : rule.getProject().getName()).distinct().collect(Collectors.joining(SpecCharEnum.COMMA.getValue())));
            newApplication.setClusterName(rules.stream().map(Rule::getRuleDataSources).flatMap(ruleDataSources -> ruleDataSources.stream()).filter(ruleDataSource -> StringUtils.isNotBlank(ruleDataSource.getClusterName())).iterator().next().getClusterName());

            newApplication.setRuleGroupName(rules.iterator().next().getRuleGroup().getRuleGroupName());
            newApplication.setRuleDatesource(rules.stream().map(Rule::getRuleDataSources).flatMap(ruleDataSources -> ruleDataSources.stream())
                    .filter(ruleDataSource -> StringUtils.isNotBlank(ruleDataSource.getDbName()) && StringUtils.isNotBlank(ruleDataSource.getTableName()))
                    .map(ruleDataSource -> ruleDataSource.getDbName() + "." + ruleDataSource.getTableName() + " ")
                    .collect(Collectors.joining()));
            // 初始化失败任务必告警
            if (ApplicationStatusEnum.TASK_SUBMIT_FAILED.getCode().equals(newApplication.getStatus()) || ApplicationStatusEnum.NOT_PASS.getCode().equals(newApplication.getStatus())) {
                List<ApplicationComment> collect = APPLICATION_COMMENT_LIST.stream().filter(item -> item.getCode().toString().equals(String.valueOf(commentCode))).collect(Collectors.toList());
                AlarmUtil.sendInitFailedMessage(newApplication, CollectionUtils.isNotEmpty(collect) ? collect.get(0) : null, rules, imsConfig, imsAlarmClient, alarmInfoDao);
            }
        }
        applicationDao.saveApplication(newApplication);

        LOGGER.info("Succeed to set application status to [{}], application ID: [{}]", newApplication.getStatus(), newApplication.getId());
    }

    @Override
    public Application generateApplicationInfo(String createUser, String executionUser, Date date, Integer invokeCode, String jobId) throws UnExpectedRequestException {
        String nonce = UuidGenerator.generateRandom(QualitisConstants.APPLICATION_RANDOM_LENGTH);
        String submitTime = QualitisConstants.PRINT_TIME_FORMAT.format(date);

        String applicationId = generateTaskId(date, nonce);
        LOGGER.info("Succeed to generate application ID: {}", applicationId);


        Application application = new Application();
        application.setJobId(jobId);
        application.setId(applicationId);
        application.setSubmitTime(submitTime);
        application.setInvokeType(invokeCode);
        application.setCreateUser(createUser);
        application.setExecuteUser(executionUser);
        String ip;
        // Get tenant user from execution user's department
        User user = userDao.findByUsername(executionUser);
        Department department = user.getDepartment();
        if (department != null) {
            TenantUser tenantUser = department.getTenantUser();
            if (tenantUser != null) {
                application.setTenantUserName(tenantUser.getTenantName());

                List<String> ipList = tenantUser.getServiceInfos().stream().filter(u -> ServiceInfoStatusEnum.RUNNING.getCode().equals(u.getStatus())).map(u -> u.getIp()).collect(Collectors.toList());
                ip = sortAndGetMostIdleIp(ipList);
                LOGGER.info("Gnerate application with ip tag: " + ip);

                application.setIp(ip);
                return applicationDao.saveApplication(application);
            }
        }

        List<ServiceInfo> serviceInfos = serviceInfoDao.findByStatus(ServiceInfoStatusEnum.RUNNING.getCode());
        if (CollectionUtils.isEmpty(serviceInfos) || serviceInfos.size() == 1) {
            ip = QualitisConstants.QUALITIS_SERVER_HOST;
        } else {
            List<String> ipList = serviceInfos.stream().filter(serviceInfo -> serviceInfo.getTenantUser() == null).map(serviceInfo -> serviceInfo.getIp()).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(ipList)) {
                throw new UnExpectedRequestException("No tenant, no service");
            }
            ip = sortAndGetMostIdleIp(ipList);
        }
        LOGGER.info("Gnerate application with ip tag: " + ip);

        application.setIp(ip);
        return applicationDao.saveApplication(application);
    }

    private String sortAndGetMostIdleIp(List<String> ipList) {
        List<Map<String, Object>> fitMap = new ArrayList<>();
        for (String currentIp : ipList) {
            Map<String, Object> ipAndNum = new HashMap<>(2);
            ipAndNum.put("ip", currentIp);
            int num = applicationDao.countNotFinishApplicationNum(currentIp);
            ipAndNum.put("num", num);
            fitMap.add(ipAndNum);
        }
        if (fitMap.isEmpty()) {
            return ipList.get(secureRandom.nextInt(ipList.size()));
        } else {
            Collections.sort(fitMap, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    Integer oneNum = Integer.parseInt(o1.get("num").toString());
                    Integer secondNum = Integer.parseInt(o2.get("num").toString());
                    return oneNum.compareTo(secondNum);
                }
            });
            LOGGER.info("the least example ip with num: " + fitMap.get(0).get("ip") + ":" + fitMap.get(0).get("num"));
            return (String) fitMap.get(0).get("ip");
        }

    }

    private String generateTaskId(Date date, String nonce) {
        return "QUALITIS" + TASK_TIME_FORMAT.format(date) + "_" + nonce;
    }

    @Override
    public ApplicationTaskSimpleResponse commonExecution(List<Rule> rules, StringBuilder partition, String executionUser, String nodeName
            , String fpsFileId, String fpsHashValue, String startupParam, String clusterName, String setFlag, Map<String, String> execParams, Application newApplication
            , Date date, StringBuilder runDate, StringBuilder splitBy, String partitionFullSize, Boolean engineReuse, String createUser) throws Exception {
        // current user
        String userName = executionUser;

        // Generate database name.
        Map<Long, Map<String, Object>> ruleReplaceInfo = ruleReplaceInfo(userName, execParams, rules);

        // Save application
        newApplication.setRuleSize(rules.size());
        if (ApplicationStatusEnum.SUBMIT_PENDING.getCode().equals(newApplication.getStatus())) {
            newApplication.setStatus(ApplicationStatusEnum.SUCCESSFUL_CREATE_APPLICATION.getCode());
        }
        Application saveApplication = applicationDao.saveApplication(newApplication);

        List<TaskSubmitResult> taskSubmitResults = new ArrayList<>();
        List<Rule> fileRules = new ArrayList<>();
        List<String> leftCols = new ArrayList<>();
        List<String> rightCols = new ArrayList<>();
        List<String> comelexCols = new ArrayList<>();
        Map<Long, List<Map<String, Object>>> dataSourceMysqlConnect = new HashMap<>(2);
        handleRule(rules, partition, nodeName, fpsFileId, fpsHashValue, clusterName, execParams, newApplication, date, userName, ruleReplaceInfo, fileRules, dataSourceMysqlConnect, leftCols, rightCols, comelexCols, partitionFullSize);
        String submitTime = QualitisConstants.PRINT_TIME_FORMAT.format(date);
        // General task.
        if (!rules.isEmpty()) {
            taskSubmitResults.addAll(executionManager.submitApplication(rules, nodeName, submitTime, userName, ruleReplaceInfo, partition
                    , date, saveApplication, clusterName, startupParam, setFlag, execParams, runDate, splitBy, dataSourceMysqlConnect, newApplication.getTenantUserName(), leftCols, rightCols, comelexCols, createUser));
        }
        // Execute file rule task and save task result.
        List<Rule> fileShellRules = fileRules.stream().filter(rule -> "目录文件数".equals(rule.getTemplate().getName())).collect(Collectors.toList());
        if (! fileShellRules.isEmpty()) {
            taskSubmitResults.addAll(executionManager.executeFileRuleWithShell(fileShellRules, submitTime, saveApplication, userName, clusterName, runDate.toString(), ruleReplaceInfo, startupParam, engineReuse, EngineTypeEnum.DEFAULT_ENGINE.getMessage()));
            fileRules.removeAll(fileShellRules);
        }
        if (!fileRules.isEmpty()) {
            taskSubmitResults.add(executionManager.executeFileRule(fileRules, submitTime, saveApplication, userName, clusterName, runDate, ruleReplaceInfo));
        }
        saveApplication.setTotalTaskNum(taskSubmitResults.size());
        LOGGER.info("Succeed to submit application. result: {}", taskSubmitResults);
        Application applicationInDb = applicationDao.saveApplication(saveApplication);
        LOGGER.info("Succeed to save application. application: {}", applicationInDb);
        return new ApplicationTaskSimpleResponse(taskSubmitResults);
    }

    private void handleRule(List<Rule> rules, StringBuilder partition, String nodeName, String fpsFileId, String fpsHashValue, String clusterName,
                            Map<String, String> execParams, Application newApplication, Date date, String userName, Map<Long, Map<String, Object>> ruleReplaceInfo,
                            List<Rule> fileRules, Map<Long, List<Map<String, Object>>> dataSourceMysqlConnect, List<String> leftCols, List<String> rightCols,
                            List<String> comelexCols, String partitionFullSize) throws Exception {
        for (Iterator<Rule> iterator = rules.iterator(); iterator.hasNext(); ) {
            Rule currentRule = iterator.next();
            if (currentRule.getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
                // Replace with execution parameter and parse datasource to save.
                customReSaveDateSource(currentRule, execParams, clusterName, date);
            }
            List<Map<String, String>> mappingCols = new ArrayList<>();
            getMappingCols(currentRule, mappingCols);
            // Check datasource before submit job.
            try {
                checkDatasource(currentRule, userName, partition, mappingCols, fpsFileId, fpsHashValue, nodeName, clusterName, dataSourceMysqlConnect, leftCols, rightCols, comelexCols, partitionFullSize);
            } catch (BothNullDatasourceException e) {
                Task taskInDb = taskDao.save(new Task(newApplication, newApplication.getSubmitTime(), TaskStatusEnum.PASS_CHECKOUT.getCode()));
                taskInDb.setClusterName(clusterName);
                TaskRuleSimple taskRuleSimple = new TaskRuleSimple(currentRule, taskInDb, ruleReplaceInfo);

                Set<TaskDataSource> taskDataSources = new HashSet<>(fileRules.size());
                Set<TaskRuleSimple> taskRuleSimples = new HashSet<>(fileRules.size());

                taskRuleSimples.add(taskRuleSimpleDao.save(taskRuleSimple));

                for (RuleDataSource ruleDataSource : currentRule.getRuleDataSources()) {
                    taskDataSources.add(taskDataSourceRepository.save(new TaskDataSource(ruleDataSource, taskInDb)));
                }

                taskInDb.setEndTime(QualitisConstants.PRINT_TIME_FORMAT.format(new Date()));
                taskInDb.setTaskDataSources(taskDataSources);
                taskInDb.setTaskRuleSimples(taskRuleSimples);
                TaskResult taskResult = new TaskResult();

                taskResult.setCreateTime(newApplication.getSubmitTime());
                taskResult.setApplicationId(newApplication.getId());
                taskResult.setRuleId(currentRule.getId());
                taskResult.setResultType("Long");
                taskResult.setValue(0 + "");

                taskResultDao.saveTaskResult(taskResult);
                taskDao.save(taskInDb);
                iterator.remove();

                if (!iterator.hasNext()) {
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
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public void saveGatewayJobInfo(String jobId, int applicationNum) {
        GatewayJobInfo gatewayJobInfo = new GatewayJobInfo(jobId, applicationNum, linkisConfig.getGatewayQueryTimeout());
        gatewayJobInfoDao.saveAndFlush(gatewayJobInfo);
    }

    @Override
    public GeneralResponse<GatewayJobInfoResponse> queryJobInfo(String jobId) throws UnExpectedRequestException {
        if (StringUtils.isEmpty(jobId)) {
            throw new UnExpectedRequestException("Job ID " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        GatewayJobInfo gatewayJobInfo = gatewayJobInfoDao.getByJobId(jobId);
        if (gatewayJobInfo == null) {
            return new GeneralResponse<>("200", "Gateway job info is preparing.", null);
        }
        GatewayJobInfoResponse response = new GatewayJobInfoResponse(gatewayJobInfo.getApplicationNum(), gatewayJobInfo.getApplicationQueryTimeOut());
        return new GeneralResponse<>("200", "Success to get job info.", response);
    }

    @Override
    public GeneralResponse<ApplicationProjectResponse> queryApplications(String jobId) throws UnExpectedRequestException, java.text.ParseException {
        if (StringUtils.isEmpty(jobId)) {
            throw new UnExpectedRequestException("Job ID " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        LOGGER.info("Start to query applications with jobId: {}", jobId);
        List<Application> applicationList = gatewayJobInfoDao.getAllApplication(jobId);
        ApplicationProjectResponse applicationProjectResponse = new ApplicationProjectResponse();
        boolean first = false;
        for (Application application : applicationList) {
            if (!first) {
                first = true;
                SimpleDateFormat applicationTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Long lastSubmitTime = applicationTimeFormat.parse(application.getSubmitTime()).getTime();
                applicationProjectResponse.setLastSubmitTime(lastSubmitTime);
            }
            ApplicationTaskSimpleResponse applicationTaskSimpleResponse = new ApplicationTaskSimpleResponse(application.getId(), application.getStatus());
            applicationProjectResponse.getApplicationTaskSimpleResponses().add(applicationTaskSimpleResponse);
        }
        return new GeneralResponse<>("200", "Success to query application tasks", applicationProjectResponse);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public void deleteRule(BatchDeleteRuleRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        BatchDeleteRuleRequest.checkRequest(request);
        //权限检验没做判断，  目前loginUser为null
        List<Long> ruleIdList = request.getRuleIdList();
        for (Long ruleId : ruleIdList) {
            // Check existence of rule
            Rule ruleInDb = ruleDao.findById(ruleId);
            if (ruleInDb == null) {
                throw new UnExpectedRequestException("rule_id [" + ruleId + "] {&DOES_NOT_EXIST}");
            }
            // Check existence of project
            Project projectInDb = projectService.checkProjectExistence(ruleInDb.getProject().getId(),
                    request.getLoginUser());
            // Check permissions of project
            List<Integer> permissions = new ArrayList<>();
            permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
            projectService.checkProjectPermission(projectInDb, request.getLoginUser(), permissions);

            if (ruleInDb.getRuleType().equals(RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode())) {
                SpringContextHolder.getBean(RuleService.class).deleteRuleReal(ruleInDb);
            } else if (ruleInDb.getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
                SpringContextHolder.getBean(CustomRuleService.class).deleteCustomRuleReal(ruleInDb);
            } else if (ruleInDb.getRuleType().equals(RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode())) {
                SpringContextHolder.getBean(MultiSourceRuleService.class).deleteMultiRuleReal(ruleInDb);
            } else if (ruleInDb.getRuleType().equals(RuleTypeEnum.FILE_TEMPLATE_RULE.getCode())) {
                SpringContextHolder.getBean(FileRuleService.class).deleteRuleReal(ruleInDb);
            }
        }

    }

    @Override
    public void reSubmitCheckAlertGroup(String executeUser, Long ruleGroupId, Long projectId, String executionParam) throws UnExpectedRequestException, PermissionDeniedRequestException {
        handleCheckAlert(executeUser, ruleGroupDao.findById(ruleGroupId), projectDao.findById(projectId), executionParam);
    }

    private void customReSaveDateSource(Rule currentRule, Map<String, String> execParams, String clusterName, Date date)
            throws UnExpectedRequestException, SemanticException, ParseException {
        String midTableAction = currentRule.getTemplate().getMidTableAction();
        if (StringUtils.isNotBlank(currentRule.getWhereContent())) {
            midTableAction = midTableAction.replace("${filter}", currentRule.getWhereContent());
        }
        // Replace placeholder with key and value for sql parse check.
        for (Map.Entry<String, String> entry : execParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            midTableAction = midTableAction.replace("${" + key + "}", value);
        }
        midTableAction = DateExprReplaceUtil.replaceRunDate(date, midTableAction);
        // Save datasource in first execution(Only possible data sources related to fps).
        boolean firstExecution = CollectionUtils.isEmpty(currentRule.getRuleDataSources().stream().filter(ruleDataSource -> !ORIGINAL_INDEX.equals(ruleDataSource.getDatasourceIndex())).collect(
                Collectors.toSet()));
        boolean fpsExecution = CollectionUtils.isNotEmpty(currentRule.getRuleDataSources())
                && CollectionUtils.isEmpty(currentRule.getRuleDataSources().stream().filter(ruleDataSource -> StringUtils.isEmpty(ruleDataSource.getFileId())).collect(
                Collectors.toList()));
        if (!firstExecution || fpsExecution) {
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
        Integer datasourceType = TemplateDataSourceTypeEnum.HIVE.getCode();
        if (originalRuleDataSource.getLinkisDataSourceId() != null) {
            datasourceType = originalRuleDataSource.getDatasourceType();
            DbType dbType = JdbcConstants.MYSQL;
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
        for (Map.Entry<String, List<String>> entry : dbAndTables.entrySet()) {
            String db = entry.getKey();
            List<String> tables = entry.getValue();
            List<RuleDataSource> ruleDataSources = new ArrayList<>();
            for (String table : tables) {
                RuleDataSource ruleDataSource = new RuleDataSource();

                ruleDataSource.setClusterName(clusterName);
                ruleDataSource.setProjectId(currentRule.getProject().getId());
                ruleDataSource.setProxyUser(originalRuleDataSource.getProxyUser());
                ruleDataSource.setDatasourceType(datasourceType);
                ruleDataSource.setDbName(db.toLowerCase());
                ruleDataSource.setRule(currentRule);
                ruleDataSource.setTableName(table);
                ruleDataSources.add(ruleDataSource);
            }
            Set<RuleDataSource> ruleDataSourcesExist = currentRule.getRuleDataSources();
            ruleDataSourcesExist.addAll(ruleDataSourceDao.saveAllRuleDataSource(ruleDataSources).stream().collect(Collectors.toSet()));
            currentRule.setRuleDataSources(ruleDataSourcesExist);
            ruleDao.saveRule(currentRule);
        }
    }

    private Map<String, List<String>> toDbAndTables(Map<String, List<String>> dbAndTableMap, Map<Name, TableStat> tableStatMap)
            throws UnExpectedRequestException {
        for (Name dbAndTable : tableStatMap.keySet()) {
            String[] dbAndTables = dbAndTable.getName().split("\\.");
            if (dbAndTables.length < 2) {
                throw new UnExpectedRequestException("Please use 'db.table' in sql.");
            }
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

    private void checkDatasource(Rule currentRule, String userName, StringBuilder partition, List<Map<String, String>> mappingCols, String fpsFileId
            , String fpsHashValue, String nodeName, String clusterName, Map<Long, List<Map<String, Object>>> dataSourceMysqlConnect, List<String> leftCols
            , List<String> rightCols, List<String> comelexCols, String partitionFullSize) throws Exception {
        // For multi source rule to check tables' size before submit.
        List<Double> datasourceSizeList = new ArrayList<>(currentRule.getRuleDataSources().size());

        List<RuleDataSource> currentRuleDataSources = currentRule.getRuleDataSources().stream().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(currentRule.getRuleDataSources()) && RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode().equals(currentRule.getRuleType())) {
            currentRuleDataSources = currentRuleDataSources.stream().sorted(Comparator.comparing(RuleDataSource::getDatasourceIndex)).collect(Collectors.toList());
        }
        for (RuleDataSource ruleDataSource : currentRuleDataSources) {
            Map<String, String> mappingCol = null;
            // Get all env connect params.
            List<Map<String, Object>> connectParamMaps = new ArrayList<>();
            if (ORIGINAL_INDEX.equals(ruleDataSource.getDatasourceIndex())) {
                if (CollectionUtils.isNotEmpty(ruleDataSource.getRuleDataSourceEnvs())) {
                    getAllEnvConnectParams(connectParamMaps, ruleDataSource);
                    dataSourceMysqlConnect.put(ruleDataSource.getId(), connectParamMaps);
                } else if (ruleDataSource.getLinkisDataSourceId() != null) {
                    // Before 0.21.0 rdms execution
                    getDatasourceMainParams(connectParamMaps, clusterName, ruleDataSource);
                    dataSourceMysqlConnect.put(ruleDataSource.getId(), connectParamMaps);
                }
                boolean fps = ruleDataSource != null && StringUtils.isNotBlank(ruleDataSource.getFileId());
                if (fps) {
                    LOGGER.info("Start to prepare fps params in rule datasource service.");
                    // Upload fps file.
                    String fpsExecUser = StringUtils.isNotBlank(ruleDataSource.getProxyUser()) ? ruleDataSource.getProxyUser() : userName;
                    checkAndUploadFpsFile(fpsExecUser, fpsFileId, fpsHashValue, ruleDataSource, clusterName);
                    // Save the last fps file infomation.
                    ruleDataSourceDao.saveRuleDataSource(ruleDataSource);
                }
                continue;
            }
            if (RuleTypeEnum.CUSTOM_RULE.getCode().equals(currentRule.getRuleType())) {
                continue;
            }
            if (ruleDataSource.getDatasourceIndex() != null && mappingCols.get(ruleDataSource.getDatasourceIndex()).size() > 0) {
                mappingCol = mappingCols.get(ruleDataSource.getDatasourceIndex());
            }
            if (ruleDataSource.getLinkisDataSourceId() != null) {
                LOGGER.info("Start to solve relationship datasource info.");
                if (CollectionUtils.isNotEmpty(ruleDataSource.getRuleDataSourceEnvs())) {
                    getAllEnvConnectParams(connectParamMaps, ruleDataSource);
                } else {
                    // Before 0.21.0 rdms execution
                    getDatasourceMainParams(connectParamMaps, clusterName, ruleDataSource);
                }
                dataSourceMysqlConnect.put(ruleDataSource.getId(), connectParamMaps);
                continue;
            }

            // Fps file check.
            boolean fps = ruleDataSource != null && StringUtils.isNotBlank(ruleDataSource.getFileId());
            checkTableOrPartition(currentRule, userName, partition, fpsFileId, fpsHashValue, nodeName, clusterName, datasourceSizeList, ruleDataSource, mappingCol, leftCols, rightCols, comelexCols, fps, partitionFullSize);
        }
        if (CollectionUtils.isNotEmpty(datasourceSizeList) && currentRule.getRuleType().equals(RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode())) {
            if (datasourceSizeList.size() != DATASOURCE_SIZE) {
                return;
            }
            double left = datasourceSizeList.get(0);
            double right = datasourceSizeList.get(1);
            LOGGER.info("Current multi source rule left table size number[{}], right table size number[{}]", left, right);

            if (BigDecimal.ZERO.compareTo(BigDecimal.valueOf(left)) == 0 && BigDecimal.ZERO.compareTo(BigDecimal.valueOf(right)) == 0) {
                throw new BothNullDatasourceException("{&BOTH_SIDE_ARE_NULL}");
            } else if (BigDecimal.ZERO.compareTo(BigDecimal.valueOf(left)) == 0) {
                throw new LeftNullDatasourceException("{&ONE_SIDE_ARE_NULL}");
            } else if (BigDecimal.ZERO.compareTo(BigDecimal.valueOf(right)) == 0) {
                throw new RightNullDatasourceException("{&ONE_SIDE_ARE_NULL}");
            }
        }
    }

    private void getDatasourceMainParams(List<Map<String, Object>> connectParamMaps, String clusterName, RuleDataSource ruleDataSource) throws Exception {
        GeneralResponse<Map<String, Object>> dataSourceInfoDetail = metaDataClient.getDataSourceInfoDetail(StringUtils.isNotBlank(clusterName) ? clusterName : ruleDataSource.getClusterName(), linkisConfig.getDatasourceAdmin(), ruleDataSource.getLinkisDataSourceId(), ruleDataSource.getLinkisDataSourceVersionId());
        GeneralResponse<Map<String, Object>> dataSourceConnectParams = metaDataClient.getDataSourceConnectParams(StringUtils.isNotBlank(clusterName) ? clusterName : ruleDataSource.getClusterName(), linkisConfig.getDatasourceAdmin(), ruleDataSource.getLinkisDataSourceId(), ruleDataSource.getLinkisDataSourceVersionId());
        Map<String, Object> connectParamsReal = (Map<String, Object>) dataSourceConnectParams.getData().get("connectParams");
        if (connectParamsReal.size() == 0) {
            throw new UnExpectedRequestException("{&THE_DATASOURCE_IS_NOT_DEPLOYED}");
        }
        Map<String, Object> connectParams = (Map<String, Object>) ((Map<String, Object>) dataSourceInfoDetail.getData().get("info")).get("connectParams");
        String dataType = (String) ((Map<String, Object>) ((Map<String, Object>) dataSourceInfoDetail.getData().get("info")).get("dataSourceType")).get("name");
        connectParams.put("envName", QualitisConstants.UNION_ALL);
        connectParams.put("needDecode", "false");
        connectParams.put("dataType", dataType);
        connectParamMaps.add(connectParams);
    }

    private void getAllEnvConnectParams(List<Map<String, Object>> connectParamMaps, RuleDataSource ruleDataSource) throws Exception {
        List<RuleDataSourceEnv> envList = ruleDataSource.getRuleDataSourceEnvs();
        GeneralResponse<Map<String, Object>> dataSourceInfoDetail = metaDataClient.getDataSourceInfoDetail(linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin(), ruleDataSource.getLinkisDataSourceId()
                , ruleDataSource.getLinkisDataSourceVersionId());

        checkRdmsSqlMetaInfo(linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin(), ruleDataSource);

        for (RuleDataSourceEnv env : envList) {
            GeneralResponse<Map<String, Object>> envMap = metaDataClient.getDatasourceEnvById(linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin(), env.getEnvId());

            Map<String, Object> connectParams = (Map<String, Object>) ((Map<String, Object>) envMap.getData().get("env")).get("connectParams");
            Map<String, Object> shareConnectParams = (Map<String, Object>) ((Map<String, Object>) dataSourceInfoDetail.getData().get("info")).get("connectParams");
            String dataType = (String) ((Map<String, Object>) ((Map<String, Object>) dataSourceInfoDetail.getData().get("info")).get("dataSourceType")).get("name");
            connectParams.put("dataType", dataType);
            // Put env name in connectParams, get("envName")
            connectParams.put("envName", env.getEnvName());
            if (Boolean.TRUE.equals(shareConnectParams.get("share"))) {
                connectParams.put("authType", shareConnectParams.get("authType"));
                if ("accountPwd".equals(shareConnectParams.get("authType"))) {
                    connectParams.put("username", shareConnectParams.get("username"));
                    connectParams.put("password", CryptoUtils.encode(shareConnectParams.get("password").toString()));
                } else {
                    connectParams.put("dk", shareConnectParams.get("dk"));
                    connectParams.put("appid", shareConnectParams.get("appid"));
                    connectParams.put("objectid", shareConnectParams.get("objectid"));
                    connectParams.put("timestamp", shareConnectParams.get("timestamp"));
                }
            }
            connectParamMaps.add(connectParams);
        }
    }

    private void checkTableOrPartition(Rule currentRule, String userName, StringBuilder partition, String fpsFileId, String fpsHashValue,
                                       String nodeName, String clusterName, List<Double> datasourceSizeList, RuleDataSource ruleDataSource, Map<String, String> mappingCol,
                                       List<String> leftCols, List<String> rightCols, List<String> complexCols, boolean fps, String partitionFullSize) throws Exception {
        if (fps) {
            LOGGER.info("Start to prepare fps params in rule datasource service.");
            // Upload fps file.
            String fpsExecUser = StringUtils.isNotBlank(ruleDataSource.getProxyUser())? ruleDataSource.getProxyUser(): userName;
            checkAndUploadFpsFile(fpsExecUser, fpsFileId, fpsHashValue, ruleDataSource, clusterName);
            // Save the last fps file infomation.
            ruleDataSourceDao.saveRuleDataSource(ruleDataSource);
        } else {
            if (StringUtils.isNotBlank(ruleDataSource.getDbName()) && !ruleDataSource.getDbName().equals(RuleConstraintEnum.CUSTOM_DATABASE_PREFIS.getValue())) {
                // Get actual fields info.
                List<ColumnInfoDetail> cols;
                try {
                    cols = metaDataClient.getColumnInfo(StringUtils.isNotBlank(clusterName) ? clusterName : ruleDataSource.getClusterName(), ruleDataSource.getDbName(), ruleDataSource.getTableName(), userName);
                } catch (Exception e) {
                    throw new DataSourceMoveException("Table[" + ruleDataSource.getTableName() + "]. {&RULE_DATASOURCE_BE_MOVED}");
                }
                if (CollectionUtils.isEmpty(cols)) {
                    throw new DataSourceMoveException("Table[" + ruleDataSource.getTableName() + "]. {&RULE_DATASOURCE_BE_MOVED}");
                }
                if (currentRule.getRuleType().equals(RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode()) && QualitisConstants.LEFT_INDEX.equals(ruleDataSource.getDatasourceIndex())) {
                    String filterColName = ruleDataSource.getColName();
                    List<String> filterColNameList = new ArrayList<>();
                    if (StringUtils.isNotEmpty(filterColName)) {
                        String[] realColumns = filterColName.split(SpecCharEnum.VERTICAL_BAR.getValue());
                        for (String column : realColumns) {
                            String[] colInfo = column.split(SpecCharEnum.COLON.getValue());
                            String colName = colInfo[0];
                            filterColNameList.add(colName);
                        }
                    }
                    for (ColumnInfoDetail columnInfoDetail : cols) {
                        if (columnInfoDetail.getDataType().toLowerCase().startsWith(QualitisConstants.MAP_TYPE) || columnInfoDetail.getDataType().toLowerCase().startsWith(QualitisConstants.ARRAY_TYPE) || columnInfoDetail.getDataType().toLowerCase().startsWith(QualitisConstants.STRUCT_TYPE)) {
                            complexCols.add(columnInfoDetail.getFieldName());
                        }
                        if (CollectionUtils.isNotEmpty(filterColNameList) && !filterColNameList.contains(columnInfoDetail.getFieldName())) {
                            continue;
                        }
                        leftCols.add(currentRule.getId() + SpecCharEnum.MINUS.getValue() + columnInfoDetail.getFieldName());
                        rightCols.add(currentRule.getId() + SpecCharEnum.MINUS.getValue() + columnInfoDetail.getFieldName());
                    }
                }
                // Get actual partition fields.
                List<String> partitionFields = cols.stream().filter(ColumnInfoDetail::getPartitionField).map(ColumnInfoDetail::getFieldName).collect(Collectors.toList());
                if (RuleTypeEnum.FILE_TEMPLATE_RULE.getCode().equals(currentRule.getRuleType())) {
                    fileReSaveDataSource(partitionFields, ruleDataSource);
                }
                // Check filter fields.
                boolean partitionTable = CollectionUtils.isNotEmpty(partitionFields);
                if (!(partitionTable && partition.length() > 0)) {
                    partition.delete(0, partition.length());
                }

                ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(StringUtils.isNotBlank(clusterName) ? clusterName : ruleDataSource.getClusterName());
                if (clusterInfo != null && StringUtils.isNotBlank(clusterInfo.getSkipDataSize())) {
                    if (partitionTable && partition.length() > 0) {
                        // Check partition size.
                        checkPartitionSize(userName, partition, clusterInfo, datasourceSizeList, ruleDataSource, partitionFullSize);
                    } else {
                        // Check table size.
                        checkTableSize(userName, clusterInfo, datasourceSizeList, ruleDataSource);
                    }
                }

                if (currentRule.getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
                    return;
                }
                if (!metaDataClient.fieldExist(ruleDataSource.getColName(), cols, mappingCol)) {
                    throw new DataSourceMoveException("Table[" + ruleDataSource.getTableName() + "] {&RULE_DATASOURCE_BE_MOVED}");
                }
            } else if (StringUtils.isNotBlank(currentRule.getCsId())) {
                checkDatasourceInContextService(ruleDataSource, mappingCol, clusterName, userName, nodeName, currentRule.getCsId());
            }
        }
    }

    private void fileReSaveDataSource(List<String> partitionFields, RuleDataSource ruleDataSource) {
        if (StringUtils.isNotEmpty(ruleDataSource.getFilter())) {
            String filter = ruleDataSource.getFilter();
            String[] conditions = filter.split(AND);
            boolean legal = true;
            for (String condition : conditions) {
                String[] colNameAndValue = condition.split("=");
                if (colNameAndValue.length < 2) {
                    legal = false;
                    break;
                }
                if (!partitionFields.contains(colNameAndValue[0])) {
                    legal = false;
                }
            }

            if (!legal) {
                ruleDataSource.setFilter("");
                ruleDataSourceDao.saveRuleDataSource(ruleDataSource);
            }
        }
    }

    private void checkPartitionSize(String userName, StringBuilder partition, ClusterInfo clusterInfo, List<Double> datasourceSizeList, RuleDataSource ruleDataSource
            , String partitionFullSize) throws Exception {

        String fullSize = partitionFullSize;

        if (StringUtils.isEmpty(fullSize)) {
            PartitionStatisticsInfo partitionStatisticsInfo = metaDataClient.getPartitionStatisticsInfo(clusterInfo.getClusterName()
                    , ruleDataSource.getDbName(), ruleDataSource.getTableName(), filterToPartitionPath(partition.toString()), userName);
            fullSize = partitionStatisticsInfo.getPartitionSize();
        }

        if (StringUtils.isNotBlank(fullSize)) {
            double number = 0;
            String unit = "B";
            if (!NULL_TABLE_SIZE.equals(fullSize)) {
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
    }

    private void checkTableSize(String userName, ClusterInfo clusterInfo, List<Double> datasourceSizeList, RuleDataSource ruleDataSource) throws Exception {
        TableStatisticsInfo tableStatisticsInfo = metaDataClient.getTableStatisticsInfo(clusterInfo.getClusterName()
                , ruleDataSource.getDbName(), ruleDataSource.getTableName(), userName);
        String fullSize = tableStatisticsInfo.getTableSize();

        if (StringUtils.isNotBlank(fullSize)) {
            LOGGER.info("Check datasource[" + fullSize + "] if or not oversize with system config[" + clusterInfo.getSkipDataSize() + "]");
            double number = 0;
            String unit = "B";
            if (!NULL_TABLE_SIZE.equals(fullSize)) {
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
    }

    private void checkRdmsSqlMetaInfo(String clusterName, String userName, RuleDataSource ruleDataSource) throws Exception {
        if (StringUtils.isBlank(ruleDataSource.getDbName()) || StringUtils.isBlank(ruleDataSource.getTableName())) {
            LOGGER.warn("db_name or table_name is null: {}", ruleDataSource.getRule().getId());
            return;
        }
        Long envId = ruleDataSource.getRuleDataSourceEnvs().iterator().next().getEnvId();
        DataInfo<ColumnInfoDetail> cols = metaDataClient.getColumnsByDataSourceName(clusterName, userName, ruleDataSource.getLinkisDataSourceName(), ruleDataSource.getDbName(), ruleDataSource.getTableName(), envId);
        if (cols == null || CollectionUtils.isEmpty(cols.getContent())) {
            throw new DataSourceMoveException("Table[" + ruleDataSource.getTableName() + "] {&RULE_DATASOURCE_BE_MOVED}");
        }
        if (!metaDataClient.fieldExist(ruleDataSource.getColName(), cols.getContent(), null)) {
            throw new DataSourceMoveException("Table[" + ruleDataSource.getTableName() + "] {&RULE_DATASOURCE_BE_MOVED}");
        }
    }

    private void checkDatasourceInContextService(RuleDataSource ruleDataSource, Map<String, String> mappingCol, String clusterName, String userName
            , String nodeName, String csId) throws Exception {
        GetUserTableByCsIdRequest getUserTableByCsIdRequest = new GetUserTableByCsIdRequest();
        getUserTableByCsIdRequest.setClusterName(StringUtils.isNotBlank(clusterName) ? clusterName : ruleDataSource.getClusterName());
        getUserTableByCsIdRequest.setLoginUser(userName);
        getUserTableByCsIdRequest.setCsId(csId);
        if (StringUtils.isBlank(nodeName)) {
            getUserTableByCsIdRequest.setNodeName(RuleConstraintEnum.DEFAULT_NODENAME.getValue());
        } else {
            getUserTableByCsIdRequest.setNodeName(nodeName);
        }
        DataInfo<CsTableInfoDetail> csTableInfoDetails = null;
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
                List<ColumnInfoDetail> cols = metaDataClient.getColumnByCsId(getUserColumnByCsRequest).getContent();
                if (ruleDataSource.getRule().getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
                    continue;
                }
                if (!metaDataClient.fieldExist(ruleDataSource.getColName(), cols, mappingCol)) {
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

    private void checkAndUploadFpsFile(String userName, String fpsFileId, String fpsHashValue, RuleDataSource ruleDataSource, String clusterName)
            throws UnExpectedRequestException, TaskNotExistException, ClusterInfoNotConfigException {
        User user = userDao.findByUsername(userName);
        if (user == null) {
            throw new UnExpectedRequestException(String.format("{&FAILED_TO_FIND_USER} %s", userName));
        }
        if (StringUtils.isNotBlank(fpsFileId) && StringUtils.isNotBlank(fpsHashValue)) {
            // MUL FPS FILE.
            if (fpsFileId.contains(SpecCharEnum.COLON.getValue()) && fpsHashValue.contains(SpecCharEnum.COLON.getValue())) {
                String currentFpsId = fpsFileId.split(SpecCharEnum.COLON.getValue())[ruleDataSource.getDatasourceIndex()];
                String currentFpsHash = fpsHashValue.split(SpecCharEnum.COLON.getValue())[ruleDataSource.getDatasourceIndex()];
                ruleDataSource.setFileId(currentFpsId);
                ruleDataSource.setFileHashValue(currentFpsHash);
            } else {
                ruleDataSource.setFileId(fpsFileId);
                ruleDataSource.setFileHashValue(fpsHashValue);
            }

        }
        String fileId = ruleDataSource.getFileId();
        String fileHashValue = ruleDataSource.getFileHashValue();
        String table = ruleDataSource.getTableName();
        String cluster = StringUtils.isNotBlank(clusterName) ? clusterName : ruleDataSource.getClusterName();
        String fileType = ruleDataSource.getFileType();
        LOGGER.info("Fps params: fileId[{}], fileName[{}], clusterName[{}]", fileId, table, cluster);
        fpsService.downloadFpsFile(fileId, fileHashValue, table.concat(fileType), cluster, user);
        LOGGER.info("Finish to operate fps file with fps service.");
        // Save fps excel sheet name.
        if (ExcelTypeEnum.XLSX.getValue().equals(ruleDataSource.getFileType()) || ExcelTypeEnum.XLS.getValue().equals(ruleDataSource.getFileType())) {
            String sheetName = fpsService.getExcelSheetName(table.concat(fileType), cluster, user);
            ruleDataSource.setFileSheetName(sheetName);
            ruleDataSourceDao.saveRuleDataSource(ruleDataSource);
        }
    }

    private void getMappingCols(Rule currentRule, List<Map<String, String>> mappingCols) {
        Map<String, String> leftCols = new LinkedHashMap<>();
        Map<String, String> rightCols = new LinkedHashMap<>();
        if (CollectionUtils.isNotEmpty(currentRule.getRuleDataSourceMappings())) {
            for (RuleDataSourceMapping ruleDataSourceMapping : currentRule.getRuleDataSourceMappings()) {
                if (StringUtils.isBlank(ruleDataSourceMapping.getLeftColumnNames())
                        || StringUtils.isBlank(ruleDataSourceMapping.getLeftColumnTypes())
                        || StringUtils.isBlank(ruleDataSourceMapping.getRightColumnNames())
                        || StringUtils.isBlank(ruleDataSourceMapping.getRightColumnTypes())) {
                    break;
                }
                String[] leftJoinCols = ruleDataSourceMapping.getLeftColumnNames().split(",");
                String[] leftJoinTypes = ruleDataSourceMapping.getLeftColumnTypes().split("\\|");
                for (int i = 0; i < leftJoinCols.length; i++) {
                    leftCols.put(leftJoinCols[i].split("\\.")[1], leftJoinTypes[i]);
                }
                String[] rightJoinCols = ruleDataSourceMapping.getRightColumnNames().split(",");
                String[] rightJoinTypes = ruleDataSourceMapping.getRightColumnTypes().split("\\|");
                for (int i = 0; i < rightJoinCols.length; i++) {
                    rightCols.put(rightJoinCols[i].split("\\.")[1], rightJoinTypes[i]);
                }
            }
        }

        mappingCols.add(leftCols);
        mappingCols.add(rightCols);
    }

    private Map<Long, Map<String, Object>> ruleReplaceInfo(String username, Map<String, String> executionParamOuter, List<Rule> rules) throws SystemConfigException {
        SystemConfig systemConfigInDb = systemConfigDao.findByKeyName(systemKeyConfig.getSaveDatabasePattern());

        if (systemConfigInDb == null || StringUtils.isBlank(systemConfigInDb.getValue())) {
            throw new SystemConfigException(String.format("System Config: %s, can not be null or empty", systemKeyConfig.getSaveDatabasePattern()));
        }

        Map<Long, Map<String, Object>> ruleReplaceInfo = new HashMap<>(8);
        for (Rule rule : rules) {
            Map<String, Object> ruleRealExecParams = new HashMap<>(8);

            ruleRealExecParams.put("qualitis_alert_level", rule.getAlertLevel());
            ruleRealExecParams.put("qualitis_alert_receivers", rule.getAlertReceiver());
            ruleRealExecParams.put("qualitis_abort_on_failure", rule.getAbortOnFailure());
            ruleRealExecParams.put("qualitis_startup_param", rule.getStaticStartupParam());
            ruleRealExecParams.put("qualitis_delete_fail_check_result", rule.getDeleteFailCheckResult());

            if (StringUtils.isNotBlank(rule.getAbnormalDatabase()) && StringUtils.isNotBlank(rule.getAbnormalCluster())) {
                ruleRealExecParams.put("qualitis_abnormal_database", rule.getAbnormalDatabase());
            } else {
                ruleRealExecParams.put("qualitis_abnormal_database", systemConfigInDb.getValue().replace(USERNAME_FORMAT_PLACEHOLDER, username));
            }

            if (StringUtils.isNotBlank(rule.getExecutionParametersName())) {
                ExecutionParameters executionParametersInDb = executionParametersDao.findByNameAndProjectId(rule.getExecutionParametersName(), rule.getProject().getId());
                ruleRealExecParams.put("union_all_save", executionParametersInDb.getUnionAll());
                ruleRealExecParams.put("qualitis_alert_level", executionParametersInDb.getAlertLevel());
                ruleRealExecParams.put("qualitis_alert_receivers", executionParametersInDb.getAlertReceiver());

                Boolean specifyStaticStartupParam = executionParametersInDb.getSpecifyStaticStartupParam();
                Boolean deleteFailCheckResult = executionParametersInDb.getDeleteFailCheckResult();
                Boolean uploadRuleMetricValue = executionParametersInDb.getUploadRuleMetricValue();
                Boolean uploadAbnormalValue = executionParametersInDb.getUploadAbnormalValue();
                Boolean abortOnFailure = executionParametersInDb.getAbortOnFailure();
                String abnormalDb = executionParametersInDb.getAbnormalDatabase();

                if (executionParamOuter.keySet().contains("qualitis_abort_on_failure")) {
                    abortOnFailure = Boolean.valueOf(executionParamOuter.get("qualitis_abort_on_failure"));
                }
                if (executionParamOuter.keySet().contains("qualitis_upload_abnormal_value")) {
                    uploadAbnormalValue = Boolean.valueOf(executionParamOuter.get("qualitis_upload_abnormal_value"));
                }
                if (executionParamOuter.keySet().contains("qualitis_upload_rule_metric_value")) {
                    uploadRuleMetricValue = Boolean.valueOf(executionParamOuter.get("qualitis_upload_rule_metric_value"));
                }
                if (executionParamOuter.keySet().contains("qualitis_delete_fail_check_result")) {
                    deleteFailCheckResult = Boolean.valueOf(executionParamOuter.get("qualitis_delete_fail_check_result"));
                }
                if (executionParamOuter.keySet().contains("qualitis_startup_param_enable")) {
                    specifyStaticStartupParam = Boolean.valueOf(executionParamOuter.get("qualitis_startup_param_enable"));
                }
                if (executionParamOuter.keySet().contains("qualitis_abnormal_database")) {
                    abnormalDb = executionParamOuter.get("qualitis_abnormal_database");
                }
                if (StringUtils.isNotBlank(abnormalDb)) {
                    ruleRealExecParams.put("qualitis_abnormal_database", abnormalDb);
                }
                ruleRealExecParams.put("qualitis_startup_param_enable", specifyStaticStartupParam);
                ruleRealExecParams.put("qualitis_delete_fail_check_result", deleteFailCheckResult);
                ruleRealExecParams.put("qualitis_upload_rule_metric_value", uploadRuleMetricValue);
                ruleRealExecParams.put("qualitis_upload_abnormal_value", uploadAbnormalValue);
                ruleRealExecParams.put("qualitis_abort_on_failure", abortOnFailure);

                String staticStartupParam = "";
                if (StringUtils.isNotBlank(executionParametersInDb.getStaticStartupParam())) {
                    staticStartupParam = executionParametersInDb.getStaticStartupParam();
                }
                if (executionParamOuter.keySet().contains("qualitis_startup_param")) {
                    staticStartupParam = executionParamOuter.get("qualitis_startup_param").replace(SpecCharEnum.COMMA.getValue(), SpecCharEnum.DIVIDER.getValue());
                }
                ruleRealExecParams.put("qualitis_startup_param", staticStartupParam);

                Map<String, String> executionParamInner = new HashMap<>();
                if (StringUtils.isNotBlank(executionParametersInDb.getExecutionParam())) {
                    parseExecParams(executionParametersInDb.getExecutionParam(), executionParamInner);
                    for (Map.Entry<String, String> entry : executionParamInner.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        if (!executionParamOuter.keySet().contains(key)) {
                            executionParamOuter.put(key, value);
                        }
                    }
                }
            }

            ruleReplaceInfo.put(rule.getId(), ruleRealExecParams);
        }

        return ruleReplaceInfo;
    }

    private void checkPermissionCreateUserProxyExecuteUser(String createUser, String executeUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException {
        User userInDb = userDao.findByUsername(createUser);
        if (userInDb == null) {
            throw new UnExpectedRequestException("Create user: " + createUser + " {&DOES_NOT_EXIST}");
        }

        if (createUser.equals(executeUser)) {
            LOGGER.info("Create user is equals to execute user, pass permission, create user: {}", createUser);
            return;
        }

        if (CollectionUtils.isEmpty(userInDb.getUserProxyUsers())) {
            throw new PermissionDeniedRequestException("Create user: " + createUser + " {&HAS_NO_PERMISSION_PROXYING_EXECUTE_USER}: " + executeUser, 403);
        }

        for (UserProxyUser userProxyUser : userInDb.getUserProxyUsers()) {
            if (userProxyUser.getProxyUser().getProxyUserName().equals(executeUser)) {
                LOGGER.info("Create user has permission proxying execute user, pass permission, user: {}, execute user: {}",
                        createUser, executeUser);
                return;
            }
        }
        throw new PermissionDeniedRequestException("Create user: " + createUser + " {&HAS_NO_PERMISSION_PROXYING_EXECUTE_USER}: " + executeUser, 403);
    }

}
