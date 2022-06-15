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
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.service.ProjectEventService;
import com.webank.wedatasphere.qualitis.rule.adapter.AutoArgumentAdapter;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.AlarmConfigDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceMappingDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleTemplateDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleVariableDao;
import com.webank.wedatasphere.qualitis.rule.request.*;
import com.webank.wedatasphere.qualitis.rule.response.RuleDetailResponse;
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
import com.webank.wedatasphere.qualitis.submitter.impl.ExecutionManagerImpl;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import org.apache.commons.lang.StringUtils;
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
    private RuleTemplateDao ruleTemplateDao;
    @Autowired
    private RuleVariableDao ruleVariableDao;

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
    private ProjectDao projectDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectEventService projectEventService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleServiceImpl.class);

    private HttpServletRequest httpServletRequest;
    public RuleServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> addRule(AddRuleRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        return addRuleReal(request, loginUser);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> addRuleForOuter(AbstractAddRequest request, String loginUser)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        return addRuleReal((AddRuleRequest) request, loginUser);
    }

    private GeneralResponse<RuleResponse> addRuleReal(AddRuleRequest request, String loginUser)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
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
        Project projectInDb = projectService.checkProjectExistence(request.getProjectId(),
            loginUser);
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);
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
        setBasicInfo(newRule, ruleGroup, templateInDb, projectInDb, request, loginUser);
        String csId = request.getCsId();
        // For workflow node context.
        boolean cs = false;
        if (StringUtils.isNotBlank(csId))  {
            newRule.setCsId(csId);
            cs = true;
        }

        Rule savedRule = ruleDao.saveRule(newRule);
        LOGGER.info("Succeed to save rule, rule_id: {}", savedRule.getId());

        // Save alarm_config,ruleVariable and ruleDataSource
        // Generate rule variable and save
        LOGGER.info("Start to generate and save rule_variable.");
        // For repeat fps table name when concurrent task is running.
        addUuid(request.getDatasource());

        List<RuleVariable> ruleVariables = autoAdaptArgumentAndGetRuleVariable(templateInDb, request.getDatasource(),
            request.getTemplateArgumentRequests(), savedRule);
        List<RuleVariable> savedRuleVariables = ruleVariableService.saveRuleVariable(ruleVariables);
        LOGGER.info("Succeed to save rule_variables, rule_variables: {}", savedRuleVariables);
        List<AlarmConfig> savedAlarmConfigs = new ArrayList<>();
        if (request.getAlarm()) {
            savedAlarmConfigs  = alarmConfigService.checkAndSaveAlarmVariable(request.getAlarmVariable(), savedRule);
            LOGGER.info("Succeed to save alarm_configs, alarm_configs: {}", savedAlarmConfigs);
        }
        List<RuleDataSource> savedRuleDataSource = ruleDataSourceService.checkAndSaveRuleDataSource(request.getDatasource(), savedRule, cs, loginUser);
        LOGGER.info("Succeed to save rule_dataSources, rule_dataSources: {}, context service: {}", savedRuleDataSource, cs);

        savedRule.setRuleVariables(new HashSet<>(savedRuleVariables));
        savedRule.setAlarmConfigs(new HashSet<>(savedAlarmConfigs));
        savedRule.setRuleDataSources(new HashSet<>(savedRuleDataSource));
        // Update rule count of datasource
        ruleDataSourceService.updateRuleDataSourceCount(savedRule, 1);

        RuleResponse response = new RuleResponse(savedRule);
        LOGGER.info("Succeed to add rule, rule_id: {}", response.getRuleId());
        return new GeneralResponse<>("200", "{&ADD_RULE_SUCCESSFULLY}", response);
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
    public GeneralResponse<?> deleteRule(DeleteRuleRequest request, String loginUser) throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check Arguments
        DeleteRuleRequest.checkRequest(request);
        // Check existence of rule
        Rule ruleInDb = ruleDao.findById(request.getRuleId());
        if (ruleInDb == null || ! ruleInDb.getRuleType().equals(RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode())) {
            throw new UnExpectedRequestException("Rule id [" + request.getRuleId() + "]) {&IS_NOT_A_RULE_WITH_TEMPLATE}");
        }
        // Check existence of project
        Project projectInDb = projectService.checkProjectExistence(ruleInDb.getProject().getId(),
            loginUser);
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);
        // Update rule count of datasource
        ruleDataSourceService.updateRuleDataSourceCount(ruleInDb, -1);
        // Record project event.
