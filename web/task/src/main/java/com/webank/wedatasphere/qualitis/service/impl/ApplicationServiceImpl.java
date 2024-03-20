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

package com.webank.wedatasphere.qualitis.service.impl;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.webank.wedatasphere.qualitis.client.RequestLinkis;
import com.webank.wedatasphere.qualitis.client.request.AskLinkisParameter;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.constant.AlarmConfigStatusEnum;
import com.webank.wedatasphere.qualitis.constant.ApplicationStatusEnum;
import com.webank.wedatasphere.qualitis.constant.TaskStatusEnum;
import com.webank.wedatasphere.qualitis.dao.*;
import com.webank.wedatasphere.qualitis.entity.*;
import com.webank.wedatasphere.qualitis.excel.ExcelResult;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.project.constant.ExcelSheetName;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.dao.ProjectUserDao;
import com.webank.wedatasphere.qualitis.request.*;
import com.webank.wedatasphere.qualitis.response.*;
import com.webank.wedatasphere.qualitis.rule.constant.CheckTemplateEnum;
import com.webank.wedatasphere.qualitis.rule.constant.CompareTypeEnum;
import com.webank.wedatasphere.qualitis.service.ApplicationService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Service
public class ApplicationServiceImpl implements ApplicationService {
    @Autowired
    private LinkisConfig linkisConfig;

    @Autowired
    private MetaDataClient metaDataClient;

    @Autowired
    private TaskDataSourceDao taskDataSourceDao;
    @Autowired
    private TaskRuleSimpleDao taskRuleSimpleDao;
    @Autowired
    private ApplicationDao applicationDao;
    @Autowired
    private ProjectUserDao projectUserDao;
    @Autowired
    private ClusterInfoDao clusterInfoDao;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private TaskResultDao taskResultDao;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RequestLinkis requestLinkis;

    private HttpServletRequest httpServletRequest;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    private static final Map<Integer, Integer> COMMENT_STATUS = new HashMap<Integer, Integer>();

    static {
        COMMENT_STATUS.put(1, 9);
        COMMENT_STATUS.put(2, 7);
        COMMENT_STATUS.put(3, 7);
        COMMENT_STATUS.put(4, 7);
        COMMENT_STATUS.put(5, 7);
        COMMENT_STATUS.put(6, 7);
        COMMENT_STATUS.put(7, 8);
        COMMENT_STATUS.put(8, 8);
        COMMENT_STATUS.put(9, 4);
        COMMENT_STATUS.put(10, 4);
        COMMENT_STATUS.put(11, 9);
        COMMENT_STATUS.put(12, 7);
        COMMENT_STATUS.put(13, 8);
        COMMENT_STATUS.put(14, 9);
    }

    public ApplicationServiceImpl(@Context HttpServletRequest request) {
        this.httpServletRequest = request;
    }

