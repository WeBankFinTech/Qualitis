package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.service.ProjectEventService;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.request.AbstractAddRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddFileRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteFileRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyFileRuleRequest;
import com.webank.wedatasphere.qualitis.rule.response.RuleDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import com.webank.wedatasphere.qualitis.rule.service.AlarmConfigService;
import com.webank.wedatasphere.qualitis.rule.service.FileRuleService;
import com.webank.wedatasphere.qualitis.rule.service.RuleDataSourceService;
import com.webank.wedatasphere.qualitis.rule.service.RuleService;
import com.webank.wedatasphere.qualitis.rule.service.RuleTemplateService;
import com.webank.wedatasphere.qualitis.submitter.impl.ExecutionManagerImpl;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
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

/**
 * @author allenzhou
 */
@Service
public class FileRuleServiceImpl implements FileRuleService {
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

    private static final Logger LOGGER = LoggerFactory.getLogger(FileRuleServiceImpl.class);

    private HttpServletRequest httpServletRequest;

    public FileRuleServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> addRule(AddFileRuleRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        return addRuleReal(request, loginUser);
    }

    @Override
    public GeneralResponse<RuleResponse> addRuleForOuter(AbstractAddRequest request, String loginUser)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        return addRuleReal((AddFileRuleRequest) request, loginUser);
    }

    private GeneralResponse<RuleResponse> addRuleReal(AddFileRuleRequest request, String loginUser)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check Arguments
        AddFileRuleRequest.checkRequest(request, false);
        // Generate Template, TemplateOutputMeta and save
        Template template = ruleTemplateService.addFileTemplate(request);

        // Check existence of project
        Project projectInDb = projectService.checkProjectExistence(request.getProjectId(),
            loginUser);
        String nowDate = ExecutionManagerImpl.PRINT_TIME_FORMAT.format(new Date());
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);
        // Check unique of rule name
        ruleService.checkRuleName(request.getRuleName(), projectInDb, null);
        // Check cluster support
        ruleDataSourceService.checkDataSourceClusterSupport(request.getDatasource().getClusterName());
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

        // Save rule.
        Rule newRule = new Rule();
        newRule.setRuleType(RuleTypeEnum.FILE_TEMPLATE_RULE.getCode());
        newRule.setTemplate(template);
        newRule.setName(request.getRuleName());
        newRule.setCnName(request.getRuleCnName());
        newRule.setDetail(request.getRuleDetail());
        newRule.setAlarm(request.getAlarm());
        newRule.setProject(projectInDb);
        newRule.setRuleTemplateName(template.getName());
        newRule.setRuleGroup(ruleGroup);
        newRule.setAbortOnFailure(request.getAbortOnFailure());
        newRule.setCreateUser(loginUser);
        newRule.setCreateTime(nowDate);
        String csId = request.getCsId();
        boolean cs = false;
        if (StringUtils.isNotBlank(csId))  {
            newRule.setCsId(csId);
            cs = true;
        }
        Rule savedRule = ruleDao.saveRule(newRule);
        LOGGER.info("Succeed to save file rule, rule_id: {}", savedRule.getId());

        List<AlarmConfig> savedAlarmConfigs = new ArrayList<>();
        if (request.getAlarm()) {
            savedAlarmConfigs  = alarmConfigService.checkAndSaveFileAlarmVariable(request.getAlarmVariable(), savedRule);
            LOGGER.info("Succeed to save alarm_configs, alarm_configs: {}", savedAlarmConfigs);
        }
        List<RuleDataSource> ruleDataSources = new ArrayList<>();
        ruleDataSources.add(ruleDataSourceService.checkAndSaveFileRuleDataSource(request.getDatasource(), savedRule, cs));

        savedRule.setAlarmConfigs(new HashSet<>(savedAlarmConfigs));
        savedRule.setRuleDataSources(new HashSet<>(ruleDataSources));

        // Update rule count of datasource
        ruleDataSourceService.updateRuleDataSourceCount(savedRule, 1);
        RuleResponse response = new RuleResponse(savedRule);
        LOGGER.info("Succeed to add file rule, rule_id: {}", savedRule.getId());
        return new GeneralResponse<>("200", "{&SUCCEED_TO_ADD_FILE_RULE}", response);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> deleteRule(DeleteFileRuleRequest request, String loginUser) throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check Arguments
        DeleteFileRuleRequest.checkRequest(request);
        // Check existence of rule
        Rule ruleInDb = ruleDao.findById(request.getRuleId());
        if (ruleInDb == null || !ruleInDb.getRuleType().equals(RuleTypeEnum.FILE_TEMPLATE_RULE.getCode())) {
            throw new UnExpectedRequestException("Rule id [" + request.getRuleId() + "]) {&IS_NOT_A_RULE_WITH_TEMPLATE}");
        }
        ruleDataSourceService.updateRuleDataSourceCount(ruleInDb, -1);
        Project projectInDb = projectService.checkProjectExistence(ruleInDb.getProject().getId(),
            loginUser);
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, loginUser, permissions);
        // Update rule count of datasource
        ruleDataSourceService.updateRuleDataSourceCount(ruleInDb, -1);
        // Record project event.
