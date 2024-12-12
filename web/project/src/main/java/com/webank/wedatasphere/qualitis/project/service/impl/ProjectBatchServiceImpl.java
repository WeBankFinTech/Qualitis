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

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.constant.RuleMetricLevelEnum;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDao;
import com.webank.wedatasphere.qualitis.dao.RuleMetricDepartmentUserDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.entity.RuleMetricDepartmentUser;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserRole;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.ExcelSheetName;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.dao.repository.ProjectRepository;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectLabel;
import com.webank.wedatasphere.qualitis.project.excel.*;
import com.webank.wedatasphere.qualitis.project.request.AddProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.DownloadProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.ModifyProjectDetailRequest;
import com.webank.wedatasphere.qualitis.project.service.ProjectBatchService;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.constant.RoleDefaultTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.exception.WriteExcelException;
import com.webank.wedatasphere.qualitis.rule.service.RuleBatchService;
import com.webank.wedatasphere.qualitis.service.RoleService;
import com.webank.wedatasphere.qualitis.submitter.impl.ExecutionManagerImpl;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.project.excel.ExcelCustomRuleByProject;
import com.webank.wedatasphere.qualitis.project.excel.ExcelMultiTemplateRuleByProject;
import com.webank.wedatasphere.qualitis.project.excel.ExcelProjectListener;
import com.webank.wedatasphere.qualitis.project.excel.ExcelTemplateRuleByProject;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.stream.Collectors;
import javax.management.relation.RoleNotFoundException;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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

/**
 * @author howeye
 */
@Service
public class ProjectBatchServiceImpl implements ProjectBatchService {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private RuleMetricDao ruleMetricDao;
    @Autowired
    private RuleMetricDepartmentUserDao ruleMetricDepartmentUserDao;