    @Override
    public GeneralResponse<GetAllResponse<ApplicationResponse>> filterStatusApplication(FilterStatusRequest request) throws UnExpectedRequestException {
        // Check arguments
        FilterStatusRequest.checkRequest(request);
        String userName = HttpUtils.getUserName(httpServletRequest);
        if (request.getStatus() != null) {
            LOGGER.info("User: {} wants to find applications with status: {}", userName, request.getStatus());
        } else {
            LOGGER.info("User: {} wants to find all applications", userName);
        }

        if (request.getCommentType() != null) {
            request.setStatus(COMMENT_STATUS.get(request.getCommentType()));
        }

        List<Application> applicationList;
        Long total;
        Integer page = request.getPage();
        Integer size = request.getSize();
        if (request.getStatus() == null || request.getStatus().intValue() == 0) {
            // Paging find applications by user
            long currentTimeUser = System.currentTimeMillis();
            applicationList = applicationDao.findByCreateUser(userName, page, size);
            LOGGER.info("timechecker find page application :" + (System.currentTimeMillis() - currentTimeUser));
            long currentTimeCountUser = System.currentTimeMillis();
            total = applicationDao.countByCreateUser(userName);
            LOGGER.info("timechecker count application :" + (System.currentTimeMillis() - currentTimeCountUser));
        } else {
            // Paging find applications by user and status
            applicationList = applicationDao.findByCreateUserAndStatus(userName, request.getStatus(), request.getCommentType(), page, size);
            total = applicationDao.countByCreateUserAndStatus(userName, request.getStatus(), request.getCommentType());
        }
        long currentTimeResponse = System.currentTimeMillis();
        GetAllResponse<ApplicationResponse> getAllResponse = new GetAllResponse<>();
        List<ApplicationResponse> applicationResponses = new ArrayList<>();

        for (Application application : applicationList) {
            List<Task> tasks = taskDao.findByApplication(application);
            ApplicationResponse response = new ApplicationResponse(application, tasks);
            if (application.getCreateUser().equals(userName) || application.getExecuteUser().equals(userName)) {
                response.setKillOption(true);
            } else {
                response.setKillOption(false);
            }
            applicationResponses.add(response);
        }

        getAllResponse.setData(applicationResponses);
        getAllResponse.setTotal(total);

        List<String> applicationIdList = getAllResponse.getData().stream().map(ApplicationResponse::getApplicationId).collect(Collectors.toList());
        LOGGER.info("timechecker response :" + (System.currentTimeMillis() - currentTimeResponse));
        LOGGER.info("Succeed to find applications. size: {}, id of applications: {}", total, applicationIdList);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_APPLICATIONS}", getAllResponse);
    }

    @Override
    public GeneralResponse<GetAllResponse<ApplicationResponse>> filterProjectApplication(FilterProjectRequest request) throws UnExpectedRequestException {
        // Check arguments
        FilterProjectRequest.checkRequest(request);

        Long userId = HttpUtils.getUserId(httpServletRequest);
        User user = userDao.findById(userId);

        Integer page = request.getPage();
        Integer size = request.getSize();
        Long projectId = request.getProjectId();

        int total = applicationDao.countByCreateUserAndProject(user.getUsername(), projectId).intValue();
        List<Application> applicationList = applicationDao.findByCreateUserAndProject(user.getUsername(), projectId, page, size);

        GetAllResponse<ApplicationResponse> getAllResponse = new GetAllResponse<>();
        List<ApplicationResponse> applicationResponses = new ArrayList<>();
        for (Application application : applicationList) {
            List<Task> tasks = taskDao.findByApplication(application);
            ApplicationResponse response = new ApplicationResponse(application, tasks);
            if (application.getCreateUser().equals(user.getUsername()) || application.getExecuteUser().equals(user.getUsername())) {
                response.setKillOption(true);
            } else {
                response.setKillOption(false);
            }
            applicationResponses.add(response);
        }
        getAllResponse.setData(applicationResponses);
        getAllResponse.setTotal(total);

        List<String> applicationIdList = getAllResponse.getData().stream().map(ApplicationResponse::getApplicationId).collect(Collectors.toList());
        LOGGER.info("Succeed to find applications. size: {}, id of applications: {}", total, applicationIdList);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_APPLICATIONS}", getAllResponse);
    }

    @Override
    public GeneralResponse<GetAllResponse<ApplicationResponse>> filterDataSourceApplication(FilterDataSourceRequest request) throws UnExpectedRequestException {
        // Check arguments
        FilterDataSourceRequest.checkRequest(request);

        Integer page = request.getPage();
        Integer size = request.getSize();
        String clusterName = request.getClusterName();
        String databaseName = StringUtils.isEmpty(request.getDatabaseName()) ? "" : request.getDatabaseName();
        String tableName = StringUtils.isEmpty(request.getTableName()) ? "" : request.getTableName();

        Long userId = HttpUtils.getUserId(httpServletRequest);
        User user = userDao.findById(userId);

        List<TaskDataSource> taskDataSources;
        long total;
        // Find datasource by user
        taskDataSources = taskDataSourceDao.findByCreateUserAndDatasource(user.getUsername(), clusterName, databaseName, tableName, page, size);
        total = taskDataSourceDao.countByCreateUserAndDatasource(user.getUsername(), clusterName, databaseName, tableName);

        List<Application> applicationList = taskDataSources.stream().map(jobDataSource -> jobDataSource.getTask().getApplication()).collect(Collectors.toList());

        GetAllResponse<ApplicationResponse> getAllResponse = new GetAllResponse<>();
        List<ApplicationResponse> applicationResponses = new ArrayList<>();
        for (Application application : applicationList) {
            List<Task> tasks = taskDao.findByApplication(application);
            ApplicationResponse response = new ApplicationResponse(application, tasks);
            if (application.getCreateUser().equals(user.getUsername()) || application.getExecuteUser().equals(user.getUsername())) {
                response.setKillOption(true);
            } else {
                response.setKillOption(false);
            }
            applicationResponses.add(response);
        }
        getAllResponse.setData(applicationResponses);
        getAllResponse.setTotal(total);

        List<String> applicationIdList = getAllResponse.getData().stream().map(ApplicationResponse::getApplicationId).collect(Collectors.toList());
        LOGGER.info("Succeed to find applications. size: {}, id of applications: {}", total, applicationIdList);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_APPLICATIONS}", getAllResponse);
    }

    /**
     * Find application by applicationId
     *
     * @param applicationId
     * @param page
     * @param size
     * @param taskPage
     * @param taskSize
     * @return
     */
    @Override
    public GeneralResponse<GetAllResponse<ApplicationResponse>> filterApplicationId(String applicationId, Integer filterStatus, Integer page, Integer size
        , Integer taskPage, Integer taskSize) {

        Long userId = HttpUtils.getUserId(httpServletRequest);
        // Find applications by user
        User user = userDao.findById(userId);
        boolean logSelect = filterStatus != null;

        List<Application> applicationList = applicationDao.findByCreateUserAndId(user.getUsername(), StringUtils.isNotBlank(applicationId) ? "%" + applicationId + "%" : "", logSelect ? 0 : page, logSelect ? 1 : size);
        long total = applicationDao.countByCreateUserAndId(user.getUsername(), StringUtils.isNotBlank(applicationId) ? "%" + applicationId + "%" : "");
        if (applicationList == null) {
            LOGGER.info("User: {} , Not find applications with applicationId: {}", user.getUsername(), applicationId);
            return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_APPLICATIONS_BUT_FIND_NO_RESULTS}", null);
        }
        GetAllResponse<ApplicationResponse> getAllResponse = new GetAllResponse<>();
        List<ApplicationResponse> applicationResponses = new ArrayList<>();
        for (Application application : applicationList) {
            int taskPageTemp = taskPage != null ? taskPage : 0;
            int taskSizeTemp = taskSize != null ? taskSize : 5;
            List<Task> tasks = taskDao.findByApplicationPageable(application, logSelect && filterStatus == 1, taskPageTemp, taskSizeTemp);
            int taskTotal = taskDao.countByApplication(application);
            ApplicationResponse response = new ApplicationResponse(application, tasks);
            if (application.getCreateUser().equals(user.getUsername()) || application.getExecuteUser().equals(user.getUsername())) {
                response.setKillOption(true);
            } else {
                response.setKillOption(false);
            }

            response.setTaskTotal(taskTotal);
            applicationResponses.add(response);
        }
        getAllResponse.setData(applicationResponses);
        getAllResponse.setTotal(total);

        List<String> applicationIdList = getAllResponse.getData().stream().map(ApplicationResponse::getApplicationId).collect(Collectors.toList());
        LOGGER.info("User: {}, find {} applications with like applicationId : {},Id of applications: {}", user.getUsername(), applicationList.size(), applicationId, applicationIdList);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_APPLICATIONS}", getAllResponse);
    }

    @Override
    public GeneralResponse<Integer> uploadDataSourceAnalysisResult(UploadResultRequest request) throws UnExpectedRequestException, IOException {
        // Login user permission.
        Long userId = HttpUtils.getUserId(httpServletRequest);
        User user = userDao.findById(userId);
        if (user == null) {
            throw new UnExpectedRequestException("User {&DOES_NOT_EXIST}");
        }
        UploadResultRequest.checkRequest(request);

        File tmpFile = getFileFromDataSource(request, user);

        // Upload to HDFS
        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(request.getClusterName());
        if (clusterInfo == null) {
            throw new UnExpectedRequestException("Cluster info " + "[" + request.getClusterName() + "]" + "{&DOES_NOT_EXIST}");
        }

        CloseableHttpClient httpclient = HttpClients.createDefault();

        // Get root
        String rootDir = "";
        String urlGetRoot = UriBuilder.fromUri(clusterInfo.getLinkisAddress()).path(linkisConfig.getPrefix()).path(linkisConfig.getUploadRoot()).queryParam("pathType", "hdfs").toString();
        AskLinkisParameter askLinkisParameter = new AskLinkisParameter(urlGetRoot, "QUALITIS-AUTH", StringUtils.isNotBlank(request.getProxyUser()) ? request.getProxyUser() : user.getUsername(), "get user hdfs root dir");
        try {
            Map<String, Object> responseMap = requestLinkis.getLinkisResponseByGet(askLinkisParameter);
            rootDir = (String) ((Map<String, Object>) responseMap.get("data")).get("userHDFSRootPath");
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            if(tmpFile.exists()){
                Files.delete(tmpFile.toPath());
            }

            throw new UnExpectedRequestException((StringUtils.isNotBlank(request.getProxyUser()) ? request.getProxyUser() : user.getUsername()) + " has no hdfs root dir");
        }

        // send request to get dbs
        String url = UriBuilder.fromUri(clusterInfo.getLinkisAddress()).path(linkisConfig.getPrefix()).path(linkisConfig.getUpload()).toString();
        HttpPost httppost = new HttpPost(url);

        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.setContentType(ContentType.MULTIPART_FORM_DATA);
        multipartEntityBuilder.setCharset(Charset.forName("UTF-8"));
        multipartEntityBuilder.addBinaryBody("file", tmpFile);
        multipartEntityBuilder.addTextBody("path", linkisConfig.getUploadPrefix() + rootDir.substring(0, rootDir.length() - 1) + request.getHdfsPath());
        httppost.addHeader("Token-User", StringUtils.isNotBlank(request.getProxyUser()) ? request.getProxyUser() : user.getUsername());
        httppost.addHeader("Token-Code", clusterInfo.getLinkisToken());

        httppost.setEntity(multipartEntityBuilder.build());

        CloseableHttpResponse response;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            throw new UnExpectedRequestException("{&FAILED_TO_CALL_UPLOAD_API}");
        }
        int code = response.getStatusLine().getStatusCode();
        response.close();

        if(tmpFile.exists()){
            Files.delete(tmpFile.toPath());
        }

        if (code != HttpStatus.SC_OK) {
            throw new UnExpectedRequestException("{&FAILED_TO_CALL_UPLOAD_API}");
        }
        return new GeneralResponse<>(code + "", "{&SUCCESS_TO_UPLOAD_ANALYSIS_EXCEL}", code);
    }

    private File getFileFromDataSource(UploadResultRequest request, User user) throws UnExpectedRequestException, FileNotFoundException {
        List<ApplicationClusterResponse> responses = (List<ApplicationClusterResponse>) getDataSource(new PageRequest(0, Integer.MAX_VALUE)).getData();
        List<String> tables = responses.stream()
                .filter(cluster -> cluster.getClusterName().equals(request.getClusterName()))
                .map(ApplicationClusterResponse::getDatabase).flatMap(database -> database.stream())
                .filter(databaseResponse -> databaseResponse.getDatabaseName().equals(request.getDatabaseName()))
                .map(ApplicationDatabaseResponse::getTable).flatMap(table -> table.stream())
                .distinct().collect(Collectors.toList());

        if (StringUtils.isNotBlank(request.getTableName())) {
            tables.clear();
            tables.add(request.getTableName());
        }

        LOGGER.info("Start to write excel");
        StringBuilder fileName = new StringBuilder();
        fileName.append(linkisConfig.getUploadTmpPath()).append(File.separator)
                .append(StringUtils.isNotBlank(request.getProxyUser()) ? request.getProxyUser() : user.getUsername())
                .append("_")
                .append(request.getClusterName())
                .append("_")
                .append(request.getDatabaseName())
                .append("_")
                .append(UUID.randomUUID().toString())
                .append(ExcelTypeEnum.XLSX.getValue());
        File tmpFile = new File(fileName.toString());
        writeExcelFile(tmpFile, tables, request);
        return tmpFile;
    }

    private void writeExcelFile(File tmpFile, List<String> tables, UploadResultRequest request) throws UnExpectedRequestException, FileNotFoundException {
        int sheetNo = 1;
        try (OutputStream tmpOutputStream = new FileOutputStream(tmpFile)){
            Path parentPath = Paths.get(tmpFile.getParent());
            if (Files.notExists(parentPath)) {
                tmpFile.getParentFile().mkdirs();
            }
            if (!tmpFile.exists()) {
                LOGGER.info("Start to create local file.");
                if (!tmpFile.createNewFile()) {
                    LOGGER.error("{&FAILED_TO_CREATE_NEW_FILE}");
                }
            }

            ExcelWriter writer = new ExcelWriter(tmpOutputStream, ExcelTypeEnum.XLSX, true);
            // Find task with the start time and end time.
            for (String tableName : tables) {
                List<Task> tasks = taskDao.findWithSubmitTimeAndDatasource(request.getStartTime(), request.getEndTime(), request.getClusterName()
                        , request.getDatabaseName(), tableName);
                List<TaskRuleSimple> taskRuleSimples = tasks.stream().map(Task::getTaskRuleSimples).flatMap(
                        taskRuleSimpleSet -> taskRuleSimpleSet.stream()).distinct().collect(Collectors.toList());

                // Generate analysis result excel
                List<ExcelResult> results = new ArrayList<>(taskRuleSimples.size());
                for (TaskRuleSimple taskRuleSimple : taskRuleSimples) {
                    ExcelResult excelResult = new ExcelResult();
                    Task currentTask = taskRuleSimple.getTask();
                    excelResult.setProjectName(taskRuleSimple.getProjectName());
                    excelResult.setRuleName(taskRuleSimple.getRuleName());
                    excelResult.setClusterName(request.getClusterName());
                    excelResult.setDatabaseName(request.getDatabaseName());
                    excelResult.setTableName(tableName);
                    excelResult.setPartition(currentTask.getApplication().getPartition());
                    excelResult.setBeginTime(currentTask.getBeginTime());
                    excelResult.setEndTime(currentTask.getEndTime());
                    excelResult.setApplicationId(currentTask.getApplication().getId());
                    ApplicationComment applicationComment = SpringContextHolder.getBean(ApplicationCommentDao.class).getByCode(currentTask.getApplication().getApplicationComment());
                    excelResult.setApplicationComment(applicationComment != null ? applicationComment.getZhMessage() : null);
                    StringBuilder checkTemplateStr = new StringBuilder();
                    StringBuilder resultStr = new StringBuilder();
                    joinAlarmConfig(results, taskRuleSimple, excelResult, request, checkTemplateStr, resultStr);
                }
                Sheet templateSheet = new Sheet(sheetNo++, 0, ExcelResult.class);
                templateSheet.setSheetName(tableName + "-" + ExcelSheetName.ANALYSIS_NAME);
                writer.write(results, templateSheet);
            }
            writer.finish();
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UnExpectedRequestException("{&FAILED_TO_CREATE_LOCAL_FILE}");
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new UnExpectedRequestException("{&FAILED_TO_CREATE_LOCAL_FILE}");
        }
    }

    private void joinAlarmConfig(List<ExcelResult> results, TaskRuleSimple taskRuleSimple, ExcelResult excelResult, UploadResultRequest request,
                                 StringBuilder checkTemplateStr, StringBuilder resultStr) {
        for (TaskRuleAlarmConfig taskRuleAlarmConfig : taskRuleSimple.getTaskRuleAlarmConfigList()) {
            Integer checkTemplate = taskRuleAlarmConfig.getCheckTemplate();
            String checkTemplateName = CheckTemplateEnum.getCheckTemplateName(checkTemplate);
            String outputName = taskRuleAlarmConfig.getOutputName();
            String compare = CompareTypeEnum.getCompareTypeName(taskRuleAlarmConfig.getCompareType());
            Double threshold = taskRuleAlarmConfig.getThreshold();
            Integer statusCode = taskRuleAlarmConfig.getStatus();
            String status = AlarmConfigStatusEnum.getMessage(statusCode);
            RuleMetric ruleMetric = taskRuleAlarmConfig.getRuleMetric();
            TaskResult taskResult = taskResultDao.find(taskRuleSimple.getApplicationId(), taskRuleSimple.getRuleId(), ruleMetric != null ? ruleMetric.getId() : -1);
            String resultValue = "";
            Long runDate = null;
            if (taskResult != null) {
                resultValue = StringUtils.isNotBlank(taskResult.getValue()) ? taskResult.getValue() : "0";
                runDate = taskResult.getRunDate();
            }

            if (request.getStatus().contains(statusCode)) {
                checkTemplateStr.append(checkTemplateName)
                        .append(StringUtils.isNotBlank(compare) ? compare : "")
                        .append(" ").append(threshold);
                ExcelResult excelAlarmConfig = new ExcelResult();
                BeanUtils.copyProperties(excelResult, excelAlarmConfig);
                excelAlarmConfig.setRuleCheckTemplates(checkTemplateStr.toString());
                resultStr.append(outputName).append(": ").append(resultValue);
                excelAlarmConfig.setStatus(status);
                excelAlarmConfig.setHistoryResult(resultStr.toString());
                excelAlarmConfig.setCreateTime(runDate != null ? runDate.toString() : "");
                excelAlarmConfig.setExecutionUser(taskRuleSimple.getExecuteUser());
                results.add(excelAlarmConfig);

                checkTemplateStr.delete(0, checkTemplateStr.length());
                resultStr.delete(0, checkTemplateStr.length());
            }
        }
    }

    @Override
    public GeneralResponse<GetAllResponse<ApplicationResponse>> filterAdvanceApplication(FilterAdvanceRequest request) {
        Long userId = HttpUtils.getUserId(httpServletRequest);
        // Find applications by user
        User user = userDao.findById(userId);

        List<Application> applicationList;
        long total = 0;

        // If application ID is not empty, just return application which ID like the input string.
        if (StringUtils.isNotBlank(request.getApplicationId())) {
            applicationList = applicationDao.findByCreateUserAndId(user.getUsername(), request.getApplicationId(), request.getPage(), request.getSize());
            total = applicationDao.countByCreateUserAndId(user.getUsername(), request.getApplicationId());
        } else if (StringUtils.isNotBlank(request.getClusterName())) {
            if (request.getStatus() != null) {
                if (request.getStatus().equals(ApplicationStatusEnum.FINISHED.getCode())) {
                    request.setStatus(TaskStatusEnum.PASS_CHECKOUT.getCode());
                } else if (request.getStatus().equals(ApplicationStatusEnum.NOT_PASS.getCode())) {
                    request.setStatus(TaskStatusEnum.FAIL_CHECKOUT.getCode());
                } else if (request.getStatus().equals(ApplicationStatusEnum.SUCCESSFUL_CREATE_APPLICATION.getCode())) {
                    request.setStatus(TaskStatusEnum.INITED.getCode());
                } else if (request.getStatus().equals(ApplicationStatusEnum.RUNNING.getCode())) {
                    request.setStatus(TaskStatusEnum.RUNNING.getCode());
                } else if (request.getStatus() == 0) {
                    request.setStatus(null);
                }
            }
            // If data source is not empty, it will be used as the basic filter.
            applicationList = applicationDao.findApplicationByAdvanceConditionsWithDatasource(user.getUsername(), request.getClusterName()
                    , request.getDatabaseName(), request.getTableName(), request.getProjectId(), request.getStatus(), request.getCommentType()
                    , request.getStartTime(), request.getEndTime(), request.getRuleGroupId(), request.getExecuteUser(), request.getPage()
                    , request.getSize());
            total = applicationDao.countApplicationByAdvanceConditionsWithDatasource(user.getUsername(), request.getClusterName()
                    , request.getDatabaseName(), request.getTableName(), request.getProjectId(), request.getStatus()
                    , request.getCommentType(), request.getStartTime(), request.getEndTime()
                    , request.getRuleGroupId(), request.getExecuteUser());
        } else {
            Page<Application> applicationPage = applicationDao.findApplicationByAdvanceConditions(user.getUsername(), request.getProjectId(), request.getStatus(), request.getCommentType()
                    , request.getStartTime(), request.getEndTime(), request.getRuleGroupId(), request.getExecuteUser(), CollectionUtils.isEmpty(request.getStopAbleStatus()) ? null : request.getStopAbleStatus()
                    , StringUtils.isNotEmpty(request.getStartFinishTime())?request.getStartFinishTime(): Strings.EMPTY, StringUtils.isNotEmpty(request.getEndFinishTime())?request.getEndFinishTime(): Strings.EMPTY, request.getPage(), request.getSize());
            applicationList = applicationPage.getContent();
            total = applicationPage.getTotalElements();
        }

        GetAllResponse<ApplicationResponse> getAllResponse = new GetAllResponse<>();
        List<ApplicationResponse> applicationResponses = new ArrayList<>();

        List<Task> taskList = taskDao.findByApplicationList(applicationList);
        Map<String, List<Task>> applicationListMap = taskList.stream().collect(Collectors.groupingBy(task -> task.getApplication().getId()));
        for (Application application : applicationList) {
            List<Task> tasks = applicationListMap.get(application.getId());
            ApplicationResponse response = new ApplicationResponse(application, tasks);
            if (application.getCreateUser().equals(user.getUsername()) || application.getExecuteUser().equals(user.getUsername())) {
                response.setKillOption(true);
            } else {
                response.setKillOption(false);
            }
            applicationResponses.add(response);
        }

        getAllResponse.setData(applicationResponses);
        getAllResponse.setTotal(total);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_APPLICATIONS}", getAllResponse);
    }

    @Override
    public GeneralResponse<List<String>> getAllExecuteUser() {
        String userName = HttpUtils.getUserName(httpServletRequest);
        List<String> allExecuteUser = applicationDao.getAllExecuteUser(userName);

        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_ALL_EXECUTE_USER}", allExecuteUser);
    }

    @Override
    public GeneralResponse<List<ApplicationClusterResponse>> getDataSource(PageRequest request) throws UnExpectedRequestException {
        // Check arguments
        PageRequest.checkRequest(request);

        Long userId = HttpUtils.getUserId(httpServletRequest);
        User user = userDao.findById(userId);

        long total;

        // Find datasource by user
        List<Map<String, String>> taskDataSourceMap = taskDataSourceDao.findByUser(user.getUsername());
        total = taskDataSourceMap.size();

        List<ApplicationClusterResponse> response = new ArrayList<>();
        Map<String, ApplicationClusterResponse> map = new HashMap<>(2);
        for (Map<String, String> currentMap : taskDataSourceMap) {
            putIntoCluster(response, currentMap, map);
        }

        LOGGER.info("Succeed to find dataSources. size: {}, id of dataSources: {}", total, response);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_APPLICATION_DATASOURCE}", response);
    }

    private void putIntoCluster(List<ApplicationClusterResponse> responses, Map<String, String> taskDataSourceMap, Map<String, ApplicationClusterResponse> map) {
        String cluster = taskDataSourceMap.get("cluster_name");
        if (StringUtils.isEmpty(cluster)) {
            return;
        }
        if (map.containsKey(cluster)) {
            ApplicationClusterResponse response = map.get(cluster);
            putIntoDatabase(taskDataSourceMap, response);
        } else {
            ApplicationClusterResponse response = new ApplicationClusterResponse(cluster);
            putIntoDatabase(taskDataSourceMap, response);
            responses.add(response);
            map.put(cluster, response);
        }
    }

    private void putIntoDatabase(Map<String, String> taskDataSourceMap, ApplicationClusterResponse applicationClusterResponse) {
        String database = taskDataSourceMap.get("db_name");
        if (StringUtils.isEmpty(database)) {
            return;
        }
        if (applicationClusterResponse.getMap().containsKey(database)) {
            ApplicationDatabaseResponse response = applicationClusterResponse.getMap().get(database);
            putIntoTable(taskDataSourceMap, response);
        } else {
            ApplicationDatabaseResponse response = new ApplicationDatabaseResponse(database);
            response.setDatabaseName(database);
            putIntoTable(taskDataSourceMap, response);

            applicationClusterResponse.getMap().put(database, response);
            applicationClusterResponse.getDatabase().add(response);
        }
    }

    private void putIntoTable(Map<String, String> taskDataSource, ApplicationDatabaseResponse applicationDatabaseResponse) {
        String table = taskDataSource.get("table_name");
        if (StringUtils.isEmpty(table)) {
            return;
        }
        if (!applicationDatabaseResponse.getTable().contains(table)) {
            applicationDatabaseResponse.getTable().add(table);
        }
    }
}
