package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.webank.wedatasphere.qualitis.LocalConfig;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDepartmentUserDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.project.constant.ProjectTypeEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.excel.ExcelMultiTemplateRuleByProject;
import com.webank.wedatasphere.qualitis.project.excel.ExcelTemplateFileRuleByProject;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.project.service.OuterWorkflowService;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.constant.CheckTemplateEnum;
import com.webank.wedatasphere.qualitis.rule.constant.CompareTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.FileOutputNameEnum;
import com.webank.wedatasphere.qualitis.rule.constant.FileOutputUnitEnum;
import com.webank.wedatasphere.qualitis.rule.constant.FunctionTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.constant.MappingOperationEnum;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateDataSourceTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.AlarmConfigDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceMappingDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleTemplateDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleVariableDao;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateDataSourceTypeDao;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateOutputMetaDao;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceMapping;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.entity.RuleVariable;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateDataSourceType;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateOutputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;
import com.webank.wedatasphere.qualitis.rule.request.AddCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddFileRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.AlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.CopyRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.CustomAlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceColumnRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.FileAlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.RuleNodeRequest;
import com.webank.wedatasphere.qualitis.rule.request.RuleNodeRequests;
import com.webank.wedatasphere.qualitis.rule.request.TemplateArgumentRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.AddMultiSourceRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinColumnRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinConfigRequest;
import com.webank.wedatasphere.qualitis.rule.response.RuleNodeResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleNodeResponses;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import com.webank.wedatasphere.qualitis.rule.service.AlarmConfigService;
import com.webank.wedatasphere.qualitis.rule.service.CustomRuleService;
import com.webank.wedatasphere.qualitis.rule.service.FileRuleService;
import com.webank.wedatasphere.qualitis.rule.service.MultiSourceRuleService;
import com.webank.wedatasphere.qualitis.rule.service.RuleDataSourceMappingService;
import com.webank.wedatasphere.qualitis.rule.service.RuleDataSourceService;
import com.webank.wedatasphere.qualitis.rule.service.RuleNodeService;
import com.webank.wedatasphere.qualitis.rule.service.RuleService;
import com.webank.wedatasphere.qualitis.rule.service.RuleTemplateService;
import com.webank.wedatasphere.qualitis.rule.service.RuleVariableService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateMidTableInputMetaService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateOutputMetaService;
import com.webank.wedatasphere.qualitis.rule.service.TemplateStatisticsInputMetaService;
import com.webank.wedatasphere.qualitis.rule.util.TemplateMidTableUtil;
import com.webank.wedatasphere.qualitis.service.RoleService;
import com.webank.wedatasphere.qualitis.service.UserService;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.management.relation.RoleNotFoundException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
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
    private LocalConfig localConfig;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private CustomRuleService customRuleService;
    @Autowired
    private MultiSourceRuleService multiSourceRuleService;
    @Autowired
    private FileRuleService fileRuleService;

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
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
    @Autowired
    private TemplateOutputMetaDao templateOutputMetaDao;
    @Autowired
    private TemplateDataSourceTypeDao templateDataSourceTypeDao;

    @Autowired
    private RuleMetricDao ruleMetricDao;
    @Autowired
    private RuleMetricDepartmentUserDao ruleMetricDepartmentUserDao;


    private static final String DEV_CENTER = "dev";
    private static final String PROD_CENTER = "prod";

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleNodeServiceImpl.class);

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> deleteRule(DeleteRuleRequest request) throws UnExpectedRequestException {
        // Check Arguments
        DeleteRuleRequest.checkRequest(request);
        Long ruleGroupId = request.getRuleGroupId();
        if (StringUtils.isBlank(ruleGroupId.toString())) {
            throw new UnExpectedRequestException("Rule group id is invalid.");
        }
        // Delete any rule when find rule by rule group ID
        LOGGER.info("Check existence of rule before deleting. Rule group id: {}.", ruleGroupId.toString());
        List<Rule> rules = ruleDao.findByRuleGroup(ruleGroupDao.findById(request.getRuleGroupId()));
        if(CollectionUtils.isEmpty(rules)) {
            return new GeneralResponse<>("200", "No rules to be delete.", null);
        }
        for (Rule ruleInDb : rules) {
            LOGGER.info("Start to delete rule. Rule group id: {}.", ruleGroupId.toString());
            ruleService.deleteRuleReal(ruleInDb);
        }
        LOGGER.info("Start to delete rule. Rule group id: {}.", ruleGroupId.toString());
        return new GeneralResponse<>("200", "{&DELETE_RULE_SUCCESSFULLY}", null);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> modifyRuleByCsId(ModifyRuleRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "ModifyRuleRequest");
        Project projectInDb = projectDao.findById(request.getProjectId());
        if (projectInDb == null) {
            projectInDb = projectDao.findByName(request.getProjectName());
            if (projectInDb == null) {
                throw new UnExpectedRequestException("Project is not exist.");
            }
        }
        if (projectInDb.getProjectType().equals(ProjectTypeEnum.NORMAL_PROJECT.getCode())) {
            throw new UnExpectedRequestException("Project is not workflow project.");
        }

        List<Rule> rules = ruleDao.findByProject(projectInDb);
        if(rules.isEmpty() || rules.get(0) == null) {
            throw new UnExpectedRequestException("Project [id = " + projectInDb.getId() + "] does not have rules.");
        }
        LOGGER.info("Start to update context service ID.");
        for (Rule ruleInDb : rules) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                if (ruleInDb.getCsId() == null || ruleInDb.getCsId().length() == 0) {
                    continue;
                }
                JsonNode ruleCsId = mapper.readTree(ruleInDb.getCsId());
                JsonNode requestCsId = mapper.readTree(request.getCsId());
                String ruleCsIdValue = ruleCsId.get("value").toString();
                String requestCsIdValue = requestCsId.get("value").toString();
                String ruleFlowStr = ruleCsIdValue.substring(1, ruleCsIdValue.length() - 1).replaceAll("\\\\", "");
                String requestFlowStr = requestCsIdValue.substring(1, requestCsIdValue.length() - 1).replaceAll("\\\\", "");
                JsonNode ruleFlow = mapper.readTree(ruleFlowStr);
                JsonNode requestFlow = mapper.readTree(requestFlowStr);
                // Converting string to json and compare the flow name.
                if (ruleFlow.get("flow").toString().equals(requestFlow.get("flow").toString())) {
                    ruleInDb.setCsId(request.getCsId());
                    ruleDao.saveRule(ruleInDb);
                    LOGGER.info("Succeed to save rule. rule_id: {}, rule_cs_id: {}", ruleInDb.getId(), ruleInDb.getCsId());
                }
            } catch (NullPointerException e) {
                LOGGER.error("Update context service ID NPE.", e);
            } catch (JsonProcessingException e) {
                LOGGER.error("Update context service ID exception.", e);
            } catch (IOException e) {
                LOGGER.error("Update context service ID exception.", e);
            }
        }
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
            if(CollectionUtils.isEmpty(rules)) {
                throw new UnExpectedRequestException("Rule group [id = " + ruleGroup.getId() + "] does not have rules.");
            }
            for (Rule rule : rules) {
                responses.add(ruleNodeResponse(rule));
            }
        } catch (IOException e) {
            LOGGER.error("Failed to export rule because of JSON serialization opeartions.", e);
            return new GeneralResponse<>("500", "{&FAILED_TO_EXPORT_RULE}", null);
        }
        LOGGER.info("Succeed to export rule. Rule info: {}", Arrays.toString(responses.toArray()));
        RuleNodeResponses ruleNodeResponses = new RuleNodeResponses(responses);
        return new GeneralResponse<>("200", "{&EXPORT_RULE_SUCCESSFULLY}", ruleNodeResponses);
    }

    /**
     *  Parse the rule information and encapsulate it into the response body.
     * @param rule
     * @throws IOException
     */
    private RuleNodeResponse ruleNodeResponse(Rule rule) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        RuleNodeResponse response = new RuleNodeResponse();
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
            response.setChildTemplateTemplateStatisticsInputMetaObject(objectMapper.writerWithType(new TypeReference<Set<TemplateStatisticsInputMeta>>() {})
                .writeValueAsString(rule.getChildRule().getTemplate().getStatisticAction()));
        }

        return response;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> importRuleGroup(RuleNodeRequests ruleNodeRequests)
        throws UnExpectedRequestException, IOException, PermissionDeniedRequestException {
        // Project, rule group.
        Project projectInDb = projectDao.findById(ruleNodeRequests.getNewProjectId());

        ObjectMapper objectMapper = new ObjectMapper();
        RuleGroup ruleGroup = objectMapper.readValue(ruleNodeRequests.getRuleNodeRequests().iterator().next().getRuleGroupObject(), RuleGroup.class);
        RuleGroup ruleGroupInDb = ruleGroupDao.saveRuleGroup(new RuleGroup(ruleGroup.getRuleGroupName(), projectInDb.getId()));
        if (DEV_CENTER.equals(localConfig.getCenter())) {
            CopyRuleRequest copyRuleRequest = new CopyRuleRequest();
            copyRuleRequest.setSourceRuleGroupId(ruleGroup.getId());
            copyRuleRequest.setCreateUser(projectInDb.getCreateUser());
            copyRuleRequest.setTargetRuleGroupId(ruleGroupInDb.getId());
            copyRuleRequest.setTargetProjectId(ruleNodeRequests.getNewProjectId());
            copyRuleByRuleGroupId(copyRuleRequest);
            return new GeneralResponse<>("200", "{&IMPORT_RULE_SUCCESSFULLY}", new RuleResponse(ruleGroupInDb.getId()));
        }

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

            Project newProject = projectService.addProjectReal(currentUser.getId(), currentUser.getUserName() + "_project_" + UUID.randomUUID().toString()
                , currentUser.getUserName() + "_项目_" + UUID.randomUUID().toString(), "Auto created.");
            newProject.setProjectType(ProjectTypeEnum.WORKFLOW_PROJECT.getCode());
            projectInDb = projectDao.saveProject(newProject);
            LOGGER.info("Succeed to create project. New project: {}", projectInDb.toString());
        }

        for (RuleNodeRequest request : ruleNodeRequests.getRuleNodeRequests()) {
            importRule(request, projectInDb, ruleGroupInDb, objectMapper);
        }
        return new GeneralResponse<>("200", "{&IMPORT_RULE_SUCCESSFULLY}", new RuleResponse(ruleGroupInDb.getId()));
    }

    @Override
    public GeneralResponse<RuleResponse> copyRuleByRuleGroupId(CopyRuleRequest request)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        CopyRuleRequest.checkRequest(request);
        Long ruleGroupId = request.getSourceRuleGroupId();
        RuleGroup ruleGroupInDb = ruleGroupDao.findById(request.getSourceRuleGroupId());

        int totalFinish = 0;
        RuleGroup targetRuleGroup;
        if (request.getTargetRuleGroupId() != null) {
            RuleGroup targetRuleGroupInDb = ruleGroupDao.findById(request.getTargetRuleGroupId());
            if (targetRuleGroupInDb != null) {
                targetRuleGroup = targetRuleGroupInDb;
            } else {
                throw new UnExpectedRequestException("Rule group id is illegal.");
            }
        } else {
            RuleGroup currentRuleGroup = new RuleGroup("Group_" + UUID.randomUUID().toString().replace("-", "")
                , request.getTargetProjectId() != null ? request.getTargetProjectId() : ruleGroupInDb.getProjectId());
            currentRuleGroup.setVersion(request.getVersion());
            targetRuleGroup = ruleGroupDao.saveRuleGroup(currentRuleGroup);
        }
        if (ruleGroupId != null) {
            LOGGER.info("Start to copy rules of rule group[ID=" + ruleGroupId + "].");

            if (ruleGroupInDb == null) {
                throw new UnExpectedRequestException("Rule group {&DOES_NOT_EXIST}");
            }
            List<Rule> rules = ruleDao.findByRuleGroup(ruleGroupInDb);
            for (Rule rule : rules) {
                switch (rule.getRuleType().intValue()) {
                    case 1:
                        AddRuleRequest addRuleRequest = constructSingleRequest(rule, targetRuleGroup);
                        ruleService.addRuleForOuter(addRuleRequest, request.getCreateUser());
                        totalFinish ++;
                        break;
                    case 2:
                        AddCustomRuleRequest addCustomRuleRequest = constructCustomRequest(rule, targetRuleGroup);
                        customRuleService.addRuleForOuter(addCustomRuleRequest, request.getCreateUser());
                        totalFinish ++;
                        break;
                    case 3:
                        AddMultiSourceRuleRequest addMultiSourceRuleRequest = constructMultiRequest(rule, targetRuleGroup);
                        addMultiSourceRuleRequest.setLoginUser(request.getCreateUser());

                        multiSourceRuleService.addRuleForOuter(addMultiSourceRuleRequest, false);
                        totalFinish ++;
                        break;
                    case 4:
                        AddFileRuleRequest addFileRuleRequest = constructFileRequest(rule, targetRuleGroup);
                        fileRuleService.addRuleForOuter(addFileRuleRequest, request.getCreateUser());
                        totalFinish ++;
                        break;
                    default:

                }

            }
            if (totalFinish != rules.size()) {
                return new GeneralResponse<>("200", "{&COPY_RULE_FAILED}", new RuleResponse(targetRuleGroup.getId()));
            }

        } else if (CollectionUtils.isNotEmpty(request.getSourceRuleIdList())) {

        }
        return new GeneralResponse<>("200", "{&COPY_RULE_SUCCESSFULLY}", new RuleResponse(targetRuleGroup.getId()));
    }

    private AddFileRuleRequest constructFileRequest(Rule rule, RuleGroup ruleGroup) {
        AddFileRuleRequest addFileRuleRequest = new AddFileRuleRequest();
        String newVersion = ruleGroup.getVersion();
        if (StringUtils.isEmpty(newVersion)) {
            String newRuleName = rule.getName() + "_copy_" + ruleGroup.getId();
            LOGGER.info("File rule start to be copied. Copied rule name: " + newRuleName);
            addFileRuleRequest.setRuleName(newRuleName);
            if (StringUtils.isNotBlank(rule.getCnName())) {
                addFileRuleRequest.setRuleCnName(rule.getCnName() + "_副本");
            }
        } else {
            String oldVersion = rule.getRuleGroup().getVersion();
            LOGGER.info("File rule start to be copied. Copied rule name: " + rule.getName() + "_" + ruleGroup.getVersion());
            if (StringUtils.isNotBlank(oldVersion) && rule.getName().endsWith(oldVersion)) {
                addFileRuleRequest.setRuleName(rule.getName().replace(oldVersion, newVersion));
                if (StringUtils.isNotBlank(rule.getCnName())) {
                    addFileRuleRequest.setRuleCnName(rule.getCnName().replace(oldVersion, newVersion));
                }
            } else {
                addFileRuleRequest.setRuleName(rule.getName() + "_" + newVersion);
                if (StringUtils.isNotBlank(rule.getCnName())) {
                    addFileRuleRequest.setRuleCnName(rule.getCnName() + "_" + newVersion);
                }
            }
        }
        String ruleDetail = rule.getDetail();
        addFileRuleRequest.setRuleDetail(ruleDetail);
        addFileRuleRequest.setAbortOnFailure(rule.getAbortOnFailure());

        addFileRuleRequest.setCsId(rule.getCsId());
        addFileRuleRequest.setRuleGroupId(ruleGroup.getId());
        addFileRuleRequest.setProjectId(ruleGroup.getProjectId());
        addFileRuleRequest.setAbortOnFailure(rule.getAbortOnFailure());

        for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
            DataSourceRequest dataSourceRequest = new DataSourceRequest();
            String clusterName = ruleDataSource.getClusterName();
            String databaseName = ruleDataSource.getDbName();
            String tableName = ruleDataSource.getTableName();
            String filter = ruleDataSource.getFilter();

            dataSourceRequest.setClusterName(clusterName);
            dataSourceRequest.setDbName(databaseName);
            dataSourceRequest.setTableName(tableName);
            dataSourceRequest.setFilter(filter);

            addFileRuleRequest.setDatasource(dataSourceRequest);
            break;
        }
        List<FileAlarmConfigRequest> alarmVariable = new ArrayList<>();
        for (AlarmConfig alarmConfig : rule.getAlarmConfigs()) {
            FileAlarmConfigRequest fileAlarmConfigRequest = new FileAlarmConfigRequest();

            Double threshold = alarmConfig.getThreshold();
            Integer unit = alarmConfig.getFileOutputUnit();
            Integer alarmCompareType = alarmConfig.getCompareType();
            Integer alarmOutputName = alarmConfig.getFileOutputName();

            Integer checkTemplateName = alarmConfig.getCheckTemplate();

            fileAlarmConfigRequest.setFileOutputUnit(unit);
            fileAlarmConfigRequest.setCompareType(alarmCompareType);
            fileAlarmConfigRequest.setFileOutputName(alarmOutputName);
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
        addFileRuleRequest.setAlarm(true);
        addFileRuleRequest.setAlarmVariable(alarmVariable);
        return addFileRuleRequest;
    }

    private AddMultiSourceRuleRequest constructMultiRequest(Rule rule, RuleGroup ruleGroup) {
        AddMultiSourceRuleRequest addMultiSourceRuleRequest = new AddMultiSourceRuleRequest();
        String newVersion = ruleGroup.getVersion();
        if (StringUtils.isEmpty(newVersion)) {
            String newRuleName = rule.getName() + "_copy_" + ruleGroup.getId();
            LOGGER.info("Multi rule start to be copied. Copied rule name: " + newRuleName);
            addMultiSourceRuleRequest.setRuleName(newRuleName);
            if (StringUtils.isNotBlank(rule.getCnName())) {
                addMultiSourceRuleRequest.setRuleCnName(rule.getCnName() + "_副本");
            }
        } else {
            String oldVersion = rule.getRuleGroup().getVersion();
            LOGGER.info("Multi rule start to be copied. Copied rule name: " + rule.getName() + "_" + ruleGroup.getVersion());
            if (StringUtils.isNotBlank(oldVersion) && rule.getName().endsWith(oldVersion)) {
                addMultiSourceRuleRequest.setRuleName(rule.getName().replace(oldVersion, newVersion));
                if (StringUtils.isNotBlank(rule.getCnName())) {
                    addMultiSourceRuleRequest.setRuleCnName(rule.getCnName().replace(oldVersion, newVersion));
                }
            } else {
                addMultiSourceRuleRequest.setRuleName(rule.getName() + "_" + newVersion);
                if (StringUtils.isNotBlank(rule.getCnName())) {
                    addMultiSourceRuleRequest.setRuleCnName(rule.getCnName() + "_" + newVersion);
                }
            }
        }

        String ruleDetail = rule.getDetail();

        String clusterName = rule.getRuleDataSources().iterator().next().getClusterName();
        addMultiSourceRuleRequest.setAbortOnFailure(rule.getAbortOnFailure());
        addMultiSourceRuleRequest.setClusterName(clusterName);
        addMultiSourceRuleRequest.setRuleDetail(ruleDetail);
        addMultiSourceRuleRequest.setCsId(rule.getCsId());
        addMultiSourceRuleRequest.setSpecifyStaticStartupParam(rule.getSpecifyStaticStartupParam());
        addMultiSourceRuleRequest.setDeleteFailCheckResult(rule.getDeleteFailCheckResult());
        addMultiSourceRuleRequest.setMultiSourceRuleTemplateId(rule.getTemplate().getId());
        addMultiSourceRuleRequest.setStaticStartupParam(rule.getStaticStartupParam());
        addMultiSourceRuleRequest.setProjectId(ruleGroup.getProjectId());
        addMultiSourceRuleRequest.setRuleGroupId(ruleGroup.getId());

        List<RuleVariable> filterRuleVariable = rule.getRuleVariables().stream().filter(ruleVariable ->
            ruleVariable.getTemplateMidTableInputMeta().getInputType().equals(TemplateInputTypeEnum.CONDITION.getCode())).collect(Collectors.toList());
        if (filterRuleVariable != null && filterRuleVariable.size() != 0) {
            addMultiSourceRuleRequest.setFilter(filterRuleVariable.iterator().next().getValue());
        }

        // Data source request
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
        // Mapping
        List<MultiDataSourceJoinConfigRequest> mappings = new ArrayList<>();
        for (RuleDataSourceMapping mapping : rule.getRuleDataSourceMappings()) {
            MultiDataSourceJoinConfigRequest multiDataSourceJoinConfigRequest = new MultiDataSourceJoinConfigRequest();
            multiDataSourceJoinConfigRequest.setOperation(mapping.getOperation());
            multiDataSourceJoinConfigRequest.setLeftStatement(mapping.getLeftStatement());
            multiDataSourceJoinConfigRequest.setRightStatement(mapping.getRightStatement());

            List<MultiDataSourceJoinColumnRequest> left = getMultiDataSourceJoinColumnRequest(mapping.getLeftColumnNames(), mapping.getLeftColumnTypes());
            List<MultiDataSourceJoinColumnRequest> right = getMultiDataSourceJoinColumnRequest(mapping.getRightColumnNames(), mapping.getLeftColumnTypes());
            multiDataSourceJoinConfigRequest.setLeft(left);
            multiDataSourceJoinConfigRequest.setRight(right);
            mappings.add(multiDataSourceJoinConfigRequest);
        }
        addMultiSourceRuleRequest.setMappings(mappings);

        List<AlarmConfigRequest> alarmConfigRequests = constructAlarmConfigRequest(rule.getAlarmConfigs());
        addMultiSourceRuleRequest.setAlarm(true);
        addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        return addMultiSourceRuleRequest;
    }

    private List<MultiDataSourceJoinColumnRequest> getMultiDataSourceJoinColumnRequest(String names, String types) {
        List<MultiDataSourceJoinColumnRequest> joinColumnRequests = new ArrayList<>();
        String[] columnNames = names.split(",");
        String[] columnTypes = types.split("\\|");
        for (int i = 0; i < columnNames.length; i ++) {
            joinColumnRequests.add(new MultiDataSourceJoinColumnRequest(columnNames[i], columnTypes[i]));
        }
        return joinColumnRequests;
    }

    private AddCustomRuleRequest constructCustomRequest(Rule rule, RuleGroup ruleGroup) {
        AddCustomRuleRequest addCustomRuleRequest = new AddCustomRuleRequest();
        String newVersion = ruleGroup.getVersion();
        if (StringUtils.isEmpty(newVersion)) {
            String newRuleName = rule.getName() + "_copy_" + ruleGroup.getId();
            LOGGER.info("Custom rule start to be copied. Copied rule name: " + newRuleName);
            addCustomRuleRequest.setRuleName(newRuleName);
            if (StringUtils.isNotBlank(rule.getCnName())) {
                addCustomRuleRequest.setRuleCnName(rule.getCnName() + "_副本");
            }
        } else {
            String oldVersion = rule.getRuleGroup().getVersion();
            LOGGER.info("Custom rule start to be copied. Copied rule name: " + rule.getName() + "_" + ruleGroup.getVersion());
            if (StringUtils.isNotBlank(oldVersion) && rule.getName().endsWith(oldVersion)) {
                addCustomRuleRequest.setRuleName(rule.getName().replace(oldVersion, newVersion));
                if (StringUtils.isNotBlank(rule.getCnName())) {
                    addCustomRuleRequest.setRuleCnName(rule.getCnName().replace(oldVersion, newVersion));
                }
            } else {
                addCustomRuleRequest.setRuleName(rule.getName() + "_" + newVersion);
                if (StringUtils.isNotBlank(rule.getCnName())) {
                    addCustomRuleRequest.setRuleCnName(rule.getCnName() + "_" + newVersion);
                }
            }
        }

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
        RuleDataSource ruleDataSource = rule.getRuleDataSources().iterator().next();
        addCustomRuleRequest.setClusterName(ruleDataSource.getClusterName());
        addCustomRuleRequest.setProxyUser(ruleDataSource.getProxyUser());

        addCustomRuleRequest.setProjectId(ruleGroup.getProjectId());
        addCustomRuleRequest.setRuleGroupId(ruleGroup.getId());
        addCustomRuleRequest.setCsId(rule.getCsId());
        addCustomRuleRequest.setAbortOnFailure(rule.getAbortOnFailure());
        addCustomRuleRequest.setStaticStartupParam(rule.getStaticStartupParam());
        addCustomRuleRequest.setSpecifyStaticStartupParam(rule.getSpecifyStaticStartupParam());

        if (ruleDataSource.getLinkisDataSourceId() != null) {
            addCustomRuleRequest.setLinkisDataSourceId(ruleDataSource.getLinkisDataSourceId());
            addCustomRuleRequest.setLinkisDataSourceName(ruleDataSource.getLinkisDataSourceName());
            addCustomRuleRequest.setLinkisDataSourceType(TemplateDataSourceTypeEnum.getMessage(ruleDataSource.getDatasourceType()));
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
            addCustomRuleRequest.setOutputName(ruleMetric.getName());
            alarmConfigRequests.add(alarmConfigRequest);
        }

        return alarmConfigRequests;
    }

    private AddRuleRequest constructSingleRequest(Rule rule, RuleGroup ruleGroup) {
        AddRuleRequest addRuleRequest = new AddRuleRequest();
        String newVersion = ruleGroup.getVersion();
        if (StringUtils.isEmpty(newVersion)) {
            String newRuleName = rule.getName() + "_copy_" + ruleGroup.getId();
            LOGGER.info("Single rule start to be copied. Copied rule name: " + newRuleName);
            addRuleRequest.setRuleName(newRuleName);
            if (StringUtils.isNotBlank(rule.getCnName())) {
                addRuleRequest.setRuleCnName(rule.getCnName() + "_副本");
            }
        } else {
            String oldVersion = rule.getRuleGroup().getVersion();
            LOGGER.info("Single rule start to be copied. Copied rule name: " + rule.getName() + "_" + ruleGroup.getVersion());
            if (StringUtils.isNotBlank(oldVersion) && rule.getName().endsWith(oldVersion)) {
                addRuleRequest.setRuleName(rule.getName().replace(oldVersion, newVersion));
                if (StringUtils.isNotBlank(rule.getCnName())) {
                    addRuleRequest.setRuleCnName(rule.getCnName().replace(oldVersion, newVersion));
                }
            } else {
                addRuleRequest.setRuleName(rule.getName() + "_" + newVersion);
                if (StringUtils.isNotBlank(rule.getCnName())) {
                    addRuleRequest.setRuleCnName(rule.getCnName() + "_" + newVersion);
                }
            }
        }

        addRuleRequest.setRuleDetail(rule.getDetail());
        addRuleRequest.setRuleTemplateId(rule.getTemplate().getId());

        addRuleRequest.setAlarm(true);
        addRuleRequest.setAlarmVariable(constructAlarmConfigRequest(rule.getAlarmConfigs()));
        addRuleRequest.setTemplateArgumentRequests(constructTemplateArgumentRequest(rule));
        addRuleRequest.setDatasource(constructDataSourceRequest(rule.getRuleDataSources()));

        addRuleRequest.setProjectId(ruleGroup.getProjectId());
        addRuleRequest.setRuleGroupId(ruleGroup.getId());
        addRuleRequest.setCsId(rule.getCsId());

        addRuleRequest.setAbortOnFailure(rule.getAbortOnFailure());
        addRuleRequest.setDeleteFailCheckResult(rule.getDeleteFailCheckResult());
        addRuleRequest.setSpecifyStaticStartupParam(rule.getSpecifyStaticStartupParam());
        addRuleRequest.setStaticStartupParam(rule.getStaticStartupParam());
        return addRuleRequest;
    }

    private List<TemplateArgumentRequest> constructTemplateArgumentRequest(Rule rule) {
        List<TemplateArgumentRequest> templateArgumentRequests = new ArrayList<>(rule.getTemplate().getTemplateMidTableInputMetas().size());

        for (TemplateMidTableInputMeta templateMidTableInputMeta : rule.getTemplate().getTemplateMidTableInputMetas()) {
            if (TemplateMidTableUtil.shouldResponse(templateMidTableInputMeta)) {
                for (RuleVariable ruleVariable : rule.getRuleVariables()) {
                    TemplateArgumentRequest templateArgumentRequest = new TemplateArgumentRequest();

                    if (ruleVariable.getTemplateMidTableInputMeta().equals(templateMidTableInputMeta)) {
                        String value = StringEscapeUtils.unescapeJava(ruleVariable.getValue());
                        if (templateMidTableInputMeta.getInputType().equals(TemplateInputTypeEnum.REGEXP.getCode())) {
                            if (templateMidTableInputMeta.getRegexpType() != null) {
                                value = ruleVariable.getOriginValue();
                            }
                        }
                        templateArgumentRequest.setArgumentStep(InputActionStepEnum.TEMPLATE_INPUT_META.getCode());
                        templateArgumentRequest.setArgumentId(templateMidTableInputMeta.getId());
                        templateArgumentRequest.setArgumentValue(value);

                        templateArgumentRequests.add(templateArgumentRequest);
                    }
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
    public void importRule(RuleNodeRequest ruleNodeRequest, Project projectInDb, RuleGroup ruleGroup, ObjectMapper objectMapper)
        throws UnExpectedRequestException, IOException {
        // Rule, Template, RuleGroup.
        Rule rule = objectMapper.readValue(ruleNodeRequest.getRuleObject(), Rule.class);
        Template template = objectMapper.readValue(ruleNodeRequest.getTemplateObject(), Template.class);
        // RuleDataSource.
        Set<RuleDataSource> ruleDataSources = objectMapper.readValue(ruleNodeRequest.getRuleDataSourcesObject(),
            new TypeReference<Set<RuleDataSource>>() {});
        Set<RuleDataSourceMapping> ruleDataSourceMappings = objectMapper.readValue(ruleNodeRequest.getRuleDataSourceMappingsObject(),
            new TypeReference<Set<RuleDataSourceMapping>>() {});
        // Rule check meta info.
        Set<AlarmConfig> alarmConfigs = objectMapper.readValue(ruleNodeRequest.getAlarmConfigsObject(), new TypeReference<Set<AlarmConfig>>() {});
        Set<RuleVariable> ruleVariables = objectMapper.readValue(ruleNodeRequest.getRuleVariablesObject(), new TypeReference<Set<RuleVariable>>() {});
        LOGGER.info("Import basic information: {}", new StringBuilder().append("\n").append(rule.getName()).append("\n")
                .append(template.getName()).append("\n").toString());
        LOGGER.info(objectMapper.writeValueAsString(ruleNodeRequest));

        Rule ruleInDb = ruleDao.findByProjectAndRuleName(projectInDb, rule.getName());
        try {
            importRuleReal(ruleNodeRequest, ruleInDb, rule, projectInDb, template, ruleGroup, alarmConfigs, ruleVariables, ruleDataSources, ruleDataSourceMappings);
        } catch(NullPointerException e) {
            LOGGER.error("Rule object attributes must not be null.", e);
        }
    }

    private void importRuleReal(RuleNodeRequest ruleNodeRequest, Rule ruleInDb, Rule rule, Project projectInDb, Template template, RuleGroup ruleGroup,
                                                    Set<AlarmConfig> alarmConfigs, Set<RuleVariable> ruleVariables, Set<RuleDataSource> ruleDataSources,
                                                      Set<RuleDataSourceMapping> ruleDataSourceMappings) throws IOException, UnExpectedRequestException {
        if (ruleInDb == null) {
            LOGGER.info("Import in first time. That means adding.");
            rule.setProject(projectInDb);
            ruleGroup.setProjectId(projectInDb.getId());
            if (RuleTypeEnum.CUSTOM_RULE.getCode().equals(rule.getRuleType())) {
                Template saveTemplate = ruleTemplateDao.saveTemplate(template);
                Set<Integer> templateDateTypes = ruleDataSources.stream()
                    .filter(currRuleDataSource -> currRuleDataSource.getDatasourceType() != null)
                    .map(RuleDataSource::getDatasourceType)
                    .collect(Collectors.toSet());
                for (Integer templateDataType : templateDateTypes) {
                    TemplateDataSourceType templateDataSourceType = new TemplateDataSourceType(templateDataType, saveTemplate);
                    templateDataSourceTypeDao.save(templateDataSourceType);
                }
                Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas = new HashSet<>();
                Set<TemplateOutputMeta> templateOutputMetaSet = new HashSet<>();
                if (rule.getOutputName() != null && rule.getFunctionType() != null && rule.getFunctionContent() != null) {
                    templateStatisticsInputMetas = templateStatisticsInputMetaService.getAndSaveTemplateStatisticsInputMeta(
                        rule.getOutputName(), rule.getFunctionType(), rule.getFunctionContent(), saveTemplate.getSaveMidTable(), saveTemplate);
                    templateOutputMetaSet = templateOutputMetaService.getAndSaveTemplateOutputMeta(rule.getOutputName(),
                        rule.getFunctionType(), saveTemplate.getSaveMidTable(), saveTemplate);
                } else {
                    List<RuleMetric> ruleMetrics = alarmConfigs.stream().map(AlarmConfig::getRuleMetric).collect(Collectors.toList());
                    for (RuleMetric ruleMetric : ruleMetrics) {
                        templateStatisticsInputMetas.addAll(templateStatisticsInputMetaService.getAndSaveTemplateStatisticsInputMeta(
                            ruleMetric.getName(), FunctionTypeEnum.MAX_FUNCTION.getCode(), ruleMetric.getName(), saveTemplate.getSaveMidTable(), saveTemplate));
                        templateOutputMetaSet.addAll(templateOutputMetaService.getAndSaveTemplateOutputMeta(ruleMetric.getName(),
                            FunctionTypeEnum.MAX_FUNCTION.getCode(), saveTemplate.getSaveMidTable(), saveTemplate));
                    }
                }

                saveTemplate.setStatisticAction(templateStatisticsInputMetas);
                saveTemplate.setTemplateOutputMetas(templateOutputMetaSet);
                rule.setTemplate(saveTemplate);
            } else if (RuleTypeEnum.FILE_TEMPLATE_RULE.getCode().equals(rule.getRuleType())) {
                LOGGER.info("Start to import file rule. {}", rule.getName());
                // Save file rule template.
                rule.setTemplate(ruleTemplateDao.saveTemplate(template));
                // Save file rule group
                rule.setRuleGroup(ruleGroup);
                Rule savedRule = ruleDao.saveRule(rule);
                // Save file alarmconfig
                List<AlarmConfig> alarmConfigList = new ArrayList<>();
                for (AlarmConfig alarmConfig : alarmConfigs) {
                    ruleMetricSycn(alarmConfig);
                    alarmConfig.setRule(savedRule);
                    alarmConfigList.add(alarmConfig);
                }

                List<RuleDataSource> ruleDataSourceList = new ArrayList<>();
                for (RuleDataSource ruleDataSource : ruleDataSources) {
                    ruleDataSource.setProjectId(projectInDb.getId());
                    ruleDataSource.setRule(savedRule);
                    ruleDataSourceList.add(ruleDataSource);
                }
                savedRule.setAlarmConfigs(new HashSet<>(alarmConfigDao.saveAllAlarmConfig(alarmConfigList)));
                savedRule.setRuleDataSources(new HashSet<>(ruleDataSourceDao.saveAllRuleDataSource(ruleDataSourceList)));
                LOGGER.info("Finish to import file rule. {}", rule.getName());
                return;
            } else {
                // When trying out the newly created rule template in the development center, the production center needs to synchronize the rule template.
                synchroRuleTemplate(ruleNodeRequest, rule, template, ruleDataSources);
            }
            rule.setRuleGroup(ruleGroup);
            Rule savedRule = ruleDao.saveRule(rule);
            saveRuleInfo(savedRule, template, projectInDb, alarmConfigs, ruleVariables, ruleDataSources, ruleDataSourceMappings);
            if (ruleNodeRequest.getChildRuleObject() != null) {
                importChildRule(savedRule, ruleGroup, ruleNodeRequest, projectInDb);
            }
        } else {
            LOGGER.info("Import multiple times. That is to update.");
            if (RuleTypeEnum.FILE_TEMPLATE_RULE.getCode().equals(rule.getRuleType())) {
                LOGGER.info("Start to update import file rule. {}", rule.getName());
                Set<AlarmConfig> alarmConfigList = new HashSet<>();
                for (AlarmConfig alarmConfig : alarmConfigs) {
                    ruleMetricSycn(alarmConfig);
                    alarmConfig.setRule(ruleInDb);
                    alarmConfigList.add(alarmConfig);
                }
                updateImportedFileRule(rule, ruleInDb, ruleGroup, alarmConfigList, ruleDataSources);
                LOGGER.info("Finish to update import file rule. {}", rule.getName());
                return;
            }
            updateImportedRule(rule, ruleInDb, ruleGroup, ruleNodeRequest, template, alarmConfigs, ruleVariables, ruleDataSources, ruleDataSourceMappings);
        }
    }

    private void ruleMetricSycn(AlarmConfig alarmConfig) {
        // Rule metric fix.
        if (alarmConfig.getRuleMetric() != null) {
            RuleMetric ruleMetric = alarmConfig.getRuleMetric();
            if (ruleMetricDao.findByEnCode(ruleMetric.getEnCode()) != null) {
                alarmConfig.setRuleMetric(ruleMetricDao.findByEnCode(ruleMetric.getEnCode()));
            } else if (ruleMetricDao.findByName(ruleMetric.getName()) != null){
                alarmConfig.setRuleMetric(ruleMetricDao.findByName(ruleMetric.getName()));
            } else {
                alarmConfig.setRuleMetric(ruleMetricDao.add(ruleMetric));
            }
        }
    }

    private void updateImportedFileRule(Rule rule, Rule ruleInDb, RuleGroup ruleGroup,
        Set<AlarmConfig> alarmConfigs, Set<RuleDataSource> ruleDataSources) {
        ruleInDb.setRuleGroup(ruleGroup);
        ruleInDb.setAlarm(rule.getAlarm());
        ruleInDb.setAbortOnFailure(rule.getAbortOnFailure());
        alarmConfigService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all alarm_config. rule_id: {}", ruleInDb.getId());
        ruleDataSourceService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all rule_dataSources. rule_id: {}", ruleInDb.getId());
        Rule updateRule = ruleDao.saveRule(ruleInDb);
        // Save file alarmconfig
        List<AlarmConfig> alarmConfigList = new ArrayList<>();
        for (AlarmConfig alarmConfig : alarmConfigs) {
            alarmConfig.setRule(updateRule);
            alarmConfigList.add(alarmConfig);
        }
        // Save file datasource
        List<RuleDataSource> ruleDataSourceList = new ArrayList<>();
        for (RuleDataSource ruleDataSource : ruleDataSources) {
            ruleDataSource.setProjectId(updateRule.getId());
            ruleDataSource.setRule(updateRule);
            ruleDataSourceList.add(ruleDataSource);
        }
        updateRule.setAlarmConfigs(new HashSet<>(alarmConfigDao.saveAllAlarmConfig(alarmConfigList)));
        updateRule.setRuleDataSources(new HashSet<>(ruleDataSourceDao.saveAllRuleDataSource(ruleDataSourceList)));
    }

    private void importChildRule(Rule parentRule, RuleGroup ruleGroup, RuleNodeRequest ruleNodeRequest,
        Project projectInDb) throws IOException {
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
        childRule.setRuleGroup(ruleGroup);
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

    public void updateImportedRule(Rule rule, Rule ruleInDb, RuleGroup ruleGroup,
                            RuleNodeRequest ruleNodeRequest, Template template, Set<AlarmConfig> alarmConfigs,
                            Set<RuleVariable> ruleVariables, Set<RuleDataSource> ruleDataSources, Set<RuleDataSourceMapping> ruleDataSourceMappings)
                            throws UnExpectedRequestException, IOException {
        ruleInDb.setRuleGroup(ruleGroup);
        ruleInDb.setAlarm(rule.getAlarm());
        ruleInDb.setAbortOnFailure(rule.getAbortOnFailure());
        ruleInDb.setFromContent(rule.getFromContent());
        ruleInDb.setWhereContent(rule.getWhereContent());
        ruleInDb.setRuleType(rule.getRuleType());
        ruleInDb.setRuleTemplateName(template.getName());
        ruleInDb.setOutputName(rule.getOutputName());
        ruleInDb.setFunctionType(rule.getFunctionType());
        ruleInDb.setFunctionContent(rule.getFunctionContent());
        ruleInDb.setDeleteFailCheckResult(rule.getDeleteFailCheckResult());
        if (RuleTypeEnum.CUSTOM_RULE.getCode().equals(rule.getRuleType())) {
            ruleTemplateService.deleteCustomTemplate(ruleInDb.getTemplate());
            Template savedTemplate = ruleTemplateDao.saveTemplate(template);
            Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas = new HashSet<>();
            Set<TemplateOutputMeta> templateOutputMetaSet = new HashSet<>();
            if (rule.getOutputName() != null && rule.getFunctionType() != null && rule.getFunctionContent() != null) {
                templateStatisticsInputMetas = templateStatisticsInputMetaService.getAndSaveTemplateStatisticsInputMeta(
                    rule.getOutputName(), rule.getFunctionType(), rule.getFunctionContent(), savedTemplate.getSaveMidTable(), savedTemplate);
                templateOutputMetaSet = templateOutputMetaService.getAndSaveTemplateOutputMeta(rule.getOutputName(),
                    rule.getFunctionType(), savedTemplate.getSaveMidTable(), savedTemplate);
            } else {
                List<RuleMetric> ruleMetrics = alarmConfigs.stream().map(AlarmConfig::getRuleMetric).collect(Collectors.toList());
                for (RuleMetric ruleMetric : ruleMetrics) {
                    templateStatisticsInputMetas.addAll(templateStatisticsInputMetaService.getAndSaveTemplateStatisticsInputMeta(
                        ruleMetric.getName(), FunctionTypeEnum.MAX_FUNCTION.getCode(), ruleMetric.getName(), savedTemplate.getSaveMidTable(), savedTemplate));
                    templateOutputMetaSet.addAll(templateOutputMetaService.getAndSaveTemplateOutputMeta(ruleMetric.getName(),
                        FunctionTypeEnum.MAX_FUNCTION.getCode(), savedTemplate.getSaveMidTable(), savedTemplate));
                }
            }

            savedTemplate.setStatisticAction(templateStatisticsInputMetas);
            savedTemplate.setTemplateOutputMetas(templateOutputMetaSet);
            ruleInDb.setTemplate(savedTemplate);
        } else {
            synchroRuleTemplate(ruleNodeRequest, ruleInDb, template, ruleDataSources);
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
        saveRuleInfo(updateRule, template, updateRule.getProject(), alarmConfigs, ruleVariables, ruleDataSources, ruleDataSourceMappings);
        updateImportedChildRule(updateRule, ruleNodeRequest);
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

    public void synchroRuleTemplate(RuleNodeRequest ruleNodeRequest, Rule rule, Template template, Set<RuleDataSource> ruleDataSources) throws IOException {
        Template templateInDb = ruleTemplateDao.findById(template.getId());
        Template templateNameInDb = null;
        if (StringUtils.isNotBlank(template.getImportExportName())) {
            templateNameInDb = ruleTemplateDao.findByImportExportName(template.getImportExportName());
        }

        if (templateInDb == null && templateNameInDb == null) {
            LOGGER.info("Start to synchronize the rule template. Template: {}", template);
            ObjectMapper objectMapper = new ObjectMapper();
            // Template meta info.
            Set<TemplateOutputMeta> templateOutputMetaSet = objectMapper.readValue(ruleNodeRequest.getTemplateTemplateOutputMetaObject(),
                new TypeReference<Set<TemplateOutputMeta>>() {});
            Set<TemplateMidTableInputMeta> templateMidTableInputMetaSet = objectMapper.readValue(ruleNodeRequest.getTemplateTemplateMidTableInputMetaObject(),
                new TypeReference<Set<TemplateMidTableInputMeta>>() {});
            Set<TemplateStatisticsInputMeta> templateStatisticsInputMetaSet = objectMapper.readValue(ruleNodeRequest.getTemplateTemplateStatisticsInputMetaObject(),
                new TypeReference<Set<TemplateStatisticsInputMeta>>() {});

            Template savedTemplate = ruleTemplateDao.saveTemplate(template);
            Set<TemplateOutputMeta> templateOutputMetas = new HashSet<>();
            for (TemplateOutputMeta outputMeta : templateOutputMetaSet) {
                outputMeta.setTemplate(savedTemplate);
                templateOutputMetas.add(templateOutputMetaDao.saveTemplateOutputMeta(outputMeta));
            }
            savedTemplate.setTemplateOutputMetas(templateOutputMetas);
            LOGGER.info("Success to save template output meta. TemplateOutputMetas: {}", savedTemplate.getTemplateOutputMetas());

            List<TemplateMidTableInputMeta> templateMidTableInputMetas = new ArrayList<>();
            for (TemplateMidTableInputMeta templateMidTableInputMeta : templateMidTableInputMetaSet) {
                templateMidTableInputMeta.setTemplate(savedTemplate);
                templateMidTableInputMetas.add(templateMidTableInputMeta);
            }
            templateMidTableInputMetas.sort(Comparator.comparing(TemplateMidTableInputMeta::getId));
            savedTemplate.setTemplateMidTableInputMetas(templateMidTableInputMetaService.saveAll(templateMidTableInputMetas));
            LOGGER.info("Success to save template mid_table input meta. TemplateMidTableInputMetas: {}", savedTemplate.getTemplateMidTableInputMetas());

            List<TemplateStatisticsInputMeta> templateStatisticsInputMetas = new ArrayList<>();
            for (TemplateStatisticsInputMeta templateStatisticsInputMeta : templateStatisticsInputMetaSet) {
                templateStatisticsInputMeta.setTemplate(savedTemplate);
                templateStatisticsInputMetas.add(templateStatisticsInputMeta);
            }
            savedTemplate.setStatisticAction(templateStatisticsInputMetaService.saveAll(templateStatisticsInputMetas));
            LOGGER.info("Success to save template statistics input meta. templateStatisticsInputMetas: {}", savedTemplate.getStatisticAction());
            Set<Integer> templateDateTypes = ruleDataSources.stream()
                .filter(currRuleDataSource -> currRuleDataSource.getDatasourceType() != null)
                .map(RuleDataSource::getDatasourceType)
                .collect(Collectors.toSet());
            for (Integer templateDataType : templateDateTypes) {
                TemplateDataSourceType templateDataSourceType = new TemplateDataSourceType(templateDataType, savedTemplate);
                templateDataSourceTypeDao.save(templateDataSourceType);
            }
            LOGGER.info("Success to save template data types. Template data types: {}", Arrays.toString(templateDateTypes.toArray()));
            LOGGER.info("Finished to synchronize the rule template. Template: {}", savedTemplate);
            rule.setTemplate(savedTemplate);
        } else if (templateInDb != null) {
            rule.setTemplate(templateInDb);
        } else if (templateNameInDb != null) {
            rule.setTemplate(templateNameInDb);
        } else {
            rule.setTemplate(template);
        }
    }

    public void saveRuleInfo(Rule savedRule, Template template, Project projectInDb, Set<AlarmConfig> alarmConfigs, Set<RuleVariable> ruleVariables,
                                                            Set<RuleDataSource> ruleDataSources, Set<RuleDataSourceMapping> ruleDataSourceMappings) {
        LOGGER.info("Start to save rule Info.");
        List<AlarmConfig> alarmConfigList = new ArrayList<>();
        for (AlarmConfig alarmConfig : alarmConfigs) {
            alarmConfig.setRule(savedRule);
            ruleMetricSycn(alarmConfig);
            alarmConfig.setTemplateOutputMeta(savedRule.getTemplate().getTemplateOutputMetas().iterator().next());
            alarmConfigList.add(alarmConfig);
        }
        List<RuleVariable> ruleVariablesList = new ArrayList<>();
        TemplateStatisticsInputMeta templateStatisticsInputMeta = savedRule.getTemplate().getStatisticAction().iterator().next();
        for (RuleVariable ruleVariable : ruleVariables) {
            ruleVariable.setRule(savedRule);
            ruleVariable.setTemplateStatisticsInputMeta(templateStatisticsInputMeta);
            ruleVariablesList.add(ruleVariable);
        }
        if (template.getId().intValue() != savedRule.getTemplate().getId().intValue()) {
            ruleVariablesList.sort(Comparator.comparing(RuleVariable::getId));
            List<TemplateMidTableInputMeta> templateMidTableInputMetaList = new ArrayList<>();
            if (savedRule.getTemplate().getTemplateMidTableInputMetas() != null) {
                templateMidTableInputMetaList.addAll(savedRule.getTemplate().getTemplateMidTableInputMetas());
                templateMidTableInputMetaList.sort(Comparator.comparing(TemplateMidTableInputMeta::getId));
                Iterator iterator = templateMidTableInputMetaList.iterator();
                for (RuleVariable ruleVariable : ruleVariablesList) {
                    ruleVariable.setTemplateMidTableInputMeta( (TemplateMidTableInputMeta) iterator.next());
                }
            }
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
                customAlarmConfig.setRuleMetric(alarmConfig.getRuleMetric());
                customAlarmConfig.setThreshold(alarmConfig.getThreshold());
                customAlarmConfig.setTemplateOutputMeta(templateOutputMetaInDb);
                customAlarmConfig.setCheckTemplate(alarmConfig.getCheckTemplate());
                Integer checkTemplateCode = alarmConfig.getCheckTemplate();
                if (alarmConfig.getCheckTemplate().equals(CheckTemplateEnum.FIXED_VALUE.getCode())
                    || checkTemplateCode.equals(CheckTemplateEnum.FULL_YEAR_RING_GROWTH.getCode())
                    || checkTemplateCode.equals(CheckTemplateEnum.HALF_YEAR_GROWTH.getCode())
                    || checkTemplateCode.equals(CheckTemplateEnum.SEASON_RING_GROWTH.getCode())
                    || checkTemplateCode.equals(CheckTemplateEnum.MONTH_RING_GROWTH.getCode())
                    || checkTemplateCode.equals(CheckTemplateEnum.WEEK_RING_GROWTH.getCode())
                    || checkTemplateCode.equals(CheckTemplateEnum.DAY_RING_GROWTH.getCode())
                    || checkTemplateCode.equals(CheckTemplateEnum.HOUR_RING_GROWTH.getCode())
                    || checkTemplateCode.equals(CheckTemplateEnum.YEAR_ON_YEAR.getCode())) {
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
    }
}
