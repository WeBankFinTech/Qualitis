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
import com.google.common.collect.Maps;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.DiffRequestTypeEnum;
import com.webank.wedatasphere.qualitis.project.constant.ExcelSheetName;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.excel.ExcelExecutionParametersByProject;
import com.webank.wedatasphere.qualitis.project.excel.ExcelGroupByProject;
import com.webank.wedatasphere.qualitis.project.excel.ExcelRuleByProject;
import com.webank.wedatasphere.qualitis.project.request.DiffVariableRequest;
import com.webank.wedatasphere.qualitis.project.service.ProjectBatchService;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTemplateTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TableDataTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.*;
import com.webank.wedatasphere.qualitis.rule.dao.repository.DiffVariableRepository;
import com.webank.wedatasphere.qualitis.rule.entity.*;
import com.webank.wedatasphere.qualitis.rule.excel.ExcelRuleListener;
import com.webank.wedatasphere.qualitis.rule.exception.WriteExcelException;
import com.webank.wedatasphere.qualitis.rule.request.DownloadRuleRequest;
import com.webank.wedatasphere.qualitis.rule.service.*;
import com.webank.wedatasphere.qualitis.scheduled.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.service.DataVisibilityService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

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
    private RuleUdfDao ruleUdfDao;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private RuleGroupDao ruleGroupDao;
    @Autowired
    private RuleTemplateDao templateDao;
    @Autowired
    private RuleMetricDao ruleMetricDao;
    @Autowired
    private AlarmConfigDao alarmConfigDao;
    @Autowired
    private RuleVariableDao ruleVariableDao;
    @Autowired
    private DataVisibilityDao dataVisibilityDao;
    @Autowired
    private ExecutionParametersDao executionParametersDao;
    @Autowired
    private StandardValueVersionDao standardValueVersionDao;
    @Autowired
    private RuleDataSourceMappingDao ruleDataSourceMappingDao;
    @Autowired
    private TemplateDataSourceTypeDao templateDataSourceTypeDao;
    @Autowired
    private TemplateMidTableInputMetaDao templateMidTableInputMetaDao;
    @Autowired
    private DiffVariableRepository diffVariableRepository;
    @Autowired
    private TemplateStatisticsInputMetaService templateStatisticsInputMetaService;
    @Autowired
    private TemplateCountFunctionDao templateCountFunctionDao;
    @Autowired
    private TemplateOutputMetaDao templateOutputMetaDao;
    @Autowired
    private TemplateUdfDao templateUdfDao;


    @Autowired
    private DataVisibilityService dataVisibilityService;
    @Autowired
    private ProjectBatchService projectBatchService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private RuleDataSourceService ruleDataSourceService;
    @Autowired
    private RuleDataSourceMappingService ruleDataSourceMappingService;
    @Autowired
    private RuleVariableService ruleVariableService;
    @Autowired
    private AlarmConfigService alarmConfigService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleBatchServiceImpl.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private HttpServletRequest httpServletRequest;

    public RuleBatchServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse downloadRules(DownloadRuleRequest request, HttpServletResponse response) throws UnExpectedRequestException, IOException
            , WriteExcelException, PermissionDeniedRequestException {
        // Check Arguments
        DownloadRuleRequest.checkRequest(request);
        String loginUser = HttpUtils.getUserName(httpServletRequest);

        Boolean projectAllRules = request.getProjectId() != null;

        List<Rule> ruleLists;
        if (projectAllRules) {
            LOGGER.info("Downloading all rules of project. Project ID: {}", request.getProjectId());
            Project project = projectDao.findById(request.getProjectId());

            if (project == null) {
                throw new UnExpectedRequestException("Project {&DOES_NOT_EXIST}");
            }

            ruleLists = ruleDao.findByProject(project);
        } else {
            LOGGER.info("Downloading some rules. Rule IDs: {}", request.getRuleIds());
            ruleLists = ruleDao.findByIds(request.getRuleIds());

            List<Long> ruleIds = ruleLists.stream().map(Rule::getId).distinct().collect(Collectors.toList());
            List<Long> notExistRules = request.getRuleIds().stream().filter(currId -> !ruleIds.contains(currId)).collect(Collectors.toList());

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

        return downloadRulesReal(ruleLists, response);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse uploadRules(InputStream fileInputStream, FormDataContentDisposition fileDisposition, Long projectId)
            throws UnExpectedRequestException, IOException, PermissionDeniedRequestException {
        String userName = HttpUtils.getUserName(httpServletRequest);

        if (projectId == null) {
            throw new UnExpectedRequestException("Project ID {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }

        return uploadRulesReal(fileInputStream, fileDisposition.getFileName(), userName, projectId);
    }

    private GeneralResponse uploadRulesReal(InputStream fileInputStream, String fileName, String userName, Long projectId) throws UnExpectedRequestException
            , IOException, PermissionDeniedRequestException {

        if (userName == null) {
            return new GeneralResponse<>("401", "{&PLEASE_LOGIN}", null);
        }

        // Check suffix name of file
        String suffixName = fileName.substring(fileName.lastIndexOf('.'));
        if (!suffixName.equals(QualitisConstants.SUPPORT_EXCEL_SUFFIX_NAME)) {
            throw new UnExpectedRequestException("{&DO_NOT_SUPPORT_SUFFIX_NAME}: [" + suffixName + "]. {&ONLY_SUPPORT} [" + QualitisConstants.SUPPORT_EXCEL_SUFFIX_NAME + "]", 422);
        }
        ExcelRuleListener excelRuleListener = readExcel(fileInputStream);
        if (excelRuleListener.getExcelRuleContent().isEmpty()) {
            throw new UnExpectedRequestException("{&FILE_CAN_NOT_BE_EMPTY_OR_FILE_CAN_NOT_BE_RECOGNIZED}", 422);
        }

        Project projectInDb = projectDao.findById(projectId);
        if (projectInDb == null) {
            throw new UnExpectedRequestException("Project {&DOES_NOT_EXIST}");
        }
        // Check permissions of project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        projectService.checkProjectPermission(projectInDb, userName, permissions);
        projectBatchService.handleTableGroup(excelRuleListener, projectInDb.getId(), null);
        projectBatchService.handleExecutionParameters(excelRuleListener.getExcelExecutionParametersContent(), userName, projectInDb.getId());

        getAndSaveRule(excelRuleListener.getExcelRuleContent(), projectInDb, null, userName, null);

        if (fileInputStream != null) {
            fileInputStream.close();
        }

        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&SUCCEED_TO_UPLOAD_FILE}", null);
    }

    @Override
    public void getAndSaveRule(List<ExcelRuleByProject> excelRuleContentList, Project projectInDb, List<String> newRuleNames, String userName, List<DiffVariableRequest> diffVariableRequestList) throws IOException, UnExpectedRequestException {
        List<DiffVariableRequest> ruleJsonDiffVariableRequestList = diffVariableRequestList.stream()
                .filter(diffVariableRequest -> DiffRequestTypeEnum.JSON_REPLACEMENT.getCode().equals(diffVariableRequest.getType()))
                .filter(diffVariableRequest -> diffVariableRequest.getName().startsWith(ExcelSheetName.RULE_NAME + "$"))
                .collect(Collectors.toList());

        for (ExcelRuleByProject excelRuleByProject : excelRuleContentList) {
            String jsonStr = excelRuleByProject.getRuleJsonObject();

            // Replace json value with diff variable request.
            String modifiedJsonStr = projectBatchService.replaceJsonValue(ruleJsonDiffVariableRequestList, jsonStr);

            Rule rule = objectMapper.readValue(modifiedJsonStr, Rule.class);
            rule.setId(Long.MAX_VALUE);
            Rule ruleInDb = ruleDao.findByProjectAndRuleName(projectInDb, rule.getName());
            LOGGER.info("{} start to handle rule {}", userName, rule.getName());
            String ruleGroupName = excelRuleByProject.getRuleGroupName();
            RuleGroup realRuleGroup;
            if (ruleInDb != null) {
                RuleGroup ruleGroupInDb = ruleInDb.getRuleGroup();

                ruleGroupInDb.setRuleGroupName(ruleGroupName);
                realRuleGroup = ruleGroupDao.saveRuleGroup(ruleGroupInDb);
            } else {
                RuleGroup ruleGroupInDb = ruleGroupDao.findByRuleGroupNameAndProjectId(ruleGroupName, projectInDb.getId());
                if (ruleGroupInDb != null) {
                    realRuleGroup = ruleGroupInDb;
                } else {
                    realRuleGroup = ruleGroupDao.saveRuleGroup(new RuleGroup(ruleGroupName, projectInDb.getId()));
                }
            }
            handleRule(rule, ruleInDb, modifiedJsonStr, excelRuleByProject.getRuleTemplateJsonObject(), excelRuleByProject.getRuleTemplateVisibilityObject(), projectInDb, realRuleGroup, newRuleNames, diffVariableRequestList);
            LOGGER.info("Finish to handle rule");
        }
    }

    @Override
    public void handleRule(Rule rule, Rule ruleInDb, String ruleJsonObject, String ruleTemplateJsonObject
            , String ruleTemplateVisibilityObject, Project projectInDb, RuleGroup realRuleGroup, List<String> newRuleNames
            , List<DiffVariableRequest> diffVariableRequestList) throws IOException, UnExpectedRequestException {

        Set<RuleDataSourceMapping> ruleDataSourceMappings = rule.getRuleDataSourceMappings();
        Set<RuleDataSource> ruleDataSources = rule.getRuleDataSources();
        Set<RuleVariable> ruleVariables = rule.getRuleVariables();
        Set<AlarmConfig> alarmConfigs = rule.getAlarmConfigs();
        Set<RuleUdf> ruleUdfs = rule.getRuleUdfs();

        Template template = objectMapper.readValue(ruleTemplateJsonObject, Template.class);

        Template templateInDb;
        if (!template.getTemplateType().equals(RuleTemplateTypeEnum.CUSTOM.getCode())) {
            //原代码templateInDb = templateDao.findByName(template.getName());  因为1.2.0版本存在相同模板name，所以该逻辑要改动
            Template templateByEnName = templateDao.findTemplateByEnName(template.getEnName());
            templateInDb = templateByEnName;
        } else {
            templateInDb = null;
        }
        templateInDb = chooseTemplate(rule, template, templateInDb, projectInDb, diffVariableRequestList, ruleTemplateVisibilityObject);

        if (ruleInDb != null) {
            rule.setId(ruleInDb.getId());
            rule.setTemplate(templateInDb);
            rule.setRuleGroup(realRuleGroup);
            rule.setProject(ruleInDb.getProject());
            rule.setCreateTime(ruleInDb.getCreateTime());
            rule.setModifyTime(QualitisConstants.PRINT_TIME_FORMAT.format(new Date()));

            // Clear
            ruleDataSourceMappingService.deleteByRule(ruleInDb);
            projectBatchService.clearDatasourceEnv(ruleInDb);
            ruleVariableService.deleteByRule(ruleInDb);
            alarmConfigService.deleteByRule(ruleInDb);
            ruleUdfDao.deleteByRule(ruleInDb);
        } else {
            rule.setId(null);
            rule.setProject(projectInDb);
            rule.setTemplate(templateInDb);
            rule.setRuleGroup(realRuleGroup);

            rule.setRuleDataSourceMappings(null);
            rule.setRuleDataSources(null);
            rule.setRuleVariables(null);
            rule.setAlarmConfigs(null);
            rule.setRuleUdfs(null);
        }
        if (StringUtils.isNotEmpty(rule.getStandardValueVersionEnName())) {
            StandardValueVersion standardValueVersion = standardValueVersionDao.findByEnName(rule.getStandardValueVersionEnName());
            if (standardValueVersion != null) {
                rule.setStandardValueVersionId(standardValueVersion.getId());
            } else {
                rule.setStandardValueVersionId(null);
            }
        }
        ruleInDb = ruleDao.saveRule(rule);
        projectBatchService.createDatasourceEnv(ruleDataSources, ruleInDb, diffVariableRequestList);
        createDatasourceMapping(ruleDataSourceMappings, ruleInDb);
        createRuleVariable(ruleVariables, ruleInDb);
        createAlarmConfig(alarmConfigs, ruleInDb);
        createRuleUdf(ruleUdfs, ruleInDb);

        Rule savedRule = ruleDao.saveRule(ruleInDb);
        if (newRuleNames != null) {
            newRuleNames.add(savedRule.getName());
        }
    }

    private Template chooseTemplate(Rule rule, Template template, Template templateInDb, Project projectInDb, List<DiffVariableRequest> diffVariableRequestList, String ruleTemplateVisibilityObject)
            throws IOException {
        if (templateInDb != null) {
            LOGGER.info("Template {} already exists.", templateInDb.getName());
        } else {
            LOGGER.info("Create template because this system which will be import have no template: {}.", template.getName());
            template.setId(null);
            if (template.getTemplateType().equals(RuleTemplateTypeEnum.CUSTOM.getCode())) {
                template.setName(projectInDb.getId() + "_" + rule.getName() + "_template");
            }
            Set<TemplateUdf> templateUdf = template.getTemplateUdf();
            Set<TemplateOutputMeta> templateOutputMetas = template.getTemplateOutputMetas();
            Set<TemplateDataSourceType> templateDataSourceTypes = template.getTemplateDataSourceType();
            Set<TemplateStatisticsInputMeta> templateStatisticsInputMetas = template.getStatisticAction();
            Set<TemplateMidTableInputMeta> templateMidTableInputMetas = template.getTemplateMidTableInputMetas();

            template.setTemplateUdf(null);
            template.setStatisticAction(null);
            template.setTemplateOutputMetas(null);
            template.setTemplateDataSourceType(null);
            template.setTemplateMidTableInputMetas(null);

            List<DiffVariableRequest> sqlDiffVariableRequestList = diffVariableRequestList.stream().filter(
                    diffVariableRequest -> DiffRequestTypeEnum.SQL_REPLACEMENT.getCode().equals(diffVariableRequest.getType())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(sqlDiffVariableRequestList)) {
                String midTableAction = template.getMidTableAction();
                for (DiffVariableRequest diffVariableRequest : sqlDiffVariableRequestList) {
                    midTableAction = midTableAction.replace(diffVariableRequest.getName(), diffVariableRequest.getValue());
                }
                template.setMidTableAction(midTableAction);
            }
            Template savedTemplate = templateDao.saveTemplate(template);
            LOGGER.info("Success to save template basic info: {}", savedTemplate.toString());

            if (CollectionUtils.isNotEmpty(templateUdf)) {
                List<TemplateUdf> templateUdfList = new ArrayList<>();
                templateUdfList.addAll(templateUdfDao.saveAll(templateUdf.stream().map(currTemplateUdf -> {
                    currTemplateUdf.setTemplate(savedTemplate);
                    currTemplateUdf.setId(null);
                    return currTemplateUdf;
                }).collect(Collectors.toList())));
                savedTemplate.setTemplateUdf(templateUdf);
            }

            if (CollectionUtils.isNotEmpty(templateOutputMetas)) {
                Set<TemplateOutputMeta> templateOutputMetaHashSet = new HashSet<>();
                templateOutputMetaHashSet.add(templateOutputMetaDao.saveTemplateOutputMeta(templateOutputMetas.stream().map(templateOutputMeta -> {
                    templateOutputMeta.setTemplate(savedTemplate);
                    templateOutputMeta.setId(null);
                    return templateOutputMeta;
                }).collect(Collectors.toList()).iterator().next()));
                savedTemplate.setTemplateOutputMetas(templateOutputMetaHashSet);
            }

            if (CollectionUtils.isNotEmpty(templateStatisticsInputMetas)) {
                Set<TemplateStatisticsInputMeta> templateStatisticsInputMetaSet = new HashSet<>();
                templateStatisticsInputMetaSet.addAll(templateStatisticsInputMetaService.saveAll(templateStatisticsInputMetas.stream().map(templateStatisticsInputMeta -> {
                    templateStatisticsInputMeta.setTemplate(savedTemplate);
                    templateStatisticsInputMeta.setId(null);
                    return templateStatisticsInputMeta;
                }).collect(Collectors.toList())));
                savedTemplate.setStatisticAction(templateStatisticsInputMetaSet);
            }

            if (CollectionUtils.isNotEmpty(templateMidTableInputMetas)) {
                Set<TemplateMidTableInputMeta> templateMidTableInputMetaSet = new HashSet<>();
                templateMidTableInputMetaSet.addAll(templateMidTableInputMetaDao.saveAll(templateMidTableInputMetas.stream().map(templateMidTableInputMeta -> {
                    templateMidTableInputMeta.setTemplate(savedTemplate);
                    templateMidTableInputMeta.setId(null);
                    return templateMidTableInputMeta;
                }).collect(Collectors.toList())));
                savedTemplate.setTemplateMidTableInputMetas(templateMidTableInputMetaSet);
            }

            if (CollectionUtils.isNotEmpty(templateDataSourceTypes)) {
                Set<TemplateDataSourceType> templateDataSourceTypeSet = new HashSet<>();
                for (TemplateDataSourceType templateDataSourceType : templateDataSourceTypes) {
                    templateDataSourceType.setId(null);
                    templateDataSourceType.setTemplate(savedTemplate);
                    templateDataSourceTypeSet.add(templateDataSourceTypeDao.save(templateDataSourceType));
                }
                savedTemplate.setTemplateDataSourceType(templateDataSourceTypeSet);
            }

            if (StringUtils.isNotEmpty(ruleTemplateVisibilityObject)) {
                List<DataVisibility> dataVisibilityList = objectMapper.readValue(ruleTemplateVisibilityObject, new TypeReference<List<DataVisibility>>() {
                });
                dataVisibilityList = dataVisibilityList.stream().map(dataVisibility -> {
                    dataVisibility.setId(null);
                    dataVisibility.setTableDataId(savedTemplate.getId());
                    return dataVisibility;
                }).collect(Collectors.toList());

                dataVisibilityDao.saveAll(dataVisibilityList);
            }
            templateInDb = templateDao.saveTemplate(savedTemplate);
        }
        return templateInDb;
    }

    private void createRuleUdf(Set<RuleUdf> ruleUdfs, Rule ruleInDb) {
        List<RuleUdf> ruleUdfList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(ruleUdfs)) {
            for (RuleUdf ruleUdf : ruleUdfs) {
                ruleUdf.setId(null);
                ruleUdf.setRule(ruleInDb);

                ruleUdfList.add(ruleUdf);
            }
            ruleInDb.setRuleUdfs(new HashSet<>(ruleUdfDao.saveAll(ruleUdfList)));
        }
    }

    private void createAlarmConfig(Set<AlarmConfig> alarmConfigs, Rule ruleInDb) {
        List<AlarmConfig> alarmConfigList = new ArrayList<>(alarmConfigs.size());

        if (CollectionUtils.isNotEmpty(alarmConfigs)) {
            Map<String, TemplateOutputMeta> templateOutputMetaMap = Maps.newHashMap();
            if (ruleInDb.getTemplate().getTemplateOutputMetas() != null) {
                for (TemplateOutputMeta templateOutputMeta : ruleInDb.getTemplate().getTemplateOutputMetas()) {
                    templateOutputMetaMap.put(templateOutputMeta.getOutputEnName(), templateOutputMeta);
                }
            }

            for (AlarmConfig alarmConfig : alarmConfigs) {
                alarmConfig.setId(null);
                alarmConfig.setRule(ruleInDb);

                if (alarmConfig.getRuleMetric() != null) {
                    RuleMetric ruleMetricInDb = ruleMetricDao.findByName(alarmConfig.getRuleMetric().getName());
                    if (ruleMetricInDb != null) {
                        alarmConfig.setRuleMetric(ruleMetricInDb);
                    } else {
                        RuleMetric ruleMetric = alarmConfig.getRuleMetric();
                        ruleMetric.setId(null);
                        ruleMetric.setCreateUser(ruleInDb.getCreateUser());
                        ruleMetric.setCreateTime(ruleInDb.getCreateTime());
                        RuleMetric savedRuleMetric = ruleMetricDao.add(ruleMetric);

                        alarmConfig.setRuleMetric(savedRuleMetric);
                    }
                }

                if (alarmConfig.getTemplateOutputMeta() != null) {
                    alarmConfig.setTemplateOutputMeta(templateOutputMetaMap.get(alarmConfig.getTemplateOutputMeta().getOutputEnName()));
                }
                alarmConfigList.add(alarmConfig);
            }
        }

        if (CollectionUtils.isNotEmpty(alarmConfigList)) {
            ruleInDb.setAlarmConfigs(alarmConfigDao.saveAllAlarmConfig(alarmConfigList).stream().collect(Collectors.toSet()));
        }

    }

    private void createRuleVariable(Set<RuleVariable> ruleVariables, Rule ruleInDb) {
        List<RuleVariable> ruleVariableList = new ArrayList<>(ruleVariables.size());

        if (CollectionUtils.isNotEmpty(ruleVariables)) {
            Map<String, TemplateMidTableInputMeta> templateMidTableInputMetaMap = Maps.newHashMap();
            for (TemplateMidTableInputMeta templateMidTableInputMeta : ruleInDb.getTemplate().getTemplateMidTableInputMetas()) {
                templateMidTableInputMetaMap.put(templateMidTableInputMeta.getEnName(), templateMidTableInputMeta);
            }
            Map<String, TemplateStatisticsInputMeta> templateStatisticsInputMetaHashMap = Maps.newHashMap();
            for (TemplateStatisticsInputMeta templateStatisticsInputMeta : ruleInDb.getTemplate().getStatisticAction()) {
                templateStatisticsInputMetaHashMap.put(templateStatisticsInputMeta.getPureName(), templateStatisticsInputMeta);
            }

            for (RuleVariable ruleVariable : ruleVariables) {
                ruleVariable.setId(null);
                ruleVariable.setRule(ruleInDb);
                if (ruleVariable.getTemplateMidTableInputMeta() != null) {
                    ruleVariable.setTemplateMidTableInputMeta(templateMidTableInputMetaMap.get(ruleVariable.getTemplateMidTableInputMeta().getEnName()));
                    // 同步规则的标准值ID
                    if (TemplateInputTypeEnum.STANDARD_VALUE_EXPRESSION.getCode().equals(ruleVariable.getTemplateMidTableInputMeta().getInputType())) {
                        ruleVariable.setValue(null != ruleInDb.getStandardValueVersionId() ? ruleInDb.getStandardValueVersionId().toString() : null);
                    }
                }
                if (ruleVariable.getTemplateStatisticsInputMeta() != null) {
                    ruleVariable.setTemplateStatisticsInputMeta(templateStatisticsInputMetaHashMap.get(ruleVariable.getTemplateStatisticsInputMeta().getPureName()));
                }
                ruleVariableList.add(ruleVariable);
            }
        }

        if (CollectionUtils.isNotEmpty(ruleVariableList)) {
            List<RuleVariable> savedRuleVariables = ruleVariableDao.saveAllRuleVariable(ruleVariableList);
            ruleInDb.setRuleVariables(savedRuleVariables.stream().collect(Collectors.toSet()));
        }
    }

    private void createDatasourceMapping(Set<RuleDataSourceMapping> ruleDataSourceMappings, Rule ruleInDb) {
        List<RuleDataSourceMapping> ruleDataSourceMappingList = new ArrayList<>(ruleDataSourceMappings.size());

        if (CollectionUtils.isNotEmpty(ruleDataSourceMappings)) {
            for (RuleDataSourceMapping ruleDataSourceMapping : ruleDataSourceMappings) {
                ruleDataSourceMapping.setId(null);
                ruleDataSourceMapping.setRule(ruleInDb);

                ruleDataSourceMappingList.add(ruleDataSourceMapping);
            }
        }

        if (CollectionUtils.isNotEmpty(ruleDataSourceMappingList)) {
            ruleInDb.setRuleDataSourceMappings(ruleDataSourceMappingDao.saveAll(ruleDataSourceMappingList).stream().collect(Collectors.toSet()));
        }
    }

    private ExcelRuleListener readExcel(InputStream inputStream) {
        LOGGER.info("Start to read excel");
        ExcelRuleListener excelRuleListener = read(inputStream);
        LOGGER.info("Finish to read excel. Rule content: {}", excelRuleListener.getExcelRuleContent());
        return excelRuleListener;
    }

    private TemplateMidTableInputMeta findMidTableInputMetaByTemplateAndMidTableName(Template template, String argumentKey) {
        List<TemplateMidTableInputMeta> templateMidTableInputMetas = templateMidTableInputMetaDao.findByRuleTemplate(template);
        for (TemplateMidTableInputMeta templateMidTableInputMeta : templateMidTableInputMetas) {
            if (templateMidTableInputMeta.getName().equals(argumentKey)) {
                return templateMidTableInputMeta;
            }
        }
        return null;
    }

    private TemplateOutputMeta findOutputMetaByTemplateAndOutputName(Template template, String templateOutputName) {
        List<TemplateOutputMeta> templateOutputMetas = templateOutputMetaDao.findByRuleTemplate(template);
        for (TemplateOutputMeta templateOutputMeta : templateOutputMetas) {
            if (templateOutputMeta.getOutputName().equals(templateOutputName)) {
                return templateOutputMeta;
            }
        }
        return null;
    }

    private ExcelRuleListener read(InputStream inputStream) {
        ExcelRuleListener listener = new ExcelRuleListener();
        ExcelReader excelReader = new ExcelReader(inputStream, null, listener);
        List<Sheet> sheets = excelReader.getSheets();
        for (Sheet sheet : sheets) {
            if (sheet.getSheetName().equals(ExcelSheetName.EXECUTION_PARAMETERS_NAME)) {
                sheet.setClazz(ExcelExecutionParametersByProject.class);
                sheet.setHeadLineMun(1);
                excelReader.read(sheet);
            } else if (sheet.getSheetName().equals(ExcelSheetName.RULE_NAME)) {
                sheet.setClazz(ExcelRuleByProject.class);
                sheet.setHeadLineMun(1);
                excelReader.read(sheet);
            } else if (sheet.getSheetName().equals(ExcelSheetName.TABLE_GROUP)) {
                sheet.setClazz(ExcelGroupByProject.class);
                sheet.setHeadLineMun(1);
                excelReader.read(sheet);
            }
        }

        return listener;
    }

    private GeneralResponse<?> downloadRulesReal(List<Rule> rules, HttpServletResponse response) throws IOException, WriteExcelException {
        String fileName = "batch_rules_export_" + QualitisConstants.FILE_DATE_FORMATTER.format(new Date()) + QualitisConstants.SUPPORT_EXCEL_SUFFIX_NAME;

        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setContentType("application/octet-stream");

        response.addHeader("Content-Disposition", "attachment;filename*=UTF-8''" + fileName);
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");

        List<Project> projects = rules.stream().map(rule -> rule.getProject()).collect(Collectors.toList());
        List<String> executionParamNames = rules.stream().filter(rule -> StringUtils.isNotEmpty(rule.getExecutionParametersName())).map(rule -> rule.getExecutionParametersName()).collect(Collectors.toList());

        // Write to response.getOutputStream
        OutputStream outputStream = response.getOutputStream();

        List<ExcelRuleByProject> templateRules = getRule(rules, null);
        List<ExcelGroupByProject> excelGroupByProjects = projectBatchService.getTableGroup(rules, null);
        List<ExcelExecutionParametersByProject> excelExecutionParametersByProject = projectBatchService.getExecutionParameters(projects, executionParamNames, true);
        writeExcelToOutput(templateRules, excelGroupByProjects, excelExecutionParametersByProject, outputStream);
        outputStream.flush();

        LOGGER.info("Succeed to download all rules in type of excel");
        return new GeneralResponse<>(ResponseStatusConstants.OK, "SUCCESS", null);
    }

    private void writeExcelToOutput(List<ExcelRuleByProject> templateRules,
                                    List<ExcelGroupByProject> excelGroupByProjects, List<ExcelExecutionParametersByProject> excelExecutionParametersByProject,
                                    OutputStream outputStream) throws WriteExcelException, IOException {
        try {
            LOGGER.info("Start to write excel");
            ExcelWriter writer = new ExcelWriter(outputStream, ExcelTypeEnum.XLSX, true);
            Sheet templateSheet = new Sheet(1, 0, ExcelRuleByProject.class);
            templateSheet.setSheetName(ExcelSheetName.RULE_NAME);
            writer.write(templateRules, templateSheet);

            Sheet groupSheet = new Sheet(2, 0, ExcelGroupByProject.class);
            groupSheet.setSheetName(ExcelSheetName.TABLE_GROUP);
            writer.write(excelGroupByProjects, groupSheet);

            Sheet executionParametersSheet = new Sheet(3, 0, ExcelExecutionParametersByProject.class);
            executionParametersSheet.setSheetName(ExcelSheetName.EXECUTION_PARAMETERS_NAME);
            writer.write(excelExecutionParametersByProject, executionParametersSheet);

            writer.finish();
            LOGGER.info("Finish to write excel");
        } catch (Exception e) {
            throw new WriteExcelException(e.getMessage(), 500);
        } finally {
            outputStream.close();
        }
    }

    @Override
    public List<ExcelRuleByProject> getRule(Iterable<Rule> rules, List<DiffVariableRequest> diffVariableRequestList) throws IOException {
        List<ExcelRuleByProject> lines = new ArrayList<>();
        for (Rule rule : rules) {
            ExcelRuleByProject ruleLine = new ExcelRuleByProject();
            // Basic info.
            ruleLine.setProjectName(rule.getProject().getName());
            ruleLine.setRuleGroupName(rule.getRuleGroup().getRuleGroupName());
            if (CollectionUtils.isNotEmpty(diffVariableRequestList)) {
                projectBatchService.replaceDiffVariable(rule, diffVariableRequestList);
            }
            ruleLine.setRuleJsonObject(objectMapper.writeValueAsString(rule));
            // Template
            ruleLine.setRuleTemplateJsonObject(objectMapper.writeValueAsString(rule.getTemplate()));
            // Template visibility department name list
            List<DataVisibility> dataVisibilityList = dataVisibilityService.filter(rule.getTemplate().getId(), TableDataTypeEnum.RULE_TEMPLATE);
            // Template preview
            if (RuleTypeEnum.CUSTOM_RULE.getCode().equals(rule.getRuleType())) {
                ruleLine.setRuleTemplatePreview(rule.getTemplate().getMidTableAction());
            } else if (RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode().equals(rule.getRuleType())){
                String templatePreview = getTemplatePreview(rule.getRuleDataSources(), rule.getRuleVariables(), rule.getTemplate().getMidTableAction());
                ruleLine.setRuleTemplatePreview(templatePreview);
            }

            if (CollectionUtils.isNotEmpty(dataVisibilityList)) {
                ruleLine.setRuleTemplateVisibilityObject(objectMapper.writerWithType(new TypeReference<List<DataVisibility>>() {
                }).writeValueAsString(dataVisibilityList));
            }
            lines.add(ruleLine);
        }

        return lines;
    }

    private String getTemplatePreview(Set<RuleDataSource> ruleDataSources, Set<RuleVariable> ruleVariables, String midTableAction) {
        if (CollectionUtils.isNotEmpty(ruleDataSources)) {
            ruleDataSources = ruleDataSources.stream().filter(ruleDataSource -> StringUtils.isNotBlank(ruleDataSource.getFilter())).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(ruleDataSources)) {
                String filter = ruleDataSources.iterator().next().getFilter();
                midTableAction = midTableAction.replace("${filter}", filter);
            }
        }
        if (CollectionUtils.isNotEmpty(ruleVariables)) {
            for (RuleVariable ruleVariable : ruleVariables) {
                if (ruleVariable.getTemplateMidTableInputMeta() == null) {
                    continue;
                }
                String midInputMetaPlaceHolder = ruleVariable.getTemplateMidTableInputMeta().getPlaceholder();
                String placeHolder = "\\$\\{" + midInputMetaPlaceHolder + "}";

                if (StringUtils.isNotBlank(ruleVariable.getValue())) {
                    midTableAction = midTableAction.replaceAll(placeHolder, ruleVariable.getValue());
                    LOGGER.info("Succeed to replace {} into {}", placeHolder, ruleVariable.getValue());
                }
            }
        }

        return midTableAction;
    }
}
