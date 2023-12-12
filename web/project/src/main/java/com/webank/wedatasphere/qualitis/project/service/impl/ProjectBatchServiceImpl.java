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

package com.webank.wedatasphere.qualitis.project.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Sheet;
import com.google.common.collect.Lists;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.*;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserRole;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.LinkisMetaDataManager;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.client.OperateCiService;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.request.LinkisDataSourceEnvRequest;
import com.webank.wedatasphere.qualitis.metadata.request.LinkisDataSourceRequest;
import com.webank.wedatasphere.qualitis.metadata.request.ModifyDataSourceParameterRequest;
import com.webank.wedatasphere.qualitis.metadata.response.datasource.LinkisDataSourceInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.datasource.LinkisDataSourceParamsResponse;
import com.webank.wedatasphere.qualitis.project.constant.ExcelSheetName;
import com.webank.wedatasphere.qualitis.project.constant.OperateTypeEnum;
import com.webank.wedatasphere.qualitis.project.constant.ProjectTransportTypeEnum;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.dao.ProjectLabelDao;
import com.webank.wedatasphere.qualitis.project.dao.ProjectUserDao;
import com.webank.wedatasphere.qualitis.project.dao.repository.ProjectRepository;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectLabel;
import com.webank.wedatasphere.qualitis.project.entity.ProjectUser;
import com.webank.wedatasphere.qualitis.project.excel.*;
import com.webank.wedatasphere.qualitis.project.request.DiffVariableRequest;
import com.webank.wedatasphere.qualitis.project.request.DownloadProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.UploadProjectRequest;
import com.webank.wedatasphere.qualitis.project.service.ProjectBatchService;
import com.webank.wedatasphere.qualitis.project.service.ProjectEventService;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.constant.DynamicEngineEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TableDataTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.*;
import com.webank.wedatasphere.qualitis.rule.dao.repository.DiffVariableRepository;
import com.webank.wedatasphere.qualitis.rule.entity.*;
import com.webank.wedatasphere.qualitis.rule.excel.ExcelRuleListener;
import com.webank.wedatasphere.qualitis.rule.service.*;
import com.webank.wedatasphere.qualitis.service.DataVisibilityService;
import com.webank.wedatasphere.qualitis.service.FileService;
import com.webank.wedatasphere.qualitis.service.RoleService;
import com.webank.wedatasphere.qualitis.service.SubDepartmentPermissionService;
import com.webank.wedatasphere.qualitis.util.DateUtils;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author howeye
 */
@Service
public class ProjectBatchServiceImpl implements ProjectBatchService {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private RuleDataSourceMappingDao ruleDataSourceMappingDao;
    @Autowired
    private DiffVariableRepository diffVariableRepository;
    @Autowired
    private RuleDatasourceEnvDao ruleDatasourceEnvDao;
    @Autowired
    private RuleDataSourceDao ruleDataSourceDao;
    @Autowired
    private ClusterInfoDao clusterInfoDao;
    @Autowired
    private RuleGroupDao ruleGroupDao;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private RuleMetricDao ruleMetricDao;
    @Autowired
    private ProjectUserDao projectUserDao;
    @Autowired
    private ProjectLabelDao projectLabelDao;
    @Autowired
    private ExecutionVariableDao executionVariableDao;
    @Autowired
    private ExecutionParametersDao executionParametersDao;
    @Autowired
    private StaticExecutionParametersDao staticExecutionParametersDao;
    @Autowired
    private NoiseEliminationManagementDao noiseEliminationManagementDao;
    @Autowired
    private AlarmArgumentsExecutionParametersDao alarmArgumentsExecutionParametersDao;
    @Autowired
    private RuleMetricDepartmentUserDao ruleMetricDepartmentUserDao;

    @Autowired
    private SubDepartmentPermissionService subDepartmentPermissionService;
    @Autowired
    private ExecutionParametersService executionParametersService;
    @Autowired
    private DataVisibilityService dataVisibilityService;
    @Autowired
    private ProjectEventService projectEventService;
    @Autowired
    private RuleBatchService ruleBatchService;
    @Autowired
    private OperateCiService operateCiService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private MetaDataClient metaDataClient;
    @Autowired
    private FileService fileService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private LinkisConfig linkisConfig;
    @Autowired
    private RuleTemplateDao ruleTemplateDao;
    @Autowired
    private TemplateMidTableInputMetaService templateMidTableInputMetaService;
    @Autowired
    private TemplateStatisticsInputMetaService templateStatisticsInputMetaService;
    @Autowired
    private TemplateOutputMetaDao templateOutputMetaDao;
    @Autowired
    private DataVisibilityDao dataVisibilityDao;
    @Autowired
    private LinkisMetaDataManager linkisMetaDataManager;
    @Autowired
    private LinkisDataSourceService linkisDataSourceService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectBatchServiceImpl.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private HttpServletRequest httpServletRequest;

    public ProjectBatchServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    private GeneralResponse uploadProjectsReal(ExcelProjectListener listener, String userName, boolean aomp, Long updateProjectId
            , List<DiffVariableRequest> diffVariableRequestList, List<File> udfFiles, StringBuilder operateComment) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException, JSONException, MetaDataAcquireFailedException {

        if (userName == null) {
            return new GeneralResponse<>("401", "{&PLEASE_LOGIN}", null);
        }

        User user = userDao.findByUsername(userName);

        if (user == null) {
            return new GeneralResponse<>("401", "{&PLEASE_LOGIN}", null);
        }

        // Check if excel file is empty
        if (listener.getExcelProjectContent().isEmpty() && listener.getExcelMetricContent().isEmpty() && listener.getExcelExecutionParametersContent().isEmpty() && listener.getExcelGroupByProjects().isEmpty()) {
            throw new UnExpectedRequestException("{&FILE_CAN_NOT_BE_EMPTY_OR_FILE_CAN_NOT_BE_RECOGNIZED}", 422);
        }

        updateProjectId = handleProject(user, listener.getExcelProjectContent(), updateProjectId, aomp);

        handleExecutionParameters(listener.getExcelExecutionParametersContent(), userName, updateProjectId);
        try {
            handleMetricData(user, listener.getExcelMetricContent());
            handleDataSource(user, listener.getExcelDatasourceEnvContent());
        } catch (Exception e) {
            LOGGER.error("uploadProjectsReal, failed message: " + e.getMessage(), e);
            operateComment.append(SpecCharEnum.LINE.getValue()).append(e.getMessage());
        }

        // Create rules according to excel sheet
        List<ExcelRuleByProject> excelRuleByProject = listener.getExcelRuleContent();

        if (updateProjectId != null) {

            List<Project> projectList = new ArrayList<>();
            Project projectInDb = projectDao.findById(updateProjectId);
            if (projectInDb == null) {
                throw new UnExpectedRequestException("Project: [ID=" + updateProjectId + "] {&DOES_NOT_EXIST}");
            }

            projectList.add(projectInDb);
            try {
                handleTableGroup(listener, updateProjectId, diffVariableRequestList);

                List<String> newRuleNames = new ArrayList<>();
                ruleBatchService.getAndSaveRule(excelRuleByProject, projectInDb, newRuleNames, userName, diffVariableRequestList);

                // Disable rules not be modified.
                List<Rule> rules = ruleDao.findByProject(projectInDb);
                rules = rules.stream().filter(rule -> !newRuleNames.contains(rule.getName())).map(rule -> {
                    rule.setEnable(Boolean.FALSE);
                    return rule;
                }).collect(Collectors.toList());
                ruleDao.saveRules(rules);
            } catch (Exception e) {
                LOGGER.error("uploadProjectsReal, failed to save rules", e);
                operateComment.append(SpecCharEnum.LINE.getValue()).append(e.getMessage());
            }

            projectEventService.recordBatch(projectList, userName, operateComment.append(SpecCharEnum.LINE.getValue()).toString(), OperateTypeEnum.IMPORT_PROJECT);
        }

        return new GeneralResponse<>("200", "{&SUCCEED_TO_UPLOAD_FILE}", null);
    }

