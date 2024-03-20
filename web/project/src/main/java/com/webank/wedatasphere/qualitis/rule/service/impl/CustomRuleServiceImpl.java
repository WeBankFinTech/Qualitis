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

import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.TaskNotExistException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.project.constant.OperateTypeEnum;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.project.service.ProjectEventService;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.constant.RuleLockRangeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.*;
import com.webank.wedatasphere.qualitis.rule.entity.*;
import com.webank.wedatasphere.qualitis.rule.exception.RuleLockException;
import com.webank.wedatasphere.qualitis.rule.request.*;
import com.webank.wedatasphere.qualitis.rule.response.CustomRuleDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import com.webank.wedatasphere.qualitis.rule.service.*;
import com.webank.wedatasphere.qualitis.scheduled.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Service
public class CustomRuleServiceImpl extends AbstractRuleService implements CustomRuleService {

    @Autowired
    private RuleTemplateService ruleTemplateService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private RuleGroupDao ruleGroupDao;
    @Autowired
    private RuleDataSourceDao ruleDatasourceDao;
    @Autowired
    private BdpClientHistoryDao bdpClientHistoryDao;
    @Autowired
    private ExecutionParametersDao executionParametersDao;
    @Autowired
    private TemplateDataSourceTypeDao templateDataSourceTypeDao;
    @Autowired
    private RuleDataSourceService ruleDataSourceService;
    @Autowired
    private AlarmConfigService alarmConfigService;

    @Autowired
    private ProjectEventService projectEventService;

