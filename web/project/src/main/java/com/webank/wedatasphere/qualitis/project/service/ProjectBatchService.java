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

package com.webank.wedatasphere.qualitis.project.service;

import com.alibaba.excel.event.AnalysisEventListener;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.excel.ExcelExecutionParametersByProject;
import com.webank.wedatasphere.qualitis.project.excel.ExcelGroupByProject;
import com.webank.wedatasphere.qualitis.project.excel.ExcelRuleMetric;
import com.webank.wedatasphere.qualitis.project.request.DiffVariableRequest;
import com.webank.wedatasphere.qualitis.project.request.DownloadProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.UploadProjectRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.json.JSONException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

/**
 * @author howeye
 */
public interface ProjectBatchService {

    /**
     * Handle metric
     * @param user
     * @param excelMetricContent
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws IOException
     */
    void handleMetricData(User user, List<ExcelRuleMetric> excelMetricContent) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException;

    /**
     * Handle execution parameters
     * @param excelExecutionParametersByProjects
     * @param userName
     * @param updateProjectId
     * @throws IOException
     */
    void handleExecutionParameters(List<ExcelExecutionParametersByProject> excelExecutionParametersByProjects,
        String userName, Long updateProjectId) throws IOException;

    /**
     * Handle execution parameters real
     * @param executionParameterJsonObject
     * @param userName
     * @param updateProjectId
     * @throws IOException
     */
    void handleExecutionParametersReal(String executionParameterJsonObject, String userName, Long updateProjectId) throws IOException;

    /**
     * Handle table group.
     * @param listener
     * @param projectId
     * @param diffVariableRequestList
     * @throws IOException
     * @throws UnExpectedRequestException
     */
    void handleTableGroup(AnalysisEventListener listener, Long projectId, List<DiffVariableRequest> diffVariableRequestList)
        throws IOException, UnExpectedRequestException;

    /**
     * Create datasource
     * @param ruleDataSourcesFromJson
     * @param object
     * @param diffVariableRequestList
     * @throws UnExpectedRequestException
     */
    void createDatasourceEnv(Set<RuleDataSource> ruleDataSourcesFromJson, Object object,
        List<DiffVariableRequest> diffVariableRequestList) throws UnExpectedRequestException;

    /**
     * Clear datasource
     * @param object
     */
    void clearDatasourceEnv(Object object);

    /**
     * For aomp deploy.
     * @param fileInputStream
     * @param fileName
     * @param userName
     * @return
     * @throws IOException
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws JSONException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse uploadProjectFromAomp(InputStream fileInputStream, FormDataContentDisposition fileName, String userName)
        throws IOException, UnExpectedRequestException, PermissionDeniedRequestException, JSONException, MetaDataAcquireFailedException;

    /**
     * Get table group
     * @param rules
     * @param diffVariableRequestList
     * @return
     * @throws IOException
     */
    List<ExcelGroupByProject> getTableGroup(List<Rule> rules,
        List<DiffVariableRequest> diffVariableRequestList) throws IOException;

    /**
     * Replace placeholder
     * @param ruleOrGroup
     * @param diffVariableRequestList
     */
    void replaceDiffVariable(Object ruleOrGroup, List<DiffVariableRequest> diffVariableRequestList);

    /**
     * Get execution parameter
     * @param projects
     * @param executionParamNames
     * @param batchRules
     * @return
     * @throws IOException
     */
    List<ExcelExecutionParametersByProject> getExecutionParameters(List<Project> projects, List<String> executionParamNames, boolean batchRules) throws IOException;

    /**
     * Upload from local
     * @param request
     * @param aomp
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws IOException
     * @throws JSONException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse uploadProjectFromLocalOrGit(UploadProjectRequest request, boolean aomp)
        throws UnExpectedRequestException, PermissionDeniedRequestException, IOException, JSONException, MetaDataAcquireFailedException;

    /**
     * Download to local
     * @param downloadProjectRequest
     * @param response
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws IOException
     */
    GeneralResponse downloadProjectsToLocalOrGit(DownloadProjectRequest downloadProjectRequest, HttpServletResponse response)
        throws UnExpectedRequestException, PermissionDeniedRequestException, IOException;

    /**
     * List diff variable
     * @return
     */
    GeneralResponse diffVariables();
}
