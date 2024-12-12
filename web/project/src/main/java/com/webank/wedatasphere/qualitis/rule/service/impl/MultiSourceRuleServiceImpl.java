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

import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.project.service.ProjectEventService;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.adapter.AutoArgumentAdapter;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTemplateTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.entity.*;
import com.webank.wedatasphere.qualitis.rule.request.AbstractAddRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.*;
import com.webank.wedatasphere.qualitis.rule.response.MultiRuleDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import com.webank.wedatasphere.qualitis.rule.service.*;
import com.webank.wedatasphere.qualitis.rule.service.RuleService;
import com.webank.wedatasphere.qualitis.rule.service.RuleVariableService;
import com.webank.wedatasphere.qualitis.submitter.impl.ExecutionManagerImpl;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author howeye
 */
@Service
public class MultiSourceRuleServiceImpl implements MultiSourceRuleService {

    @Autowired
    private RuleService ruleService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private AlarmConfigService alarmConfigService;
    @Autowired
    private RuleVariableService ruleVariableService;
    @Autowired
    private ProjectEventService projectEventService;
    @Autowired
    private RuleTemplateService ruleTemplateService;
    @Autowired
    private RuleDataSourceService ruleDataSourceService;
    @Autowired
    private RuleDataSourceMappingService ruleDataSourceMappingService;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private RuleGroupDao ruleGroupDao;
    @Autowired
    private ClusterInfoDao clusterInfoDao;

