package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.qualitis.LocalConfig;
import com.webank.wedatasphere.qualitis.checkalert.dao.CheckAlertDao;
import com.webank.wedatasphere.qualitis.checkalert.entity.CheckAlert;
import com.webank.wedatasphere.qualitis.checkalert.service.CheckAlertService;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDepartmentUserDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.ProjectTypeEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.service.OuterWorkflowService;
import com.webank.wedatasphere.qualitis.project.service.ProjectBatchService;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.config.RuleConfig;
import com.webank.wedatasphere.qualitis.rule.constant.*;
import com.webank.wedatasphere.qualitis.rule.dao.*;
import com.webank.wedatasphere.qualitis.rule.entity.*;
import com.webank.wedatasphere.qualitis.rule.request.*;
import com.webank.wedatasphere.qualitis.rule.request.multi.AddMultiSourceRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinColumnRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinConfigRequest;
import com.webank.wedatasphere.qualitis.rule.response.CopyRuleWithDatasourceResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleNodeResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleNodeResponses;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import com.webank.wedatasphere.qualitis.rule.service.*;
import com.webank.wedatasphere.qualitis.rule.timer.RuleNodeCallable;
import com.webank.wedatasphere.qualitis.rule.timer.RuleNodeThreadFactory;
import com.webank.wedatasphere.qualitis.rule.util.TemplateMidTableUtil;
import com.webank.wedatasphere.qualitis.scheduled.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.service.DataVisibilityService;
import com.webank.wedatasphere.qualitis.service.RoleService;
import com.webank.wedatasphere.qualitis.service.UserService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import com.webank.wedatasphere.qualitis.util.map.CustomObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author allenzhou
 */
@Service
public class RuleNodeServiceImpl implements RuleNodeService {
    @Autowired
    private RuleConfig ruleConfig;
    @Autowired
    private LocalConfig localConfig;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private FileRuleService fileRuleService;
    @Autowired
    private CustomRuleService customRuleService;
    @Autowired
    private MultiSourceRuleService multiSourceRuleService;
    @Autowired
    private DataVisibilityService dataVisibilityService;
    @Autowired
    private CheckAlertService checkAlertService;

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private RuleBatchService ruleBatchService;
    @Autowired
    private AlarmConfigService alarmConfigService;
    @Autowired
    private ProjectBatchService projectBatchService;
    @Autowired
    private RuleVariableService ruleVariableService;
    @Autowired
    private RuleTemplateService ruleTemplateService;
    @Autowired
    private OuterWorkflowService outerWorkflowService;
    @Autowired
    private RuleDataSourceService ruleDataSourceService;
    @Autowired
    private TemplateOutputMetaService templateOutputMetaService;
    @Autowired
    private RuleDataSourceMappingService ruleDataSourceMappingService;
    @Autowired
    private TemplateMidTableInputMetaService templateMidTableInputMetaService;
    @Autowired
    private TemplateStatisticsInputMetaService templateStatisticsInputMetaService;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private CheckAlertDao checkAlertDao;
    @Autowired
    private AlarmConfigDao alarmConfigDao;
    @Autowired
    private RuleVariableDao ruleVariableDao;
    @Autowired
    private RuleDataSourceDao ruleDataSourceDao;
    @Autowired
    private RuleDatasourceEnvDao ruleDatasourceEnvDao;
    @Autowired
    private RuleDataSourceMappingDao ruleDataSourceMappingDao;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private RuleGroupDao ruleGroupDao;
    @Autowired
    private RuleTemplateDao ruleTemplateDao;
    @Autowired
    private TemplateOutputMetaDao templateOutputMetaDao;
    @Autowired
    private TemplateDataSourceTypeDao templateDataSourceTypeDao;
    @Autowired
    private AlarmArgumentsExecutionParametersDao alarmArgumentsExecutionParametersDao;

    @Autowired
    private RuleMetricDao ruleMetricDao;
    @Autowired
    private RuleMetricDepartmentUserDao ruleMetricDepartmentUserDao;

    @Autowired
    private ExecutionParametersDao executionParametersDao;
    @Autowired
    private NoiseEliminationManagementDao noiseEliminationManagementDao;
    @Autowired
    private ExecutionVariableDao executionVariableDao;
    @Autowired
    private StaticExecutionParametersDao staticExecutionParametersDao;

