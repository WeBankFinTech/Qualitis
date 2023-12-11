package com.webank.wedatasphere.qualitis.service;

import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constant.TemplateFunctionNameEnum;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.request.AddProjectRequest;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.rule.builder.*;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.*;
import com.webank.wedatasphere.qualitis.rule.entity.*;
import com.webank.wedatasphere.qualitis.rule.request.TemplateArgumentRequest;
import com.webank.wedatasphere.qualitis.rule.util.TemplateMidTableUtil;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author allenzhou@webank.com
 * @date 2021/8/17 17:45
 */
@Scope("prototype")
@Component
public class AddDirector {
    private AddRequestBuilder addRequestBuilder;
    private BdpClientHistory bdpClientHistory;
    private String projectName;
    private Project project;

    private String ruleDetail;
    private String ruleCnName;
    private String ruleName;
    private Rule rule;

    private String proxyUser;
    private String userName;

    private SubDepartmentPermissionService subDepartmentPermissionService;

    private ProjectService projectService;

    private MetaDataClient metaDataClient;

    private RuleMetricDao ruleMetricDao;

    private RuleDao ruleDao;

    private UserDao userDao;

    private ProjectDao projectDao;

    private RuleGroupDao ruleGroupDao;

    private RuleTemplateDao ruleTemplateDao;

    private BdpClientHistoryDao bdpClientHistoryDao;

    private LinkisConfig linkisConfig;

    private static Integer TWO = 2;
    private static Integer THREE = 3;

    public AddDirector(SubDepartmentPermissionService subDepartmentPermissionService, ProjectService projectService, MetaDataClient metaDataClient
        , RuleMetricDao ruleMetricDao, RuleDao ruleDao, UserDao userDao, ProjectDao projectDao, RuleGroupDao ruleGroupDao
        , RuleTemplateDao ruleTemplateDao, BdpClientHistoryDao bdpClientHistoryDao, LinkisConfig linkisConfig) {
        this.subDepartmentPermissionService = subDepartmentPermissionService;
        this.projectService = projectService;
        this.metaDataClient = metaDataClient;
        this.ruleMetricDao = ruleMetricDao;
        this.ruleDao = ruleDao;
        this.userDao = userDao;
        this.projectDao = projectDao;
        this.ruleGroupDao = ruleGroupDao;
        this.ruleTemplateDao = ruleTemplateDao;
        this.bdpClientHistoryDao = bdpClientHistoryDao;
        this.linkisConfig = linkisConfig;
    }

    public AddRequestBuilder expectColumnNotNull(String datasource, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
        boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        addRequestBuilder = new AddRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster());

        // Template
        Template templateInDb = ruleTemplateDao.findById(TemplateFunctionNameEnum.EXPECT_COLUMN_NOT_NULL.getCode());
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, datasource, TemplateFunctionNameEnum.EXPECT_COLUMN_NOT_NULL.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(datasource, deleteFailCheckResult, uploadRuleMetricValue, uploadAbnormalValue, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }

    private void getCurrentNameFromDatasource(Map<String, String> names, String datasource, String templateFuncName) throws UnExpectedRequestException {
        String[] datasourceStrs = datasource.split(SpecCharEnum.PERIOD.getValue());
        if (datasourceStrs.length < THREE) {
            throw new UnExpectedRequestException("Datasource param is illegle");
        }

        String currentProjectName = datasourceStrs[0] + "_" + datasourceStrs[1];
        String currentRuleName = datasourceStrs[2] + "_" + templateFuncName + "_" + UuidGenerator.generate();
        names.put("project", currentProjectName);
        names.put("rule", currentRuleName);

        if (bdpClientHistory != null) {
            bdpClientHistory.setProjectName(StringUtils.isNotBlank(projectName) ? projectName : currentProjectName);
            bdpClientHistory.setDatasource(datasourceStrs[0] + "_" + datasourceStrs[1] + "_" + datasourceStrs[2]);
            bdpClientHistory.setTemplateFunction(templateFuncName);
        }

        // Find in bdp-client history.
        BdpClientHistory currentBdpClientHistory = bdpClientHistoryDao.findByTemplateFunctionAndDatasourceAndProjectName(templateFuncName
            , datasourceStrs[0] + "_" + datasourceStrs[1] + "_" + datasourceStrs[2]
            , StringUtils.isNotBlank(projectName) ? projectName : currentProjectName);
        if (currentBdpClientHistory == null) {
            return;
        }
        bdpClientHistoryFix(currentBdpClientHistory, currentProjectName);
    }

