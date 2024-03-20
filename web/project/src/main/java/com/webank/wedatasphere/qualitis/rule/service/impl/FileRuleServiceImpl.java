package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.OperateTypeEnum;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.project.service.ProjectEventService;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.constant.RuleLockRangeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.BdpClientHistoryDao;
import com.webank.wedatasphere.qualitis.rule.dao.ExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.entity.*;
import com.webank.wedatasphere.qualitis.rule.exception.RuleLockException;
import com.webank.wedatasphere.qualitis.rule.request.*;
import com.webank.wedatasphere.qualitis.rule.response.RuleDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import com.webank.wedatasphere.qualitis.rule.service.*;
import com.webank.wedatasphere.qualitis.scheduled.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author allenzhou
 */
@Service
public class FileRuleServiceImpl extends AbstractRuleService implements FileRuleService {
    @Autowired
    private RuleTemplateService ruleTemplateService;
    @Autowired
    private ProjectEventService projectEventService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private AlarmConfigService alarmConfigService;
    @Autowired
    private RuleDataSourceService ruleDataSourceService;

    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private RuleGroupDao ruleGroupDao;
    @Autowired
    private BdpClientHistoryDao bdpClientHistoryDao;
    @Autowired
    private ExecutionParametersDao executionParametersDao;

    @Autowired
    private RuleLockService ruleLockService;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileRuleServiceImpl.class);

    private HttpServletRequest httpServletRequest;

    public FileRuleServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> addRule(AddFileRuleRequest request, String loginUser, boolean groupRules) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        return addRuleReal(request, loginUser, groupRules);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> addRuleForOuter(AbstractCommonRequest request, String loginUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        return addRuleReal((AddFileRuleRequest) request, loginUser, false);
    }

    private GeneralResponse<RuleResponse> addRuleReal(AddFileRuleRequest request, String loginUser, boolean groupRules)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        // Check Arguments
        AddFileRuleRequest.checkRequest(request, false);
        if (request.getRuleEnable() == null) {
            request.setRuleEnable(true);
        }
        if (request.getUnionAll() == null) {
            request.setUnionAll(false);
        }
        // Generate Template, TemplateOutputMeta and save
        Template template = ruleTemplateService.checkRuleTemplate(request.getRuleTemplateId());