//        projectEventService.record(projectInDb.getId(), loginUser, "delete", "file rule[name= " + ruleInDb.getName() + "].", EventTypeEnum.MODIFY_PROJECT.getCode());

        return deleteRuleReal(ruleInDb);
    }

    @Override
    public GeneralResponse<?> deleteRuleReal(Rule rule) throws UnExpectedRequestException {
        // Delete rule
        ruleDao.deleteRule(rule);
        // Delete template of custom rule
        ruleTemplateService.deleteFileRuleTemplate(rule.getTemplate().getId());
        LOGGER.info("Succeed to delete file rule. rule_id: {}", rule.getId());
        return new GeneralResponse<>("200", "{&DELETE_FILE_RULE_SUCCESSFULLY}", null);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> modifyRuleDetail(ModifyFileRuleRequest request)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        return modifyRuleDetailReal(request, loginUser);
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> modifyRuleDetailForOuter(ModifyFileRuleRequest modifyRuleRequest, String userName)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        return modifyRuleDetailReal(modifyRuleRequest, userName);
    }

    private GeneralResponse<RuleResponse> modifyRuleDetailReal(ModifyFileRuleRequest request, String loginUser)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check Arguments
        ModifyFileRuleRequest.checkRequest(request);
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
        if (!ruleInDb.getRuleType().equals(RuleTypeEnum.FILE_TEMPLATE_RULE.getCode())) {
            throw new UnExpectedRequestException("rule(id: [" + request.getRuleId() + "]) {&IS_NOT_A_FILE_RULE}");
        }
        LOGGER.info("Succeed to find rule. rule_id: {}", ruleInDb.getId());
        String nowDate = ExecutionManagerImpl.PRINT_TIME_FORMAT.format(new Date());
        String csId = request.getCsId();
        boolean cs = false;
        if (StringUtils.isNotBlank(csId)) {
            cs = true;
            ruleInDb.setCsId(csId);
        } else {
            LOGGER.info("Delete context service ID. rule_id: {}， cs_id: {}", ruleInDb.getId(), ruleInDb.getCsId());
            ruleInDb.setCsId(csId);
            LOGGER.info("After delete context service ID. rule_id: {}， cs_id: {}", ruleInDb.getId(), ruleInDb.getCsId());
        }

        // Check cluster support
        ruleDataSourceService.checkDataSourceClusterSupport(request.getDatasource().getClusterName());
        // Delete alarm config by file rule
        alarmConfigService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all alarm_config. rule_id: {}", ruleInDb.getId());
        // Delete template of file rule
        ruleTemplateService.deleteFileRuleTemplate(ruleInDb.getTemplate().getId());
        LOGGER.info("Succeed to delete file rule template. rule_id: {}", request.getRuleId());

        // Delete rule datasource of file rule
        ruleDataSourceService.deleteByRule(ruleInDb);
        LOGGER.info("Succeed to delete all rule_dataSources. rule_id: {}", ruleInDb.getId());
        // Update rule count of datasource
        ruleDataSourceService.updateRuleDataSourceCount(ruleInDb, -1);

        // Check existence of rule
        AddFileRuleRequest addFileRuleRequest = new AddFileRuleRequest();
        BeanUtils.copyProperties(request, addFileRuleRequest);
        Template template = ruleTemplateService.addFileTemplate(addFileRuleRequest);
        // Save rule
        ruleInDb.setRuleType(RuleTypeEnum.FILE_TEMPLATE_RULE.getCode());
        ruleInDb.setTemplate(template);
        ruleInDb.setName(request.getRuleName());
        ruleInDb.setCnName(request.getRuleCnName());
        ruleInDb.setDetail(request.getRuleDetail());
        ruleInDb.setAlarm(request.getAlarm());
        ruleInDb.setRuleTemplateName(template.getName());
        ruleInDb.setAbortOnFailure(request.getAbortOnFailure());
        ruleInDb.setModifyUser(loginUser);
        ruleInDb.setModifyTime(nowDate);
        Rule savedRule = ruleDao.saveRule(ruleInDb);
        LOGGER.info("Succeed to save file rule, rule_id: {}", savedRule.getId());

        List<AlarmConfig> savedAlarmConfigs = new ArrayList<>();
        if (request.getAlarm()) {
            savedAlarmConfigs  = alarmConfigService.checkAndSaveFileAlarmVariable(request.getAlarmVariable(), savedRule);
            LOGGER.info("Succeed to save alarm_configs, alarm_configs: {}", savedAlarmConfigs);
        }
        List<RuleDataSource> ruleDataSources = new ArrayList<>();
        ruleDataSources.add(ruleDataSourceService.checkAndSaveFileRuleDataSource(request.getDatasource(), savedRule, cs));

        savedRule.setAlarmConfigs(new HashSet<>(savedAlarmConfigs));
        savedRule.setRuleDataSources(new HashSet<>(ruleDataSources));
        // Update rule count of datasource
        ruleDataSourceService.updateRuleDataSourceCount(ruleInDb, 1);

        RuleResponse response = new RuleResponse(savedRule);
        LOGGER.info("Succeed to modify file rule, rule_id: {}", savedRule.getId());
        // Record project event.
//        projectEventService.record(savedRule.getProject().getId(), loginUser, "modify", "file rule[name= " + savedRule.getName() + "].", EventTypeEnum.MODIFY_PROJECT.getCode());
        return new GeneralResponse<>("200", "{&SUCCEED_TO_MODIFY_FILE_RULE}", response);
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

        LOGGER.info("Succeed to find rule. rule_id: {}", ruleInDb.getId());

        RuleDetailResponse response = new RuleDetailResponse(ruleInDb);
        LOGGER.info("Succeed to get rule detail. rule_id: {}", ruleId);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_FILE_RULE_DETAIL}", response);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RuleResponse> addRuleForUpload(AddFileRuleRequest request)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        return addRuleReal(request, loginUser);
    }

}
