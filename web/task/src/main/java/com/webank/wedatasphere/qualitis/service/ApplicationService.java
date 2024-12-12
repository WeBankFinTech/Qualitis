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

package com.webank.wedatasphere.qualitis.service;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.request.FilterAdvanceRequest;
import com.webank.wedatasphere.qualitis.request.FilterDataSourceRequest;
import com.webank.wedatasphere.qualitis.request.FilterProjectRequest;
import com.webank.wedatasphere.qualitis.request.FilterStatusRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.request.UploadResultRequest;
import com.webank.wedatasphere.qualitis.response.ApplicationClusterResponse;
import com.webank.wedatasphere.qualitis.response.ApplicationResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;

import java.io.IOException;
import java.util.List;

/**
 * @author howeye
 */
public interface ApplicationService {


    /**
     * Find applications information by application status
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<ApplicationResponse>> filterStatusApplication(FilterStatusRequest request) throws UnExpectedRequestException;


    /**
     * Find applications information by project
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<ApplicationResponse>> filterProjectApplication(FilterProjectRequest request) throws UnExpectedRequestException;


    /**
     * Paging get all datasources
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<List<ApplicationClusterResponse>> getDataSource(PageRequest request) throws UnExpectedRequestException;

    /**
     * 根据数据源过滤查询application信息
     * @param request 数据源过滤请求
     * @return 请求返回值
     * @throws UnExpectedRequestException 参数错误时抛出的异常
     */
    GeneralResponse<GetAllResponse<ApplicationResponse>> filterDataSourceApplication(FilterDataSourceRequest request) throws UnExpectedRequestException;

    /**
     * Find application by applicationId
     * @param applicationId
     * @param filterStatus
     * @param page
     * @param size
     * @param taskPage
     * @param taskSize
     * @return
     */
    GeneralResponse<GetAllResponse<ApplicationResponse>> filterApplicationId(String applicationId, Integer filterStatus, Integer page, Integer size,
        Integer taskPage, Integer taskSize);

    /**
     * Upload datasource analysis result. such as: rules, check, task result value.
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws IOException
     */
    GeneralResponse<Integer> uploadDataSourceAnalysisResult(UploadResultRequest request)
        throws UnExpectedRequestException, MetaDataAcquireFailedException, IOException;

    /**
     * Advance filter applications.
     * @param request
     * @return
     */
    GeneralResponse<GetAllResponse<ApplicationResponse>> filterAdvanceApplication(FilterAdvanceRequest request);

    /**
     * get All ExecuteUser
     *
     * @return
     */
    GeneralResponse<List<String>> getAllExecuteUser();

}
