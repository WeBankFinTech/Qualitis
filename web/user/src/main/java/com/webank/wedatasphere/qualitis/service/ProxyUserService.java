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

import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.ProxyUserDepartment;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.AddProxyUserRequest;
import com.webank.wedatasphere.qualitis.request.DeleteProxyUserRequest;
import com.webank.wedatasphere.qualitis.request.ModifyProxyUserRequest;
import com.webank.wedatasphere.qualitis.request.QueryProxyUserRequest;
import com.webank.wedatasphere.qualitis.response.AddProxyUserResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;

import java.util.List;

/**
 * @author howeye
 */
public interface ProxyUserService {

    /**
     * Add proxy user
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<AddProxyUserResponse> addProxyUser(AddProxyUserRequest request) throws UnExpectedRequestException;

    /**
     * Delete proxy user
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<Object> deleteProxyUser(DeleteProxyUserRequest request) throws UnExpectedRequestException;

    /**
     * Modify proxy user name
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<Object> modifyProxyUser(ModifyProxyUserRequest request) throws UnExpectedRequestException;

    /**
     * Paging get all proxy users
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<AddProxyUserResponse>> getAllProxyUser(QueryProxyUserRequest request) throws UnExpectedRequestException;

    /**
     * Get all names
     * @return
     */
    List<String> getAllProxyUserName();

    /**
     * sync Department Name
     * @param department
     * @param departmentName
     * @return
     */
    void syncDepartmentName(Department department, String departmentName);

    /**
     * sync Sub Department Name
     * @param subDepartmentCode
     * @param departmentName
     * @return
     */
    void syncSubDepartmentName(Long subDepartmentCode, String departmentName);

    /**
     * find By Sub Department Code
     * @param subDepartmentCode
     * @param subDepartmentCode
     * @return
     */
    List<ProxyUserDepartment> findBySubDepartmentCode(Long subDepartmentCode);

    /**
     * add proxy user if not exists
     * @param proxyUser
     * @return
     */
    void addProxyUserIfNotExists(String proxyUser);
}
