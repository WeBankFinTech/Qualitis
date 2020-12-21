package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.ProjectTypeEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.project.service.OuterWorkflowService;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.constant.CheckTemplateEnum;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.AlarmConfigDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceMappingDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleTemplateDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleVariableDao;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceMapping;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.entity.RuleVariable;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateOutputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;
import com.webank.wedatasphere.qualitis.rule.request.DeleteRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.RuleNodeRequest;
import com.webank.wedatasphere.qualitis.rule.response.RuleNodeResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import com.webank.wedatasphere.qualitis.rule.service.AlarmConfigService;
import com.webank.wedatasphere.qualitis.rule.service.RuleDataSourceMappingService;
import com.webank.wedatasphere.qualitis.rule.service.RuleDataSourceService;
import com.webank.wedatasphere.qualitis.rule.service.RuleNodeService;
import com.webank.wedatasphere.qualitis.rule.service.RuleService;
import com.webank.wedatasphere.qualitis.rule.service.RuleTemplateService;
import com.webank.wedatasphere.qualitis.rule.service.RuleVariableService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateOutputMetaService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateStatisticsInputMetaService;
import com.webank.wedatasphere.qualitis.service.UserService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.management.relation.RoleNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author allenzhou
 */
@Service
public class RuleNodeServiceImpl implements RuleNodeService {

    @Autowired
    private RuleService ruleService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private AlarmConfigService alarmConfigService;
    @Autowired
    private RuleVariableService ruleVariableService;
    @Autowired
    private RuleTemplateService ruleTemplateService;
    @Autowired
    private OuterWorkflowService outerWorkflowService;
    @Autowired
    private RuleDataSourceService ruleDataSourceService;
    @Autowired
    private RuleDataSourceMappingService ruleDataSourceMappingService;
    @Autowired
    private TemplateOutputMetaService templateOutputMetaService;
    @Autowired
    private TemplateStatisticsInputMetaService templateStatisticsInputMetaService;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private AlarmConfigDao alarmConfigDao;
    @Autowired
    private RuleVariableDao ruleVariableDao;
    @Autowired
    private RuleDataSourceDao ruleDataSourceDao;
    @Autowired
    private RuleDataSourceMappingDao ruleDataSourceMappingDao;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private RuleGroupDao ruleGroupDao;
    @Autowired
    private RuleTemplateDao ruleTemplateDao;


    private static final Logger LOGGER = LoggerFactory.getLogger(RuleNodeServiceImpl.class);

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> deleteRule(DeleteRuleRequest request) throws UnExpectedRequestException {
        // Check Arguments
        DeleteRuleRequest.checkRequest(request);
        Long ruleGroupId = request.getRuleGroupId();
        if (ruleGroupId == null || StringUtils.isBlank(ruleGroupId.toString())) {
            throw new UnExpectedRequestException("Rule group id is invalid.");
        }
        // Delete any rule when find rule by rule group ID
        LOGGER.info("Check existence of rule before deleting. Rule group id: {}.", ruleGroupId.toString());
        List<Rule> rules = ruleDao.findByRuleGroup(ruleGroupDao.findById(request.getRuleGroupId()));
        if(rules.isEmpty() || rules.get(0) == null) {
            throw new UnExpectedRequestException("Rule group [id = " + request.getRuleGroupId() + "] does not have rules.");
        }
        Rule ruleInDb = rules.get(0);
        LOGGER.info("Start to delete rule. Rule group id: {}.", ruleGroupId.toString());
        return ruleService.deleteRuleReal(ruleInDb);
    }

    @Override
    public GeneralResponse<RuleResponse> modifyRuleWithContextService(ModifyRuleRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "ModifyRuleRequest");
        Project projectInDb = projectDao.findById(request.getProjectId());
        if (projectInDb == null) {
            projectInDb = projectDao.findByName(request.getProjectName());
            if (projectInDb == null) {
                throw new UnExpectedRequestException("Project is not exist.");
            }
        }

