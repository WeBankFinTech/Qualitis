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

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.parser.LocaleParser;
import com.webank.wedatasphere.qualitis.project.constant.ExcelSheetName;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.constant.CheckTemplateEnum;
import com.webank.wedatasphere.qualitis.rule.constant.CompareTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.FunctionTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.constant.MappingOperationEnum;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleTemplateDao;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateMidTableInputMetaDao;
import com.webank.wedatasphere.qualitis.rule.dao.TemplateOutputMetaDao;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceMapping;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.entity.RuleVariable;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateOutputMeta;
import com.webank.wedatasphere.qualitis.rule.excel.ExcelCustomRule;
import com.webank.wedatasphere.qualitis.rule.excel.ExcelMultiTemplateRule;
import com.webank.wedatasphere.qualitis.rule.excel.ExcelRuleListener;
import com.webank.wedatasphere.qualitis.rule.excel.ExcelTemplateRule;
import com.webank.wedatasphere.qualitis.rule.exception.WriteExcelException;
import com.webank.wedatasphere.qualitis.rule.request.AddCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.AlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.CustomAlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceColumnRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.DownloadRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplateArgumentRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.AddMultiSourceRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinColumnRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinConfigRequest;
import com.webank.wedatasphere.qualitis.rule.service.CustomRuleService;
import com.webank.wedatasphere.qualitis.rule.service.MultiSourceRuleService;
import com.webank.wedatasphere.qualitis.rule.service.RuleBatchService;
import com.webank.wedatasphere.qualitis.rule.service.RuleService;
import com.webank.wedatasphere.qualitis.rule.util.TemplateMidTableUtil;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author howeye
 */
@Service
public class RuleBatchServiceImpl implements RuleBatchService {

    @Autowired
    private RuleDao ruleDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private RuleTemplateDao templateDao;

    @Autowired
    private TemplateMidTableInputMetaDao templateMidTableInputMetaDao;

    @Autowired
    private TemplateOutputMetaDao templateOutputMetaDao;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private CustomRuleService customRuleService;

    @Autowired
    private RuleGroupDao ruleGroupDao;

    @Autowired
    private MultiSourceRuleService multiSourceRuleService;

