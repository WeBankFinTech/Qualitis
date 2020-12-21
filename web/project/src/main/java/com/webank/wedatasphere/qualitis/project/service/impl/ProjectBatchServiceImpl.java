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
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.ExcelSheetName;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectLabel;
import com.webank.wedatasphere.qualitis.project.excel.*;
import com.webank.wedatasphere.qualitis.project.request.AddProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.DownloadProjectRequest;
import com.webank.wedatasphere.qualitis.project.service.ProjectBatchService;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.excel.ExcelCustomRule;
import com.webank.wedatasphere.qualitis.rule.excel.ExcelMultiTemplateRule;
import com.webank.wedatasphere.qualitis.rule.excel.ExcelTemplateRule;
import com.webank.wedatasphere.qualitis.rule.exception.WriteExcelException;
import com.webank.wedatasphere.qualitis.rule.service.RuleBatchService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.project.excel.ExcelCustomRuleByProject;
import com.webank.wedatasphere.qualitis.project.excel.ExcelMultiTemplateRuleByProject;
import com.webank.wedatasphere.qualitis.project.excel.ExcelProjectListener;
import com.webank.wedatasphere.qualitis.project.excel.ExcelTemplateRuleByProject;
import com.webank.wedatasphere.qualitis.project.constant.ExcelSheetName;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.excel.*;
import com.webank.wedatasphere.qualitis.project.request.AddProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.DownloadProjectRequest;
import com.webank.wedatasphere.qualitis.project.service.ProjectBatchService;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    private ProjectDao projectDao;

    @Autowired
    private RuleBatchService ruleBatchService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserDao userDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectBatchServiceImpl.class);
    private static final String SUPPORT_EXCEL_SUFFIX_NAME = ".xlsx";
    private final FastDateFormat FILE_DATE_FORMATTER = FastDateFormat.getInstance("yyyyMMddHHmmss");

    private HttpServletRequest httpServletRequest;

    public ProjectBatchServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public GeneralResponse<?> uploadProjects(InputStream fileInputStream, FormDataContentDisposition fileDisposition) throws UnExpectedRequestException,
            MetaDataAcquireFailedException, IOException, SemanticException, ParseException {
        String fileName = fileDisposition.getFileName();
        String suffixName = fileName.substring(fileName.lastIndexOf('.'));
        if (!suffixName.equals(SUPPORT_EXCEL_SUFFIX_NAME)) {
            throw new UnExpectedRequestException("{&DO_NOT_SUPPORT_SUFFIX_NAME}: [" + suffixName + "]. {&ONLY_SUPPORT} [" + SUPPORT_EXCEL_SUFFIX_NAME + "]");
        }

        String username = HttpUtils.getUserName(httpServletRequest);
        if (username == null) {
            return new GeneralResponse<>("401", "{&PLEASE_LOGIN}", null);
        }
        Long userId = HttpUtils.getUserId(httpServletRequest);

        // Read file and create project
        ExcelProjectListener listener = readExcel(fileInputStream);

        // Check if excel file is empty
        if (listener.getExcelProjectContent().isEmpty() && listener.getExcelRuleContent().isEmpty()
                && listener.getExcelCustomRuleContent().isEmpty() && listener.getExcelMultiRuleContent().isEmpty()) {
            throw new UnExpectedRequestException("{&FILE_CAN_NOT_BE_EMPTY_OR_FILE_CAN_NOT_BE_RECOGNIZED}");
        }

        for (ExcelProject excelProject : listener.getExcelProjectContent()) {
            // Check excel project arguments is valid or not
            AddProjectRequest request = convertExcelProjectToAddProjectRequest(excelProject);
            projectService.addProject(request, userId);
        }

        // Create rules according to excel sheet
        Map<String, Map<String, List<ExcelTemplateRule>>> excelTemplateRulePartitionedByProject = listener.getExcelRuleContent();
        Map<String, Map<String, List<ExcelCustomRule>>> excelCustomRulePartitionedByProject = listener.getExcelCustomRuleContent();
        Map<String, Map<String, List<ExcelMultiTemplateRule>>> excelMultiTemplateRulePartitionedByProject = listener.getExcelMultiRuleContent();
        Set<String> allProjects = new HashSet<>();
        allProjects.addAll(excelTemplateRulePartitionedByProject.keySet());
        allProjects.addAll(excelCustomRulePartitionedByProject.keySet());
        allProjects.addAll(excelMultiTemplateRulePartitionedByProject.keySet());

        for (String projectName : allProjects) {

            try {
                Project projectInDb = projectDao.findByName(projectName);
                if (projectInDb == null) {
                    throw new UnExpectedRequestException("{&PROJECT}: [" + projectName + "] {&DOES_NOT_EXIST}");
                }
                ruleBatchService.getAndSaveRule(excelTemplateRulePartitionedByProject.get(projectName), excelCustomRulePartitionedByProject.get(projectName),
                    excelMultiTemplateRulePartitionedByProject.get(projectName), projectInDb, username);
            } catch (Exception e) {
                 throw new UnExpectedRequestException("{&PROJECT_NAME_EXISTS}");
            }
        }

        fileInputStream.close();
        return new GeneralResponse<>("200", "{&SUCCEED_TO_UPLOAD_FILE}", null);
    }

    private AddProjectRequest convertExcelProjectToAddProjectRequest(ExcelProject excelProject) throws UnExpectedRequestException {
        // Construct AddProjectRequest
        AddProjectRequest addProjectRequest = new AddProjectRequest();
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
            }
        }


        LOGGER.info("Finish to read project excel. excel content: rule sheet {}, project sheet {}", listener.getExcelRuleContent(), listener.getExcelProjectContent());
        return listener;
    }

    @Override
    public GeneralResponse<?> downloadProjects(DownloadProjectRequest request, HttpServletResponse response) throws UnExpectedRequestException, WriteExcelException, IOException {
        // Check Arguments
        DownloadProjectRequest.checkRequest(request);

        List<Project> projectsInDb = new ArrayList<>();
        for (Long projectId : request.getProjectId()) {
            Project projectInDb = projectDao.findById(projectId);
            if (projectInDb == null) {
                throw new UnExpectedRequestException("{&PROJECT_ID} : [" + projectId + "] {&DOES_NOT_EXIST}");
            }
            projectsInDb.add(projectInDb);
        }

        // Write project and rules
        return writeProjectAndRules(projectsInDb, response);
    }

    private GeneralResponse<?> writeProjectAndRules(List<Project> projects, HttpServletResponse response) throws IOException, WriteExcelException {
        String fileName = "batch_project_export_" + FILE_DATE_FORMATTER.format(new Date()) +".xlsx";
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment;filename*=UTF-8''" + fileName);
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");

        OutputStream outputStream = response.getOutputStream();
        List<ExcelProject> excelProject = getExcelProject(projects);
        List<ExcelTemplateRuleByProject> excelTemplateRuleByProject = getExcelRuleByProject(projects);
        List<ExcelCustomRuleByProject> excelCustomRuleByProject = getExcelCustomRuleByProject(projects);
        List<ExcelMultiTemplateRuleByProject> excelMultiTemplateRuleByProject = getExcelMultiTemplateRuleByProject(projects);
        writeExcelToOutput(excelProject, excelTemplateRuleByProject, excelCustomRuleByProject, excelMultiTemplateRuleByProject, outputStream);
        outputStream.flush();
        LOGGER.info("Succeed to download all projects in type of excel");
        return null;
    }

    private void writeExcelToOutput(List<ExcelProject> excelProjects, List<ExcelTemplateRuleByProject> excelTemplateRuleByProjects, List<ExcelCustomRuleByProject> excelCustomRuleByProjects,
                                    List<ExcelMultiTemplateRuleByProject> excelMultiTemplateRuleByProjects, OutputStream outputStream) throws WriteExcelException, IOException {
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
            writer.finish();
            LOGGER.info("Finish to write excel");
        } catch (Exception e) {
            throw new WriteExcelException(e.getMessage());
        } finally {
            outputStream.close();
        }
    }

    private List<ExcelMultiTemplateRuleByProject> getExcelMultiTemplateRuleByProject(List<Project> projects) {
        List<ExcelMultiTemplateRuleByProject> excelMultiTemplateRuleByProjects = new ArrayList<>();
        for (Project project : projects) {
            List<ExcelMultiTemplateRule> excelMultiTemplateRules = ruleBatchService.getMultiTemplateRule(project.getRules());
            for (ExcelMultiTemplateRule excelMultiTemplateRule : excelMultiTemplateRules) {
                ExcelMultiTemplateRuleByProject excelMultiTemplateRuleByProject = new ExcelMultiTemplateRuleByProject();
                BeanUtils.copyProperties(excelMultiTemplateRule, excelMultiTemplateRuleByProject);
                excelMultiTemplateRuleByProject.setProjectName(project.getName());
                LOGGER.info("Collect excel line of custom rule: {}", excelMultiTemplateRuleByProject);
                excelMultiTemplateRuleByProjects.add(excelMultiTemplateRuleByProject);
            }
        }
        return excelMultiTemplateRuleByProjects;
    }

    private List<ExcelCustomRuleByProject> getExcelCustomRuleByProject(List<Project> projects) {
        List<ExcelCustomRuleByProject> excelCustomRuleByProjects = new ArrayList<>();
        for (Project project : projects) {
            List<ExcelCustomRule> excelCustomRules = ruleBatchService.getCustomRule(project.getRules());
            for (ExcelCustomRule excelCustomRule : excelCustomRules) {
                ExcelCustomRuleByProject excelCustomRuleByProject = new ExcelCustomRuleByProject();
                BeanUtils.copyProperties(excelCustomRule, excelCustomRuleByProject);
                excelCustomRuleByProject.setProjectName(project.getName());
                LOGGER.info("Collect excel line of custom rule: {}", excelCustomRuleByProject);
                excelCustomRuleByProjects.add(excelCustomRuleByProject);
            }
        }
        return excelCustomRuleByProjects;
    }

    private List<ExcelTemplateRuleByProject> getExcelRuleByProject(List<Project> projects) {
        List<ExcelTemplateRuleByProject> excelTemplateRuleByProjects = new ArrayList<>();
        for (Project project : projects) {
            List<ExcelTemplateRule> excelTemplateRules = ruleBatchService.getTemplateRule(project.getRules());
            for (ExcelTemplateRule excelTemplateRule : excelTemplateRules) {
                ExcelTemplateRuleByProject excelTemplateRuleByProject = new ExcelTemplateRuleByProject();
                BeanUtils.copyProperties(excelTemplateRule, excelTemplateRuleByProject);
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