    private void getCurrentNameFromDatasource(Map<String, String> names, String cluster, String datasource, String templateFuncName) throws UnExpectedRequestException {
        String[] multiDatasourceStrs = datasource.split(SpecCharEnum.VERTICAL_BAR.getValue());
        String sourcStr = multiDatasourceStrs[0];
        String[] datasourceStrs = sourcStr.split(SpecCharEnum.COLON.getValue());
        if (datasourceStrs.length > TWO) {
            throw new UnExpectedRequestException("Datasource param is illegle");
        }
        String[] datasources = datasourceStrs[0].split(SpecCharEnum.PERIOD.getValue());
        if (datasources.length != TWO) {
            throw new UnExpectedRequestException("Datasource param is illegle");
        }
        String database = datasources[0];
        String table = datasources[1];
        String currentProjectName = cluster + "_" + database;
        String currentRuleName = table + "_" + templateFuncName + "_" + UuidGenerator.generate();
        names.put("project", currentProjectName);
        names.put("rule", currentRuleName);

        if (bdpClientHistory != null) {
            bdpClientHistory.setProjectName(StringUtils.isNotBlank(projectName) ? projectName : currentProjectName);
            bdpClientHistory.setDatasource(cluster + "_" + database + "_" + table);
            bdpClientHistory.setTemplateFunction(templateFuncName);
        }

        // Find in bdp-client history.
        BdpClientHistory currentBdpClientHistory = bdpClientHistoryDao.findByTemplateFunctionAndDatasourceAndProjectName(templateFuncName
            , cluster + "_" + database + "_" + table
            , StringUtils.isNotBlank(projectName) ? projectName : currentProjectName);
        if (currentBdpClientHistory == null) {
            return;
        }
        bdpClientHistoryFix(currentBdpClientHistory, currentProjectName);
    }

    private void bdpClientHistoryFix(BdpClientHistory currentBdpClientHistory, String currentProjectName) {
        Rule ruleInDb = ruleDao.findById(currentBdpClientHistory.getRuleId());
        if (StringUtils.isBlank(ruleName)) {
            if (StringUtils.isNotBlank(projectName)) {
                if (ruleInDb.getProject().getName().equals(projectName)) {
                    setRule(ruleInDb);
                    setProject(ruleInDb.getProject());
                }
            } else {
                if (ruleInDb.getProject().getName().equals(currentProjectName)) {
                    setRule(ruleInDb);
                    setProject(ruleInDb.getProject());
                }
            }
        } else {
            Project currentProject = projectDao.findByNameAndCreateUser(currentBdpClientHistory.getProjectName(), userName);
            if (currentProject != null) {
                Rule currentRule = ruleDao.findByProjectAndRuleName(currentProject, ruleName);
                if (currentRule != null) {
                    setRule(currentRule);
                    setProject(currentProject);
                }
            }
            if (ruleInDb.getName().equals(ruleName)) {
                setRule(ruleInDb);
                setProject(ruleInDb.getProject());
            } else {
                bdpClientHistoryDao.delete(currentBdpClientHistory);
            }
        }

    }

    private void setProject(Project project, String projectName) throws UnExpectedRequestException {
        addRequestBuilder.setUserName(userName);
        addRequestBuilder.setProxyUser(proxyUser);
        if (project == null) {
            Project projectInDb = projectDao.findByNameAndCreateUser(projectName, userName);
            if (projectInDb != null) {
                addRequestBuilder.setProject(projectInDb);
                return;
            }
            User user = userDao.findByUsername(userName);
            AddProjectRequest addProjectRequest = new AddProjectRequest();
            addProjectRequest.setProjectName(projectName);
            addProjectRequest.setDescription("This is auto created for bdp-client.");
            projectService.addProject(addProjectRequest, user.getId());
            addRequestBuilder.setProject(projectDao.findByNameAndCreateUser(projectName, userName));
        } else {
            addRequestBuilder.setProject(project);
        }
    }

    private void setRule(Rule rule, String ruleName) {
        addRequestBuilder.setRuleDetail(ruleDetail);
        addRequestBuilder.setRuleCnName(ruleCnName);
        if (rule == null) {
            addRequestBuilder.setRuleName(ruleName);
        } else {
            addRequestBuilder.setRuleName(rule.getName());
        }
    }

