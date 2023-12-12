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
import com.webank.wedatasphere.qualitis.request.DeleteUserTenantUserRequest;
import com.webank.wedatasphere.qualitis.response.AddUserTenantUserResponse;
import com.webank.wedatasphere.qualitis.request.AddUserTenantUserRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.request.PageRequest;

/**
 * @author allenzhou
 */
public interface UserTenantUserService {

    /**
     * Add tenant user to user
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<AddUserTenantUserResponse> addUserTenantUser(AddUserTenantUserRequest request) throws UnExpectedRequestException;

    /**
     * Remove proxy user of user
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<Object> deleteUserTenantUser(DeleteUserTenantUserRequest request) throws UnExpectedRequestException;

    /**
     * Paging get all user proxy user relationship by proxy user
     * @param request
     * @param tenantUserName
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<AddUserTenantUserResponse>> getAllUserTenantUserByTenantUserName(String tenantUserName, PageRequest request) throws UnExpectedRequestException;

}