    @Autowired
    private AutoArgumentAdapter autoArgumentAdapter;

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiSourceRuleServiceImpl.class);
    private static final String MID_TMP_STR = "@#$%^&";

    private HttpServletRequest httpServletRequest;
    public MultiSourceRuleServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class, SQLException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> addMultiSourceRule(AddMultiSourceRuleRequest request, boolean check)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        return addMultiSourceRuleReal(request, check);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> addRuleForOuter(AbstractAddRequest request, boolean check)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        return addMultiSourceRuleReal((AddMultiSourceRuleRequest) request, check);
    }

    private GeneralResponse<RuleResponse> addMultiSourceRuleReal(AddMultiSourceRuleRequest request, boolean check)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check Arguments
        CommonChecker.checkObject(request, "Request");
        String csId = request.getCsId();
        boolean cs = false;
        if (StringUtils.isNotBlank(csId))  {
            cs = true;
        }
        AddMultiSourceRuleRequest.checkRequest(request, false, cs);
        String loginUser = "";
        if (request.getLoginUser() != null) {
            loginUser = request.getLoginUser();
            LOGGER.info("Recover user[{}] from is adding rule.", loginUser);
        } else {
            loginUser = HttpUtils.getUserName(httpServletRequest);
        }
        LOGGER.info("Check permission user[{}] who is adding rule.", loginUser);

        // Check existence of project
        Project projectInDb = projectService.checkProjectExistence(request.getProjectId(),
            loginUser);
        // Check permissions of project
        if (check) {
            List<Integer> permissions = new ArrayList<>();
            permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
            projectService.checkProjectPermission(projectInDb, loginUser, permissions);
        }

        // Check existence of rule name
        ruleService.checkRuleName(request.getRuleName(), projectInDb, null);
        // Check existence of cluster
        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(request.getClusterName());
        if (clusterInfo == null) {
            throw new UnExpectedRequestException("Cluster :" + request.getClusterName() + " {&DOES_NOT_EXIST}");
        }
        // Check existence of id and check if multi-table rule
        Template templateInDb = ruleTemplateService.checkRuleTemplate(request.getMultiSourceRuleTemplateId());
        if (! templateInDb.getTemplateType().equals(RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode())) {
            throw new UnExpectedRequestException("Template id :" + request.getMultiSourceRuleTemplateId() + " {&IS_NOT_A_MULTI_SOURCE_TEMPLATE}");
        }

        RuleGroup ruleGroup;
        if (request.getRuleGroupId() != null) {
            ruleGroup = ruleGroupDao.findById(request.getRuleGroupId());
            if (ruleGroup == null) {
                throw new UnExpectedRequestException(String.format("Rule Group: %s {&CAN_NOT_BE_NULL_OR_EMPTY}", request.getRuleGroupId()));
            }
        } else {
            ruleGroup = ruleGroupDao.saveRuleGroup(
                new RuleGroup("Group_" + UUID.randomUUID().toString().replace("-", ""), projectInDb.getId()));
        }
        String leftUuid = UuidGenerator.generate();
        String rightUuid = UuidGenerator.generate();
        Rule savedRule = generateRule(request, projectInDb, false, null, false, cs, leftUuid, rightUuid, loginUser);
        savedRule.setRuleGroup(ruleGroup);
        savedRule.setSpecifyStaticStartupParam(request.getSpecifyStaticStartupParam());
        if (request.getSpecifyStaticStartupParam() != null && request.getSpecifyStaticStartupParam()) {
            savedRule.setStaticStartupParam(request.getStaticStartupParam());
        }
        savedRule = ruleDao.saveRule(savedRule);
        if (templateInDb.getChildTemplate() != null) {
            // Generate child rule
            AddMultiSourceRuleRequest addMultiSourceRuleRequest = generateChildRequest(request, templateInDb.getChildTemplate());
            Rule childRule = generateRule(addMultiSourceRuleRequest, null, false, null, true, cs, leftUuid, rightUuid, loginUser);
            childRule.setParentRule(savedRule);
            ruleDao.saveRule(childRule);
        }

        RuleResponse response = new RuleResponse(savedRule);
        LOGGER.info("Succeed to add multi source rule, rule_id: {}", savedRule.getId());
        return new GeneralResponse<>("200", "{&ADD_MULTI_RULE_SUCCESSFULLY}", response);
    }

    @Override
    @Transactional(rollbackFor = {UnExpectedRequestException.class, Exception.class})
    public GeneralResponse<?> deleteMultiSourceRule(DeleteMultiSourceRequest request, String loginUser)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check Arguments
        DeleteMultiSourceRequest.checkRequest(request);
        // Check existence of rule and check if multi-table rule
        Rule ruleInDb = ruleDao.findById(request.getMultiRuleId());
        if (ruleInDb == null) {
            throw new UnExpectedRequestException("rule_id [" + request.getMultiRuleId() + "] {&DOES_NOT_EXIST}");
        }
        // Check existence of project
        Project projectInDb = projectService.checkProjectExistence(ruleInDb.getProject().getId(),
            loginUser);
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);
        if (!ruleInDb.getRuleType().equals(RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode())) {
            throw new UnExpectedRequestException("rule_id: [" + request.getMultiRuleId() + "]) {&IS_NOT_A_MULTI_TEMPLATE_RULE}");
        }
        // Update rule count of datasource
        ruleDataSourceService.updateRuleDataSourceCount(ruleInDb, -1);
        // Record project event.