    public AddRequestBuilder expectColumnsPrimaryNotRepeat(String datasource, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
        boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        addRequestBuilder = new AddRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster());

        // Template
        Template templateInDb = ruleTemplateDao.findById(TemplateFunctionNameEnum.EXPECT_COLUMNS_PRIMARY_NOT_REPEAT.getCode());
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, datasource, TemplateFunctionNameEnum.EXPECT_COLUMNS_PRIMARY_NOT_REPEAT.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(datasource, deleteFailCheckResult, uploadRuleMetricValue, uploadAbnormalValue, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }

    public AddRequestBuilder expectTableRows(String datasource, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
        boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        addRequestBuilder = new AddRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster());
        // Template
        Template templateInDb = ruleTemplateDao.findById(TemplateFunctionNameEnum.EXPECT_TABLE_ROWS.getCode());
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, datasource, TemplateFunctionNameEnum.EXPECT_TABLE_ROWS.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(datasource, deleteFailCheckResult, uploadRuleMetricValue, uploadAbnormalValue, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }

    public AddRequestBuilder expectColumnAvg(String datasource, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
        boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        addRequestBuilder = new AddRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster());
        // Template
        Template templateInDb = ruleTemplateDao.findById(TemplateFunctionNameEnum.EXPECT_COLUMNS_AVG.getCode());
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, datasource, TemplateFunctionNameEnum.EXPECT_COLUMNS_AVG.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(datasource, deleteFailCheckResult, uploadRuleMetricValue, uploadAbnormalValue, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }

    public AddRequestBuilder expectColumnSum(String datasource, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
        boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        addRequestBuilder = new AddRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster());
        // Template
        Template templateInDb = ruleTemplateDao.findById(TemplateFunctionNameEnum.EXPECT_COLUMNS_SUM.getCode());
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, datasource, TemplateFunctionNameEnum.EXPECT_COLUMNS_SUM.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(datasource, deleteFailCheckResult, uploadRuleMetricValue, uploadAbnormalValue, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }
    public AddRequestBuilder expectColumnMax(String datasource, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
        boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        addRequestBuilder = new AddRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster());
        // Template
        Template templateInDb = ruleTemplateDao.findById(TemplateFunctionNameEnum.EXPECT_COLUMNS_MAX.getCode());
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, datasource, TemplateFunctionNameEnum.EXPECT_COLUMNS_MAX.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(datasource, deleteFailCheckResult, uploadRuleMetricValue, uploadAbnormalValue, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }

    public AddRequestBuilder expectColumnMin(String datasource, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
        boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        addRequestBuilder = new AddRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster());
        // Template
        Template templateInDb = ruleTemplateDao.findById(TemplateFunctionNameEnum.EXPECT_COLUMNS_MIN.getCode());
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, datasource, TemplateFunctionNameEnum.EXPECT_COLUMNS_MIN.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(datasource, deleteFailCheckResult, uploadRuleMetricValue, uploadAbnormalValue, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }

    public AddRequestBuilder expectColumnMatchRegx(String datasource, String regx, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
        boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        addRequestBuilder = new AddRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster());
        // Template
        Template templateInDb = ruleTemplateDao.findById(TemplateFunctionNameEnum.EXPECT_COLUMNS_MATCH_REGX.getCode());
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, datasource, TemplateFunctionNameEnum.EXPECT_COLUMNS_MATCH_REGX.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(datasource, regx, deleteFailCheckResult, uploadRuleMetricValue, uploadAbnormalValue, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }

    public AddRequestBuilder expectColumnMatchDate(String datasource, String dateRegx, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
        boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        addRequestBuilder = new AddRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster());
        // Template
        Template templateInDb = ruleTemplateDao.findById(9L);
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, datasource, TemplateFunctionNameEnum.EXPECT_COLUMNS_MATCH_DATE.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(datasource, dateRegx, deleteFailCheckResult, uploadRuleMetricValue, uploadAbnormalValue, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }

    public AddRequestBuilder expectColumnMatchNum(String datasource, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
        boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        addRequestBuilder = new AddRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster());
        // Template
        Template templateInDb = ruleTemplateDao.findById(TemplateFunctionNameEnum.EXPECT_COLUMNS_MATCH_NUM.getCode());
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, datasource, TemplateFunctionNameEnum.EXPECT_COLUMNS_MATCH_NUM.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(datasource, deleteFailCheckResult, uploadRuleMetricValue, uploadAbnormalValue, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }

