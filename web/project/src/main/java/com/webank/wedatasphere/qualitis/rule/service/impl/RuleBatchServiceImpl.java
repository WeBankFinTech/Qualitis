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
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.TaskNotExistException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.parser.LocaleParser;
import com.webank.wedatasphere.qualitis.project.constant.ExcelSheetName;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.excel.ExcelCustomRuleByProject;
import com.webank.wedatasphere.qualitis.project.excel.ExcelMultiTemplateRuleByProject;
import com.webank.wedatasphere.qualitis.project.excel.ExcelTemplateFileRuleByProject;
import com.webank.wedatasphere.qualitis.project.excel.ExcelTemplateRuleByProject;
import com.webank.wedatasphere.qualitis.project.request.AddProjectRequest;
import com.webank.wedatasphere.qualitis.project.service.ProjectEventService;
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
import com.webank.wedatasphere.qualitis.rule.excel.ExcelRuleListener;
import com.webank.wedatasphere.qualitis.rule.exception.WriteExcelException;
import com.webank.wedatasphere.qualitis.rule.request.AddCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddFileRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.AlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.CustomAlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceColumnRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.DownloadRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.FileAlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyFileRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplateArgumentRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.AddMultiSourceRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.ModifyMultiSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinColumnRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinConfigRequest;
import com.webank.wedatasphere.qualitis.rule.service.CustomRuleService;
import com.webank.wedatasphere.qualitis.rule.service.FileRuleService;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author howeye
 */
