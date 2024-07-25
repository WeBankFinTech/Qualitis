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

package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.webank.wedatasphere.qualitis.constant.TemplateFunctionNameEnum;
import com.webank.wedatasphere.qualitis.constant.UnionWayEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.OperateTypeEnum;
import com.webank.wedatasphere.qualitis.project.constant.ProjectTypeEnum;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.adapter.AutoArgumentAdapter;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.constant.RuleLockRangeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.StatisticsValueTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.AlarmConfigDao;
import com.webank.wedatasphere.qualitis.rule.dao.BdpClientHistoryDao;
import com.webank.wedatasphere.qualitis.rule.dao.ExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceMappingDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleTemplateDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleVariableDao;
import com.webank.wedatasphere.qualitis.rule.dao.StandardValueVersionDao;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.BdpClientHistory;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.entity.RuleVariable;
import com.webank.wedatasphere.qualitis.rule.entity.StandardValueVersion;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;
import com.webank.wedatasphere.qualitis.rule.exception.RuleLockException;
import com.webank.wedatasphere.qualitis.rule.request.AbstractCommonRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.EnableRequest;
import com.webank.wedatasphere.qualitis.rule.request.EnableRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplateArgumentRequest;
import com.webank.wedatasphere.qualitis.rule.response.RuleDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleEnableResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import com.webank.wedatasphere.qualitis.rule.service.AlarmConfigService;
import com.webank.wedatasphere.qualitis.rule.service.RuleDataSourceService;
import com.webank.wedatasphere.qualitis.rule.service.RuleLockService;
import com.webank.wedatasphere.qualitis.rule.service.RuleService;
import com.webank.wedatasphere.qualitis.rule.service.RuleTemplateService;
import com.webank.wedatasphere.qualitis.rule.service.RuleVariableService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateMidTableInputMetaService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateOutputMetaService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateStatisticsInputMetaService;
import com.webank.wedatasphere.qualitis.rule.util.TemplateMidTableUtil;
import com.webank.wedatasphere.qualitis.rule.util.TemplateStatisticsUtil;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTypeEnum;
//import com.webank.wedatasphere.qualitis.scheduled.dao.ScheduledFrontBackRuleDao;
//import com.webank.wedatasphere.qualitis.scheduled.dao.ScheduledWorkflowTaskRelationDao;
//import com.webank.wedatasphere.qualitis.scheduled.service.ScheduledTaskService;
import com.webank.wedatasphere.qualitis.service.UserService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Service
public class RuleServiceImpl extends AbstractRuleService implements RuleService {

    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private RuleTemplateDao ruleTemplateDao;
    @Autowired
    private RuleVariableDao ruleVariableDao;
    @Autowired
    private BdpClientHistoryDao bdpClientHistoryDao;

    @Autowired
    private TemplateStatisticsInputMetaService templateStatisticsInputMetaService;

    @Autowired
    private TemplateMidTableInputMetaService templateMidTableInputMetaService;

    @Autowired
    private TemplateOutputMetaService templateOutputMetaService;

    @Autowired
    private RuleDataSourceService ruleDataSourceService;

    @Autowired
    private RuleTemplateService ruleTemplateService;

    @Autowired
    private RuleVariableService ruleVariableService;

    @Autowired
    private AlarmConfigService alarmConfigService;

    @Autowired
    private AlarmConfigDao alarmConfigDao;

    @Autowired
    private RuleDataSourceDao ruleDataSourceDao;

    @Autowired
    private RuleDataSourceMappingDao ruleDataSourceMappingDao;

    @Autowired
    private AutoArgumentAdapter autoArgumentAdapter;

    @Autowired
    private RuleGroupDao ruleGroupDao;

    @Autowired
    private ExecutionParametersDao executionParametersDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private StandardValueVersionDao standardValueVersionDao;
//    @Autowired
//    private ScheduledWorkflowTaskRelationDao scheduledWorkflowTaskRelationDao;
//    @Autowired
//    private ScheduledFrontBackRuleDao scheduledFrontBackRuleDao;
    @Autowired
    private RuleLockService ruleLockService;
//    @Autowired
//    private ScheduledTaskService scheduledTaskService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleServiceImpl.class);

    private HttpServletRequest httpServletRequest;

    public RuleServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> addRule(AddRuleRequest request, String loginUser, boolean groupRules) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        return addRuleReal(request, loginUser, groupRules);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> addRuleForOuter(AbstractCommonRequest request, String loginUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        return addRuleReal((AddRuleRequest) request, loginUser, false);
    }

    private GeneralResponse<RuleResponse> addRuleReal(AddRuleRequest request, String loginUser, boolean groupRules)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        // Check Arguments
        AddRuleRequest.checkRequest(request, false);
        LOGGER.info("add single rule request detail: {}", request.toString());

        if (request.getRuleEnable() == null) {
            request.setRuleEnable(true);
        }
        if (request.getUnionWay() == null) {
            request.setUnionWay(UnionWayEnum.NO_COLLECT_CALCULATE.getCode());
        }
        // Check existence of rule template
        Template templateInDb = ruleTemplateService.checkRuleTemplate(request.getRuleTemplateId());

//        ruleTemplateService.checkAccessiblePermission(templateInDb);

