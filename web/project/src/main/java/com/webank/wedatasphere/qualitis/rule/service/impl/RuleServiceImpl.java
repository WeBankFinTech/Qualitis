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

import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.request.AddProjectRequest;
import com.webank.wedatasphere.qualitis.project.response.ProjectDetailResponse;
import com.webank.wedatasphere.qualitis.rule.adapter.AutoArgumentAdapter;
import com.webank.wedatasphere.qualitis.rule.constant.CheckTemplateEnum;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.AlarmConfigDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceMappingDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleTemplateDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleVariableDao;
import com.webank.wedatasphere.qualitis.rule.request.*;
import com.webank.wedatasphere.qualitis.rule.response.RuleDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleNodeResponse;
import com.webank.wedatasphere.qualitis.rule.service.*;
import com.webank.wedatasphere.qualitis.rule.util.TemplateMidTableUtil;
import com.webank.wedatasphere.qualitis.rule.util.TemplateStatisticsUtil;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.constant.StatisticsValueTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.entity.*;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import com.webank.wedatasphere.qualitis.service.UserService;
import java.io.IOException;
import javax.management.relation.RoleNotFoundException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Service
public class RuleServiceImpl implements RuleService {

    @Autowired
    private RuleDao ruleDao;

    @Autowired
    private RuleTemplateService ruleTemplateService;

    @Autowired
    private RuleTemplateDao ruleTemplateDao;

    @Autowired
    private TemplateStatisticsInputMetaService templateStatisticsInputMetaService;

    @Autowired
    private TemplateMidTableInputMetaService templateMidTableInputMetaService;

    @Autowired
    private TemplateOutputMetaService templateOutputMetaService;

    @Autowired
    private RuleVariableService ruleVariableService;

    @Autowired
    private RuleVariableDao ruleVariableDao;

    @Autowired
    private AlarmConfigService alarmConfigService;

    @Autowired
    private AlarmConfigDao alarmConfigDao;

    @Autowired
    private RuleDataSourceService ruleDataSourceService;

    @Autowired
    private RuleDataSourceDao ruleDataSourceDao;

    @Autowired
    private RuleDataSourceMappingDao ruleDataSourceMappingDao;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private AutoArgumentAdapter autoArgumentAdapter;