@Service
public class RuleBatchServiceImpl implements RuleBatchService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private RuleDao ruleDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private RuleGroupDao ruleGroupDao;

    @Autowired
    private RuleTemplateDao templateDao;

    @Autowired
    private RuleMetricDao ruleMetricDao;

    @Autowired
    private TemplateMidTableInputMetaDao templateMidTableInputMetaDao;

    @Autowired
    private TemplateOutputMetaDao templateOutputMetaDao;

    @Autowired
    private ProjectEventService projectEventService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private FileRuleService fileRuleService;

    @Autowired
    private CustomRuleService customRuleService;

    @Autowired
    private MultiSourceRuleService multiSourceRuleService;

    @Autowired
    private LocaleParser localeParser;

    private static final String SUPPORT_EXCEL_SUFFIX_NAME = ".xlsx";

    private final FastDateFormat FILE_DATE_FORMATTER = FastDateFormat.getInstance("yyyyMMddHHmmss");

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleBatchServiceImpl.class);

    private HttpServletRequest httpServletRequest;

    public RuleBatchServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }



    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> downloadRules(DownloadRuleRequest request, HttpServletResponse response) throws UnExpectedRequestException, IOException
        , WriteExcelException, PermissionDeniedRequestException {
        // Check Arguments
        DownloadRuleRequest.checkRequest(request);
        String loginUser = HttpUtils.getUserName(httpServletRequest);

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
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(ruleLists.iterator().next().getProject(), loginUser, permissions);
        LOGGER.info("Succeed to find rules that will be downloaded. rule_ids: {}", ruleLists.stream().map(Rule::getId));

        return downloadRulesReal(ruleLists, response, loginUser);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> uploadRules(InputStream fileInputStream, FormDataContentDisposition fileDisposition, Long projectId)
        throws UnExpectedRequestException, IOException, MetaDataAcquireFailedException, SemanticException, ParseException, ClusterInfoNotConfigException, TaskNotExistException, PermissionDeniedRequestException {
        String userName = HttpUtils.getUserName(httpServletRequest);
        return uploadRulesReal(fileInputStream, fileDisposition.getFileName(), userName, projectId, false);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> outerUploadRules(InputStream fileInputStream, String fileName, String userName)
        throws UnExpectedRequestException, IOException, MetaDataAcquireFailedException, SemanticException, ParseException, ClusterInfoNotConfigException, TaskNotExistException, PermissionDeniedRequestException {
        return uploadRulesReal(fileInputStream, fileName, userName, null, true);
    }

    private GeneralResponse<?> uploadRulesReal(InputStream fileInputStream, String fileName, String userName, Long projectId, boolean aomp)
        throws UnExpectedRequestException, ClusterInfoNotConfigException, ParseException, SemanticException, MetaDataAcquireFailedException, TaskNotExistException, IOException, PermissionDeniedRequestException {
        if (userName == null) {
            return new GeneralResponse<>("401", "{&PLEASE_LOGIN}", null);
        }

        // Check suffix name of file
        String suffixName = fileName.substring(fileName.lastIndexOf('.'));
        if (! suffixName.equals(SUPPORT_EXCEL_SUFFIX_NAME)) {
            throw new UnExpectedRequestException("{&DO_NOT_SUPPORT_SUFFIX_NAME}: [" + suffixName + "]. {&ONLY_SUPPORT} [" + SUPPORT_EXCEL_SUFFIX_NAME + "]", 422);
        }
        ExcelRuleListener excelRuleListener = readExcel(fileInputStream);
        if (excelRuleListener.getCustomExcelContent().isEmpty() && excelRuleListener.getTemplateExcelContent().isEmpty()
            && excelRuleListener.getMultiTemplateExcelContent().isEmpty() && excelRuleListener.getTemplateFileExcelContent().isEmpty()) {
            throw new UnExpectedRequestException("{&FILE_CAN_NOT_BE_EMPTY_OR_FILE_CAN_NOT_BE_RECOGNIZED}", 422);
        }
        // Record project event.
        List<String> ruleNames = new ArrayList<>(excelRuleListener.getTemplateExcelContent().keySet().size()
                                                                + excelRuleListener.getCustomExcelContent().keySet().size()
                                                                + excelRuleListener.getTemplateFileExcelContent().keySet().size()
                                                                + excelRuleListener.getMultiTemplateExcelContent().keySet().size());
        ruleNames.addAll(excelRuleListener.getCustomExcelContent().keySet());
        ruleNames.addAll(excelRuleListener.getTemplateExcelContent().keySet());
        ruleNames.addAll(excelRuleListener.getTemplateFileExcelContent().keySet());
        ruleNames.addAll(excelRuleListener.getMultiTemplateExcelContent().keySet());
        if (projectId != null) {
            Project projectInDb = projectDao.findById(projectId);
            if (projectInDb == null) {
                throw new UnExpectedRequestException("{&PROJECT_ID} {&DOES_NOT_EXIST}");
            }
            // Check permissions of project
            List<Integer> permissions = new ArrayList<>();
            permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
            projectService.checkProjectPermission(projectInDb, userName, permissions);
            getAndSaveRule(excelRuleListener.getTemplateExcelContent(), excelRuleListener.getCustomExcelContent(),
                excelRuleListener.getMultiTemplateExcelContent(), excelRuleListener.getTemplateFileExcelContent(), projectInDb, userName, aomp);
//            projectEventService.record(projectInDb.getId(), userName, "upload", "[" + Arrays.toString(ruleNames.toArray()) + "].", EventTypeEnum.MODIFY_PROJECT.getCode());
        } else {
            // All three rules are derived from the same project.
            String projectName = "";
            if (CollectionUtils.isNotEmpty(excelRuleListener.getTemplateExcelContent().keySet())) {
                projectName = excelRuleListener.getTemplateExcelContent().values().iterator().next().iterator().next().getProjectName();
            } else if (CollectionUtils.isNotEmpty(excelRuleListener.getCustomExcelContent().keySet())) {
                projectName = excelRuleListener.getCustomExcelContent().values().iterator().next().iterator().next().getProjectName();
            } else if (CollectionUtils.isNotEmpty(excelRuleListener.getMultiTemplateExcelContent().keySet())) {
                projectName = excelRuleListener.getMultiTemplateExcelContent().values().iterator().next().iterator().next().getProjectName();
            } else if (CollectionUtils.isNotEmpty(excelRuleListener.getTemplateFileExcelContent().keySet())) {
                projectName = excelRuleListener.getTemplateFileExcelContent().values().iterator().next().iterator().next().getProjectName();
            }
            Project project = projectDao.findByNameAndCreateUser(projectName, userName);
            if (project == null) {
                // Auto create project for aomp rules upload.
                AddProjectRequest addProjectRequest = new AddProjectRequest();
                String currentProjectName = "AOMP_AUTO_PROJECT_" + System.currentTimeMillis();
                addProjectRequest.setProjectName(currentProjectName);
                addProjectRequest.setDescription("This is for aomp");

                User user = userDao.findByUsername(userName);

                Long userId = user.getId();
                projectService.addProject(addProjectRequest, userId);
                project = projectDao.findByNameAndCreateUser(currentProjectName, userName);
            }
            getAndSaveRule(excelRuleListener.getTemplateExcelContent(), excelRuleListener.getCustomExcelContent(),
                excelRuleListener.getMultiTemplateExcelContent(), excelRuleListener.getTemplateFileExcelContent(), project, userName, aomp);
//            projectEventService.record(project.getId(), userName, "upload", "[" + Arrays.toString(ruleNames.toArray()) + "].", EventTypeEnum.MODIFY_PROJECT.getCode());
        }
        fileInputStream.close();

        return new GeneralResponse<>("200", "{&SUCCEED_TO_UPLOAD_FILE}", null);
    }

    private ExcelRuleListener readExcel(InputStream inputStream) {
        LOGGER.info("Start to read excel");
        ExcelRuleListener excelRuleListener = partitionByRuleName(inputStream);
        LOGGER.info("Finish to read excel. template rule content: {}", excelRuleListener.getTemplateExcelContent());
        LOGGER.info("Finish to read excel. custom rule content: {}", excelRuleListener.getCustomExcelContent());
        LOGGER.info("Finish to read excel. multi rule content: {}", excelRuleListener.getMultiTemplateExcelContent());
        LOGGER.info("Finish to read excel. template file rule content: {}", excelRuleListener.getTemplateFileExcelContent());
        return excelRuleListener;
    }

    @Override
    public void getAndSaveRule(Map<String, List<ExcelTemplateRuleByProject>> rulePartitionedByRuleName,
        Map<String, List<ExcelCustomRuleByProject>> customRulePartitionedByRuleName,
        Map<String, List<ExcelMultiTemplateRuleByProject>> multiRulePartitionedByRuleName,
        Map<String, List<ExcelTemplateFileRuleByProject>> fileRulePartitionedByRuleName, Project project, String userName, boolean aomp)
        throws UnExpectedRequestException, MetaDataAcquireFailedException, SemanticException, ParseException, TaskNotExistException, ClusterInfoNotConfigException, PermissionDeniedRequestException {

        // Add template rule
        if (rulePartitionedByRuleName != null) {
            addTemplateRule(rulePartitionedByRuleName, project, userName, aomp);
        }

        // Add custom rule
        if (customRulePartitionedByRuleName != null) {
            addCustomRule(customRulePartitionedByRuleName, project, userName, aomp);
        }

        // Add multi-table rule
        if (multiRulePartitionedByRuleName != null) {
            addMultiTemplateRule(multiRulePartitionedByRuleName, project, userName, aomp);
        }

        // Add template file rule
        if (fileRulePartitionedByRuleName != null) {
            addTemplateFileRule(fileRulePartitionedByRuleName, project, userName, aomp);
        }
    }

    private void addTemplateFileRule(Map<String, List<ExcelTemplateFileRuleByProject>> fileRulePartitionedByRuleName, Project project,
        String userName, boolean aomp) throws UnExpectedRequestException, PermissionDeniedRequestException {

        Map<String, List<ExcelTemplateFileRuleByProject>> modifyRulePartitionedByRuleName = new HashMap<>(1);
        Map<String, List<ExcelTemplateFileRuleByProject>> addRulePartitionedByRuleName = new HashMap<>(1);
        String localeStr = httpServletRequest.getHeader("Content-Language");
        for (Iterator<String> iterator = fileRulePartitionedByRuleName.keySet().iterator(); iterator.hasNext(); ) {
            String currentRuleName = iterator.next();
            Rule currentRule = ruleDao.findByProjectAndRuleName(project, currentRuleName);
            if (currentRule != null) {
                modifyRulePartitionedByRuleName.put(currentRuleName, fileRulePartitionedByRuleName.get(currentRuleName));
                List<AddFileRuleRequest> addRuleRequestList = constructAddFileRuleRequest(modifyRulePartitionedByRuleName, project, localeStr);
                ModifyFileRuleRequest modifyRuleRequest = new ModifyFileRuleRequest();
                modifyRuleRequest.setRuleId(currentRule.getId());
                AddFileRuleRequest addRuleRequest = addRuleRequestList.iterator().next();
                BeanUtils.copyProperties(addRuleRequest, modifyRuleRequest);
                LOGGER.info("Start to modify template file rule. request: {}", modifyRuleRequest);
                if (aomp) {
                    fileRuleService.modifyRuleDetailForOuter(modifyRuleRequest, userName);
                } else {
                    fileRuleService.modifyRuleDetail(modifyRuleRequest);
                }
                LOGGER.info("Success to modify template file rule");
                modifyRulePartitionedByRuleName.clear();
            } else {
                addRulePartitionedByRuleName.put(currentRuleName, fileRulePartitionedByRuleName.get(currentRuleName));
                List<AddFileRuleRequest> addRuleRequestList = constructAddFileRuleRequest(addRulePartitionedByRuleName, project, localeStr);

                AddFileRuleRequest currentAddFileRuleRequest = addRuleRequestList.iterator().next();
                LOGGER.info("Start to add template file rule. request: {}", currentAddFileRuleRequest);
                if (aomp) {
                    fileRuleService.addRuleForOuter(currentAddFileRuleRequest, userName);
                } else {
                    fileRuleService.addRuleForUpload(currentAddFileRuleRequest);
                }
                LOGGER.info("Success to add template file rule.");
                addRulePartitionedByRuleName.clear();
            }

        }
    }

    private List<AddFileRuleRequest> constructAddFileRuleRequest(Map<String, List<ExcelTemplateFileRuleByProject>> fileRulePartitionedByRuleName
                                                                            , Project project, String localeStr) throws UnExpectedRequestException {

        List<AddFileRuleRequest> addFileRuleRequests = new ArrayList<>();
        for (String ruleName : fileRulePartitionedByRuleName.keySet()) {
            List<ExcelTemplateFileRuleByProject> ruleInfos = fileRulePartitionedByRuleName.get(ruleName);
            ExcelTemplateFileRuleByProject firstCommonInfo = ruleInfos.get(0);

            AddFileRuleRequest addFileRuleRequest = new AddFileRuleRequest();
            String ruleGroupName = firstCommonInfo.getRuleGroupName();
            if (StringUtils.isBlank(ruleGroupName)) {
                throw new UnExpectedRequestException("RuleGroupName {&CAN_NOT_BE_NULL_OR_EMPTY}");
            }

            boolean alarm = false;
            List<FileAlarmConfigRequest> alarmConfigRequests = new ArrayList<>();
            DataSourceRequest dataSourceRequest = new DataSourceRequest();
            Long projectId = project.getId();
            for (ExcelTemplateFileRuleByProject excelTemplateFileRule : ruleInfos) {
                getDataFileSourceRequest(dataSourceRequest, excelTemplateFileRule);
                getFileAlarmConfig(alarmConfigRequests, excelTemplateFileRule, localeStr);
            }

            RuleGroup ruleGroupInDb = ruleGroupDao.findByRuleGroupNameAndProjectId(ruleGroupName, projectId);
            if (ruleGroupInDb != null) {
                addFileRuleRequest.setRuleGroupId(ruleGroupInDb.getId());
            } else {
                RuleGroup ruleGroup = ruleGroupDao.saveRuleGroup(
                    new RuleGroup(ruleGroupName, project.getId()));
                addFileRuleRequest.setRuleGroupId(ruleGroup.getId());
            }

            // Construct addRuleRequest
            addFileRuleRequest.setRuleName(ruleName);
            addFileRuleRequest.setRuleDetail(firstCommonInfo.getRuleDetail());
            addFileRuleRequest.setAbortOnFailure(firstCommonInfo.getAbortOnFailure());

            if (!alarmConfigRequests.isEmpty()) {
                alarm = true;
            }
            addFileRuleRequest.setAlarm(alarm);
            addFileRuleRequest.setAlarmVariable(alarmConfigRequests);
            addFileRuleRequest.setDatasource(dataSourceRequest);
            addFileRuleRequest.setProjectId(projectId);

            addFileRuleRequests.add(addFileRuleRequest);
        }

        return addFileRuleRequests;
    }

    private void getFileAlarmConfig(List<FileAlarmConfigRequest> alarmConfigRequests, ExcelTemplateFileRuleByProject excelTemplateFileRule, String localeStr)
        throws UnExpectedRequestException {
        String templateOutputName = excelTemplateFileRule.getAlarmCheckName();
        if (! StringUtils.isBlank(templateOutputName)) {
            Integer unit = FileOutputUnitEnum.fileOutputUnitCode(excelTemplateFileRule.getUnit());
            String checkTemplateName = excelTemplateFileRule.getCheckTemplateName();
            String compareTypeName = excelTemplateFileRule.getCompareType();
            String threshold = excelTemplateFileRule.getThreshold();

            FileAlarmConfigRequest fileAlarmConfigRequest = new FileAlarmConfigRequest();
            fileAlarmConfigRequest.setFileOutputName(FileOutputNameEnum.getFileOutputNameCode(templateOutputName, localeStr));
            fileAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.getCheckTemplateCode(checkTemplateName, localeStr));
            fileAlarmConfigRequest.setCompareType(CompareTypeEnum.getCompareTypeCode(compareTypeName));
            fileAlarmConfigRequest.setThreshold(Double.valueOf(threshold));
            fileAlarmConfigRequest.setFileOutputUnit(unit);
            // Rule Metric.
            String ruleMetricEnCode = excelTemplateFileRule.getRuleMetricEnCode();
            if (StringUtils.isNotBlank(ruleMetricEnCode)) {
                RuleMetric ruleMetricInDb = ruleMetricDao.findByEnCode(ruleMetricEnCode);
                if (ruleMetricInDb == null) {
                    throw new UnExpectedRequestException("Rule metric[Code=" + ruleMetricEnCode + "] " + "{&DOES_NOT_EXIST}");
                }
                String code = ruleMetricInDb.getEnCode();
                fileAlarmConfigRequest.setRuleMetricEnCode(code);
            }
            alarmConfigRequests.add(fileAlarmConfigRequest);
        }
    }

    private void getDataFileSourceRequest(DataSourceRequest dataSourceRequest, ExcelTemplateFileRuleByProject excelTemplateFileRule) {
        String clusterName = excelTemplateFileRule.getClusterName();
        if (! StringUtils.isBlank(clusterName)) {
            dataSourceRequest.setClusterName(excelTemplateFileRule.getClusterName());
            dataSourceRequest.setProxyUser(excelTemplateFileRule.getProxyUser());
            dataSourceRequest.setTableName(excelTemplateFileRule.getTableName());
            dataSourceRequest.setDbName(excelTemplateFileRule.getDatabaseName());
            dataSourceRequest.setFilter(excelTemplateFileRule.getFilter());
        }
    }

    private void addMultiTemplateRule(Map<String, List<ExcelMultiTemplateRuleByProject>> multiRulePartitionedByRuleName, Project project,
        String userName, boolean aomp)
        throws UnExpectedRequestException, ClusterInfoNotConfigException, TaskNotExistException, PermissionDeniedRequestException {

        Map<String, List<ExcelMultiTemplateRuleByProject>> modifyRulePartitionedByRuleName = new HashMap<>(1);
        Map<String, List<ExcelMultiTemplateRuleByProject>> addRulePartitionedByRuleName = new HashMap<>(1);
        String localeStr = httpServletRequest.getHeader("Content-Language");

        for (Iterator<String> iterator = multiRulePartitionedByRuleName.keySet().iterator(); iterator.hasNext(); ) {
            String currentRuleName = iterator.next();
            Rule currentRule = ruleDao.findByProjectAndRuleName(project, currentRuleName);
            if (currentRule != null) {
                modifyRulePartitionedByRuleName.put(currentRuleName, multiRulePartitionedByRuleName.get(currentRuleName));
                List<AddMultiSourceRuleRequest> addRuleRequestList = constructAddMultiSourceRuleRequest(modifyRulePartitionedByRuleName, project, localeStr);
                ModifyMultiSourceRequest modifyRuleRequest = new ModifyMultiSourceRequest();
                modifyRuleRequest.setRuleId(currentRule.getId());

                AddMultiSourceRuleRequest addRuleRequest = addRuleRequestList.iterator().next();
                BeanUtils.copyProperties(addRuleRequest, modifyRuleRequest);

                LOGGER.info("Start to modify multi rule. request: {}", modifyRuleRequest);
                if (aomp) {
                    multiSourceRuleService.modifyRuleDetailForOuter(modifyRuleRequest, userName);
                } else {
                    multiSourceRuleService.modifyMultiSourceRule(modifyRuleRequest);
                }
                LOGGER.info("Success to modify multi rule.");
                modifyRulePartitionedByRuleName.clear();
            } else {
                addRulePartitionedByRuleName.put(currentRuleName, multiRulePartitionedByRuleName.get(currentRuleName));
                List<AddMultiSourceRuleRequest> addRuleRequestList = constructAddMultiSourceRuleRequest(addRulePartitionedByRuleName, project, localeStr);
                AddMultiSourceRuleRequest currentAddMultiSourceRuleRequest = addRuleRequestList.iterator().next();
                LOGGER.info("Start to add multi rule. request: {}", currentAddMultiSourceRuleRequest);
                if (aomp) {
                    currentAddMultiSourceRuleRequest.setLoginUser(userName);
                    multiSourceRuleService.addRuleForOuter(currentAddMultiSourceRuleRequest, true);
                } else {
                    multiSourceRuleService.addMultiSourceRuleForUpload(currentAddMultiSourceRuleRequest, true);
                }
                LOGGER.info("Success to add multi rule.");
                addRulePartitionedByRuleName.clear();
            }

        }

    }

    private void addCustomRule(Map<String, List<ExcelCustomRuleByProject>> customRulePartitionedByRuleName, Project project, String userName, boolean aomp)
        throws UnExpectedRequestException, SemanticException, ParseException, TaskNotExistException, ClusterInfoNotConfigException, MetaDataAcquireFailedException, PermissionDeniedRequestException {

        Map<String, List<ExcelCustomRuleByProject>> modifyRulePartitionedByRuleName = new HashMap<>(1);
        Map<String, List<ExcelCustomRuleByProject>> addRulePartitionedByRuleName = new HashMap<>(1);
        String localeStr = httpServletRequest.getHeader("Content-Language");
        for (Iterator<String> iterator = customRulePartitionedByRuleName.keySet().iterator(); iterator.hasNext(); ) {
            String currentRuleName = iterator.next();
            Rule currentRule = ruleDao.findByProjectAndRuleName(project, currentRuleName);
            if (currentRule != null) {
                modifyRulePartitionedByRuleName.put(currentRuleName, customRulePartitionedByRuleName.get(currentRuleName));
                List<AddCustomRuleRequest> addRuleRequestList = constructAddCustomRuleRequest(modifyRulePartitionedByRuleName, project, localeStr);
                ModifyCustomRuleRequest modifyRuleRequest = new ModifyCustomRuleRequest();
                modifyRuleRequest.setRuleId(currentRule.getId());

                AddCustomRuleRequest addRuleRequest = addRuleRequestList.iterator().next();
                BeanUtils.copyProperties(addRuleRequest, modifyRuleRequest);

                LOGGER.info("Start to modify custom rule. request: {}", modifyRuleRequest);
                if (aomp) {
                    customRuleService.modifyRuleDetailForOuter(modifyRuleRequest, userName);
                } else {
                    customRuleService.modifyCustomRule(modifyRuleRequest);
                }
                LOGGER.info("Succeed to modify custom rule");
                modifyRulePartitionedByRuleName.clear();
            } else {
                addRulePartitionedByRuleName.put(currentRuleName, customRulePartitionedByRuleName.get(currentRuleName));
                List<AddCustomRuleRequest> addRuleRequestList = constructAddCustomRuleRequest(addRulePartitionedByRuleName, project, localeStr);

                AddCustomRuleRequest currentAddCustomRuleRequest = addRuleRequestList.iterator().next();
                LOGGER.info("Start to add custom rule. request: {}", currentAddCustomRuleRequest);
                if (aomp) {
                    customRuleService.addRuleForOuter(currentAddCustomRuleRequest, userName);
                } else {
                    customRuleService.addCustomRuleForUpload(currentAddCustomRuleRequest);
                }
                LOGGER.info("Succeed to add custom rule");
                addRulePartitionedByRuleName.clear();
            }

        }

    }

    private void addTemplateRule(Map<String, List<ExcelTemplateRuleByProject>> rulePartitionedByRuleName, Project project, String userName, boolean aomp)
        throws UnExpectedRequestException, TaskNotExistException, ClusterInfoNotConfigException, PermissionDeniedRequestException {

        Map<String, List<ExcelTemplateRuleByProject>> modifyRulePartitionedByRuleName = new HashMap<>(1);
        Map<String, List<ExcelTemplateRuleByProject>> addRulePartitionedByRuleName = new HashMap<>(1);
        String localeStr = httpServletRequest.getHeader("Content-Language");

        for (Iterator<String> iterator = rulePartitionedByRuleName.keySet().iterator(); iterator.hasNext(); ) {
            String currentRuleName = iterator.next();
            Rule currentRule = ruleDao.findByProjectAndRuleName(project, currentRuleName);
            if (currentRule != null) {
                modifyRulePartitionedByRuleName.put(currentRuleName, rulePartitionedByRuleName.get(currentRuleName));
                List<AddRuleRequest> addRuleRequestList = constructAddRuleRequest(modifyRulePartitionedByRuleName, project, localeStr);
                ModifyRuleRequest modifyRuleRequest = new ModifyRuleRequest();
                modifyRuleRequest.setRuleId(currentRule.getId());

                AddRuleRequest addRuleRequest = addRuleRequestList.iterator().next();
                BeanUtils.copyProperties(addRuleRequest, modifyRuleRequest);

                LOGGER.info("Start to modify template rule. request: {}", modifyRuleRequest);
                if (aomp) {
                    ruleService.modifyRuleDetailForOuter(modifyRuleRequest, userName);
                } else {
                    ruleService.modifyRuleDetail(modifyRuleRequest);
                }
                LOGGER.info("Succeed to modify template rule.");
                modifyRulePartitionedByRuleName.clear();
            } else {
                addRulePartitionedByRuleName.put(currentRuleName, rulePartitionedByRuleName.get(currentRuleName));

                List<AddRuleRequest> addRuleRequestList = constructAddRuleRequest(addRulePartitionedByRuleName, project, localeStr);
                AddRuleRequest currentAddRuleRequest = addRuleRequestList.iterator().next();

                LOGGER.info("Start to add template rule. request: {}", currentAddRuleRequest);
                if (aomp) {
                    ruleService.addRuleForOuter(currentAddRuleRequest, userName);
                } else {
                    ruleService.addRuleForUpload(currentAddRuleRequest);
                }
                LOGGER.info("Succeed to add template rule.");
                addRulePartitionedByRuleName.clear();
            }

        }
    }

    private List<AddMultiSourceRuleRequest> constructAddMultiSourceRuleRequest(Map<String, List<ExcelMultiTemplateRuleByProject>> multiRulePartitionedByRuleName
                                                                                    , Project project, String localeStr) throws UnExpectedRequestException {
        List<AddMultiSourceRuleRequest> addMultiSourceRuleRequests = new ArrayList<>();
        for (String ruleName : multiRulePartitionedByRuleName.keySet()) {
            List<ExcelMultiTemplateRuleByProject> ruleInfos = multiRulePartitionedByRuleName.get(ruleName);
            ExcelMultiTemplateRuleByProject firstCommonInfo = ruleInfos.get(0);

            AddMultiSourceRuleRequest addMultiSourceRuleRequest = new AddMultiSourceRuleRequest();
            String ruleTemplateName = firstCommonInfo.getTemplateName();
            String ruleGroupName = firstCommonInfo.getRuleGroupName();
            addMultiSourceRuleRequest.setSpecifyStaticStartupParam(firstCommonInfo.getSpecifyStaticStartupParam());
            addMultiSourceRuleRequest.setDeleteFailCheckResult(firstCommonInfo.getDeleteFailCheckResult());
            addMultiSourceRuleRequest.setStaticStartupParam(firstCommonInfo.getStaticStartupParam());
            addMultiSourceRuleRequest.setAbortOnFailure(firstCommonInfo.getAbortOnFailure());

            if (StringUtils.isBlank(ruleGroupName)) {
                throw new UnExpectedRequestException("RuleGroupName {&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            Template template = findTemplateByName(ruleTemplateName);
            if (template == null) {
                throw new UnExpectedRequestException("{&TEMPLATE_NAME}: [" + ruleTemplateName + "] {&DOES_NOT_EXIST}");
            }

            String filter = null;
            boolean alarm = false;
            String clusterName = firstCommonInfo.getClusterName();
            MultiDataSourceConfigRequest sourceConfigRequest = new MultiDataSourceConfigRequest();
            MultiDataSourceConfigRequest targetConfigRequest = new MultiDataSourceConfigRequest();
            List<MultiDataSourceJoinConfigRequest> mappings = new ArrayList<>();
            List<AlarmConfigRequest> alarmConfigRequests = new ArrayList<>();
            for (ExcelMultiTemplateRuleByProject excelMultiTemplateRule : ruleInfos) {
                if (StringUtils.isNotBlank(excelMultiTemplateRule.getWhereFilter())) {
                    filter = excelMultiTemplateRule.getWhereFilter();
                }

                getAlarmConfig(alarmConfigRequests, excelMultiTemplateRule, template, localeStr);
                getMultiDataSourceRequest(sourceConfigRequest, excelMultiTemplateRule, 0);
                getMultiDataSourceRequest(targetConfigRequest, excelMultiTemplateRule, 1);
                getMultiDataSourceJoinRequest(mappings, excelMultiTemplateRule);
            }

            if (alarmConfigRequests.size() != 0) {
                alarm = true;
            }

            RuleGroup ruleGroupInDb = ruleGroupDao.findByRuleGroupNameAndProjectId(ruleGroupName, project.getId());
            if (ruleGroupInDb != null) {
                addMultiSourceRuleRequest.setRuleGroupId(ruleGroupInDb.getId());
            } else {
                RuleGroup ruleGroup = ruleGroupDao.saveRuleGroup(new RuleGroup(ruleGroupName, project.getId()));
                addMultiSourceRuleRequest.setRuleGroupId(ruleGroup.getId());
            }

            addMultiSourceRuleRequest.setRuleName(ruleName);
            addMultiSourceRuleRequest.setClusterName(clusterName);
            addMultiSourceRuleRequest.setProjectId(project.getId());
            addMultiSourceRuleRequest.setRuleDetail(firstCommonInfo.getRuleDetail());
            addMultiSourceRuleRequest.setMultiSourceRuleTemplateId(template.getId());
            addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
            addMultiSourceRuleRequests.add(addMultiSourceRuleRequest);
            addMultiSourceRuleRequest.setSource(sourceConfigRequest);
            addMultiSourceRuleRequest.setTarget(targetConfigRequest);
            addMultiSourceRuleRequest.setMappings(mappings);
            addMultiSourceRuleRequest.setFilter(filter);
            addMultiSourceRuleRequest.setAlarm(alarm);
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

    private void getMultiDataSourceJoinRequest(List<MultiDataSourceJoinConfigRequest> mappings, ExcelMultiTemplateRuleByProject excelMultiTemplateRule)
        throws UnExpectedRequestException {
        String leftStatement = excelMultiTemplateRule.getLeftMappingStatement();
        if (StringUtils.isNotBlank(leftStatement)) {
            MultiDataSourceJoinConfigRequest mapping = new MultiDataSourceJoinConfigRequest();
            List<MultiDataSourceJoinColumnRequest> leftRequest = getMultiDataSourceJoinColumnRequest(excelMultiTemplateRule.getLeftMappingNames(), excelMultiTemplateRule.getLeftMappingTypes());
            List<MultiDataSourceJoinColumnRequest> rightRequest = getMultiDataSourceJoinColumnRequest(excelMultiTemplateRule.getRightMappingNames(), excelMultiTemplateRule.getRightMappingTypes());
            mapping.setLeft(leftRequest);
            mapping.setLeftStatement(excelMultiTemplateRule.getLeftMappingStatement());
            mapping.setOperation(MappingOperationEnum.getOperationCode(excelMultiTemplateRule.getMappingOperation()));
            mapping.setRight(rightRequest);
            mapping.setRightStatement(excelMultiTemplateRule.getRightMappingStatement());
            mappings.add(mapping);
        }
    }

    private List<MultiDataSourceJoinColumnRequest> getMultiDataSourceJoinColumnRequest(String names, String types) throws UnExpectedRequestException {
        List<MultiDataSourceJoinColumnRequest> joinColumnRequests = new ArrayList<>();
        String[] columnNames = names.split(",");
        String[] columnTypes = types.split("\\|");
        if (columnNames.length != columnTypes.length) {
            throw new UnExpectedRequestException("Mapping fields names and types not corrent.");
        }
        for (int i = 0; i < columnNames.length; i ++) {
            joinColumnRequests.add(new MultiDataSourceJoinColumnRequest(columnNames[i], columnTypes[i]));
        }
        return joinColumnRequests;
    }

    private void getMultiDataSourceRequest(MultiDataSourceConfigRequest multiDataSourceConfigRequest, ExcelMultiTemplateRuleByProject excelMultiTemplateRule, Integer index) {
        String leftDbName = excelMultiTemplateRule.getLeftDbName();
        String rightDbName = excelMultiTemplateRule.getRightDbName();
        if (StringUtils.isNotBlank(leftDbName) || StringUtils.isNotBlank(rightDbName)) {
            if (index == 0) {
                if (StringUtils.isNotBlank(excelMultiTemplateRule.getLeftLinkisDataSourceId())) {
                    multiDataSourceConfigRequest.setLinkisDataSourceType(excelMultiTemplateRule.getLeftLinkisDataSourceType());
                    multiDataSourceConfigRequest.setLinkisDataSourceName(excelMultiTemplateRule.getLeftLinkisDataSourceName());
                    multiDataSourceConfigRequest.setLinkisDataSourceId(Long.parseLong(excelMultiTemplateRule.getLeftLinkisDataSourceId()));
                }
                multiDataSourceConfigRequest.setDbName(excelMultiTemplateRule.getLeftDbName());
                multiDataSourceConfigRequest.setTableName(excelMultiTemplateRule.getLeftTableName());
                multiDataSourceConfigRequest.setFilter(excelMultiTemplateRule.getLeftFilter());
                multiDataSourceConfigRequest.setProxyUser(excelMultiTemplateRule.getLeftProxyUser());
            } else {
                if (StringUtils.isNotBlank(excelMultiTemplateRule.getRightLinkisDataSourceId())) {
                    multiDataSourceConfigRequest.setLinkisDataSourceType(excelMultiTemplateRule.getRightLlinkisDataSourceType());
                    multiDataSourceConfigRequest.setLinkisDataSourceName(excelMultiTemplateRule.getRightLlinkisDataSourceName());
                    multiDataSourceConfigRequest.setLinkisDataSourceId(Long.parseLong(excelMultiTemplateRule.getRightLinkisDataSourceId()));
                }
                multiDataSourceConfigRequest.setDbName(excelMultiTemplateRule.getRightDbName());
                multiDataSourceConfigRequest.setTableName(excelMultiTemplateRule.getRightTableName());
                multiDataSourceConfigRequest.setFilter(excelMultiTemplateRule.getRightFilter());
                multiDataSourceConfigRequest.setProxyUser(excelMultiTemplateRule.getRightProxyUser());
            }
        }
    }

    private void getAlarmConfig(List<AlarmConfigRequest> alarmConfigRequests, ExcelMultiTemplateRuleByProject excelMultiTemplateRule, Template template, String localeStr) throws UnExpectedRequestException {
        String templateOutputName = excelMultiTemplateRule.getAlarmCheckName();
        if (! StringUtils.isBlank(templateOutputName)) {
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
            alarmConfigRequest.setCheckTemplate(CheckTemplateEnum.getCheckTemplateCode(checkTemplateName, localeStr));
            alarmConfigRequest.setCompareType(CompareTypeEnum.getCompareTypeCode(compareTypeName));
            alarmConfigRequest.setThreshold(Double.valueOf(threshold));
            alarmConfigRequest.setOutputMetaId(templateOutputMeta.getId());
            // Rule Metric.
            String ruleMetricEnCode = excelMultiTemplateRule.getRuleMetricEnCode();
            if (StringUtils.isNotBlank(ruleMetricEnCode)) {
                // xx_xx_xx_encode, index is 3.
                RuleMetric ruleMetricInDb = ruleMetricDao.findByEnCode(ruleMetricEnCode);
                if (ruleMetricInDb == null) {
                    throw new UnExpectedRequestException("Rule metric[Code=" + ruleMetricEnCode + "] " + "{&DOES_NOT_EXIST}");
                }
                String code = ruleMetricInDb.getEnCode();
                alarmConfigRequest.setRuleMetricEnCode(code);
            }
            alarmConfigRequest.setUploadRuleMetricValue(excelMultiTemplateRule.getUploadRuleMetricValue());
            alarmConfigRequest.setUploadAbnormalValue(excelMultiTemplateRule.getUploadAbnormalValue());
            alarmConfigRequests.add(alarmConfigRequest);
        }
    }

    private TemplateOutputMeta findTemplateOutputMetaByTemplateAndOutputName(Template template, String templateOutputName) {
        List<TemplateOutputMeta> templateOutputMetas = templateOutputMetaDao.findByRuleTemplate(template);
        for (TemplateOutputMeta templateOutputMeta: templateOutputMetas) {
            if (templateOutputMeta.getOutputName().equals(templateOutputName)) {
                return templateOutputMeta;
            }
        }
        return null;
    }

    private List<AddCustomRuleRequest> constructAddCustomRuleRequest(Map<String, List<ExcelCustomRuleByProject>> customRulePartitionedByRuleName
        , Project project, String localeStr) throws UnExpectedRequestException {

        List<AddCustomRuleRequest> addCustomRuleRequests = new ArrayList<>();
        for (String ruleName : customRulePartitionedByRuleName.keySet()) {
            List<ExcelCustomRuleByProject> ruleInfos = customRulePartitionedByRuleName.get(ruleName);
            AddCustomRuleRequest addCustomRuleRequest = new AddCustomRuleRequest();
            ExcelCustomRuleByProject firstCommonInfo = ruleInfos.get(0);
            boolean alarm = false;
            Long projectId = project.getId();

            addCustomRuleRequest.setSpecifyStaticStartupParam(firstCommonInfo.getSpecifyStaticStartupParam());
            addCustomRuleRequest.setDeleteFailCheckResult(firstCommonInfo.getDeleteFailCheckResult());
            addCustomRuleRequest.setStaticStartupParam(firstCommonInfo.getStaticStartupParam());
            addCustomRuleRequest.setAbortOnFailure(firstCommonInfo.getAbortOnFailure());
            addCustomRuleRequest.setProxyUser(firstCommonInfo.getProxyUser());
            String ruleGroupName = firstCommonInfo.getRuleGroupName();
            String clusterName = firstCommonInfo.getClusterName();
            String outputName = firstCommonInfo.getOutputName();
            String fromContent = null;
            String whereContent = null;
            Integer functionType = null;
            String functionContent = null;
            Boolean saveMidTable = firstCommonInfo.getSaveMidTable();

            if (firstCommonInfo.getLinkisDataSourceId() != null) {
                addCustomRuleRequest.setLinkisDataSourceId(Long.parseLong(firstCommonInfo.getLinkisDataSourceId()));
                addCustomRuleRequest.setLinkisDataSourceName(firstCommonInfo.getLinkisDataSourceName());
                addCustomRuleRequest.setLinkisDataSourceType(firstCommonInfo.getLinkisDataSourceType());
            }
            if (StringUtils.isNotBlank(firstCommonInfo.getFunctionName()) && StringUtils.isNotBlank(firstCommonInfo.getFunctionContent())
                && StringUtils.isNotBlank(firstCommonInfo.getFromContent()) && StringUtils.isNotBlank(firstCommonInfo.getWhereContent())) {
                functionType = FunctionTypeEnum.getFunctionTypeByName(firstCommonInfo.getFunctionName());
                functionContent = firstCommonInfo.getFunctionContent();
                whereContent = firstCommonInfo.getWhereContent();
                fromContent = firstCommonInfo.getFromContent();
            } else {
                addCustomRuleRequest.setSqlCheckArea(firstCommonInfo.getSqlCheckArea());
            }
            List<CustomAlarmConfigRequest> alarmConfigRequests = new ArrayList<>();
            for (ExcelCustomRuleByProject excelCustomRule : ruleInfos) {
                getCustomAlarmConfig(alarmConfigRequests, excelCustomRule, localeStr);
            }
            if (StringUtils.isBlank(ruleGroupName)) {
                throw new UnExpectedRequestException("RuleGroupName {&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            RuleGroup ruleGroupInDb = ruleGroupDao.findByRuleGroupNameAndProjectId(ruleGroupName, projectId);
            if (ruleGroupInDb != null) {
                addCustomRuleRequest.setRuleGroupId(ruleGroupInDb.getId());
            } else {
                RuleGroup ruleGroup = ruleGroupDao.saveRuleGroup(new RuleGroup(ruleGroupName, project.getId()));
                addCustomRuleRequest.setRuleGroupId(ruleGroup.getId());
            }
            addCustomRuleRequest.setRuleName(ruleName);
            addCustomRuleRequest.setRuleCnName(firstCommonInfo.getRuleCnName());
            addCustomRuleRequest.setRuleDetail(firstCommonInfo.getRuleDetail());
            if (! alarmConfigRequests.isEmpty()) {
                alarm = true;
            }
            addCustomRuleRequest.setAlarm(alarm);
            addCustomRuleRequest.setProjectId(projectId);
            addCustomRuleRequest.setOutputName(outputName);
            addCustomRuleRequest.setFunctionType(functionType);
            addCustomRuleRequest.setSaveMidTable(saveMidTable);
            addCustomRuleRequest.setFunctionContent(functionContent);
            addCustomRuleRequest.setAlarmVariable(alarmConfigRequests);
            addCustomRuleRequest.setWhereContent(whereContent);
            addCustomRuleRequest.setFromContent(fromContent);
            addCustomRuleRequest.setClusterName(clusterName);

            addCustomRuleRequests.add(addCustomRuleRequest);
        }
        return addCustomRuleRequests;
    }

    private List<AddRuleRequest> constructAddRuleRequest(Map<String, List<ExcelTemplateRuleByProject>> partitionedByRuleName, Project project, String localeStr) throws UnExpectedRequestException {
        List<AddRuleRequest> addRuleRequests = new ArrayList<>();
        for (String ruleName : partitionedByRuleName.keySet()) {
            List<ExcelTemplateRuleByProject> ruleInfos = partitionedByRuleName.get(ruleName);
            ExcelTemplateRuleByProject firstCommonInfo = ruleInfos.get(0);
            AddRuleRequest addRuleRequest = new AddRuleRequest();
            String ruleDetail = firstCommonInfo.getRuleDetail();
            String ruleCnName = firstCommonInfo.getRuleCnName();
            String ruleGroupName = firstCommonInfo.getRuleGroupName();
            String ruleTemplateName = firstCommonInfo.getTemplateName();
            addRuleRequest.setRuleCnName(ruleCnName);
            addRuleRequest.setAbortOnFailure(firstCommonInfo.getAbortOnFailure());
            addRuleRequest.setStaticStartupParam(firstCommonInfo.getStaticStartupParam());
            addRuleRequest.setDeleteFailCheckResult(firstCommonInfo.getDeleteFailCheckResult());
            addRuleRequest.setSpecifyStaticStartupParam(firstCommonInfo.getSpecifyStaticStartupParam());

            if (StringUtils.isBlank(ruleGroupName)) {
                throw new UnExpectedRequestException("RuleGroupName {&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            Template template = findTemplateByName(ruleTemplateName);
            if (template == null) {
                throw new UnExpectedRequestException("{&TEMPLATE_NAME}: [" + ruleTemplateName + "] {&DOES_NOT_EXIST}");
            }

            boolean alarm = false;
            Long projectId = project.getId();
            List<DataSourceRequest> dataSourceRequests = new ArrayList<>();
            List<AlarmConfigRequest> alarmConfigRequests = new ArrayList<>();

            List<TemplateArgumentRequest> templateArgumentRequests = new ArrayList<>();

            for (ExcelTemplateRuleByProject excelTemplateRuleByProject : ruleInfos) {
                getDataSourceRequest(dataSourceRequests, excelTemplateRuleByProject);
                getTemplateArgument(templateArgumentRequests, excelTemplateRuleByProject, template);
                getAlarmConfig(alarmConfigRequests, excelTemplateRuleByProject, template, localeStr);
            }

            RuleGroup ruleGroupInDb = ruleGroupDao.findByRuleGroupNameAndProjectId(ruleGroupName, projectId);
            if (ruleGroupInDb != null) {
                addRuleRequest.setRuleGroupId(ruleGroupInDb.getId());
            } else {
                RuleGroup ruleGroup = ruleGroupDao.saveRuleGroup(new RuleGroup(ruleGroupName, project.getId()));
                addRuleRequest.setRuleGroupId(ruleGroup.getId());
            }

            // Construct addRuleRequest
            addRuleRequest.setRuleName(ruleName);
            addRuleRequest.setRuleDetail(ruleDetail);
            addRuleRequest.setRuleTemplateId(template.getId());
            if (! alarmConfigRequests.isEmpty()) {
                alarm = true;
            }
            addRuleRequest.setAlarmVariable(alarmConfigRequests);
            addRuleRequest.setDatasource(dataSourceRequests);
            addRuleRequest.setProjectId(projectId);
            addRuleRequest.setAlarm(alarm);

            addRuleRequest.setTemplateArgumentRequests(templateArgumentRequests);
            addRuleRequests.add(addRuleRequest);
        }

        return addRuleRequests;
    }

    private void getCustomAlarmConfig(List<CustomAlarmConfigRequest> customAlarmConfigRequests, ExcelCustomRuleByProject excelCustomRule, String localeStr)
        throws UnExpectedRequestException {
        String templateOutputName = excelCustomRule.getAlarmCheckName();
        if (! StringUtils.isBlank(templateOutputName)) {
            String checkTemplateName = excelCustomRule.getCheckTemplateName();
            String compareTypeName = excelCustomRule.getCompareType();
            String threshold = excelCustomRule.getThreshold();

            CustomAlarmConfigRequest customAlarmConfigRequest = new CustomAlarmConfigRequest();
            customAlarmConfigRequest.setCheckTemplate(CheckTemplateEnum.getCheckTemplateCode(checkTemplateName, localeStr));
            customAlarmConfigRequest.setCompareType(CompareTypeEnum.getCompareTypeCode(compareTypeName));
            customAlarmConfigRequest.setThreshold(Double.valueOf(threshold));
            // Rule Metric.
            String ruleMetricEnCode = excelCustomRule.getRuleMetricEnCode();
            if (StringUtils.isNotBlank(ruleMetricEnCode)) {
                // xx_xx_xx_encode, index is 3.
                RuleMetric ruleMetricInDb = ruleMetricDao.findByEnCode(ruleMetricEnCode);
                if (ruleMetricInDb == null) {
                    throw new UnExpectedRequestException("Rule metric[Code=" + ruleMetricEnCode + "] " + "{&DOES_NOT_EXIST}");
                }
                String code = ruleMetricInDb.getEnCode();
                customAlarmConfigRequest.setRuleMetricEnCode(code);
            }
            customAlarmConfigRequest.setUploadRuleMetricValue(excelCustomRule.getUploadRuleMetricValue());
            customAlarmConfigRequest.setUploadAbnormalValue(excelCustomRule.getUploadAbnormalValue());
            customAlarmConfigRequests.add(customAlarmConfigRequest);
        }
    }

    private void getAlarmConfig(List<AlarmConfigRequest> alarmConfigRequests, ExcelTemplateRuleByProject excelTemplateRule, Template template, String localeStr) throws UnExpectedRequestException {
        String templateOutputName = excelTemplateRule.getAlarmCheckName();
        if (! StringUtils.isBlank(templateOutputName)) {
            String checkTemplateName = excelTemplateRule.getCheckTemplateName();
            String compareTypeName = excelTemplateRule.getCompareType();
            String threshold = excelTemplateRule.getThreshold();
            TemplateOutputMeta templateOutputMeta = findTemplateOutputMetaByTemplateAndOutputName(template, templateOutputName);

            if (templateOutputMeta == null) {
                throw new UnExpectedRequestException("{&TEMPLATE_OUTPUT_NAME} {&DOES_NOT_EXIST}");
            }
            AlarmConfigRequest alarmConfigRequest = new AlarmConfigRequest();
            alarmConfigRequest.setCheckTemplate(CheckTemplateEnum.getCheckTemplateCode(checkTemplateName, localeStr));
            alarmConfigRequest.setCompareType(CompareTypeEnum.getCompareTypeCode(compareTypeName));
            alarmConfigRequest.setThreshold(Double.valueOf(threshold));
            alarmConfigRequest.setOutputMetaId(templateOutputMeta.getId());
            // Rule Metric.
            String ruleMetricEnCode = excelTemplateRule.getRuleMetricEnCode();
            if (StringUtils.isNotBlank(ruleMetricEnCode)) {
                // xx_xx_xx_encode, index is 3.
                RuleMetric ruleMetricInDb = ruleMetricDao.findByEnCode(ruleMetricEnCode);
                if (ruleMetricInDb == null) {
                    throw new UnExpectedRequestException("Rule metric[Code=" + ruleMetricEnCode + "] " + "{&DOES_NOT_EXIST}");
                }
                String code = ruleMetricInDb.getEnCode();
                alarmConfigRequest.setRuleMetricEnCode(code);
            }
            alarmConfigRequest.setUploadRuleMetricValue(excelTemplateRule.getUploadRuleMetricValue());
            alarmConfigRequest.setUploadAbnormalValue(excelTemplateRule.getUploadAbnormalValue());
            alarmConfigRequests.add(alarmConfigRequest);
        }
    }

    private void getTemplateArgument(List<TemplateArgumentRequest> templateArgumentRequests, ExcelTemplateRuleByProject excelTemplateRule, Template template) throws UnExpectedRequestException {
        String argumentKey = excelTemplateRule.getArgumentKey();
        if (! StringUtils.isBlank(argumentKey)) {
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

    private void getDataSourceRequest(List<DataSourceRequest> dataSourceRequests, ExcelTemplateRuleByProject excelTemplateRule) {
        String clusterName = excelTemplateRule.getCluster();
        if (! StringUtils.isBlank(clusterName)) {
            DataSourceRequest dataSourceRequest = new DataSourceRequest();

            if (excelTemplateRule.getLinkisDataSourceId() != null) {
                dataSourceRequest.setLinkisDataSourceId(Long.parseLong(excelTemplateRule.getLinkisDataSourceId()));
                dataSourceRequest.setLinkisDataSourceName(excelTemplateRule.getLinkisDataSourceName());
                dataSourceRequest.setLinkisDataSourceType(excelTemplateRule.getLinkisDataSourceType());
            }

            dataSourceRequest.setClusterName(excelTemplateRule.getCluster());
            dataSourceRequest.setProxyUser(excelTemplateRule.getProxyUser());
            dataSourceRequest.setTableName(excelTemplateRule.getTableName());
            dataSourceRequest.setDbName(excelTemplateRule.getDbName());
            dataSourceRequest.setFilter(excelTemplateRule.getFilter());
            String colNamesOrigin = excelTemplateRule.getColumnNames();
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
            // Default all column will be white list field to be imported.
            dataSourceRequest.setBlackList(false);
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
                sheet.setClazz(ExcelTemplateRuleByProject.class);
                sheet.setHeadLineMun(1);
                excelReader.read(sheet);
            } else if (sheet.getSheetName().equals(ExcelSheetName.CUSTOM_RULE_NAME)) {
                sheet.setClazz(ExcelCustomRuleByProject.class);
                sheet.setHeadLineMun(1);
                excelReader.read(sheet);
            } else if (sheet.getSheetName().equals(ExcelSheetName.MULTI_TEMPLATE_RULE_NAME)) {
                sheet.setClazz(ExcelMultiTemplateRuleByProject.class);
                sheet.setHeadLineMun(1);
                excelReader.read(sheet);
            } else if (sheet.getSheetName().equals(ExcelSheetName.TEMPLATE_FILE_RULE_NAME)) {
                sheet.setClazz(ExcelTemplateFileRuleByProject.class);
                sheet.setHeadLineMun(1);
                excelReader.read(sheet);
            }
        }

        return listener;
    }

    private GeneralResponse<?> downloadRulesReal(List<Rule> rules, HttpServletResponse response, String loginUser) throws IOException, WriteExcelException {
        String fileName = "batch_rules_export_" + FILE_DATE_FORMATTER.format(new Date()) + SUPPORT_EXCEL_SUFFIX_NAME;
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setContentType("application/octet-stream");

        response.addHeader("Content-Disposition", "attachment;filename*=UTF-8''" + fileName);
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        String localeStr = httpServletRequest.getHeader("Content-Language");

        // Write to response.getOutputStream
        OutputStream outputStream = response.getOutputStream();
        List<ExcelCustomRuleByProject> customRules = getCustomRule(rules, localeStr);
        List<ExcelTemplateRuleByProject> templateRules = getTemplateRule(rules, localeStr);
        List<ExcelTemplateFileRuleByProject> templateFileRules = getFileRule(rules, localeStr);
        List<ExcelMultiTemplateRuleByProject> multiTemplateRules = getMultiTemplateRule(rules, localeStr);

        writeExcelToOutput(templateRules, customRules, multiTemplateRules, templateFileRules, outputStream);
        outputStream.flush();
        LOGGER.info("Succeed to download all rules in type of excel");
        // Record project event.
//        projectEventService.record(rules.iterator().next().getProject().getId(), loginUser, "download", "rules[" + Arrays.toString(rules.stream().map(Rule::getName).toArray()) + "].", EventTypeEnum.MODIFY_PROJECT.getCode());
        return null;
    }

    @Override
    public List<ExcelTemplateFileRuleByProject> getFileRule(Iterable<Rule> rules, String localeStr) {
        List<ExcelTemplateFileRuleByProject> lines = new ArrayList<>();
        for (Rule rule : rules) {
            if (! rule.getRuleType().equals(RuleTypeEnum.FILE_TEMPLATE_RULE.getCode())) {
                continue;
            }
            String ruleName = rule.getName();
            String ruleCnName = rule.getCnName();
            String ruleDetail = rule.getDetail();
            String templateName = rule.getTemplate().getName();
            ExcelTemplateFileRuleByProject ruleLinePrefix = new ExcelTemplateFileRuleByProject();
            ruleLinePrefix.setRuleName(ruleName);
            ruleLinePrefix.setRuleCnName(ruleCnName);
            ruleLinePrefix.setRuleDetail(ruleDetail);
            ruleLinePrefix.setTemplateName(templateName);
            ruleLinePrefix.setProjectName(rule.getProject().getName());
            ruleLinePrefix.setAbortOnFailure(rule.getAbortOnFailure());
            ruleLinePrefix.setRuleGroupName(rule.getRuleGroup().getRuleGroupName());

            ruleLinePrefix.setCreateUser(rule.getCreateUser());
            ruleLinePrefix.setCreateTime(rule.getCreateTime());
            ruleLinePrefix.setModifyUser(rule.getModifyUser());
            ruleLinePrefix.setModifyTime(rule.getModifyTime());
            ruleLinePrefix.setDeleteFailCheckResult(rule.getDeleteFailCheckResult());
            lines.add(ruleLinePrefix);
            for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
                String clusterName = ruleDataSource.getClusterName();
                String databaseName = ruleDataSource.getDbName();
                String tableName = ruleDataSource.getTableName();
                String filter = ruleDataSource.getFilter();

                ExcelTemplateFileRuleByProject tmp = new ExcelTemplateFileRuleByProject(rule.getName());
                tmp.setClusterName(clusterName);
                tmp.setDatabaseName(databaseName);
                tmp.setTableName(tableName);
                tmp.setFilter(filter);
                LOGGER.info("Collect excel line: {}", tmp);
                lines.add(tmp);
            }
            for (AlarmConfig alarmConfig : rule.getAlarmConfigs()) {
                Double threshold = alarmConfig.getThreshold();
                String alarmCompareType = CompareTypeEnum.getCompareTypeName(alarmConfig.getCompareType());
                String alarmOutputName = FileOutputNameEnum.getFileOutputName(alarmConfig.getFileOutputName(), localeStr);
                String checkTemplateName = CheckTemplateEnum.getCheckTemplateName(alarmConfig.getCheckTemplate(), localeStr);
                String unit = alarmConfig.getFileOutputUnit() == null ? "" : FileOutputUnitEnum.fileOutputUnit(alarmConfig.getFileOutputUnit());
                ExcelTemplateFileRuleByProject tmp = new ExcelTemplateFileRuleByProject(rule.getName());
                tmp.setUnit(unit);
                tmp.setCompareType(alarmCompareType);
                tmp.setAlarmCheckName(alarmOutputName);
                tmp.setCheckTemplateName(checkTemplateName);
                tmp.setThreshold(String.valueOf(threshold));
                RuleMetric ruleMetric = alarmConfig.getRuleMetric();
                // Recod rule metric info (unique code).
                if (ruleMetric != null) {
                    String enCode = ruleMetric.getEnCode();

                    tmp.setRuleMetricEnCode(enCode);
                    tmp.setRuleMetricName(ruleMetric.getName());
                }
                LOGGER.info("Collect excel line: {}", tmp);
                lines.add(tmp);
            }
        }

        return lines;
    }

    private void writeExcelToOutput(List<ExcelTemplateRuleByProject> templateRules, List<ExcelCustomRuleByProject> customRules,
                                    List<ExcelMultiTemplateRuleByProject> multiTemplateRules, List<ExcelTemplateFileRuleByProject> templateFileRules, OutputStream outputStream) throws WriteExcelException, IOException {
        try {
            LOGGER.info("Start to write excel");
            ExcelWriter writer = new ExcelWriter(outputStream, ExcelTypeEnum.XLSX, true);
            Sheet templateSheet = new Sheet(1, 0, ExcelTemplateRuleByProject.class);
            templateSheet.setSheetName(ExcelSheetName.TEMPLATE_RULE_NAME);
            writer.write(templateRules, templateSheet);

            Sheet customSheet = new Sheet(2, 0, ExcelCustomRuleByProject.class);
            customSheet.setSheetName(ExcelSheetName.CUSTOM_RULE_NAME);
            writer.write(customRules, customSheet);

            Sheet multiTemplateSheet = new Sheet(3, 0, ExcelMultiTemplateRuleByProject.class);
            multiTemplateSheet.setSheetName(ExcelSheetName.MULTI_TEMPLATE_RULE_NAME);
            writer.write(multiTemplateRules, multiTemplateSheet);

            Sheet templateFileSheet = new Sheet(4, 0, ExcelTemplateFileRuleByProject.class);
            templateFileSheet.setSheetName(ExcelSheetName.TEMPLATE_FILE_RULE_NAME);
            writer.write(templateFileRules, templateFileSheet);

            writer.finish();
            LOGGER.info("Finish to write excel");
        } catch (Exception e) {
            throw new WriteExcelException(e.getMessage(), 500);
        } finally {
            outputStream.close();
        }
    }

    @Override
    public List<ExcelCustomRuleByProject> getCustomRule(Iterable<Rule> rules, String localeStr) {
        List<ExcelCustomRuleByProject> lines = new ArrayList<>();
        for (Rule rule : rules) {
            if (! rule.getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
                continue;
            }
            ExcelCustomRuleByProject ruleLinePrefix = new ExcelCustomRuleByProject();
            basicInfoToExcel(ruleLinePrefix, rule);

            lines.add(ruleLinePrefix);

            for (AlarmConfig alarmConfig : rule.getAlarmConfigs()) {
                Double threshold = alarmConfig.getThreshold();
                String alarmOutputName = alarmConfig.getTemplateOutputMeta().getOutputName();
                String alarmCompareType = CompareTypeEnum.getCompareTypeName(alarmConfig.getCompareType());
                String checkTemplateName = CheckTemplateEnum.getCheckTemplateName(alarmConfig.getCheckTemplate(), localeStr);

                ExcelCustomRuleByProject tmp = new ExcelCustomRuleByProject(rule.getName());
                tmp.setCompareType(alarmCompareType);
                tmp.setAlarmCheckName(alarmOutputName);
                tmp.setThreshold(String.valueOf(threshold));
                tmp.setCheckTemplateName(checkTemplateName);
                RuleMetric ruleMetric = alarmConfig.getRuleMetric();
                // Recod rule metric info (unique code).
                if (ruleMetric != null) {
                    String enCode = ruleMetric.getEnCode();

                    tmp.setRuleMetricEnCode(enCode);
                    tmp.setRuleMetricName(ruleMetric.getName());
                }
                tmp.setUploadRuleMetricValue(alarmConfig.getUploadRuleMetricValue());
                tmp.setUploadAbnormalValue(alarmConfig.getUploadAbnormalValue());
                lines.add(tmp);
            }
        }

        return lines;
    }

    private void basicInfoToExcel(ExcelCustomRuleByProject ruleLinePrefix, Rule rule) {
        String ruleName = rule.getName();
        String ruleDetail = rule.getDetail();
        String ruleCnName = rule.getCnName();
        String outputName = rule.getOutputName();
        Boolean saveMidTable = rule.getTemplate().getSaveMidTable();
        RuleDataSource ruleDataSource = rule.getRuleDataSources().iterator().next();
        ruleLinePrefix.setProxyUser(ruleDataSource.getProxyUser());
        String clusterName = ruleDataSource.getClusterName();

        // Original custom rule, not sql check type.
        if (rule.getFunctionType() != null && StringUtils.isNotBlank(rule.getFunctionContent())
            && StringUtils.isNotBlank(rule.getFromContent()) && StringUtils.isNotBlank(rule.getWhereContent())) {

            String functionName = FunctionTypeEnum.getFunctionByCode(rule.getFunctionType());
            String functionContent = rule.getFunctionContent();
            String whereContent = rule.getWhereContent();
            String fromContent = rule.getFromContent();

            ruleLinePrefix.setFunctionContent(functionContent);
            ruleLinePrefix.setWhereContent(whereContent);
            ruleLinePrefix.setFunctionName(functionName);
            ruleLinePrefix.setFromContent(fromContent);
        } else {
            ruleLinePrefix.setSqlCheckArea(rule.getTemplate().getMidTableAction());
        }

        if (ruleDataSource.getLinkisDataSourceId() != null) {
            String linkisDataSourceName = ruleDataSource.getLinkisDataSourceName();
            String linkisDataSourceId = String.valueOf(ruleDataSource.getLinkisDataSourceId());
            String linkisDataSourceType = TemplateDataSourceTypeEnum.getMessage(ruleDataSource.getDatasourceType());
            ruleLinePrefix.setLinkisDataSourceName(linkisDataSourceName);
            ruleLinePrefix.setLinkisDataSourceType(linkisDataSourceType);
            ruleLinePrefix.setLinkisDataSourceId(linkisDataSourceId);
        }

        ruleLinePrefix.setRuleGroupName(rule.getRuleGroup().getRuleGroupName());
        ruleLinePrefix.setProjectName(rule.getProject().getName());
        ruleLinePrefix.setAbortOnFailure(rule.getAbortOnFailure());
        ruleLinePrefix.setRuleDetail(ruleDetail);
        ruleLinePrefix.setRuleCnName(ruleCnName);
        ruleLinePrefix.setRuleName(ruleName);
        ruleLinePrefix.setOutputName(outputName);
        ruleLinePrefix.setClusterName(clusterName);
        ruleLinePrefix.setSaveMidTable(saveMidTable);
        ruleLinePrefix.setCreateUser(rule.getCreateUser());
        ruleLinePrefix.setCreateTime(rule.getCreateTime());
        ruleLinePrefix.setModifyUser(rule.getModifyUser());
        ruleLinePrefix.setModifyTime(rule.getModifyTime());
        ruleLinePrefix.setDeleteFailCheckResult(rule.getDeleteFailCheckResult());
        ruleLinePrefix.setSpecifyStaticStartupParam(rule.getSpecifyStaticStartupParam());
        if (rule.getSpecifyStaticStartupParam() != null && rule.getSpecifyStaticStartupParam()) {
            ruleLinePrefix.setStaticStartupParam(rule.getStaticStartupParam());
        }
    }

    @Override
    public List<ExcelMultiTemplateRuleByProject> getMultiTemplateRule(Iterable<Rule> rules, String localeStr) {
        List<ExcelMultiTemplateRuleByProject> lines = new ArrayList<>();
        for (Rule rule : rules) {
            if (! rule.getRuleType().equals(RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode())) {
                continue;
            }
            ExcelMultiTemplateRuleByProject ruleLinePrefix = new ExcelMultiTemplateRuleByProject();
            // Rule basic info.
            String clusterName = rule.getRuleDataSources().iterator().next().getClusterName();
            basicInfoToExcel(ruleLinePrefix, rule, clusterName);

            List<RuleVariable> filterRuleVariable = rule.getRuleVariables().stream().filter(ruleVariable ->
                    ruleVariable.getTemplateMidTableInputMeta().getInputType().equals(TemplateInputTypeEnum.CONDITION.getCode())).collect(Collectors.toList());
            if (filterRuleVariable != null && filterRuleVariable.size() != 0) {
                ruleLinePrefix.setWhereFilter(filterRuleVariable.iterator().next().getValue());
            }
            Boolean sourceAddedFlag = false;
            Boolean targetAddedFlag = false;
            lines.add(ruleLinePrefix);
            ExcelMultiTemplateRuleByProject datasourceExcelRule = new ExcelMultiTemplateRuleByProject(rule.getName());
            for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
                String databaseName = ruleDataSource.getDbName();
                String tableName = ruleDataSource.getTableName();

                String filter = ruleDataSource.getFilter();
                Integer datasourceIndex = ruleDataSource.getDatasourceIndex();
                if (datasourceIndex == 0) {
                    datasourceExcelRule.setLeftDbName(databaseName);
                    datasourceExcelRule.setLeftTableName(tableName);
                    datasourceExcelRule.setLeftFilter(filter);
                    datasourceExcelRule.setLeftProxyUser(ruleDataSource.getProxyUser());
                    if (ruleDataSource.getLinkisDataSourceId() != null) {
                        datasourceExcelRule.setLeftLinkisDataSourceType(TemplateDataSourceTypeEnum.getMessage(ruleDataSource.getDatasourceType()));
                        datasourceExcelRule.setLeftLinkisDataSourceId(String.valueOf(ruleDataSource.getLinkisDataSourceId()));
                        datasourceExcelRule.setLeftLinkisDataSourceName(ruleDataSource.getLinkisDataSourceName());
                    }
                    sourceAddedFlag = true;
                } else {
                    datasourceExcelRule.setRightDbName(databaseName);
                    datasourceExcelRule.setRightTableName(tableName);
                    datasourceExcelRule.setRightFilter(filter);
                    datasourceExcelRule.setRightProxyUser(ruleDataSource.getProxyUser());
                    if (ruleDataSource.getLinkisDataSourceId() != null) {
                        datasourceExcelRule.setRightLlinkisDataSourceType(TemplateDataSourceTypeEnum.getMessage(ruleDataSource.getDatasourceType()));
                        datasourceExcelRule.setRightLinkisDataSourceId(String.valueOf(ruleDataSource.getLinkisDataSourceId()));
                        datasourceExcelRule.setRightLlinkisDataSourceName(ruleDataSource.getLinkisDataSourceName());
                    }
                    targetAddedFlag = true;
                }

                if (sourceAddedFlag && targetAddedFlag) {
                    LOGGER.info("Collect excel line: {}", datasourceExcelRule);
                    lines.add(datasourceExcelRule);
                }
            }
            // Get mapping and alarm info to excel.
            getMappingInfoAndAlarm(rule.getRuleDataSourceMappings(), rule.getAlarmConfigs(), lines, ruleLinePrefix, localeStr);
        }
        return lines;
    }

    private void getMappingInfoAndAlarm(Set<RuleDataSourceMapping> ruleDataSourceMappings, Set<AlarmConfig> alarmConfigs, List<ExcelMultiTemplateRuleByProject> lines
        , ExcelMultiTemplateRuleByProject ruleLinePrefix, String localeStr) {

        for (RuleDataSourceMapping mapping : ruleDataSourceMappings) {
            ExcelMultiTemplateRuleByProject tmp = new ExcelMultiTemplateRuleByProject(ruleLinePrefix.getRuleName());
            tmp.setLeftMappingStatement(mapping.getLeftStatement());
            tmp.setLeftMappingNames(mapping.getLeftColumnNames());
            tmp.setLeftMappingTypes(mapping.getLeftColumnTypes());
            tmp.setMappingOperation(MappingOperationEnum.getByCode(mapping.getOperation()).getSymbol());
            tmp.setRightMappingStatement(mapping.getRightStatement());
            tmp.setRightMappingNames(mapping.getRightColumnNames());
            tmp.setRightMappingTypes(mapping.getRightColumnTypes());
            LOGGER.info("Collect excel line: {}", tmp);
            lines.add(tmp);
        }

        for (AlarmConfig alarmConfig : alarmConfigs) {
            Double threshold = alarmConfig.getThreshold();
            String alarmOutputName = alarmConfig.getTemplateOutputMeta().getOutputName();
            String alarmCompareType = CompareTypeEnum.getCompareTypeName(alarmConfig.getCompareType());
            String checkTemplateName = CheckTemplateEnum.getCheckTemplateName(alarmConfig.getCheckTemplate(), localeStr);
            ExcelMultiTemplateRuleByProject tmp = new ExcelMultiTemplateRuleByProject(ruleLinePrefix.getRuleName());
            tmp.setCompareType(alarmCompareType);
            tmp.setAlarmCheckName(alarmOutputName);
            tmp.setCheckTemplateName(checkTemplateName);
            tmp.setThreshold(String.valueOf(threshold));
            RuleMetric ruleMetric = alarmConfig.getRuleMetric();
            // Recod rule metric info (unique code).
            if (ruleMetric != null) {
                String enCode = ruleMetric.getEnCode();

                tmp.setRuleMetricEnCode(enCode);
                tmp.setRuleMetricName(ruleMetric.getName());
            }
            tmp.setUploadRuleMetricValue(alarmConfig.getUploadRuleMetricValue());
            tmp.setUploadAbnormalValue(alarmConfig.getUploadAbnormalValue());
            LOGGER.info("Collect excel line: {}", tmp);
            lines.add(tmp);
        }
    }

    private void basicInfoToExcel(ExcelMultiTemplateRuleByProject ruleLinePrefix, Rule rule, String clusterName) {
        String ruleName = rule.getName();
        String ruleDetail = rule.getDetail();
        String ruleCnName = rule.getCnName();
        String templateName = rule.getTemplate().getName();
        ruleLinePrefix.setAbortOnFailure(rule.getAbortOnFailure());
        ruleLinePrefix.setProjectName(rule.getProject().getName());
        ruleLinePrefix.setRuleGroupName(rule.getRuleGroup().getRuleGroupName());
        ruleLinePrefix.setTemplateName(templateName);
        ruleLinePrefix.setClusterName(clusterName);
        ruleLinePrefix.setRuleDetail(ruleDetail);
        ruleLinePrefix.setRuleCnName(ruleCnName);
        ruleLinePrefix.setRuleName(ruleName);
        ruleLinePrefix.setCreateUser(rule.getCreateUser());
        ruleLinePrefix.setCreateTime(rule.getCreateTime());
        ruleLinePrefix.setModifyUser(rule.getModifyUser());
        ruleLinePrefix.setModifyTime(rule.getModifyTime());
        ruleLinePrefix.setDeleteFailCheckResult(rule.getDeleteFailCheckResult());
        ruleLinePrefix.setSpecifyStaticStartupParam(rule.getSpecifyStaticStartupParam());
        if (rule.getSpecifyStaticStartupParam() != null && rule.getSpecifyStaticStartupParam()) {
            ruleLinePrefix.setStaticStartupParam(rule.getStaticStartupParam());
        }
    }

    private void basicInfoToExcel(ExcelTemplateRuleByProject ruleLinePrefix, Rule rule) {
        String ruleName = rule.getName();
        String ruleCnName = rule.getCnName();
        String ruleDetail = rule.getDetail();
        String templateName = rule.getTemplate().getName();

        ruleLinePrefix.setRuleName(ruleName);
        ruleLinePrefix.setRuleCnName(ruleCnName);
        ruleLinePrefix.setRuleDetail(ruleDetail);
        ruleLinePrefix.setTemplateName(templateName);
        ruleLinePrefix.setAbortOnFailure(rule.getAbortOnFailure());
        ruleLinePrefix.setProjectName(rule.getProject().getName());
        ruleLinePrefix.setRuleGroupName(rule.getRuleGroup().getRuleGroupName());

        ruleLinePrefix.setCreateUser(rule.getCreateUser());
        ruleLinePrefix.setCreateTime(rule.getCreateTime());
        ruleLinePrefix.setModifyUser(rule.getModifyUser());
        ruleLinePrefix.setModifyTime(rule.getModifyTime());
        ruleLinePrefix.setDeleteFailCheckResult(rule.getDeleteFailCheckResult());
        ruleLinePrefix.setSpecifyStaticStartupParam(rule.getSpecifyStaticStartupParam());
        if (rule.getSpecifyStaticStartupParam() != null && rule.getSpecifyStaticStartupParam()) {
            ruleLinePrefix.setStaticStartupParam(rule.getStaticStartupParam());
        }
    }

    @Override
    public List<ExcelTemplateRuleByProject> getTemplateRule(Iterable<Rule> rules, String localeStr) {
        List<ExcelTemplateRuleByProject> lines = new ArrayList<>();
        for (Rule rule : rules) {
            if (! rule.getRuleType().equals(RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode())) {
                continue;
            }
            // Rule basic info.
            ExcelTemplateRuleByProject ruleLinePrefix = new ExcelTemplateRuleByProject();
            basicInfoToExcel(ruleLinePrefix, rule);
            lines.add(ruleLinePrefix);

            for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
                String clusterName = ruleDataSource.getClusterName();
                String databaseName = ruleDataSource.getDbName();
                String tableName = ruleDataSource.getTableName();
                String columnName = ruleDataSource.getColName();
                String filter = ruleDataSource.getFilter();

                ExcelTemplateRuleByProject tmp = new ExcelTemplateRuleByProject(rule.getName());
                tmp.setBlackColName(ruleDataSource.getBlackColName());
                tmp.setProxyUser(ruleDataSource.getProxyUser());
                tmp.setFilter(filter);
                tmp.setCluster(clusterName);
                tmp.setDbName(databaseName);
                tmp.setTableName(tableName);
                tmp.setColumnNames(columnName);

                if (ruleDataSource.getLinkisDataSourceId() != null) {
                    tmp.setLinkisDataSourceName(ruleDataSource.getLinkisDataSourceName());
                    tmp.setLinkisDataSourceId(String.valueOf(ruleDataSource.getLinkisDataSourceId()));
                    tmp.setLinkisDataSourceType(TemplateDataSourceTypeEnum.getMessage(ruleDataSource.getDatasourceType()));
                }
                LOGGER.info("Collect excel line: {}", tmp);
                lines.add(tmp);
            }

            for (TemplateMidTableInputMeta templateMidTableInputMeta : rule.getTemplate().getTemplateMidTableInputMetas()) {
                if (TemplateMidTableUtil.shouldResponse(templateMidTableInputMeta)) {
                    for (RuleVariable ruleVariable : rule.getRuleVariables()) {
                        if (ruleVariable.getTemplateMidTableInputMeta().equals(templateMidTableInputMeta)) {
                            ExcelTemplateRuleByProject tmp = new ExcelTemplateRuleByProject(rule.getName());

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
            alarmConfigsToExcel(rule, lines, localeStr);
        }

        return lines;
    }

    private void alarmConfigsToExcel(Rule rule, List<ExcelTemplateRuleByProject> lines, String localeStr) {
        for (AlarmConfig alarmConfig : rule.getAlarmConfigs()) {
            Double threshold = alarmConfig.getThreshold();
            String alarmOutputName = alarmConfig.getTemplateOutputMeta().getOutputName();
            String alarmCompareType = CompareTypeEnum.getCompareTypeName(alarmConfig.getCompareType());
            String checkTemplateName = CheckTemplateEnum.getCheckTemplateName(alarmConfig.getCheckTemplate(), localeStr);

            ExcelTemplateRuleByProject tmp = new ExcelTemplateRuleByProject(rule.getName());
            tmp.setCheckTemplateName(checkTemplateName);
            tmp.setThreshold(String.valueOf(threshold));
            tmp.setAlarmCheckName(alarmOutputName);
            tmp.setCompareType(alarmCompareType);

            RuleMetric ruleMetric = alarmConfig.getRuleMetric();
            if (ruleMetric != null) {
                String enCode = ruleMetric.getEnCode();

                tmp.setRuleMetricEnCode(enCode);
                tmp.setRuleMetricName(ruleMetric.getName());
            }
            tmp.setUploadRuleMetricValue(alarmConfig.getUploadRuleMetricValue());
            tmp.setUploadAbnormalValue(alarmConfig.getUploadAbnormalValue());
            LOGGER.info("Collect excel line: {}", tmp);
            lines.add(tmp);
        }
    }

}