        // Check if cluster supported
        checkDataSourceClusterLimit(request.getDatasource());
        // Check Arguments size
        checkRuleArgumentSize(request.getTemplateArgumentRequests(), templateInDb);
        // Check existence of project
        Project projectInDb = projectService.checkProjectExistence(request.getProjectId(), loginUser);
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);
        // Check existence of rule name
        checkRuleName(request.getRuleName(), request.getWorkFlowName(), request.getWorkFlowVersion(), projectInDb, null);
        //check the same rule name number
        checkRuleNameNumber(request.getRuleName(), projectInDb);

        RuleGroup ruleGroup;
        String ruleGroupName = request.getRuleGroupName();
        if (request.getRuleGroupId() != null) {
            ruleGroup = ruleGroupDao.findById(request.getRuleGroupId());
            if (ruleGroup == null) {
                throw new UnExpectedRequestException(String.format("Rule Group: %s {&DOES_NOT_EXIST}", request.getRuleGroupId()));
            }
            if (StringUtils.isNotEmpty(ruleGroupName)) {
                ruleGroup.setRuleGroupName(ruleGroupName);
            }
            ruleGroup = ruleGroupDao.saveRuleGroup(ruleGroup);
            if (CollectionUtils.isNotEmpty(ruleGroup.getRuleDataSources())) {
                groupRules = true;
            }
        } else if (request.getNewRuleGroupId() != null) {
            ruleGroup = ruleGroupDao.findById(request.getNewRuleGroupId());
            if (ruleGroup == null) {
                throw new UnExpectedRequestException(String.format("Rule Group: %s {&DOES_NOT_EXIST}", request.getRuleGroupId()));
            }
            if (StringUtils.isNotEmpty(ruleGroupName)) {
                ruleGroup.setRuleGroupName(ruleGroupName);
            }
            ruleGroup = ruleGroupDao.saveRuleGroup(ruleGroup);
            if (CollectionUtils.isNotEmpty(ruleGroup.getRuleDataSources())) {
                groupRules = true;
            }
        } else {
            if (StringUtils.isEmpty(ruleGroupName)) {
                ruleGroupName = "Group_" + UuidGenerator.generate();
            }
            ruleGroup = ruleGroupDao.saveRuleGroup(new RuleGroup(ruleGroupName, projectInDb.getId()));
        }

        // Create and save rule
        Rule newRule = new Rule();
        setBasicInfo(newRule, ruleGroup, templateInDb, projectInDb, request, loginUser);
        // For workflow node context.
        boolean cs = false;
        if (StringUtils.isNotBlank(request.getCsId())) {
            newRule.setCsId(request.getCsId());
            cs = true;
        }

        setExecutionParametersInfo(request, groupRules, projectInDb, newRule);

        handleStandardValue(request, newRule);

        Rule savedRule = ruleDao.saveRule(newRule);
        LOGGER.info("Succeed to save rule, rule ID: {}", savedRule.getId());

        // Save alarm_config,ruleVariable and ruleDataSource
        // Generate rule variable and save
        LOGGER.info("Start to generate and save rule_variable.");
        // For repeat fps table name when concurrent task is running.
        List<RuleVariable> savedRuleVariables = extractionRuleVariables(templateInDb, savedRule, request.getDatasource(), request.getTemplateArgumentRequests(), "Succeed to save rule_variables, rule_variables: {}");
        List<AlarmConfig> savedAlarmConfigs = new ArrayList<>();
        if (request.getAlarm()) {
            request.getAlarmVariable().stream().map(u -> {
                u.setUploadRuleMetricValue(request.getUploadRuleMetricValue() != null ? request.getUploadRuleMetricValue() : false);
                u.setDeleteFailCheckResult(request.getDeleteFailCheckResult() != null ? request.getDeleteFailCheckResult() : false);
                u.setUploadAbnormalValue(request.getUploadAbnormalValue() != null ? request.getUploadAbnormalValue() : false);
                return u;
            }).collect(Collectors.toList());
            savedAlarmConfigs = alarmConfigService.checkAndSaveAlarmVariable(request.getAlarmVariable(), savedRule, loginUser, request.getDatasource(), null, null);
            LOGGER.info("Succeed to save alarm_configs, alarm_configs: {}", savedAlarmConfigs);
        }

        super.recordEvent(loginUser, savedRule, OperateTypeEnum.CREATE_RULES);

        List<RuleDataSource> savedRuleDataSource = ruleDataSourceService.checkAndSaveRuleDataSource(request.getDatasource(), savedRule,
                null, cs, loginUser);
        LOGGER.info("Succeed to save rule_dataSources, rule_dataSources: {}, context service: {}", savedRuleDataSource, cs);
        setRuleInfo(savedRule, savedRuleVariables, savedAlarmConfigs, savedRuleDataSource);
        RuleResponse response = new RuleResponse(savedRule);
        LOGGER.info("Succeed to add rule, rule id: {}", response.getRuleId());
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&ADD_RULE_SUCCESSFULLY}", response);
    }

    private List<RuleVariable> extractionRuleVariables(Template templateInDb, Rule savedRule, List<DataSourceRequest> datasource, List<TemplateArgumentRequest> templateArgumentRequests, String variables) throws UnExpectedRequestException {
        addUuid(datasource);

        List<RuleVariable> ruleVariables = autoAdaptArgumentAndGetRuleVariable(templateInDb, datasource,
                templateArgumentRequests, savedRule);
        List<RuleVariable> savedRuleVariables = ruleVariableService.saveRuleVariable(ruleVariables);
        LOGGER.info(variables, savedRuleVariables);
        return savedRuleVariables;
    }

    private StandardValueVersion handleStandardValue(AddRuleRequest request, Rule newRule) {
        //标准值版本id和英文名称
        StandardValueVersion standardValueVersion = null;
        if (request.getStandardValueVersionId() != null) {
            standardValueVersion = standardValueVersionDao.findById(request.getStandardValueVersionId());
            if (standardValueVersion != null) {
                newRule.setStandardValueVersionId(request.getStandardValueVersionId());
                newRule.setStandardValueVersionEnName(standardValueVersion.getEnName());
            }
        }
        return standardValueVersion;
    }

    private void setRuleInfo(Rule savedRule, List<RuleVariable> savedRuleVariables, List<AlarmConfig> savedAlarmConfigs, List<RuleDataSource> savedRuleDataSource) {
        savedRule.setRuleVariables(new HashSet<>(savedRuleVariables));
        savedRule.setAlarmConfigs(new HashSet<>(savedAlarmConfigs));
        savedRule.setRuleDataSources(new HashSet<>(savedRuleDataSource));
    }

    private void setExecutionParametersInfo(AddRuleRequest request, boolean groupRules, Project projectInDb, Rule newRule) {
        if (StringUtils.isNotBlank(request.getExecutionParametersName())) {
            ExecutionParameters executionParameters = executionParametersDao.findByNameAndProjectId(request.getExecutionParametersName(), projectInDb.getId());
            if (groupRules || executionParameters != null) {
                newRule.setExecutionParametersName(request.getExecutionParametersName());
                newRule.setUnionWay(executionParameters.getUnionWay());
                request.setUploadAbnormalValue(executionParameters.getUploadAbnormalValue());
                request.setUploadRuleMetricValue(executionParameters.getUploadRuleMetricValue());
                request.setDeleteFailCheckResult(executionParameters.getDeleteFailCheckResult());
                if (StringUtils.isNotEmpty(executionParameters.getFilter())) {
                    request.setDatasource(request.getDatasource().stream().map((dataSourceRequest) -> {
                        dataSourceRequest.setFilter(executionParameters.getFilter());
                        return dataSourceRequest;
                    }).collect(Collectors.toList()));
                }
            }
        } else {
            setAddRuleInfo(request, newRule);
        }
    }

    private void setAddRuleInfo(AddRuleRequest request, Rule newRule) {
        extractionAddOrUpdate(newRule, request.getUnionWay(), request.getRuleEnable(), request.getAlert(), request.getAlertLevel(), request.getAlertReceiver(), request.getDeleteFailCheckResult(), request.getAbortOnFailure(), request.getStaticStartupParam(), request.getSpecifyStaticStartupParam(), request.getAbnormalCluster(), request.getAbnormalDatabase(), request.getAbnormalProxyUser());
    }

    private void extractionAddOrUpdate(Rule newRule, Integer unionWay, Boolean ruleEnable, Boolean alert, Integer alertLevel, String alertReceiver, Boolean deleteFailCheckResult, Boolean abortOnFailure, String staticStartupParam, Boolean specifyStaticStartupParam, String abnormalCluster, String abnormalDatabase, String abnormalProxyUser) {
        newRule.setExecutionParametersName(null);
        newRule.setAlert(alert);
        if (alert != null && alert) {
            newRule.setAlertLevel(alertLevel);
            newRule.setAlertReceiver(alertReceiver);
        }
        newRule.setAbortOnFailure(abortOnFailure);
        newRule.setStaticStartupParam(staticStartupParam);
        newRule.setDeleteFailCheckResult(deleteFailCheckResult);
        newRule.setSpecifyStaticStartupParam(specifyStaticStartupParam);
        newRule.setAbnormalCluster(StringUtils.isNotBlank(abnormalCluster) ? abnormalCluster : null);
        newRule.setAbnormalDatabase(StringUtils.isNotBlank(abnormalDatabase) ? abnormalDatabase : null);
        newRule.setAbnormalProxyUser(StringUtils.isNotBlank(abnormalProxyUser) ? abnormalProxyUser : null);
        newRule.setEnable(ruleEnable);
        newRule.setUnionWay(unionWay);
    }

    @Override
    public void addUuid(List<DataSourceRequest> datasources) {
        for (DataSourceRequest dataSourceRequest : datasources) {
            String fileId = dataSourceRequest.getFileId();
            if (StringUtils.isNotBlank(fileId)) {
                dataSourceRequest.setTableName(dataSourceRequest.getTableName().concat("_").concat(UuidGenerator.generate()));
            }
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<Object> deleteRule(DeleteRuleRequest request, String loginUser) throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check Arguments
        DeleteRuleRequest.checkRequest(request);
        LOGGER.info("delete single rule request detail: {}", request.toString());

        // Check existence of rule
        Rule ruleInDb = ruleDao.findById(request.getRuleId());
        if (ruleInDb == null || !ruleInDb.getRuleType().equals(RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode())) {
            throw new UnExpectedRequestException("Rule id [" + request.getRuleId() + "]) {&IS_NOT_A_RULE_WITH_TEMPLATE}");
        }
        // Check existence of project
        Project projectInDb = projectService.checkProjectExistence(ruleInDb.getProject().getId(),
                loginUser);
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);

        return deleteRuleReal(ruleInDb,null);
    }

    @Override
    public GeneralResponse<Object> deleteRuleReal(Rule rule,String loginUser) throws UnExpectedRequestException {
        // Delete bdp-client history
        BdpClientHistory bdpClientHistory = bdpClientHistoryDao.findByRuleId(rule.getId());
        if (bdpClientHistory != null) {
            bdpClientHistoryDao.delete(bdpClientHistory);
        }
        // Delete rule
//        scheduledTaskService.checkRuleGroupIfDependedBySchedule(rule.getRuleGroup());
        ruleDao.deleteRule(rule);
        LOGGER.info("Succeed to delete rule, rule id: {}", rule.getId());

        super.recordEvent(StringUtils.isNotBlank(loginUser) ? loginUser : HttpUtils.getUserName(httpServletRequest), rule, OperateTypeEnum.DELETE_RULES);

        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&DELETE_RULE_SUCCESSFULLY}", null);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> modifyRuleDetail(ModifyRuleRequest request, String loginUser, boolean groupRules)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        return modifyRuleDetailReal(request, loginUser, groupRules);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    @Override
    public GeneralResponse<RuleResponse> modifyRuleDetailWithLock(ModifyRuleRequest request, String loginUser, boolean groupRules) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException, RuleLockException {
        if (!ruleLockService.tryAcquire(request.getRuleId(), RuleLockRangeEnum.RULE, loginUser)) {
            LOGGER.warn("Failed to acquire lock");
            throw new RuleLockException("{&RULE_LOCK_ACQUIRE_FAILED}");
        }
        try {
            return modifyRuleDetailReal(request, loginUser, groupRules);
        } finally {
            ruleLockService.release(request.getRuleId(), RuleLockRangeEnum.RULE, loginUser);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> modifyRuleDetailForOuter(ModifyRuleRequest modifyRuleRequest, String userName)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        return modifyRuleDetailReal(modifyRuleRequest, userName, false);
    }

    @Override
    public List<Rule> getDeployStandardVersionId(Long standardVersionId) {
        return ruleDao.getDeployStandardVersionId(standardVersionId);
    }

    private GeneralResponse<RuleResponse> modifyRuleDetailReal(ModifyRuleRequest request, String loginUser, boolean groupRules)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        // Check Arguments
        ModifyRuleRequest.checkRequest(request);
        LOGGER.info("modify single rule request detail: {}", request.toString());
        if (request.getRuleEnable() == null) {
            request.setRuleEnable(true);
        }
        if (request.getUnionWay() == null) {
            request.setUnionWay(UnionWayEnum.NO_COLLECT_CALCULATE.getCode());
        }
        // Check existence of rule
        Rule ruleInDb = ruleDao.findById(request.getRuleId());
        if (ruleInDb == null) {
            throw new UnExpectedRequestException("rule_id [" + request.getRuleId() + "] {&DOES_NOT_EXIST}");
        }

        // Check existence of project
        Project projectInDb = projectService.checkProjectExistence(ruleInDb.getProject().getId(), loginUser);
        if (!ruleInDb.getRuleType().equals(RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode())) {
            throw new UnExpectedRequestException("rule(id: [" + request.getRuleId() + "]) {&IS_NOT_A_RULE_WITH_TEMPLATE}");
        }
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);
        LOGGER.info("Succeed to find rule, rule id: {}", ruleInDb.getId());
        // For workflow node context.
        boolean cs = false;
        if (StringUtils.isNotBlank(request.getCsId())) {
            cs = true;
        }
        if (CollectionUtils.isNotEmpty(ruleInDb.getRuleGroup().getRuleDataSources())) {
            groupRules = true;
        }

        if (request.getRuleGroupId() != null) {
            RuleGroup ruleGroup = ruleGroupDao.findById(request.getRuleGroupId());
            if (ruleGroup != null) {
                ruleInDb.setRuleGroup(ruleGroup);
            }
        }

        if (request.getNewRuleGroupId() != null) {
            RuleGroup ruleGroup = ruleGroupDao.findById(request.getNewRuleGroupId());
            if (ruleGroup != null) {
                ruleInDb.setRuleGroup(ruleGroup);
            }
        }

        String ruleGroupName = request.getRuleGroupName();
        if (StringUtils.isNotEmpty(ruleGroupName)) {
            ruleInDb.getRuleGroup().setRuleGroupName(ruleGroupName);
            ruleGroupDao.saveRuleGroup(ruleInDb.getRuleGroup());
        }
        ruleInDb.setCsId(request.getCsId());
        paddingModifyInfo(request, groupRules, ruleInDb, projectInDb);

        // Check existence of rule template
        Template templateInDb = ruleTemplateService.checkRuleTemplate(request.getRuleTemplateId());

