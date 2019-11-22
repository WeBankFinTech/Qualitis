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

import com.webank.wedatasphere.qualitis.dao.*;
import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.dao.TaskDataSourceDao;
import com.webank.wedatasphere.qualitis.dao.TaskRuleSimpleDao;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.TaskDataSource;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;
import com.webank.wedatasphere.qualitis.service.ApplicationService;
import com.webank.wedatasphere.qualitis.entity.*;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.FilterDataSourceRequest;
import com.webank.wedatasphere.qualitis.request.FilterProjectRequest;
import com.webank.wedatasphere.qualitis.request.FilterStatusRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.*;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.response.ApplicationDatabaseResponse;
import com.webank.wedatasphere.qualitis.response.ApplicationResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.dao.*;
import com.webank.wedatasphere.qualitis.entity.*;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.FilterDataSourceRequest;
import com.webank.wedatasphere.qualitis.request.FilterProjectRequest;
import com.webank.wedatasphere.qualitis.request.FilterStatusRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.*;
import com.webank.wedatasphere.qualitis.service.ApplicationService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationDao applicationDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private TaskDataSourceDao taskDataSourceDao;
    @Autowired
    private TaskRuleSimpleDao taskRuleSimpleDao;

    private HttpServletRequest httpServletRequest;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    public ApplicationServiceImpl(@Context HttpServletRequest request) {
        this.httpServletRequest =  request;
    }

    @Override
    public GeneralResponse<?> filterStatusApplication(FilterStatusRequest request) throws UnExpectedRequestException {
        // Check arguments
        FilterStatusRequest.checkRequest(request);

        Long userId = HttpUtils.getUserId(httpServletRequest);
        // Find applications of user
        User user = userDao.findById(userId);
        if (request.getStatus() != null) {
            LOGGER.info("User: {} wants to find applications with status: {}", user.getUsername(), request.getStatus());
        } else {
            LOGGER.info("User: {} wants to find all applications", user.getUsername());
        }

        List<Application> applicationList;
        Long total;
        Integer page = request.getPage();
        Integer size = request.getSize();
        if (request.getStatus() == null) {
            // Paging find applications by user
            applicationList = applicationDao.findByCreateUser(user.getUsername(), page, size);
            total = applicationDao.countByCreateUser(user.getUsername());
        } else {
            // Paging find applications by user and status
            applicationList = applicationDao.findByCreateUserAndStatus(user.getUsername(), request.getStatus(), page, size);
            total = applicationDao.countByCreateUserAndStatus(user.getUsername(), request.getStatus());
        }

        GetAllResponse<ApplicationResponse> getAllResponse = new GetAllResponse<>();
        List<ApplicationResponse> applicationResponses = new ArrayList<>();

        for (Application application : applicationList) {
            List<Task> tasks = taskDao.findByApplication(application);
            ApplicationResponse response = new ApplicationResponse(application, tasks);
            applicationResponses.add(response);
        }
        getAllResponse.setData(applicationResponses);
        getAllResponse.setTotal(total);

        List<String> applicationIdList = getAllResponse.getData().stream().map(ApplicationResponse::getApplicationId).collect(Collectors.toList());
        LOGGER.info("Succeed to find applications. size: {}, id of applications: {}", total, applicationIdList);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_APPLICATIONS}", getAllResponse);
    }

    @Override
    public GeneralResponse<?> filterProjectApplication(FilterProjectRequest request) throws UnExpectedRequestException {
        // Check arguments
        FilterProjectRequest.checkRequest(request);

        Long userId = HttpUtils.getUserId(httpServletRequest);
        User user = userDao.findById(userId);

        Integer page = request.getPage();
        Integer size = request.getSize();
        Long projectId = request.getProjectId();
        List<TaskRuleSimple> taskRuleSimples;
        int total;
        taskRuleSimples = taskRuleSimpleDao.findByCreateUserAndProjectId(user.getUsername(), projectId, page, size);
        total = taskRuleSimpleDao.countByCreateUserAndProjectId(user.getUsername(), projectId);

        List<Application> applicationList = taskRuleSimples.stream().map(jobRuleSimple -> jobRuleSimple.getTask().getApplication()).collect(Collectors.toList());

        GetAllResponse<ApplicationResponse> getAllResponse = new GetAllResponse<>();
        List<ApplicationResponse> applicationResponses = new ArrayList<>();
        for (Application application : applicationList) {
            List<Task> tasks = taskDao.findByApplication(application);
            ApplicationResponse response = new ApplicationResponse(application, tasks);
            applicationResponses.add(response);
        }
        getAllResponse.setData(applicationResponses);
        getAllResponse.setTotal(total);

        List<String> applicationIdList = getAllResponse.getData().stream().map(ApplicationResponse::getApplicationId).collect(Collectors.toList());
        LOGGER.info("Succeed to find applications. size: {}, id of applications: {}", total, applicationIdList);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_APPLICATIONS}", getAllResponse);
    }

    @Override
    public GeneralResponse<?> filterDataSourceApplication(FilterDataSourceRequest request) throws UnExpectedRequestException {
        // Check arguments
        FilterDataSourceRequest.checkRequest(request);

        Integer page = request.getPage();
        Integer size = request.getSize();
        String clusterName = request.getClusterName();
        String databaseName = request.getDatabaseName();
        String tableName = request.getTableName();

        Long userId = HttpUtils.getUserId(httpServletRequest);
        User user = userDao.findById(userId);

        List<TaskDataSource> taskDataSources;
        long total;
        // Find datasource by user
        taskDataSources = taskDataSourceDao.findByUserAndDataSource(user.getUsername(), clusterName, databaseName, tableName, page, size);
        total = taskDataSourceDao.countByUserAndDataSource(user.getUsername(), clusterName, databaseName, tableName);

        List<Application> applicationList = taskDataSources.stream().map(jobDataSource -> jobDataSource.getTask().getApplication()).collect(Collectors.toList());

        GetAllResponse<ApplicationResponse> getAllResponse = new GetAllResponse<>();
        List<ApplicationResponse> applicationResponses = new ArrayList<>();
        for (Application application : applicationList) {
            List<Task> tasks = taskDao.findByApplication(application);
            ApplicationResponse response = new ApplicationResponse(application, tasks);
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
     * @param applicationId
     * @return
     */
    @Override
    public GeneralResponse<?> filterApplicationId(String applicationId) {
        Long userId = HttpUtils.getUserId(httpServletRequest);
        // Find applications by user
        User user = userDao.findById(userId);

        List<Application> applicationList;
        applicationList = applicationDao.findByCreateUserAndIdLike(user.getUsername(), applicationId);

        if (applicationList == null){
            LOGGER.info("User: {} , Not find applications with applicationId: {}", user.getUsername(), applicationId);
            return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_APPLICATIONS_BUT_FIND_NO_RESULTS}", null);
        }
        GetAllResponse<ApplicationResponse> getAllResponse = new GetAllResponse<>();
        List<ApplicationResponse> applicationResponses = new ArrayList<>();

        for (Application application : applicationList) {
            List<Task> tasks = taskDao.findByApplication(application);
            ApplicationResponse response = new ApplicationResponse(application, tasks);
            applicationResponses.add(response);
        }
        getAllResponse.setData(applicationResponses);
        getAllResponse.setTotal(applicationList.size());

        List<String> applicationIdList = getAllResponse.getData().stream().map(ApplicationResponse::getApplicationId).collect(Collectors.toList());
        LOGGER.info("User: {}, find {} applications with like applicationId : {},Id of applications: {}", user.getUsername(), applicationList.size(), applicationId, applicationIdList);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_APPLICATIONS}", getAllResponse);
    }

    @Override
    public GeneralResponse<?> getDataSource(PageRequest request) throws UnExpectedRequestException {
        // Check arguments
        PageRequest.checkRequest(request);

        Integer page = request.getPage();
        Integer size = request.getSize();

        Long userId = HttpUtils.getUserId(httpServletRequest);
        User user = userDao.findById(userId);

        List<TaskDataSource> taskDataSources;
        int total;
        // Find datasource by user
        taskDataSources = taskDataSourceDao.findByUser(user.getUsername(), page, size);
        total = taskDataSourceDao.countByUser(user.getUsername());

        List<ApplicationClusterResponse> response = new ArrayList<>();
        Map<String, ApplicationClusterResponse> map = new HashMap<>(2);
        for (TaskDataSource taskDataSource : taskDataSources) {
            putIntoCluster(response, taskDataSource, map);
        }

        LOGGER.info("Succeed to find dataSources. size: {}, id of dataSources: {}", total, response);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_GET_APPLICATION_DATASOURCE}", response);
    }

    private void putIntoCluster(List<ApplicationClusterResponse> responses, TaskDataSource taskDataSource, Map<String, ApplicationClusterResponse> map) {
        String cluster = taskDataSource.getClusterName();
        if (map.containsKey(cluster)) {
            ApplicationClusterResponse response = map.get(cluster);
            putIntoDatabase(taskDataSource, response);
        } else {
            ApplicationClusterResponse response = new ApplicationClusterResponse(cluster);
            putIntoDatabase(taskDataSource, response);
            responses.add(response);
            map.put(cluster, response);
        }
    }

    private void putIntoDatabase(TaskDataSource taskDataSource, ApplicationClusterResponse applicationClusterResponse) {
        String database = taskDataSource.getDatabaseName();
        if (applicationClusterResponse.getMap().containsKey(database)) {
            ApplicationDatabaseResponse response = applicationClusterResponse.getMap().get(database);
            putIntoTable(taskDataSource, response);
        } else {
            ApplicationDatabaseResponse response = new ApplicationDatabaseResponse(database);
            response.setDatabaseName(database);
            putIntoTable(taskDataSource, response);

            applicationClusterResponse.getMap().put(database, response);
            applicationClusterResponse.getDatabase().add(response);
        }
    }

    private void putIntoTable(TaskDataSource taskDataSource, ApplicationDatabaseResponse applicationDatabaseResponse) {
        String table = taskDataSource.getTableName();
        if (!applicationDatabaseResponse.getTable().contains(table)) {
            applicationDatabaseResponse.getTable().add(table);
        }
    }
}