    private Long handleProject(User user, List<ExcelProject> excelProjectContent, Long updateProjectId, boolean aomp) throws UnExpectedRequestException
            , IOException, PermissionDeniedRequestException {

        for (ExcelProject excelProject : excelProjectContent) {
            Project project = objectMapper.readValue(excelProject.getProjectObject(), Project.class);

            if (aomp) {
                Project projectInDb = projectDao.findByNameAndCreateUser(project.getName(), user.getUsername());
                if (projectInDb != null) {
                    updateProjectId = projectInDb.getId();
                }
            }
            if (updateProjectId != null) {
                LOGGER.info("Start to modfiy project[ID={}] with upload project file.", updateProjectId);

                // Check existence of project
                Project projectInDb = projectService.checkProjectExistence(updateProjectId, user.getUsername());
                // Check permissions of project
                List<Integer> permissions = new ArrayList<>();
                permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
                projectService.checkProjectPermission(projectInDb, user.getUsername(), permissions);

                project.setId(updateProjectId);

                Project otherProject = projectDao.findByNameAndCreateUser(project.getName(), project.getCreateUser());
                if (otherProject != null && !otherProject.getId().equals(updateProjectId)) {
                    throw new UnExpectedRequestException(String.format("Project name: %s already exist", otherProject.getName()));
                }
                project.setModifyUser(user.getUsername());
                project.setModifyTime(QualitisConstants.PRINT_TIME_FORMAT.format(new Date()));
                project.setModifyUserFullName(user.getUsername() + "(" + (StringUtils.isNotEmpty(user.getChineseName()) ? user.getChineseName() : "") + ")");
                Project savedProject = projectDao.saveProject(project);
                projectService.autoAuthAdminAndProxy(user, savedProject);
                handleProjectUserAndLabel(excelProject, savedProject);
                updateProjectId = savedProject.getId();
            } else {
                LOGGER.info("Start to add project with upload project file.");

                project.setId(updateProjectId);

                Project otherProject = projectDao.findByNameAndCreateUser(project.getName(), user.getUsername());
                if (otherProject != null) {
                    throw new UnExpectedRequestException(String.format("Project name: %s already exist", otherProject.getName()));
                }
                project.setCreateUser(user.getUsername());
                project.setCreateTime(QualitisConstants.PRINT_TIME_FORMAT.format(new Date()));
                project.setCreateUserFullName(user.getUsername() + "(" + (StringUtils.isNotEmpty(user.getChineseName()) ? user.getChineseName() : "") + ")");
                Project savedProject = projectDao.saveProject(project);
                projectService.autoAuthAdminAndProxy(user, savedProject);
                handleProjectUserAndLabel(excelProject, savedProject);
                updateProjectId = savedProject.getId();
            }
        }

        return updateProjectId;
    }

    @Override
    public void handleMetricData(User user, List<ExcelRuleMetric> excelMetricContent) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException {
        List<UserRole> userRoles = userRoleDao.findByUser(user);
        Integer roleType = roleService.getRoleType(userRoles);

        for (ExcelRuleMetric excelRuleMetric : excelMetricContent) {
            RuleMetric ruleMetric = objectMapper.readValue(excelRuleMetric.getRuleMetricJsonObject(), RuleMetric.class);

            RuleMetric ruleMetricInDb = ruleMetricDao.findByName(ruleMetric.getName());

            RuleMetric savedRuleMetric;
            if (ruleMetricInDb == null) {
                ruleMetric.setId(null);
                ruleMetric.setCreateUser(user.getUsername());
                ruleMetric.setCreateTime(QualitisConstants.PRINT_TIME_FORMAT.format(new Date()));

                savedRuleMetric = ruleMetricDao.add(ruleMetric);
            } else {
                subDepartmentPermissionService.checkEditablePermission(roleType, user, null, ruleMetric.getDevDepartmentId(), ruleMetric.getOpsDepartmentId(), false);

                ruleMetric.setId(ruleMetricInDb.getId());
                ruleMetric.setModifyUser(user.getUsername());
                ruleMetric.setModifyTime(QualitisConstants.PRINT_TIME_FORMAT.format(new Date()));
                savedRuleMetric = ruleMetricDao.add(ruleMetric);
                List<DataVisibility> dataVisibilityList = dataVisibilityService.filter(savedRuleMetric.getId(), TableDataTypeEnum.RULE_METRIC);
                if (CollectionUtils.isNotEmpty(dataVisibilityList)) {
                    dataVisibilityService.delete(savedRuleMetric.getId(), TableDataTypeEnum.RULE_METRIC);
                }
            }
            if (StringUtils.isNotEmpty(excelRuleMetric.getDataVisibilityJsonObject())) {
                List<DataVisibility> dataVisibilitys = objectMapper.readValue(excelRuleMetric.getDataVisibilityJsonObject(), new TypeReference<List<DataVisibility>>() {});

                if (CollectionUtils.isNotEmpty(dataVisibilitys)) {
                    dataVisibilitys = dataVisibilitys.stream().map(dataVisibility -> {
                        dataVisibility.setId(null);
                        dataVisibility.setTableDataId(savedRuleMetric.getId());
                        dataVisibility.setTableDataType(TableDataTypeEnum.RULE_METRIC.getCode());
                        return dataVisibility;
                    }).collect(Collectors.toList());

                    dataVisibilityDao.saveAll(dataVisibilitys);
                }
            }
        }
    }

    private void handleProjectUserAndLabel(ExcelProject excelProject, Project savedProject) throws IOException {
        if (StringUtils.isNotEmpty(excelProject.getProjectUserObject())) {
            if (savedProject.getProjectUsers() != null) {
                for (ProjectUser projectUser : savedProject.getProjectUsers()) {
                    projectUserDao.deleteByProjectAndUserName(savedProject, projectUser.getUserName());
                }
            }
            Set<ProjectUser> projectUsers = objectMapper
                    .readValue(excelProject.getProjectUserObject(), new TypeReference<Set<ProjectUser>>() {
                    });
            projectUsers = projectUsers.stream().map(projectUser -> {
                projectUser.setId(null);
                projectUser.setProject(savedProject);
                return projectUser;
            }).collect(Collectors.toSet());
            projectUserDao.saveAll(projectUsers);
        } else {
            if (savedProject.getProjectUsers() != null) {
                for (ProjectUser projectUser : savedProject.getProjectUsers()) {
                    projectUserDao.deleteByProjectAndUserName(savedProject, projectUser.getUserName());
                }
            }
        }

        if (StringUtils.isNotEmpty(excelProject.getProjectLabelObject())) {
            projectLabelDao.deleteByProject(savedProject);
            Set<ProjectLabel> projectLabels = objectMapper
                    .readValue(excelProject.getProjectLabelObject(), new TypeReference<Set<ProjectLabel>>() {
                    });
            projectLabels = projectLabels.stream().map(projectLabel -> {
                projectLabel.setId(null);
                projectLabel.setProject(savedProject);
                return projectLabel;
            }).collect(Collectors.toSet());
            projectLabelDao.saveAll(projectLabels);
        } else {
            projectLabelDao.deleteByProject(savedProject);
        }
    }


    @Override
    public void handleExecutionParameters(List<ExcelExecutionParametersByProject> excelExecutionParametersByProjects, String userName, Long updateProjectId) throws IOException {
        for (ExcelExecutionParametersByProject excelExecutionParametersByProject : excelExecutionParametersByProjects) {
            LOGGER.info("{} start to handle current execution param json {}", userName, excelExecutionParametersByProject.getExecutionParameterJsonObject());
            handleExecutionParametersReal(excelExecutionParametersByProject.getExecutionParameterJsonObject(), userName, updateProjectId);
            LOGGER.info("Finish to handle execution param", userName);
        }
    }

    @Override
    public void handleExecutionParametersReal(String executionParameterJsonObject, String userName, Long updateProjectId) throws IOException {
        ExecutionParameters executionParameters = objectMapper.readValue(executionParameterJsonObject, ExecutionParameters.class);
        ExecutionParameters executionParametersInDb = executionParametersDao.findByNameAndProjectId(executionParameters.getName(), updateProjectId);
        Set<AlarmArgumentsExecutionParameters> alarmArgumentsExecutionParameters = executionParameters.getAlarmArgumentsExecutionParameters();
        Set<NoiseEliminationManagement> noiseEliminationManagement = executionParameters.getNoiseEliminationManagement();
        Set<StaticExecutionParameters> staticExecutionParameters = executionParameters.getStaticExecutionParameters();
        Set<ExecutionVariable> executionVariableSets = executionParameters.getExecutionVariableSets();

        if (executionParametersInDb != null) {
            executionParameters.setId(executionParametersInDb.getId());
            executionParameters.setProjectId(executionParametersInDb.getProjectId());
            executionParameters.setModifyTime(QualitisConstants.PRINT_TIME_FORMAT.format(new Date()));
            executionParameters.setModifyUser(userName);

            // Clear
            clearExecutionParametersRelations(executionParametersInDb);
        } else {
            executionParameters.setId(null);
            executionParameters.setProjectId(updateProjectId);
            executionParameters.setCreateTime(QualitisConstants.PRINT_TIME_FORMAT.format(new Date()));
            executionParameters.setCreateUser(userName);
            executionParameters.setExecutionVariableSets(null);
            executionParameters.setStaticExecutionParameters(null);
            executionParameters.setNoiseEliminationManagement(null);
            executionParameters.setAlarmArgumentsExecutionParameters(null);
            executionParametersInDb = executionParametersDao.saveExecutionParameters(executionParameters);
        }
        createExecutionParametersRelations(alarmArgumentsExecutionParameters, noiseEliminationManagement, staticExecutionParameters, executionVariableSets, executionParametersInDb);
        executionParametersDao.saveExecutionParameters(executionParametersInDb);
    }

