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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.TaskNotExistException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.OperateTypeEnum;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.project.service.ProjectEventService;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.adapter.AutoArgumentAdapter;
import com.webank.wedatasphere.qualitis.rule.constant.*;
import com.webank.wedatasphere.qualitis.rule.dao.BdpClientHistoryDao;
import com.webank.wedatasphere.qualitis.rule.dao.ExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.entity.*;
import com.webank.wedatasphere.qualitis.rule.exception.RuleLockException;
import com.webank.wedatasphere.qualitis.rule.request.AbstractCommonRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceColumnRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplateArgumentRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.*;
import com.webank.wedatasphere.qualitis.rule.response.MultiRuleDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import com.webank.wedatasphere.qualitis.rule.service.*;
import com.webank.wedatasphere.qualitis.scheduled.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import com.webank.wedatasphere.qualitis.util.map.CustomObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Service
public class MultiSourceRuleServiceImpl extends AbstractRuleService implements MultiSourceRuleService {
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
    private BdpClientHistoryDao bdpClientHistoryDao;
    @Autowired
    private ExecutionParametersDao executionParametersDao;

    @Autowired
    private AutoArgumentAdapter autoArgumentAdapter;

    @Autowired
    private RuleLockService ruleLockService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiSourceRuleServiceImpl.class);

    private static final String MID_TMP_STR = "@#$%^&";

    private HttpServletRequest httpServletRequest;

    public MultiSourceRuleServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class, SQLException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> addMultiSourceRule(AddMultiSourceRuleRequest request, boolean check)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        return addMultiSourceRuleReal(request, check);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> addRuleForOuter(AbstractCommonRequest request, boolean check)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        return addMultiSourceRuleReal((AddMultiSourceRuleRequest) request, check);
    }

    private GeneralResponse<RuleResponse> addMultiSourceRuleReal(AddMultiSourceRuleRequest request, boolean check)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {

        if (request.getRuleEnable() == null) {
            request.setRuleEnable(true);
        }
        if (request.getUnionAll() == null) {
            request.setUnionAll(false);
        }
        String csId = request.getCsId();
        boolean cs = false;
        if (StringUtils.isNotBlank(csId)) {
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
        Project projectInDb = projectService.checkProjectExistence(request.getProjectId(), loginUser);
        // Check permissions of project
        if (check) {
            List<Integer> permissions = new ArrayList<>();
            permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
            projectService.checkProjectPermission(projectInDb, loginUser, permissions);

            // Check existence of rule name
            ruleService.checkRuleName(request.getRuleName(), request.getWorkFlowName(), request.getWorkFlowVersion(), projectInDb, null);
            //check the same rule name number
            ruleService.checkRuleNameNumber(request.getRuleName(), projectInDb);
        }
        // Check existence of cluster
        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(request.getClusterName());
        if (clusterInfo == null) {
            throw new UnExpectedRequestException("Cluster :" + request.getClusterName() + " {&DOES_NOT_EXIST}");
        }

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
        String leftUuid = UuidGenerator.generate();
        String rightUuid = UuidGenerator.generate();
        Rule savedRule = generateRule(request, projectInDb, false, null, cs, leftUuid, rightUuid, loginUser);
        savedRule.setRuleGroup(ruleGroup);
        savedRule = ruleDao.saveRule(savedRule);

        super.recordEvent(loginUser, savedRule, OperateTypeEnum.CREATE_RULES);

        RuleResponse response = new RuleResponse(savedRule);
        LOGGER.info("Succeed to add multi source rule, rule id: {}", savedRule.getId());
        return new GeneralResponse<>("200", "{&ADD_MULTI_RULE_SUCCESSFULLY}", response);
    }

    @Override
    @Transactional(rollbackFor = {UnExpectedRequestException.class, Exception.class})
    public GeneralResponse deleteMultiSourceRule(DeleteMultiSourceRequest request, String loginUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check Arguments
        DeleteMultiSourceRequest.checkRequest(request);
        // Check existence of rule and check if multi-table rule
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
            throw new UnExpectedRequestException("Rule id: [" + request.getRuleId() + "]) {&IS_NOT_A_MULTI_TEMPLATE_RULE}");
        }
        // Record project event.
//        projectEventService.record(projectInDb.getId(), loginUser, "delete", "multi source rule[name= " + ruleInDb.getName() + "].", EventTypeEnum.MODIFY_PROJECT.getCode());
        // Delete rule
        return deleteMultiRuleReal(ruleInDb);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, UnExpectedRequestException.class})
    public GeneralResponse deleteMultiRuleReal(Rule rule) throws UnExpectedRequestException {
        // Delete bdp-client history
        BdpClientHistory bdpClientHistory = bdpClientHistoryDao.findByRuleId(rule.getId());
        if (bdpClientHistory != null) {
            bdpClientHistoryDao.delete(bdpClientHistory);
        }

        // Delete rule
        ruleDao.deleteRule(rule);

        super.recordEvent(HttpUtils.getUserName(httpServletRequest), rule, OperateTypeEnum.DELETE_RULES);
        LOGGER.info("Succeed to delete multi rule. rule id: {}", rule.getId());
        return new GeneralResponse<>("200", "{&DELETE_MULTI_RULE_SUCCESSFULLY}", null);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> modifyMultiSourceRule(ModifyMultiSourceRequest request)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        return modifyRuleDetailReal(request, loginUser);
    }

    @Transactional(rollbackFor = {Exception.class, UnExpectedRequestException.class})
    @Override
    public GeneralResponse<RuleResponse> modifyMultiSourceRuleWithLock(ModifyMultiSourceRequest request) throws UnExpectedRequestException, TaskNotExistException, ClusterInfoNotConfigException, PermissionDeniedRequestException, IOException, RuleLockException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        if (!ruleLockService.tryAcquire(request.getRuleId(), RuleLockRangeEnum.RULE, loginUser)) {
            LOGGER.warn("Failed to acquire lock");
            throw new RuleLockException("{&RULE_LOCK_ACQUIRE_FAILED}");
        }
        try {
            return modifyRuleDetailReal(request, loginUser);
        } finally {
            ruleLockService.release(request.getRuleId(), RuleLockRangeEnum.RULE, loginUser);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, SQLException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> modifyRuleDetailForOuter(ModifyMultiSourceRequest modifyRuleRequest, String userName)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        return modifyRuleDetailReal(modifyRuleRequest, userName);
    }

    @Override
    public List<Map<String, Object>> getAllConstrastEnum() {
        return ContrastTypeEnum.getConstrastEnumList();
    }

    private GeneralResponse<RuleResponse> modifyRuleDetailReal(ModifyMultiSourceRequest request, String loginUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        // Check Arguments
        CommonChecker.checkObject(request, "request");
        if (request.getRuleEnable() == null) {
            request.setRuleEnable(true);
        }
        if (request.getUnionAll() == null) {
            request.setUnionAll(false);
        }
        String csId = request.getCsId();
        boolean cs = false;
        if (StringUtils.isNotBlank(csId)) {
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
            throw new UnExpectedRequestException("Rule id: [" + request.getRuleId() + "]) {&IS_NOT_A_MULTI_TEMPLATE_RULE}");
        }
        LOGGER.info("Succeed to find multi rule. rule id: {}", ruleInDb.getId());
        // Check existence of project name
        ruleService.checkRuleName(request.getRuleName(), request.getWorkFlowName(), request.getWorkFlowVersion(), ruleInDb.getProject(), ruleInDb.getId());
        //check the same rule name number
        ruleService.checkRuleNameNumber(request.getRuleName(), projectInDb);
        // Check if cluster name supported
        ruleDataSourceService.checkDataSourceClusterSupport(request.getClusterName());

        // Delete rule config by rule
        alarmConfigService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all alarm_config. rule id: {}", ruleInDb.getId());
        // Delete rule datasource by rule
        ruleDataSourceService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all rule_dataSources. rule id: {}", ruleInDb.getId());
        // Delete rule datasource mapping by rule
        ruleDataSourceMappingService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all rule_dataSource_mapping. rule id: {}", ruleInDb.getId());

        // Delete rule variable by rule
        ruleVariableService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all rule_variable. rule id: {}", ruleInDb.getId());
        String ruleGroupName = request.getRuleGroupName();
        if (StringUtils.isNotEmpty(ruleGroupName)) {
            ruleInDb.getRuleGroup().setRuleGroupName(ruleGroupName);
            ruleGroupDao.saveRuleGroup(ruleInDb.getRuleGroup());
        }
        AddMultiSourceRuleRequest addMultiSourceRuleRequest = new AddMultiSourceRuleRequest();
        BeanUtils.copyProperties(request, addMultiSourceRuleRequest);
        String leftUuid = UuidGenerator.generate();
        String rightUuid = UuidGenerator.generate();
        Rule savedRule = generateRule(addMultiSourceRuleRequest, projectInDb, true, ruleInDb, cs, leftUuid, rightUuid, loginUser);

        super.recordEvent(loginUser, savedRule, OperateTypeEnum.MODIFY_RULES);

        RuleResponse response = new RuleResponse(savedRule);
        LOGGER.info("Succeed to modify multi source rule, rule id: {}", savedRule.getId());
        return new GeneralResponse<>("200", "{&MODIFY_MULTI_RULE_SUCCESSFULLY}", response);
    }

    @Override
    public GeneralResponse<MultiRuleDetailResponse> getMultiSourceRule(Long ruleId) throws UnExpectedRequestException {
        // Check existence of rule
        Rule ruleInDb = ruleDao.findById(ruleId);
        if (ruleInDb == null) {
            throw new UnExpectedRequestException("Rule id [" + ruleId + "] {&DOES_NOT_EXIST}");
        }
        if (!ruleInDb.getRuleType().equals(RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode())) {
            throw new UnExpectedRequestException("Rule id: [" + ruleId + "]) {&IS_NOT_A_MULTI_TEMPLATE_RULE}");
        }
        Long projectInDbId = ruleInDb.getProject().getId();
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        projectService.checkProjectExistence(projectInDbId, loginUser);
        MultiRuleDetailResponse multiRuleDetailResponse = new MultiRuleDetailResponse(ruleInDb);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_DETAIL_OF_MULTI_RULE}", multiRuleDetailResponse);
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

    private Rule generateRule(AddMultiSourceRuleRequest request, Project projectInDb, Boolean modify, Rule ruleInDb, Boolean cs
            , String leftUuid, String rightUuid, String loginUser) throws UnExpectedRequestException, IOException, PermissionDeniedRequestException {
        // Check existence of rule and if multi-table rule
        Template templateInDb = ruleTemplateService.checkRuleTemplate(request.getMultiSourceRuleTemplateId());
        if (!templateInDb.getTemplateType().equals(RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode())) {
            throw new UnExpectedRequestException("Template id :" + request.getMultiSourceRuleTemplateId() + " {&IS_NOT_A_MULTI_SOURCE_TEMPLATE}");
        }
        String nowDate = QualitisConstants.PRINT_TIME_FORMAT.format(new Date());
        Rule savedRule;
        if (modify) {
            savedRule = setBasicInfo(ruleInDb, templateInDb, request, loginUser, nowDate);
        } else {
            // Generate rule and save
            Rule newRule = new Rule();
            fillRuleBase(request, projectInDb, loginUser, templateInDb, nowDate, newRule);

            savedRule = ruleDao.saveRule(newRule);
        }
        if (StringUtils.isNotBlank(request.getExecutionParametersName())) {
            ExecutionParameters executionParameters = executionParametersDao.findByNameAndProjectId(request.getExecutionParametersName(), projectInDb.getId());
            if (executionParameters != null) {
                savedRule.setExecutionParametersName(request.getExecutionParametersName());
                savedRule.setUnionAll(executionParameters.getUnionAll());
                request.setUploadAbnormalValue(executionParameters.getUploadAbnormalValue());
                request.setUploadRuleMetricValue(executionParameters.getUploadRuleMetricValue());
                request.setDeleteFailCheckResult(executionParameters.getDeleteFailCheckResult());
                //sourceTableFilter替换
                if (StringUtils.isNotEmpty(executionParameters.getSourceTableFilter())) {
                    request.getSource().setFilter(executionParameters.getSourceTableFilter());
                }
                //targetTableFilter 替换
                if (StringUtils.isNotEmpty(executionParameters.getTargetTableFilter())) {
                    request.getTarget().setFilter(executionParameters.getTargetTableFilter());
                }

            }
        } else {
            setMultiSoureInfo(request, savedRule);
        }
        LOGGER.info("Succeed to save rule, rule id: {}", savedRule.getId());
        MultiDataSourceConfigRequest left = request.getSource();
        MultiDataSourceConfigRequest right = request.getTarget();
        if (StringUtils.isNotBlank(left.getFileId())) {
            left.setTableName(left.getTableName().concat("_").concat(leftUuid));
        }
        if (StringUtils.isNotBlank(right.getFileId())) {
            right.setTableName(right.getTableName().concat("_").concat(rightUuid));
        }
        // 比对方向
        if (request.getContrastType() != null) {
            savedRule.setContrastType(request.getContrastType());
        }
        // Generate rule_datasource, rule_variable, rule_alarm_config
        LOGGER.info("Start to generate and save rule_variable.");
        List<RuleVariable> ruleVariables = autoAdaptRequestAndGetRuleVariable(savedRule, templateInDb, request.getClusterName(), left, right, request.getTemplateArgumentRequests(), request.getColNames());
        List<RuleVariable> savedRuleVariables = ruleVariableService.saveRuleVariable(ruleVariables);
        LOGGER.info("Succeed to save rule_variables, rule_variables: {}", savedRuleVariables);
        List<AlarmConfig> savedAlarmConfigs = new ArrayList<>();
        if (request.getAlarm()) {
            request.getAlarmVariable().stream().map(u -> {
                u.setUploadAbnormalValue(request.getUploadAbnormalValue() != null ? request.getUploadAbnormalValue() : false);
                u.setUploadRuleMetricValue(request.getUploadRuleMetricValue() != null ? request.getUploadRuleMetricValue() : false);
                u.setDeleteFailCheckResult(request.getDeleteFailCheckResult() != null ? request.getDeleteFailCheckResult() : false);
                return u;
            }).collect(Collectors.toList());
            savedAlarmConfigs = alarmConfigService.checkAndSaveAlarmVariable(request.getAlarmVariable(), savedRule, loginUser, null, request.getSource(), request.getTarget());
            LOGGER.info("Succeed to save alarm_configs, alarm_configs: {}", savedAlarmConfigs);
        }
        List<DataSourceRequest> dataSourceRequests = generateDataSourceRequest(request.getClusterName(), left, right, request.getColNames());
        List<RuleDataSource> savedRuleDataSource = ruleDataSourceService.checkAndSaveRuleDataSource(dataSourceRequests, savedRule, null
                , cs, loginUser);
        LOGGER.info("Succeed to save rule_dataSources, rule_dataSources: {}", savedRuleDataSource);
        savedRule.setRuleDataSources(new HashSet<>(savedRuleDataSource));

        // 获取比对字段、连接字段
        List<MultiDataSourceJoinConfigRequest> mappings = Lists.newArrayList();
        List<MultiDataSourceJoinConfigRequest> compareCols = Lists.newArrayList();

        for (TemplateArgumentRequest templateArgumentRequest : request.getTemplateArgumentRequests()) {
            if (TemplateInputTypeEnum.CONNECT_FIELDS.getCode().equals(templateArgumentRequest.getArgumentType()) && StringUtils.isNotBlank(templateArgumentRequest.getArgumentValue())) {
                mappings = CustomObjectMapper.transJsonToObjects(templateArgumentRequest.getArgumentValue(), MultiDataSourceJoinConfigRequest.class);
                for (MultiDataSourceJoinConfigRequest mapping : mappings) {
                    MultiDataSourceJoinConfigRequest.checkRequest(mapping);
                }
            } else if (TemplateInputTypeEnum.COMPARISON_FIELD_SETTINGS.getCode().equals(templateArgumentRequest.getArgumentType()) && StringUtils.isNotBlank(templateArgumentRequest.getArgumentValue())) {
                compareCols = CustomObjectMapper.transJsonToObjects(templateArgumentRequest.getArgumentValue(), MultiDataSourceJoinConfigRequest.class);
                for (MultiDataSourceJoinConfigRequest compareCol : compareCols) {
                    MultiDataSourceJoinConfigRequest.checkRequest(compareCol);
                }
            }
        }

        Set<RuleDataSourceMapping> ruleDataSourceMappings = Sets.newHashSet();
        List<RuleDataSourceMapping> savedRuleDataSourceMappings = ruleDataSourceMappingService.checkAndSaveRuleDataSourceMapping(mappings, savedRule, MappingTypeEnum.CONNECT_FIELDS.getCode());
        LOGGER.info("Succeed to save rule datasource join columns, rule dataSource mappings: {}", savedRuleDataSourceMappings);

        List<RuleDataSourceMapping> savedRuleDataSourceCompareCols = ruleDataSourceMappingService.checkAndSaveRuleDataSourceMapping(compareCols, savedRule, MappingTypeEnum.MATCHING_FIELDS.getCode());
        LOGGER.info("Succeed to save rule datasource compare columns, rule dataSource compare columns: {}", savedRuleDataSourceCompareCols);

        ruleDataSourceMappings.addAll(savedRuleDataSourceMappings);
        ruleDataSourceMappings.addAll(savedRuleDataSourceCompareCols);
        setRuleInfo(savedRule, savedRuleVariables, savedAlarmConfigs, ruleDataSourceMappings);
        return savedRule;
    }

    private void setMultiSoureInfo(AddMultiSourceRuleRequest request, Rule savedRule) {
        savedRule.setEnable(request.getRuleEnable());
        savedRule.setUnionAll(request.getUnionAll());
        savedRule.setExecutionParametersName(null);
        savedRule.setAlert(request.getAlert());
        if (request.getAlert() != null && request.getAlert()) {
            savedRule.setAlertLevel(request.getAlertLevel());
            savedRule.setAlertReceiver(request.getAlertReceiver());
        }
        savedRule.setAbortOnFailure(request.getAbortOnFailure());
        savedRule.setStaticStartupParam(request.getStaticStartupParam());
        savedRule.setDeleteFailCheckResult(request.getDeleteFailCheckResult());
        savedRule.setSpecifyStaticStartupParam(request.getSpecifyStaticStartupParam());
        savedRule.setAbnormalCluster(StringUtils.isNotBlank(request.getAbnormalCluster()) ? request.getAbnormalCluster() : null);
        savedRule.setAbnormalDatabase(StringUtils.isNotBlank(request.getAbnormalDatabase()) ? request.getAbnormalDatabase() : null);
        savedRule.setAbnormalProxyUser(StringUtils.isNotBlank(request.getAbnormalProxyUser()) ? request.getAbnormalProxyUser() : null);
    }

    private void fillRuleBase(AddMultiSourceRuleRequest request, Project projectInDb, String loginUser, Template templateInDb, String nowDate, Rule newRule) {
        newRule.setTemplate(templateInDb);
        newRule.setRuleType(RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode());
        newRule.setRuleTemplateName(templateInDb.getName());
        newRule.setDetail(request.getRuleDetail());
        newRule.setCnName(request.getRuleCnName());
        newRule.setName(request.getRuleName());
        newRule.setAlarm(request.getAlarm());
        newRule.setCsId(request.getCsId());
        newRule.setCreateUser(loginUser);
        newRule.setProject(projectInDb);
        newRule.setCreateTime(nowDate);
        newRule.setBashContent(request.getBashContent());
        newRule.setWorkFlowName(request.getWorkFlowName());
        newRule.setWorkFlowVersion(request.getWorkFlowVersion());
        newRule.setWorkFlowSpace(request.getWorkFlowSpace());
        newRule.setNodeName(request.getNodeName());
    }

    private Boolean handleObjectEqual(AddMultiSourceRuleRequest request, ExecutionParameters executionParameters) {
        return CommonChecker.compareIdentical(request.getUnionAll(), request.getAbortOnFailure(), request.getSpecifyStaticStartupParam(), request.getStaticStartupParam()
                , request.getAbnormalDatabase(), request.getAbnormalCluster(), request.getAlert(), request.getAlertLevel(), request.getAlertReceiver(), request.getAbnormalProxyUser(), request.getDeleteFailCheckResult(), request.getUploadRuleMetricValue(), request.getUploadAbnormalValue(), executionParameters);
    }

    private void setRuleInfo(Rule savedRule, List<RuleVariable> savedRuleVariables, List<AlarmConfig> savedAlarmConfigs, Set<RuleDataSourceMapping> ruleDataSourceMappings) {
        savedRule.setRuleVariables(new HashSet<>(savedRuleVariables));
        savedRule.setAlarmConfigs(new HashSet<>(savedAlarmConfigs));
        if (CollectionUtils.isNotEmpty(ruleDataSourceMappings)) {
            savedRule.setRuleDataSourceMappings(ruleDataSourceMappings);
        }
    }

    private Rule setBasicInfo(Rule ruleInDb, Template templateInDb, AddMultiSourceRuleRequest request, String loginUser, String nowDate) {
        ruleInDb.setTemplate(templateInDb);
        ruleInDb.setRuleTemplateName(templateInDb.getName());
        ruleInDb.setDetail(request.getRuleDetail());
        ruleInDb.setCnName(request.getRuleCnName());
        ruleInDb.setName(request.getRuleName());
        ruleInDb.setAlarm(request.getAlarm());
        ruleInDb.setCsId(request.getCsId());
        ruleInDb.setModifyUser(loginUser);
        ruleInDb.setModifyTime(nowDate);
        ruleInDb.setBashContent(request.getBashContent());
        ruleInDb.setWorkFlowName(request.getWorkFlowName());
        ruleInDb.setWorkFlowVersion(request.getWorkFlowVersion());
        ruleInDb.setWorkFlowSpace(request.getWorkFlowSpace());
        ruleInDb.setNodeName(request.getNodeName());
        return ruleDao.saveRule(ruleInDb);
    }

    private List<DataSourceRequest> generateDataSourceRequest(String clusterName, MultiDataSourceConfigRequest sourceConfig, MultiDataSourceConfigRequest targetConfig, List<DataSourceColumnRequest> colNames) {
        List<DataSourceRequest> dataSourceRequests = new ArrayList<>();
        DataSourceRequest sourceDataSourceRequest = new DataSourceRequest();

        sourceDataSourceRequest.setDatasourceIndex(0);
        sourceDataSourceRequest.setClusterName(clusterName);
        sourceDataSourceRequest.setType(sourceConfig.getType());
        sourceDataSourceRequest.setFilter(sourceConfig.getFilter());
        sourceDataSourceRequest.setFileId(sourceConfig.getFileId());
        sourceDataSourceRequest.setDbName(sourceConfig.getDbName());
        sourceDataSourceRequest.setFileType(sourceConfig.getFileType());
        sourceDataSourceRequest.setTableName(sourceConfig.getTableName());
        sourceDataSourceRequest.setProxyUser(sourceConfig.getProxyUser());
        sourceDataSourceRequest.setFileHeader(sourceConfig.getFileHeader());
        sourceDataSourceRequest.setFileDelimiter(sourceConfig.getFileDelimiter());
        sourceDataSourceRequest.setFileTablesDesc(sourceConfig.getFileTableDesc());
        sourceDataSourceRequest.setFileHashValues(sourceConfig.getFileHashValues());
        sourceDataSourceRequest.setLinkisDataSourceId(sourceConfig.getLinkisDataSourceId());
        sourceDataSourceRequest.setLinkisDataSourceName(sourceConfig.getLinkisDataSourceName());
        sourceDataSourceRequest.setLinkisDataSourceType(sourceConfig.getLinkisDataSourceType());
        sourceDataSourceRequest.setDataSourceEnvRequests(sourceConfig.getDataSourceEnvRequests());
        sourceDataSourceRequest.setLinkisDataSourceVersionId(sourceConfig.getLinkisDataSourceVersionId());

        if (CollectionUtils.isNotEmpty(colNames)) {
            sourceDataSourceRequest.setColNames(colNames);
            sourceDataSourceRequest.setBlackList(true);
        } else {
            sourceDataSourceRequest.setColNames(new ArrayList<>());
        }

        DataSourceRequest targetDataSourceRequest = new DataSourceRequest();

        targetDataSourceRequest.setDatasourceIndex(1);
        targetDataSourceRequest.setClusterName(clusterName);
        targetDataSourceRequest.setColNames(new ArrayList<>());
        targetDataSourceRequest.setType(targetConfig.getType());
        targetDataSourceRequest.setFilter(targetConfig.getFilter());
        targetDataSourceRequest.setDbName(targetConfig.getDbName());
        targetDataSourceRequest.setFileId(targetConfig.getFileId());
        targetDataSourceRequest.setFileType(targetConfig.getFileType());
        targetDataSourceRequest.setProxyUser(targetConfig.getProxyUser());
        targetDataSourceRequest.setTableName(targetConfig.getTableName());
        targetDataSourceRequest.setFileHeader(targetConfig.getFileHeader());
        targetDataSourceRequest.setFileTablesDesc(targetConfig.getFileTableDesc());
        targetDataSourceRequest.setFileDelimiter(targetConfig.getFileDelimiter());
        targetDataSourceRequest.setFileHashValues(targetConfig.getFileHashValues());
        targetDataSourceRequest.setLinkisDataSourceId(targetConfig.getLinkisDataSourceId());
        targetDataSourceRequest.setLinkisDataSourceName(targetConfig.getLinkisDataSourceName());
        targetDataSourceRequest.setLinkisDataSourceType(targetConfig.getLinkisDataSourceType());
        targetDataSourceRequest.setDataSourceEnvRequests(targetConfig.getDataSourceEnvRequests());
        targetDataSourceRequest.setLinkisDataSourceVersionId(targetConfig.getLinkisDataSourceVersionId());

        dataSourceRequests.add(sourceDataSourceRequest);
        dataSourceRequests.add(targetDataSourceRequest);
        return dataSourceRequests;
    }

    private List<RuleVariable> autoAdaptRequestAndGetRuleVariable(Rule rule, Template template, String clusterName, MultiDataSourceConfigRequest firstDataSource
            , MultiDataSourceConfigRequest secondDataSource, List<TemplateArgumentRequest> templateArgumentRequests, List<DataSourceColumnRequest> colNames) throws UnExpectedRequestException {

        List<MultiDataSourceJoinConfigRequest> mappings = Lists.newArrayList();
        List<MultiDataSourceJoinConfigRequest> compareCols = Lists.newArrayList();
        String filter = null;
        String contrastType = null;
        String connFieldOriginValue = null;
        String compFieldOriginValue = null;

        for (TemplateArgumentRequest templateArgumentRequest : templateArgumentRequests) {
            if (TemplateInputTypeEnum.CONNECT_FIELDS.getCode().equals(templateArgumentRequest.getArgumentType()) && StringUtils.isNotBlank(templateArgumentRequest.getArgumentValue())) {
                mappings = CustomObjectMapper.transJsonToObjects(templateArgumentRequest.getArgumentValue(), MultiDataSourceJoinConfigRequest.class);
                for (MultiDataSourceJoinConfigRequest mapping : mappings) {
                    MultiDataSourceJoinConfigRequest.checkRequest(mapping);
                }
                connFieldOriginValue = templateArgumentRequest.getArgumentValue();
            } else if (TemplateInputTypeEnum.COMPARISON_FIELD_SETTINGS.getCode().equals(templateArgumentRequest.getArgumentType()) && StringUtils.isNotBlank(templateArgumentRequest.getArgumentValue())) {
                compareCols = CustomObjectMapper.transJsonToObjects(templateArgumentRequest.getArgumentValue(), MultiDataSourceJoinConfigRequest.class);
                for (MultiDataSourceJoinConfigRequest compareCol : compareCols) {
                    MultiDataSourceJoinConfigRequest.checkRequest(compareCol);
                }
                compFieldOriginValue = templateArgumentRequest.getArgumentValue();
            } else if (TemplateInputTypeEnum.COMPARISON_RESULTS_FOR_FILTER.getCode().equals(templateArgumentRequest.getArgumentType())) {
                filter = templateArgumentRequest.getArgumentValue();
            } else if (TemplateInputTypeEnum.CONTRAST_TYPE.getCode().equals(templateArgumentRequest.getArgumentType())) {
                contrastType = templateArgumentRequest.getArgumentValue();
            }
        }

        List<RuleVariable> ruleVariables = new ArrayList<>();

        for (TemplateMidTableInputMeta templateMidTableInputMeta : template.getTemplateMidTableInputMetas()) {
            List<MultiDataSourceJoinConfigRequest> tmpMappings = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(mappings)) {
                if (TemplateInputTypeEnum.CONNECT_FIELDS.getCode().equals(templateMidTableInputMeta.getInputType())) {
                    tmpMappings = adjustMapping(mappings);
                } else if (TemplateInputTypeEnum.COMPARISON_FIELD_SETTINGS.getCode().equals(templateMidTableInputMeta.getInputType()) && CollectionUtils.isNotEmpty(compareCols)) {
                    tmpMappings = adjustMapping(compareCols);
                }
            }
            Map<String, String> autoAdaptValue = autoArgumentAdapter.getMultiSourceAdaptValue(rule, templateMidTableInputMeta, clusterName
                    , firstDataSource, secondDataSource, tmpMappings, colNames, filter, contrastType, connFieldOriginValue, compFieldOriginValue);
            ruleVariables.add(new RuleVariable(rule, InputActionStepEnum.TEMPLATE_INPUT_META.getCode(), templateMidTableInputMeta, null, autoAdaptValue));
        }

        return ruleVariables;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, SQLException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> addMultiSourceRuleForUpload(AddMultiSourceRuleRequest request, boolean check) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        return addMultiSourceRuleReal(request, check);
    }

}