        List<Rule> rules = ruleDao.findByProject(projectInDb);
        if(rules.isEmpty() || rules.get(0) == null) {
            throw new UnExpectedRequestException("Project [id = " + projectInDb.getId() + "] does not have rules.");
        }
        for (Rule ruleInDb : rules) {
            ruleDao.saveRule(ruleInDb);
            LOGGER.info("Succeed to save rule. rule_id: {}", ruleInDb.getId());
        }
        return new GeneralResponse<>("200", "{&MODIFY_RULE_SUCCESSFULLY}", null);
    }

    @Override
    public GeneralResponse<RuleNodeResponse> exportRuleByGroupId(Long ruleGroupId) throws UnExpectedRequestException {
        // Check existence of ruleGroup
        RuleGroup ruleGroup = ruleGroupDao.findById(ruleGroupId);
        if (ruleGroup == null) {
            throw new UnExpectedRequestException("Rule group [Id = " + ruleGroupId + "] does not exist.");
        }
        LOGGER.info("Succeed to find rule group. Rule group id: {}.", ruleGroup.getId());
        RuleNodeResponse response = new RuleNodeResponse();
        try {
            List<Rule> rules = ruleDao.findByRuleGroup(ruleGroup);
            if(rules.isEmpty() || rules.get(0) == null) {
                throw new UnExpectedRequestException("Rule group [id = " + ruleGroup.getId() + "] does not have rules.");
            }
            Rule ruleInDb = rules.get(0);
            ruleNodeResponse(ruleInDb, response);
        } catch (IOException e) {
            LOGGER.error("Failed to export rule because of JSON serialization opeartions. Exception is {}", e);
            return new GeneralResponse<>("500", "{&FAILED_TO_EXPORT_RULE}", response);
        }
        LOGGER.info("Succeed to export rule. Rule info: {}", response.toString());
        return new GeneralResponse<>("200", "{&EXPORT_RULE_SUCCESSFULLY}", response);
    }

    /**
     *  Parse the rule information and encapsulate it into the response body.
     * @param rule
     * @param response
     * @throws IOException
     */
    private void ruleNodeResponse(Rule rule, RuleNodeResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        // Project info.
        response.setProjectId(objectMapper.writeValueAsString(rule.getProject().getId()));
        response.setProjectName(objectMapper.writeValueAsString(rule.getProject().getName()));
        // Rule, Template, RuleGroup.
        response.setRuleObject(objectMapper.writeValueAsString(rule));
        response.setTemplateObject(objectMapper.writeValueAsString(rule.getTemplate()));
        response.setRuleGroupObject(objectMapper.writeValueAsString(rule.getRuleGroup()));
        // RuleDataSource.
        response.setRuleDataSourcesObject(objectMapper.writerWithType(new TypeReference<Set<RuleDataSource>>() {})
            .writeValueAsString(rule.getRuleDataSources()));
        response.setRuleDataSourceMappingsObject(objectMapper.writerWithType(new TypeReference<Set<RuleDataSourceMapping>>() {})
            .writeValueAsString(rule.getRuleDataSourceMappings()));

        // Rule check meta info.
        response.setAlarmConfigsObject(objectMapper.writerWithType(new TypeReference<Set<AlarmConfig>>() {}).writeValueAsString(rule.getAlarmConfigs()));
        response.setRuleVariablesObject(objectMapper.writerWithType(new TypeReference<Set<RuleVariable>>() {}).writeValueAsString(rule.getRuleVariables()));
        // Template check meta info.
        response.setTemplateTemplateStatisticsInputMetaObject(objectMapper.writerWithType(new TypeReference<Set<TemplateStatisticsInputMeta>>() {})
            .writeValueAsString(rule.getTemplate().getStatisticAction()));
        response.setTemplateTemplateMidTableInputMetaObject(objectMapper.writerWithType(new TypeReference<Set<TemplateMidTableInputMeta>>() {})
            .writeValueAsString(rule.getTemplate().getTemplateMidTableInputMetas()));
        response.setTemplateTemplateOutputMetaObject(objectMapper.writerWithType(new TypeReference<Set<TemplateOutputMeta>>() {})
            .writeValueAsString(rule.getTemplate().getTemplateOutputMetas()));

        // Child Rule.
        if (rule.getChildRule() != null) {
            // Rule, Template.
            response.setChildRuleObject(objectMapper.writeValueAsString(rule.getChildRule()));
            response.setChildTemplateObject(objectMapper.writeValueAsString(rule.getChildRule().getTemplate()));
            // RuleDataSource.
            response.setChildRuleDataSourcesObject(objectMapper.writerWithType(new TypeReference<Set<RuleDataSource>>() {})
                .writeValueAsString(rule.getChildRule().getRuleDataSources()));
            response.setChildRuleDataSourceMappingsObject(objectMapper.writerWithType(new TypeReference<Set<RuleDataSourceMapping>>() {})
                .writeValueAsString(rule.getChildRule().getRuleDataSourceMappings()));
            // Child rule check meta info.
            response.setChildAlarmConfigsObject(objectMapper.writerWithType(new TypeReference<Set<AlarmConfig>>() {})
                .writeValueAsString(rule.getChildRule().getAlarmConfigs()));
            response.setChildRuleVariablesObject(objectMapper.writerWithType(new TypeReference<Set<RuleVariable>>() {})
                .writeValueAsString(rule.getChildRule().getRuleVariables()));
            // Child template check meta info.
            response.setChildTemplateTemplateOutputMetaObject(objectMapper.writerWithType(new TypeReference<Set<TemplateOutputMeta>>() {})
                .writeValueAsString(rule.getChildRule().getTemplate().getTemplateOutputMetas()));
            response.setChildTemplateTemplateMidTableInputMetaObject(objectMapper.writerWithType(new TypeReference<Set<TemplateMidTableInputMeta>>() {})
                .writeValueAsString(rule.getChildRule().getTemplate().getTemplateMidTableInputMetas()));
            response.setChildTemplateTemplateStatisticsInputMetaObject(objectMapper.writerWithType(new TypeReference<Set<TemplateStatisticsInputMeta>>() { })
                .writeValueAsString(rule.getChildRule().getTemplate().getStatisticAction()));
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class}, propagation = Propagation.REQUIRED)
    public GeneralResponse<RuleResponse> importRule(RuleNodeRequest ruleNodeRequest)
        throws UnExpectedRequestException, IOException, IllegalArgumentException {
        ObjectMapper objectMapper = new ObjectMapper();
        // Rule, Template, RuleGroup.
        Rule rule = objectMapper.readValue(ruleNodeRequest.getRuleObject(), Rule.class);
        Template template = objectMapper.readValue(ruleNodeRequest.getTemplateObject(), Template.class);
        RuleGroup ruleGroup = objectMapper.readValue(ruleNodeRequest.getRuleGroupObject(), RuleGroup.class);
        // RuleDataSource.
        Set<RuleDataSource> ruleDataSources = objectMapper.readValue(ruleNodeRequest.getRuleDataSourcesObject(),
            new TypeReference<Set<RuleDataSource>>() {});
        Set<RuleDataSourceMapping> ruleDataSourceMappings = objectMapper.readValue(ruleNodeRequest.getRuleDataSourceMappingsObject(),
            new TypeReference<Set<RuleDataSourceMapping>>() {});
        // Rule check meta info.
        Set<AlarmConfig> alarmConfigs = objectMapper.readValue(ruleNodeRequest.getAlarmConfigsObject(), new TypeReference<Set<AlarmConfig>>() {});
        Set<RuleVariable> ruleVariables = objectMapper.readValue(ruleNodeRequest.getRuleVariablesObject(), new TypeReference<Set<RuleVariable>>() {});

        LOGGER.info("Import basic information: {}", new StringBuilder().append("\n")
                .append(rule.getName()).append("\n")
                .append(template.getName()).append("\n")
                .toString());

        RuleResponse ruleResponse = new RuleResponse();
        // Find project in produce center.
        Project projectInDb = projectDao.findById(Long.parseLong(ruleNodeRequest.getNewProjectId()));
        if (projectInDb != null) {
            LOGGER.info("Project info : {}", projectInDb.toString());
        } else {
            // For special case:
            // A project already exists in DSS. when creating a workflow in this project and publish it, Qualitis service cannot find the project.
            // Because Qualitis did not run when this project had been created in DSS.
            LOGGER.info("Start to create workflow project. Project[id = {}] is not exist.", ruleNodeRequest.getNewProjectId());
            String userName = ruleNodeRequest.getUserName();
            User currentUser = userDao.findByUsername(userName);
            if (currentUser == null) {
                LOGGER.info("Start to create user. User name is {}", ruleNodeRequest.getUserName());
                try {
                    userService.autoAddUser(userName);
                    currentUser = userDao.findByUsername(userName);
                } catch (RoleNotFoundException e) {
                    LOGGER.error("Role cannot be found. Exception: {}", e);
                }
            }

            Project newProject = projectService.addProjectReal(currentUser.getId(), ruleNodeRequest.getProjectName(), "Auto created.");
            newProject.setProjectType(ProjectTypeEnum.WORKFLOW_PROJECT.getCode());
            projectInDb = projectDao.saveProject(newProject);
            LOGGER.info("Succeed to create project. New project: {}", projectInDb.toString());
        }

        Rule ruleInDb = ruleDao.findByProjectAndRuleName(projectInDb, rule.getName());
        importRuleReal(ruleResponse, ruleNodeRequest, ruleInDb, rule, projectInDb, template, ruleGroup, alarmConfigs, ruleVariables, ruleDataSources, ruleDataSourceMappings);
        return new GeneralResponse<>("200", "{&IMPORT_RULE_SUCCESSFULLY}", ruleResponse);
    }

    private void importRuleReal(RuleResponse ruleResponse, RuleNodeRequest ruleNodeRequest, Rule ruleInDb, Rule rule, Project projectInDb, Template template, RuleGroup ruleGroup,
        Set<AlarmConfig> alarmConfigs, Set<RuleVariable> ruleVariables, Set<RuleDataSource> ruleDataSources, Set<RuleDataSourceMapping> ruleDataSourceMappings)
        throws IOException, UnExpectedRequestException {
        if (ruleInDb == null) {
            LOGGER.info("Import in first time. That means adding.");
            rule.setProject(projectInDb);
            ruleGroup.setProjectId(projectInDb.getId());
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
            List<Rule> rules = Collections.singletonList(savedRule);
            ruleGroup.setRules(rules);
            RuleGroup ruleGroupInDb = ruleGroupDao.saveRuleGroup(ruleGroup);
            savedRule.setRuleGroup(ruleGroupInDb);
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
                ruleDataSource.setProjectId(projectInDb.getId());
                ruleDataSource.setRule(savedRule);
                ruleDataSourceList.add(ruleDataSource);
            }
            if (RuleTypeEnum.CUSTOM_RULE.getCode().equals(savedRule.getRuleType())) {
                List<AlarmConfig> customAlarmConfigs = new ArrayList<>();
                for (AlarmConfig alarmConfig : alarmConfigList) {
                    TemplateOutputMeta templateOutputMetaInDb = savedRule.getTemplate().getTemplateOutputMetas().iterator().next();
                    AlarmConfig customAlarmConfig = new AlarmConfig();
                    customAlarmConfig.setRule(savedRule);
                    customAlarmConfig.setThreshold(alarmConfig.getThreshold());
                    customAlarmConfig.setTemplateOutputMeta(templateOutputMetaInDb);
                    customAlarmConfig.setCheckTemplate(alarmConfig.getCheckTemplate());
                    if (alarmConfig.getCheckTemplate().equals(CheckTemplateEnum.FIXED_VALUE.getCode())) {
                        customAlarmConfig.setCompareType(alarmConfig.getCompareType());
                    }
                    customAlarmConfigs.add(customAlarmConfig);
                }
                savedRule.setAlarmConfigs(new HashSet<>(alarmConfigDao.saveAllAlarmConfig(customAlarmConfigs)));
                savedRule.setRuleDataSources(new HashSet<>(ruleDataSourceDao.saveAllRuleDataSource(ruleDataSourceList)));
            } else {
                savedRule.setAlarmConfigs(new HashSet<>(alarmConfigDao.saveAllAlarmConfig(alarmConfigList)));
                savedRule.setRuleVariables(new HashSet<>(ruleVariableDao.saveAllRuleVariable(ruleVariablesList)));
                savedRule.setRuleDataSources(new HashSet<>(ruleDataSourceDao.saveAllRuleDataSource(ruleDataSourceList)));
                for (RuleDataSourceMapping ruleDataSourceMapping : ruleDataSourceMappings) {
                    ruleDataSourceMapping.setRule(savedRule);
                    ruleDataSourceMappingDao.saveRuleDataSourceMapping(ruleDataSourceMapping);
                }
            }
            if (ruleNodeRequest.getChildRuleObject() != null) {
                importChildRule(savedRule, ruleNodeRequest, projectInDb);
            }
            ruleResponse.setRuleGroupId(savedRule.getRuleGroup().getId());
        } else {
            LOGGER.info("Import multiple times. That is to update.");
            updateImportedRule(ruleResponse, rule, ruleInDb, ruleNodeRequest, template, alarmConfigs, ruleVariables, ruleDataSources, ruleDataSourceMappings);

        }
    }

    private void importChildRule(Rule parentRule, RuleNodeRequest ruleNodeRequest, Project projectInDb) throws IOException {
        LOGGER.info("Start to import child rule.");
        ObjectMapper objectMapper = new ObjectMapper();
        Rule childRule = objectMapper.readValue(ruleNodeRequest.getChildRuleObject(), Rule.class);
        Template childTemplate = objectMapper.readValue(ruleNodeRequest.getChildTemplateObject(), Template.class);
        Set<RuleDataSource> childRuleDataSources = objectMapper.readValue(ruleNodeRequest.getChildRuleDataSourcesObject(),
            new TypeReference<Set<RuleDataSource>>() {});
        Set<RuleDataSourceMapping> childRuleDataSourceMappings = objectMapper.readValue(ruleNodeRequest.getChildRuleDataSourceMappingsObject(),
            new TypeReference<Set<RuleDataSourceMapping>>() {});
        Set<AlarmConfig> childAlarmConfigs = objectMapper.readValue(ruleNodeRequest.getChildAlarmConfigsObject(), new TypeReference<Set<AlarmConfig>>() {});
        Set<RuleVariable> childRuleVariables = objectMapper.readValue(ruleNodeRequest.getChildRuleVariablesObject(), new TypeReference<Set<RuleVariable>>() {});

        childRule.setProject(projectInDb);
        childRule.setTemplate(childTemplate);
        Rule savedRule = ruleDao.saveRule(childRule);
        LOGGER.info("Succeed to save child rule. Rule info is {}", savedRule.toString());
        List<AlarmConfig> alarmConfigList = new ArrayList<>();
        for (AlarmConfig alarmConfig : childAlarmConfigs) {
            alarmConfig.setRule(savedRule);
            alarmConfigList.add(alarmConfig);
        }
        List<RuleVariable> ruleVariablesList = new ArrayList<>();
        for (RuleVariable ruleVariable : childRuleVariables) {
            ruleVariable.setRule(savedRule);
            ruleVariablesList.add(ruleVariable);
        }
        List<RuleDataSource> ruleDataSourceList = new ArrayList<>();
        for (RuleDataSource ruleDataSource : childRuleDataSources) {
            ruleDataSource.setProjectId(projectInDb.getId());
            ruleDataSource.setRule(savedRule);
            ruleDataSourceList.add(ruleDataSource);
        }
        savedRule.setAlarmConfigs(new HashSet<>(alarmConfigDao.saveAllAlarmConfig(alarmConfigList)));
        savedRule.setRuleVariables(new HashSet<>(ruleVariableDao.saveAllRuleVariable(ruleVariablesList)));
        savedRule.setRuleDataSources(new HashSet<>(ruleDataSourceDao.saveAllRuleDataSource(ruleDataSourceList)));
        for (RuleDataSourceMapping ruleDataSourceMapping : childRuleDataSourceMappings) {
            ruleDataSourceMapping.setRule(savedRule);
            ruleDataSourceMappingDao.saveRuleDataSourceMapping(ruleDataSourceMapping);
        }
        savedRule.setParentRule(parentRule);
        parentRule.setChildRule(savedRule);
        LOGGER.info("Succeed to import child rule.");
    }

    public void updateImportedRule(RuleResponse ruleResponse, Rule rule, Rule ruleInDb, RuleNodeRequest ruleNodeRequest, Template template, Set<AlarmConfig> alarmConfigs, Set<RuleVariable> ruleVariables,
        Set<RuleDataSource> ruleDataSources, Set<RuleDataSourceMapping> ruleDataSourceMappings) throws UnExpectedRequestException, IOException {
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
        Integer ruleType = ruleInDb.getRuleType();
        if (RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode().equals(ruleType) || RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode().equals(ruleType)) {
            ruleVariableService.deleteByRule(ruleInDb);
            LOGGER.info("Succeed to delete all rule_variable. rule_id: {}", ruleInDb.getId());
            if (RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode().equals(ruleType)) {
                ruleDataSourceMappingService.deleteByRule(ruleInDb);
                LOGGER.info("Succeed to delete all rule_dataSource_mapping. rule_id: {}", ruleInDb.getId());
            }
        }
        ruleDataSourceService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all rule_dataSources. rule_id: {}", ruleInDb.getId());
        Rule updateRule = ruleDao.saveRule(ruleInDb);
        List<RuleDataSource> ruleDataSourceList = new ArrayList<>();
        for (RuleDataSource ruleDataSource : ruleDataSources) {
            ruleDataSource.setProjectId(updateRule.getProject().getId());
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
        if (RuleTypeEnum.CUSTOM_RULE.getCode().equals(ruleType)) {
            List<AlarmConfig> customAlarmConfigs = new ArrayList<>();
            for (AlarmConfig alarmConfig : alarmConfigList) {
                TemplateOutputMeta templateOutputMetaInDb = updateRule.getTemplate().getTemplateOutputMetas().iterator().next();
                AlarmConfig customAlarmConfig = new AlarmConfig();
                customAlarmConfig.setRule(updateRule);
                customAlarmConfig.setTemplateOutputMeta(templateOutputMetaInDb);
                customAlarmConfig.setCheckTemplate(alarmConfig.getCheckTemplate());
                customAlarmConfig.setThreshold(alarmConfig.getThreshold());
                if (alarmConfig.getCheckTemplate().equals(CheckTemplateEnum.FIXED_VALUE.getCode())) {
                    customAlarmConfig.setCompareType(alarmConfig.getCompareType());
                }
                customAlarmConfigs.add(customAlarmConfig);
            }
            updateRule.setAlarmConfigs(new HashSet<>(alarmConfigDao.saveAllAlarmConfig(customAlarmConfigs)));
            updateRule.setRuleDataSources(new HashSet<>(ruleDataSourceDao.saveAllRuleDataSource(ruleDataSourceList)));
        } else {
            updateRule.setAlarmConfigs(new HashSet<>(alarmConfigDao.saveAllAlarmConfig(alarmConfigList)));
            updateRule.setRuleVariables(new HashSet<>(ruleVariableDao.saveAllRuleVariable(ruleVariablesList)));
            updateRule.setRuleDataSources(new HashSet<>(ruleDataSourceDao.saveAllRuleDataSource(ruleDataSourceList)));
            for (RuleDataSourceMapping ruleDataSourceMapping : ruleDataSourceMappings) {
                ruleDataSourceMapping.setRule(updateRule);
                ruleDataSourceMappingDao.saveRuleDataSourceMapping(ruleDataSourceMapping);
            }
        }
        updateImportedChildRule(updateRule, ruleNodeRequest);
        ruleResponse.setRuleGroupId(updateRule.getRuleGroup().getId());
    }

    public void updateImportedChildRule(Rule parentRule, RuleNodeRequest ruleNodeRequest) throws IOException, UnExpectedRequestException {
        if (parentRule.getChildRule() == null) {
            return;
        }
        LOGGER.info("Start to update imported child rule.");
        ObjectMapper objectMapper = new ObjectMapper();
        Rule childRule = parentRule.getChildRule();
        Rule childRuleObject = objectMapper.readValue(ruleNodeRequest.getChildRuleObject(), Rule.class);
        Template childTemplateObject = objectMapper.readValue(ruleNodeRequest.getChildTemplateObject(), Template.class);
        Set<RuleDataSource> childRuleDataSources = objectMapper.readValue(ruleNodeRequest.getChildRuleDataSourcesObject(),
            new TypeReference<Set<RuleDataSource>>() {});
        Set<RuleDataSourceMapping> childRuleDataSourceMappings = objectMapper.readValue(ruleNodeRequest.getChildRuleDataSourceMappingsObject(),
            new TypeReference<Set<RuleDataSourceMapping>>() {});
        Set<AlarmConfig> childAlarmConfigs = objectMapper.readValue(ruleNodeRequest.getChildAlarmConfigsObject(), new TypeReference<Set<AlarmConfig>>() {});
        Set<RuleVariable> childRuleVariables = objectMapper.readValue(ruleNodeRequest.getChildRuleVariablesObject(), new TypeReference<Set<RuleVariable>>() {});
        childRule.setAlarm(childRuleObject.getAlarm());
        childRule.setAbortOnFailure(childRuleObject.getAbortOnFailure());
        childRule.setFromContent(childRuleObject.getFromContent());
        childRule.setWhereContent(childRuleObject.getWhereContent());
        childRule.setRuleType(childRuleObject.getRuleType());
        childRule.setRuleTemplateName(childTemplateObject.getName());
        childRule.setOutputName(childRuleObject.getOutputName());
        childRule.setFunctionType(childRuleObject.getFunctionType());
        childRule.setFunctionContent(childRuleObject.getFunctionContent());
        Template childTemplateInDb = ruleTemplateService.checkRuleTemplate(childTemplateObject.getId());
        if (childTemplateInDb == null) {
            throw new UnExpectedRequestException("Child template [id = " + childTemplateObject.getId() + "] does not exist.");
        }
        alarmConfigService.deleteByRule(childRule);
        LOGGER.info("Succeed to delete all alarm_config of child rule. rule_id: {}", childRule.getId());
        ruleVariableService.deleteByRule(childRule);
        LOGGER.info("Succeed to delete all rule_variable of child rule. rule_id: {}", childRule.getId());
        ruleDataSourceService.deleteByRule(childRule);
        LOGGER.info("Succeed to delete all rule_dataSources of child rule. rule_id: {}", childRule.getId());
        ruleDataSourceMappingService.deleteByRule(childRule);
        LOGGER.info("Succeed to delete all rule_dataSource_mapping. rule_id: {}", childRule.getId());
        Rule updateRule = ruleDao.saveRule(childRule);
        List<RuleDataSource> ruleDataSourceList = new ArrayList<>();
        for (RuleDataSource ruleDataSource : childRuleDataSources) {
            ruleDataSource.setProjectId(parentRule.getProject().getId());
            ruleDataSource.setRule(updateRule);
            ruleDataSourceList.add(ruleDataSource);
        }
        List<AlarmConfig> alarmConfigList = new ArrayList<>();
        for (AlarmConfig alarmConfig : childAlarmConfigs) {
            alarmConfig.setRule(updateRule);
            alarmConfigList.add(alarmConfig);
        }
        List<RuleVariable> ruleVariablesList = new ArrayList<>();
        for (RuleVariable ruleVariable : childRuleVariables) {
            ruleVariable.setRule(updateRule);
            ruleVariablesList.add(ruleVariable);
        }
        updateRule.setAlarmConfigs(new HashSet<>(alarmConfigDao.saveAllAlarmConfig(alarmConfigList)));
        updateRule.setRuleVariables(new HashSet<>(ruleVariableDao.saveAllRuleVariable(ruleVariablesList)));
        updateRule.setRuleDataSources(new HashSet<>(ruleDataSourceDao.saveAllRuleDataSource(ruleDataSourceList)));
        for (RuleDataSourceMapping ruleDataSourceMapping : childRuleDataSourceMappings) {
            ruleDataSourceMapping.setRule(updateRule);
            ruleDataSourceMappingDao.saveRuleDataSourceMapping(ruleDataSourceMapping);
        }
        updateRule.setParentRule(parentRule);
        parentRule.setChildRule(updateRule);
    }
}