//        projectEventService.record(projectInDb.getId(), loginUser, "delete", "multi source rule[name= " + ruleInDb.getName() + "].", EventTypeEnum.MODIFY_PROJECT.getCode());
        // Delete rule
        return deleteMultiRuleReal(ruleInDb);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public GeneralResponse<?> deleteMultiRuleReal(Rule rule) {
        // Delete rule
        ruleDao.deleteRule(rule);
        LOGGER.info("Succeed to delete multi rule. rule_id: {}", rule.getId());
        return new GeneralResponse<>("200", "{&DELETE_MULTI_RULE_SUCCESSFULLY}", null);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> modifyMultiSourceRule(ModifyMultiSourceRequest request)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        return modifyRuleDetailReal(request, loginUser);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, SQLException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> modifyRuleDetailForOuter(ModifyMultiSourceRequest modifyRuleRequest, String userName)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        return modifyRuleDetailReal(modifyRuleRequest, userName);
    }

    private GeneralResponse<RuleResponse> modifyRuleDetailReal(ModifyMultiSourceRequest request, String loginUser)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check Arguments
        CommonChecker.checkObject(request, "request");
        String csId = request.getCsId();
        boolean cs = false;
        if (StringUtils.isNotBlank(csId))  {
            cs = true;
        }
        ModifyMultiSourceRequest.checkRequest(request, cs);

        // Check existence of rule
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
        if (!ruleInDb.getRuleType().equals(RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode())) {
            throw new UnExpectedRequestException("rule_id: [" + request.getRuleId() + "]) {&IS_NOT_A_MULTI_TEMPLATE_RULE}");
        }
        LOGGER.info("Succeed to find multi rule. rule_id: {}", ruleInDb.getId());

        // Check existence of project name
        ruleService.checkRuleName(request.getRuleName(), ruleInDb.getProject(), ruleInDb.getId());
        // Check if cluster name supported
        ruleDataSourceService.checkDataSourceClusterSupport(request.getClusterName());
        // Check existence of rule id and if multi-table rule
        Template templateInDb = ruleTemplateService.checkRuleTemplate(request.getMultiSourceRuleTemplateId());
        if (!templateInDb.getTemplateType().equals(RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode())) {
            throw new UnExpectedRequestException("Template id :" + request.getMultiSourceRuleTemplateId() + " {&IS_NOT_A_MULTI_SOURCE_TEMPLATE}");
        }

        // Delete rule config by rule
        alarmConfigService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all alarm_config. rule_id: {}", ruleInDb.getId());

        // Delete rule datasource by rule
        ruleDataSourceService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all rule_dataSources. rule_id: {}", ruleInDb.getId());
        // Update rule count of datasource
        ruleDataSourceService.updateRuleDataSourceCount(ruleInDb, -1);
        // Delete rule datasource mapping by rule
        ruleDataSourceMappingService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all rule_dataSource_mapping. rule_id: {}", ruleInDb.getId());

        // Delete rule variable by rule
        ruleVariableService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all rule_variable. rule_id: {}", ruleInDb.getId());
        // Delete child rule
        if (ruleInDb.getChildRule() != null) {
            ruleDao.deleteRule(ruleInDb.getChildRule());
            LOGGER.info("Succeed to delete all child rule. rule_id: {}", ruleInDb.getId());
        }

        AddMultiSourceRuleRequest addMultiSourceRuleRequest = new AddMultiSourceRuleRequest();
        BeanUtils.copyProperties(request, addMultiSourceRuleRequest);
        String leftUuid = UuidGenerator.generate();
        String rightUuid = UuidGenerator.generate();
        Rule savedRule = generateRule(addMultiSourceRuleRequest, projectInDb, true, ruleInDb, false, cs, leftUuid, rightUuid, loginUser);
        if (templateInDb.getChildTemplate() != null) {
            // Generate child rule
            AddMultiSourceRuleRequest childRequest = generateChildRequest(addMultiSourceRuleRequest, templateInDb.getChildTemplate());
            Rule childRule = generateRule(childRequest, null, false, null, true, cs, leftUuid, rightUuid, loginUser);
            childRule.setParentRule(savedRule);
            ruleDao.saveRule(childRule);
            LOGGER.info("Succeed to generate child rule");
        }

        RuleResponse response = new RuleResponse(savedRule);
        LOGGER.info("Succeed to modify multi source rule, rule_id: {}", savedRule.getId());
        // Record project event.
