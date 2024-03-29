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
import com.webank.wedatasphere.qualitis.request.AddTenantUserRequest;
import com.webank.wedatasphere.qualitis.request.DeleteTenantUserRequest;
import com.webank.wedatasphere.qualitis.request.ModifyTenantUserRequest;
import com.webank.wedatasphere.qualitis.request.QueryTenantUserRequest;
import com.webank.wedatasphere.qualitis.response.AddTenantUserResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;

/**
 * @author allenzhou
 */
public interface TenantUserService {

    /**
     * Add tenant user
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<AddTenantUserResponse> addTenantUser(AddTenantUserRequest request) throws UnExpectedRequestException;

    /**
     * Delete tenant user
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse deleteTenantUser(DeleteTenantUserRequest request) throws UnExpectedRequestException;

    /**
     * Modify tenant user name
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse modifyTenantUser(ModifyTenantUserRequest request) throws UnExpectedRequestException;

    /**
     * Paging get all tenant users
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<AddTenantUserResponse>> getAllTenantUser(QueryTenantUserRequest request) throws UnExpectedRequestException;

}
