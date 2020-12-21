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

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.request.AddCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.response.CustomRuleDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import com.webank.wedatasphere.qualitis.rule.service.AlarmConfigService;
import com.webank.wedatasphere.qualitis.rule.service.CustomRuleService;
import com.webank.wedatasphere.qualitis.rule.service.RuleDataSourceService;
import com.webank.wedatasphere.qualitis.rule.service.RuleService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author howeye
 */
@Service
public class CustomRuleServiceImpl implements CustomRuleService {

    @Autowired
    private RuleTemplateServiceImpl ruleTemplateService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private RuleDataSourceDao ruleDatasourceDao;
    @Autowired
    private AlarmConfigService alarmConfigService;
    @Autowired
    private RuleDataSourceService ruleDataSourceService;
    @Autowired
    private RuleGroupDao ruleGroupDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomRuleServiceImpl.class);

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class, SemanticException.class, ParseException.class})
    public GeneralResponse<RuleResponse> addCustomRule(AddCustomRuleRequest request) throws UnExpectedRequestException, SemanticException, ParseException {
        // Check Arguments
        AddCustomRuleRequest.checkRequest(request);

        // Generate Template, TemplateStatisticsInputMeta and save
        Template template = ruleTemplateService.addCustomTemplate(request);

        // Save rule, rule_alarm_config and ruleDataSource
        // Check existence of project
        Project projectInDb = projectService.checkProjectExistence(request.getProjectId());
        // Check unique of rule name
        ruleService.checkRuleName(request.getRuleName(), projectInDb, null);
        // Check if cluster name is supported
        ruleDataSourceService.checkDataSourceClusterSupport(request.getClusterName());

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

        Rule newRule = new Rule();
        newRule.setRuleType(RuleTypeEnum.CUSTOM_RULE.getCode());
        newRule.setTemplate(template);
        newRule.setName(request.getRuleName());
        newRule.setAlarm(request.getAlarm());
        newRule.setProject(projectInDb);
        newRule.setRuleTemplateName(template.getName());
        newRule.setFunctionType(request.getFunctionType());
        newRule.setFunctionContent(request.getFunctionContent());
        newRule.setFromContent(request.getFromContent());
        newRule.setWhereContent(request.getWhereContent());
        newRule.setOutputName(request.getOutputName());
        newRule.setRuleTemplateName(template.getName());
        newRule.setRuleGroup(ruleGroup);
        newRule.setAbortOnFailure(request.getAbortOnFailure());
        Rule savedRule = ruleDao.saveRule(newRule);
        LOGGER.info("Succeed to save custom rule, rule_id: {}", savedRule.getId());

        List<AlarmConfig> savedAlarmConfigs = new ArrayList<>();
        if (request.getAlarm()) {
            savedAlarmConfigs  = alarmConfigService.checkAndSaveCustomAlarmVariable(request.getAlarmVariable(), savedRule);
            LOGGER.info("Succeed to save alarm_configs, alarm_configs: {}", savedAlarmConfigs);
        }
        List<RuleDataSource> ruleDataSources = ruleDataSourceService.checkAndSaveCustomRuleDataSource(request.getClusterName(), savedRule);

        savedRule.setAlarmConfigs(new HashSet<>(savedAlarmConfigs));
        savedRule.setRuleDataSources(new HashSet<>(ruleDataSources));

        RuleResponse response = new RuleResponse(savedRule);
        LOGGER.info("Succeed to add custom rule, rule_id: {}", savedRule.getId());
        return new GeneralResponse<>("200", "{&SUCCEED_TO_ADD_CUSTOM_RULE}", response);
    }

    /**
     * Delete custom rules, including template of custom rules
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> deleteCustomRule(DeleteCustomRuleRequest request) throws UnExpectedRequestException {
        // Check Arguments
        DeleteCustomRuleRequest.checkRequest(request);

        // Check existence of rule by ruleId
        Rule ruleInDb = ruleDao.findById(request.getRuleId());
        if (ruleInDb == null) {
            throw new UnExpectedRequestException("rule_id [" + request.getRuleId() + "] {&DOES_NOT_EXIST}");
        }
        projectService.checkProjectExistence(ruleInDb.getProject().getId());
        if (!ruleInDb.getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
            throw new UnExpectedRequestException("rule_id: [" + request.getRuleId() + "]) {&IS_NOT_A_CUSTOM_RULE}");
        }

        return deleteCustomRuleReal(ruleInDb);
    }

    @Override
    public GeneralResponse<?> deleteCustomRuleReal(Rule rule) throws UnExpectedRequestException {
        // Delete rule
        ruleDao.deleteRule(rule);
        // Delete template of custom rule
        ruleTemplateService.deleteCustomTemplate(rule.getTemplate());
        LOGGER.info("Succeed to delete custom rule. rule_id: {}", rule.getId());

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
        LOGGER.info("Succeed to find rule. rule_id: {}", ruleInDb.getId());

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
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class, SemanticException.class, ParseException.class})
    public GeneralResponse<RuleResponse> modifyCustomRule(ModifyCustomRuleRequest request) throws UnExpectedRequestException, SemanticException, ParseException {
        ModifyCustomRuleRequest.checkRequest(request);

        Rule ruleInDb = ruleDao.findById(request.getRuleId());
        if (ruleInDb == null) {
            throw new UnExpectedRequestException("rule_id [" + request.getRuleId() + "] {&DOES_NOT_EXIST}");
        }
        projectService.checkProjectExistence(ruleInDb.getProject().getId());
        if (!ruleInDb.getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
            throw new UnExpectedRequestException("rule_id: [" + request.getRuleId() + "]) {&IS_NOT_A_CUSTOM_RULE}");
        }
        LOGGER.info("Succeed to find custom rule. rule_id: {}", ruleInDb.getId());

        // Check existence of project name
        ruleService.checkRuleName(request.getRuleName(), ruleInDb.getProject(), ruleInDb.getId());
        // Check if cluster name supported
        ruleDataSourceService.checkDataSourceClusterSupport(request.getClusterName());
        // Delete alarm config by custom rule
        alarmConfigService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all alarm_config. rule_id: {}", ruleInDb.getId());

        // Delete template of custom rule
        ruleTemplateService.deleteCustomTemplate(ruleInDb.getTemplate());
        LOGGER.info("Succeed to delete custom rule template. rule_id: {}", request.getRuleId());

        // Delete rule datasource of custom rule
        ruleDataSourceService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all rule_dataSources. rule_id: {}", ruleInDb.getId());

        // Save template, alarm config, rule datasource of custom rule
        AddCustomRuleRequest addCustomRuleRequest = new AddCustomRuleRequest();
        BeanUtils.copyProperties(request, addCustomRuleRequest);
        Template template = ruleTemplateService.addCustomTemplate(addCustomRuleRequest);

        // Modify custom rule and save
        ruleInDb.setName(request.getRuleName());
        ruleInDb.setOutputName(request.getOutputName());
        ruleInDb.setTemplate(template);
        ruleInDb.setFunctionType(request.getFunctionType());
        ruleInDb.setFunctionContent(request.getFunctionContent());
        ruleInDb.setFromContent(request.getFromContent());
        ruleInDb.setWhereContent(request.getWhereContent());
        ruleInDb.setAlarm(request.getAlarm());
        ruleInDb.setRuleTemplateName(template.getName());
        ruleInDb.setAbortOnFailure(request.getAbortOnFailure());
        Rule savedRule = ruleDao.saveRule(ruleInDb);

        // Save alarm config and rule datasource
        List<AlarmConfig> savedAlarmConfigs = new ArrayList<>();
        if (request.getAlarm()) {
            savedAlarmConfigs  = alarmConfigService.checkAndSaveCustomAlarmVariable(request.getAlarmVariable(), savedRule);
            LOGGER.info("Succeed to save alarm_configs, alarm_configs: {}", savedAlarmConfigs);
        }
        List<RuleDataSource> ruleDataSources = ruleDataSourceService.checkAndSaveCustomRuleDataSource(request.getClusterName(), savedRule);

        savedRule.setAlarmConfigs(new HashSet<>(savedAlarmConfigs));
        savedRule.setRuleDataSources(new HashSet<>(ruleDataSources));

        RuleResponse response = new RuleResponse(savedRule);
        LOGGER.info("Succeed to modify custom rule, rule_id: {}", savedRule.getId());
        return new GeneralResponse<>("200", "{&SUCCEED_TO_MODIFY_CUSTOM_RULE}", response);
    }
}