    @Autowired
    private RuleLockService ruleLockService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomRuleServiceImpl.class);

    private HttpServletRequest httpServletRequest;

    public CustomRuleServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class, SemanticException.class, ParseException.class})
    public GeneralResponse<RuleResponse> addCustomRule(AddCustomRuleRequest request)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        return addCustomRuleReal(request, loginUser);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> addRuleForOuter(AbstractCommonRequest request, String loginUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        return addCustomRuleReal((AddCustomRuleRequest) request, loginUser);
    }

    private GeneralResponse<RuleResponse> addCustomRuleReal(AddCustomRuleRequest request, String loginUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        // Check Arguments
        AddCustomRuleRequest.checkRequest(request);
        if (request.getRuleEnable() == null) {
            request.setRuleEnable(true);
        }
        if (request.getUnionAll() == null) {
            request.setUnionAll(false);
        }
        String nowDate = QualitisConstants.PRINT_TIME_FORMAT.format(new Date());

        // Generate Template, TemplateStatisticsInputMeta and save
        Template template = ruleTemplateService.addCustomTemplate(request, loginUser);
        // Check existence of project
        Project projectInDb = projectService.checkProjectExistence(request.getProjectId(), loginUser);
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);
        // Check unique of rule name
        ruleService.checkRuleName(request.getRuleName(), request.getWorkFlowName(), request.getWorkFlowVersion(), projectInDb, null);
        //check the same rule name number
        ruleService.checkRuleNameNumber(request.getRuleName(), projectInDb);
        // Check if cluster name is supported
        ruleDataSourceService.checkDataSourceClusterSupport(request.getClusterName());

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
        } else {
            if (StringUtils.isEmpty(ruleGroupName)) {
                ruleGroupName = "Group_" + UuidGenerator.generate();
            }
            ruleGroup = ruleGroupDao.saveRuleGroup(new RuleGroup(ruleGroupName, projectInDb.getId()));
        }

        Rule newRule = new Rule();
        // Set basic info.
        setBasicInfo(newRule, projectInDb, ruleGroup, template, loginUser, nowDate, request);
        // For context service.
        boolean cs = false;
        if (StringUtils.isNotBlank(request.getCsId())) {
            newRule.setCsId(request.getCsId());
            cs = true;
        }
        // For fps file check.
        boolean fps = false;
        if (StringUtils.isNotBlank(request.getFileId())) {
            fps = true;
        }
        // For fps file check.
        boolean sqlCheck = false;
        if (StringUtils.isNotBlank(request.getSqlCheckArea())) {
            sqlCheck = true;
        }

        handleRelatedObject(request, projectInDb, newRule);
        Rule savedRule = ruleDao.saveRule(newRule);

        LOGGER.info("Succeed to save custom rule, rule id: {}", savedRule.getId());

        super.recordEvent(loginUser, savedRule, OperateTypeEnum.CREATE_RULES);

        exctrationAlarmConfigAndRuleDatasource(loginUser, cs, fps, sqlCheck, savedRule, request.getAlarm(), request.getAlarmVariable(), request.getUploadRuleMetricValue()
                , request.getUploadAbnormalValue(), request.getDeleteFailCheckResult(), request.getClusterName(), request.getFileId(), request.getFileTableDesc(), request.getFileDb()
                , request.getFileTable(), request.getFileDelimiter(), request.getFileType(), request.getFileHeader(), request.getProxyUser(), request.getFileHashValues(), request.getLinkisDataSourceId()
                , request.getLinkisDataSourceVersionId(), request.getLinkisDataSourceName(), request.getLinkisDataSourceType(), request.getDataSourceEnvRequests(), request.getDataSourceEnvMappingRequests());
        RuleResponse response = new RuleResponse(savedRule);
        LOGGER.info("Succeed to add custom rule, rule id: {}", savedRule.getId());

        return new GeneralResponse<>("200", "{&SUCCEED_TO_ADD_CUSTOM_RULE}", response);
    }

    private void exctrationAlarmConfigAndRuleDatasource(String loginUser, boolean cs, boolean fps, boolean sqlCheck, Rule savedRule, Boolean alarm, List<CustomAlarmConfigRequest> alarmVariable
            , Boolean uploadRuleMetricValue, Boolean uploadAbnormalValue, Boolean deleteFailCheckResult, String clusterName, String fileId, String fileTableDesc, String fileDb, String fileTable, String fileDelimiter, String fileType
            , Boolean fileHeader, String proxyUser, String fileHashValues, Long linkisDataSourceId, Long linkisDataSourceVersionId, String linkisDataSourceName, String linkisDataSourceType, List<DataSourceEnvRequest> dataSourceEnvRequests, List<DataSourceEnvMappingRequest> dataSourceEnvMappingRequests) throws UnExpectedRequestException, IOException, PermissionDeniedRequestException {
        List<AlarmConfig> savedAlarmConfigs = new ArrayList<>();
        if (alarm) {
            alarmVariable.stream().map(alarmConfigRequest -> {
                alarmConfigRequest.setUploadAbnormalValue(uploadAbnormalValue != null ? uploadAbnormalValue : false);
                alarmConfigRequest.setUploadRuleMetricValue(uploadRuleMetricValue != null ? uploadRuleMetricValue : false);
                alarmConfigRequest.setDeleteFailCheckResult(deleteFailCheckResult != null ? deleteFailCheckResult : false);
                return alarmConfigRequest;
            }).collect(Collectors.toList());
            savedAlarmConfigs = alarmConfigService.checkAndSaveCustomAlarmVariable(alarmVariable, savedRule);
            LOGGER.info("Succeed to save alarm_configs, alarm_configs: {}", savedAlarmConfigs);
        }

        List<RuleDataSource> ruleDataSources = ruleDataSourceService.checkAndSaveCustomRuleDataSource(clusterName, fileId, fileTableDesc, fileDb, fileTable
                , fileDelimiter, fileType, fileHeader, proxyUser, fileHashValues, loginUser, savedRule, cs, fps, sqlCheck, linkisDataSourceId, linkisDataSourceVersionId
                , linkisDataSourceName, linkisDataSourceType, dataSourceEnvRequests, dataSourceEnvMappingRequests);
        savedRule.setAlarmConfigs(new HashSet<>(savedAlarmConfigs));
        if (CollectionUtils.isNotEmpty(ruleDataSources)) {
            savedRule.setRuleDataSources(new HashSet<>(ruleDataSources));
        }
    }

    private void handleRelatedObject(AddCustomRuleRequest request, Project projectInDb, Rule newRule) {
        if (StringUtils.isNotBlank(request.getExecutionParametersName())) {
            ExecutionParameters executionParameters = executionParametersDao.findByNameAndProjectId(request.getExecutionParametersName(), projectInDb.getId());
            if (executionParameters != null) {
                newRule.setExecutionParametersName(request.getExecutionParametersName());
                newRule.setUnionAll(executionParameters.getUnionAll());
                request.setUploadAbnormalValue(executionParameters.getUploadAbnormalValue());
                request.setUploadRuleMetricValue(executionParameters.getUploadRuleMetricValue());
                request.setDeleteFailCheckResult(executionParameters.getDeleteFailCheckResult());
            }
        } else {
            setAddCustomInfo(request, newRule);
        }
    }

    private void setAddCustomInfo(AddCustomRuleRequest request, Rule newRule) {
        newRule.setExecutionParametersName(null);
        newRule.setAlert(request.getAlert());
        if (request.getAlert() != null && request.getAlert()) {
            newRule.setAlertLevel(request.getAlertLevel());
            newRule.setAlertReceiver(request.getAlertReceiver());
        }
        newRule.setDeleteFailCheckResult(request.getDeleteFailCheckResult());
        newRule.setAbortOnFailure(request.getAbortOnFailure());
        // For startup parameters.
        newRule.setStaticStartupParam(request.getStaticStartupParam());
        newRule.setEnable(request.getRuleEnable());
        newRule.setUnionAll(request.getUnionAll());
        newRule.setSpecifyStaticStartupParam(request.getSpecifyStaticStartupParam());
        newRule.setAbnormalCluster(StringUtils.isNotBlank(request.getAbnormalCluster()) ? request.getAbnormalCluster() : null);
        newRule.setAbnormalDatabase(StringUtils.isNotBlank(request.getAbnormalDatabase()) ? request.getAbnormalDatabase() : null);
        newRule.setAbnormalProxyUser(StringUtils.isNotBlank(request.getAbnormalProxyUser()) ? request.getAbnormalProxyUser() : null);
    }

    private void setBasicInfo(Rule newRule, Project projectInDb, RuleGroup ruleGroup, Template template, String loginUser, String nowDate
            , AddCustomRuleRequest request) {
        newRule.setRuleType(RuleTypeEnum.CUSTOM_RULE.getCode());
        newRule.setTemplate(template);
        newRule.setName(request.getRuleName());
        newRule.setCnName(request.getRuleCnName());
        newRule.setDetail(request.getRuleDetail());
        newRule.setAlarm(request.getAlarm());
        newRule.setProject(projectInDb);
        newRule.setRuleTemplateName(template.getName());
        if (StringUtils.isBlank(request.getSqlCheckArea())) {
            newRule.setFunctionContent(request.getFunctionContent());
            newRule.setFunctionType(request.getFunctionType());
            newRule.setWhereContent(request.getWhereContent());
            newRule.setFromContent(request.getFromContent());
            newRule.setOutputName(request.getOutputName());
        }
        newRule.setCreateUser(loginUser);
        newRule.setRuleGroup(ruleGroup);
        newRule.setCreateTime(nowDate);

        newRule.setBashContent(request.getBashContent());
        newRule.setWorkFlowName(request.getWorkFlowName());
        newRule.setWorkFlowVersion(request.getWorkFlowVersion());
        newRule.setWorkFlowSpace(request.getWorkFlowSpace());
        newRule.setNodeName(request.getNodeName());
    }

    private void setBasicInfo(Rule ruleInDb, Template template, String loginUser, String nowDate, ModifyCustomRuleRequest request) {
        ruleInDb.setTemplate(template);
        ruleInDb.setName(request.getRuleName());
        ruleInDb.setCnName(request.getRuleCnName());
        ruleInDb.setDetail(request.getRuleDetail());
        ruleInDb.setOutputName(request.getOutputName());
        ruleInDb.setFromContent(request.getFromContent());
        ruleInDb.setFunctionType(request.getFunctionType());
        ruleInDb.setWhereContent(request.getWhereContent());
        ruleInDb.setFunctionContent(request.getFunctionContent());
        ruleInDb.setRuleTemplateName(template.getName());
        ruleInDb.setAlarm(request.getAlarm());
        ruleInDb.setModifyUser(loginUser);
        ruleInDb.setModifyTime(nowDate);
        ruleInDb.setBashContent(request.getBashContent());
        ruleInDb.setWorkFlowName(request.getWorkFlowName());
        ruleInDb.setWorkFlowVersion(request.getWorkFlowVersion());
        ruleInDb.setWorkFlowSpace(request.getWorkFlowSpace());
        ruleInDb.setNodeName(request.getNodeName());
    }

    /**
     * Delete custom rules, including template of custom rules
     *
     * @param request
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse deleteCustomRule(DeleteCustomRuleRequest request, String loginUser) throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check Arguments
        DeleteCustomRuleRequest.checkRequest(request);
        // Check existence of rule by ruleId
        Rule ruleInDb = ruleDao.findById(request.getRuleId());
        if (ruleInDb == null) {
            throw new UnExpectedRequestException("rule_id [" + request.getRuleId() + "] {&DOES_NOT_EXIST}");
        }
        // Check existence of project
        Project projectInDb = projectService.checkProjectExistence(ruleInDb.getProject().getId(),
                loginUser);
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);
        if (!ruleInDb.getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
            throw new UnExpectedRequestException("Rule id: [" + request.getRuleId() + "]) {&IS_NOT_A_CUSTOM_RULE}");
        }

        return deleteCustomRuleReal(ruleInDb);
    }

    @Override
    public GeneralResponse deleteCustomRuleReal(Rule rule) throws UnExpectedRequestException {
        // Delete bdp-client history
        BdpClientHistory bdpClientHistory = bdpClientHistoryDao.findByRuleId(rule.getId());
        if (bdpClientHistory != null) {
            bdpClientHistoryDao.delete(bdpClientHistory);
        }
        // Delete rule
        ruleDao.deleteRule(rule);
        // Delete template of custom rule
        ruleTemplateService.deleteCustomTemplate(rule.getTemplate());
        LOGGER.info("Succeed to delete custom rule, rule id: {}", rule.getId());

        super.recordEvent(HttpUtils.getUserName(httpServletRequest), rule, OperateTypeEnum.DELETE_RULES);

        return new GeneralResponse<>("200", "{&DELETE_CUSTOM_RULE_SUCCESSFULLY}", null);
    }

    @Override
    public GeneralResponse<CustomRuleDetailResponse> getCustomRuleDetail(Long ruleId) throws UnExpectedRequestException {
        // Check existence of rule
        Rule ruleInDb = ruleDao.findById(ruleId);
        if (ruleInDb == null) {
            throw new UnExpectedRequestException("rule_id [" + ruleId + "] {&DOES_NOT_EXIST}");
        }
        if (!ruleInDb.getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
            throw new UnExpectedRequestException("rule(id: [" + ruleId + "]) {&IS_NOT_A_CUSTOM_RULE}");
        }

        Long projectInDbId = ruleInDb.getProject().getId();
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        projectService.checkProjectExistence(projectInDbId, loginUser);

        LOGGER.info("Succeed to find rule, rule id: {}", ruleInDb.getId());

        CustomRuleDetailResponse response = new CustomRuleDetailResponse(ruleInDb);
        LOGGER.info("Succeed to get custom rule detail. response: {}", response);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_CUSTOM_RULE_DETAIL}", response);
    }

    /**
     * Modify custom rule
     * 1.Find custom rule
     * 2.Delete template of custom rule
     * 3.Delete alarm_config of custom rule
     * 4.Delete ruleDataSources of custom rule
     * 5.Save custom rule template
     * 6.Modify custom rule and save
     * 7.Save rule alarm config and rule datasources
     * 8.Return result
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class, SemanticException.class, ParseException.class})
    public GeneralResponse<RuleResponse> modifyCustomRule(ModifyCustomRuleRequest request)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        return modifyCustomRuleReal(request, loginUser);
    }

    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class, SemanticException.class, ParseException.class})
    @Override
    public GeneralResponse<RuleResponse> modifyCustomRuleWithLock(ModifyCustomRuleRequest request) throws UnExpectedRequestException, ClusterInfoNotConfigException, TaskNotExistException, MetaDataAcquireFailedException, PermissionDeniedRequestException, IOException, RuleLockException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        if (!ruleLockService.tryAcquire(request.getRuleId(), RuleLockRangeEnum.RULE, loginUser)) {
            LOGGER.warn("Failed to acquire lock");
            throw new RuleLockException("{&RULE_LOCK_ACQUIRE_FAILED}");
        }
        try {
            return modifyCustomRuleReal(request, loginUser);
        } finally {
            ruleLockService.release(request.getRuleId(), RuleLockRangeEnum.RULE, loginUser);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class, SemanticException.class, ParseException.class})
    public GeneralResponse<RuleResponse> modifyRuleDetailForOuter(ModifyCustomRuleRequest modifyRuleRequest, String userName)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        return modifyCustomRuleReal(modifyRuleRequest, userName);
    }

    private GeneralResponse<RuleResponse> modifyCustomRuleReal(ModifyCustomRuleRequest request, String loginUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        ModifyCustomRuleRequest.checkRequest(request);
        if (request.getRuleEnable() == null) {
            request.setRuleEnable(true);
        }
        if (request.getUnionAll() == null) {
            request.setUnionAll(false);
        }
        Rule ruleInDb = ruleDao.findById(request.getRuleId());
        if (ruleInDb == null) {
            throw new UnExpectedRequestException("rule_id [" + request.getRuleId() + "] {&DOES_NOT_EXIST}");
        }

        Project projectInDb = checkProject(request, loginUser, ruleInDb);
        // Check existence of project rule name
        ruleService.checkRuleName(request.getRuleName(), request.getWorkFlowName(), request.getWorkFlowVersion(), ruleInDb.getProject(), ruleInDb.getId());
        //check the same rule name number
        ruleService.checkRuleNameNumber(request.getRuleName(), projectInDb);
        String nowDate = QualitisConstants.PRINT_TIME_FORMAT.format(new Date());

        // Delete alarm config by custom rule
        alarmConfigService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all alarm_config, rule id: {}", ruleInDb.getId());

        // Delete rule datasource of custom rule
        ruleDataSourceService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all rule_dataSources, rule id: {}", ruleInDb.getId());

        // Save template, alarm config, rule datasource of custom rule
        AddCustomRuleRequest addCustomRuleRequest = new AddCustomRuleRequest();

        BeanUtils.copyProperties(request, addCustomRuleRequest);
        Template template = ruleTemplateService.modifyCustomTemplate(addCustomRuleRequest, ruleInDb.getTemplate(), loginUser);

        // Modify custom rule and save
        setBasicInfo(ruleInDb, template, loginUser, nowDate, request);
        String ruleGroupName = request.getRuleGroupName();
        if (StringUtils.isNotEmpty(ruleGroupName)) {
            ruleInDb.getRuleGroup().setRuleGroupName(ruleGroupName);
            ruleGroupDao.saveRuleGroup(ruleInDb.getRuleGroup());
        }
        ruleInDb.setCsId(request.getCsId());
        boolean cs = false;
        if (StringUtils.isNotBlank(request.getCsId())) {
            cs = true;
        }
        boolean fps = false;
        if (StringUtils.isNotBlank(request.getFileId())) {
            fps = true;
        }
        boolean sqlCheck = false;
        if (StringUtils.isNotBlank(request.getSqlCheckArea())) {
            sqlCheck = true;
        }
        handleExecutionParametersInfo(request, ruleInDb, projectInDb);
        Rule savedRule = ruleDao.saveRule(ruleInDb);

        super.recordEvent(loginUser, savedRule, OperateTypeEnum.MODIFY_RULES);

        // Save alarm config and rule datasource
        exctrationAlarmConfigAndRuleDatasource(loginUser, cs, fps, sqlCheck, savedRule, request.getAlarm(), request.getAlarmVariable(), request.getUploadRuleMetricValue(), request.getUploadAbnormalValue(), request.getDeleteFailCheckResult(), request.getClusterName(), request.getFileId(), request.getFileTableDesc(), request.getFileDb(), request.getFileTable(), request.getFileDelimiter(), request.getFileType(), request.getFileHeader(), request.getProxyUser(), request.getFileHashValues(), request.getLinkisDataSourceId(), request.getLinkisDataSourceVersionId(), request.getLinkisDataSourceName(), request.getLinkisDataSourceType(),
                request.getDataSourceEnvRequests(), request.getDataSourceEnvMappingRequests());
        RuleResponse response = new RuleResponse(savedRule);
        LOGGER.info("Succeed to modify custom rule, rule id: {}", savedRule.getId());
        return new GeneralResponse<>("200", "{&SUCCEED_TO_MODIFY_CUSTOM_RULE}", response);
    }

    private void handleExecutionParametersInfo(ModifyCustomRuleRequest request, Rule ruleInDb, Project projectInDb) {
        if (StringUtils.isNotBlank(request.getExecutionParametersName())) {
            ExecutionParameters executionParameters = executionParametersDao.findByNameAndProjectId(request.getExecutionParametersName(), projectInDb.getId());
            if (executionParameters != null) {
                ruleInDb.setExecutionParametersName(request.getExecutionParametersName());
                ruleInDb.setUnionAll(executionParameters.getUnionAll());
                request.setUploadAbnormalValue(executionParameters.getUploadAbnormalValue());
                request.setUploadRuleMetricValue(executionParameters.getUploadRuleMetricValue());
                request.setDeleteFailCheckResult(executionParameters.getDeleteFailCheckResult());
            }
        } else {
            setUpdateCustomInfo(request, ruleInDb);
        }
    }

    private void setUpdateCustomInfo(ModifyCustomRuleRequest request, Rule ruleInDb) {
        ruleInDb.setExecutionParametersName(null);
        ruleInDb.setAlert(request.getAlert());
        if (request.getAlert() != null && request.getAlert()) {
            ruleInDb.setAlertLevel(request.getAlertLevel());
            ruleInDb.setAlertReceiver(request.getAlertReceiver());
        }
        ruleInDb.setDeleteFailCheckResult(request.getDeleteFailCheckResult());
        ruleInDb.setAbortOnFailure(request.getAbortOnFailure());
        ruleInDb.setStaticStartupParam(request.getStaticStartupParam());
        ruleInDb.setEnable(request.getRuleEnable());
        ruleInDb.setUnionAll(request.getUnionAll());
        ruleInDb.setSpecifyStaticStartupParam(request.getSpecifyStaticStartupParam());
        ruleInDb.setAbnormalCluster(StringUtils.isNotBlank(request.getAbnormalCluster()) ? request.getAbnormalCluster() : null);
        ruleInDb.setAbnormalDatabase(StringUtils.isNotBlank(request.getAbnormalDatabase()) ? request.getAbnormalDatabase() : null);
        ruleInDb.setAbnormalProxyUser(StringUtils.isNotBlank(request.getAbnormalProxyUser()) ? request.getAbnormalProxyUser() : null);
    }

    private Boolean handleObjectEqual(AddCustomRuleRequest request, ExecutionParameters executionParameters) {
        return CommonChecker.compareIdentical(request.getUnionAll(), request.getAbortOnFailure(), request.getSpecifyStaticStartupParam(), request.getStaticStartupParam()
                , request.getAbnormalDatabase(), request.getAbnormalCluster(), request.getAlert(), request.getAlertLevel(), request.getAlertReceiver(), request.getAbnormalProxyUser(), request.getDeleteFailCheckResult(), request.getUploadRuleMetricValue(), request.getUploadAbnormalValue(), executionParameters);
    }

    private Boolean handleObjectEqual(ModifyCustomRuleRequest request, ExecutionParameters executionParameters) {
        return CommonChecker.compareIdentical(request.getUnionAll(), request.getAbortOnFailure(), request.getSpecifyStaticStartupParam(), request.getStaticStartupParam()
                , request.getAbnormalDatabase(), request.getAbnormalCluster(), request.getAlert(), request.getAlertLevel(), request.getAlertReceiver(), request.getAbnormalProxyUser(), request.getDeleteFailCheckResult(), request.getUploadRuleMetricValue(), request.getUploadAbnormalValue(), executionParameters);
    }

    private Project checkProject(ModifyCustomRuleRequest request, String loginUser, Rule ruleInDb) throws UnExpectedRequestException, PermissionDeniedRequestException {
        Project projectInDb = projectService.checkProjectExistence(ruleInDb.getProject().getId(),
                loginUser);
        if (!ruleInDb.getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
            throw new UnExpectedRequestException("Rule id: [" + request.getRuleId() + "]) {&IS_NOT_A_CUSTOM_RULE}");
        }
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);
        LOGGER.info("Succeed to find custom rule, rule id: {}", ruleInDb.getId());
        return projectInDb;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class, SemanticException.class, ParseException.class})
    public GeneralResponse<RuleResponse> addCustomRuleForUpload(AddCustomRuleRequest request)
            throws UnExpectedRequestException, MetaDataAcquireFailedException, PermissionDeniedRequestException, IOException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        return addCustomRuleReal(request, loginUser);
    }

}