    @Autowired
    private LocaleParser localeParser;
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleBatchServiceImpl.class);
    private static final String SUPPORT_EXCEL_SUFFIX_NAME = ".xlsx";
    private final FastDateFormat FILE_DATE_FORMATTER = FastDateFormat.getInstance("yyyyMMddHHmmss");

    private HttpServletRequest httpServletRequest;

    public RuleBatchServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public GeneralResponse<?> downloadRules(DownloadRuleRequest request, HttpServletResponse response) throws UnExpectedRequestException, IOException, WriteExcelException {
        // Check Arguments
        DownloadRuleRequest.checkRequest(request);

        Boolean projectAllRules = request.getProjectId() != null;
        List<Rule> ruleLists;
        if (projectAllRules) {
            LOGGER.info("Downloading all rules of project. project id: {}", request.getProjectId());
            Project project = projectDao.findById(request.getProjectId());
            if (project == null) {
                throw new UnExpectedRequestException("{&PROJECT_ID} {&DOES_NOT_EXIST}");
            }
            ruleLists = ruleDao.findByProject(project);
        } else {
            LOGGER.info("Downloading all rules. rule ids: {}", request.getRuleIds());
            ruleLists = ruleDao.findByIds(request.getRuleIds());
            List<Long> ruleIds = ruleLists.stream().map(Rule::getId).distinct().collect(Collectors.toList());
            List<Long> notExistRules = request.getRuleIds().stream().filter(l -> !ruleIds.contains(l)).collect(Collectors.toList());
            if (!notExistRules.isEmpty()) {
                throw new UnExpectedRequestException("{&THE_IDS_OF_RULE}: " + notExistRules.toString() + " {&DOES_NOT_EXIST}");
            }
        }

        if (ruleLists == null || ruleLists.isEmpty()) {
            throw new UnExpectedRequestException("{&NO_RULE_CAN_DOWNLOAD}");
        }
        LOGGER.info("Succeed to find rules that will be downloaded. rule_ids: {}", ruleLists.stream().map(Rule::getId));
        return downloadRulesReal(ruleLists, response);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> uploadRules(InputStream fileInputStream, FormDataContentDisposition fileDisposition, Long projectId) throws UnExpectedRequestException, IOException, MetaDataAcquireFailedException, SemanticException, ParseException {
        // Check Arguments
        if (fileInputStream == null || fileDisposition == null) {
            throw new UnExpectedRequestException("{&FILE_CAN_NOT_BE_NULL_OR_EMPTY}");
        }

        // Check suffix name of file
        String fileName = fileDisposition.getFileName();
        String suffixName = fileName.substring(fileName.lastIndexOf('.'));
        if (!suffixName.equals(SUPPORT_EXCEL_SUFFIX_NAME)) {
            throw new UnExpectedRequestException("{&DO_NOT_SUPPORT_SUFFIX_NAME}: [" + suffixName + "]. {&ONLY_SUPPORT} [" + SUPPORT_EXCEL_SUFFIX_NAME + "]");
        }
        Project projectInDb = projectDao.findById(projectId);
        if (projectInDb == null) {
            throw new UnExpectedRequestException("{&PROJECT_ID} {&DOES_NOT_EXIST}");
        }

        String username = HttpUtils.getUserName(httpServletRequest);
        if (username == null) {
            return new GeneralResponse<>("401", "{&PLEASE_LOGIN}", null);
        }

        ExcelRuleListener excelRuleListener = readExcel(fileInputStream);
        if (excelRuleListener.getCustomExcelContent().isEmpty() && excelRuleListener.getTemplateExcelContent().isEmpty()
                && excelRuleListener.getMultiTemplateExcelContent().isEmpty()) {
            throw new UnExpectedRequestException("{&FILE_CAN_NOT_BE_EMPTY_OR_FILE_CAN_NOT_BE_RECOGNIZED}");
        }
        getAndSaveRule(excelRuleListener.getTemplateExcelContent(), excelRuleListener.getCustomExcelContent(),
                excelRuleListener.getMultiTemplateExcelContent(), projectInDb, username);
        fileInputStream.close();
        return new GeneralResponse<>("200", "{&SUCCEED_TO_UPLOAD_FILE}", null);
    }

    private ExcelRuleListener readExcel(InputStream inputStream) {
        LOGGER.info("Start to read excel");
        ExcelRuleListener excelRuleListener = partitionByRuleName(inputStream);
        LOGGER.info("Finish to read excel. template rule content: {}", excelRuleListener.getTemplateExcelContent());
        LOGGER.info("Finish to read excel. custom rule content: {}", excelRuleListener.getCustomExcelContent());
        LOGGER.info("Finish to read excel. multi rule content: {}", excelRuleListener.getMultiTemplateExcelContent());
        return excelRuleListener;
    }

    @Override
    public void getAndSaveRule(Map<String, List<ExcelTemplateRule>> rulePartitionedByRuleName, Map<String, List<ExcelCustomRule>> customRulePartitionedByRuleName,
                               Map<String, List<ExcelMultiTemplateRule>> multiRulePartitionedByRuleName, Project project, String username) throws UnExpectedRequestException, MetaDataAcquireFailedException, SemanticException, ParseException {
        // Check if user has permission importing rule
        if (!project.getCreateUser().equals(username)) {
            throw new UnExpectedRequestException("User :[" + username + "] {&HAS_NO_PERMISSION_TO_IMPORT_RULES}");
        }

        // Add template rule
        if (rulePartitionedByRuleName != null) {
            addTemplateRule(rulePartitionedByRuleName, project);
        }

        // Add custom rule
        if (customRulePartitionedByRuleName != null) {
            addCustomRule(customRulePartitionedByRuleName, project);
        }

        // Add multi-table rule
        if (multiRulePartitionedByRuleName != null) {
            addMultiTemplateRule(multiRulePartitionedByRuleName, project);
        }
    }

    private void addMultiTemplateRule(Map<String, List<ExcelMultiTemplateRule>> multiRulePartitionedByRuleName, Project project) throws UnExpectedRequestException {
        // Construct request and add rule
        List<AddMultiSourceRuleRequest> addRuleRequests = constructAddMultiSourceRuleRequest(multiRulePartitionedByRuleName, project);

        // Add rule
        LOGGER.info("Start to add all multi rules");
        for (AddMultiSourceRuleRequest addMultiSourceRuleRequest : addRuleRequests) {
            LOGGER.info("Start to add multi rule. request: {}", addMultiSourceRuleRequest);
            multiSourceRuleService.addMultiSourceRule(addMultiSourceRuleRequest);
        }
        LOGGER.info("Succeed to add all multi rules");
    }

    private void addCustomRule(Map<String, List<ExcelCustomRule>> customRulePartitionedByRuleName, Project project) throws UnExpectedRequestException, SemanticException, ParseException {
        // Construct request and add rule
        List<AddCustomRuleRequest> addRuleRequests = constructAddCustomRuleRequest(customRulePartitionedByRuleName, project);

        // Add rule
        LOGGER.info("Start to add all custom rules");
        for (AddCustomRuleRequest addCustomRuleRequest : addRuleRequests) {
            LOGGER.info("Start to add custom rule. request: {}", addCustomRuleRequest);
            customRuleService.addCustomRule(addCustomRuleRequest);
        }
        LOGGER.info("Succeed to add all custom rules");
    }

    private void addTemplateRule(Map<String, List<ExcelTemplateRule>> templateRulePartitionedByRuleName, Project project) throws UnExpectedRequestException {
        // Construct request and add rule
        List<AddRuleRequest> addRuleRequests = constructAddRuleRequest(templateRulePartitionedByRuleName, project);

        // Add rule
        LOGGER.info("Start to add all template rules");
        for (AddRuleRequest addRuleRequest : addRuleRequests) {
            LOGGER.info("Start to add template rule. request: {}", addRuleRequest);
            ruleService.addRule(addRuleRequest);
        }
        LOGGER.info("Succeed to add all template rules");
    }

    private List<AddMultiSourceRuleRequest> constructAddMultiSourceRuleRequest(Map<String, List<ExcelMultiTemplateRule>> multiRulePartitionedByRuleName, Project project) throws UnExpectedRequestException {
        List<AddMultiSourceRuleRequest> addMultiSourceRuleRequests = new ArrayList<>();
        for (String ruleName : multiRulePartitionedByRuleName.keySet()) {
            List<ExcelMultiTemplateRule> ruleInfos = multiRulePartitionedByRuleName.get(ruleName);
            AddMultiSourceRuleRequest addMultiSourceRuleRequest = new AddMultiSourceRuleRequest();
            String ruleTemplateName = ruleInfos.get(0).getTemplateName();
            String ruleGroupName = ruleInfos.get(0).getRuleGroupName();
            addMultiSourceRuleRequest.setAbortOnFailure(ruleInfos.get(0).getAbortOnFailure());
            if (StringUtils.isBlank(ruleGroupName)) {
                throw new UnExpectedRequestException("RuleGroupName {&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            Template template = findTemplateByName(ruleTemplateName);
            if (template == null) {
                throw new UnExpectedRequestException("{&TEMPLATE_NAME}: [" + ruleTemplateName + "] {&DOES_NOT_EXIST}");
            }

            String filter = null;
            String clusterName = null;
            boolean alarm = false;
            MultiDataSourceConfigRequest sourceConfigRequest = new MultiDataSourceConfigRequest();
            MultiDataSourceConfigRequest targetConfigRequest = new MultiDataSourceConfigRequest();
            List<MultiDataSourceJoinConfigRequest> mappings = new ArrayList<>();
            List<AlarmConfigRequest> alarmConfigRequests = new ArrayList<>();
            for (ExcelMultiTemplateRule excelMultiTemplateRule : ruleInfos) {
                if (StringUtils.isNotBlank(excelMultiTemplateRule.getWhereFilter())) {
                    filter = excelMultiTemplateRule.getWhereFilter();
                }

                clusterName = excelMultiTemplateRule.getClusterName();
                getMultiDataSourceRequest(sourceConfigRequest, excelMultiTemplateRule, 0);
                getMultiDataSourceRequest(targetConfigRequest, excelMultiTemplateRule, 1);
                getMultiDataSourceJoinRequest(mappings, excelMultiTemplateRule);
                getAlarmConfig(alarmConfigRequests, excelMultiTemplateRule, template);
            }
            if (alarmConfigRequests.size() != 0) {
                alarm = true;
            }

            RuleGroup ruleGroupInDb = ruleGroupDao.findByRuleGroupNameAndProjectId(ruleGroupName, project.getId());
            if (ruleGroupInDb != null) {
                addMultiSourceRuleRequest.setRuleGroupId(ruleGroupInDb.getId());
            } else {
                RuleGroup ruleGroup = ruleGroupDao.saveRuleGroup(
                        new RuleGroup(ruleGroupName, project.getId()));
                addMultiSourceRuleRequest.setRuleGroupId(ruleGroup.getId());
            }

            addMultiSourceRuleRequest.setRuleName(ruleName);
            addMultiSourceRuleRequest.setClusterName(clusterName);
            addMultiSourceRuleRequest.setMultiSourceRuleTemplateId(template.getId());
            addMultiSourceRuleRequest.setProjectId(project.getId());
            addMultiSourceRuleRequest.setSource(sourceConfigRequest);
            addMultiSourceRuleRequest.setTarget(targetConfigRequest);
            addMultiSourceRuleRequest.setMappings(mappings);
            addMultiSourceRuleRequest.setFilter(filter);
            addMultiSourceRuleRequest.setAlarm(alarm);
            addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
            addMultiSourceRuleRequests.add(addMultiSourceRuleRequest);
        }
        return addMultiSourceRuleRequests;
    }

    private Template findTemplateByName(String templateName) {
        List<Template> templates = templateDao.getAllTemplate();
        for (Template template : templates) {
            if (template.getName().equals(templateName)) {
                return template;
            }
        }
        return null;
    }

    private void getMultiDataSourceJoinRequest(List<MultiDataSourceJoinConfigRequest> mappings, ExcelMultiTemplateRule excelMultiTemplateRule) {
        String leftStatement = excelMultiTemplateRule.getLeftMappingStatement();
        if (StringUtils.isNotBlank(leftStatement)) {
            MultiDataSourceJoinConfigRequest mapping = new MultiDataSourceJoinConfigRequest();
            List<MultiDataSourceJoinColumnRequest> leftRequest = getMultiDataSourceJoinColumnRequest(excelMultiTemplateRule.getLeftMappingStatement(), excelMultiTemplateRule.getRightMappingStatement(), 0);
            List<MultiDataSourceJoinColumnRequest> rightRequest = getMultiDataSourceJoinColumnRequest(excelMultiTemplateRule.getLeftMappingStatement(), excelMultiTemplateRule.getRightMappingStatement(), 1);
            mapping.setLeft(leftRequest);
            mapping.setLeftStatement(excelMultiTemplateRule.getLeftMappingStatement());
            mapping.setOperation(MappingOperationEnum.getOperationCode(excelMultiTemplateRule.getMappingOperation()));
            mapping.setRight(rightRequest);
            mapping.setRightStatement(excelMultiTemplateRule.getRightMappingStatement());
            mappings.add(mapping);
        }
    }

    private List<MultiDataSourceJoinColumnRequest> getMultiDataSourceJoinColumnRequest(String leftStatement, String rightStatement, Integer index) {
        String patternStr = "tmp" + (index + 1) + "\\.[_0-9a-zA-Z]*";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher leftMatcher = pattern.matcher(leftStatement);
        Matcher rightMatcher = pattern.matcher(rightStatement);
        List<MultiDataSourceJoinColumnRequest> joinColumnRequests = new ArrayList<>();
        while (leftMatcher.find()) {
            MultiDataSourceJoinColumnRequest request = new MultiDataSourceJoinColumnRequest();
            request.setColumnName(leftMatcher.group());
            joinColumnRequests.add(request);
        }
        while (rightMatcher.find()) {
            MultiDataSourceJoinColumnRequest request = new MultiDataSourceJoinColumnRequest();
            request.setColumnName(rightMatcher.group());
            joinColumnRequests.add(request);
        }

        return joinColumnRequests;
    }

    private void getMultiDataSourceRequest(MultiDataSourceConfigRequest multiDataSourceConfigRequest, ExcelMultiTemplateRule excelMultiTemplateRule, Integer index) {
        String dbName = excelMultiTemplateRule.getLeftDbName();
        if (StringUtils.isNotBlank(dbName)) {
            if (index == 0) {
                multiDataSourceConfigRequest.setDbName(excelMultiTemplateRule.getLeftDbName());
                multiDataSourceConfigRequest.setTableName(excelMultiTemplateRule.getLeftTableName());
                multiDataSourceConfigRequest.setFilter(excelMultiTemplateRule.getLeftFilter());
            } else {
                multiDataSourceConfigRequest.setDbName(excelMultiTemplateRule.getRightDbName());
                multiDataSourceConfigRequest.setTableName(excelMultiTemplateRule.getRightTableName());
                multiDataSourceConfigRequest.setFilter(excelMultiTemplateRule.getRightFilter());
            }
        }
    }

    private void getAlarmConfig(List<AlarmConfigRequest> alarmConfigRequests, ExcelMultiTemplateRule excelMultiTemplateRule, Template template) throws UnExpectedRequestException {
        String templateOutputName = excelMultiTemplateRule.getAlarmCheckName();
        if (!StringUtils.isBlank(templateOutputName)) {
            String checkTemplateName = excelMultiTemplateRule.getCheckTemplateName();
            String compareTypeName = excelMultiTemplateRule.getCompareType();
            String threshold = excelMultiTemplateRule.getThreshold();
            TemplateOutputMeta templateOutputMeta = findTemplateOutputMetaByTemplateAndOutputName(template, templateOutputName);
            if (template.getChildTemplate() != null && templateOutputMeta == null) {
                templateOutputMeta = findTemplateOutputMetaByTemplateAndOutputName(template.getChildTemplate(), templateOutputName);
            }

            if (templateOutputMeta == null) {
                throw new UnExpectedRequestException("{&TEMPLATE_OUTPUT_NAME} {&DOES_NOT_EXIST}");
            }
            AlarmConfigRequest alarmConfigRequest = new AlarmConfigRequest();
            String localeStr = httpServletRequest.getHeader("Content-Language");
            alarmConfigRequest.setCheckTemplate(CheckTemplateEnum.getCheckTemplateCode(checkTemplateName, localeStr));
            alarmConfigRequest.setCompareType(CompareTypeEnum.getCompareTypeCode(compareTypeName));
            alarmConfigRequest.setThreshold(Double.valueOf(threshold));
            alarmConfigRequest.setOutputMetaId(templateOutputMeta.getId());

            alarmConfigRequests.add(alarmConfigRequest);
        }
    }

    private TemplateOutputMeta findTemplateOutputMetaByTemplateAndOutputName(Template template, String templateOutputName) {
        List<TemplateOutputMeta> templateOutputMetas = templateOutputMetaDao.findByTemplate(template);
        for (TemplateOutputMeta templateOutputMeta: templateOutputMetas) {
            if (templateOutputMeta.getOutputName().equals(templateOutputName)) {
                return templateOutputMeta;
            }
        }
        return null;
    }

    private List<AddCustomRuleRequest> constructAddCustomRuleRequest(Map<String, List<ExcelCustomRule>> customRulePartitionedByRuleName, Project project) throws UnExpectedRequestException {
        List<AddCustomRuleRequest> addCustomRuleRequests = new ArrayList<>();
        for (String ruleName : customRulePartitionedByRuleName.keySet()) {
            List<ExcelCustomRule> ruleInfos = customRulePartitionedByRuleName.get(ruleName);
            AddCustomRuleRequest addCustomRuleRequest = new AddCustomRuleRequest();
            boolean alarm = false;
            List<CustomAlarmConfigRequest> alarmConfigRequests = new ArrayList<>();
            Long projectId = project.getId();
            String outputName = null;
            Boolean saveMidTable = null;
            Integer functionType = null;
            String functionContent = null;
            String fromContent = null;
            String whereContent = null;
            String clusterName = null;
            String ruleGroupName = null;
            addCustomRuleRequest.setAbortOnFailure(ruleInfos.get(0).getAbortOnFailure());
            for (ExcelCustomRule excelCustomRule : ruleInfos) {
                getCustomAlarmConfig(alarmConfigRequests, excelCustomRule);
                outputName = excelCustomRule.getOutputName();
                saveMidTable = excelCustomRule.getSaveMidTable();
                functionType = FunctionTypeEnum.getFunctionTypeByName(excelCustomRule.getFunctionName());
                functionContent = excelCustomRule.getFunctionContent();
                fromContent = excelCustomRule.getFromContent();
                whereContent = excelCustomRule.getWhereContent();
                clusterName = excelCustomRule.getClusterName();
                ruleGroupName = excelCustomRule.getRuleGroupName();
            }

            if (StringUtils.isBlank(ruleGroupName)) {
                throw new UnExpectedRequestException("RuleGroupName {&CAN_NOT_BE_NULL_OR_EMPTY}");
            }

            RuleGroup ruleGroupInDb = ruleGroupDao.findByRuleGroupNameAndProjectId(ruleGroupName, projectId);
            if (ruleGroupInDb != null) {
                addCustomRuleRequest.setRuleGroupId(ruleGroupInDb.getId());
            } else {
                RuleGroup ruleGroup = ruleGroupDao.saveRuleGroup(
                        new RuleGroup(ruleGroupName, project.getId()));
                addCustomRuleRequest.setRuleGroupId(ruleGroup.getId());
            }

            // Construct addCustomRuleRequest
            addCustomRuleRequest.setRuleName(ruleName);
            if (!alarmConfigRequests.isEmpty()) {
                alarm = true;
            }
            addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
            addCustomRuleRequest.setAlarm(alarm);
            addCustomRuleRequest.setProjectId(projectId);
            addCustomRuleRequest.setOutputName(outputName);
            addCustomRuleRequest.setFunctionType(functionType);
            addCustomRuleRequest.setSaveMidTable(saveMidTable);
            addCustomRuleRequest.setFunctionContent(functionContent);
            addCustomRuleRequest.setFromContent(fromContent);
            addCustomRuleRequest.setWhereContent(whereContent);
            addCustomRuleRequest.setClusterName(clusterName);

            addCustomRuleRequests.add(addCustomRuleRequest);
        }
        return addCustomRuleRequests;
    }

    private List<AddRuleRequest> constructAddRuleRequest(Map<String, List<ExcelTemplateRule>> partitionedByRuleName, Project project) throws UnExpectedRequestException {
        List<AddRuleRequest> addRuleRequests = new ArrayList<>();
        for (String ruleName : partitionedByRuleName.keySet()) {
            List<ExcelTemplateRule> ruleInfos = partitionedByRuleName.get(ruleName);
            AddRuleRequest addRuleRequest = new AddRuleRequest();
            String ruleTemplateName = ruleInfos.get(0).getTemplateName();
            String ruleGroupName = ruleInfos.get(0).getRuleGroupName();
            addRuleRequest.setAbortOnFailure(ruleInfos.get(0).getAbortOnFailure());
            if (StringUtils.isBlank(ruleGroupName)) {
                throw new UnExpectedRequestException("RuleGroupName {&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            Template template = findTemplateByName(ruleTemplateName);
            if (template == null) {
                throw new UnExpectedRequestException("{&TEMPLATE_NAME}: [" + ruleTemplateName + "] {&DOES_NOT_EXIST}");
            }

            boolean alarm = false;
            List<AlarmConfigRequest> alarmConfigRequests = new ArrayList<>();
            List<DataSourceRequest> dataSourceRequests = new ArrayList<>();
            Long projectId = project.getId();
            List<TemplateArgumentRequest> templateArgumentRequests = new ArrayList<>();
            for (ExcelTemplateRule excelTemplateRule : ruleInfos) {
                getDataSourceRequest(dataSourceRequests, excelTemplateRule);
                getTemplateArgument(templateArgumentRequests, excelTemplateRule, template);
                getAlarmConfig(alarmConfigRequests, excelTemplateRule, template);
            }

            RuleGroup ruleGroupInDb = ruleGroupDao.findByRuleGroupNameAndProjectId(ruleGroupName, projectId);
            if (ruleGroupInDb != null) {
                addRuleRequest.setRuleGroupId(ruleGroupInDb.getId());
            } else {
                RuleGroup ruleGroup = ruleGroupDao.saveRuleGroup(
                        new RuleGroup(ruleGroupName, project.getId()));
                addRuleRequest.setRuleGroupId(ruleGroup.getId());
            }

            // Construct addRuleRequest
            addRuleRequest.setRuleName(ruleName);
            addRuleRequest.setRuleTemplateId(template.getId());
            if (!alarmConfigRequests.isEmpty()) {
                alarm = true;
            }
            addRuleRequest.setAlarm(alarm);
            addRuleRequest.setAlarmVariable(alarmConfigRequests);
            addRuleRequest.setDatasource(dataSourceRequests);
            addRuleRequest.setProjectId(projectId);
            addRuleRequest.setTemplateArgumentRequests(templateArgumentRequests);

            addRuleRequests.add(addRuleRequest);
        }

        return addRuleRequests;
    }

    private void getCustomAlarmConfig(List<CustomAlarmConfigRequest> customAlarmConfigRequests, ExcelCustomRule excelCustomRule) {
        String templateOutputName = excelCustomRule.getAlarmCheckName();
        if (!StringUtils.isBlank(templateOutputName)) {
            String checkTemplateName = excelCustomRule.getCheckTemplateName();
            String compareTypeName = excelCustomRule.getCompareType();
            String threshold = excelCustomRule.getThreshold();

            CustomAlarmConfigRequest customAlarmConfigRequest = new CustomAlarmConfigRequest();
            String localeStr = httpServletRequest.getHeader("Content-Language");
            customAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.getCheckTemplateCode(checkTemplateName, localeStr));
            customAlarmConfigRequest.setCompareType(CompareTypeEnum.getCompareTypeCode(compareTypeName));
            customAlarmConfigRequest.setThreshold(Double.valueOf(threshold));

            customAlarmConfigRequests.add(customAlarmConfigRequest);
        }
    }

    private void getAlarmConfig(List<AlarmConfigRequest> alarmConfigRequests, ExcelTemplateRule excelTemplateRule, Template template) throws UnExpectedRequestException {
        String templateOutputName = excelTemplateRule.getAlarmCheckName();
        if (!StringUtils.isBlank(templateOutputName)) {
            String checkTemplateName = excelTemplateRule.getCheckTemplateName();
            String compareTypeName = excelTemplateRule.getCompareType();
            String threshold = excelTemplateRule.getThreshold();
            TemplateOutputMeta templateOutputMeta = findTemplateOutputMetaByTemplateAndOutputName(template, templateOutputName);

            if (templateOutputMeta == null) {
                throw new UnExpectedRequestException("{&TEMPLATE_OUTPUT_NAME} {&DOES_NOT_EXIST}");
            }
            AlarmConfigRequest alarmConfigRequest = new AlarmConfigRequest();
            String localeStr = httpServletRequest.getHeader("Content-Language");
            alarmConfigRequest.setCheckTemplate(CheckTemplateEnum.getCheckTemplateCode(checkTemplateName, localeStr));
            alarmConfigRequest.setCompareType(CompareTypeEnum.getCompareTypeCode(compareTypeName));
            alarmConfigRequest.setThreshold(Double.valueOf(threshold));
            alarmConfigRequest.setOutputMetaId(templateOutputMeta.getId());

            alarmConfigRequests.add(alarmConfigRequest);
        }
    }

    private void getTemplateArgument(List<TemplateArgumentRequest> templateArgumentRequests, ExcelTemplateRule excelTemplateRule, Template template) throws UnExpectedRequestException {
        String argumentKey = excelTemplateRule.getArgumentKey();
        if (!StringUtils.isBlank(argumentKey)) {
            TemplateArgumentRequest templateArgumentRequest = new TemplateArgumentRequest();
            templateArgumentRequest.setArgumentStep(InputActionStepEnum.TEMPLATE_INPUT_META.getCode());
            String argumentValue = excelTemplateRule.getArgumentValue();
            templateArgumentRequest.setArgumentValue(argumentValue);
            TemplateMidTableInputMeta templateMidTableInputMeta = findByTemplateAndName(template, argumentKey);
            if (templateMidTableInputMeta == null) {
                throw new UnExpectedRequestException("{&TEMPLATE_ARGUMENT}: [" + argumentKey + "] {&DOES_NOT_EXIST}");
            }
            templateArgumentRequest.setArgumentId(templateMidTableInputMeta.getId());

            templateArgumentRequests.add(templateArgumentRequest);
        }
    }

    private TemplateMidTableInputMeta findByTemplateAndName(Template template, String argumentKey) {
        List<TemplateMidTableInputMeta> templateMidTableInputMetas = templateMidTableInputMetaDao.findByRuleTemplate(template);
        for (TemplateMidTableInputMeta templateMidTableInputMeta : templateMidTableInputMetas) {
            if (templateMidTableInputMeta.getName().equals(argumentKey)) {
                return templateMidTableInputMeta;
            }
        }
        return null;
    }

    private void getDataSourceRequest(List<DataSourceRequest> dataSourceRequests, ExcelTemplateRule excelTemplateRule) {
        String clusterName = excelTemplateRule.getClusterName();
        if (!StringUtils.isBlank(clusterName)) {
            DataSourceRequest dataSourceRequest = new DataSourceRequest();
            dataSourceRequest.setClusterName(excelTemplateRule.getClusterName());
            dataSourceRequest.setDbName(excelTemplateRule.getDbName());
            dataSourceRequest.setTableName(excelTemplateRule.getTableName());
            dataSourceRequest.setFilter(excelTemplateRule.getFilter());
            String colNamesOrigin = excelTemplateRule.getColNames();
            List<DataSourceColumnRequest> dataSourceColumnRequests = new ArrayList<>();
            if (!StringUtils.isBlank(colNamesOrigin)) {
                String[] colNamesSplit = colNamesOrigin.split(",");
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
    }

    private ExcelRuleListener partitionByRuleName(InputStream inputStream) {
        ExcelRuleListener listener = new ExcelRuleListener();
        ExcelReader excelReader = new ExcelReader(inputStream, null, listener);
        List<Sheet> sheets = excelReader.getSheets();
        for (Sheet sheet : sheets) {
            if (sheet.getSheetName().equals(ExcelSheetName.TEMPLATE_RULE_NAME)) {
                sheet.setClazz(ExcelTemplateRule.class);
                sheet.setHeadLineMun(1);
                excelReader.read(sheet);
            } else if (sheet.getSheetName().equals(ExcelSheetName.CUSTOM_RULE_NAME)) {
                sheet.setClazz(ExcelCustomRule.class);
                sheet.setHeadLineMun(1);
                excelReader.read(sheet);
            } else if (sheet.getSheetName().equals(ExcelSheetName.MULTI_TEMPLATE_RULE_NAME)) {
                sheet.setClazz(ExcelMultiTemplateRule.class);
                sheet.setHeadLineMun(1);
                excelReader.read(sheet);
            }
        }

        return listener;
    }

    private GeneralResponse<?> downloadRulesReal(List<Rule> rules, HttpServletResponse response) throws IOException, WriteExcelException {
        String fileName = "batch_rules_export_" + FILE_DATE_FORMATTER.format(new Date()) + ".xlsx";
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment;filename*=UTF-8''" + fileName);
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");

        // Write to response.getOutputStream
        OutputStream outputStream = response.getOutputStream();
        List<ExcelTemplateRule> templateRules = getTemplateRule(rules);
        List<ExcelCustomRule> customRules = getCustomRule(rules);
        List<ExcelMultiTemplateRule> multiTemplateRules = getMultiTemplateRule(rules);
        writeExcelToOutput(templateRules, customRules, multiTemplateRules, outputStream);
        outputStream.flush();
        LOGGER.info("Succeed to download all rules in type of excel");
        return null;
    }

    private void writeExcelToOutput(List<ExcelTemplateRule> templateRules, List<ExcelCustomRule> customRules,
                                    List<ExcelMultiTemplateRule> multiTemplateRules, OutputStream outputStream) throws WriteExcelException, IOException {
        try {
            LOGGER.info("Start to write excel");
            ExcelWriter writer = new ExcelWriter(outputStream, ExcelTypeEnum.XLSX, true);
            Sheet templateSheet = new Sheet(1, 0, ExcelTemplateRule.class);
            templateSheet.setSheetName(ExcelSheetName.TEMPLATE_RULE_NAME);
            writer.write(templateRules, templateSheet);

            Sheet customSheet = new Sheet(2, 0, ExcelCustomRule.class);
            customSheet.setSheetName(ExcelSheetName.CUSTOM_RULE_NAME);
            writer.write(customRules, customSheet);

            Sheet multiTemplateSheet = new Sheet(3, 0, ExcelMultiTemplateRule.class);
            multiTemplateSheet.setSheetName(ExcelSheetName.MULTI_TEMPLATE_RULE_NAME);
            writer.write(multiTemplateRules, multiTemplateSheet);

            writer.finish();
            LOGGER.info("Finish to write excel");
        } catch (Exception e) {
            throw new WriteExcelException(e.getMessage());
        } finally {
            outputStream.close();
        }
    }

    @Override
    public List<ExcelCustomRule> getCustomRule(Iterable<Rule> rules) {
        List<ExcelCustomRule> lines = new ArrayList<>();
        for (Rule rule : rules) {
            if (!rule.getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
                continue;
            }
            String ruleName = rule.getName();
            String outputName = rule.getOutputName();
            String functionName = FunctionTypeEnum.getFunctionByCode(rule.getFunctionType());
            String clusterName = rule.getRuleDataSources().iterator().next().getClusterName();
            String functionContent = rule.getFunctionContent();
            String fromContent = rule.getFromContent();
            String whereContent = rule.getWhereContent();
            Boolean saveMidTable = rule.getTemplate().getSaveMidTable();
            ExcelCustomRule ruleLinePrefix = new ExcelCustomRule();
            ruleLinePrefix.setRuleGroupName(rule.getRuleGroup().getRuleGroupName());
            ruleLinePrefix.setRuleName(ruleName);
            ruleLinePrefix.setOutputName(outputName);
            ruleLinePrefix.setFunctionName(functionName);
            ruleLinePrefix.setClusterName(clusterName);
            ruleLinePrefix.setFunctionContent(functionContent);
            ruleLinePrefix.setFromContent(fromContent);
            ruleLinePrefix.setWhereContent(whereContent);
            ruleLinePrefix.setSaveMidTable(saveMidTable);
            ruleLinePrefix.setAbortOnFailure(rule.getAbortOnFailure());
            Boolean alarmFlag = false;
            for (AlarmConfig alarmConfig : rule.getAlarmConfigs()) {
                String alarmOutputName = alarmConfig.getTemplateOutputMeta().getOutputName();
                String localeStr = httpServletRequest.getHeader("Content-Language");
                String checkTemplateName = CheckTemplateEnum.getCheckTemplateName(alarmConfig.getCheckTemplate(), localeStr);
                String alarmCompareType = CompareTypeEnum.getCompareTypeName(alarmConfig.getCompareType());
                Double threshold = alarmConfig.getThreshold();
                ExcelCustomRule tmp = new ExcelCustomRule(ruleLinePrefix);
                tmp.setAlarmCheckName(alarmOutputName);
                tmp.setCheckTemplateName(checkTemplateName);
                tmp.setCompareType(alarmCompareType);
                tmp.setThreshold(String.valueOf(threshold));
                LOGGER.info("Collect excel line: {}", tmp);
                lines.add(tmp);
                alarmFlag = true;
            }
            if (!alarmFlag) {
                lines.add(ruleLinePrefix);
            }
        }

        return lines;
    }

    @Override
    public List<ExcelMultiTemplateRule> getMultiTemplateRule(Iterable<Rule> rules) {
        List<ExcelMultiTemplateRule> lines = new ArrayList<>();
        for (Rule rule : rules) {
            if (!rule.getRuleType().equals(RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode())) {
                continue;
            }
            String ruleName = rule.getName();
            String templateName = rule.getTemplate().getName();
            String clusterName = rule.getRuleDataSources().iterator().next().getClusterName();
            ExcelMultiTemplateRule ruleLinePrefix = new ExcelMultiTemplateRule();
            ruleLinePrefix.setRuleGroupName(rule.getRuleGroup().getRuleGroupName());
            ruleLinePrefix.setRuleName(ruleName);
            ruleLinePrefix.setTemplateName(templateName);
            ruleLinePrefix.setClusterName(clusterName);
            ruleLinePrefix.setAbortOnFailure(rule.getAbortOnFailure());
            List<RuleVariable> filterRuleVariable = rule.getRuleVariables().stream().filter(ruleVariable ->
                    ruleVariable.getTemplateMidTableInputMeta().getInputType().equals(TemplateInputTypeEnum.CONDITION.getCode())).collect(Collectors.toList());
            if (filterRuleVariable != null && filterRuleVariable.size() != 0) {
                ruleLinePrefix.setWhereFilter(filterRuleVariable.iterator().next().getValue());
            }
            Boolean sourceAddedFlag = false;
            Boolean targetAddedFlag = false;
            ExcelMultiTemplateRule datasourceExcelRule = new ExcelMultiTemplateRule(ruleLinePrefix);
            for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
                String databaseName = ruleDataSource.getDbName();
                String tableName = ruleDataSource.getTableName();
                String filter = ruleDataSource.getFilter();
                Integer datasourceIndex = ruleDataSource.getDatasourceIndex();
                datasourceExcelRule.setClusterName(clusterName);
                if (datasourceIndex == 0) {
                    datasourceExcelRule.setLeftDbName(databaseName);
                    datasourceExcelRule.setLeftTableName(tableName);
                    datasourceExcelRule.setLeftFilter(filter);
                    sourceAddedFlag = true;
                } else {
                    datasourceExcelRule.setRightDbName(databaseName);
                    datasourceExcelRule.setRightTableName(tableName);
                    datasourceExcelRule.setRightFilter(filter);
                    targetAddedFlag = true;
                }
                if (sourceAddedFlag && targetAddedFlag) {
                    LOGGER.info("Collect excel line: {}", datasourceExcelRule);
                    lines.add(datasourceExcelRule);
                }
            }
            for (RuleDataSourceMapping mapping : rule.getRuleDataSourceMappings()) {
                ExcelMultiTemplateRule tmp = new ExcelMultiTemplateRule(ruleLinePrefix);
                tmp.setLeftMappingStatement(mapping.getLeftStatement());
                tmp.setMappingOperation(MappingOperationEnum.getByCode(mapping.getOperation()).getSymbol());
                tmp.setRightMappingStatement(mapping.getRightStatement());
                LOGGER.info("Collect excel line: {}", tmp);
                lines.add(tmp);
            }
            for (AlarmConfig alarmConfig : rule.getAlarmConfigs()) {
                String alarmOutputName = alarmConfig.getTemplateOutputMeta().getOutputName();
                String localeStr = httpServletRequest.getHeader("Content-Language");
                String checkTemplateName = CheckTemplateEnum.getCheckTemplateName(alarmConfig.getCheckTemplate(), localeStr);
                String alarmCompareType = CompareTypeEnum.getCompareTypeName(alarmConfig.getCompareType());
                Double threshold = alarmConfig.getThreshold();
                ExcelMultiTemplateRule tmp = new ExcelMultiTemplateRule(ruleLinePrefix);
                tmp.setAlarmCheckName(alarmOutputName);
                tmp.setCheckTemplateName(checkTemplateName);
                tmp.setCompareType(alarmCompareType);
                tmp.setThreshold(String.valueOf(threshold));
                LOGGER.info("Collect excel line: {}", tmp);
                lines.add(tmp);
            }
        }
        return lines;
    }

    @Override
    public List<ExcelTemplateRule> getTemplateRule(Iterable<Rule> rules) {
        List<ExcelTemplateRule> lines = new ArrayList<>();
        for (Rule rule : rules) {
            if (!rule.getRuleType().equals(RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode())) {
                continue;
            }
            String ruleName = rule.getName();
            String templateName = rule.getTemplate().getName();
            ExcelTemplateRule ruleLinePrefix = new ExcelTemplateRule();
            ruleLinePrefix.setRuleGroupName(rule.getRuleGroup().getRuleGroupName());
            ruleLinePrefix.setRuleName(ruleName);
            ruleLinePrefix.setTemplateName(templateName);
            ruleLinePrefix.setAbortOnFailure(rule.getAbortOnFailure());
            for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
                String clusterName = ruleDataSource.getClusterName();
                String databaseName = ruleDataSource.getDbName();
                String tableName = ruleDataSource.getTableName();
                String columnName = ruleDataSource.getColName();
                String filter = ruleDataSource.getFilter();
                ExcelTemplateRule tmp = new ExcelTemplateRule(ruleLinePrefix);
                tmp.setClusterName(clusterName);
                tmp.setDbName(databaseName);
                tmp.setTableName(tableName);
                tmp.setColNames(columnName);
                tmp.setFilter(filter);
                LOGGER.info("Collect excel line: {}", tmp);
                lines.add(tmp);
            }

            for (TemplateMidTableInputMeta templateMidTableInputMeta : rule.getTemplate().getTemplateMidTableInputMetas()) {
                if (TemplateMidTableUtil.shouldResponse(templateMidTableInputMeta)) {
                    for (RuleVariable ruleVariable : rule.getRuleVariables()) {
                        if (ruleVariable.getTemplateMidTableInputMeta().equals(templateMidTableInputMeta)) {
                            ExcelTemplateRule tmp = new ExcelTemplateRule(ruleLinePrefix);
                            String key = templateMidTableInputMeta.getName();
                            String value = StringEscapeUtils.unescapeJava(ruleVariable.getValue());
                            if (templateMidTableInputMeta.getInputType().equals(TemplateInputTypeEnum.REGEXP.getCode())) {
                                if (templateMidTableInputMeta.getRegexpType() != null) {
                                    value = ruleVariable.getOriginValue();
                                }
                            }
                            tmp.setArgumentKey(key);
                            tmp.setArgumentValue(value);
                            LOGGER.info("Collect excel line: {}", tmp);
                            lines.add(tmp);
                        }
                    }
                }
            }

            for (AlarmConfig alarmConfig : rule.getAlarmConfigs()) {
                String alarmOutputName = alarmConfig.getTemplateOutputMeta().getOutputName();
                String localeStr = httpServletRequest.getHeader("Content-Language");
                String checkTemplateName = CheckTemplateEnum.getCheckTemplateName(alarmConfig.getCheckTemplate(), localeStr);
                String alarmCompareType = CompareTypeEnum.getCompareTypeName(alarmConfig.getCompareType());
                Double threshold = alarmConfig.getThreshold();
                ExcelTemplateRule tmp = new ExcelTemplateRule(ruleLinePrefix);
                tmp.setAlarmCheckName(alarmOutputName);
                tmp.setCheckTemplateName(checkTemplateName);
                tmp.setCompareType(alarmCompareType);
                tmp.setThreshold(String.valueOf(threshold));
                LOGGER.info("Collect excel line: {}", tmp);
                lines.add(tmp);
            }
        }

        return lines;
    }
}