    private void createExecutionParametersRelations(Set<AlarmArgumentsExecutionParameters> alarmArgumentsExecutionParameterSet, Set<NoiseEliminationManagement> noiseEliminationManagementSet
            , Set<StaticExecutionParameters> staticExecutionParameterSet, Set<ExecutionVariable> executionVariableSet, ExecutionParameters executionParametersInDb) {
        if (CollectionUtils.isNotEmpty(alarmArgumentsExecutionParameterSet)) {
            List<AlarmArgumentsExecutionParameters> alarmArgumentsExecutionParametersList = alarmArgumentsExecutionParameterSet.stream().map(alarmArgumentsExecutionParameters -> {
                alarmArgumentsExecutionParameters.setId(null);
                alarmArgumentsExecutionParameters.setExecutionParameters(executionParametersInDb);
                return alarmArgumentsExecutionParameters;
            }).collect(Collectors.toList());

            executionParametersInDb.setAlarmArgumentsExecutionParameters(alarmArgumentsExecutionParametersDao.saveAll(alarmArgumentsExecutionParametersList));
        }
        if (CollectionUtils.isNotEmpty(noiseEliminationManagementSet)) {
            List<NoiseEliminationManagement> noiseEliminationManagementList = noiseEliminationManagementSet.stream().map(noiseEliminationManagement -> {
                noiseEliminationManagement.setId(null);
                noiseEliminationManagement.setExecutionParameters(executionParametersInDb);
                return noiseEliminationManagement;
            }).collect(Collectors.toList());

            executionParametersInDb.setNoiseEliminationManagement(noiseEliminationManagementDao.saveAll(noiseEliminationManagementList));
        }
        if (CollectionUtils.isNotEmpty(staticExecutionParameterSet)) {
            List<StaticExecutionParameters> staticExecutionParametersList = staticExecutionParameterSet.stream().map(staticExecutionParameters -> {
                staticExecutionParameters.setId(null);
                staticExecutionParameters.setExecutionParameters(executionParametersInDb);
                return staticExecutionParameters;
            }).collect(Collectors.toList());

            executionParametersInDb.setStaticExecutionParameters(staticExecutionParametersDao.saveAll(staticExecutionParametersList));
            //DynamicEngineEnum中YARN、SPARK、FLINK、CUSTOM
            String staticStartupParam = staticExecutionParameterSet.stream()
                    .filter(staticExecutionParametersRequest -> DynamicEngineEnum.YARN_ARGUMENTS.getCode().equals(staticExecutionParametersRequest.getParameterType()) ||
                            DynamicEngineEnum.SPARK_ARGUMENTS.getCode().equals(staticExecutionParametersRequest.getParameterType()) ||
                            DynamicEngineEnum.FLINK_ARGUMENTS.getCode().equals(staticExecutionParametersRequest.getParameterType()) ||
                            DynamicEngineEnum.CUSTOM_ARGUMENTS.getCode().equals(staticExecutionParametersRequest.getParameterType())
                    )
                    .map(staticExecutionParametersRequest -> staticExecutionParametersRequest.getParameterName() + SpecCharEnum.EQUAL.getValue() + staticExecutionParametersRequest.getParameterValue())
                    .collect(Collectors.joining(SpecCharEnum.DIVIDER.getValue()));

            executionParametersInDb.setStaticStartupParam(staticStartupParam);
        }
        if (CollectionUtils.isNotEmpty(executionVariableSet)) {
            List<ExecutionVariable> executionVariableList = executionVariableSet.stream().map(executionVariable -> {
                executionVariable.setId(null);
                executionVariable.setExecutionParameters(executionParametersInDb);
                return executionVariable;
            }).collect(Collectors.toList());

            executionParametersInDb.setExecutionVariableSets(executionVariableDao.saveAll(executionVariableList));
            String executionParam = executionVariableSet.stream()
                    .map(executionManagementRequest -> executionManagementRequest.getVariableName() + SpecCharEnum.COLON.getValue() + executionManagementRequest.getVariableValue())
                    .collect(Collectors.joining(SpecCharEnum.DIVIDER.getValue()));
            //并发粒度：库粒度、表粒度，引擎复用参数，拼接到执行变量------>因规则执行时，表单的默认会传发粒度、引擎复用等且优先级最高，所以这里不需拼接；

            executionParametersInDb.setExecutionParam(executionParam);
        }
    }

    private void clearExecutionParametersRelations(ExecutionParameters executionParametersInDb) {
        executionVariableDao.deleteByExecutionParameters(executionParametersInDb);
        staticExecutionParametersDao.deleteByExecutionParameters(executionParametersInDb);
        noiseEliminationManagementDao.deleteByExecutionParameters(executionParametersInDb);
        alarmArgumentsExecutionParametersDao.deleteByExecutionParameters(executionParametersInDb);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public void handleTableGroup(AnalysisEventListener listener, Long projectId, List<DiffVariableRequest> diffVariableRequestList)
            throws IOException, UnExpectedRequestException {
        List<ExcelGroupByProject> excelGroupByProjects = new ArrayList<>();

        if (listener instanceof ExcelProjectListener) {
            excelGroupByProjects = ((ExcelProjectListener) listener).getExcelGroupByProjects();
        } else if (listener instanceof ExcelRuleListener) {
            excelGroupByProjects = ((ExcelRuleListener) listener).getExcelGroupByProjects();
        }

        for (ExcelGroupByProject excelGroupByProject : excelGroupByProjects) {
            RuleGroup ruleGroup = objectMapper.readValue(excelGroupByProject.getRuleGroupJsonObject(), RuleGroup.class);
            RuleGroup ruleGroupInDb = ruleGroupDao.findByRuleGroupNameAndProjectId(ruleGroup.getRuleGroupName(), projectId);

            Set<RuleDataSource> ruleDataSources = ruleGroup.getRuleDataSources();
            if (ruleGroupInDb != null) {
                ruleGroup.setId(ruleGroupInDb.getId());
                ruleGroup.setProjectId(ruleGroupInDb.getProjectId());
                // Clear
                clearDatasourceEnv(ruleGroupInDb);
            } else {
                ruleGroup.setId(null);
                ruleGroup.setProjectId(projectId);
                ruleGroup.setRuleDataSources(null);
            }
            ruleGroupInDb = ruleGroupDao.saveRuleGroup(ruleGroup);
            createDatasourceEnv(ruleDataSources, ruleGroupInDb, diffVariableRequestList);
            ruleGroupDao.saveRuleGroup(ruleGroupInDb);
        }
    }

    @Override
    public void createDatasourceEnv(Set<RuleDataSource> ruleDataSourcesFromJson, Object object, List<DiffVariableRequest> diffVariableRequestList) throws UnExpectedRequestException {
        List<RuleDataSource> ruleDataSources = new ArrayList<>();
        List<RuleDataSourceEnv> ruleDataSourceEnvs = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(ruleDataSourcesFromJson)) {
            for (RuleDataSource ruleDataSource : ruleDataSourcesFromJson) {
                if (CollectionUtils.isNotEmpty(diffVariableRequestList)) {
                    for (DiffVariableRequest diffVariableRequest : diffVariableRequestList) {
                        if (QualitisConstants.EXECUTE_USER.equals(diffVariableRequest.getName())) {
                            User user = userDao.findByUsername(diffVariableRequest.getValue());
                            if (user == null) {
                                throw new UnExpectedRequestException(diffVariableRequest.getValue() + "not in the system's user list.");
                            }
                            String proxyUser = ruleDataSource.getProxyUser();
                            if (StringUtils.isNotEmpty(proxyUser)) {
                                proxyUser = proxyUser.replace("[@" + diffVariableRequest.getName() + "]", diffVariableRequest.getValue());
                                ruleDataSource.setProxyUser(proxyUser);
                            }
                        } else if (QualitisConstants.EXECUTE_CLUSETER.equals(diffVariableRequest.getName())) {
                            String clusterName = ruleDataSource.getClusterName();
                            ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(diffVariableRequest.getValue());
                            if (clusterInfo == null) {
                                throw new UnExpectedRequestException(diffVariableRequest.getValue() + "not in the system's cluster list.");
                            }
                            if (StringUtils.isNotEmpty(clusterName)) {
                                clusterName = clusterName.replace("[@" + diffVariableRequest.getName() + "]", diffVariableRequest.getValue());
                                ruleDataSource.setClusterName(clusterName);
                            }
                        }
                    }
                }
                Map<String, Long> envs = new HashMap<>();
                if (StringUtils.isNotEmpty(ruleDataSource.getLinkisDataSourceName())) {
                    LinkisDataSource linkisDataSource = linkisDataSourceService.getByLinkisDataSourceName(ruleDataSource.getLinkisDataSourceName());
                    if (linkisDataSource != null) {
                        ruleDataSource.setLinkisDataSourceVersionId(linkisDataSource.getVersionId());
                        ruleDataSource.setLinkisDataSourceId(linkisDataSource.getLinkisDataSourceId());
                        envs.putAll(linkisDataSourceService.getEnvNameAndIdMap(linkisDataSource));
                    }
                }
                ruleDataSource.setId(null);
                if (object instanceof RuleGroup) {
                    ruleDataSource.setRuleGroup((RuleGroup) object);
                    ruleDataSource.setProjectId(((RuleGroup) object).getProjectId());
                } else {
                    ruleDataSource.setRule((Rule) object);
                    ruleDataSource.setProjectId(((Rule) object).getProject().getId());
                }
                if (CollectionUtils.isNotEmpty(ruleDataSource.getRuleDataSourceEnvs())) {
                    ruleDataSourceEnvs.addAll(ruleDataSource.getRuleDataSourceEnvs().stream().map(currRuleDataSourceEnv -> {
                        currRuleDataSourceEnv.setEnvId(envs.get(currRuleDataSourceEnv.getEnvName()));
                        currRuleDataSourceEnv.setRuleDataSource(ruleDataSource);
                        currRuleDataSourceEnv.setId(null);
                        return currRuleDataSourceEnv;
                    }).collect(Collectors.toList()));
                }
                ruleDataSources.add(ruleDataSource);
            }
        }

        if (CollectionUtils.isNotEmpty(ruleDataSources)) {
            Set<RuleDataSource> ruleDataSourceSet = ruleDataSourceDao.saveAllRuleDataSource(ruleDataSources).stream().collect(Collectors.toSet());
            if (object instanceof RuleGroup) {
                ((RuleGroup) object).setRuleDataSources(ruleDataSourceSet);
            } else {
                ((Rule) object).setRuleDataSources(ruleDataSourceSet);
            }
        }

        if (CollectionUtils.isNotEmpty(ruleDataSourceEnvs)) {
            ruleDatasourceEnvDao.saveAllRuleDataSourceEnv(ruleDataSourceEnvs);
        }
    }