    private static final ThreadPoolExecutor POOL = new ThreadPoolExecutor(50,
            Integer.MAX_VALUE,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1000),
            new RuleNodeThreadFactory(),
            new ThreadPoolExecutor.DiscardPolicy());

    private static final String RULE_NAME = "--rule-name";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleNodeServiceImpl.class);

    private HttpServletRequest httpServletRequest;

    public RuleNodeServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse deleteRule(DeleteRuleRequest request) throws UnExpectedRequestException {
        // Check Arguments
        DeleteRuleRequest.checkRequest(request);
        Long ruleGroupId = request.getRuleGroupId();
        if (StringUtils.isBlank(ruleGroupId.toString())) {
            throw new UnExpectedRequestException("Rule group id is invalid");
        }
        LOGGER.info("Check existence of rule group before deleting. Rule group id: {}.", ruleGroupId.toString());

        RuleGroup ruleGroupInDb = ruleGroupDao.findById(request.getRuleGroupId());

        if (ruleGroupInDb == null) {
            return new GeneralResponse<>("200", "No rule group to be delete", null);
        }

        if (GroupTypeEnum.CHECK_ALERT_GROUP.getCode().equals(ruleGroupInDb.getType())) {
            List<CheckAlert> checkAlerts = checkAlertDao.findByRuleGroup(ruleGroupInDb);
            LOGGER.info("Start to delete check alert. Rule group id: {}.", ruleGroupId.toString());

            if (CollectionUtils.isNotEmpty(checkAlerts)) {
                checkAlertDao.deleteAll(checkAlerts);
            }
            LOGGER.info("Success to delete check alert. Rule group id: {}.", ruleGroupId.toString());
            return new GeneralResponse<>("200", "{&DELETE_RULE_SUCCESSFULLY}", null);
        }

        List<Rule> rules = ruleDao.findByRuleGroup(ruleGroupInDb);
        if (CollectionUtils.isEmpty(rules)) {
            return new GeneralResponse<>("200", "No rules to be delete", null);
        }
        for (Rule ruleInDb : rules) {
            LOGGER.info("Start to delete rule. Rule group ID: {}.", ruleGroupId.toString());
            ruleService.deleteRuleReal(ruleInDb);
        }
        LOGGER.info("Success to delete rule. Rule group ID: {}.", ruleGroupId.toString());
        return new GeneralResponse<>("200", "{&DELETE_RULE_SUCCESSFULLY}", null);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse modifyRule(ModifyRuleRequest request) throws UnExpectedRequestException {
        Long ruleGroupId = request.getRuleGroupId();
        if (StringUtils.isBlank(ruleGroupId.toString()) || StringUtils.isBlank(request.getNodeName())) {
            throw new UnExpectedRequestException("Rule group id or node name is invalid");
        }
        LOGGER.info("Check existence of rule group before modifying. Rule group id: {}.", ruleGroupId.toString());

        RuleGroup ruleGroupInDb = ruleGroupDao.findById(request.getRuleGroupId());

        if (ruleGroupInDb == null) {
            return new GeneralResponse<>("200", "No rule group to be modify", null);
        }

        if (GroupTypeEnum.CHECK_ALERT_GROUP.getCode().equals(ruleGroupInDb.getType())) {
            List<CheckAlert> checkAlerts = checkAlertDao.findByRuleGroup(ruleGroupInDb);
            LOGGER.info("Start to modify check alert. Rule group id: {}.", ruleGroupId.toString());
            if (CollectionUtils.isEmpty(checkAlerts)) {
                return new GeneralResponse<>("200", "No check alerts to be modify", null);
            }
            checkAlerts = checkAlerts.stream().map(checkAlert -> {
                checkAlert.setNodeName(request.getNodeName());
                return checkAlert;
            }).collect(Collectors.toList());
            checkAlertDao.saveAll(checkAlerts);
            LOGGER.info("Success to modify check alert. Rule group id: {}.", ruleGroupId.toString());
            return new GeneralResponse<>("200", "{&MODIFY_RULE_SUCCESSFULLY}", null);
        }

        List<Rule> rules = ruleDao.findByRuleGroup(ruleGroupInDb);
        if (CollectionUtils.isEmpty(rules)) {
            return new GeneralResponse<>("200", "No rules to be modify", null);
        }
        rules = rules.stream().map(rule -> {
            rule.setNodeName(request.getNodeName());
            return rule;
        }).collect(Collectors.toList());
        ruleDao.saveRules(rules);
        LOGGER.info("Success to modify rule. Rule group ID: {}.", ruleGroupId.toString());
        return new GeneralResponse<>("200", "{&MODIFY_RULE_SUCCESSFULLY}", null);
    }

    @Override
    public GeneralResponse<RuleNodeResponses> exportRuleByGroupId(Long ruleGroupId) throws UnExpectedRequestException {
        // Check existence of ruleGroup
        RuleGroup ruleGroup = ruleGroupDao.findById(ruleGroupId);
        if (ruleGroup == null) {
            throw new UnExpectedRequestException("Rule group [Id = " + ruleGroupId + "] does not exist.");
        }
        LOGGER.info("Succeed to find rule group. Rule group id: {}.", ruleGroup.getId());
        List<RuleNodeResponse> responses = new ArrayList<>();
        try {
            List<Rule> rules = ruleDao.findByRuleGroup(ruleGroup);
            List<CheckAlert> checkAlerts = checkAlertDao.findByRuleGroup(ruleGroup);
            if (CollectionUtils.isEmpty(rules) && CollectionUtils.isEmpty(checkAlerts)) {
                throw new UnExpectedRequestException("Rule group [id = " + ruleGroup.getId() + "] does not have rules or check alert rules.");
            } else if (CollectionUtils.isNotEmpty(rules)) {
                for (Rule rule : rules) {
                    responses.add(ruleNodeResponse(rule));
                }
            } else if (CollectionUtils.isNotEmpty(checkAlerts)) {
                for (CheckAlert checkAlert : checkAlerts) {
                    responses.add(ruleNodeResponse(checkAlert));
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to export rule because of JSON serialization opeartions.", e);
            return new GeneralResponse<>("500", "{&FAILED_TO_EXPORT_RULE}", null);
        }
        LOGGER.info("Succeed to export rule. Rule info: {}", Arrays.toString(responses.toArray()));
        RuleNodeResponses ruleNodeResponseList = new RuleNodeResponses(responses);
        return new GeneralResponse<>("200", "{&EXPORT_RULE_SUCCESSFULLY}", ruleNodeResponseList);
    }

    private RuleNodeResponse ruleNodeResponse(CheckAlert checkAlert) throws IOException {
        RuleNodeResponse response = new RuleNodeResponse();
        response.setCheckAlertRule(objectMapper.writeValueAsString(checkAlert));

        // Project info.
        response.setProjectId(objectMapper.writeValueAsString(checkAlert.getProject().getId()));
        response.setProjectName(objectMapper.writeValueAsString(checkAlert.getProject().getName()));

        // Group info.
        response.setRuleGroupObject(objectMapper.writeValueAsString(checkAlert.getRuleGroup()));

        return response;
    }

    /**
     * Parse the rule information and encapsulate it into the response body.
     *
     * @param rule
     * @throws IOException
     */
    private RuleNodeResponse ruleNodeResponse(Rule rule) throws IOException {
        RuleNodeResponse response = new RuleNodeResponse();
        // Project info.
        response.setProjectId(objectMapper.writeValueAsString(rule.getProject().getId()));
        response.setProjectName(objectMapper.writeValueAsString(rule.getProject().getName()));

        response.setRuleGroupObject(objectMapper.writeValueAsString(rule.getRuleGroup()));
        response.setTemplateObject(objectMapper.writeValueAsString(rule.getTemplate()));
        // Template visibility department name list
        List<DataVisibility> dataVisibilityList = dataVisibilityService.filter(rule.getTemplate().getId(), TableDataTypeEnum.RULE_TEMPLATE);

        if (CollectionUtils.isNotEmpty(dataVisibilityList)) {
            response.setTemplateDataVisibilityObject(objectMapper.writerWithType(new TypeReference<List<DataVisibility>>() {}).writeValueAsString(dataVisibilityList));
        }
        response.setRuleObject(objectMapper.writeValueAsString(rule));

        if (StringUtils.isNotBlank(rule.getExecutionParametersName())) {
            ExecutionParameters executionParameters = executionParametersDao.findByNameAndProjectId(rule.getExecutionParametersName(), rule.getProject().getId());
            if (executionParameters != null) {
                response.setExecutionParamObject(objectMapper.writeValueAsString(executionParameters));
            }
        }

        return response;
    }

    @Override
    public GeneralResponse<RuleResponse> importRuleGroup(RuleNodeRequests ruleNodeRequests) throws UnExpectedRequestException, IOException, ExecutionException, InterruptedException {
        LOGGER.info("Import requests: {}", ruleNodeRequests.toString());
        // Project, rule group.
        Project projectInDb = projectDao.findById(ruleNodeRequests.getNewProjectId());
        projectInDb = handleProject(projectInDb, ruleNodeRequests);
        LOGGER.info("Import into {}", projectInDb.getName());

        RuleGroup ruleGroupExists;
        StringBuilder workflowName = new StringBuilder("");

        List<String> ruleNames = new ArrayList<>(ruleNodeRequests.getRuleNodeRequests().size());
        List<String> checkAlertTopics = new ArrayList<>(ruleNodeRequests.getRuleNodeRequests().size());
        RuleGroup ruleGroup = objectMapper.readValue(ruleNodeRequests.getRuleNodeRequests().iterator().next().getRuleGroupObject(), RuleGroup.class);
        Set<RuleDataSource> ruleDataSourceSet = ruleGroup.getRuleDataSources();


        for (RuleNodeRequest request : ruleNodeRequests.getRuleNodeRequests()) {
            if (StringUtils.isNotEmpty(request.getRuleObject())) {
                Rule rule = objectMapper.readValue(request.getRuleObject(), Rule.class);
                ruleNames.add(rule.getName());

                if (StringUtils.isEmpty(workflowName.toString()) && StringUtils.isNotEmpty(rule.getWorkFlowName())) {
                    workflowName.append(rule.getWorkFlowName());
                }
            }
            if (StringUtils.isNotEmpty(request.getCheckAlertRule())) {
                CheckAlert checkAlert = objectMapper.readValue(request.getCheckAlertRule(), CheckAlert.class);
                checkAlertTopics.add(checkAlert.getTopic());

                if (StringUtils.isEmpty(workflowName.toString()) && StringUtils.isNotEmpty(checkAlert.getWorkFlowName())) {
                    workflowName.append(checkAlert.getWorkFlowName());
                }
            }
        }
        ruleGroupExists = reuseRuleGroupExists(ruleGroup, ruleNames, checkAlertTopics, projectInDb, workflowName.toString());

        Long sourceRuleGroupId = ruleGroup.getId();

        ruleGroup.setId(null);
        ruleGroup.setProjectId(projectInDb.getId());
        RuleGroup ruleGroupInDb = ruleGroupDao.saveRuleGroup(ruleGroup);

        // Dev copy project, mainly about the project copy of different dss.
        if (Boolean.TRUE.equals(localConfig.getSupportMigrate())) {
            return devCenterCopyIfMigrate(ruleGroup, sourceRuleGroupId, projectInDb, ruleNodeRequests, workflowName.toString());
        }

        List<Future<List<Exception>>> exceptionList = new ArrayList<>();
        if (ruleGroupExists == null) {
            updateRuleGroupDatasource(ruleGroupInDb, ruleDataSourceSet);
            ruleNodeRequestFuture(ruleNodeRequests, projectInDb, objectMapper, ruleGroupInDb, exceptionList);
            if (CollectionUtils.isNotEmpty(exceptionList)) {
                StringBuilder exceptionMessage = new StringBuilder();
                for (Future<List<Exception>> eList : exceptionList) {
                    for (Exception e : eList.get()) {
                        exceptionMessage.append(e.getMessage()).append("\n");
                    }
                }
                if (StringUtils.isNotEmpty(exceptionMessage.toString())) {
                    return new GeneralResponse<>("500", "{&FAILED_TO_IMPORT_RULE}, caused by " + exceptionMessage.toString(), null);
                }
            }
            disableRules(ruleGroupInDb);
            modifyRuleContextService(ruleGroupInDb, ruleNodeRequests.getCsId());
            LOGGER.info("Imported rule group ID: {}", ruleGroupInDb.getId());
            return new GeneralResponse<>("200", "{&IMPORT_RULE_SUCCESSFULLY}", new RuleResponse(ruleGroupInDb.getId()));
        } else {
            updateRuleGroupDatasource(ruleGroupExists, ruleDataSourceSet);
            ruleNodeRequestFuture(ruleNodeRequests, projectInDb, objectMapper, ruleGroupExists, exceptionList);
            if (CollectionUtils.isNotEmpty(exceptionList)) {
                StringBuilder exceptionMessage = new StringBuilder();
                for (Future<List<Exception>> eList : exceptionList) {
                    for (Exception e : eList.get()) {
                        exceptionMessage.append(e.getMessage()).append("\n");
                    }
                }

                if (StringUtils.isNotEmpty(exceptionMessage.toString())) {
                    return new GeneralResponse<>("500", "{&FAILED_TO_IMPORT_RULE}, caused by " + exceptionMessage.toString(), null);
                }
            }
            disableRules(ruleGroupExists);
            modifyRuleContextService(ruleGroupExists, ruleNodeRequests.getCsId());
            LOGGER.info("Imported rule group ID: {}", ruleGroupExists.getId());
            return new GeneralResponse<>("200", "{&IMPORT_RULE_SUCCESSFULLY}", new RuleResponse(ruleGroupExists.getId()));
        }
    }

    private void modifyRuleContextService(RuleGroup ruleGroupInDb, String csId) {
        LOGGER.info("Rules need to update contextID: {}", csId);
        List<Rule> rules = ruleDao.findByRuleGroup(ruleGroupInDb);
        if (CollectionUtils.isNotEmpty(rules) && StringUtils.isNotEmpty(csId)) {
            for (Rule rule : rules) {
                if (StringUtils.isEmpty(rule.getCsId())) {
                    continue;
                }
                LOGGER.info(rule.getName() + " " + " update contextID.");
                rule.setCsId(csId);
            }
            ruleDao.saveRules(rules);
        }
    }

    private GeneralResponse<RuleResponse> devCenterCopyIfMigrate(RuleGroup ruleGroup, Long sourceRuleGroupId, Project projectInDb, RuleNodeRequests ruleNodeRequests, String workflowName) throws InterruptedException, ExecutionException, UnExpectedRequestException {
        CopyRuleRequest copyRuleRequest = new CopyRuleRequest();

        copyRuleRequest.setWorkFlowName(workflowName);
        copyRuleRequest.setVersion(ruleGroup.getVersion());
        copyRuleRequest.setCsId(ruleNodeRequests.getCsId());
        copyRuleRequest.setSourceRuleGroupId(sourceRuleGroupId);
        copyRuleRequest.setCreateUser(projectInDb.getCreateUser());
        copyRuleRequest.setTargetProjectId(ruleNodeRequests.getNewProjectId());

        return copyRuleByRuleGroupId(copyRuleRequest);
    }

    private RuleGroup reuseRuleGroupExists(RuleGroup ruleGroup, List<String> ruleNames, List<String> checkAlertTopics, Project projectInDb, String workflowName) {
        if (CollectionUtils.isNotEmpty(ruleNames)) {
            List<Rule> rulesInDb = ruleDao.findByProjectAndWorkflowNameAndRuleNames(projectInDb, workflowName, ruleNames);

            if (CollectionUtils.isNotEmpty(rulesInDb)) {
                Rule currentRule = rulesInDb.iterator().next();
                RuleGroup ruleGroupExists = currentRule.getRuleGroup();

                ruleGroupExists.setVersion(ruleGroup.getVersion());
                ruleGroupExists.setNodeName(ruleGroup.getNodeName());
                ruleGroupExists.setRuleGroupName(ruleGroup.getRuleGroupName());

                ruleGroupExists = ruleGroupDao.saveRuleGroup(ruleGroupExists);
                LOGGER.info("Success to save exists rule group[" + ruleGroupExists.getRuleGroupName() + "]");
                return ruleGroupExists;
            }
        } else if (CollectionUtils.isNotEmpty(checkAlertTopics)) {
            List<CheckAlert> checkAlertsInDb = checkAlertDao.findByProjectAndWorkflowNameAndTopics(projectInDb, workflowName, checkAlertTopics);

            if (CollectionUtils.isNotEmpty(checkAlertsInDb)) {
                CheckAlert currentCheckAlert = checkAlertsInDb.iterator().next();
                RuleGroup ruleGroupExists = currentCheckAlert.getRuleGroup();

                ruleGroupExists.setVersion(ruleGroup.getVersion());
                ruleGroupExists.setNodeName(ruleGroup.getNodeName());
                ruleGroupExists.setRuleGroupName(ruleGroup.getRuleGroupName());

                ruleGroupExists = ruleGroupDao.saveRuleGroup(ruleGroupExists);
                LOGGER.info("Success to save exists rule group[" + ruleGroupExists.getRuleGroupName() + "]");
                return ruleGroupExists;
            }
        }
        return null;
    }

    private Project handleProject(Project projectInDb, RuleNodeRequests ruleNodeRequests) throws UnExpectedRequestException {
        if (projectInDb != null) {
            LOGGER.info("Project info : {}", projectInDb.toString());
        } else {
            // For special case:
            // A project already exists in DSS. when creating a workflow in this project and publish it, Qualitis service cannot find the project.
            // Because Qualitis did not run when this project had been created in DSS.
            LOGGER.info("Start to create workflow project. Project[id = {}] is not exist.", ruleNodeRequests.getNewProjectId());
            String userName = ruleNodeRequests.getUserName();
            User currentUser = userDao.findByUsername(userName);
            if (currentUser == null) {
                LOGGER.info("Start to create user. User name is {}", userName);
                try {
                    userService.autoAddUser(userName);
                    currentUser = userDao.findByUsername(userName);
                } catch (RoleNotFoundException e) {
                    LOGGER.error("Role cannot be found. Exception: {}", e);
                }
            }

            Project newProject = projectService.addProjectReal(currentUser.getId(), currentUser.getUsername() + "_project_" + UUID.randomUUID().toString()
                , currentUser.getUsername() + "_项目_" + UUID.randomUUID().toString(), "Auto created.");
            newProject.setProjectType(ProjectTypeEnum.WORKFLOW_PROJECT.getCode());
            projectInDb = projectDao.saveProject(newProject);
            LOGGER.info("Succeed to create project. New project: {}", projectInDb.toString());
        }
        return projectInDb;
    }

    private void disableRules(RuleGroup ruleGroupInDb) {
        List<Rule> rules = ruleDao.findByRuleGroup(ruleGroupInDb);
        if (StringUtils.isNotEmpty(ruleGroupInDb.getVersion())) {
            rules = rules.stream().filter(rule -> !rule.getWorkFlowVersion().equals(ruleGroupInDb.getVersion())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(rules)) {
                LOGGER.info("Some rules need to be unavailable. Current rule group name: {}, version: {}", ruleGroupInDb.getRuleGroupName(),
                    ruleGroupInDb.getVersion());
                for (Rule rule : rules) {
                    LOGGER.info(rule.getName() + " " + rule.getWorkFlowName() + " " + rule.getWorkFlowVersion()
                        + " need to be unavailable because workflow version is different from rule group.");
                    rule.setEnable(Boolean.FALSE);
                }
                ruleDao.saveRules(rules);
            }
        }
    }

    private void ruleNodeRequestFuture(RuleNodeRequests ruleNodeRequests, Project projectInDb, ObjectMapper objectMapper, RuleGroup ruleGroupInDb, List<Future<List<Exception>>> exceptionList) {
        int total = ruleNodeRequests.getRuleNodeRequests().size();
        int updateThreadSize = total / ruleConfig.getRuleUpdateSize() + 1;
        if (total % ruleConfig.getRuleUpdateSize() == 0) {
            updateThreadSize = total / ruleConfig.getRuleUpdateSize();
        }
        CountDownLatch latch = new CountDownLatch(updateThreadSize);
        for (int indexThread = 0; total > 0 && indexThread < total; indexThread += ruleConfig.getRuleUpdateSize()) {
            if (indexThread + ruleConfig.getRuleUpdateSize() < total) {
                Future<List<Exception>> exceptionFuture = POOL.submit(new RuleNodeCallable(ruleNodeRequests.getRuleNodeRequests().subList(indexThread, indexThread + ruleConfig.getRuleUpdateSize()), null, projectInDb, ruleGroupInDb, objectMapper, latch));
                exceptionList.add(exceptionFuture);
            } else {
                Future<List<Exception>> exceptionFuture = POOL.submit(new RuleNodeCallable(ruleNodeRequests.getRuleNodeRequests().subList(indexThread, total), null, projectInDb, ruleGroupInDb, objectMapper, latch));
                exceptionList.add(exceptionFuture);
            }
            updateThreadSize--;
        }

        if (total > 0 && updateThreadSize == 0) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public GeneralResponse<RuleResponse> copyRuleByRuleGroupId(CopyRuleRequest request)
            throws UnExpectedRequestException, ExecutionException, InterruptedException {
        CopyRuleRequest.checkRequest(request);
        LOGGER.info("Copy request: {}", request.toString());

        Long ruleGroupId = request.getSourceRuleGroupId();
        RuleGroup ruleGroupInDb = ruleGroupDao.findById(request.getSourceRuleGroupId());
        if (ruleGroupInDb == null) {
            throw new UnExpectedRequestException("Rule group {&DOES_NOT_EXIST}");
        }
        Project targetProject = projectDao.findById(ruleGroupInDb.getProjectId());
        if (targetProject == null) {
            throw new UnExpectedRequestException("Source project {&DOES_NOT_EXIST}");
        }

        boolean crossProject = request.getTargetProjectId() != null && (! ruleGroupInDb.getProjectId().equals(request.getTargetProjectId()));
        // Check project existence.
        if (crossProject) {
            Project projectInDb = projectDao.findById(request.getTargetProjectId());
            if (projectInDb == null) {
                throw new UnExpectedRequestException("Target project {&DOES_NOT_EXIST}");
            }
            targetProject = projectInDb;
        }
        RuleGroup targetRuleGroup;
        if (request.getTargetRuleGroupId() != null) {
            RuleGroup targetRuleGroupInDb = ruleGroupDao.findById(request.getTargetRuleGroupId());
            if (targetRuleGroupInDb != null) {
                targetRuleGroup = targetRuleGroupInDb;
            } else {
                throw new UnExpectedRequestException("Rule group id is illegal.");
            }
        } else {
            RuleGroup currentRuleGroup = new RuleGroup(ruleGroupInDb.getRuleGroupName(), crossProject ? request.getTargetProjectId() : ruleGroupInDb.getProjectId());
            currentRuleGroup.setNodeName(StringUtils.isNotBlank(request.getNodeName()) ? request.getNodeName() : "");
            currentRuleGroup.setVersion(StringUtils.isNotBlank(request.getVersion()) ? request.getVersion() : "");

            if (ruleGroupInDb.getType() != null) {
                currentRuleGroup.setType(ruleGroupInDb.getType());
            }
            targetRuleGroup = ruleGroupDao.saveRuleGroup(currentRuleGroup);
            updateRuleGroupDatasource(targetRuleGroup, ruleGroupInDb.getRuleDataSources());
        }
        if (ruleGroupId != null) {
            boolean checkAlertGroup = GroupTypeEnum.CHECK_ALERT_GROUP.getCode().equals(ruleGroupInDb.getType());

            List<Rule> rules = new ArrayList<>();
            List<CheckAlert> checkAlerts = new ArrayList<>();

            LOGGER.info("Start to copy of rule group[ID=" + ruleGroupId + "].");

            int total;

            if (! checkAlertGroup) {
                rules = ruleDao.findByRuleGroup(ruleGroupInDb);
                total = rules.size();
            } else {
                checkAlerts = checkAlertDao.findByRuleGroup(ruleGroupInDb);
                total = checkAlerts.size();
            }

            int updateThreadSize = total / ruleConfig.getRuleUpdateSize() + 1;
            if (total % ruleConfig.getRuleUpdateSize() == 0) {
                updateThreadSize = total / ruleConfig.getRuleUpdateSize();
            }

            List<Future<List<Exception>>> exceptionList = new ArrayList<>();

            CountDownLatch latch = new CountDownLatch(updateThreadSize);
            for (int indexThread = 0; total > 0 && indexThread < total; indexThread += ruleConfig.getRuleUpdateSize()) {
                if (indexThread + ruleConfig.getRuleUpdateSize() < total) {
                    Future<List<Exception>> exceptionFuture = POOL.submit(new RuleNodeCallable(null, checkAlertGroup ? null : rules.subList(indexThread, indexThread + ruleConfig.getRuleUpdateSize()), checkAlertGroup ? checkAlerts.subList(indexThread, indexThread + ruleConfig.getRuleUpdateSize()) : null, request, ruleService, customRuleService, multiSourceRuleService, fileRuleService, ruleDao, checkAlertDao, targetRuleGroup, targetProject, objectMapper, latch));
                    exceptionList.add(exceptionFuture);
                } else {
                    Future<List<Exception>> exceptionFuture = POOL.submit(new RuleNodeCallable(null, checkAlertGroup ? null : rules.subList(indexThread, total), checkAlertGroup ? checkAlerts.subList(indexThread, total) : null, request, ruleService, customRuleService, multiSourceRuleService, fileRuleService, ruleDao, checkAlertDao, targetRuleGroup, targetProject, objectMapper, latch));
                    exceptionList.add(exceptionFuture);
                }
                updateThreadSize--;
            }

            if (total > 0 && updateThreadSize == 0) {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    LOGGER.error("Interrupted!", e);
                    Thread.currentThread().interrupt();
                    throw new InterruptedException(e.getMessage());
                }
            }

            // Check
            List<Exception> exceptions = new ArrayList<>();
            StringBuilder exceptionMessage = new StringBuilder();
            for (Future<List<Exception>> future : exceptionList) {
                if (CollectionUtils.isNotEmpty(future.get())) {
                    exceptions.addAll(future.get());
                }
            }
            if (CollectionUtils.isEmpty(exceptions)) {
                modifyRuleContextService(targetRuleGroup, request.getCsId());
                return new GeneralResponse<>("200", "{&COPY_RULE_SUCCESSFULLY}", new RuleResponse(targetRuleGroup.getId()));
            } else {
                for (Exception e : exceptions) {
                    exceptionMessage.append(e.getMessage()).append("\n");
                }

                return new GeneralResponse<>("500", "{&FAILED_TO_COPY_RULE_DETAIL}, caused by " + exceptionMessage.toString(), null);
            }
        }
        modifyRuleContextService(targetRuleGroup, request.getCsId());
        return new GeneralResponse<>("200", "{&COPY_RULE_SUCCESSFULLY}", new RuleResponse(targetRuleGroup.getId()));
    }

    private void updateRuleGroupDatasource(RuleGroup targetRuleGroup, Set<RuleDataSource> ruleDataSourceSet) {
        if (CollectionUtils.isNotEmpty(ruleDataSourceSet)) {
            List<RuleDataSourceEnv> ruleDataSourceEnvList = new ArrayList<>();
            List<RuleDataSource> ruleDataSourceList = new ArrayList<>();
            ruleDataSourceList.addAll(ruleDataSourceSet.stream().map(ruleDataSource -> {
                ruleDataSource.setId(null);
                ruleDataSource.setRuleGroup(targetRuleGroup);
                if (CollectionUtils.isNotEmpty(ruleDataSource.getRuleDataSourceEnvs())) {
                    ruleDataSourceEnvList.addAll(ruleDataSource.getRuleDataSourceEnvs().stream().map(currRuleDataSourceEnv -> {
                        currRuleDataSourceEnv.setId(null);
                        return currRuleDataSourceEnv;
                    }).collect(Collectors.toList()));
                }
                return ruleDataSource;
            }).collect(Collectors.toList()));

            ruleDataSourceDao.saveAllRuleDataSource(ruleDataSourceList);
            ruleDatasourceEnvDao.saveAllRuleDataSourceEnv(ruleDataSourceEnvList);
        }
    }

    @Override
    public int handleParameterIsNull(CopyRuleRequest request, int totalFinish, RuleGroup targetRuleGroup, Rule rule) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        switch (rule.getRuleType().intValue()) {
            case 1:
                AddRuleRequest addRuleRequest = constructSingleRequest(rule, targetRuleGroup, null);
                ruleService.addRuleForOuter(addRuleRequest, request.getCreateUser());
                totalFinish++;
                break;
            case 2:
                AddCustomRuleRequest addCustomRuleRequest = constructCustomRequest(rule, targetRuleGroup, null);
                customRuleService.addRuleForOuter(addCustomRuleRequest, request.getCreateUser());
                totalFinish++;
                break;
            case 3:
                AddMultiSourceRuleRequest addMultiSourceRuleRequest = constructMultiRequest(rule, targetRuleGroup, null);
                addMultiSourceRuleRequest.setLoginUser(request.getCreateUser());
                multiSourceRuleService.addRuleForOuter(addMultiSourceRuleRequest, false);
                totalFinish++;
                break;
            case 4:
                AddFileRuleRequest addFileRuleRequest = constructFileRequest(rule, targetRuleGroup, null);
                fileRuleService.addRuleForOuter(addFileRuleRequest, request.getCreateUser());
                totalFinish++;
                break;
            default:
                break;
        }
        return totalFinish;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse copyRuleWithDatasource(CopyRuleWithDatasourceRequest request)
            throws UnExpectedRequestException {
        CopyRuleWithDatasourceRequest.checkRequest(request);
        List<Long> ruleIds = request.getRuleIdList();
        List<Rule> rules = ruleDao.findByIds(ruleIds);

        RuleGroup ruleGroup;
        Long ruleGroupId = null;

        int totalFinish = 0;
        boolean firstAdd = true;

        if (null != request.getRuleGroupId()) {
            ruleGroup = ruleGroupDao.findById(request.getRuleGroupId());
            //规则的工作流 version 保存前端入参的，不要继承旧规则的
            if (StringUtils.isNotBlank(request.getWorkFlowVersion())) {
                ruleGroup.setVersion(request.getWorkFlowVersion());
            }

        } else {
            RuleGroup tempRuleGroup = new RuleGroup("Group_" + UUID.randomUUID().toString().replace("-", "")
                    , request.getProjectId());
            tempRuleGroup.setVersion(StringUtils.isNotBlank(request.getWorkFlowVersion()) ? request.getWorkFlowVersion() : "");
            ruleGroup = ruleGroupDao.saveRuleGroup(tempRuleGroup);
        }

        String loginUser = HttpUtils.getUserName(httpServletRequest);

        for (Rule rule : rules) {
            try {
                if (RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode().equals(rule.getRuleType().intValue())) {
                    List<DataSourceRequest> dataSourceRequests = new ArrayList<>();
                    AddRuleRequest addRuleRequest = constructSingleRequest(rule, ruleGroup, StringUtils.isNotBlank(request.getWorkFlowName()) ? request.getWorkFlowName() : null);
                    LOGGER.info("Succeed to construct old rule request: {}", addRuleRequest.toString());
                    DataSourceRequest dataSourceRequest = addRuleRequest.getDatasource().iterator().next();
                    DataSourceRequest dataSourceRequestReal = request.getDatasource().iterator().next();
                    setDataSourceRequest(dataSourceRequest, dataSourceRequestReal);
                    if (StringUtils.isNotBlank(request.getCsId())) {
                        addRuleRequest.setCsId(request.getCsId());
                    } else {
                        dataSourceRequest.setDbName(dataSourceRequestReal.getDbName());
                    }

                    dataSourceRequests.add(dataSourceRequest);
                    addRuleRequest.setDatasource(dataSourceRequests);

                    // Remove temp rule group info
                    if (null != request.getRuleGroupId()) {
                        addRuleRequest.setRuleGroupId(request.getRuleGroupId());
                    } else {
                        addRuleRequest.setRuleGroupId(ruleGroupId);
                    }
                    addRuleRequest.setRuleName(addRuleRequest.getRuleName().replace("_" + ruleGroup.getId(), ""));
                    LOGGER.info("Succeed to replace rule request with datasource: {}", addRuleRequest.toString());
                    RuleResponse response = ruleService.addRule(addRuleRequest, loginUser, false).getData();
                    if (firstAdd) {
                        firstAdd = false;
                        ruleGroupId = response.getRuleGroupId();
                    }
                    totalFinish++;
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                LOGGER.error("Failed to copy rule with datasource, exception: {}", e.getMessage());
            }
        }

        if (totalFinish != rules.size()) {
            return new GeneralResponse<>("400", "{&COPY_RULE_FAILED}", new RuleResponse(ruleGroupId));
        }

        return new GeneralResponse<>("200", "{&COPY_RULE_SUCCESSFULLY}", new CopyRuleWithDatasourceResponse(ruleGroup.getProjectId(), ruleGroupId, request.getRuleIdList()));
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<List<Long>> updateRuleDataForBatch(BatchExecutionParametersRequest request) throws UnExpectedRequestException {
        BatchExecutionParametersRequest.checkRequest(request);

        String loginUser = HttpUtils.getUserName(httpServletRequest);
        //遍历规则ID
        for (Long ids : request.getRuleIdList()) {
            Rule ruleInDb = ruleDao.findById(ids);
            if (ruleInDb == null) {
                throw new UnExpectedRequestException("rule_id [" + ids + "] {&DOES_NOT_EXIST}");
            }
            boolean groupRules = CollectionUtils.isNotEmpty(ruleInDb.getRuleGroup().getRuleDataSources());
            if (StringUtils.isNotBlank(request.getExecutionParametersName())) {
                ExecutionParameters executionParameters = executionParametersDao.findByNameAndProjectId(request.getExecutionParametersName(), ruleInDb.getProject().getId());
                if (groupRules || executionParameters != null) {
                    ruleInDb.setExecutionParametersName(request.getExecutionParametersName());
                    ruleInDb.setUnionAll(executionParameters.getUnionAll());

                    alarmConfigDao.saveAllAlarmConfig(ruleInDb.getAlarmConfigs().stream().map(temp -> {
                        temp.setUploadRuleMetricValue(executionParameters.getUploadRuleMetricValue() != null ? executionParameters.getUploadRuleMetricValue() : false);
                        temp.setDeleteFailCheckResult(executionParameters.getDeleteFailCheckResult() != null ? executionParameters.getDeleteFailCheckResult() : false);
                        temp.setUploadAbnormalValue(executionParameters.getUploadAbnormalValue() != null ? executionParameters.getUploadAbnormalValue() : false);
                        return temp;
                    }).collect(Collectors.toList()));

                    //ExecutionParameters Filter() 单表过滤条件  SourceTableFilter() 源表过滤条件 TargetTableFilter() 目标表过滤条件

                    if (RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode().equals(ruleInDb.getRuleType()) && CollectionUtils.isNotEmpty(ruleInDb.getRuleDataSources())
                            && ruleInDb.getRuleDataSources().size() == 1 && Boolean.TRUE.equals(executionParameters.getSpecifyFilter())) {
                        ruleDataSourceDao.saveAllRuleDataSource(ruleInDb.getRuleDataSources().stream().map((ruleDataSource) -> {
                            ruleDataSource.setFilter(StringUtils.isNotBlank(executionParameters.getFilter()) ? executionParameters.getFilter() : "");
                            return ruleDataSource;
                        }).collect(Collectors.toList()));
                    }

                    if (RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode().equals(ruleInDb.getRuleType()) && CollectionUtils.isNotEmpty(ruleInDb.getRuleDataSources())
                            && ruleInDb.getRuleDataSources().size() == 2 && Boolean.TRUE.equals(executionParameters.getSpecifyFilter())) {
                        //源表
                        ruleDataSourceDao.saveAllRuleDataSource(ruleInDb.getRuleDataSources().stream().filter(
                                ruleDataSources -> ruleDataSources.getDatasourceIndex() == 0).map((temp) -> {
                            temp.setFilter(StringUtils.isNotBlank(executionParameters.getSourceTableFilter()) ? executionParameters.getSourceTableFilter() : "");
                            return temp;
                        }).collect(Collectors.toList()));

                        //目标表
                        ruleDataSourceDao.saveAllRuleDataSource(ruleInDb.getRuleDataSources().stream().filter(
                                ruleDataSources -> ruleDataSources.getDatasourceIndex() == 1).map((temp) -> {
                            temp.setFilter(StringUtils.isNotBlank(executionParameters.getTargetTableFilter()) ? executionParameters.getTargetTableFilter() : "");
                            return temp;
                        }).collect(Collectors.toList()));
                    }

                }
            } else {
                throw new UnExpectedRequestException("execution_parameters_name [" + request.getExecutionParametersName() + "] {&DOES_NOT_EXIST}");
            }
            String nowDate = QualitisConstants.PRINT_TIME_FORMAT.format(new Date());
            ruleInDb.setModifyUser(loginUser);
            ruleInDb.setModifyTime(nowDate);

            Rule savedRule = ruleDao.saveRule(ruleInDb);
            LOGGER.info("Succeed to update rule. Rule id: {}", savedRule.getId());
        }
        return new GeneralResponse<>("200", "{&BATCH_MODIFY_RULE_SUCCESSFULLY}", request.getRuleIdList());
    }

    private void setDataSourceRequest(DataSourceRequest dataSourceRequest, DataSourceRequest dataSourceRequestReal) {
        dataSourceRequest.setClusterName(dataSourceRequestReal.getClusterName());
        dataSourceRequest.setTableName(dataSourceRequestReal.getTableName());
        dataSourceRequest.setColNames(dataSourceRequestReal.getColNames());

        dataSourceRequest.setLinkisDataSourceId(dataSourceRequestReal.getLinkisDataSourceId());
        dataSourceRequest.setDataSourceEnvRequests(dataSourceRequest.getDataSourceEnvRequests());
        dataSourceRequest.setLinkisDataSourceType(dataSourceRequestReal.getLinkisDataSourceType());
        dataSourceRequest.setLinkisDataSourceName(dataSourceRequestReal.getLinkisDataSourceName());
        dataSourceRequest.setLinkisDataSourceVersionId(dataSourceRequest.getLinkisDataSourceVersionId());
    }

    @Override
    public AddFileRuleRequest constructFileRequest(Rule rule, RuleGroup ruleGroup, String workFlowName) {
        AddFileRuleRequest addFileRuleRequest = new AddFileRuleRequest();
        addFileRuleRequest.setRuleName(rule.getName());
        addFileRuleRequest.setRuleCnName(rule.getCnName());
        addFileRuleRequest.setNodeName(rule.getNodeName());
        addFileRuleRequest.setWorkFlowSpace(rule.getWorkFlowSpace());
        addFileRuleRequest.setWorkFlowName(StringUtils.isNotBlank(workFlowName) ? workFlowName : "");
        addFileRuleRequest.setWorkFlowVersion(StringUtils.isNotBlank(ruleGroup.getVersion()) ? ruleGroup.getVersion() : "");

        addFileRuleRequest.setRuleDetail(rule.getDetail());
        addFileRuleRequest.setAbortOnFailure(rule.getAbortOnFailure());

        addFileRuleRequest.setCsId(rule.getCsId());
        addFileRuleRequest.setRuleEnable(rule.getEnable());
        addFileRuleRequest.setUnionAll(rule.getUnionAll());
        addFileRuleRequest.setRuleGroupId(ruleGroup.getId());
        addFileRuleRequest.setProjectId(ruleGroup.getProjectId());
        addFileRuleRequest.setRuleTemplateId(rule.getTemplate().getId());

        if (StringUtils.isNotBlank(rule.getExecutionParametersName())) {
            addFileRuleRequest.setExecutionParametersName(rule.getExecutionParametersName());
            // Sync execution parameters in different project.
            synchroExecutionParameters(rule, ruleGroup);
        } else {
            addFileRuleRequest.setAlert(rule.getAlert());
            if (rule.getAlert() != null && rule.getAlert()) {
                addFileRuleRequest.setAlertLevel(rule.getAlertLevel());
                addFileRuleRequest.setAlertReceiver(rule.getAlertReceiver());
            }
            addFileRuleRequest.setAbortOnFailure(rule.getAbortOnFailure());
        }
        if (StringUtils.isNotBlank(rule.getBashContent())) {
            int startIndexOfRuleNameKey = rule.getBashContent().indexOf(RULE_NAME);
            int startIndexOfRuleName = rule.getBashContent().indexOf(rule.getName(), startIndexOfRuleNameKey);
            addFileRuleRequest.setBashContent(rule.getBashContent().substring(startIndexOfRuleNameKey, startIndexOfRuleName) + rule.getName());
        }

        if (CollectionUtils.isNotEmpty(rule.getRuleDataSources())) {
            DataSourceRequest dataSourceRequest = new DataSourceRequest();
            String clusterName = rule.getRuleDataSources().iterator().next().getClusterName();
            String databaseName = rule.getRuleDataSources().iterator().next().getDbName();
            String tableName = rule.getRuleDataSources().iterator().next().getTableName();
            String filter = rule.getRuleDataSources().iterator().next().getFilter();

            dataSourceRequest.setClusterName(clusterName);
            dataSourceRequest.setDbName(databaseName);
            dataSourceRequest.setTableName(tableName);
            dataSourceRequest.setFilter(filter);

            addFileRuleRequest.setDatasource(Lists.newArrayList(dataSourceRequest));

        }

        List<FileAlarmConfigRequest> alarmVariable = new ArrayList<>();
        setFileAlarm(rule, alarmVariable);
        addFileRuleRequest.setAlarm(true);
        addFileRuleRequest.setFileAlarmVariable(alarmVariable);
        return addFileRuleRequest;
    }

    private void setFileAlarm(Rule rule, List<FileAlarmConfigRequest> alarmVariable) {
        for (AlarmConfig alarmConfig : rule.getAlarmConfigs()) {
            FileAlarmConfigRequest fileAlarmConfigRequest = new FileAlarmConfigRequest();

            Double threshold = alarmConfig.getThreshold();
            Integer unit = alarmConfig.getFileOutputUnit();
            Integer alarmCompareType = alarmConfig.getCompareType();
            Integer checkTemplateName = alarmConfig.getCheckTemplate();

            fileAlarmConfigRequest.setFileOutputUnit(unit);
            fileAlarmConfigRequest.setCompareType(alarmCompareType);
            fileAlarmConfigRequest.setOutputMetaId(alarmConfig.getTemplateOutputMeta().getId());
            fileAlarmConfigRequest.setCheckTemplate(checkTemplateName);
            fileAlarmConfigRequest.setThreshold(threshold);

            RuleMetric ruleMetric = alarmConfig.getRuleMetric();
            // Recod rule metric info (unique code).
            if (ruleMetric != null) {
                String enCode = ruleMetric.getEnCode();

                fileAlarmConfigRequest.setRuleMetricEnCode(enCode);
                fileAlarmConfigRequest.setUploadAbnormalValue(alarmConfig.getUploadAbnormalValue());
                fileAlarmConfigRequest.setUploadRuleMetricValue(alarmConfig.getUploadRuleMetricValue());
            }
            fileAlarmConfigRequest.setDeleteFailCheckResult(alarmConfig.getDeleteFailCheckResult());
            alarmVariable.add(fileAlarmConfigRequest);
        }
    }

    @Override
    public AddMultiSourceRuleRequest constructMultiRequest(Rule rule, RuleGroup ruleGroup, String workFlowName) {
        AddMultiSourceRuleRequest addMultiSourceRuleRequest = new AddMultiSourceRuleRequest();
        addMultiSourceRuleRequest.setRuleName(rule.getName());
        addMultiSourceRuleRequest.setRuleCnName(rule.getCnName());
        addMultiSourceRuleRequest.setNodeName(rule.getNodeName());
        addMultiSourceRuleRequest.setWorkFlowSpace(rule.getWorkFlowSpace());
        addMultiSourceRuleRequest.setWorkFlowName(StringUtils.isNotBlank(workFlowName) ? workFlowName : "");
        addMultiSourceRuleRequest.setWorkFlowVersion(StringUtils.isNotBlank(ruleGroup.getVersion()) ? ruleGroup.getVersion() : "");

        addMultiSourceRuleRequest.setDeleteFailCheckResult(rule.getDeleteFailCheckResult());
        addMultiSourceRuleRequest.setMultiSourceRuleTemplateId(rule.getTemplate().getId());
        String clusterName = rule.getRuleDataSources().iterator().next().getClusterName();
        addMultiSourceRuleRequest.setAbortOnFailure(rule.getAbortOnFailure());
        addMultiSourceRuleRequest.setProjectId(ruleGroup.getProjectId());
        addMultiSourceRuleRequest.setRuleGroupId(ruleGroup.getId());
        addMultiSourceRuleRequest.setRuleDetail(rule.getDetail());
        addMultiSourceRuleRequest.setRuleEnable(rule.getEnable());
        addMultiSourceRuleRequest.setUnionAll(rule.getUnionAll());
        addMultiSourceRuleRequest.setClusterName(clusterName);
        addMultiSourceRuleRequest.setCsId(rule.getCsId());

        if (StringUtils.isNotBlank(rule.getExecutionParametersName())) {
            addMultiSourceRuleRequest.setExecutionParametersName(rule.getExecutionParametersName());
            // Sync execution parameters in different project.
            synchroExecutionParameters(rule, ruleGroup);
        } else {
            addMultiSourceRuleRequest.setAlert(rule.getAlert());
            if (rule.getAlert() != null && rule.getAlert()) {
                addMultiSourceRuleRequest.setAlertLevel(rule.getAlertLevel());
                addMultiSourceRuleRequest.setAlertReceiver(rule.getAlertReceiver());
            }
            addMultiSourceRuleRequest.setAbnormalCluster(rule.getAbnormalCluster());
            addMultiSourceRuleRequest.setAbnormalDatabase(rule.getAbnormalDatabase());
            addMultiSourceRuleRequest.setAbnormalProxyUser(rule.getAbnormalProxyUser());
            addMultiSourceRuleRequest.setStaticStartupParam(rule.getStaticStartupParam());
            addMultiSourceRuleRequest.setSpecifyStaticStartupParam(rule.getSpecifyStaticStartupParam());
        }
        if (StringUtils.isNotBlank(rule.getBashContent())) {
            int startIndexOfRuleNameKey = rule.getBashContent().indexOf(RULE_NAME);
            int startIndexOfRuleName = rule.getBashContent().indexOf(rule.getName(), startIndexOfRuleNameKey);
            addMultiSourceRuleRequest.setBashContent(rule.getBashContent().substring(startIndexOfRuleNameKey, startIndexOfRuleName) + rule.getName());
        }

        List<TemplateArgumentRequest> templateArgumentRequests = Lists.newArrayList();

        List<RuleVariable> filterRuleVariable = rule.getRuleVariables().stream().filter(ruleVariable ->
                ruleVariable.getTemplateMidTableInputMeta().getInputType().equals(TemplateInputTypeEnum.CONDITION.getCode())).collect(Collectors.toList());
        if (filterRuleVariable != null && filterRuleVariable.size() != 0) {
            TemplateArgumentRequest templateArgumentRequest = new TemplateArgumentRequest();
            templateArgumentRequest.setArgumentType(TemplateInputTypeEnum.COMPARISON_RESULTS_FOR_FILTER.getCode());
            templateArgumentRequest.setArgumentValue(filterRuleVariable.iterator().next().getValue());
            templateArgumentRequests.add(templateArgumentRequest);
        }

        // Data source request
        requestDataSource(rule, addMultiSourceRuleRequest);
        // Mapping
        List<MultiDataSourceJoinConfigRequest> mappings = new ArrayList<>();
        List<MultiDataSourceJoinConfigRequest> compareCols = new ArrayList<>();
        for (RuleDataSourceMapping mapping : rule.getRuleDataSourceMappings()) {
            MultiDataSourceJoinConfigRequest multiDataSourceJoinConfigRequest = new MultiDataSourceJoinConfigRequest();
            multiDataSourceJoinConfigRequest.setOperation(mapping.getOperation());
            multiDataSourceJoinConfigRequest.setLeftStatement(mapping.getLeftStatement());
            multiDataSourceJoinConfigRequest.setRightStatement(mapping.getRightStatement());

            List<MultiDataSourceJoinColumnRequest> left = getMultiDataSourceJoinColumnRequest(mapping.getLeftColumnNames(), mapping.getLeftColumnTypes());
            List<MultiDataSourceJoinColumnRequest> right = getMultiDataSourceJoinColumnRequest(mapping.getRightColumnNames(), mapping.getLeftColumnTypes());
            multiDataSourceJoinConfigRequest.setLeft(left);
            multiDataSourceJoinConfigRequest.setRight(right);
            if (MappingTypeEnum.CONNECT_FIELDS.getCode().equals(mapping.getMappingType())) {
                mappings.add(multiDataSourceJoinConfigRequest);
            } else if (MappingTypeEnum.MATCHING_FIELDS.getCode().equals(mapping.getMappingType())) {
                compareCols.add(multiDataSourceJoinConfigRequest);
            }
        }

        if (CollectionUtils.isNotEmpty(mappings)) {
            TemplateArgumentRequest templateArgumentRequest = new TemplateArgumentRequest();
            templateArgumentRequest.setArgumentType(TemplateInputTypeEnum.CONNECT_FIELDS.getCode());
            templateArgumentRequest.setArgumentValue(CustomObjectMapper.transObjectToJson(mappings));
            templateArgumentRequests.add(templateArgumentRequest);
        }

        if (CollectionUtils.isNotEmpty(compareCols)) {
            TemplateArgumentRequest templateArgumentRequest = new TemplateArgumentRequest();
            templateArgumentRequest.setArgumentType(TemplateInputTypeEnum.COMPARISON_FIELD_SETTINGS.getCode());
            templateArgumentRequest.setArgumentValue(CustomObjectMapper.transObjectToJson(compareCols));
            templateArgumentRequests.add(templateArgumentRequest);
        }

        addMultiSourceRuleRequest.setTemplateArgumentRequests(templateArgumentRequests);

        List<AlarmConfigRequest> alarmConfigRequests = constructAlarmConfigRequest(rule.getAlarmConfigs());
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        addMultiSourceRuleRequest.setAlarm(true);
        return addMultiSourceRuleRequest;
    }

    private void requestDataSource(Rule rule, AddMultiSourceRuleRequest addMultiSourceRuleRequest) {
        for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
            String databaseName = ruleDataSource.getDbName();
            String tableName = ruleDataSource.getTableName();
            String filter = ruleDataSource.getFilter();
            Integer datasourceIndex = ruleDataSource.getDatasourceIndex();
            MultiDataSourceConfigRequest dataSourceConfigRequest = new MultiDataSourceConfigRequest();
            dataSourceConfigRequest.setDbName(databaseName);
            dataSourceConfigRequest.setTableName(tableName);
            dataSourceConfigRequest.setFilter(filter);
            dataSourceConfigRequest.setProxyUser(ruleDataSource.getProxyUser());
            if (StringUtils.isNotBlank(ruleDataSource.getFileId())) {
                dataSourceConfigRequest.setFileId(ruleDataSource.getFileId());
                dataSourceConfigRequest.setFileType(ruleDataSource.getFileType());
                dataSourceConfigRequest.setFileHeader(ruleDataSource.getFileHeader());
                dataSourceConfigRequest.setFileHashValues(ruleDataSource.getFileHashValue());
                dataSourceConfigRequest.setFileTableDesc(ruleDataSource.getFileTableDesc());
                dataSourceConfigRequest.setFileDelimiter(ruleDataSource.getFileDelimiter());
            }
            if (ruleDataSource.getLinkisDataSourceId() != null) {
                dataSourceConfigRequest.setLinkisDataSourceType(TemplateDataSourceTypeEnum.getMessage(ruleDataSource.getDatasourceType()));
                dataSourceConfigRequest.setLinkisDataSourceName(ruleDataSource.getLinkisDataSourceName());
                dataSourceConfigRequest.setLinkisDataSourceId(ruleDataSource.getLinkisDataSourceId());
            }
            if (datasourceIndex == 0) {
                addMultiSourceRuleRequest.setSource(dataSourceConfigRequest);
            } else {
                addMultiSourceRuleRequest.setTarget(dataSourceConfigRequest);
            }
        }
    }

    private List<MultiDataSourceJoinColumnRequest> getMultiDataSourceJoinColumnRequest(String names, String types) {
        List<MultiDataSourceJoinColumnRequest> joinColumnRequests = new ArrayList<>();
        String[] columnNames = names.split(",");
        String[] columnTypes = types.split("\\|");
        for (int i = 0; i < columnNames.length; i++) {
            joinColumnRequests.add(new MultiDataSourceJoinColumnRequest(columnNames[i], columnTypes[i]));
        }
        return joinColumnRequests;
    }

    @Override
    public AddCustomRuleRequest constructCustomRequest(Rule rule, RuleGroup ruleGroup, String workFlowName) {
        AddCustomRuleRequest addCustomRuleRequest = new AddCustomRuleRequest();
        addCustomRuleRequest.setRuleName(rule.getName());
        addCustomRuleRequest.setRuleCnName(rule.getCnName());
        addCustomRuleRequest.setNodeName(rule.getNodeName());
        addCustomRuleRequest.setWorkFlowSpace(rule.getWorkFlowSpace());
        addCustomRuleRequest.setWorkFlowName(StringUtils.isNotBlank(workFlowName) ? workFlowName : "");
        addCustomRuleRequest.setWorkFlowVersion(StringUtils.isNotBlank(ruleGroup.getVersion()) ? ruleGroup.getVersion() : "");

        addCustomRuleRequest.setRuleDetail(rule.getDetail());
        addCustomRuleRequest.setSaveMidTable(rule.getTemplate().getSaveMidTable());
        if (rule.getFunctionType() != null && StringUtils.isNotBlank(rule.getFunctionContent())
                && StringUtils.isNotBlank(rule.getFromContent()) && StringUtils.isNotBlank(rule.getWhereContent())) {
            String functionContent = rule.getFunctionContent();
            String whereContent = rule.getWhereContent();
            String fromContent = rule.getFromContent();

            addCustomRuleRequest.setFunctionType(rule.getFunctionType());
            addCustomRuleRequest.setFunctionContent(functionContent);
            addCustomRuleRequest.setWhereContent(whereContent);
            addCustomRuleRequest.setFromContent(fromContent);
        } else {
            addCustomRuleRequest.setSqlCheckArea(rule.getTemplate().getMidTableAction());
        }

        addCustomRuleRequest.setAlarm(true);
        addCustomRuleRequest.setAlarmVariable(constructCustomAlarmConfigRequest(rule.getAlarmConfigs(), addCustomRuleRequest));
        RuleDataSource ruleDataSource = rule.getRuleDataSources().stream().filter(currentRuleDataSource -> currentRuleDataSource.getDatasourceIndex() != null).iterator().next();
        addCustomRuleRequest.setClusterName(ruleDataSource.getClusterName());
        addCustomRuleRequest.setProxyUser(ruleDataSource.getProxyUser());

        addCustomRuleRequest.setProjectId(ruleGroup.getProjectId());
        addCustomRuleRequest.setRuleGroupId(ruleGroup.getId());
        addCustomRuleRequest.setRuleEnable(rule.getEnable());
        addCustomRuleRequest.setUnionAll(rule.getUnionAll());
        addCustomRuleRequest.setCsId(rule.getCsId());

        if (StringUtils.isNotBlank(rule.getExecutionParametersName())) {
            addCustomRuleRequest.setExecutionParametersName(rule.getExecutionParametersName());
            // Sync execution parameters in different project.
            synchroExecutionParameters(rule, ruleGroup);
        } else {
            addCustomRuleRequest.setAlert(rule.getAlert());
            addCustomRuleRequest.setAlertLevel(rule.getAlertLevel());
            addCustomRuleRequest.setAlertReceiver(rule.getAlertReceiver());
            addCustomRuleRequest.setAbortOnFailure(rule.getAbortOnFailure());
            addCustomRuleRequest.setStaticStartupParam(rule.getStaticStartupParam());
            addCustomRuleRequest.setSpecifyStaticStartupParam(rule.getSpecifyStaticStartupParam());
        }
        if (StringUtils.isNotBlank(rule.getBashContent())) {
            int startIndexOfRuleNameKey = rule.getBashContent().indexOf(RULE_NAME);
            int startIndexOfRuleName = rule.getBashContent().indexOf(rule.getName(), startIndexOfRuleNameKey);
            addCustomRuleRequest.setBashContent(rule.getBashContent().substring(startIndexOfRuleNameKey, startIndexOfRuleName) + rule.getName());

        }

        if (ruleDataSource.getLinkisDataSourceId() != null) {
            addCustomRuleRequest.setLinkisDataSourceId(ruleDataSource.getLinkisDataSourceId());
            addCustomRuleRequest.setLinkisDataSourceName(ruleDataSource.getLinkisDataSourceName());
            addCustomRuleRequest.setLinkisDataSourceType(TemplateDataSourceTypeEnum.getMessage(ruleDataSource.getDatasourceType()));

            List<RuleDataSourceEnv> ruleDataSourceEnvs = ruleDataSource.getRuleDataSourceEnvs();
            if (CollectionUtils.isNotEmpty(ruleDataSourceEnvs)) {
                List<DataSourceEnvRequest> ruleDataSourceEnvRequests = new ArrayList<>(ruleDataSourceEnvs.size());
                for (RuleDataSourceEnv ruleDataSourceEnv : ruleDataSourceEnvs) {
                    ruleDataSourceEnvRequests.add(new DataSourceEnvRequest(ruleDataSourceEnv));
                }
                addCustomRuleRequest.setDataSourceEnvRequests(ruleDataSourceEnvRequests);
            }
        }

        String fileId = ruleDataSource.getFileId();
        if (StringUtils.isNotBlank(fileId)) {
            addCustomRuleRequest.setFileId(fileId);
            addCustomRuleRequest.setFileType(ruleDataSource.getFileType());
            addCustomRuleRequest.setFileHeader(ruleDataSource.getFileHeader());
            addCustomRuleRequest.setFileDelimiter(ruleDataSource.getFileDelimiter());
            addCustomRuleRequest.setFileHashValues(ruleDataSource.getFileHashValue());
            addCustomRuleRequest.setFileTableDesc(ruleDataSource.getFileTableDesc());
        }

        return addCustomRuleRequest;
    }

    private List<CustomAlarmConfigRequest> constructCustomAlarmConfigRequest(Set<AlarmConfig> alarmConfigs, AddCustomRuleRequest addCustomRuleRequest) {
        List<CustomAlarmConfigRequest> alarmConfigRequests = new ArrayList<>(alarmConfigs.size());

        for (AlarmConfig alarmConfig : alarmConfigs) {
            CustomAlarmConfigRequest alarmConfigRequest = new CustomAlarmConfigRequest();
            alarmConfigRequest.setCheckTemplate(alarmConfig.getCheckTemplate());
            alarmConfigRequest.setCompareType(alarmConfig.getCompareType());
            alarmConfigRequest.setThreshold(alarmConfig.getThreshold());

            RuleMetric ruleMetric = alarmConfig.getRuleMetric();
            alarmConfigRequest.setRuleMetricEnCode(ruleMetric != null ? ruleMetric.getEnCode() : "");
            alarmConfigRequest.setUploadAbnormalValue(alarmConfig.getUploadAbnormalValue());

            alarmConfigRequest.setUploadRuleMetricValue(alarmConfig.getUploadRuleMetricValue());
            alarmConfigRequest.setDeleteFailCheckResult(alarmConfig.getDeleteFailCheckResult());
            addCustomRuleRequest.setOutputName(ruleMetric != null ? ruleMetric.getName() : "");
            alarmConfigRequests.add(alarmConfigRequest);
        }

        return alarmConfigRequests;
    }

    @Override
    public AddRuleRequest constructSingleRequest(Rule rule, RuleGroup ruleGroup, String workFlowName) {
        AddRuleRequest addRuleRequest = new AddRuleRequest();
        addRuleRequest.setRuleName(rule.getName());
        // Same work flow name, a higher level version in rule group.
        // New work flow name, initialized version
        addRuleRequest.setNodeName(rule.getNodeName());
        addRuleRequest.setWorkFlowSpace(rule.getWorkFlowSpace());
        addRuleRequest.setWorkFlowName(StringUtils.isNotBlank(workFlowName) ? workFlowName : "");
        addRuleRequest.setWorkFlowVersion(StringUtils.isNotBlank(ruleGroup.getVersion()) ? ruleGroup.getVersion() : "");

        addRuleRequest.setRuleCnName(rule.getCnName());
        addRuleRequest.setRuleDetail(rule.getDetail());
        addRuleRequest.setRuleTemplateId(rule.getTemplate().getId());

        addRuleRequest.setAlarm(true);
        addRuleRequest.setAlarmVariable(constructAlarmConfigRequest(rule.getAlarmConfigs()));
        addRuleRequest.setTemplateArgumentRequests(constructTemplateArgumentRequest(rule));
        addRuleRequest.setDatasource(constructDataSourceRequest(rule.getRuleDataSources()));

        addRuleRequest.setDeleteFailCheckResult(rule.getDeleteFailCheckResult());
        addRuleRequest.setProjectId(ruleGroup.getProjectId());
        addRuleRequest.setRuleGroupId(ruleGroup.getId());
        addRuleRequest.setRuleEnable(rule.getEnable());
        addRuleRequest.setUnionAll(rule.getUnionAll());
        addRuleRequest.setCsId(rule.getCsId());

        if (StringUtils.isNotBlank(rule.getExecutionParametersName())) {
            addRuleRequest.setExecutionParametersName(rule.getExecutionParametersName());
            // Sync execution parameters in different project.
            synchroExecutionParameters(rule, ruleGroup);
        } else {
            addRuleRequest.setAlert(rule.getAlert());
            addRuleRequest.setAlertLevel(rule.getAlertLevel());
            addRuleRequest.setAlertReceiver(rule.getAlertReceiver());
            addRuleRequest.setAbortOnFailure(rule.getAbortOnFailure());
            addRuleRequest.setAbnormalCluster(rule.getAbnormalCluster());
            addRuleRequest.setAbnormalDatabase(rule.getAbnormalDatabase());
            addRuleRequest.setAbnormalProxyUser(rule.getAbnormalProxyUser());
            addRuleRequest.setSpecifyStaticStartupParam(rule.getSpecifyStaticStartupParam());
            addRuleRequest.setStaticStartupParam(rule.getStaticStartupParam());
        }

        if (StringUtils.isNotBlank(rule.getBashContent())) {
            addRuleRequest.setBashContent(rule.getBashContent());
        }
        return addRuleRequest;
    }

    private void synchroExecutionParameters(Rule rule, RuleGroup ruleGroup) {
        ExecutionParameters executionParameters = executionParametersDao.findByNameAndProjectId(rule.getExecutionParametersName(), rule.getProject().getId());
        if (!rule.getProject().getId().equals(ruleGroup.getProjectId())) {
            ExecutionParameters targetExecutionParameters = executionParametersDao.findByNameAndProjectId(rule.getExecutionParametersName(), ruleGroup.getProjectId());
            if (targetExecutionParameters == null) {
                targetExecutionParameters = new ExecutionParameters();
                BeanUtils.copyProperties(executionParameters, targetExecutionParameters);
                targetExecutionParameters.setId(null);
            } else {
                Long originId = targetExecutionParameters.getId();
                BeanUtils.copyProperties(executionParameters, targetExecutionParameters);
                targetExecutionParameters.setId(originId);
            }
            targetExecutionParameters.setProjectId(ruleGroup.getProjectId());
            executionParametersDao.saveExecutionParameters(targetExecutionParameters);
        }
    }

    private List<TemplateArgumentRequest> constructTemplateArgumentRequest(Rule rule) {
        List<TemplateMidTableInputMeta> templateMidTableInputMetaList = rule.getTemplate().getTemplateMidTableInputMetas().stream().filter(t -> TemplateMidTableUtil.shouldResponse(t)).collect(Collectors.toList());
        List<TemplateArgumentRequest> templateArgumentRequests = new ArrayList<>(templateMidTableInputMetaList.size());

        for (TemplateMidTableInputMeta templateMidTableInputMeta : templateMidTableInputMetaList) {
            for (RuleVariable ruleVariable : rule.getRuleVariables()) {
                TemplateArgumentRequest templateArgumentRequest = new TemplateArgumentRequest();

                if (ruleVariable.getTemplateMidTableInputMeta().equals(templateMidTableInputMeta)) {
                    String value = StringEscapeUtils.unescapeJava(ruleVariable.getValue());
                    if (templateMidTableInputMeta.getInputType().equals(TemplateInputTypeEnum.REGEXP.getCode()) && templateMidTableInputMeta.getRegexpType() != null) {
                        value = ruleVariable.getOriginValue();
                    }
                    templateArgumentRequest.setArgumentStep(InputActionStepEnum.TEMPLATE_INPUT_META.getCode());
                    templateArgumentRequest.setArgumentType(templateMidTableInputMeta.getInputType());
                    templateArgumentRequest.setArgumentId(templateMidTableInputMeta.getId());
                    templateArgumentRequest.setArgumentValue(value);

                    templateArgumentRequests.add(templateArgumentRequest);
                }
            }
        }
        return templateArgumentRequests;
    }

    private List<DataSourceRequest> constructDataSourceRequest(Set<RuleDataSource> ruleDataSources) {
        List<DataSourceRequest> dataSourceRequests = new ArrayList<>(ruleDataSources.size());
        for (RuleDataSource ruleDataSource : ruleDataSources) {
            DataSourceRequest dataSourceRequest = new DataSourceRequest();
            if (ruleDataSource.getLinkisDataSourceId() != null) {
                dataSourceRequest.setLinkisDataSourceId(ruleDataSource.getLinkisDataSourceId());
                dataSourceRequest.setLinkisDataSourceName(ruleDataSource.getLinkisDataSourceName());
                dataSourceRequest.setLinkisDataSourceType(TemplateDataSourceTypeEnum.getMessage(ruleDataSource.getDatasourceType()));
            }

            dataSourceRequest.setClusterName(ruleDataSource.getClusterName());
            dataSourceRequest.setBlackList(ruleDataSource.getBlackColName());
            dataSourceRequest.setProxyUser(ruleDataSource.getProxyUser());
            dataSourceRequest.setTableName(ruleDataSource.getTableName());
            dataSourceRequest.setDbName(ruleDataSource.getDbName());
            dataSourceRequest.setFilter(ruleDataSource.getFilter());
            String colNamesOrigin = ruleDataSource.getColName();
            List<DataSourceColumnRequest> dataSourceColumnRequests = new ArrayList<>();
            if (!StringUtils.isBlank(colNamesOrigin)) {
                String[] colNamesSplit = colNamesOrigin.split(SpecCharEnum.VERTICAL_BAR.getValue());
                for (String str : colNamesSplit) {
                    DataSourceColumnRequest dataSourceColumnRequest = new DataSourceColumnRequest();
                    dataSourceColumnRequest.setColumnName(str.split(":")[0]);
                    dataSourceColumnRequest.setDataType(str.split(":")[1]);
                    dataSourceColumnRequests.add(dataSourceColumnRequest);
                }
            }
            dataSourceRequest.setColNames(dataSourceColumnRequests);
            String fileId = ruleDataSource.getFileId();
            if (StringUtils.isNotBlank(fileId)) {
                dataSourceRequest.setFileId(fileId);
                dataSourceRequest.setFileType(ruleDataSource.getFileType());
                dataSourceRequest.setFileHeader(ruleDataSource.getFileHeader());
                dataSourceRequest.setFileDelimiter(ruleDataSource.getFileDelimiter());
                dataSourceRequest.setFileHashValues(ruleDataSource.getFileHashValue());
                dataSourceRequest.setFileTablesDesc(ruleDataSource.getFileTableDesc());

                String table = dataSourceRequest.getTableName();

                // UUID remove.
                if (StringUtils.isNotBlank(ruleDataSource.getFileId()) && StringUtils.isNotBlank(table) && table.contains(SpecCharEnum.BOTTOM_BAR.getValue()) && table.length() - UuidGenerator.generate().length() - 1 > 0) {
                    table = table.substring(0, table.length() - UuidGenerator.generate().length() - 1);
                }
                dataSourceRequest.setTableName(table);
            }
            List<RuleDataSourceEnv> ruleDataSourceEnvs = ruleDataSource.getRuleDataSourceEnvs();
            if (CollectionUtils.isNotEmpty(ruleDataSourceEnvs)) {
                List<DataSourceEnvRequest> ruleDataSourceEnvRequests = new ArrayList<>(ruleDataSourceEnvs.size());
                for (RuleDataSourceEnv ruleDataSourceEnv : ruleDataSourceEnvs) {
                    ruleDataSourceEnvRequests.add(new DataSourceEnvRequest(ruleDataSourceEnv));
                }
                dataSourceRequest.setDataSourceEnvRequests(ruleDataSourceEnvRequests);
            }
            dataSourceRequests.add(dataSourceRequest);
        }
        return dataSourceRequests;
    }

    private List<AlarmConfigRequest> constructAlarmConfigRequest(Set<AlarmConfig> alarmConfigs) {
        List<AlarmConfigRequest> alarmConfigRequests = new ArrayList<>(alarmConfigs.size());

        for (AlarmConfig alarmConfig : alarmConfigs) {
            AlarmConfigRequest alarmConfigRequest = new AlarmConfigRequest();
            alarmConfigRequest.setOutputMetaId(alarmConfig.getTemplateOutputMeta().getId());
            alarmConfigRequest.setCheckTemplate(alarmConfig.getCheckTemplate());
            alarmConfigRequest.setCompareType(alarmConfig.getCompareType());
            alarmConfigRequest.setThreshold(alarmConfig.getThreshold());

            RuleMetric ruleMetric = alarmConfig.getRuleMetric();
            alarmConfigRequest.setRuleMetricEnCode(ruleMetric != null ? ruleMetric.getEnCode() : "");

            alarmConfigRequest.setUploadAbnormalValue(alarmConfig.getUploadAbnormalValue());
            alarmConfigRequest.setUploadRuleMetricValue(alarmConfig.getUploadRuleMetricValue());

            alarmConfigRequests.add(alarmConfigRequest);
        }

        return alarmConfigRequests;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class}, propagation = Propagation.REQUIRED)
    public void importRule(RuleNodeRequest ruleNodeRequest, Project projectInDb, RuleGroup ruleGroupInDb, ObjectMapper objectMapper) throws UnExpectedRequestException, IOException {
        if (StringUtils.isNotBlank(ruleNodeRequest.getCheckAlertRule())) {
            LOGGER.info("Start to import check alert rule. Json:{}", ruleNodeRequest.getCheckAlertRule());
            handleCheckAlertRule(ruleNodeRequest.getCheckAlertRule(), projectInDb, ruleGroupInDb, objectMapper);
            LOGGER.info("Success to import check alert rule. ");
            return;
        }

        Rule rule = objectMapper.readValue(ruleNodeRequest.getRuleObject(), Rule.class);
        LOGGER.info("Find highest version of rule with same name={} and workflow name={}", rule.getName(), rule.getWorkFlowName());
        Rule ruleInDb = ruleDao.findHighestVersionByProjectAndWorkFlowName(projectInDb, rule.getWorkFlowName(), rule.getName());
        try {
            ruleBatchService.handleRule(rule, ruleInDb, ruleNodeRequest.getRuleObject(), ruleNodeRequest.getTemplateObject()
                , ruleNodeRequest.getTemplateDataVisibilityObject(), projectInDb, ruleGroupInDb.getRuleGroupName()
                , null, null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void handleCheckAlertRule(String checkAlertRule, Project projectInDb, RuleGroup ruleGroupInDb, ObjectMapper objectMapper) throws IOException {
        CheckAlert checkAlert = objectMapper.readValue(checkAlertRule, CheckAlert.class);
        CheckAlert checkAlertInDb = checkAlertDao.findByProjectAndWorkflowNameAndTopic(projectInDb, checkAlert.getWorkFlowName(), checkAlert.getTopic());

        if (checkAlertInDb == null) {
            LOGGER.info("Import in first time. That means adding.");

            checkAlert.setId(null);
            checkAlert.setProject(projectInDb);
            checkAlert.setRuleGroup(ruleGroupInDb);
            LOGGER.info("Success to import new check alert rule. {}", checkAlertDao.save(checkAlert).toString());
        } else {
            LOGGER.info("Import multiple times. That is to update.");

            checkAlert.setId(checkAlertInDb.getId());

            checkAlert.setProject(projectInDb);
            checkAlert.setRuleGroup(ruleGroupInDb);
            LOGGER.info("Success to import update check alert rule. {}", checkAlertDao.save(checkAlert).toString());
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class}, propagation = Propagation.REQUIRED)
    public void handleExecutionParamObject(RuleNodeRequests ruleNodeRequests) throws IOException, UnExpectedRequestException {
        // Project
        Project projectInDb = projectDao.findById(ruleNodeRequests.getNewProjectId());

        projectInDb = handleProject(projectInDb, ruleNodeRequests);
        List<String> finishedExecutionParameters = new ArrayList<>();
        for (RuleNodeRequest ruleNodeRequest : ruleNodeRequests.getRuleNodeRequests()) {
            if (StringUtils.isEmpty(ruleNodeRequest.getExecutionParamObject())) {
                continue;
            }
            // For execution parameters
            if (finishedExecutionParameters.contains(ruleNodeRequest.getExecutionParamObject())) {
                continue;
            } else {
                finishedExecutionParameters.add(ruleNodeRequest.getExecutionParamObject());
            }
            projectBatchService.handleExecutionParametersReal(ruleNodeRequest.getExecutionParamObject(), projectInDb.getCreateUser(), projectInDb.getId());
        }
    }
}