    @Autowired
    private RuleBatchService ruleBatchService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private LinkisConfig linkisConfig;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectBatchServiceImpl.class);
    private static final String SUPPORT_EXCEL_SUFFIX_NAME = ".xlsx";

    private final FastDateFormat FILE_DATE_FORMATTER = FastDateFormat.getInstance("yyyyMMddHHmmss");

    private HttpServletRequest httpServletRequest;

    public ProjectBatchServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> uploadProjects(InputStream fileInputStream, FormDataContentDisposition fileDisposition)
        throws UnExpectedRequestException,
        IOException, PermissionDeniedRequestException, RoleNotFoundException {
        String userName = HttpUtils.getUserName(httpServletRequest);
        String fileName = fileDisposition.getFileName();
        return uploadProjectsReal(fileInputStream, fileName, userName, false);

    }

    private GeneralResponse<?> uploadProjectsReal(InputStream fileInputStream, String fileName, String userName, boolean aomp)
        throws IOException, UnExpectedRequestException, PermissionDeniedRequestException, RoleNotFoundException {
        String suffixName = fileName.substring(fileName.lastIndexOf('.'));
        if (! suffixName.equals(SUPPORT_EXCEL_SUFFIX_NAME)) {
            throw new UnExpectedRequestException("{&DO_NOT_SUPPORT_SUFFIX_NAME}: [" + suffixName + "]. {&ONLY_SUPPORT} [" + SUPPORT_EXCEL_SUFFIX_NAME + "]", 422);
        }

        if (userName == null) {
            return new GeneralResponse<>("401", "{&PLEASE_LOGIN}", null);
        }
        User user = userDao.findByUsername(userName);
        Long userId = user.getId();

        // Read file and create project
        ExcelProjectListener listener = readExcel(fileInputStream);

        // Check if excel file is empty
        if (listener.getExcelProjectContent().isEmpty() && listener.getExcelRuleContent().isEmpty()
            && listener.getExcelCustomRuleContent().isEmpty() && listener.getExcelMultiRuleContent().isEmpty()
            && listener.getTemplateFileExcelContent().isEmpty() && listener.getExcelMetricContent().isEmpty()) {
            throw new UnExpectedRequestException("{&FILE_CAN_NOT_BE_EMPTY_OR_FILE_CAN_NOT_BE_RECOGNIZED}", 422);
        }

        for (ExcelProject excelProject : listener.getExcelProjectContent()) {
            Project project = projectDao.findByNameAndCreateUser(excelProject.getProjectName(), userName);

            if (project != null) {
                if (! aomp) {
                    LOGGER.info("hint for user to decide to override or not.");
                }
                // Means update project.
                LOGGER.info("Start to update project[name={}] with upload project file.", project.getName());
                ModifyProjectDetailRequest request = convertExcelProjectToModifyProjectRequest(excelProject, project, userName);
                projectService.modifyProjectDetail(request, false);
            } else {
                // Check excel project arguments is valid or not
                AddProjectRequest request = convertExcelProjectToAddProjectRequest(excelProject);
                projectService.addProject(request, userId);
            }
        }

        for (ExcelRuleMetric excelRuleMetric : listener.getExcelMetricContent()) {
            RuleMetric ruleMetric = ruleMetricDao.findByName(excelRuleMetric.getName());
            if (ruleMetric != null) {
                if (! aomp) {
                    LOGGER.info("hint for user to decide to override or not.");
                }
                LOGGER.info("Start to update rule metric[name={}] with upload rule metric file.", ruleMetric.getName());
                modifyRuleMetric(excelRuleMetric, ruleMetric, userName);
            } else {
                addRuleMetric(excelRuleMetric, userName);
            }
        }
        // Create rules according to excel sheet
        Map<String, Map<String, List<ExcelTemplateRuleByProject>>> excelTemplateRulePartitionedByProject = listener.getExcelRuleContent();
        Map<String, Map<String, List<ExcelCustomRuleByProject>>> excelCustomRulePartitionedByProject = listener.getExcelCustomRuleContent();
        Map<String, Map<String, List<ExcelMultiTemplateRuleByProject>>> excelMultiTemplateRulePartitionedByProject = listener.getExcelMultiRuleContent();
        Map<String, Map<String, List<ExcelTemplateFileRuleByProject>>> excelTemplateFileRulePartitionedByProject = listener.getTemplateFileExcelContent();
        Set<String> allProjects = new HashSet<>();
        allProjects.addAll(excelTemplateRulePartitionedByProject.keySet());
        allProjects.addAll(excelCustomRulePartitionedByProject.keySet());
        allProjects.addAll(excelMultiTemplateRulePartitionedByProject.keySet());
        allProjects.addAll(excelTemplateFileRulePartitionedByProject.keySet());

        for (String projectName : allProjects) {
            try {
                Project projectInDb = projectDao.findByNameAndCreateUser(projectName, userName);
                if (projectInDb == null) {
                    throw new UnExpectedRequestException("{&PROJECT}: [" + projectName + "] {&DOES_NOT_EXIST}");
                }
                ruleBatchService.getAndSaveRule(excelTemplateRulePartitionedByProject.get(projectName), excelCustomRulePartitionedByProject.get(projectName),
                    excelMultiTemplateRulePartitionedByProject.get(projectName), excelTemplateFileRulePartitionedByProject.get(projectName), projectInDb,
                    userName, aomp);
            } catch (Exception e) {
                throw new UnExpectedRequestException(e.getMessage());
            }
        }

        fileInputStream.close();
        return new GeneralResponse<>("200", "{&SUCCEED_TO_UPLOAD_FILE}", null);
    }

    private void modifyRuleMetric(ExcelRuleMetric excelRuleMetric, RuleMetric ruleMetric, String userName)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check en code existence.
        RuleMetric ruleMetricInDb = ruleMetricDao.findByEnCode(excelRuleMetric.getEnCode());
        if (ruleMetricInDb != null && ruleMetricInDb.getId().longValue() != ruleMetric.getId().longValue()) {
            throw new UnExpectedRequestException("Rule Metric [EN_CODE=" + excelRuleMetric.getEnCode() + "] {&DOES_NOT_EXIST}");
        }
        LOGGER.info("Start to modify rule metric, modify request: [{}], user: [{}]", excelRuleMetric.toString(), userName);
        User loginUser = userDao.findByUsername(userName);
        List<UserRole> userRoles =  userRoleDao.findByUser(loginUser);
        Integer roleType = roleService.getRoleType(userRoles);

        if (roleType.equals(RoleDefaultTypeEnum.ADMIN.getCode())) {
            LOGGER.info("First level(created by SYS_ADMIN) indicator will be modified soon.");
        } else if (roleType.equals(RoleDefaultTypeEnum.DEPARTMENT_ADMIN.getCode())){
            LOGGER.info("Second level(created by DEPARTMENT_ADMIN) indicator will be modified soon.");

            if (ruleMetric.getLevel().equals(RuleMetricLevelEnum.DEFAULT_METRIC.getCode())) {
                throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
            }
            List<Department> managedDepartment = new ArrayList<>();
            for (UserRole userRole : userRoles) {
                Department department = userRole.getRole().getDepartment();
                if (department != null) {
                    managedDepartment.add(department);
                }
            }

            RuleMetricDepartmentUser ruleMetricDepartmentUser = ruleMetricDepartmentUserDao.findByRuleMetric(ruleMetric);
            if (ruleMetricDepartmentUser != null && managedDepartment.contains(ruleMetricDepartmentUser.getDepartment())) {
                LOGGER.info("Rule metric[{}] comes from department: {}", ruleMetric.toString(), ruleMetricDepartmentUser.getDepartment().getName());
            } else {
                throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
            }
        } else {
            LOGGER.info("Third level(created by PROJECTOR) indicator will be modified soon.");

            if (! ruleMetric.getLevel().equals(RuleMetricLevelEnum.PERSONAL_METRIC.getCode())
                || ! ruleMetric.getCreateUser().equals(loginUser.getUserName())) {
                throw new PermissionDeniedRequestException("User {&HAS_NO_PERMISSION_TO_ACCESS}", 403);
            }
        }
        ruleMetric.setName(excelRuleMetric.getName());
        ruleMetric.setMetricDesc(excelRuleMetric.getMetricDesc());
        ruleMetric.setSubSystemName(excelRuleMetric.getSubSystemName());
        ruleMetric.setFullCnName(excelRuleMetric.getFullCnName());
        ruleMetric.setModifyUser(userName);
        ruleMetric.setModifyTime(ExecutionManagerImpl.PRINT_TIME_FORMAT.format(new Date()));
        ruleMetric.setType(excelRuleMetric.getType());
        ruleMetric.setFrequency(Integer.parseInt(excelRuleMetric.getFrequency()));
        ruleMetric.setEnCode(excelRuleMetric.getEnCode());
        ruleMetric.setDepartmentName(excelRuleMetric.getDepartmentName());
        ruleMetric.setDevDepartmentName(excelRuleMetric.getDevDepartmentName());
        ruleMetric.setOpsDepartmentName(excelRuleMetric.getOpsDepartmentName());
        ruleMetric.setAvailable(excelRuleMetric.getAvailable());
        ruleMetricDao.add(ruleMetric);
    }

    private ModifyProjectDetailRequest convertExcelProjectToModifyProjectRequest(ExcelProject excelProject,
        Project project, String userName) {
        // Construct ModifyProjectDetailRequest
        ModifyProjectDetailRequest request = new ModifyProjectDetailRequest();
        request.setUsername(userName);
        request.setProjectId(project.getId());
        request.setCnName(excelProject.getProjectChName());
        request.setProjectName(excelProject.getProjectName());
        String projectLabels = excelProject.getProjectLabels();
        request.setDescription(excelProject.getProjectDescription());

        if (projectLabels != null && projectLabels.length() > 0) {
            String[] labels = projectLabels.split(" ");
            Set<String> labelSet = new HashSet<>();
            for (String label : labels) {
                labelSet.add(label);
            }
            request.setProjectLabelStrs(labelSet);
        }

        return request;
    }

    private void addRuleMetric(ExcelRuleMetric excelRuleMetric, String userName) throws UnExpectedRequestException {
        RuleMetric ruleMetricInDb = ruleMetricDao.findByEnCode(excelRuleMetric.getEnCode());
        if (ruleMetricInDb != null) {
            throw new UnExpectedRequestException("En code + [" + ruleMetricInDb.getEnCode() + "] +  of metric {&ALREADY_EXIST}");
        }
        LOGGER.info("Start to add rule metric, user: [{}]", userName);
        User loginUser = userDao.findByUsername(userName);

        List<UserRole> userRoles =  userRoleDao.findByUser(loginUser);
        Integer roleType = roleService.getRoleType(userRoles);
        RuleMetric newRuleMetric = new RuleMetric();
        newRuleMetric.setCreateUser(userName);
        newRuleMetric.setName(excelRuleMetric.getName());
        newRuleMetric.setCnName(excelRuleMetric.getChName());
        newRuleMetric.setEnCode(excelRuleMetric.getEnCode());
        newRuleMetric.setMetricDesc(excelRuleMetric.getMetricDesc());
        newRuleMetric.setCreateTime(ExecutionManagerImpl.PRINT_TIME_FORMAT.format(new Date()));

        RuleMetric savedRuleMetric = ruleMetricDao.add(newRuleMetric);
        if (roleType.equals(RoleDefaultTypeEnum.ADMIN.getCode())) {
            LOGGER.info("First level(created by SYS_ADMIN) indicator will be created soon.");
            savedRuleMetric.setLevel(RuleMetricLevelEnum.DEFAULT_METRIC.getCode());
        } else if (roleType.equals(RoleDefaultTypeEnum.DEPARTMENT_ADMIN.getCode())){
            LOGGER.info("Second level(created by DEPARTMENT_ADMIN) indicator will be created soon.");
            savedRuleMetric.setLevel(RuleMetricLevelEnum.DEPARTMENT_METRIC.getCode());
            for (UserRole temp : userRoles) {
                Department department = temp.getRole().getDepartment();
                if (department != null) {
                    RuleMetricDepartmentUser ruleMetricDepartmentUser = new RuleMetricDepartmentUser();
                    ruleMetricDepartmentUser.setDepartment(department);
                    ruleMetricDepartmentUser.setRuleMetric(savedRuleMetric);
                    ruleMetricDepartmentUserDao.add(ruleMetricDepartmentUser);
                    LOGGER.info("Succeed to save rule metric department user.");
                }
            }
        } else {
            LOGGER.info("Third level(created by PROJECTOR) indicator will be created soon.");
            savedRuleMetric.setLevel(RuleMetricLevelEnum.PERSONAL_METRIC.getCode());
            RuleMetricDepartmentUser ruleMetricDepartmentUser = new RuleMetricDepartmentUser();
            ruleMetricDepartmentUser.setDepartment(loginUser.getDepartment());
            ruleMetricDepartmentUser.setRuleMetric(savedRuleMetric);
            ruleMetricDepartmentUser.setUser(loginUser);
            ruleMetricDepartmentUserDao.add(ruleMetricDepartmentUser);
            LOGGER.info("Succeed to save rule metric department user.");
        }
        savedRuleMetric.setBussCode(Integer.parseInt(excelRuleMetric.getDimension()));
        savedRuleMetric.setCreateUser(userName);

        savedRuleMetric.setFrequency(Integer.parseInt(excelRuleMetric.getFrequency()));
        savedRuleMetric.setDevDepartmentName(excelRuleMetric.getDevDepartmentName());
        savedRuleMetric.setOpsDepartmentName(excelRuleMetric.getOpsDepartmentName());
        savedRuleMetric.setDepartmentName(excelRuleMetric.getDepartmentName());
        savedRuleMetric.setSubSystemName(excelRuleMetric.getSubSystemName());
        savedRuleMetric.setProductName(excelRuleMetric.getProductName());
        savedRuleMetric.setFullCnName(excelRuleMetric.getFullCnName());
        savedRuleMetric.setBussCustom(excelRuleMetric.getBussCustom());
        savedRuleMetric.setAvailable(excelRuleMetric.getAvailable());
        savedRuleMetric.setType(excelRuleMetric.getType());

        ruleMetricDao.add(savedRuleMetric);
    }

    private AddProjectRequest convertExcelProjectToAddProjectRequest(ExcelProject excelProject) {
        // Construct AddProjectRequest
        AddProjectRequest addProjectRequest = new AddProjectRequest();

        addProjectRequest.setCnName(excelProject.getProjectChName());
        addProjectRequest.setProjectName(excelProject.getProjectName());
        addProjectRequest.setDescription(excelProject.getProjectDescription());
        String projectLabels = excelProject.getProjectLabels();
        if (projectLabels != null && projectLabels.length() > 0) {
            String[] labels = projectLabels.split(" ");
            Set<String> labelSet = new HashSet<>();
            for (String label : labels) {
                labelSet.add(label);
            }
            addProjectRequest.setProjectLabels(labelSet);
        }

        return addProjectRequest;
    }

    private ExcelProjectListener readExcel(InputStream inputStream) {
        LOGGER.info("Start to read project excel");
        ExcelProjectListener listener = new ExcelProjectListener();
        ExcelReader excelReader = new ExcelReader(inputStream, null, listener);
        List<Sheet> sheets = excelReader.getSheets();
        for (Sheet sheet : sheets) {
            if (sheet.getSheetName().equals(ExcelSheetName.TEMPLATE_RULE_NAME)) {
                sheet.setClazz(ExcelTemplateRuleByProject.class);
                sheet.setHeadLineMun(1);
                excelReader.read(sheet);
            } else if (sheet.getSheetName().equals(ExcelSheetName.PROJECT_NAME)) {
                sheet.setClazz(ExcelProject.class);
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
            } else if (sheet.getSheetName().equals(ExcelSheetName.RULE_METRIC_NAME)) {
                sheet.setClazz(ExcelRuleMetric.class);
                sheet.setHeadLineMun(1);
                excelReader.read(sheet);
            }
        }

        LOGGER.info("Finish to read project excel. excel content: rule sheet {}, project sheet {}", listener.getExcelRuleContent(), listener.getExcelProjectContent());
        return listener;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> downloadProjects(DownloadProjectRequest request, HttpServletResponse response) throws UnExpectedRequestException, WriteExcelException, IOException, PermissionDeniedRequestException {
        // Check Arguments
        DownloadProjectRequest.checkRequest(request);
        String loginUser = HttpUtils.getUserName(httpServletRequest);

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
            projectsInDb.add(projectInDb);
        }

        // Write project and rules
        return writeProjectAndRules(projectsInDb, response);
    }

    @Override
    public GeneralResponse<?> outerUploadProjects(InputStream fileInputStream, String fileName, String userName)
        throws IOException, UnExpectedRequestException, PermissionDeniedRequestException, RoleNotFoundException {
        return uploadProjectsReal(fileInputStream, fileName, userName, true);
    }

    @Override
    public GeneralResponse<?> uploadProjectRulesAndRuleMetrics(InputStream fileInputStream, FormDataContentDisposition fileDisposition, String userName) {
        StringBuffer zipFilePath = new StringBuffer();
        zipFilePath.append(linkisConfig.getUploadTmpPath()).append(File.separator)
            .append(userName).append(File.separator).append(UuidGenerator.generate()).append(File.separator);
        String destUnzipPath = zipFilePath.toString();

        File zipFileDir = new File(destUnzipPath);
        if (! zipFileDir.exists()) {
            zipFileDir.mkdirs();
        }

        File zipFile = new File(zipFilePath.append(fileDisposition.getFileName()).toString());
        if (! zipFile.exists()) {
            try {
                zipFile.createNewFile();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
                return new GeneralResponse<>("500", "{&FAILED_TO_UPLOAD_PROJECTS}, caused by: " + e.getMessage(), null);
            }
        }
        // Decompress directory
        File destDir = new File(destUnzipPath);
        List<File> extractedFileList= new ArrayList<>();

        // Unzip the zip file and get excels.
        try {
            unzipAndGet(zipFile, extractedFileList, destUnzipPath, destDir, fileInputStream);
        } catch (Exception e) {
            return new GeneralResponse<>("500", "{&FAILED_TO_UPLOAD_PROJECTS}, caused by: " + e.getMessage(), null);
        }

        // Request upload api one by one according to excel type with name.
        StringBuffer responseErrorExecl = new StringBuffer();
        responseErrorExecl.append("Upload error files: ");
        List<String> errorExcelNames = new ArrayList<>();
        for (File currentFile : extractedFileList) {
            try {
                if (currentFile.getName().contains("metrics") || currentFile.getName().contains("project")) {
                    // TODO: Metric import issue.
                    outerUploadProjects(new FileInputStream(currentFile), currentFile.getName(), userName);
                } else {
                    ruleBatchService.outerUploadRules(new FileInputStream(currentFile), currentFile.getName(), userName);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to upload projects, caused by: {}", e.getMessage(), e);
                errorExcelNames.add(currentFile.getName());
            } finally {
                currentFile.delete();
            }
        }
        zipFile.delete();
        destDir.delete();
        if (CollectionUtils.isNotEmpty(errorExcelNames)) {
            responseErrorExecl.append(StringUtils.join(errorExcelNames, ","));
            return new GeneralResponse<>("500", "{&FINISH_TO_UPLOAD_FILE}", responseErrorExecl.toString());
        } else {
            return new GeneralResponse<>("200", "{&SUCCEED_TO_UPLOAD_FILE}", extractedFileList.stream().map(File::getName).collect(Collectors.toList()));
        }
    }

    private void unzipAndGet(File zipFile, List<File> extractedFileList, String destUnzipPath, File destDir, InputStream fileInputStream) throws UnExpectedRequestException {
        OutputStream out = null;

        try {
            int read = 0;
            byte[] bytes = new byte[1024];
            out = new FileOutputStream(zipFile);
            while ((read = fileInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UnExpectedRequestException("{&FAILED_TO_UPLOAD_PROJECTS}, caused by: " + e.getMessage());
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
                zipFile.delete();
                throw new UnExpectedRequestException("{&FAILED_TO_UPLOAD_PROJECTS}, caused by: " + e.getMessage());
            }
        }


        // First, create a. zip file that points to the disk.
        ZipFile zFile = new ZipFile(zipFile);

        try {
            // Extract the file to the unzip directory
            zFile.extractAll(destUnzipPath);
            List<FileHeader> headerList = zFile.getFileHeaders();
            for (FileHeader fileHeader : headerList) {
                if (! fileHeader.isDirectory()) {
                    extractedFileList.add(new File(destDir, fileHeader.getFileName()));
                }
            }

        } catch (ZipException e) {
            LOGGER.error(e.getMessage(), e);
            zipFile.delete();
            destDir.delete();
            throw new UnExpectedRequestException("{&FAILED_TO_UPLOAD_PROJECTS}, caused by: " + e.getMessage());
        }
    }

    private GeneralResponse<?> writeProjectAndRules(List<Project> projects, HttpServletResponse response) throws IOException, WriteExcelException {
        String fileName = "batch_project_export_" + FILE_DATE_FORMATTER.format(new Date()) + SUPPORT_EXCEL_SUFFIX_NAME;
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment;filename*=UTF-8''" + fileName);
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        String localeStr = httpServletRequest.getHeader("Content-Language");

        OutputStream outputStream = response.getOutputStream();
        List<ExcelProject> excelProject = getExcelProject(projects);
        List<ExcelTemplateRuleByProject> excelTemplateRuleByProject = getExcelRuleByProject(projects, localeStr);
        List<ExcelCustomRuleByProject> excelCustomRuleByProject = getExcelCustomRuleByProject(projects, localeStr);
        List<ExcelTemplateFileRuleByProject> excelTemplateFileRuleByProject = getExcelTemplateFileRuleByProject(projects, localeStr);
        List<ExcelMultiTemplateRuleByProject> excelMultiTemplateRuleByProject = getExcelMultiTemplateRuleByProject(projects, localeStr);
        writeExcelToOutput(excelProject, excelTemplateRuleByProject, excelCustomRuleByProject, excelMultiTemplateRuleByProject, excelTemplateFileRuleByProject, outputStream);
        outputStream.flush();
        LOGGER.info("Succeed to download all projects in type of excel");
        return null;
    }

    private List<ExcelTemplateFileRuleByProject> getExcelTemplateFileRuleByProject(List<Project> projects, String localeStr) {
        List<ExcelTemplateFileRuleByProject> excelTemplateFileRuleByProjects = new ArrayList<>();
        for (Project project : projects) {
            List<Rule> rules = ruleDao.findByProject(project);
            List<ExcelTemplateFileRuleByProject> excelTemplateFileRules = ruleBatchService.getFileRule(rules, localeStr);
            for (ExcelTemplateFileRuleByProject excelTemplateFileRule : excelTemplateFileRules) {
                ExcelTemplateFileRuleByProject excelTemplateFileRuleByProject = new ExcelTemplateFileRuleByProject();
                BeanUtils.copyProperties(excelTemplateFileRule, excelTemplateFileRuleByProject);
                excelTemplateFileRuleByProject.setProjectName(project.getName());

                LOGGER.info("Collect excel line of template rule: {}", excelTemplateFileRuleByProject);
                excelTemplateFileRuleByProjects.add(excelTemplateFileRuleByProject);
            }
        }
        return excelTemplateFileRuleByProjects;
    }

    private void writeExcelToOutput(List<ExcelProject> excelProjects, List<ExcelTemplateRuleByProject> excelTemplateRuleByProjects,
        List<ExcelCustomRuleByProject> excelCustomRuleByProjects, List<ExcelMultiTemplateRuleByProject> excelMultiTemplateRuleByProjects,
        List<ExcelTemplateFileRuleByProject> excelTemplateFileRuleByProject, OutputStream outputStream) throws WriteExcelException, IOException {
        try {
            LOGGER.info("Start to write excel");
            ExcelWriter writer = new ExcelWriter(outputStream, ExcelTypeEnum.XLSX, true);
            Sheet templateRuleSheet = new Sheet(1, 0, ExcelTemplateRuleByProject.class);
            templateRuleSheet.setSheetName(ExcelSheetName.TEMPLATE_RULE_NAME);
            writer.write(excelTemplateRuleByProjects, templateRuleSheet);

            Sheet projectSheet = new Sheet(2, 0, ExcelProject.class);
            projectSheet.setSheetName(ExcelSheetName.PROJECT_NAME);
            writer.write(excelProjects, projectSheet);

            Sheet customRuleSheet = new Sheet(3, 0, ExcelCustomRuleByProject.class);
            customRuleSheet.setSheetName(ExcelSheetName.CUSTOM_RULE_NAME);
            writer.write(excelCustomRuleByProjects, customRuleSheet);

            Sheet multiRuleSheet = new Sheet(4, 0, ExcelMultiTemplateRuleByProject.class);
            multiRuleSheet.setSheetName(ExcelSheetName.MULTI_TEMPLATE_RULE_NAME);
            writer.write(excelMultiTemplateRuleByProjects, multiRuleSheet);

            Sheet templateFileSheet = new Sheet(5, 0, ExcelTemplateFileRuleByProject.class);
            templateFileSheet.setSheetName(ExcelSheetName.TEMPLATE_FILE_RULE_NAME);
            writer.write(excelTemplateFileRuleByProject, templateFileSheet);

            writer.finish();
            LOGGER.info("Finish to write excel");
        } catch (Exception e) {
            throw new WriteExcelException(e.getMessage());
        } finally {
            outputStream.close();
        }
    }

    private List<ExcelMultiTemplateRuleByProject> getExcelMultiTemplateRuleByProject(List<Project> projects, String localeStr) {
        List<ExcelMultiTemplateRuleByProject> excelMultiTemplateRuleByProjects = new ArrayList<>();
        for (Project project : projects) {
            List<Rule> rules = ruleDao.findByProject(project);
            List<ExcelMultiTemplateRuleByProject> excelMultiTemplateRules = ruleBatchService.getMultiTemplateRule(rules, localeStr);
            for (ExcelMultiTemplateRuleByProject excelMultiTemplateRule : excelMultiTemplateRules) {
                ExcelMultiTemplateRuleByProject excelMultiTemplateRuleByProject = new ExcelMultiTemplateRuleByProject();
                BeanUtils.copyProperties(excelMultiTemplateRule, excelMultiTemplateRuleByProject);
                excelMultiTemplateRuleByProject.setProjectName(project.getName());

                LOGGER.info("Collect excel line of custom rule: {}", excelMultiTemplateRuleByProject);
                excelMultiTemplateRuleByProjects.add(excelMultiTemplateRuleByProject);
            }
        }
        return excelMultiTemplateRuleByProjects;
    }

    private List<ExcelCustomRuleByProject> getExcelCustomRuleByProject(List<Project> projects, String localeStr) {
        List<ExcelCustomRuleByProject> excelCustomRuleByProjects = new ArrayList<>();
        for (Project project : projects) {
            List<Rule> rules = ruleDao.findByProject(project);
            List<ExcelCustomRuleByProject> excelCustomRules = ruleBatchService.getCustomRule(rules, localeStr);
            for (ExcelCustomRuleByProject excelCustomRule : excelCustomRules) {
                ExcelCustomRuleByProject excelCustomRuleByProject = new ExcelCustomRuleByProject();
                BeanUtils.copyProperties(excelCustomRule, excelCustomRuleByProject);
                excelCustomRuleByProject.setProjectName(project.getName());
                LOGGER.info("Collect excel line of custom rule: {}", excelCustomRuleByProject);
                excelCustomRuleByProjects.add(excelCustomRuleByProject);
            }
        }
        return excelCustomRuleByProjects;
    }

    private List<ExcelTemplateRuleByProject> getExcelRuleByProject(List<Project> projects, String localeStr) {
        List<ExcelTemplateRuleByProject> excelTemplateRuleByProjects = new ArrayList<>();
        for (Project project : projects) {
            List<Rule> rules = ruleDao.findByProject(project);
            List<ExcelTemplateRuleByProject> excelTemplateRules = ruleBatchService.getTemplateRule(rules, localeStr);
            for (ExcelTemplateRuleByProject templateRuleByProject : excelTemplateRules) {
                ExcelTemplateRuleByProject excelTemplateRuleByProject = new ExcelTemplateRuleByProject();
                BeanUtils.copyProperties(templateRuleByProject, excelTemplateRuleByProject);
                excelTemplateRuleByProject.setProjectName(project.getName());
                LOGGER.info("Collect excel line of template rule: {}", excelTemplateRuleByProject);
                excelTemplateRuleByProjects.add(excelTemplateRuleByProject);
            }
        }
        return excelTemplateRuleByProjects;
    }

    private List<ExcelProject> getExcelProject(List<Project> projects) {
        List<ExcelProject> excelProjects = new ArrayList<>();
        for (Project project : projects) {
            ExcelProject excelProject = new ExcelProject();

            excelProject.setProjectName(project.getName());
            excelProject.setProjectDescription(project.getDescription());

            Set<ProjectLabel> projectLabels = project.getProjectLabels();
            StringBuffer labelBuffer = new StringBuffer();
            if (projectLabels != null && ! projectLabels.isEmpty()) {
                for (ProjectLabel projectLabel : projectLabels) {
                    labelBuffer.append(projectLabel.getLabelName() + " ");
                }
                excelProject.setProjectLabels(labelBuffer.toString());
            }

            LOGGER.info("Collect excel line: {}", excelProject);
            excelProjects.add(excelProject);
        }
        return excelProjects;
    }
}