//        ruleTemplateService.checkAccessiblePermission(template);

        // Check existence of project
        Project projectInDb = projectService.checkProjectExistence(request.getProjectId(),
                loginUser);
        String nowDate = QualitisConstants.PRINT_TIME_FORMAT.format(new Date());
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);
        // Check unique of rule name
        ruleService.checkRuleName(request.getRuleName(), request.getWorkFlowName(), request.getWorkFlowVersion(), projectInDb, null);
        //check the same rule name number
        ruleService.checkRuleNameNumber(request.getRuleName(), projectInDb);
        // Check cluster support
        ruleDataSourceService.checkDataSourceClusterSupport(request.getDatasource().iterator().next().getClusterName());
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
            if (CollectionUtils.isNotEmpty(ruleGroup.getRuleDataSources())) {
                groupRules = true;
            }
        } else {
            if (StringUtils.isEmpty(ruleGroupName)) {
                ruleGroupName = "Group_" + UuidGenerator.generate();
            }
            ruleGroup = ruleGroupDao.saveRuleGroup(new RuleGroup(ruleGroupName, projectInDb.getId()));
        }

        // Save rule.
        Rule newRule = new Rule();
        setAddFileInfo(request, loginUser, template, projectInDb, nowDate, ruleGroup, newRule);

        boolean cs = false;
        if (StringUtils.isNotBlank(request.getCsId())) {
            newRule.setCsId(request.getCsId());
            cs = true;
        }

        setExecutionParametersInfo(request, groupRules, projectInDb, newRule);
        Rule savedRule = ruleDao.saveRule(newRule);
        LOGGER.info("Succeed to save file rule, rule id: {}", savedRule.getId());

        super.recordEvent(loginUser, savedRule, OperateTypeEnum.CREATE_RULES);

        List<AlarmConfig> savedAlarmConfigs = new ArrayList<>();
        extractionMethod(loginUser, cs, savedRule, savedAlarmConfigs, request.getAlarm(), request.getFileAlarmVariable(), request.getUploadRuleMetricValue(), request.getUploadAbnormalValue(), request.getDeleteFailCheckResult(), request.getDatasource().iterator().next());

        RuleResponse response = new RuleResponse(savedRule);
        LOGGER.info("Succeed to add file rule, rule id: {}", savedRule.getId());
        return new GeneralResponse<>("200", "{&SUCCEED_TO_ADD_FILE_RULE}", response);
    }

    private void extractionMethod(String loginUser, boolean cs, Rule savedRule, List<AlarmConfig> savedAlarmConfigs, Boolean alarm, List<FileAlarmConfigRequest> fileAlarmVariable, Boolean uploadRuleMetricValue, Boolean uploadAbnormalValue, Boolean deleteFailCheckResult, DataSourceRequest datasource) throws UnExpectedRequestException, IOException, PermissionDeniedRequestException {
        if (alarm) {
            fileAlarmVariable.stream().map(u -> {
                u.setUploadRuleMetricValue(uploadRuleMetricValue != null ? uploadRuleMetricValue : false);
                u.setUploadAbnormalValue(uploadAbnormalValue != null ? uploadAbnormalValue : false);
                u.setDeleteFailCheckResult(deleteFailCheckResult != null ? deleteFailCheckResult : false);
                return u;
            }).collect(Collectors.toList());
            savedAlarmConfigs = alarmConfigService.checkAndSaveFileAlarmVariable(fileAlarmVariable, savedRule, loginUser, datasource);
            LOGGER.info("Succeed to save alarm_configs, alarm_configs: {}", savedAlarmConfigs);
        }
        List<RuleDataSource> ruleDataSources = new ArrayList<>();
        ruleDataSources.add(ruleDataSourceService.checkAndSaveFileRuleDataSource(datasource, savedRule, cs, loginUser));

        savedRule.setAlarmConfigs(new HashSet<>(savedAlarmConfigs));
        savedRule.setRuleDataSources(new HashSet<>(ruleDataSources));
    }

    private void setExecutionParametersInfo(AddFileRuleRequest request, boolean groupRules, Project projectInDb, Rule newRule) {
        if (StringUtils.isNotBlank(request.getExecutionParametersName())) {
            ExecutionParameters executionParameters = executionParametersDao.findByNameAndProjectId(request.getExecutionParametersName(), projectInDb.getId());
            if (groupRules || executionParameters != null) {
                newRule.setExecutionParametersName(request.getExecutionParametersName());
                newRule.setUnionAll(executionParameters.getUnionAll());
                request.setUploadAbnormalValue(executionParameters.getUploadAbnormalValue());
                request.setUploadRuleMetricValue(executionParameters.getUploadRuleMetricValue());
                request.setDeleteFailCheckResult(executionParameters.getDeleteFailCheckResult());
//                if (StringUtils.isNotEmpty(executionParameters.getFilter())) {
//                    request.setDatasource(request.getDatasource().stream().map((dataSourceRequest) -> {
//                        dataSourceRequest.setFilter(executionParameters.getFilter());
//                        return dataSourceRequest;
//                    }).collect(Collectors.toList()));
//                }
            }
        } else {
            setFileAddInfo(request, newRule);
        }
    }

    private void setAddFileInfo(AddFileRuleRequest request, String loginUser, Template template, Project projectInDb, String nowDate, RuleGroup ruleGroup, Rule newRule) {
        newRule.setTemplate(template);
        newRule.setName(request.getRuleName());
        newRule.setCnName(request.getRuleCnName());
        newRule.setRuleTemplateName(template.getName());
        newRule.setRuleType(RuleTypeEnum.FILE_TEMPLATE_RULE.getCode());
        newRule.setDetail(request.getRuleDetail());
        newRule.setAlarm(request.getAlarm());
        newRule.setCreateUser(loginUser);
        newRule.setProject(projectInDb);
        newRule.setRuleGroup(ruleGroup);
        newRule.setWorkFlowName(request.getWorkFlowName());
        newRule.setWorkFlowVersion(request.getWorkFlowVersion());
        newRule.setWorkFlowSpace(request.getWorkFlowSpace());
        newRule.setNodeName(request.getNodeName());
        newRule.setRuleNo(request.getRuleNo());
        newRule.setCreateTime(nowDate);
        newRule.setBashContent(request.getBashContent());
    }

    private void setFileAddInfo(AddFileRuleRequest request, Rule newRule) {
        newRule.setExecutionParametersName(null);
        newRule.setAlert(request.getAlert());
        if (request.getAlert() != null && request.getAlert()) {
            newRule.setAlertLevel(request.getAlertLevel());
            newRule.setAlertReceiver(request.getAlertReceiver());
        }
        newRule.setDeleteFailCheckResult(request.getDeleteFailCheckResult());
        newRule.setAbortOnFailure(request.getAbortOnFailure());
        newRule.setEnable(request.getRuleEnable());
        newRule.setUnionAll(request.getUnionAll());
        newRule.setAbnormalCluster(StringUtils.isNotBlank(request.getAbnormalCluster()) ? request.getAbnormalCluster() : null);
        newRule.setAbnormalDatabase(StringUtils.isNotBlank(request.getAbnormalDatabase()) ? request.getAbnormalDatabase() : null);
        newRule.setAbnormalProxyUser(StringUtils.isNotBlank(request.getAbnormalProxyUser()) ? request.getAbnormalProxyUser() : null);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse deleteRule(DeleteFileRuleRequest request, String loginUser) throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check Arguments
        DeleteFileRuleRequest.checkRequest(request);
        // Check existence of rule
        Rule ruleInDb = ruleDao.findById(request.getRuleId());
        if (ruleInDb == null || !ruleInDb.getRuleType().equals(RuleTypeEnum.FILE_TEMPLATE_RULE.getCode())) {
            throw new UnExpectedRequestException("Rule id [" + request.getRuleId() + "]) {&IS_NOT_A_RULE_WITH_TEMPLATE}");
        }
        Project projectInDb = projectService.checkProjectExistence(ruleInDb.getProject().getId(),
                loginUser);
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);

        return deleteRuleReal(ruleInDb);
    }

    @Override
    public GeneralResponse deleteRuleReal(Rule rule) throws UnExpectedRequestException {
        // Delete bdp-client history
        BdpClientHistory bdpClientHistory = bdpClientHistoryDao.findByRuleId(rule.getId());
        if (bdpClientHistory != null) {
            bdpClientHistoryDao.delete(bdpClientHistory);
        }
        // Delete rule
        ruleDao.deleteRule(rule);

        super.recordEvent(HttpUtils.getUserName(httpServletRequest), rule, OperateTypeEnum.DELETE_RULES);
        // Delete template of custom rule
        // ruleTemplateService.deleteFileRuleTemplate(rule.getTemplate().getId());
        LOGGER.info("Succeed to delete file rule. rule id: {}", rule.getId());
        return new GeneralResponse<>("200", "{&DELETE_FILE_RULE_SUCCESSFULLY}", null);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> modifyRuleDetail(ModifyFileRuleRequest request, String loginUser, boolean groupRules)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        return modifyRuleDetailReal(request, loginUser, groupRules);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    @Override
    public GeneralResponse<RuleResponse> modifyRuleDetailWithLock(ModifyFileRuleRequest request, String loginUser, boolean groupRules) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException, RuleLockException {
        if (!ruleLockService.tryAcquire(request.getRuleId(), RuleLockRangeEnum.RULE, loginUser)) {
            LOGGER.warn("Failed to acquire lock");
            throw new RuleLockException("{&RULE_LOCK_ACQUIRE_FAILED}");
        }
        try {
            return modifyRuleDetailReal(request, loginUser, groupRules);
        } finally {
            ruleLockService.release(request.getRuleId(), RuleLockRangeEnum.RULE, loginUser);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> modifyRuleDetailForOuter(ModifyFileRuleRequest modifyRuleRequest, String userName)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        return modifyRuleDetailReal(modifyRuleRequest, userName, false);
    }

    private GeneralResponse<RuleResponse> modifyRuleDetailReal(ModifyFileRuleRequest request, String loginUser, boolean groupRules)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        // Check Arguments
        ModifyFileRuleRequest.checkRequest(request);
        if (request.getRuleEnable() == null) {
            request.setRuleEnable(true);
        }
        if (request.getUnionAll() == null) {
            request.setUnionAll(false);
        }
        // Check existence of rule
        Rule ruleInDb = ruleDao.findById(request.getRuleId());
        if (ruleInDb == null) {
            throw new UnExpectedRequestException("rule_id [" + request.getRuleId() + "] {&DOES_NOT_EXIST}");
        }
        Project projectInDb = checkProject(request, loginUser, ruleInDb);
        String nowDate = QualitisConstants.PRINT_TIME_FORMAT.format(new Date());
        String csId = request.getCsId();
        boolean cs = false;
        if (StringUtils.isNotBlank(csId)) {
            cs = true;
            ruleInDb.setCsId(csId);
        } else {
            LOGGER.info("Delete context service ID. rule id: {}， cs_id: {}", ruleInDb.getId(), ruleInDb.getCsId());
            ruleInDb.setCsId(csId);
            LOGGER.info("After delete context service ID. rule id: {}， cs_id: {}", ruleInDb.getId(), ruleInDb.getCsId());
        }
        if (CollectionUtils.isNotEmpty(ruleInDb.getRuleGroup().getRuleDataSources())) {
            groupRules = true;
        }
        String ruleGroupName = request.getRuleGroupName();
        if (StringUtils.isNotEmpty(ruleGroupName)) {
            ruleInDb.getRuleGroup().setRuleGroupName(ruleGroupName);
            ruleGroupDao.saveRuleGroup(ruleInDb.getRuleGroup());
        }
        if (StringUtils.isNotBlank(request.getExecutionParametersName())) {
            ExecutionParameters executionParameters = executionParametersDao.findByNameAndProjectId(request.getExecutionParametersName(), projectInDb.getId());
            if (groupRules || executionParameters != null) {
                ruleInDb.setExecutionParametersName(request.getExecutionParametersName());
                ruleInDb.setUnionAll(executionParameters.getUnionAll());
                request.setUploadAbnormalValue(executionParameters.getUploadAbnormalValue());
                request.setUploadRuleMetricValue(executionParameters.getUploadRuleMetricValue());
                request.setDeleteFailCheckResult(executionParameters.getDeleteFailCheckResult());
//                if (StringUtils.isNotEmpty(executionParameters.getFilter())) {
//                    request.getDatasource().iterator().next().setFilter(executionParameters.getFilter());
//                }
            }
        } else {
            setUpdateFileInfo(request, ruleInDb);
        }
        // Check existence of project rule name
        ruleService.checkRuleName(request.getRuleName(), request.getWorkFlowName(), request.getWorkFlowVersion(), projectInDb, ruleInDb.getId());
        //check the same rule name number
        ruleService.checkRuleNameNumber(request.getRuleName(), projectInDb);
        // Check cluster name support
        ruleDataSourceService.checkDataSourceClusterSupport(request.getDatasource().iterator().next().getClusterName());
        // Delete alarm config by file rule
        alarmConfigService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all alarm_config. rule id: {}", ruleInDb.getId());
        // Delete template of file rule
        //ruleTemplateService.deleteFileRuleTemplate(ruleInDb.getTemplate().getId());
        LOGGER.info("Succeed to delete file rule template. rule id: {}", request.getRuleId());

        // Delete rule datasource of file rule
        ruleDataSourceService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all rule_dataSources. rule id: {}", ruleInDb.getId());
        // Check existence of rule
        AddFileRuleRequest addFileRuleRequest = new AddFileRuleRequest();
        BeanUtils.copyProperties(request, addFileRuleRequest);
        //Template template = ruleTemplateService.addFileTemplate(addFileRuleRequest);
        Template template = ruleTemplateService.checkRuleTemplate(request.getRuleTemplateId());
//        ruleTemplateService.checkAccessiblePermission(template);

        setBaseForRule(request, loginUser, ruleInDb, nowDate, template);
        Rule savedRule = ruleDao.saveRule(ruleInDb);
        LOGGER.info("Succeed to save file rule, rule id: {}", savedRule.getId());

        super.recordEvent(loginUser, savedRule, OperateTypeEnum.MODIFY_RULES);

        List<AlarmConfig> savedAlarmConfigs = new ArrayList<>();
        extractionMethod(loginUser, cs, savedRule, savedAlarmConfigs, request.getAlarm(), request.getFileAlarmVariable(), request.getUploadRuleMetricValue()
                , request.getUploadAbnormalValue(), request.getDeleteFailCheckResult(), request.getDatasource().iterator().next());

        RuleResponse response = new RuleResponse(savedRule);
        LOGGER.info("Succeed to modify file rule, rule id: {}", savedRule.getId());
        // Record project event.
//        projectEventService.record(savedRule.getProject().getId(), loginUser, "modify", "file rule[name= " + savedRule.getName() + "].", EventTypeEnum.MODIFY_PROJECT.getCode());
        return new GeneralResponse<>("200", "{&SUCCEED_TO_MODIFY_FILE_RULE}", response);
    }

    private void setUpdateFileInfo(ModifyFileRuleRequest request, Rule ruleInDb) {
        ruleInDb.setExecutionParametersName(null);
        ruleInDb.setAlert(request.getAlert());
        if (request.getAlert() != null && request.getAlert()) {
            ruleInDb.setAlertLevel(request.getAlertLevel());
            ruleInDb.setAlertReceiver(request.getAlertReceiver());
        }
        ruleInDb.setDeleteFailCheckResult(request.getDeleteFailCheckResult());
        ruleInDb.setAbortOnFailure(request.getAbortOnFailure());
        ruleInDb.setEnable(request.getRuleEnable());
        ruleInDb.setUnionAll(request.getUnionAll());
        ruleInDb.setAbnormalCluster(StringUtils.isNotBlank(request.getAbnormalCluster()) ? request.getAbnormalCluster() : null);
        ruleInDb.setAbnormalDatabase(StringUtils.isNotBlank(request.getAbnormalDatabase()) ? request.getAbnormalDatabase() : null);
        ruleInDb.setAbnormalProxyUser(StringUtils.isNotBlank(request.getAbnormalProxyUser()) ? request.getAbnormalProxyUser() : null);
    }

    private Boolean handleObjectEqual(AddFileRuleRequest request, ExecutionParameters executionParameters) {
        return CommonChecker.compareIdentical(request.getUnionAll(), request.getAbortOnFailure(), request.getSpecifyStaticStartupParam(), request.getStaticStartupParam()
                , request.getAbnormalDatabase(), request.getAbnormalCluster(), request.getAlert(), request.getAlertLevel(), request.getAlertReceiver(), request.getAbnormalProxyUser(), request.getDeleteFailCheckResult(), request.getUploadRuleMetricValue(), request.getUploadAbnormalValue(), executionParameters);
    }

    private Boolean handleObjectEqual(ModifyFileRuleRequest request, ExecutionParameters executionParameters) {
        return CommonChecker.compareIdentical(request.getUnionAll(), request.getAbortOnFailure(), request.getSpecifyStaticStartupParam(), request.getStaticStartupParam()
                , request.getAbnormalDatabase(), request.getAbnormalCluster(), request.getAlert(), request.getAlertLevel(), request.getAlertReceiver(), request.getAbnormalProxyUser(), request.getDeleteFailCheckResult(), request.getUploadRuleMetricValue(), request.getUploadAbnormalValue(), executionParameters);
    }

    private Project checkProject(ModifyFileRuleRequest request, String loginUser, Rule ruleInDb) throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check existence of project
        Project projectInDb = projectService.checkProjectExistence(ruleInDb.getProject().getId(),
                loginUser);
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);
        if (!ruleInDb.getRuleType().equals(RuleTypeEnum.FILE_TEMPLATE_RULE.getCode())) {
            throw new UnExpectedRequestException("rule(id: [" + request.getRuleId() + "]) {&IS_NOT_A_FILE_RULE}");
        }
        LOGGER.info("Succeed to find rule. rule id: {}", ruleInDb.getId());
        return projectInDb;
    }

    private void setBaseForRule(ModifyFileRuleRequest request, String loginUser, Rule ruleInDb, String nowDate, Template template) {
        // Save rule
        ruleInDb.setRuleType(RuleTypeEnum.FILE_TEMPLATE_RULE.getCode());
        ruleInDb.setRuleTemplateName(template.getName());
        ruleInDb.setCnName(request.getRuleCnName());
        ruleInDb.setDetail(request.getRuleDetail());
        ruleInDb.setName(request.getRuleName());
        ruleInDb.setAlarm(request.getAlarm());
        ruleInDb.setTemplate(template);
        ruleInDb.setModifyTime(nowDate);
        ruleInDb.setModifyUser(loginUser);
        ruleInDb.setBashContent(request.getBashContent());
        ruleInDb.setWorkFlowName(request.getWorkFlowName());
        ruleInDb.setWorkFlowVersion(request.getWorkFlowVersion());
        ruleInDb.setWorkFlowSpace(request.getWorkFlowSpace());
        ruleInDb.setNodeName(request.getNodeName());
    }

    @Override
    public GeneralResponse<RuleDetailResponse> getRuleDetail(Long ruleId) throws UnExpectedRequestException {
        // Check existence of rule
        Rule ruleInDb = ruleDao.findById(ruleId);
        if (ruleInDb == null) {
            throw new UnExpectedRequestException("rule_id [" + ruleId + "] {&DOES_NOT_EXIST}");
        }
        if (!ruleInDb.getRuleType().equals(RuleTypeEnum.FILE_TEMPLATE_RULE.getCode())) {
            throw new UnExpectedRequestException("rule(id: [" + ruleId + "]) {&IS_NOT_A_FILE_RULE}");
        }

        Long projectInDbId = ruleInDb.getProject().getId();
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        projectService.checkProjectExistence(projectInDbId, loginUser);

        LOGGER.info("Succeed to find rule. rule id: {}", ruleInDb.getId());

        RuleDetailResponse response = new RuleDetailResponse(ruleInDb);
        LOGGER.info("Succeed to get rule detail. rule id: {}", ruleId);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_FILE_RULE_DETAIL}", response);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> addRuleForUpload(AddFileRuleRequest request)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        return addRuleReal(request, loginUser, false);
    }

}