    @Autowired
    private RuleGroupDao ruleGroupDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleServiceImpl.class);

    private HttpServletRequest httpServletRequest;
    public RuleServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class}, propagation = Propagation.REQUIRED)
    public GeneralResponse<RuleResponse> addRule(AddRuleRequest request) throws UnExpectedRequestException {
        // Check Arguments
        AddRuleRequest.checkRequest(request, false);

        // Check existence of rule template
        Template templateInDb = ruleTemplateService.checkRuleTemplate(request.getRuleTemplateId());
        // Check datasource size
        checkDataSourceNumber(templateInDb, request.getDatasource());
        // Check if cluster supported
        checkDataSourceClusterLimit(request.getDatasource());
        // Check Arguments size
        checkRuleArgumentSize(request.getTemplateArgumentRequests(), templateInDb);
        // Check existence of project
        Project projectInDb = projectService.checkProjectExistence(request.getProjectId());
        // Check existence of rule name
        checkRuleName(request.getRuleName(), projectInDb, null);

        RuleGroup ruleGroup;
        if (request.getRuleGroupId() != null) {
            ruleGroup = ruleGroupDao.findById(request.getRuleGroupId());
            if (ruleGroup == null) {
                throw new UnExpectedRequestException(String.format("Rule Group: %s {&DOES_NOT_EXIST}", request.getRuleGroupId()));
            }
        } else {
            ruleGroup = ruleGroupDao.saveRuleGroup(
                    new RuleGroup("Group_" + UUID.randomUUID().toString().replace("-", ""), projectInDb.getId()));
        }

        // Create and save rule
        Rule newRule = new Rule();
        newRule.setTemplate(templateInDb);
        newRule.setRuleTemplateName(templateInDb.getName());
        newRule.setName(request.getRuleName());
        newRule.setAlarm(request.getAlarm());
        newRule.setProject(projectInDb);
        newRule.setRuleType(RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode());
        newRule.setRuleGroup(ruleGroup);
        newRule.setAbortOnFailure(request.getAbortOnFailure());
        Rule savedRule = ruleDao.saveRule(newRule);
        LOGGER.info("Succeed to save rule, rule_id: {}", savedRule.getId());

        // Save alarm_config,ruleVariable and ruleDataSource
        // Generate rule variable and save
        LOGGER.info("Start to generate and save rule_variable.");
        List<RuleVariable> ruleVariables = autoAdaptArgumentAndGetRuleVariable(templateInDb, request.getDatasource(),
                                                                        request.getTemplateArgumentRequests(), savedRule);
        List<RuleVariable> savedRuleVariables = ruleVariableService.saveRuleVariable(ruleVariables);
        LOGGER.info("Succeed to save rule_variables, rule_variables: {}", savedRuleVariables);
        List<AlarmConfig> savedAlarmConfigs = new ArrayList<>();
        if (request.getAlarm()) {
            savedAlarmConfigs  = alarmConfigService.checkAndSaveAlarmVariable(request.getAlarmVariable(), savedRule);
            LOGGER.info("Succeed to save alarm_configs, alarm_configs: {}", savedAlarmConfigs);
        }
        List<RuleDataSource> savedRuleDataSource = ruleDataSourceService.checkAndSaveRuleDataSource(request.getDatasource(), savedRule);
        LOGGER.info("Succeed to save rule_dataSources, rule_dataSources: {}", savedRuleDataSource);

        savedRule.setRuleVariables(new HashSet<>(savedRuleVariables));
        savedRule.setAlarmConfigs(new HashSet<>(savedAlarmConfigs));
        savedRule.setRuleDataSources(new HashSet<>(savedRuleDataSource));

        RuleResponse response = new RuleResponse(savedRule);
        LOGGER.info("Succeed to add rule, rule_id: {}", response.getRuleId());
        return new GeneralResponse<>("200", "{&ADD_RULE_SUCCESSFULLY}", response);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> deleteRule(DeleteRuleRequest request) throws UnExpectedRequestException {
        // Check Arguments
        DeleteRuleRequest.checkRequest(request);

        // Check existence of rule
        Rule ruleInDb = ruleDao.findById(request.getRuleId());
        if (ruleInDb == null || !ruleInDb.getRuleType().equals(RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode())) {
            throw new UnExpectedRequestException("Rule id [" + request.getRuleId() + "]) {&IS_NOT_A_RULE_WITH_TEMPLATE}");
        }

        projectService.checkProjectExistence(ruleInDb.getProject().getId());
        return deleteRuleReal(ruleInDb);
    }

    @Override
    public GeneralResponse<?> deleteRuleReal(Rule rule) {
        // Delete rule
        ruleDao.deleteRule(rule);
        LOGGER.info("Succeed to delete rule. rule_id: {}", rule.getId());

        return new GeneralResponse<>("200", "{&DELETE_RULE_SUCCESSFULLY}", null);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> modifyRuleDetail(ModifyRuleRequest request) throws UnExpectedRequestException {
        // Check Arguments
        ModifyRuleRequest.checkRequest(request);
        // Check existence of rule
        Rule ruleInDb = ruleDao.findById(request.getRuleId());

        if (ruleInDb == null) {
            throw new UnExpectedRequestException("rule_id [" + request.getRuleId() + "] {&DOES_NOT_EXIST}");
        }
        projectService.checkProjectExistence(ruleInDb.getProject().getId());
        if (!ruleInDb.getRuleType().equals(RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode())) {
            throw new UnExpectedRequestException("rule(id: [" + request.getRuleId() + "]) {&IS_NOT_A_RULE_WITH_TEMPLATE}");
        }
        LOGGER.info("Succeed to find rule. rule_id: {}", ruleInDb.getId());
        // Set rule arguments and save
        // Check existence of rule template
        Template templateInDb = ruleTemplateService.checkRuleTemplate(request.getRuleTemplateId());
        // Check datasource size
        checkDataSourceNumber(templateInDb, request.getDatasource());
        // Check cluster name supported
        checkDataSourceClusterLimit(request.getDatasource());
        // Check arguments size
        checkRuleArgumentSize(request.getTemplateArgumentRequests(), templateInDb);
        // Check existence of rule
        checkRuleName(request.getRuleName(), ruleInDb.getProject(), ruleInDb.getId());

        ruleInDb.setTemplate(templateInDb);
        ruleInDb.setName(request.getRuleName());
        ruleInDb.setAlarm(request.getAlarm());
        ruleInDb.setAbortOnFailure(request.getAbortOnFailure());
        ruleInDb.setRuleTemplateName(templateInDb.getName());
        // Delete all alarm_config,rule_variable,rule_datasource
        alarmConfigService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all alarm_config. rule_id: {}", ruleInDb.getId());

        ruleVariableService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all rule_variable. rule_id: {}", ruleInDb.getId());

        ruleDataSourceService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all rule_dataSources. rule_id: {}", ruleInDb.getId());

        Rule savedRule = ruleDao.saveRule(ruleInDb);
        LOGGER.info("Succeed to save rule. rule_id: {}", savedRule.getId());

        // Save alarm_config,ruleVariable和ruleDataSource
        List<RuleVariable> ruleVariables = autoAdaptArgumentAndGetRuleVariable(templateInDb, request.getDatasource(),
                request.getTemplateArgumentRequests(), savedRule);
        List<RuleVariable> savedRuleVariables = ruleVariableService.saveRuleVariable(ruleVariables);
        LOGGER.info("Succeed to save rule_variable, rule_variable: {}", savedRuleVariables);

        List<AlarmConfig> savedAlarmConfigs = new ArrayList<>();
        if (request.getAlarm()) {
            savedAlarmConfigs  = alarmConfigService.checkAndSaveAlarmVariable(request.getAlarmVariable(), savedRule);
            LOGGER.info("Succeed to save alarm_configs, alarm_configs: {}", savedAlarmConfigs);
        }
        List<RuleDataSource> savedRuleDataSource = ruleDataSourceService.checkAndSaveRuleDataSource(request.getDatasource(), savedRule);
        LOGGER.info("Succeed to save rule_dataSources, rule_dataSources: {}", savedRuleDataSource);

        savedRule.setRuleVariables(new HashSet<>(savedRuleVariables));
        savedRule.setAlarmConfigs(new HashSet<>(savedAlarmConfigs));
        savedRule.setRuleDataSources(new HashSet<>(savedRuleDataSource));

        RuleResponse response = new RuleResponse(savedRule);
        LOGGER.info("Succeed to modify rule. response: {}", response);
        return new GeneralResponse<>("200", "{&MODIFY_RULE_SUCCESSFULLY}", response);
    }

    @Override
    public GeneralResponse<RuleDetailResponse> getRuleDetail(Long ruleId) throws UnExpectedRequestException {
        // Check existence of rule
        Rule ruleInDb = ruleDao.findById(ruleId);
        if (ruleInDb == null) {
            throw new UnExpectedRequestException("rule_id [" + ruleId + "] {&DOES_NOT_EXIST}");
        }
        if (!ruleInDb.getRuleType().equals(RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode())) {
            throw new UnExpectedRequestException("rule(id: [" + ruleId + "]) {&IS_NOT_A_RULE_WITH_TEMPLATE}");
        }
        LOGGER.info("Succeed to find rule. rule_id: {}", ruleInDb.getId());

        RuleDetailResponse response = new RuleDetailResponse(ruleInDb);
        LOGGER.info("Succeed to get rule detail. rule_id: {}", ruleId);
        return new GeneralResponse<>("200", "{&GET_RULE_DETAIL_SUCCESSFULLY}", response);
    }

    @Override
    public GeneralResponse<RuleNodeResponse> exportRuleByGroupId(Long ruleGroupId) throws UnExpectedRequestException {
        // Check existence of ruleGroup
        RuleGroup ruleGroup = ruleGroupDao.findById(ruleGroupId);
        if (ruleGroup == null) {
            throw new UnExpectedRequestException("ruleGroupId [" + ruleGroupId + "] {&DOES_NOT_EXIST}");
        }
        LOGGER.info("Succeed to find rule. ruleGroupId: {}", ruleGroup.getId());
        RuleNodeResponse response = new RuleNodeResponse();
        try {
            List<Rule> rulesInDb = ruleDao.findByRuleGroup(ruleGroup);
            ruleToNodeResponse(rulesInDb.get(0), response);
        } catch (IOException e) {
            LOGGER.error("Failed to export rule because of JSON opeartions. Exception is {}", e);
            return new GeneralResponse<>("500", "{&FAILED_TO_EXPORT_RULE}", response);
        }
        LOGGER.info("Succeed to export rule detail. Rule info: {}", response.toString());
        return new GeneralResponse<>("200", "{&EXPORT_RULE_SUCCESSFULLY}", response);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class}, propagation = Propagation.REQUIRED)
    public GeneralResponse<RuleResponse> importRule(RuleNodeRequest ruleNodeRequest) throws UnExpectedRequestException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Rule rule = objectMapper.readValue(ruleNodeRequest.getRuleObject(), Rule.class);
            Template template = objectMapper.readValue(ruleNodeRequest.getTemplateObject(), Template.class);
            RuleGroup ruleGroup = objectMapper.readValue(ruleNodeRequest.getRuleGroupObject(), RuleGroup.class);

            Set<RuleDataSource> ruleDataSources = objectMapper.readValue(ruleNodeRequest.getRuleDataSourcesObject(),
                new TypeReference<Set<RuleDataSource>>() {});
            Set<RuleDataSourceMapping> ruleDataSourceMappings = objectMapper.readValue(ruleNodeRequest.getRuleDataSourceMappingsObject(),
                new TypeReference<Set<RuleDataSourceMapping>>() {});
            Set<AlarmConfig> alarmConfigs = objectMapper.readValue(ruleNodeRequest.getAlarmConfigsObject(), new TypeReference<Set<AlarmConfig>>() {});
            Set<RuleVariable> ruleVariables = objectMapper.readValue(ruleNodeRequest.getRuleVariablesObject(), new TypeReference<Set<RuleVariable>>() {});

            RuleResponse ruleResponse = new RuleResponse();

            // Find project in produce center.
            Project projectInDb = projectDao.findById(Long.parseLong(ruleNodeRequest.getNewProjectId()));
            if (projectInDb == null) {
                AddProjectRequest addProjectRequest = new AddProjectRequest();
                addProjectRequest.setProjectName(ruleNodeRequest.getProjectName());
                addProjectRequest.setDescription("This is a auto created project for new qualitis node.");
                String userName = ruleNodeRequest.getUserName();
                addProjectRequest.setUsername(userName);
                User currentUser = userDao.findByUsername(userName);
                if (currentUser == null) {
                    userService.autoAddUser(userName);
                }
                GeneralResponse<ProjectDetailResponse> response = projectService.addProject(addProjectRequest, userDao.findByUsername(userName).getId());
                if ("200".equals(response.getCode())) {
                    ruleResponse.setNewProjectId(response.getData().getProjectDetail().getProjectId().toString());
                    projectInDb = projectDao.findById(response.getData().getProjectDetail().getProjectId());
                } else {
                    throw new UnExpectedRequestException("Project id :["+ ruleNodeRequest.getNewProjectId() + "] {&DOES_NOT_EXIST}");
                }
            }
            Rule ruleInDb = ruleDao.findByProjectAndRuleName(projectInDb, rule.getName());
            if (ruleInDb != null) {
                // Update basic rule info.
                ruleInDb.setAlarm(rule.getAlarm());
                ruleInDb.setAbortOnFailure(rule.getAbortOnFailure());
                ruleInDb.setFromContent(rule.getFromContent());
                ruleInDb.setWhereContent(rule.getWhereContent());
                ruleInDb.setRuleType(rule.getRuleType());
                ruleInDb.setRuleTemplateName(template.getName());
                ruleInDb.setOutputName(rule.getOutputName());
                ruleInDb.setFunctionType(rule.getFunctionType());
                ruleInDb.setFunctionContent(rule.getFunctionContent());

                Template templateInDb = ruleTemplateService.checkRuleTemplate(ruleInDb.getTemplate().getId());
                if (RuleTypeEnum.CUSTOM_RULE.getCode().equals(rule.getRuleType())) {
                    ruleTemplateService.deleteCustomTemplate(ruleInDb.getTemplate());
                    Template savedTemplate = ruleTemplateDao.saveTemplate(template);
                    Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas = templateStatisticsInputMetaService.getAndSaveTemplateStatisticsInputMeta(
                        rule.getOutputName(), rule.getFunctionType(), rule.getFunctionContent(), savedTemplate.getSaveMidTable(), savedTemplate);
                    savedTemplate.setStatisticAction(templateStatisticsInputMetas);
                    Set<TemplateOutputMeta> templateOutputMetaSet = templateOutputMetaService.getAndSaveTemplateOutputMeta(rule.getOutputName(),
                        rule.getFunctionType(), savedTemplate.getSaveMidTable(), savedTemplate);
                    savedTemplate.setTemplateOutputMetas(templateOutputMetaSet);
                    ruleInDb.setTemplate(savedTemplate);
                } else {
                    ruleInDb.setTemplate(templateInDb);
                }
                alarmConfigService.deleteByRule(ruleInDb);
                LOGGER.info("Succeed to delete all alarm_config. rule_id: {}", ruleInDb.getId());
                if (! RuleTypeEnum.CUSTOM_RULE.getCode().equals(ruleInDb.getRuleType())) {
                    ruleVariableService.deleteByRule(ruleInDb);
                }
                LOGGER.info("Succeed to delete all rule_variable. rule_id: {}", ruleInDb.getId());
                ruleDataSourceService.deleteByRule(ruleInDb);
                LOGGER.info("Succeed to delete all rule_dataSources. rule_id: {}", ruleInDb.getId());
                Rule updateRule = ruleDao.saveRule(ruleInDb);
                ruleResponse.setRuleGroupId(updateRule.getRuleGroup().getId());
                List<RuleDataSource> ruleDataSourceList = new ArrayList<>();
                for (RuleDataSource ruleDataSource : ruleDataSources) {
                    ruleDataSource.setProjectId(Long.parseLong(ruleNodeRequest.getNewProjectId()));
                    ruleDataSource.setRule(updateRule);
                    ruleDataSourceList.add(ruleDataSource);
                }
                List<AlarmConfig> alarmConfigList = new ArrayList<>();
                for (AlarmConfig alarmConfig : alarmConfigs) {
                    alarmConfig.setRule(updateRule);
                    alarmConfigList.add(alarmConfig);
                }
                List<RuleVariable> ruleVariablesList = new ArrayList<>();
                for (RuleVariable ruleVariable : ruleVariables) {
                    ruleVariable.setRule(updateRule);
                    ruleVariablesList.add(ruleVariable);
                }
                if (RuleTypeEnum.CUSTOM_RULE.getCode().equals(updateRule.getRuleType())) {
                    List<AlarmConfig> newAlarmConfigs = new ArrayList<>();
                    for (AlarmConfig alarmConfig : alarmConfigList) {
                        // Check existence of templateOutputMeta
                        TemplateOutputMeta templateOutputMetaInDb = updateRule.getTemplate().getTemplateOutputMetas().iterator().next();

                        // Generate alarmConfig and save
                        AlarmConfig newAlarmConfig = new AlarmConfig();
                        newAlarmConfig.setRule(updateRule);
                        newAlarmConfig.setTemplateOutputMeta(templateOutputMetaInDb);
                        newAlarmConfig.setCheckTemplate(alarmConfig.getCheckTemplate());
                        newAlarmConfig.setThreshold(alarmConfig.getThreshold());
                        if (alarmConfig.getCheckTemplate().equals(CheckTemplateEnum.FIXED_VALUE.getCode())) {
                            newAlarmConfig.setCompareType(alarmConfig.getCompareType());
                        }

                        newAlarmConfigs.add(newAlarmConfig);
                    }
                    updateRule.setAlarmConfigs(new HashSet<>(alarmConfigDao.saveAllAlarmConfig(newAlarmConfigs)));

                    updateRule.setRuleDataSources(new HashSet<>(ruleDataSourceDao.saveAllRuleDataSource(ruleDataSourceList)));
                } else {
                    updateRule.setAlarmConfigs(new HashSet<>(alarmConfigDao.saveAllAlarmConfig(alarmConfigList)));
                    updateRule.setRuleVariables(new HashSet<>(ruleVariableDao.saveAllRuleVariable(ruleVariablesList)));
                    updateRule.setRuleDataSources(new HashSet<>(ruleDataSourceDao.saveAllRuleDataSource(ruleDataSourceList)));
                    for (RuleDataSourceMapping ruleDataSourceMapping : ruleDataSourceMappings) {
                        ruleDataSourceMapping.setRule(updateRule);
                        ruleDataSourceMappingDao.saveRuleDataSourceMapping(ruleDataSourceMapping);
                    }
                    Rule childRule = updateRule.getChildRule();
                    if (childRule != null) {
                        LOGGER.info("Start to import updated child rule.");
                        Rule childRuleObject = objectMapper.readValue(ruleNodeRequest.getChildRuleObject(), Rule.class);
                        Template childTemplateObject = objectMapper.readValue(ruleNodeRequest.getChildTemplateObject(), Template.class);

                        childRule.setAlarm(childRuleObject.getAlarm());
                        childRule.setAbortOnFailure(childRuleObject.getAbortOnFailure());
                        childRule.setFromContent(childRuleObject.getFromContent());
                        childRule.setWhereContent(childRuleObject.getWhereContent());
                        childRule.setRuleType(childRuleObject.getRuleType());
                        childRule.setRuleTemplateName(childTemplateObject.getName());
                        childRule.setOutputName(childRuleObject.getOutputName());
                        childRule.setFunctionType(childRuleObject.getFunctionType());
                        childRule.setFunctionContent(childRuleObject.getFunctionContent());
                        Set<RuleDataSource> childRuleDataSources = objectMapper.readValue(ruleNodeRequest.getChildRuleDataSourcesObject(),
                            new TypeReference<Set<RuleDataSource>>() {});
                        Set<RuleDataSourceMapping> childRuleDataSourceMappings = objectMapper.readValue(ruleNodeRequest.getChildRuleDataSourceMappingsObject(),
                            new TypeReference<Set<RuleDataSourceMapping>>() {});
                        Set<AlarmConfig> childAlarmConfigs = objectMapper.readValue(ruleNodeRequest.getChildAlarmConfigsObject(), new TypeReference<Set<AlarmConfig>>() {});
                        Set<RuleVariable> childRuleVariables = objectMapper.readValue(ruleNodeRequest.getChildRuleVariablesObject(), new TypeReference<Set<RuleVariable>>() {});

                        Template childTemplateInDb = ruleTemplateService.checkRuleTemplate(childTemplateObject.getId());
                        if (childTemplateInDb == null) {
                            throw new UnExpectedRequestException("Child template id :[" + childTemplateObject.getId() + "] {&DOES_NOT_EXIST}");
                        }
                        importChildRule(updateRule, childRule, ruleNodeRequest.getNewProjectId(), childTemplateInDb, projectInDb, childRuleDataSources, childRuleDataSourceMappings,
                            childAlarmConfigs, childRuleVariables, true);
                        LOGGER.info("Success to import updated child rule.");
                    }
                }
            } else {
                rule.setProject(projectInDb);
                ruleGroup.setProjectId(projectInDb.getId());
                // For rule group persistence.
                List<Rule> rules = new ArrayList<>(1);
                if (RuleTypeEnum.CUSTOM_RULE.getCode().equals(rule.getRuleType())) {
                    Template saveTemplate = ruleTemplateDao.saveTemplate(template);
                    Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas = templateStatisticsInputMetaService.getAndSaveTemplateStatisticsInputMeta(
                        rule.getOutputName(), rule.getFunctionType(), rule.getFunctionContent(), saveTemplate.getSaveMidTable(), saveTemplate);
                    saveTemplate.setStatisticAction(templateStatisticsInputMetas);
                    Set<TemplateOutputMeta> templateOutputMetaSet = templateOutputMetaService.getAndSaveTemplateOutputMeta(rule.getOutputName(),
                        rule.getFunctionType(), saveTemplate.getSaveMidTable(), saveTemplate);
                    saveTemplate.setTemplateOutputMetas(templateOutputMetaSet);
                    rule.setTemplate(saveTemplate);
                } else {
                    rule.setTemplate(template);
                }
                Rule savedRule = ruleDao.saveRule(rule);
                rules.add(savedRule);
                ruleGroup.setRules(rules);
                RuleGroup ruleGroupInDb = ruleGroupDao.saveRuleGroup(ruleGroup);
                savedRule.setRuleGroup(ruleGroupInDb);
                ruleResponse.setRuleGroupId(savedRule.getRuleGroup().getId());
                List<AlarmConfig> alarmConfigList = new ArrayList<>();
                for (AlarmConfig alarmConfig : alarmConfigs) {
                    alarmConfig.setRule(savedRule);
                    alarmConfigList.add(alarmConfig);
                }

                List<RuleVariable> ruleVariablesList = new ArrayList<>();
                for (RuleVariable ruleVariable : ruleVariables) {
                    ruleVariable.setRule(savedRule);
                    ruleVariablesList.add(ruleVariable);
                }

                List<RuleDataSource> ruleDataSourceList = new ArrayList<>();
                for (RuleDataSource ruleDataSource : ruleDataSources) {
                    ruleDataSource.setProjectId(Long.parseLong(ruleNodeRequest.getNewProjectId()));
                    ruleDataSource.setRule(savedRule);
                    ruleDataSourceList.add(ruleDataSource);
                }
                if (RuleTypeEnum.CUSTOM_RULE.getCode().equals(savedRule.getRuleType())) {
                    List<AlarmConfig> newAlarmConfigs = new ArrayList<>();
                    for (AlarmConfig alarmConfig : alarmConfigList) {
                        // Check existence of templateOutputMeta
                        TemplateOutputMeta templateOutputMetaInDb = savedRule.getTemplate().getTemplateOutputMetas().iterator().next();

                        // Generate alarmConfig and save
                        AlarmConfig newAlarmConfig = new AlarmConfig();
                        newAlarmConfig.setRule(savedRule);
                        newAlarmConfig.setTemplateOutputMeta(templateOutputMetaInDb);
                        newAlarmConfig.setCheckTemplate(alarmConfig.getCheckTemplate());
                        newAlarmConfig.setThreshold(alarmConfig.getThreshold());
                        if (alarmConfig.getCheckTemplate().equals(CheckTemplateEnum.FIXED_VALUE.getCode())) {
                            newAlarmConfig.setCompareType(alarmConfig.getCompareType());
                        }

                        newAlarmConfigs.add(newAlarmConfig);
                    }
                    savedRule.setAlarmConfigs(new HashSet<>(alarmConfigDao.saveAllAlarmConfig(newAlarmConfigs)));

                    savedRule.setRuleDataSources(new HashSet<>(ruleDataSourceDao.saveAllRuleDataSource(ruleDataSourceList)));
                } else {
                    savedRule.setAlarmConfigs(new HashSet<>(alarmConfigDao.saveAllAlarmConfig(alarmConfigList)));
                    savedRule.setRuleVariables(new HashSet<>(ruleVariableDao.saveAllRuleVariable(ruleVariablesList)));
                    savedRule.setRuleDataSources(new HashSet<>(ruleDataSourceDao.saveAllRuleDataSource(ruleDataSourceList)));
                    for (RuleDataSourceMapping ruleDataSourceMapping : ruleDataSourceMappings) {
                        ruleDataSourceMapping.setRule(savedRule);
                        ruleDataSourceMappingDao.saveRuleDataSourceMapping(ruleDataSourceMapping);
                    }
                    if (ruleNodeRequest.getChildRuleObject() != null) {
                        Rule childRule = objectMapper.readValue(ruleNodeRequest.getChildRuleObject(), Rule.class);
                        if (childRule != null) {
                            LOGGER.info("Start to import child rule.");
                            Template childTemplate = objectMapper.readValue(ruleNodeRequest.getChildTemplateObject(), Template.class);
                            Set<RuleDataSource> childRuleDataSources = objectMapper.readValue(ruleNodeRequest.getChildRuleDataSourcesObject(),
                                new TypeReference<Set<RuleDataSource>>() {});
                            Set<RuleDataSourceMapping> childRuleDataSourceMappings = objectMapper.readValue(ruleNodeRequest.getChildRuleDataSourceMappingsObject(),
                                new TypeReference<Set<RuleDataSourceMapping>>() {});
                            Set<AlarmConfig> childAlarmConfigs = objectMapper.readValue(ruleNodeRequest.getChildAlarmConfigsObject(), new TypeReference<Set<AlarmConfig>>() {});
                            Set<RuleVariable> childRuleVariables = objectMapper.readValue(ruleNodeRequest.getChildRuleVariablesObject(), new TypeReference<Set<RuleVariable>>() {});

                            importChildRule(savedRule, childRule, ruleNodeRequest.getNewProjectId(), childTemplate, projectInDb, childRuleDataSources, childRuleDataSourceMappings,
                                childAlarmConfigs, childRuleVariables, false);
                            LOGGER.info("Success to import child rule.");
                        }
                    }
                }

            }


            return new GeneralResponse<>("200", "{&IMPORT_RULE_SUCCESSFULLY}", ruleResponse);
        } catch (IOException | RoleNotFoundException e) {
            LOGGER.error("Fail to set rule because of json opeartions. Exception is {}", e);
        }
        return new GeneralResponse<>("500", "{&FAILED_TO_IMPORT_RULE}", null);
    }

    private void importChildRule(Rule parentRule, Rule rule, String newProjectId, Template template, Project projectInDb, Set<RuleDataSource> ruleDataSources,
        Set<RuleDataSourceMapping> ruleDataSourceMappings, Set<AlarmConfig> alarmConfigs, Set<RuleVariable> ruleVariables, boolean modify) throws UnExpectedRequestException {
        if (modify) {
            alarmConfigService.deleteByRule(rule);
            LOGGER.info("Succeed to delete all alarm_config of child rule. rule_id: {}", rule.getId());
            ruleVariableService.deleteByRule(rule);
            LOGGER.info("Succeed to delete all rule_variable of child rule. rule_id: {}", rule.getId());
            ruleDataSourceService.deleteByRule(rule);
            LOGGER.info("Succeed to delete all rule_dataSources of child rule. rule_id: {}", rule.getId());
            Rule updateRule = ruleDao.saveRule(rule);
            List<RuleDataSource> ruleDataSourceList = new ArrayList<>();
            for (RuleDataSource ruleDataSource : ruleDataSources) {
                ruleDataSource.setProjectId(Long.parseLong(newProjectId));
                ruleDataSource.setRule(updateRule);
                ruleDataSourceList.add(ruleDataSource);
            }
            List<AlarmConfig> alarmConfigList = new ArrayList<>();
            for (AlarmConfig alarmConfig : alarmConfigs) {
                alarmConfig.setRule(updateRule);
                alarmConfigList.add(alarmConfig);
            }
            List<RuleVariable> ruleVariablesList = new ArrayList<>();
            for (RuleVariable ruleVariable : ruleVariables) {
                ruleVariable.setRule(updateRule);
                ruleVariablesList.add(ruleVariable);
            }
            updateRule.setAlarmConfigs(new HashSet<>(alarmConfigDao.saveAllAlarmConfig(alarmConfigList)));
            updateRule.setRuleVariables(new HashSet<>(ruleVariableDao.saveAllRuleVariable(ruleVariablesList)));
            updateRule.setRuleDataSources(new HashSet<>(ruleDataSourceDao.saveAllRuleDataSource(ruleDataSourceList)));
            for (RuleDataSourceMapping ruleDataSourceMapping : ruleDataSourceMappings) {
                ruleDataSourceMapping.setRule(updateRule);
                ruleDataSourceMappingDao.saveRuleDataSourceMapping(ruleDataSourceMapping);
            }
            updateRule.setParentRule(parentRule);
            parentRule.setChildRule(updateRule);
        } else {
            rule.setProject(projectInDb);
            rule.setTemplate(template);
            Rule savedRule = ruleDao.saveRule(rule);

            List<AlarmConfig> alarmConfigList = new ArrayList<>();
            for (AlarmConfig alarmConfig : alarmConfigs) {
                alarmConfig.setRule(savedRule);
                alarmConfigList.add(alarmConfig);
            }

            List<RuleVariable> ruleVariablesList = new ArrayList<>();
            for (RuleVariable ruleVariable : ruleVariables) {
                ruleVariable.setRule(savedRule);
                ruleVariablesList.add(ruleVariable);
            }

            List<RuleDataSource> ruleDataSourceList = new ArrayList<>();
            for (RuleDataSource ruleDataSource : ruleDataSources) {
                ruleDataSource.setProjectId(Long.parseLong(newProjectId));
                ruleDataSource.setRule(savedRule);
                ruleDataSourceList.add(ruleDataSource);
            }

            savedRule.setAlarmConfigs(new HashSet<>(alarmConfigDao.saveAllAlarmConfig(alarmConfigList)));
            savedRule.setRuleVariables(new HashSet<>(ruleVariableDao.saveAllRuleVariable(ruleVariablesList)));
            savedRule.setRuleDataSources(new HashSet<>(ruleDataSourceDao.saveAllRuleDataSource(ruleDataSourceList)));
            for (RuleDataSourceMapping ruleDataSourceMapping : ruleDataSourceMappings) {
                ruleDataSourceMapping.setRule(savedRule);
                ruleDataSourceMappingDao.saveRuleDataSourceMapping(ruleDataSourceMapping);
            }
            savedRule.setParentRule(parentRule);
            parentRule.setChildRule(savedRule);
        }

    }

    @Override
    public void checkRuleName(String ruleName, Project project, Long ruleId) throws UnExpectedRequestException {
        List<Rule> rules = ruleDao.findByProject(project);
        for (Rule rule : rules) {
            if (rule.getName().equals(ruleName)) {
                if (ruleId != null && !rule.getId().equals(ruleId)) {
                    throw new UnExpectedRequestException("rule_name {&ALREADY_EXIST}");
                }
                if (ruleId == null) {
                    throw new UnExpectedRequestException("rule_name {&ALREADY_EXIST}");
                }
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
     * @param requests
     * @param template
     */
    private void checkRuleArgumentSize(List<TemplateArgumentRequest> requests, Template template) throws UnExpectedRequestException {
        List<Long> midTableArgumentId = template.getTemplateMidTableInputMetas().stream().filter(t -> TemplateMidTableUtil.shouldResponse(t))
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
        for (TemplateMidTableInputMeta templateMidTableInputMeta : template.getTemplateMidTableInputMetas()){
            if (dataSourceRequests.size() != 1) {
                throw new UnExpectedRequestException("{&ONLY_SINGLE_DATASOURCE_CAN_BE_AUTO_ADAPTED}");
            }
            DataSourceRequest dataSourceRequest = dataSourceRequests.get(0);

            Map<String, String> autoAdaptValue = autoArgumentAdapter.getAdaptValue(templateMidTableInputMeta, dataSourceRequest, templateArgumentRequests);
            ruleVariables.add(new RuleVariable(rule, InputActionStepEnum.TEMPLATE_INPUT_META.getCode(), templateMidTableInputMeta,
                                                                null, autoAdaptValue));
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

    private void checkDataSourceNumber(Template template, List<DataSourceRequest> requests) throws UnExpectedRequestException {
        // Check cluster, database, table, field num of template
        Integer clusterNum = template.getClusterNum();
        Integer dbNum = template.getDbNum();
        Integer tableNum = template.getTableNum();
        Integer fieldNum = template.getFieldNum();

        Integer requestClusterNum = 0;
        Integer requestDbNum = 0;
        Integer requestTableNum = 0;
        Integer requestFieldNum = 0;
        for (DataSourceRequest request : requests) {
            requestClusterNum++;
            requestDbNum++;
            requestTableNum++;
            requestFieldNum += request.getColNames().size();
        }

        if (clusterNum != -1 && !clusterNum.equals(requestClusterNum)) {
            throw new UnExpectedRequestException(String.format("{&TEMPLATE_NAME}:%s,{&ONLY_CONFIG}%d {&CLUSTER_NUMS_BUT_NOW_HAS_CONFIG_NUM_IS}:%d", template.getName(), clusterNum, requestClusterNum));
        }
        if (dbNum != -1 && !dbNum.equals(requestDbNum)) {
            throw new UnExpectedRequestException(String.format("{&TEMPLATE_NAME}:%s,{&ONLY_CONFIG}%d {&DATABASE_NUMS_BUT_NOW_HAS_CONFIG_NUM_IS}:%d", template.getName(), dbNum, requestDbNum));
        }
        if (tableNum != -1 && !tableNum.equals(requestTableNum)) {
            throw new UnExpectedRequestException(String.format("{&TEMPLATE_NAME}:%s,{&ONLY_CONFIG}%d {&TABLE_NUMS_BUT_NOW_HAS_CONFIG_NUM_IS}:%d", template.getName(), tableNum, requestTableNum));
        }
        if (fieldNum != -1 && !fieldNum.equals(requestFieldNum)) {
            throw new UnExpectedRequestException(String.format("{&TEMPLATE_NAME}:%s,{&ONLY_CONFIG}%d {&COLUMN_NUMS_BUT_NOW_HAS_CONFIG_NUM_IS}:%d", template.getName(), fieldNum, requestFieldNum));
        }
    }

    /**
     * Check cluster name supported
     * @param datasource
     * @throws UnExpectedRequestException
     */
    private void checkDataSourceClusterLimit(List<DataSourceRequest> datasource)  throws UnExpectedRequestException {
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
        List<TemplateArgumentRequest> request =  requests.stream().filter(r -> r.getArgumentStep().equals(actionStep) && r.getArgumentId().equals(id)).collect(Collectors.toList());
        if (request.size() != 1) {
            throw new UnExpectedRequestException("{&CAN_NOT_FIND_SUITABLE_REQUEST}");
        }
        TemplateArgumentRequest.checkRequest(request.get(0));

        return request.get(0);
    }

    private void ruleToNodeResponse(Rule rule, RuleNodeResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        // Project info.
        response.setProjectId(objectMapper.writeValueAsString(rule.getProject().getId()));
        response.setProjectName(objectMapper.writeValueAsString(rule.getProject().getName()));
        // Rule, template, rule group.
        response.setRuleObject(objectMapper.writeValueAsString(rule));
        response.setTemplateObject(objectMapper.writeValueAsString(rule.getTemplate()));
        response.setRuleGroupObject(objectMapper.writeValueAsString(rule.getRuleGroup()));
        // Rule data source.
        response.setRuleDataSourcesObject(objectMapper.writerWithType(new TypeReference<Set<RuleDataSource>>() {})
            .writeValueAsString(rule.getRuleDataSources()));
        response.setRuleDataSourceMappingsObject(objectMapper.writerWithType(new TypeReference<Set<RuleDataSourceMapping>>() {})
            .writeValueAsString(rule.getRuleDataSourceMappings()));
        // Rule check meta info.
        response.setAlarmConfigsObject(objectMapper.writerWithType(new TypeReference<Set<AlarmConfig>>() {}).writeValueAsString(rule.getAlarmConfigs()));
        response.setRuleVariablesObject(objectMapper.writerWithType(new TypeReference<Set<RuleVariable>>() {}).writeValueAsString(rule.getRuleVariables()));
        // Template check meta info.
        response.setTemplateTemplateMidTableInputMetaObject(objectMapper.writerWithType(new TypeReference<Set<TemplateMidTableInputMeta>>() {})
            .writeValueAsString(rule.getTemplate().getTemplateMidTableInputMetas()));
        response.setTemplateTemplateStatisticsInputMetaObject(objectMapper.writerWithType(new TypeReference<Set<TemplateStatisticsInputMeta>>() { })
            .writeValueAsString(rule.getTemplate().getStatisticAction()));
        response.setTemplateTemplateOutputMetaObject(objectMapper.writerWithType(new TypeReference<Set<TemplateOutputMeta>>() {})
            .writeValueAsString(rule.getTemplate().getTemplateOutputMetas()));

        // Child rule
        if (rule.getChildRule() != null) {
            response.setChildRuleObject(objectMapper.writeValueAsString(rule.getChildRule()));
            response.setChildTemplateObject(objectMapper.writeValueAsString(rule.getChildRule().getTemplate()));
            // Rule data source.
            response.setChildRuleDataSourcesObject(objectMapper.writerWithType(new TypeReference<Set<RuleDataSource>>() {})
                .writeValueAsString(rule.getChildRule().getRuleDataSources()));
            response.setChildRuleDataSourceMappingsObject(objectMapper.writerWithType(new TypeReference<Set<RuleDataSourceMapping>>() {})
                .writeValueAsString(rule.getChildRule().getRuleDataSourceMappings()));
            // Rule check meta info.
            response.setChildAlarmConfigsObject(objectMapper.writerWithType(new TypeReference<Set<AlarmConfig>>() {}).writeValueAsString(rule.getChildRule().getAlarmConfigs()));
            response.setChildRuleVariablesObject(objectMapper.writerWithType(new TypeReference<Set<RuleVariable>>() {}).writeValueAsString(rule.getChildRule().getRuleVariables()));
            // Template check meta info.
            response.setChildTemplateTemplateMidTableInputMetaObject(objectMapper.writerWithType(new TypeReference<Set<TemplateMidTableInputMeta>>() {})
                .writeValueAsString(rule.getChildRule().getTemplate().getTemplateMidTableInputMetas()));
            response.setChildTemplateTemplateStatisticsInputMetaObject(objectMapper.writerWithType(new TypeReference<Set<TemplateStatisticsInputMeta>>() { })
                .writeValueAsString(rule.getChildRule().getTemplate().getStatisticAction()));
            response.setChildTemplateTemplateOutputMetaObject(objectMapper.writerWithType(new TypeReference<Set<TemplateOutputMeta>>() {})
                .writeValueAsString(rule.getChildRule().getTemplate().getTemplateOutputMetas()));
        }
    }

}