//        projectEventService.record(projectInDb.getId(), loginUser, "delete", "rule[name= " + ruleInDb.getName() + "].", EventTypeEnum.MODIFY_PROJECT.getCode());

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
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> modifyRuleDetail(ModifyRuleRequest request)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        return modifyRuleDetailReal(request, loginUser);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> modifyRuleDetailForOuter(ModifyRuleRequest modifyRuleRequest, String userName)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        return modifyRuleDetailReal(modifyRuleRequest, userName);
    }

    private GeneralResponse<RuleResponse> modifyRuleDetailReal(ModifyRuleRequest request, String loginUser)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check Arguments
        ModifyRuleRequest.checkRequest(request);
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
        LOGGER.info("Succeed to find rule. rule_id: {}", ruleInDb.getId());
        // For workflow node context.
        String csId = request.getCsId();
        boolean cs = false;
        if (StringUtils.isNotBlank(csId)) {
            cs = true;
        }
        ruleInDb.setCsId(csId);
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
        // Basic rule info.
        setBasicInfo(ruleInDb, templateInDb, request, loginUser);
        // Delete all alarm_config,rule_variable,rule_datasource
        alarmConfigService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all alarm_config. rule_id: {}", ruleInDb.getId());
        ruleVariableService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all rule_variable. rule_id: {}", ruleInDb.getId());
        ruleDataSourceService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all rule_dataSources. rule_id: {}", ruleInDb.getId());
        // Update rule count of datasource
        ruleDataSourceService.updateRuleDataSourceCount(ruleInDb, -1);
        Rule savedRule = ruleDao.saveRule(ruleInDb);
        LOGGER.info("Succeed to save rule. rule_id: {}", savedRule.getId());
        // For repeat fps table name when concurrent task is running.
        addUuid(request.getDatasource());
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
        List<RuleDataSource> savedRuleDataSource = ruleDataSourceService.checkAndSaveRuleDataSource(request.getDatasource(), savedRule, cs, loginUser);
        LOGGER.info("Succeed to save rule_dataSources, rule_dataSources: {}", savedRuleDataSource);

        savedRule.setRuleVariables(new HashSet<>(savedRuleVariables));
        savedRule.setAlarmConfigs(new HashSet<>(savedAlarmConfigs));
        savedRule.setRuleDataSources(new HashSet<>(savedRuleDataSource));
        // Update rule count of datasource
        ruleDataSourceService.updateRuleDataSourceCount(ruleInDb, 1);

        RuleResponse response = new RuleResponse(savedRule);
        LOGGER.info("Succeed to modify rule. response: {}", response);
        // Record project event.
//        projectEventService.record(request.getProjectId(), loginUser, "modify", "rule[name= " + savedRule.getName() + "].", EventTypeEnum.MODIFY_PROJECT.getCode());
        return new GeneralResponse<>("200", "{&MODIFY_RULE_SUCCESSFULLY}", response);
    }

    private void setBasicInfo(Rule newRule, RuleGroup ruleGroup, Template templateInDb, Project projectInDb, AddRuleRequest request, String loginUser) {
        String nowDate = ExecutionManagerImpl.PRINT_TIME_FORMAT.format(new Date());
        newRule.setTemplate(templateInDb);
        newRule.setRuleTemplateName(templateInDb.getName());
        newRule.setName(request.getRuleName());
        newRule.setCnName(request.getRuleCnName());
        newRule.setDetail(request.getRuleDetail());
        newRule.setAlarm(request.getAlarm());
        newRule.setProject(projectInDb);
        newRule.setRuleType(RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode());
        newRule.setRuleGroup(ruleGroup);
        newRule.setAbortOnFailure(request.getAbortOnFailure());
        newRule.setCreateUser(loginUser);
        newRule.setCreateTime(nowDate);
        newRule.setDeleteFailCheckResult(request.getDeleteFailCheckResult());
        newRule.setSpecifyStaticStartupParam(request.getSpecifyStaticStartupParam());
        newRule.setStaticStartupParam(request.getStaticStartupParam());
    }
    private void setBasicInfo(Rule ruleInDb, Template templateInDb, ModifyRuleRequest request, String loginUser) {
        String nowDate = ExecutionManagerImpl.PRINT_TIME_FORMAT.format(new Date());
        ruleInDb.setTemplate(templateInDb);
        ruleInDb.setName(request.getRuleName());
        ruleInDb.setDetail(request.getRuleDetail());
        ruleInDb.setCnName(request.getRuleCnName());
        ruleInDb.setAlarm(request.getAlarm());
        ruleInDb.setAbortOnFailure(request.getAbortOnFailure());
        ruleInDb.setRuleTemplateName(templateInDb.getName());
        ruleInDb.setModifyUser(loginUser);
        ruleInDb.setModifyTime(nowDate);
        ruleInDb.setDeleteFailCheckResult(request.getDeleteFailCheckResult());
        ruleInDb.setSpecifyStaticStartupParam(request.getSpecifyStaticStartupParam());
        ruleInDb.setStaticStartupParam(request.getStaticStartupParam());
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

        Long projectInDbId = ruleInDb.getProject().getId();
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        projectService.checkProjectExistence(projectInDbId, loginUser);

        LOGGER.info("Succeed to find rule. rule_id: {}", ruleInDb.getId());

        RuleDetailResponse response = new RuleDetailResponse(ruleInDb);
        LOGGER.info("Succeed to get rule detail. rule_id: {}", ruleId);
        return new GeneralResponse<>("200", "{&GET_RULE_DETAIL_SUCCESSFULLY}", response);
    }

    @Override
    public void checkRuleName(String ruleName, Project project, Long ruleId) throws UnExpectedRequestException {
        List<Rule> rules = ruleDao.findByProject(project);
        for (Rule rule : rules) {
            if (rule.getName().equals(ruleName)) {
                if (ruleId != null && ! rule.getId().equals(ruleId)) {
                    throw new UnExpectedRequestException("Rule name {&ALREADY_EXIST}");
                }
                if (ruleId == null) {
                    throw new UnExpectedRequestException("Rule name {&ALREADY_EXIST}");
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
            if (! requestId.contains(id)) {
                throw new UnExpectedRequestException("{&MISSING_ARGUMENT_ID_OF_MID_TABLE_ACTION}: [" + id + "]");
            }
        }

        for (Long id : statisticsArgumentId) {
            if (! requestId.contains(id)) {
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

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> addRuleForUpload(AddRuleRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        return addRuleReal(request, loginUser);
    }

}