    public AddRequestBuilder expectColumnInList(String datasource, String seriesOfValues, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
        boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        addRequestBuilder = new AddRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster());
        // Template
        Template templateInDb = ruleTemplateDao.findById(TemplateFunctionNameEnum.EXPECT_COLUMNS_IN_LIST.getCode());
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, datasource, TemplateFunctionNameEnum.EXPECT_COLUMNS_IN_LIST.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(datasource, seriesOfValues, deleteFailCheckResult, uploadRuleMetricValue, uploadAbnormalValue, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }

    public AddRequestBuilder expectColumnInListNewValueCheck(String datasource, String seriesOfValues, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
        boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        addRequestBuilder = new AddRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster());
        // Template
        Template templateInDb = ruleTemplateDao.findById(TemplateFunctionNameEnum.EXPECT_COLUMNS_IN_LIST_NEW_VALUE.getCode());
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, datasource, TemplateFunctionNameEnum.EXPECT_COLUMNS_IN_LIST_NEW_VALUE.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(datasource, seriesOfValues, deleteFailCheckResult, uploadRuleMetricValue, uploadAbnormalValue, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }

    public AddRequestBuilder expectColumnInRange(String datasource, String range, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
        boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        addRequestBuilder = new AddRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster());
        // Template
        Template templateInDb = ruleTemplateDao.findById(TemplateFunctionNameEnum.EXPECT_COLUMNS_IN_RANGE.getCode());
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, datasource, TemplateFunctionNameEnum.EXPECT_COLUMNS_IN_RANGE.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(datasource, range, deleteFailCheckResult, uploadRuleMetricValue, uploadAbnormalValue, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }

    public AddRequestBuilder expectColumnInRangeNewValueCheck(String datasource, int min, String range, int max, boolean deleteFailCheckResult
        , boolean uploadRuleMetricValue, boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams) throws Exception {
        addRequestBuilder = new AddRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster());
        // Template
        Template templateInDb = ruleTemplateDao.findById(TemplateFunctionNameEnum.EXPECT_COLUMNS_IN_RANGE_NEW_VALUE.getCode());
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, datasource, TemplateFunctionNameEnum.EXPECT_COLUMNS_IN_RANGE_NEW_VALUE.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));
        List<TemplateArgumentRequest> templateArgumentRequests = new ArrayList<>();
        // Handle special template argument
        for (TemplateMidTableInputMeta templateMidTableInputMeta : templateInDb.getTemplateMidTableInputMetas()) {
            if (TemplateInputTypeEnum.MAXIMUM.getCode().equals(templateMidTableInputMeta.getInputType())) {
                templateArgumentRequests.add(new TemplateArgumentRequest(templateMidTableInputMeta.getId(), templateMidTableInputMeta.getInputType(), InputActionStepEnum.TEMPLATE_INPUT_META.getCode(), max + ""));
            } else if (TemplateInputTypeEnum.MINIMUM.getCode().equals(templateMidTableInputMeta.getInputType())) {
                templateArgumentRequests.add(new TemplateArgumentRequest(templateMidTableInputMeta.getId(), templateMidTableInputMeta.getInputType(), InputActionStepEnum.TEMPLATE_INPUT_META.getCode(), min + ""));
            } else if (TemplateInputTypeEnum.INTERMEDIATE_EXPRESSION.getCode().equals(templateMidTableInputMeta.getInputType())) {
                templateArgumentRequests.add(new TemplateArgumentRequest(templateMidTableInputMeta.getId(), templateMidTableInputMeta.getInputType(), InputActionStepEnum.TEMPLATE_INPUT_META.getCode(), range));
            }
        }
        addRequestBuilder.basicInfoWithDataSource(datasource, templateArgumentRequests, deleteFailCheckResult, uploadRuleMetricValue, uploadAbnormalValue, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }

    public AddRequestBuilder expectColumnMatchIdCard(String datasource, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
        boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        addRequestBuilder = new AddRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster());
        // Template
        Template templateInDb = ruleTemplateDao.findById(TemplateFunctionNameEnum.EXPECT_COLUMNS_MATCH_ID_CARD.getCode());
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, datasource, TemplateFunctionNameEnum.EXPECT_COLUMNS_MATCH_ID_CARD.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(datasource, deleteFailCheckResult, uploadRuleMetricValue, uploadAbnormalValue, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }

    public AddRequestBuilder expectColumnLogicCheck(String datasource, String condition1, String condition2, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
        boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        addRequestBuilder = new AddRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster());
        // Template
        Template templateInDb = ruleTemplateDao.findById(TemplateFunctionNameEnum.EXPECT_COLUMNS_LOGIC_CHECK.getCode());
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, datasource, TemplateFunctionNameEnum.EXPECT_COLUMNS_LOGIC_CHECK.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(datasource, condition1, condition2, deleteFailCheckResult, uploadRuleMetricValue, uploadAbnormalValue, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }

    public AddRequestBuilder expectColumnNotEmpty(String datasource, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
        boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        addRequestBuilder = new AddRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster());
        // Template
        Template templateInDb = ruleTemplateDao.findById(TemplateFunctionNameEnum.EXPECT_COLUMNS_NOT_EMPTY.getCode());
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, datasource, TemplateFunctionNameEnum.EXPECT_COLUMNS_NOT_EMPTY.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(datasource, deleteFailCheckResult, uploadRuleMetricValue, uploadAbnormalValue, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }

    public AddRequestBuilder expectColumnNotNullNotEmpty(String datasource, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
        boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        addRequestBuilder = new AddRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster());
        // Template
        Template templateInDb = ruleTemplateDao.findById(TemplateFunctionNameEnum.EXPECT_COLUMNS_NOT_NULL_NOT_EMPTY.getCode());
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, datasource, TemplateFunctionNameEnum.EXPECT_COLUMNS_NOT_NULL_NOT_EMPTY.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(datasource, deleteFailCheckResult, uploadRuleMetricValue, uploadAbnormalValue, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }

    public AddRequestBuilder expectTableConsistent(String cluster, String datasource, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
        boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        addRequestBuilder = new AddMultiRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster());
        // Template
        Template templateInDb = ruleTemplateDao.findById(TemplateFunctionNameEnum.EXPECT_TABLE_CONSISTENT.getCode());
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, cluster, datasource, TemplateFunctionNameEnum.EXPECT_TABLE_CONSISTENT.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(cluster, datasource, deleteFailCheckResult, uploadRuleMetricValue, uploadAbnormalValue, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }

    /**
     * Without compare cols, the mappings cols are the same. Only mappings.
     * @param cluster
     * @param datasource
     * @param mappings
     * @param deleteFailCheckResult
     * @param uploadRuleMetricValue
     * @param uploadAbnormalValue
     * @param alertInfo
     * @param abortOnFailure
     * @param execParams
     * @return
     * @throws Exception
     */
    public AddRequestBuilder expectSpecifiedColumnConsistent(String cluster, String datasource, String mappings, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
        boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        addRequestBuilder = new AddMultiRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster());
        // Template
        Template templateInDb = ruleTemplateDao.findById(TemplateFunctionNameEnum.EXPECT_SPECIFIED_COLUMN_CONSISTENT.getCode());
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, cluster, datasource, TemplateFunctionNameEnum.EXPECT_SPECIFIED_COLUMN_CONSISTENT.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(cluster, datasource, mappings, deleteFailCheckResult, uploadRuleMetricValue, uploadAbnormalValue, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }

    /**
     * With compare cols.
     * @param cluster
     * @param datasource
     * @param mapping
     * @param compareCols
     * @param deleteFailCheckResult
     * @param uploadRuleMetricValue
     * @param uploadAbnormalValue
     * @param alertInfo
     * @param abortOnFailure
     * @param execParams
     * @return
     * @throws Exception
     */
    public AddRequestBuilder expectSpecifiedColumnConsistent(String cluster, String datasource, String mapping, String compareCols, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
        boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        addRequestBuilder = new AddMultiRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster());
        // Template
        Template templateInDb = ruleTemplateDao.findById(TemplateFunctionNameEnum.EXPECT_SPECIFIED_COLUMN_CONSISTENT.getCode());
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, cluster, datasource, TemplateFunctionNameEnum.EXPECT_SPECIFIED_COLUMN_CONSISTENT.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(cluster, datasource, mapping, compareCols, deleteFailCheckResult, uploadRuleMetricValue, uploadAbnormalValue, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }

    public AddRequestBuilder expectPartitionAmountCount(String datasource, String alertInfo, boolean abortOnFailure)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        addRequestBuilder = new AddFileRuleRequestBuilder(ruleMetricDao, ruleGroupDao);
        // Template
        Template templateInDb = ruleTemplateDao.findByName("分区数");
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, datasource, TemplateFunctionNameEnum.EXPECT_FILE_AMOUNT_COUNT.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(datasource, abortOnFailure, alertInfo);
        return addRequestBuilder;
    }

    public AddRequestBuilder expectFileAmountCount(String datasource, String alertInfo, boolean abortOnFailure)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        addRequestBuilder = new AddFileRuleRequestBuilder(ruleMetricDao, ruleGroupDao);
        // Template
        Template templateInDb = ruleTemplateDao.findByName("目录文件数");
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, datasource, TemplateFunctionNameEnum.EXPECT_FILE_AMOUNT_COUNT.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(datasource, abortOnFailure, alertInfo);
        return addRequestBuilder;
    }

    public AddRequestBuilder expectFileSizePass(String datasource, String alertInfo, boolean abortOnFailure)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        addRequestBuilder = new AddFileRuleRequestBuilder(ruleMetricDao);
        // Template
        Template templateInDb = ruleTemplateDao.findByName("目录容量");
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, datasource, TemplateFunctionNameEnum.EXPECT_FILE_SIZE_PASS.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(datasource, abortOnFailure, alertInfo);
        return addRequestBuilder;
    }

    public AddRequestBuilder expectSqlPass(String cluster, String sql, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        addRequestBuilder = new AddCustomRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster(), subDepartmentPermissionService);
        // Project and rule related with builder.
        setProject(project, projectName);
        setRule(rule, ruleName);

        addRequestBuilder.basicInfoWithDataSource(cluster, sql, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }

    public AddRequestBuilder expectLinesNotRepeat(String datasource, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
        boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        addRequestBuilder = new AddRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster());

        // Template
        Template templateInDb = ruleTemplateDao.findById(TemplateFunctionNameEnum.EXPECT_LINES_NOT_REPEAT.getCode());
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, datasource, TemplateFunctionNameEnum.EXPECT_COLUMNS_PRIMARY_NOT_REPEAT.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(datasource, deleteFailCheckResult, uploadRuleMetricValue, uploadAbnormalValue, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }

    public AddRequestBuilder expectDataNotRepeat(String datasource, boolean deleteFailCheckResult, boolean uploadRuleMetricValue,
        boolean uploadAbnormalValue, String alertInfo, boolean abortOnFailure, String execParams)
            throws Exception {
        addRequestBuilder = new AddRuleRequestBuilder(ruleMetricDao, ruleGroupDao, metaDataClient, linkisConfig.getDatasourceCluster());

        // Template
        Template templateInDb = ruleTemplateDao.findById(TemplateFunctionNameEnum.EXPECT_DATA_NOT_REPEAT.getCode());
        addRequestBuilder.setTemplate(templateInDb);
        // Project and rule related with builder.
        Map<String, String> names = new HashMap<>(TWO);
        getCurrentNameFromDatasource(names, datasource, TemplateFunctionNameEnum.EXPECT_COLUMNS_PRIMARY_NOT_REPEAT.getName());
        setProject(project, StringUtils.isNotBlank(projectName) ? projectName : names.get("project"));
        setRule(rule, StringUtils.isNotBlank(ruleName) ? ruleName : names.get("rule"));

        addRequestBuilder.basicInfoWithDataSource(datasource, deleteFailCheckResult, uploadRuleMetricValue, uploadAbnormalValue, alertInfo, abortOnFailure, execParams);
        return addRequestBuilder;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getRuleDetail() {
        return ruleDetail;
    }

    public void setRuleDetail(String ruleDetail) {
        this.ruleDetail = ruleDetail;
    }

    public String getRuleCnName() {
        return ruleCnName;
    }

    public void setRuleCnName(String ruleCnName) {
        this.ruleCnName = ruleCnName;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public BdpClientHistory getBdpClientHistory() {
        return bdpClientHistory;
    }

    public void setBdpClientHistory(BdpClientHistory bdpClientHistory) {
        this.bdpClientHistory = bdpClientHistory;
    }

    public void reset() {
        this.setBdpClientHistory(null);
        this.setProject(null);
        this.setRule(null);
        this.setRuleName("");
        this.setRuleCnName("");
        this.setRuleDetail("");
        this.setProjectName("");
    }

    public void resetRule() {
        this.setRule(null);
        this.setRuleName("");
        this.setRuleCnName("");
        this.setRuleDetail("");
    }
}