    @Override
    public void clearDatasourceEnv(Object object) {
        if (object instanceof RuleGroup) {
            if (CollectionUtils.isNotEmpty(((RuleGroup) object).getRuleDataSources())) {
                ruleDataSourceDao.deleteByRuleGroup(((RuleGroup) object));
            }
        } else {
            if (CollectionUtils.isNotEmpty(((Rule) object).getRuleDataSources())) {
                ruleDataSourceDao.deleteByRule(((Rule) object));
            }
        }
    }

    private ExcelProjectListener readExcel(InputStream inputStream, ExcelProjectListener listener) {
        LOGGER.info("Start to read project excel");
        ExcelReader excelReader = new ExcelReader(new BufferedInputStream(inputStream), null, listener);
        List<Sheet> sheets = excelReader.getSheets();
        for (Sheet sheet : sheets) {
            if (sheet.getSheetName().equals(ExcelSheetName.RULE_NAME)) {
                sheet.setClazz(ExcelRuleByProject.class);
                sheet.setHeadLineMun(1);
                excelReader.read(sheet);
            } else if (sheet.getSheetName().equals(ExcelSheetName.PROJECT_NAME)) {
                sheet.setClazz(ExcelProject.class);
                sheet.setHeadLineMun(1);
                excelReader.read(sheet);
            } else if (sheet.getSheetName().equals(ExcelSheetName.RULE_METRIC_NAME)) {
                sheet.setClazz(ExcelRuleMetric.class);
                sheet.setHeadLineMun(1);
                excelReader.read(sheet);
            } else if (sheet.getSheetName().equals(ExcelSheetName.EXECUTION_PARAMETERS_NAME)) {
                sheet.setClazz(ExcelExecutionParametersByProject.class);
                sheet.setHeadLineMun(1);
                excelReader.read(sheet);
            } else if (sheet.getSheetName().equals(ExcelSheetName.TABLE_GROUP)) {
                sheet.setClazz(ExcelGroupByProject.class);
                sheet.setHeadLineMun(1);
                excelReader.read(sheet);
            } else if (sheet.getSheetName().equals(ExcelSheetName.DATASOURCE_ENV)) {
                sheet.setClazz(ExcelDatasourceEnv.class);
                sheet.setHeadLineMun(1);
                excelReader.read(sheet);
            }
        }

        LOGGER.info("Finish to read project excel. excel content: rule sheet {}, project sheet {}", listener.getExcelRuleContent(), listener.getExcelProjectContent());
        return listener;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse uploadProjectFromAomp(InputStream fileInputStream, FormDataContentDisposition fileDisposition, String userName)
            throws IOException, UnExpectedRequestException, PermissionDeniedRequestException, JSONException, MetaDataAcquireFailedException {

        UploadProjectRequest uploadProjectRequest = new UploadProjectRequest();
        String localZipPath = fileService.uploadFile(fileInputStream, fileDisposition, userName).getData();
        uploadProjectRequest.setUploadType(ProjectTransportTypeEnum.LOCAL.getCode());
        uploadProjectRequest.setZipPath(localZipPath);
        uploadProjectRequest.setLoginUser(userName);

        return uploadProjectFromLocalOrGit(uploadProjectRequest, true);
    }

    private List<ExcelGroupByProject> getGroup(List<Project> projects, List<DiffVariableRequest> diffVariableRequestList) throws IOException {
        List<ExcelGroupByProject> excelGroupByProjects = new ArrayList<>();
        for (Project project : projects) {
            List<Rule> rules = ruleDao.findByProject(project);
            if (CollectionUtils.isEmpty(rules)) {
                continue;
            }
            excelGroupByProjects = getTableGroup(rules, diffVariableRequestList);
        }

        return excelGroupByProjects;
    }

    @Override
    public List<ExcelGroupByProject> getTableGroup(List<Rule> rules, List<DiffVariableRequest> diffVariableRequestList) throws IOException {
        Set<RuleGroup> ruleGroups = rules.stream().map(rule -> rule.getRuleGroup()).filter(ruleGroup -> CollectionUtils.isNotEmpty(ruleGroup.getRuleDataSources())).collect(Collectors.toSet());
        List<ExcelGroupByProject> excelGroupByProjects = new ArrayList<>();
        List<String> existNames = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ruleGroups)) {
            for (RuleGroup ruleGroup : ruleGroups) {
                if (existNames.contains(ruleGroup.getRuleGroupName())) {
                    continue;
                }
                if (CollectionUtils.isNotEmpty(diffVariableRequestList)) {
                    replaceDiffVariable(ruleGroup, diffVariableRequestList);
                }
                ExcelGroupByProject excelGroupByProject = new ExcelGroupByProject();
                excelGroupByProject.setRuleGroupJsonObject(objectMapper.writeValueAsString(ruleGroup));

                excelGroupByProjects.add(excelGroupByProject);
                existNames.add(ruleGroup.getRuleGroupName());
            }
        }