//        ruleTemplateService.checkAccessiblePermission(templateInDb);

        // Check cluster name supported
        checkDataSourceClusterLimit(request.getDatasource());
        // Check arguments size
        checkRuleArgumentSize(request.getTemplateArgumentRequests(), templateInDb);
        // Check existence of rule
        checkRuleName(request.getRuleName(), request.getWorkFlowName(), request.getWorkFlowVersion(), ruleInDb.getProject(), ruleInDb.getId());
        //check the same rule name number
        checkRuleNameNumber(request.getRuleName(), projectInDb);
        // Basic rule info.
        setBasicInfo(ruleInDb, templateInDb, request, loginUser);
        Rule savedRule = ruleDao.saveRule(ruleInDb);
        LOGGER.info("Succeed to save rule, rule id: {}", savedRule.getId());
        // Delete all alarm_config,rule_variable,rule_datasource
        alarmConfigService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all alarm_config, rule id: {}", ruleInDb.getId());
        ruleVariableService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all rule_variable, rule id: {}", ruleInDb.getId());
        ruleDataSourceService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all rule_dataSources, rule id: {}", ruleInDb.getId());
        // For repeat fps table name when concurrent task is running.
        List<RuleVariable> savedRuleVariables = extractionRuleVariables(templateInDb, savedRule, request.getDatasource(), request.getTemplateArgumentRequests(), "Succeed to save rule_variable, rule_variable: {}");

        List<AlarmConfig> savedAlarmConfigs = new ArrayList<>();
        if (request.getAlarm()) {
            request.getAlarmVariable().stream().map(u -> {
                u.setUploadRuleMetricValue(request.getUploadRuleMetricValue() != null ? request.getUploadRuleMetricValue() : false);
                u.setUploadAbnormalValue(request.getUploadAbnormalValue() != null ? request.getUploadAbnormalValue() : false);
                u.setDeleteFailCheckResult(request.getDeleteFailCheckResult() != null ? request.getDeleteFailCheckResult() : false);
                return u;
            }).collect(Collectors.toList());
            savedAlarmConfigs = alarmConfigService.checkAndSaveAlarmVariable(request.getAlarmVariable(), savedRule, loginUser, request.getDatasource(), null, null);
            LOGGER.info("Succeed to save alarm_configs, alarm_configs: {}", savedAlarmConfigs);
        }
        List<RuleDataSource> savedRuleDataSource = ruleDataSourceService.checkAndSaveRuleDataSource(request.getDatasource(), savedRule,
                null, cs, loginUser);
        LOGGER.info("Succeed to save rule_dataSources, rule_dataSources: {}", savedRuleDataSource);

        super.recordEvent(loginUser, savedRule, OperateTypeEnum.MODIFY_RULES);
        setRuleInfo(savedRule, savedRuleVariables, savedAlarmConfigs, savedRuleDataSource);
        RuleResponse response = new RuleResponse(savedRule);
        LOGGER.info("Succeed to modify rule. response: {}", response);
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&MODIFY_RULE_SUCCESSFULLY}", response);
    }

    private StandardValueVersion paddingModifyInfo(ModifyRuleRequest request, boolean groupRules, Rule ruleInDb, Project projectInDb) {
        setExecutionParametersInfo(request, groupRules, ruleInDb, projectInDb);

        //标准值版本id和英文名称
        StandardValueVersion standardValueVersion = null;
        if (request.getStandardValueVersionId() != null) {
            standardValueVersion = standardValueVersionDao.findById(request.getStandardValueVersionId());
            if (standardValueVersion != null) {
                ruleInDb.setStandardValueVersionId(request.getStandardValueVersionId());
                ruleInDb.setStandardValueVersionEnName(standardValueVersion.getEnName());
            }
        } else {
            ruleInDb.setStandardValueVersionId(null);
            ruleInDb.setStandardValueVersionEnName("");
        }
        return standardValueVersion;
    }

    private void setExecutionParametersInfo(ModifyRuleRequest request, boolean groupRules, Rule ruleInDb, Project projectInDb) {
        if (StringUtils.isNotBlank(request.getExecutionParametersName())) {
            ExecutionParameters executionParameters = executionParametersDao.findByNameAndProjectId(request.getExecutionParametersName(), projectInDb.getId());
            if (groupRules || executionParameters != null) {
                ruleInDb.setExecutionParametersName(request.getExecutionParametersName());
                ruleInDb.setUnionWay(executionParameters.getUnionWay());
                request.setUploadAbnormalValue(executionParameters.getUploadAbnormalValue());
                request.setUploadRuleMetricValue(executionParameters.getUploadRuleMetricValue());
                request.setDeleteFailCheckResult(executionParameters.getDeleteFailCheckResult());
                if (StringUtils.isNotEmpty(executionParameters.getFilter())) {
                    request.setDatasource(request.getDatasource().stream().map((dataSourceRequest) -> {
                        dataSourceRequest.setFilter(executionParameters.getFilter());
                        return dataSourceRequest;
                    }).collect(Collectors.toList()));
                }
            }
        } else {
            setUpdateRuleInfo(request, ruleInDb);
        }
    }

    private void setUpdateRuleInfo(ModifyRuleRequest request, Rule ruleInDb) {
        extractionAddOrUpdate(ruleInDb, request.getUnionWay(), request.getRuleEnable(), request.getAlert(), request.getAlertLevel(), request.getAlertReceiver(), request.getDeleteFailCheckResult(), request.getAbortOnFailure(), request.getStaticStartupParam(), request.getSpecifyStaticStartupParam(), request.getAbnormalCluster(), request.getAbnormalDatabase(), request.getAbnormalProxyUser());
    }

    private Boolean handleObjectEqual(AddRuleRequest request, ExecutionParameters executionParameters) {
        return CommonChecker.compareIdentical(request.getUnionWay(), request.getAbortOnFailure(), request.getSpecifyStaticStartupParam(), request.getStaticStartupParam()
                , request.getAbnormalDatabase(), request.getAbnormalCluster(), request.getAlert(), request.getAlertLevel(), request.getAlertReceiver(), request.getAbnormalProxyUser(), request.getDeleteFailCheckResult(), request.getUploadRuleMetricValue(), request.getUploadAbnormalValue(), executionParameters);
    }

    private Boolean handleObjectEqual(ModifyRuleRequest request, ExecutionParameters executionParameters) {
        return CommonChecker.compareIdentical(request.getUnionWay(), request.getAbortOnFailure(), request.getSpecifyStaticStartupParam(), request.getStaticStartupParam()
                , request.getAbnormalDatabase(), request.getAbnormalCluster(), request.getAlert(), request.getAlertLevel(), request.getAlertReceiver(), request.getAbnormalProxyUser(), request.getDeleteFailCheckResult(), request.getUploadRuleMetricValue(), request.getUploadAbnormalValue(), executionParameters);
    }

    private void setBasicInfo(Rule newRule, RuleGroup ruleGroup, Template templateInDb, Project projectInDb, AddRuleRequest request, String loginUser) {
        String nowDate = QualitisConstants.PRINT_TIME_FORMAT.format(new Date());
        newRule.setTemplate(templateInDb);
        newRule.setName(request.getRuleName());
        newRule.setCnName(request.getRuleCnName());
        newRule.setDetail(request.getRuleDetail());
        newRule.setRuleTemplateName(templateInDb.getName());
        newRule.setRuleType(RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode());
        newRule.setAlarm(request.getAlarm());
        newRule.setCreateUser(loginUser);
        newRule.setProject(projectInDb);
        newRule.setRuleGroup(ruleGroup);
        newRule.setCreateTime(nowDate);
        newRule.setBashContent(request.getBashContent());
        newRule.setWorkFlowName(request.getWorkFlowName());
        newRule.setWorkFlowVersion(request.getWorkFlowVersion());
        newRule.setWorkFlowSpace(request.getWorkFlowSpace());
        newRule.setNodeName(request.getNodeName());
        newRule.setRuleNo(request.getRuleNo());
        newRule.setEnable(request.getRuleEnable());
        newRule.setUnionWay(request.getUnionWay());
    }

    private void setBasicInfo(Rule ruleInDb, Template templateInDb, ModifyRuleRequest request, String loginUser) {
        String nowDate = QualitisConstants.PRINT_TIME_FORMAT.format(new Date());
        ruleInDb.setTemplate(templateInDb);
        ruleInDb.setAlarm(request.getAlarm());
        ruleInDb.setName(request.getRuleName());
        ruleInDb.setDetail(request.getRuleDetail());
        ruleInDb.setCnName(request.getRuleCnName());
        ruleInDb.setRuleTemplateName(templateInDb.getName());

        ruleInDb.setModifyUser(loginUser);
        ruleInDb.setModifyTime(nowDate);
        ruleInDb.setBashContent(request.getBashContent());
        ruleInDb.setEnable(request.getRuleEnable());
        if (StringUtils.isNotEmpty(request.getWorkFlowName())) {
            ruleInDb.setWorkFlowName(request.getWorkFlowName());
        }
        if (StringUtils.isNotEmpty(request.getWorkFlowVersion())) {
            ruleInDb.setWorkFlowVersion(request.getWorkFlowVersion());
        }
        ruleInDb.setWorkFlowSpace(StringUtils.isNotEmpty(request.getWorkFlowSpace()) ? request.getWorkFlowSpace() : null);
        ruleInDb.setNodeName(StringUtils.isNotEmpty(request.getNodeName()) ? request.getNodeName() : null);
    }

    @Override
    public GeneralResponse<RuleDetailResponse> getRuleDetail(Long ruleId) throws UnExpectedRequestException {
        LOGGER.info("get single rule request detail: {}", ruleId);
        // Check existence of rule
        Rule ruleInDb = ruleDao.findById(ruleId);
        if (ruleInDb == null) {
            throw new UnExpectedRequestException("rule_id [" + ruleId + "] {&DOES_NOT_EXIST}");
        }
        if (!ruleInDb.getRuleType().equals(RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode())) {
            throw new UnExpectedRequestException("rule(id: [" + ruleId + "]) {&IS_NOT_A_RULE_WITH_TEMPLATE}");
        }

        Long projectInDbId = ruleInDb.getProject().getId();
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        projectService.checkProjectExistence(projectInDbId, loginUser);

        LOGGER.info("Succeed to find rule, rule id: {}", ruleInDb.getId());

        List<RuleVariable> ruleVariableList = ruleVariableService.queryByRules(Arrays.asList(ruleInDb));
        Map<Long, List<RuleVariable>> ruleVariableMap = ruleVariableList.stream().collect(Collectors.groupingBy(ruleVariable -> ruleVariable.getRule().getId()));
        RuleDetailResponse response = new RuleDetailResponse(ruleInDb, ruleVariableMap.getOrDefault(ruleInDb.getId(), Collections.emptyList()));
        LOGGER.info("Succeed to get rule detail, rule id: {}", ruleId);
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_RULE_DETAIL_SUCCESSFULLY}", response);
    }

    @Override
    public void checkRuleName(String ruleName, String workFlowName, String workFlowVersion, Project project, Long ruleId) throws UnExpectedRequestException {
        //1普通项目 2工作流项目
        if (ProjectTypeEnum.NORMAL_PROJECT.getCode().equals(project.getProjectType())) {
            examineRuleName(ruleName, project, ruleId);
        } else if (ProjectTypeEnum.WORKFLOW_PROJECT.getCode().equals(project.getProjectType())) {
            if (StringUtils.isNotBlank(workFlowName) && StringUtils.isNotBlank(workFlowVersion)) {
                Long ruleDbId = ruleDao.selectMateRule(ruleName, workFlowName, workFlowVersion, project.getId());
                Rule marryRule = null;
                if (ruleDbId != null) {
                    marryRule = ruleDao.findById(ruleDbId);
                }
                if (marryRule != null) {
                    if (ruleId != null && !marryRule.getId().equals(ruleId)) {
                        throw new UnExpectedRequestException("Rule name {&ALREADY_EXIST}");
                    }
                    if (ruleId == null) {
                        throw new UnExpectedRequestException("Rule name {&ALREADY_EXIST}");
                    }
                }
            } else {
                examineRuleName(ruleName, project, ruleId);
            }
        }

    }

    private void examineRuleName(String ruleName, Project project, Long ruleId) throws UnExpectedRequestException {
        List<Map<String, Object>> rules = ruleDao.findSpecialInfoByProject(project, ruleName);
        if (CollectionUtils.isNotEmpty(rules)) {
            for (Map<String, Object> rule : rules) {
                Long currentRuleId = (Long) rule.get("rule_id");
                String currentRuleName = (String) rule.get("rule_name");
                if (currentRuleName.equals(ruleName)) {
                    if (ruleId != null && !currentRuleId.equals(ruleId)) {
                        throw new UnExpectedRequestException("Rule name {&ALREADY_EXIST}");
                    }
                    if (ruleId == null) {
                        throw new UnExpectedRequestException("Rule name {&ALREADY_EXIST}");
                    }
                }
            }
        }
    }

    @Override
    public void checkRuleNameNumber(String ruleName, Project project) throws UnExpectedRequestException {
        int total = ruleDao.countByProjectAndRuleName(ruleName, project.getId());
        if (total > QualitisConstants.DSS_NODE_VERSION_NUM) {
            Rule rule = ruleDao.findMinWorkFlowVersionRule(ruleName, project.getId());
            if (rule != null) {
                // Delete rule
//                scheduledTaskService.checkRuleGroupIfDependedBySchedule(rule.getRuleGroup());
                ruleDao.deleteRule(rule);
                LOGGER.info("Succeed to delete rule, rule id: {}", rule.getId());
            }
        }
    }

    @Override
    public void checkRuleOfTemplate(Template templateInDb) throws UnExpectedRequestException {
        List<Rule> rules = ruleDao.findByTemplate(templateInDb);
        if (rules != null && !rules.isEmpty()) {
            throw new UnExpectedRequestException("{&CANNOT_DELETE_TEMPLATE}");
        }
    }


    /**
     * Check argument size and if arguments left
     *
     * @param requests
     * @param template
     */
    private void checkRuleArgumentSize(List<TemplateArgumentRequest> requests, Template template) throws UnExpectedRequestException {
        if (CollectionUtils.isNotEmpty(requests)) {
            requests = requests.stream().filter(templateArgumentRequest -> !TemplateInputTypeEnum.FIELD.getCode().equals(templateArgumentRequest.getArgumentType())).collect(Collectors.toList());
        }
        List<Long> midTableArgumentId = template.getTemplateMidTableInputMetas().stream().filter(t -> TemplateMidTableUtil.shouldResponse(t)).filter(t -> !TemplateInputTypeEnum.FIELD.getCode().equals(t.getInputType()))
                .map(TemplateMidTableInputMeta::getId).collect(Collectors.toList());
        List<Long> statisticsArgumentId = template.getStatisticAction().stream().filter(t -> TemplateStatisticsUtil.shouldResponse(t))
                .map(TemplateStatisticsInputMeta::getId).collect(Collectors.toList());
        if (requests.size() != midTableArgumentId.size() + statisticsArgumentId.size()) {
            throw new UnExpectedRequestException("{&THE_SIZE_OF_TEMPLATE_ARGUMENT_REQUEST_IS_NOT_CORRECT}");
        }

        List<Long> requestId = requests.stream().map(TemplateArgumentRequest::getArgumentId).collect(Collectors.toList());
        for (Long id : midTableArgumentId) {
            if (!requestId.contains(id)) {
                throw new UnExpectedRequestException("{&MISSING_ARGUMENT_ID_OF_MID_TABLE_ACTION}: [" + id + "]");
            }
        }

        for (Long id : statisticsArgumentId) {
            if (!requestId.contains(id)) {
                throw new UnExpectedRequestException("{&MISSING_ARGUMENT_ID_OF_STATISTICS_ACTION}: [" + id + "]");
            }
        }
    }

    /**
     * Auto adapt arguments and generate rule variables
     *
     * @param template
     * @param dataSourceRequests
     * @param templateArgumentRequests
     * @param rule
     * @return
     */
    private List<RuleVariable> autoAdaptArgumentAndGetRuleVariable(Template template, List<DataSourceRequest> dataSourceRequests,
                                                                   List<TemplateArgumentRequest> templateArgumentRequests, Rule rule) throws UnExpectedRequestException {
        List<RuleVariable> ruleVariables = new ArrayList<>();

        // Find related value by template type
        for (TemplateMidTableInputMeta templateMidTableInputMeta : template.getTemplateMidTableInputMetas()) {
            if (dataSourceRequests.size() != 1) {
                throw new UnExpectedRequestException("{&ONLY_SINGLE_DATASOURCE_CAN_BE_AUTO_ADAPTED}");
            }
            DataSourceRequest dataSourceRequest = dataSourceRequests.get(0);

            if (templateMidTableInputMeta.getInputType().equals(TemplateInputTypeEnum.CONDITION.getCode())) {
                continue;
            }
            Map<String, String> autoAdaptValue = autoArgumentAdapter.getAdaptValue(templateMidTableInputMeta, dataSourceRequest, templateArgumentRequests);
            ruleVariables.add(new RuleVariable(rule, InputActionStepEnum.TEMPLATE_INPUT_META.getCode(), templateMidTableInputMeta, null, autoAdaptValue));
        }

        for (TemplateStatisticsInputMeta templateStatisticsInputMeta : template.getStatisticAction()) {
            // FIELD
            if (templateStatisticsInputMeta.getValueType().equals(StatisticsValueTypeEnum.FIELD.getCode())) {
                TemplateArgumentRequest request = findRequest(templateArgumentRequests, templateStatisticsInputMeta);
                // Check Arguments
                TemplateArgumentRequest.checkRequest(request);
                ruleVariables.add(new RuleVariable(rule, InputActionStepEnum.STATISTICS_ARG.getCode(), null,
                        templateStatisticsInputMeta, request.getArgumentValue(), null, null, null));
            }
        }

        return ruleVariables;
    }

    /**
     * Check cluster name supported
     *
     * @param datasource
     * @throws UnExpectedRequestException
     */
    private void checkDataSourceClusterLimit(List<DataSourceRequest> datasource) throws UnExpectedRequestException {
        if (datasource == null || datasource.isEmpty()) {
            return;
        }
        Set<String> submittedClusterNames = new HashSet<>();
        for (DataSourceRequest request : datasource) {
            submittedClusterNames.add(request.getClusterName());
        }
        ruleDataSourceService.checkDataSourceClusterSupport(submittedClusterNames);
    }

    private TemplateArgumentRequest findRequest(List<TemplateArgumentRequest> requests, TemplateMidTableInputMeta templateMidTableInputMeta) throws UnExpectedRequestException {
        Long id = templateMidTableInputMeta.getId();
        Integer actionStep = InputActionStepEnum.TEMPLATE_INPUT_META.getCode();
        return findRequest(requests, id, actionStep);
    }

    private TemplateArgumentRequest findRequest(List<TemplateArgumentRequest> requests, TemplateStatisticsInputMeta templateStatisticsInputMeta) throws UnExpectedRequestException {
        Long id = templateStatisticsInputMeta.getId();
        Integer actionStep = InputActionStepEnum.STATISTICS_ARG.getCode();
        return findRequest(requests, id, actionStep);
    }

    private TemplateArgumentRequest findRequest(List<TemplateArgumentRequest> requests, Long id, Integer actionStep) throws UnExpectedRequestException {
        List<TemplateArgumentRequest> request = requests.stream().filter(r -> r.getArgumentStep().equals(actionStep) && r.getArgumentId().equals(id)).collect(Collectors.toList());
        if (request.size() != 1) {
            throw new UnExpectedRequestException("{&CAN_NOT_FIND_SUITABLE_REQUEST}");
        }
        TemplateArgumentRequest.checkRequest(request.get(0));

        return request.get(0);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> addRuleForUpload(AddRuleRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        return addRuleReal(request, loginUser, false);
    }


    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleEnableResponse> enableRule(EnableRuleRequest request, String loginUser) throws UnExpectedRequestException, PermissionDeniedRequestException {
        //Check Argument
        EnableRuleRequest.checkRequest(request);
        LOGGER.info("rule enable or disable request detail: {}", request.toString());
        //Check existence of rules
        List<Long> ruleIds = request.getRuleEnableList().stream().map(EnableRequest::getRuleId).collect(Collectors.toList());
        List<Rule> rules = ruleDao.findByIds(ruleIds);
        List<Long> collect = rules.stream().map(Rule::getId).collect(Collectors.toList());
        List<Long> nonexistentRule = ruleIds.stream().filter(o -> !collect.contains(o)).collect(Collectors.toList());

        if (!nonexistentRule.isEmpty()) {
            throw new UnExpectedRequestException("rule_ids :" + nonexistentRule + " {&DOES_NOT_EXIST}");
        }
        //Check existence of projects
        List<Long> projectIds = rules.stream().map(rule -> rule.getProject().getId()).distinct().collect(Collectors.toList());
        List<Project> projectList = projectService.checkProjectsExistence(projectIds, loginUser);

        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        for (Project project : projectList) {
            projectService.checkProjectPermission(project, loginUser, permissions);
        }
        Map<Long, Boolean> ruleEnableMap = request.getRuleEnableList().stream().collect(Collectors.toMap(EnableRequest::getRuleId, EnableRequest::isRuleEnable, (oldVal, newVal) -> oldVal));
        for (Rule rule : rules) {
            setExecutionParametersInfo(rule, ruleEnableMap.get(rule), rule.getProject().getId());
            rule.setEnable(ruleEnableMap.get(rule.getId()));
        }
        List<Rule> rulesList = ruleDao.saveRules(rules);
        List<Long> ruleIdList = rulesList.stream().map(o -> o.getId()).collect(Collectors.toList());
        RuleEnableResponse ruleEnableResponse = new RuleEnableResponse();
        ruleEnableResponse.setRuleList(ruleIdList);

        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&SUCCESS_TO_ENABLE_RULE}", ruleEnableResponse);
    }

    @Override
    public void setExecutionParametersInfo(Rule rule, Boolean ruleEnable, Long id) {
        if (StringUtils.isNotBlank(rule.getExecutionParametersName())) {
            ExecutionParameters executionParameters = executionParametersDao.findByNameAndProjectId(rule.getExecutionParametersName(), id);
            if (executionParameters != null) {
                setBasicInfo(rule, executionParameters);
            } else {
                rule.setEnable(ruleEnable);
            }
        } else {
            rule.setEnable(ruleEnable);
        }
    }

    private void setBasicInfo(Rule rule, ExecutionParameters executionParameters) {
        rule.setExecutionParametersName(executionParameters.getName());
        rule.setAlert(executionParameters.getAlert());
        if (executionParameters.getAlert()) {
            rule.setAlertLevel(executionParameters.getAlertLevel());
            rule.setAlertReceiver(executionParameters.getAlertReceiver());
        }
        rule.setAbortOnFailure(executionParameters.getAbortOnFailure());
        rule.setStaticStartupParam(executionParameters.getStaticStartupParam());
        rule.setDeleteFailCheckResult(executionParameters.getDeleteFailCheckResult());
        rule.setSpecifyStaticStartupParam(executionParameters.getSpecifyStaticStartupParam());
        rule.setAbnormalCluster(StringUtils.isNotBlank(executionParameters.getCluster()) ? executionParameters.getCluster() : null);
        rule.setAbnormalDatabase(StringUtils.isNotBlank(executionParameters.getAbnormalDatabase()) ? executionParameters.getAbnormalDatabase() : null);
        rule.setAbnormalProxyUser(StringUtils.isNotBlank(executionParameters.getAbnormalProxyUser()) ? executionParameters.getAbnormalProxyUser() : null);
        rule.setUnionWay(executionParameters.getUnionWay());
    }
}
