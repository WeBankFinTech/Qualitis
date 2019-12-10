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
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.adapter.AutoArgumentAdapter;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTemplateTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.entity.*;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.*;
import com.webank.wedatasphere.qualitis.rule.response.MultiRuleDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import com.webank.wedatasphere.qualitis.rule.service.*;
import com.webank.wedatasphere.qualitis.rule.service.RuleService;
import com.webank.wedatasphere.qualitis.rule.service.RuleVariableService;
import org.apache.catalina.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    private RuleTemplateService ruleTemplateService;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private RuleVariableService ruleVariableService;
    @Autowired
    private AutoArgumentAdapter autoArgumentAdapter;
    @Autowired
    private AlarmConfigService alarmConfigService;
    @Autowired
    private RuleDataSourceService ruleDataSourceService;
    @Autowired
    private RuleDataSourceMappingService ruleDataSourceMappingService;
    @Autowired
    private RuleGroupDao ruleGroupDao;
    @Autowired
    private ClusterInfoDao clusterInfoDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiSourceRuleServiceImpl.class);
    private static final String MID_TMP_STR = "@#$%^&";

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public GeneralResponse<RuleResponse> addMultiSourceRule(AddMultiSourceRuleRequest request) throws UnExpectedRequestException {
        // Check Arguments
        AddMultiSourceRuleRequest.checkRequest(request, false);

        // Check existence of project
        Project projectInDb = projectService.checkProjectExistence(request.getProjectId());
        // Check existence of rule name
        ruleService.checkRuleName(request.getRuleName(), projectInDb, null);
        // Check existence of cluster
        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(request.getClusterName());
        if (clusterInfo == null) {
            throw new UnExpectedRequestException("Cluster :" + request.getClusterName() + " {&DOES_NOT_EXIST}");
        }
        // Check existence of id and check if multi-table rule
        Template templateInDb = ruleTemplateService.checkRuleTemplate(request.getMultiSourceRuleTemplateId());
        if (!templateInDb.getTemplateType().equals(RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode())) {
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

        Rule savedRule = generateRule(request, projectInDb, false, null, false);
        savedRule.setRuleGroup(ruleGroup);
        savedRule = ruleDao.saveRule(savedRule);
        if (templateInDb.getChildTemplate() != null) {
            // Generate child rule
            AddMultiSourceRuleRequest addMultiSourceRuleRequest = generateChildRequest(request, templateInDb.getChildTemplate());
            Rule childRule = generateRule(addMultiSourceRuleRequest, null, false, null, true);
            childRule.setParentRule(savedRule);
            ruleDao.saveRule(childRule);
        }

        RuleResponse response = new RuleResponse(savedRule);
        LOGGER.info("Succeed to add custom rule, rule_id: {}", savedRule.getId());
        return new GeneralResponse<>("200", "{&ADD_MULTI_RULE_SUCCESSFULLY}", response);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public GeneralResponse<?> deleteMultiSourceRule(DeleteMultiSourceRequest request) throws UnExpectedRequestException {
        // Check Arguments
        DeleteMultiSourceRequest.checkRequest(request);

        // Check existence of rule and check if multi-table rule
        Rule ruleInDb = ruleDao.findById(request.getMultiRuleId());
        if (ruleInDb == null) {
            throw new UnExpectedRequestException("rule_id [" + request.getMultiRuleId() + "] {&DOES_NOT_EXIST}");
        }
        projectService.checkProjectExistence(ruleInDb.getProject().getId());
        if (!ruleInDb.getRuleType().equals(RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode())) {
            throw new UnExpectedRequestException("rule_id: [" + request.getMultiRuleId() + "]) {&IS_NOT_A_MULTI_TEMPLATE_RULE}");
        }
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
    @Transactional(rollbackFor = {Exception.class})
    public GeneralResponse<RuleResponse> modifyMultiSourceRule(ModifyMultiSourceRequest request) throws UnExpectedRequestException {
        // Check Arguments
        ModifyMultiSourceRequest.checkRequest(request);

        // Check existence of rule
        Rule ruleInDb = ruleDao.findById(request.getRuleId());
        if (ruleInDb == null) {
            throw new UnExpectedRequestException("rule_id [" + request.getRuleId() + "] {&DOES_NOT_EXIST}");
        }
        Project projectInDb = projectService.checkProjectExistence(ruleInDb.getProject().getId());
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
        Rule savedRule = generateRule(addMultiSourceRuleRequest, projectInDb, true, ruleInDb, false);
        if (templateInDb.getChildTemplate() != null) {
            // Generate child rule
            AddMultiSourceRuleRequest childRequest = generateChildRequest(addMultiSourceRuleRequest, templateInDb.getChildTemplate());
            Rule childRule = generateRule(childRequest, null, false, null, true);
            childRule.setParentRule(savedRule);
            ruleDao.saveRule(childRule);
            LOGGER.info("Succeed to generate child rule");
        }

        RuleResponse response = new RuleResponse(savedRule);
        LOGGER.info("Succeed to modify custom rule, rule_id: {}", savedRule.getId());
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
        return reverseRequest;
    }

    private List<MultiDataSourceJoinConfigRequest> adjustMapping(List<MultiDataSourceJoinConfigRequest> mappings) {
        List<MultiDataSourceJoinConfigRequest> result = new ArrayList<>();
        for (MultiDataSourceJoinConfigRequest mapping : mappings) {
            MultiDataSourceJoinConfigRequest tmp = new MultiDataSourceJoinConfigRequest();
            List<MultiDataSourceJoinColumnRequest> left = new ArrayList<>();
            List<MultiDataSourceJoinColumnRequest> right = new ArrayList<>();
            for (MultiDataSourceJoinColumnRequest columnRequest : mapping.getLeft()) {
                if (columnRequest.getColumnName().contains("tmp1")) {
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
                if (columnRequest.getColumnName().contains("tmp1")) {
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

    private Rule generateRule(AddMultiSourceRuleRequest request, Project projectInDb, Boolean modify, Rule ruleInDb, Boolean isChild) throws UnExpectedRequestException {
        // Check existence of rule and if multi-table rule
        Template templateInDb = ruleTemplateService.checkRuleTemplate(request.getMultiSourceRuleTemplateId());
        if (!templateInDb.getTemplateType().equals(RuleTemplateTypeEnum.MULTI_SOURCE_TEMPLATE.getCode())) {
            throw new UnExpectedRequestException("Template id :" + request.getMultiSourceRuleTemplateId() + " {&IS_NOT_A_MULTI_SOURCE_TEMPLATE}");
        }

        Rule savedRule;
        if (modify) {
            ruleInDb.setTemplate(templateInDb);
            ruleInDb.setRuleTemplateName(templateInDb.getName());
            ruleInDb.setName(request.getRuleName());
            ruleInDb.setAlarm(request.getAlarm());
            savedRule = ruleDao.saveRule(ruleInDb);
            LOGGER.info("Succeed to save rule, rule_id: {}", savedRule.getId());
        } else {
            // Generate rule and save
            Rule newRule = new Rule();
            newRule.setTemplate(templateInDb);
            newRule.setRuleTemplateName(templateInDb.getName());
            newRule.setName(request.getRuleName());
            newRule.setAlarm(request.getAlarm());
            newRule.setProject(projectInDb);
            newRule.setRuleType(RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode());
            savedRule = ruleDao.saveRule(newRule);
            LOGGER.info("Succeed to save rule, rule_id: {}", savedRule.getId());
        }

        // Generate rule_datasource, rule_variable, rule_alarm_config
        LOGGER.info("Start to generate and save rule_variable.");
        List<RuleVariable> ruleVariables = autoAdaptRequestAndGetRuleVariable(savedRule, templateInDb, request.getClusterName(),
                request.getSource(), request.getTarget(), request.getMappings(), request.getFilter());
        List<RuleVariable> savedRuleVariables = ruleVariableService.saveRuleVariable(ruleVariables);
        LOGGER.info("Succeed to save rule_variables, rule_variables: {}", savedRuleVariables);
        List<AlarmConfig> savedAlarmConfigs = new ArrayList<>();
        if (request.getAlarm()) {
            savedAlarmConfigs  = alarmConfigService.checkAndSaveAlarmVariable(request.getAlarmVariable(), savedRule);
            LOGGER.info("Succeed to save alarm_configs, alarm_configs: {}", savedAlarmConfigs);
        }
        if (!isChild) {
            List<DataSourceRequest> dataSourceRequests = generateDataSourceRequest(request.getClusterName(), request.getSource(), request.getTarget());
            List<RuleDataSource> savedRuleDataSource = ruleDataSourceService.checkAndSaveRuleDataSource(dataSourceRequests, savedRule);
            LOGGER.info("Succeed to save rule_dataSources, rule_dataSources: {}", savedRuleDataSource);
            savedRule.setRuleDataSources(new HashSet<>(savedRuleDataSource));
        }

        List<RuleDataSourceMapping> savedRuleDataSourceMappings = ruleDataSourceMappingService.checkAndSaveRuleDataSourceMapping(request.getMappings(), savedRule);
        LOGGER.info("Succeed to save rule_dataSource_mappings, rule_dataSource_mappings: {}", savedRuleDataSourceMappings);

        savedRule.setRuleVariables(new HashSet<>(savedRuleVariables));
        savedRule.setAlarmConfigs(new HashSet<>(savedAlarmConfigs));
        savedRule.setRuleDataSourceMappings(new HashSet<>(savedRuleDataSourceMappings));

        return savedRule;
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

        DataSourceRequest targetDataSourceRequest = new DataSourceRequest();
        targetDataSourceRequest.setClusterName(clusterName);
        targetDataSourceRequest.setFilter(targetConfig.getFilter());
        targetDataSourceRequest.setTableName(targetConfig.getTableName());
        targetDataSourceRequest.setDbName(targetConfig.getDbName());
        targetDataSourceRequest.setColNames(new ArrayList<>());
        targetDataSourceRequest.setDatasourceIndex(1);

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
}