        return excelGroupByProjects;
    }

    @Override
    public void replaceDiffVariable(Object ruleOrGroup, List<DiffVariableRequest> diffVariableRequestList) {
        List<DiffVariable> diffVariableList = diffVariableRepository.findAll();
        List<String> diffVariableNameList = diffVariableList.stream().map(diffVariable -> diffVariable.getName()).collect(Collectors.toList());

        for (DiffVariableRequest diffVariableRequest : diffVariableRequestList) {
            if (! diffVariableNameList.contains(diffVariableRequest.getName())) {
                continue;
            }
            Set<RuleDataSource> ruleDatasources;
            if (ruleOrGroup instanceof Rule) {
                ruleDatasources = ((Rule) ruleOrGroup).getRuleDataSources();
            } else {
                ruleDatasources = ((RuleGroup) ruleOrGroup).getRuleDataSources();
            }
            if (QualitisConstants.EXECUTE_USER.equals(diffVariableRequest.getName())) {
                ruleDatasources = ruleDatasources.stream().map(ruleDataSource -> {
                    ruleDataSource.setProxyUser("[@" + diffVariableRequest.getName() + "]");
                    return ruleDataSource;
                }).collect(Collectors.toSet());

            } else if (QualitisConstants.EXECUTE_CLUSETER.equals(diffVariableRequest.getName())) {
                ruleDatasources = ruleDatasources.stream().map(ruleDataSource -> {
                    ruleDataSource.setClusterName("[@" + diffVariableRequest.getName() + "]");
                    return ruleDataSource;
                }).collect(Collectors.toSet());
            }
            if (ruleOrGroup instanceof Rule) {
                ((Rule) ruleOrGroup).setRuleDataSources(ruleDatasources);
            } else {
                ((RuleGroup) ruleOrGroup).setRuleDataSources(ruleDatasources);
            }

        }
    }

    private List<ExcelRuleByProject> getExcelRuleByProject(List<Project> projects, List<DiffVariableRequest> diffVariableRequestList) throws IOException {
        List<ExcelRuleByProject> excelRuleByProjects = new ArrayList<>();
        for (Project project : projects) {
            List<Rule> rules = ruleDao.findByProject(project);
            List<ExcelRuleByProject> excelRuleByProjectList = ruleBatchService.getRule(rules, diffVariableRequestList);

            excelRuleByProjects.addAll(excelRuleByProjectList);
        }
        return excelRuleByProjects;
    }

    private List<ExcelProject> getExcelProject(List<Project> projects) throws IOException {
        List<ExcelProject> excelProjects = new ArrayList<>();
        for (Project project : projects) {
            ExcelProject excelProject = new ExcelProject();
            excelProject.setProjectObject(objectMapper.writeValueAsString(project));

            Set<ProjectUser> projectUsers = project.getProjectUsers();
            if (CollectionUtils.isNotEmpty(projectUsers)) {
                projectUsers = projectUsers.stream().filter(projectUser -> projectUser.getUserName().equals(project.getCreateUser())).collect(Collectors.toSet());
                excelProject.setProjectUserObject(objectMapper.writerWithType(new TypeReference<Set<ProjectUser>>() {
                }).writeValueAsString(projectUsers));
            }

            LOGGER.info("Collect excel line: {}", excelProject);
            excelProjects.add(excelProject);
        }
        return excelProjects;
    }

    @Override
    public List<ExcelExecutionParametersByProject> getExecutionParameters(List<Project> projects, List<String> executionParamNames, boolean batchRules) throws IOException {
        List<ExcelExecutionParametersByProject> excelList = new ArrayList<>();
        for (Project project : projects) {
            List<ExecutionParameters> allExecutionParameters = executionParametersDao.getAllExecutionParameters(project.getId());

            for (ExecutionParameters allExecutionParameter : allExecutionParameters) {
                if (batchRules && CollectionUtils.isNotEmpty(executionParamNames) && !executionParamNames.contains(allExecutionParameter.getName())) {
                    continue;
                }
                ExcelExecutionParametersByProject excelExecutionParameters = new ExcelExecutionParametersByProject();
                excelExecutionParameters.setExecutionParameterJsonObject(objectMapper.writeValueAsString(allExecutionParameter));
                LOGGER.info("Collect excel line of ExecutionParameters: {}", excelExecutionParameters);
                excelList.add(excelExecutionParameters);
            }
        }
        return excelList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse uploadProjectFromLocalOrGit(UploadProjectRequest request, boolean aomp) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException, JSONException, MetaDataAcquireFailedException {
        String userName = aomp ? request.getLoginUser() : HttpUtils.getUserName(httpServletRequest);
        List<File> udfFiles = new ArrayList<>();
        List<File> projectFiles = new ArrayList<>();
        StringBuilder operateComment = new StringBuilder();
        if (ProjectTransportTypeEnum.LOCAL.getCode().equals(request.getUploadType())) {
            // Check file and unzip
            projectFiles = checkFileAndUnzip(request.getZipPath());
            operateComment.append(ProjectTransportTypeEnum.LOCAL.name());
        } else {
            throw new UnExpectedRequestException("Not support upload type.");
        }

        ExcelProjectListener listener = new ExcelProjectListener();
        // Read files, generate objects and clear files
        for (File currentFile : projectFiles) {
            if (currentFile.getName().endsWith(QualitisConstants.SUPPORT_SCALA_SUFFIX_NAME) || currentFile.getName().endsWith(QualitisConstants.SUPPORT_PYTHON_SUFFIX_NAME) || currentFile.getName().endsWith(QualitisConstants.SUPPORT_JAR_SUFFIX_NAME)) {
                udfFiles.add(currentFile);
                continue;
            }
            if (aomp && currentFile.getName().endsWith(QualitisConstants.SUPPORT_CONFIG_SUFFIX_NAME)) {
                Properties prop = new Properties();
                try (FileInputStream inputStream = new FileInputStream(currentFile)) {
                    prop.load(inputStream);
                    Set<String> propKeys = prop.stringPropertyNames();
                    List<DiffVariableRequest> diffVariableRequests = new ArrayList<>(propKeys.size());
                    for (String propKey : propKeys) {
                        diffVariableRequests.add(new DiffVariableRequest(propKey, prop.getProperty(propKey)));
                    }
                    request.setDiffVariableRequestList(diffVariableRequests);
                    continue;
                } catch (Exception e) {
                    LOGGER.error("Failed to upload projects, caused by: {}", e.getMessage(), e);
                }

            }
            if (!currentFile.getName().endsWith(QualitisConstants.SUPPORT_EXCEL_SUFFIX_NAME)) {
                continue;
            }
            try (InputStream currentFileInputStream = new FileInputStream(currentFile)) {
                listener = readExcel(currentFileInputStream, listener);
            } catch (Exception e) {
                LOGGER.error("Failed to upload projects, caused by: {}", e.getMessage(), e);
            } finally {
                if (currentFile.exists() && currentFile.getName().endsWith(QualitisConstants.SUPPORT_EXCEL_SUFFIX_NAME) && ProjectTransportTypeEnum.LOCAL.getCode().equals(request.getUploadType())) {
                    try {
                        Files.delete(currentFile.toPath());
                    } catch (IOException e) {
                        LOGGER.error("Failed to delete {}", currentFile.getName());
                    }
                }
            }
        }

        return uploadProjectsReal(listener, userName, StringUtils.isNotEmpty(request.getLoginUser()), request.getProjectId(), request.getDiffVariableRequestList(), udfFiles, operateComment);
    }

    @Override
    public GeneralResponse downloadProjectsToLocalOrGit(DownloadProjectRequest request, HttpServletResponse response) throws UnExpectedRequestException
            , PermissionDeniedRequestException, IOException {
        // Check Arguments
        DownloadProjectRequest.checkRequest(request);
        String loginUser = HttpUtils.getUserName(httpServletRequest);

        StringBuilder currProjectId = new StringBuilder();

        List<Project> projectsInDb = new ArrayList<>();
        for (Long projectId : request.getProjectId()) {
            Project projectInDb = projectDao.findById(projectId);
            if (projectInDb == null) {
                throw new UnExpectedRequestException("{&PROJECT_ID} : [" + projectId + "] {&DOES_NOT_EXIST}");
            }
            // Check permissions of project
            List<Integer> permissions = new ArrayList<>();
            permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
            projectService.checkProjectPermission(projectInDb, loginUser, permissions);
            if (StringUtils.isEmpty(currProjectId.toString())) {
                currProjectId.append(projectId.toString());
            }
            projectsInDb.add(projectInDb);
        }

        // Generate file in temp path and zip them
        String tmp = UuidGenerator.generate();
        String zipFileName = tmp + QualitisConstants.SUPPORT_ZIP_SUFFIX_NAME;

        StringBuilder tempDirForGenFiles = new StringBuilder();
        tempDirForGenFiles.append(linkisConfig.getUploadTmpPath()).append(File.separator).append(loginUser).append(File.separator).append(tmp);

        File forGenFilesDirFile = new File(tempDirForGenFiles.toString());
        if (!forGenFilesDirFile.exists()) {
            forGenFilesDirFile.mkdirs();
        }

        // Get excel content
        List<ExcelProject> excelProject = getExcelProject(projectsInDb);
        List<ExcelRuleMetric> excelRuleMetrics = getRuleMetric(projectsInDb);
        List<ExcelDatasourceEnv> excelDatasourceEnvs = getDataSourceSheet(projectsInDb);
        List<ExcelGroupByProject> excelGroupByProjects = getGroup(projectsInDb, request.getDiffVariableRequestList());
        List<ExcelRuleByProject> excelRuleByProject = getExcelRuleByProject(projectsInDb, request.getDiffVariableRequestList());
        List<ExcelExecutionParametersByProject> excelExecutionParametersByProject = getExecutionParameters(projectsInDb, Collections.emptyList(), false);

        generateFiles(forGenFilesDirFile, excelProject, excelRuleMetrics, excelGroupByProjects, excelRuleByProject, excelExecutionParametersByProject
                , request.getDiffVariableRequestList(), excelDatasourceEnvs);
        String operateComment;
        if (ProjectTransportTypeEnum.LOCAL.getCode().equals(request.getDownloadType())) {
            String zipFilePath = tempDirForGenFiles.toString() + QualitisConstants.SUPPORT_ZIP_SUFFIX_NAME;
            // Create a FileOutputStream to write the zip file
            FileOutputStream fos = new FileOutputStream(zipFilePath);
            // Create a ZipOutputStream to write the zip file
            ZipOutputStream zos = new ZipOutputStream(fos);

            // Traverse the source folder and add all files to the zip file
            addFolderToZip(tempDirForGenFiles.toString(), "", zos);
            // Close the ZipOutputStream and FileOutputStream
            zos.close();
            fos.close();

            // Output stream to download zip package and clear
            response.setContentType("application/octet-stream");
            zipFileName = URLEncoder.encode(zipFileName, "UTF-8");
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.addHeader("Content-Disposition", "attachment; filename*=UTF-8''" + zipFileName);

            // Create input stream from the file and output stream from the response
            try (InputStream inputStream = new FileInputStream(zipFilePath);
                 OutputStream outputStream = response.getOutputStream()) {
                // Set buffer size to 4KB
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    // Write buffer to output stream
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }
            File zipFile = new File(zipFilePath);
            if (zipFile.exists()) {
                LOGGER.info("Delete: {}", zipFile.getName());
                Files.delete(zipFile.toPath());
            }
            // Clear
            deleteDirectory(forGenFilesDirFile);
            operateComment = ProjectTransportTypeEnum.LOCAL.name();
        } else {
            throw new UnExpectedRequestException("Not support download type.");
        }

        projectEventService.recordBatch(projectsInDb, loginUser, operateComment, OperateTypeEnum.EXPORT_PROJECT);
        return new GeneralResponse<>("200", "SUCCESS", null);
    }

    private List<ExcelRuleMetric> getRuleMetric(List<Project> projectsInDb) throws IOException {
        List<ExcelRuleMetric> excelRuleMetrics = new ArrayList<>();
        for (Project project : projectsInDb) {
            List<Rule> rules = ruleDao.findByProject(project);
            List<RuleMetric> ruleMetrics = rules.stream().map(rule -> rule.getAlarmConfigs()).flatMap(alarmConfigs -> alarmConfigs.stream()).filter(alarmConfig -> alarmConfig.getRuleMetric() != null).map(alarmConfig -> alarmConfig.getRuleMetric()).distinct().collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(ruleMetrics)) {
                for (RuleMetric ruleMetric : ruleMetrics) {
                    ExcelRuleMetric excelRuleMetric = new ExcelRuleMetric();
                    excelRuleMetric.setRuleMetricJsonObject(objectMapper.writeValueAsString(ruleMetric));
                    List<DataVisibility> dataVisibilityList = dataVisibilityService.filter(ruleMetric.getId(), TableDataTypeEnum.RULE_METRIC);

                    if (CollectionUtils.isNotEmpty(dataVisibilityList)) {
                        excelRuleMetric.setDataVisibilityJsonObject(objectMapper.writeValueAsString(dataVisibilityList));
                    }
                    excelRuleMetrics.add(excelRuleMetric);
                }
            }
        }
        return excelRuleMetrics;
    }

    public void deleteDirectory(File directory) {// TODO: 文件被占用
        Path rootPath = Paths.get(directory.getPath());

        try {
            Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    LOGGER.info("Delete file: " + file.toString());
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    LOGGER.info("delete dir: " + dir.toString());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            LOGGER.error("Failed to delete dir: " + directory.getPath());
        }
    }

    @Override
    public GeneralResponse diffVariables() {
        List<DiffVariable> diffVariables = diffVariableRepository.findAll();
        return new GeneralResponse("200", "Success to list diff variables", diffVariables);
    }

    private void addFolderToZip(String folderPath, String parentFolderPath, ZipOutputStream zos) throws IOException {
        File folder = new File(folderPath);
        String folderName = folder.getName();

        // Create a new ZipEntry for the folder
        ZipEntry zipEntry = new ZipEntry(parentFolderPath + folderName + "/");

        // Add the ZipEntry to the ZipOutputStream
        zos.putNextEntry(zipEntry);

        // Traverse the folder and add all files to the zip file
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                addFolderToZip(file.getAbsolutePath(), parentFolderPath + folderName + "/", zos);
            } else {
                byte[] buffer = new byte[1024];
                try (FileInputStream fis = new FileInputStream(file)) {
                    zos.putNextEntry(new ZipEntry(parentFolderPath + folderName + "/" + file.getName()));
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }

            }
        }
    }

    private void generateFiles(File zipDirFile,
        List<ExcelProject> excelProject,
        List<ExcelRuleMetric> excelRuleMetrics,
        List<ExcelGroupByProject> excelGroupByProjects,
        List<ExcelRuleByProject> excelRulesByProject,
        List<ExcelExecutionParametersByProject> excelExecutionParametersByProject,
        List<DiffVariableRequest> diffVariableRequestList,
        List<ExcelDatasourceEnv> excelDatasourceEnvs) throws IOException {
        if (CollectionUtils.isNotEmpty(diffVariableRequestList)) {
            File projectDir = new File(zipDirFile, "project-info");
            if (!projectDir.exists()) {
                projectDir.mkdirs();
            }
            Properties prop = new Properties();
            File diffVariablesConfigFile = new File(projectDir, "diff_variables_config.properties");
            if (diffVariablesConfigFile.exists()) {
                Files.delete(diffVariablesConfigFile.toPath());
                LOGGER.info("Delete old diff variables config file {}", diffVariablesConfigFile.getName());
            }
            if (!diffVariablesConfigFile.exists()) {
                boolean newFile = diffVariablesConfigFile.createNewFile();
                if (!newFile) {
                    LOGGER.error("{&FAILED_TO_CREATE_NEW_FILE}");
                }

            }
            try (OutputStream output = new FileOutputStream(diffVariablesConfigFile)) {
                // 设置属性值
                for (DiffVariableRequest diffVariableRequest : diffVariableRequestList) {
                    prop.setProperty(diffVariableRequest.getName(), "[@" + diffVariableRequest.getName() + "]");
                }

                // 将属性写入文件
                prop.store(output, null);

                LOGGER.info("差异化变量配置文件已创建并写入完成！");
            } catch (IOException io) {
                LOGGER.error(io.getMessage(), io);
            }
        }
        if (CollectionUtils.isNotEmpty(excelProject)) {
            LOGGER.info("Start to write project excel");
            String fileName = "batch_project_export" + QualitisConstants.SUPPORT_EXCEL_SUFFIX_NAME;

            File projectDir = new File(zipDirFile, "project-info");
            if (!projectDir.exists()) {
                projectDir.mkdirs();
            }

            File projectExcel = new File(projectDir, fileName);
            if (!projectExcel.exists()) {
                boolean newFile = projectExcel.createNewFile();
                if (!newFile) {
                    LOGGER.error("{&FAILED_TO_CREATE_NEW_FILE}");
                }

            }
            // Write data to an Excel file
            ExcelWriter writer = null;
            try {
                writer = EasyExcelFactory.getWriter(new FileOutputStream(projectDir.getPath().concat(File.separator).concat(fileName)));
                Sheet sheet = new Sheet(1, 1, ExcelProject.class);
                sheet.setSheetName(ExcelSheetName.PROJECT_NAME);
                writer.write(excelProject, sheet);
                LOGGER.info("Finish to write project excel");
            } catch (FileNotFoundException e) {
                LOGGER.error("Failed to write project content to excel.");
            } finally {
                if (writer != null) {
                    writer.finish();
                }
            }
        }
        if (CollectionUtils.isNotEmpty(excelRuleMetrics)) {
            LOGGER.info("Start to write metric excel");
            String fileName = "batch_metric_export" + QualitisConstants.SUPPORT_EXCEL_SUFFIX_NAME;

            File projectDir = new File(zipDirFile, "project-ref-metric");
            if (!projectDir.exists()) {
                projectDir.mkdirs();
            }

            File projectExcel = new File(projectDir, fileName);
            if (!projectExcel.exists()) {
                boolean newFile = projectExcel.createNewFile();
                if (!newFile) {
                    LOGGER.error("{&FAILED_TO_CREATE_NEW_FILE}");
                }

            }
            // Write data to an Excel file
            ExcelWriter writer = null;
            try {
                writer = EasyExcelFactory.getWriter(new FileOutputStream(projectDir.getPath().concat(File.separator).concat(fileName)));
                Sheet sheet = new Sheet(1, 1, ExcelRuleMetric.class);
                sheet.setSheetName(ExcelSheetName.RULE_METRIC_NAME);
                writer.write(excelRuleMetrics, sheet);
                LOGGER.info("Finish to write metric excel");
            } catch (FileNotFoundException e) {
                LOGGER.error("Failed to write metric content to excel.");
            } finally {
                if (writer != null) {
                    writer.finish();
                }
            }
        }
        if (CollectionUtils.isNotEmpty(excelRulesByProject)) {
            LOGGER.info("Start to write rule excel");
            String fileName = "batch_rules_export" + QualitisConstants.SUPPORT_EXCEL_SUFFIX_NAME;

            File projectDir = new File(zipDirFile, "project-rule");
            if (!projectDir.exists()) {
                projectDir.mkdirs();
            }

            File projectExcel = new File(projectDir, fileName);
            if (!projectExcel.exists()) {
                boolean newFile = projectExcel.createNewFile();
                if (!newFile) {
                    LOGGER.error("{&FAILED_TO_CREATE_NEW_FILE}");
                }
            }
            // Write data to an Excel file
            ExcelWriter writer = null;
            try {
                writer = EasyExcelFactory.getWriter(new FileOutputStream(projectDir.getPath().concat(File.separator).concat(fileName)));
                Sheet sheetRule = new Sheet(1, 1, ExcelRuleByProject.class);
                sheetRule.setSheetName(ExcelSheetName.RULE_NAME);
                writer.write(excelRulesByProject, sheetRule);

                if (CollectionUtils.isNotEmpty(excelGroupByProjects)) {
                    LOGGER.info("Start to write group excel");
                    Sheet sheetGroup = new Sheet(2, 1, ExcelGroupByProject.class);
                    sheetGroup.setSheetName(ExcelSheetName.TABLE_GROUP);
                    writer.write(excelGroupByProjects, sheetGroup);
                    LOGGER.info("Finish to write group excel");
                }
                LOGGER.info("Finish to write rule excel");
            } catch (FileNotFoundException e) {
                LOGGER.error("Failed to write rule & group to excel.");
            } finally {
                if (writer != null) {
                    writer.finish();
                }
            }
        }
        if (CollectionUtils.isNotEmpty(excelExecutionParametersByProject)) {
            LOGGER.info("Start to write execution parameter excel");
            String fileName = "batch_execution_parameters_export" + QualitisConstants.SUPPORT_EXCEL_SUFFIX_NAME;

            File projectDir = new File(zipDirFile, "project-parameter");
            if (!projectDir.exists()) {
                projectDir.mkdirs();
            }

            File projectExcel = new File(projectDir, fileName);
            if (!projectExcel.exists()) {
                boolean newFile = projectExcel.createNewFile();
                if (!newFile) {
                    LOGGER.error("{&FAILED_TO_CREATE_NEW_FILE}");
                }
            }
            // Write data to an Excel file
            ExcelWriter writer = null;
            try {
                writer = EasyExcelFactory.getWriter(new FileOutputStream(projectDir.getPath().concat(File.separator).concat(fileName)));
                Sheet sheet = new Sheet(1, 1, ExcelExecutionParametersByProject.class);
                sheet.setSheetName(ExcelSheetName.EXECUTION_PARAMETERS_NAME);
                writer.write(excelExecutionParametersByProject, sheet);
                LOGGER.info("Finish to write execution parameter excel");
            } catch (FileNotFoundException e) {
                LOGGER.error("Failed to write execution parameter to excel.");
            } finally {
                if (writer != null) {
                    writer.finish();
                }
            }
        }
        if (CollectionUtils.isNotEmpty(excelDatasourceEnvs)) {
            LOGGER.info("Start to write datasource env excel");
            String fileName = "batch_datasource_env_export" + QualitisConstants.SUPPORT_EXCEL_SUFFIX_NAME;

            File projectDir = new File(zipDirFile, "project-cus-datasource");
            if (!projectDir.exists()) {
                projectDir.mkdirs();
            }

            File projectExcel = new File(projectDir, fileName);
            if (!projectExcel.exists()) {
                boolean newFile = projectExcel.createNewFile();
                if (!newFile) {
                    LOGGER.error("{&FAILED_TO_CREATE_NEW_FILE}");
                }
            }
            // Write data to an Excel file
            ExcelWriter writer = null;
            try {
                writer = EasyExcelFactory.getWriter(new FileOutputStream(projectDir.getPath().concat(File.separator).concat(fileName)));
                Sheet sheet = new Sheet(1, 1, ExcelDatasourceEnv.class);
                sheet.setSheetName(ExcelSheetName.DATASOURCE_ENV);
                writer.write(excelDatasourceEnvs, sheet);
                LOGGER.info("Finish to write datasource env excel");
            } catch (FileNotFoundException e) {
                LOGGER.error("Failed to write datasource env to excel.");
            } finally {
                if (writer != null) {
                    writer.finish();
                }
            }
        }
    }

    private List<File> checkFileAndUnzip(String zipPath) throws UnExpectedRequestException, IOException {
        List<File> extractedFileList = new ArrayList<>();
        if (StringUtils.isEmpty(zipPath) || !zipPath.endsWith(QualitisConstants.SUPPORT_ZIP_SUFFIX_NAME)) {
            throw new UnExpectedRequestException("Zip path is illegal");
        }

        File zipFile = new File(zipPath);
        if (!zipFile.exists()) {
            throw new UnExpectedRequestException("Zip file {&DOES_NOT_EXIST}");
        }

        // Unzip the zip file and get excels.
        try {
            extract(zipFile.getPath(), extractedFileList, zipFile.getParentFile().getPath());
        } catch (Exception e) {
            LOGGER.error("Failed to extracte zip files.");
            throw new UnExpectedRequestException("Failed to get zip files.");
        } finally {
            if (zipFile.exists()) {
                Files.delete(zipFile.toPath());
            }
        }

        return extractedFileList;
    }

    public void extract(String zipFilePath, List<File> extractedFileList, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }

        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath), Charset.forName("UTF-8"))) {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                String filePath = destDirectory + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    extractFile(zipIn, filePath);
                    extractedFileList.add(new File(filePath));
                } else {
                    File dir = new File(filePath);
                    dir.mkdir();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }

    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] bytesIn = new byte[4096];
            int read = 0;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }

    }



    public List<ExcelDatasourceEnv> getDataSourceSheet(List<Project> projects) {
        List<ExcelDatasourceEnv> excelRuleDataSourceList = Lists.newArrayList();
        try {
            List<RuleDataSource> allRuleDataSourceList = Lists.newArrayList();
            for (Project project : projects) {
                List<RuleDataSource> ruleDataSourceList = ruleDataSourceDao.findByProjectId(project.getId());
                if (CollectionUtils.isEmpty(ruleDataSourceList)) {
                    continue;
                }
                allRuleDataSourceList.addAll(ruleDataSourceList);
            }
//                导出规则依赖的数据源
            List<Long> linkisDataSourceIds = allRuleDataSourceList.stream().map(RuleDataSource::getLinkisDataSourceId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
            List<LinkisDataSource> linkisDataSourceInDbList = linkisDataSourceService.getByLinkisDataSourceIds(linkisDataSourceIds);

//                导出规则依赖的环境
            List<RuleDataSourceEnv> ruleDataSourceEnvList = ruleDatasourceEnvDao.findByRuleDataSourceList(allRuleDataSourceList);
//                将规则依赖的环境名称，填充到依赖的数据源中去，而不是直接导出数据源下的所有环境信息
            Map<String, List<RuleDataSourceEnv>> linkisDataSourceNameMap = ruleDataSourceEnvList.stream().collect(Collectors.groupingBy(ruleDataSourceEnv -> ruleDataSourceEnv.getRuleDataSource().getLinkisDataSourceName()));
            for (LinkisDataSource linkisDataSource : linkisDataSourceInDbList) {
                if (linkisDataSourceNameMap.containsKey(linkisDataSource.getLinkisDataSourceName())) {
                    List<RuleDataSourceEnv> ruleDataSourceEnvs = linkisDataSourceNameMap.get(linkisDataSource.getLinkisDataSourceName());
                    String ruleEnvs = StringUtils.join(ruleDataSourceEnvs.stream().map(RuleDataSourceEnv::getEnvName).distinct().collect(Collectors.toList()), SpecCharEnum.COMMA.getValue());
                    linkisDataSource.setEnvs(ruleEnvs);
                }
            }

//            设置部门可见范围
            List<Long> linkisDataSourcePrimaryIds = linkisDataSourceInDbList.stream().map(LinkisDataSource::getId).collect(Collectors.toList());
            List<DataVisibility> dataVisibilityList = dataVisibilityService.filterByIds(linkisDataSourcePrimaryIds, TableDataTypeEnum.LINKIS_DATA_SOURCE);
            Map<Long, List<DataVisibility>> dtaVisibilityListMap = dataVisibilityList.stream().collect(Collectors.groupingBy(DataVisibility::getTableDataId));

            for (LinkisDataSource linkisDataSource : linkisDataSourceInDbList) {
                ExcelDatasourceEnv excelDatasourceEnv = ExcelDatasourceEnv.fromLinkisDataSource(linkisDataSource, dtaVisibilityListMap.get(linkisDataSource.getId()));
                excelRuleDataSourceList.add(excelDatasourceEnv);
            }

        } catch (IOException e) {
            LOGGER.error("Failed to generate JSON of rule datasource", e);
        } catch (Exception e) {
            LOGGER.error("", e);
        }

        return excelRuleDataSourceList;
    }

    public void handleDataSource(User user, List<ExcelDatasourceEnv> excelRuleDataSourceList) throws UnExpectedRequestException {
        String operateTime = DateUtils.now();
        for (ExcelDatasourceEnv excelDatasourceEnv : excelRuleDataSourceList) {
            LinkisDataSource importLinkisDataSource = excelDatasourceEnv.getLinkisDataSource(objectMapper);
            if (null == importLinkisDataSource) {
                throw new UnExpectedRequestException("Failed to deserialize LinkisDataSource");
            }
            LinkisDataSource linkisDataSourceInDb = linkisDataSourceService.getByLinkisDataSourceName(importLinkisDataSource.getLinkisDataSourceName());

            List<String> newImportEnvs;
            if (null == linkisDataSourceInDb) {
                importLinkisDataSource.setCreateUser(user.getUsername());
                importLinkisDataSource.setCreateTime(operateTime);
                newImportEnvs = Arrays.asList(StringUtils.split(importLinkisDataSource.getEnvs(), SpecCharEnum.COMMA.getValue()));
            } else {
                importLinkisDataSource.setId(linkisDataSourceInDb.getId());
                importLinkisDataSource.setModifyUser(user.getUsername());
                importLinkisDataSource.setModifyTime(operateTime);
                importLinkisDataSource.setCreateUser(linkisDataSourceInDb.getCreateUser());
                importLinkisDataSource.setCreateTime(linkisDataSourceInDb.getCreateTime());
                newImportEnvs = extractNewEnvsFromImport(linkisDataSourceInDb, importLinkisDataSource);
                if (CollectionUtils.isEmpty(newImportEnvs)) {
                    importLinkisDataSource.setEnvs(linkisDataSourceInDb.getEnvs());
                }
            }

            Long linkisDataSourceId = pushDataSourceToLinkis(importLinkisDataSource);
            importLinkisDataSource.setLinkisDataSourceId(linkisDataSourceId);

            if (CollectionUtils.isNotEmpty(newImportEnvs)) {
                List<LinkisDataSourceEnvRequest> linkisDataSourceEnvRequestList = pushNewEnvsToLinkis(importLinkisDataSource, newImportEnvs);
                Map<Long, String> envIdAndNameMap = linkisDataSourceEnvRequestList.stream()
                        .collect(Collectors.toMap(LinkisDataSourceEnvRequest::getId, LinkisDataSourceEnvRequest::getEnvName, (oldVal, newVal) -> oldVal));
                String envs = linkisDataSourceService.formatEnvsField(envIdAndNameMap);
                if (Objects.nonNull(linkisDataSourceInDb) && StringUtils.isNotEmpty(linkisDataSourceInDb.getEnvs())) {
                    envs += SpecCharEnum.COMMA.getValue() + linkisDataSourceInDb.getEnvs();
                }
                importLinkisDataSource.setEnvs(envs);
                Long versionId = updateDataSourceParamWithEnvIdsToLinkis(importLinkisDataSource);
                importLinkisDataSource.setVersionId(versionId);
            }

            linkisDataSourceService.addOrModify(importLinkisDataSource);

//            在已有的部门可见范围的基础上，添加新的部门可见范围（如果有）
            List<DataVisibility> importDataVisibilityList = excelDatasourceEnv.getDataVisibilityList(objectMapper);
            if (CollectionUtils.isNotEmpty(importDataVisibilityList)) {
                List<DataVisibility> dataVisibilityListInDb = dataVisibilityService.filter(importLinkisDataSource.getId(), TableDataTypeEnum.LINKIS_DATA_SOURCE);
                List<String> deptNamesInDb = dataVisibilityListInDb.stream().map(DataVisibility::getDepartmentSubName).collect(Collectors.toList());
                List<DataVisibility> importNewDataVisibilityList = importDataVisibilityList.stream()
                        .filter(importDataVisibility -> !deptNamesInDb.contains(importDataVisibility.getDepartmentSubName()))
                        .map(importDataVisibility -> {
                            importDataVisibility.setTableDataId(importLinkisDataSource.getId());
                            return importDataVisibility;
                        }).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(importNewDataVisibilityList)) {
                    dataVisibilityDao.saveAll(importNewDataVisibilityList);
                }
            }
        }
    }

    /**
     *
     * @param importLinkisDataSource
     * @param newEnvs
     * @return 环境ID和环境名称组合而成的集合
     * @throws UnExpectedRequestException
     */
    public List<LinkisDataSourceEnvRequest> pushNewEnvsToLinkis(LinkisDataSource importLinkisDataSource, List<String> newEnvs) throws UnExpectedRequestException {
        List<LinkisDataSourceEnvRequest> linkisDataSourceEnvRequestList = newEnvs.stream().map(envName -> {
            LinkisDataSourceEnvRequest linkisDataSourceEnvRequest = new LinkisDataSourceEnvRequest();
            linkisDataSourceEnvRequest.setDataSourceTypeId(importLinkisDataSource.getDataSourceTypeId());
            linkisDataSourceEnvRequest.setEnvName(envName);
            return linkisDataSourceEnvRequest;
        }).collect(Collectors.toList());
        try {
            return linkisMetaDataManager.createDataSourceEnv(importLinkisDataSource.getInputType(), importLinkisDataSource.getVerifyType(), linkisDataSourceEnvRequestList, linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin());
        } catch (UnExpectedRequestException | MetaDataAcquireFailedException e) {
            LOGGER.error("Failed to create dataSource env", e);
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to create dataSource env", e);
            throw new UnExpectedRequestException("Failed to create dataSource env");
        }
    }

    private Long updateDataSourceParamWithEnvIdsToLinkis(LinkisDataSource importLinkisDataSource) throws UnExpectedRequestException {
        try {
//            modify parameter of DataSource, establish link between env_id and dataSource
            ModifyDataSourceParameterRequest modifyDataSourceParameterRequest = new ModifyDataSourceParameterRequest();
            modifyDataSourceParameterRequest.setLinkisDataSourceId(importLinkisDataSource.getLinkisDataSourceId());
            modifyDataSourceParameterRequest.setComment("Update");
            LinkisDataSourceInfoDetail linkisDataSourceInfoDetail = metaDataClient.getDataSourceInfoById(linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin(), importLinkisDataSource.getLinkisDataSourceId());
            modifyDataSourceParameterRequest.setConnectParams(linkisDataSourceInfoDetail.getConnectParams());
            List<String> envIdArray = linkisDataSourceService.getEnvNameAndIdMap(importLinkisDataSource).values().stream().map(String::valueOf).collect(Collectors.toList());
            modifyDataSourceParameterRequest.setEnvIdArray(envIdArray);
            LinkisDataSourceParamsResponse linkisDataSourceParamsResponse = linkisMetaDataManager.modifyDataSourceParams(modifyDataSourceParameterRequest, linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin());
            return linkisDataSourceParamsResponse.getVersionId();
        } catch (UnExpectedRequestException | MetaDataAcquireFailedException e) {
            LOGGER.error("Failed to update dataSource param", e);
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to update dataSource param", e);
            throw new UnExpectedRequestException("Failed to update dataSource param");
        }
    }

    /**
     * 将导入的DataSource中修改的项目基础信息
     *
     * @param importLinkisDataSource
     * @return
     */
    private Long pushDataSourceToLinkis(LinkisDataSource importLinkisDataSource) throws UnExpectedRequestException {
        try {
//            覆盖或添加数据基础信息
            LinkisDataSourceRequest linkisDataSourceRequest = buildLinkisDataSourceRequest(importLinkisDataSource);
            if (null != importLinkisDataSource.getLinkisDataSourceId()) {
                return linkisMetaDataManager.modifyDataSource(linkisDataSourceRequest, linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin());
            } else {
                try {
                    return linkisMetaDataManager.createDataSource(linkisDataSourceRequest, linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin());
                } catch (MetaDataAcquireFailedException e) {
                    LOGGER.error("Failed to create LinkisDataSource", e);
                    LOGGER.info("Query DataSource by name: {}", importLinkisDataSource.getLinkisDataSourceName());
                    GeneralResponse<Map<String, Object>> generalResponse = metaDataClient.getDataSourceInfoDetailByName(linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin(), importLinkisDataSource.getLinkisDataSourceName());
                    Map<String, Object> dataMap = generalResponse.getData();
                    if (Objects.nonNull(dataMap) && dataMap.containsKey("info")) {
                        Map<String, Object> dataSourceInfo = ((Map) dataMap.get("info"));
                        return Long.valueOf((Integer) dataSourceInfo.get("id"));
                    }
                    LOGGER.info("DataSource is not existed, name: {}", importLinkisDataSource.getLinkisDataSourceName());
                }
            }
        } catch (JSONException e) {
            throw new UnExpectedRequestException("Failed to convert json format");
        } catch (MetaDataAcquireFailedException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            throw new UnExpectedRequestException("Failed to push DataSource to Linkis, exception: " + e.getMessage());
        }
        throw new UnExpectedRequestException("Failed to push DataSource to Linkis, dataSource name: " + importLinkisDataSource.getLinkisDataSourceName());
    }

    private LinkisDataSourceRequest buildLinkisDataSourceRequest(LinkisDataSource importLinkisDataSource) throws Exception {
        Map<String, Object> connectParams = new HashMap<>();
        LinkisDataSourceInfoDetail linkisDataSourceInfoDetail = metaDataClient.getDataSourceInfoById(linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin(), importLinkisDataSource.getLinkisDataSourceId());
        Map<String, Object> remoteConnectParams = linkisDataSourceInfoDetail.getConnectParams();
//        如果导出环境和导入环境都是共享登录，则保留导入环境的共享认证信息
        if (Boolean.TRUE.equals(remoteConnectParams.get("share")) && Integer.valueOf(QualitisConstants.DATASOURCE_MANAGER_VERIFY_TYPE_SHARE).equals(importLinkisDataSource.getVerifyType())) {
            String authType = String.valueOf(remoteConnectParams.get("authType"));
            if (QualitisConstants.AUTH_TYPE_ACCOUNT_PWD.equals(authType)) {
                connectParams.put("username", remoteConnectParams.get("username"));
                connectParams.put("password", remoteConnectParams.get("password"));
            } else if (QualitisConstants.AUTH_TYPE_DPM.equals(authType)) {
                connectParams.put("appid", remoteConnectParams.get("appid"));
                connectParams.put("objectid", remoteConnectParams.get("objectid"));
                connectParams.put("mkPrivate", remoteConnectParams.get("mkPrivate"));
            }
        }
        connectParams.put("share", Integer.valueOf(QualitisConstants.DATASOURCE_MANAGER_VERIFY_TYPE_SHARE).equals(importLinkisDataSource.getVerifyType()));
        connectParams.put("dcn", Integer.valueOf(QualitisConstants.DATASOURCE_MANAGER_INPUT_TYPE_AUTO).equals(importLinkisDataSource.getInputType()));
        connectParams.put("multi_env", true);
        connectParams.put("subSystem", importLinkisDataSource.getSubSystem());

        LinkisDataSourceRequest linkisDataSourceRequest = new LinkisDataSourceRequest();
        linkisDataSourceRequest.setLinkisDataSourceId(importLinkisDataSource.getLinkisDataSourceId());
        linkisDataSourceRequest.setDataSourceTypeId(importLinkisDataSource.getDataSourceTypeId());
        linkisDataSourceRequest.setLabels(importLinkisDataSource.getLabels());
        linkisDataSourceRequest.setDataSourceName(importLinkisDataSource.getLinkisDataSourceName());
        linkisDataSourceRequest.setDataSourceDesc(importLinkisDataSource.getDatasourceDesc());
        linkisDataSourceRequest.setConnectParams(connectParams);
        return linkisDataSourceRequest;
    }

    private List<String> extractNewEnvsFromImport(LinkisDataSource linkisDataSourceInDb, LinkisDataSource importLinkisDataSource) {
        List<String> originalEnvNameInDbList = linkisDataSourceService.getOriginalEnvNameList(linkisDataSourceInDb);
        List<String> importOriginalEnvNameList = linkisDataSourceService.getOriginalEnvNameList(importLinkisDataSource);
        return importOriginalEnvNameList.stream()
                .filter(originalEnvName -> !originalEnvNameInDbList.contains(originalEnvName) && StringUtils.isNotBlank(originalEnvName))
                .distinct()
                .map(originalEnvName -> linkisDataSourceService.convertOriginalEnvNameToLinkis(importLinkisDataSource.getLinkisDataSourceId()
                        , originalEnvName, importLinkisDataSource.getInputType(), StringUtils.EMPTY))
                .collect(Collectors.toList());
    }

}