//        projectEventService.record(savedRule.getProject().getId(), loginUser, "modify", "multi source rule[name= " + savedRule.getName() + "].", EventTypeEnum.MODIFY_PROJECT.getCode());
        return new GeneralResponse<>("200", "{&MODIFY_MULTI_RULE_SUCCESSFULLY}", response);
    }

    @Override
    public GeneralResponse<MultiRuleDetailResponse> getMultiSourceRule(Long ruleId) throws UnExpectedRequestException {
        // Check existence of rule
        Rule ruleInDb = ruleDao.findById(ruleId);
        if (ruleInDb == null) {
            throw new UnExpectedRequestException("rule_id [" + ruleId + "] {&DOES_NOT_EXIST}");
        }
        if (!ruleInDb.getRuleType().equals(RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode())) {
            throw new UnExpectedRequestException("rule_id: [" + ruleId + "]) {&IS_NOT_A_MULTI_TEMPLATE_RULE}");
        }
        Long projectInDbId = ruleInDb.getProject().getId();
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        projectService.checkProjectExistence(projectInDbId, loginUser);
        MultiRuleDetailResponse multiRuleDetailResponse = new MultiRuleDetailResponse(ruleInDb);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_DETAIL_OF_MULTI_RULE}", multiRuleDetailResponse);
    }

    private AddMultiSourceRuleRequest generateChildRequest(AddMultiSourceRuleRequest request, Template childTemplate) {
        AddMultiSourceRuleRequest reverseRequest = new AddMultiSourceRuleRequest();
        BeanUtils.copyProperties(request, reverseRequest);
        reverseRequest.setSource(request.getTarget());
        reverseRequest.setTarget(request.getSource());
        reverseRequest.setRuleName(request.getRuleName() + "_child_rule");
        reverseRequest.setMultiSourceRuleTemplateId(childTemplate.getId());
        reverseRequest.setMappings(getReverseMapping(request.getMappings()));
        reverseRequest.setAbortOnFailure(request.getAbortOnFailure());
        return reverseRequest;
    }

    private List<MultiDataSourceJoinConfigRequest> adjustMapping(List<MultiDataSourceJoinConfigRequest> mappings) {
        List<MultiDataSourceJoinConfigRequest> result = new ArrayList<>();
        for (MultiDataSourceJoinConfigRequest mapping : mappings) {
            MultiDataSourceJoinConfigRequest tmp = new MultiDataSourceJoinConfigRequest();
            List<MultiDataSourceJoinColumnRequest> left = new ArrayList<>();
            List<MultiDataSourceJoinColumnRequest> right = new ArrayList<>();
            for (MultiDataSourceJoinColumnRequest columnRequest : mapping.getLeft()) {
                if (columnRequest.getColumnName().startsWith("tmp1.")) {
                    if (!left.contains(columnRequest)) {
                        left.add(columnRequest);
                    }
                } else {
                    if (!right.contains(columnRequest)) {
                        right.add(columnRequest);
                    }
                }
            }
            for (MultiDataSourceJoinColumnRequest columnRequest : mapping.getRight()) {
                if (columnRequest.getColumnName().startsWith("tmp1.")) {
                    if (!left.contains(columnRequest)) {
                        left.add(columnRequest);
                    }
                } else {
                    if (!right.contains(columnRequest)) {
                        right.add(columnRequest);
                    }
                }
            }

            tmp.setLeft(left);
            tmp.setRight(right);
            tmp.setLeftStatement(mapping.getLeftStatement());
            tmp.setRightStatement(mapping.getRightStatement());
            tmp.setOperation(mapping.getOperation());

            result.add(tmp);
        }
        return result;
    }

    private List<MultiDataSourceJoinConfigRequest> getReverseMapping(List<MultiDataSourceJoinConfigRequest> sourceMappings) {
        List<MultiDataSourceJoinConfigRequest> reverseMappings = new ArrayList<>();
        for (MultiDataSourceJoinConfigRequest sourceMapping : sourceMappings) {
            MultiDataSourceJoinConfigRequest reverseMapping = new MultiDataSourceJoinConfigRequest();
            String rightStatement = reverseTmp1AndTmp2(sourceMapping.getRightStatement());
            String leftStatement = reverseTmp1AndTmp2(sourceMapping.getLeftStatement());
            List<MultiDataSourceJoinColumnRequest> right = new ArrayList<>();
            List<MultiDataSourceJoinColumnRequest> left = new ArrayList<>();
            for (MultiDataSourceJoinColumnRequest r : sourceMapping.getRight()) {
                MultiDataSourceJoinColumnRequest tmp = new MultiDataSourceJoinColumnRequest();
                tmp.setColumnName(reverseTmp1AndTmp2(r.getColumnName()));
                right.add(tmp);
            }
            for (MultiDataSourceJoinColumnRequest l : sourceMapping.getLeft()) {
                MultiDataSourceJoinColumnRequest tmp = new MultiDataSourceJoinColumnRequest();
                tmp.setColumnName(reverseTmp1AndTmp2(l.getColumnName()));
                left.add(tmp);
            }

            reverseMapping.setOperation(sourceMapping.getOperation());
            reverseMapping.setLeftStatement(leftStatement);
            reverseMapping.setRightStatement(rightStatement);
            reverseMapping.setRight(left);
            reverseMapping.setLeft(right);
            reverseMappings.add(reverseMapping);
        }
        return reverseMappings;
    }

    private String reverseTmp1AndTmp2(String str) {
        String tmp1 = str.replace("tmp1", MID_TMP_STR);
        String tmp2 = tmp1.replace("tmp2", "tmp1");
        return tmp2.replace(MID_TMP_STR, "tmp2");
    }

    private Rule generateRule(AddMultiSourceRuleRequest request, Project projectInDb, Boolean modify, Rule ruleInDb, Boolean isChild, Boolean cs,
        String leftUuid, String rightUuid, String loginUser)
        throws UnExpectedRequestException {
        // Check existence of rule and if multi-table rule
        Template templateInDb = ruleTemplateService.checkRuleTemplate(request.getMultiSourceRuleTemplateId());
        if (!templateInDb.getTemplateType().equals(RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode())) {
            throw new UnExpectedRequestException("Template id :" + request.getMultiSourceRuleTemplateId() + " {&IS_NOT_A_MULTI_SOURCE_TEMPLATE}");
        }
        String nowDate = ExecutionManagerImpl.PRINT_TIME_FORMAT.format(new Date());
        Rule savedRule;
        if (modify) {
            // Rule basic info.
            savedRule = setBasicInfo(ruleInDb, templateInDb, request, loginUser, nowDate);
        } else {
            // Generate rule and save
            Rule newRule = new Rule();
            newRule.setTemplate(templateInDb);
            newRule.setRuleTemplateName(templateInDb.getName());
            newRule.setDetail(request.getRuleDetail());
            newRule.setCnName(request.getRuleCnName());
            newRule.setName(request.getRuleName());
            newRule.setAlarm(request.getAlarm());
            newRule.setProject(projectInDb);
            newRule.setRuleType(RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode());
            newRule.setCsId(request.getCsId());
            newRule.setAbortOnFailure(request.getAbortOnFailure());
            newRule.setCreateUser(loginUser);
            newRule.setCreateTime(nowDate);
            newRule.setDeleteFailCheckResult(request.getDeleteFailCheckResult());
            newRule.setSpecifyStaticStartupParam(request.getSpecifyStaticStartupParam());
            newRule.setStaticStartupParam(request.getStaticStartupParam());
            savedRule = ruleDao.saveRule(newRule);
        }
        LOGGER.info("Succeed to save rule, rule_id: {}", savedRule.getId());
        MultiDataSourceConfigRequest left = request.getSource();
        MultiDataSourceConfigRequest right = request.getTarget();
        if (StringUtils.isNotBlank(left.getFileId()) && !isChild) {
            left.setTableName(left.getTableName().concat("_").concat(leftUuid));
        }
        if (StringUtils.isNotBlank(right.getFileId()) && !isChild) {
            right.setTableName(right.getTableName().concat("_").concat(rightUuid));
        }
        // Generate rule_datasource, rule_variable, rule_alarm_config
        LOGGER.info("Start to generate and save rule_variable.");
        List<RuleVariable> ruleVariables = autoAdaptRequestAndGetRuleVariable(savedRule, templateInDb, request.getClusterName(),
                left, right, request.getMappings(), request.getFilter());
        List<RuleVariable> savedRuleVariables = ruleVariableService.saveRuleVariable(ruleVariables);
        LOGGER.info("Succeed to save rule_variables, rule_variables: {}", savedRuleVariables);
        List<AlarmConfig> savedAlarmConfigs = new ArrayList<>();
        if (request.getAlarm()) {
            savedAlarmConfigs  = alarmConfigService.checkAndSaveAlarmVariable(request.getAlarmVariable(), savedRule);
            LOGGER.info("Succeed to save alarm_configs, alarm_configs: {}", savedAlarmConfigs);
        }
        if (! isChild) {
            List<DataSourceRequest> dataSourceRequests = generateDataSourceRequest(request.getClusterName(), left, right);
            List<RuleDataSource> savedRuleDataSource = ruleDataSourceService.checkAndSaveRuleDataSource(dataSourceRequests, savedRule, cs, loginUser);
            LOGGER.info("Succeed to save rule_dataSources, rule_dataSources: {}", savedRuleDataSource);
            savedRule.setRuleDataSources(new HashSet<>(savedRuleDataSource));
            // Update rule count of datasource
            ruleDataSourceService.updateRuleDataSourceCount(savedRule, 1);
        }

        List<RuleDataSourceMapping> savedRuleDataSourceMappings = ruleDataSourceMappingService.checkAndSaveRuleDataSourceMapping(request.getMappings(), savedRule);
        LOGGER.info("Succeed to save rule_dataSource_mappings, rule_dataSource_mappings: {}", savedRuleDataSourceMappings);

        savedRule.setRuleVariables(new HashSet<>(savedRuleVariables));
        savedRule.setAlarmConfigs(new HashSet<>(savedAlarmConfigs));
        savedRule.setRuleDataSourceMappings(new HashSet<>(savedRuleDataSourceMappings));

        return savedRule;
    }

    private Rule setBasicInfo(Rule ruleInDb, Template templateInDb, AddMultiSourceRuleRequest request, String loginUser, String nowDate) {
        ruleInDb.setTemplate(templateInDb);
        ruleInDb.setRuleTemplateName(templateInDb.getName());
        ruleInDb.setDetail(request.getRuleDetail());
        ruleInDb.setCnName(request.getRuleCnName());
        ruleInDb.setName(request.getRuleName());
        ruleInDb.setAlarm(request.getAlarm());
        ruleInDb.setCsId(request.getCsId());
        ruleInDb.setAbortOnFailure(request.getAbortOnFailure());
        ruleInDb.setModifyUser(loginUser);
        ruleInDb.setModifyTime(nowDate);
        ruleInDb.setDeleteFailCheckResult(request.getDeleteFailCheckResult());
        ruleInDb.setSpecifyStaticStartupParam(request.getSpecifyStaticStartupParam());
        ruleInDb.setStaticStartupParam(request.getStaticStartupParam());
        return ruleDao.saveRule(ruleInDb);
    }

    private List<DataSourceRequest> generateDataSourceRequest(String clusterName, MultiDataSourceConfigRequest sourceConfig, MultiDataSourceConfigRequest targetConfig) {
        List<DataSourceRequest> dataSourceRequests = new ArrayList<>();
        DataSourceRequest sourceDataSourceRequest = new DataSourceRequest();
        sourceDataSourceRequest.setClusterName(clusterName);
        sourceDataSourceRequest.setFilter(sourceConfig.getFilter());
        sourceDataSourceRequest.setTableName(sourceConfig.getTableName());
        sourceDataSourceRequest.setDbName(sourceConfig.getDbName());
        sourceDataSourceRequest.setColNames(new ArrayList<>());
        sourceDataSourceRequest.setDatasourceIndex(0);
        sourceDataSourceRequest.setFileId(sourceConfig.getFileId());
        sourceDataSourceRequest.setFileTablesDesc(sourceConfig.getFileTableDesc());
        sourceDataSourceRequest.setFileDelimiter(sourceConfig.getFileDelimiter());
        sourceDataSourceRequest.setFileType(sourceConfig.getFileType());
        sourceDataSourceRequest.setFileHeader(sourceConfig.getFileHeader());
        sourceDataSourceRequest.setProxyUser(sourceConfig.getProxyUser());
        sourceDataSourceRequest.setFileHashValues(sourceConfig.getFileHashValues());
        sourceDataSourceRequest.setLinkisDataSourceId(sourceConfig.getLinkisDataSourceId());
        sourceDataSourceRequest.setLinkisDataSourceName(sourceConfig.getLinkisDataSourceName());
        sourceDataSourceRequest.setLinkisDataSourceType(sourceConfig.getLinkisDataSourceType());
        sourceDataSourceRequest.setLinkisDataSourceVersionId(sourceConfig.getLinkisDataSourceVersionId());


        DataSourceRequest targetDataSourceRequest = new DataSourceRequest();
        targetDataSourceRequest.setClusterName(clusterName);
        targetDataSourceRequest.setFilter(targetConfig.getFilter());
        targetDataSourceRequest.setTableName(targetConfig.getTableName());
        targetDataSourceRequest.setDbName(targetConfig.getDbName());
        targetDataSourceRequest.setColNames(new ArrayList<>());
        targetDataSourceRequest.setDatasourceIndex(1);
        targetDataSourceRequest.setFileId(targetConfig.getFileId());
        targetDataSourceRequest.setFileTablesDesc(targetConfig.getFileTableDesc());
        targetDataSourceRequest.setFileDelimiter(targetConfig.getFileDelimiter());
        targetDataSourceRequest.setFileType(targetConfig.getFileType());
        targetDataSourceRequest.setFileHeader(targetConfig.getFileHeader());
        targetDataSourceRequest.setProxyUser(targetConfig.getProxyUser());
        targetDataSourceRequest.setFileHashValues(targetConfig.getFileHashValues());
        targetDataSourceRequest.setLinkisDataSourceId(targetConfig.getLinkisDataSourceId());
        targetDataSourceRequest.setLinkisDataSourceName(targetConfig.getLinkisDataSourceName());
        targetDataSourceRequest.setLinkisDataSourceType(targetConfig.getLinkisDataSourceType());
        targetDataSourceRequest.setLinkisDataSourceVersionId(targetConfig.getLinkisDataSourceVersionId());

        dataSourceRequests.add(sourceDataSourceRequest);
        dataSourceRequests.add(targetDataSourceRequest);
        return dataSourceRequests;
    }

    private List<RuleVariable> autoAdaptRequestAndGetRuleVariable(Rule rule, Template template, String clusterName, MultiDataSourceConfigRequest firstDataSource,
                                                                  MultiDataSourceConfigRequest secondDataSource, List<MultiDataSourceJoinConfigRequest> mappings,
                                                                  String filter) throws UnExpectedRequestException {
        List<RuleVariable> ruleVariables = new ArrayList<>();

        for (TemplateMidTableInputMeta templateMidTableInputMeta : template.getTemplateMidTableInputMetas()){
            List<MultiDataSourceJoinConfigRequest> tmpMapping = adjustMapping(mappings);
            Map<String, String> autoAdaptValue = autoArgumentAdapter.getMultiSourceAdaptValue(rule, templateMidTableInputMeta, clusterName,
                    firstDataSource, secondDataSource, tmpMapping, filter);
            ruleVariables.add(new RuleVariable(rule, InputActionStepEnum.TEMPLATE_INPUT_META.getCode(), templateMidTableInputMeta,
                    null, autoAdaptValue));
        }

        return ruleVariables;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, SQLException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> addMultiSourceRuleForUpload(AddMultiSourceRuleRequest request,
        boolean check) throws UnExpectedRequestException, PermissionDeniedRequestException {
        return addMultiSourceRuleReal(request, check);
    }

}
