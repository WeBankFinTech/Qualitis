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

import com.webank.wedatasphere.qualitis.request.permission.AddPermissionRequest;
import com.webank.wedatasphere.qualitis.request.permission.DeletePermissionRequest;
import com.webank.wedatasphere.qualitis.request.permission.ModifyPermissionRequest;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.PermissionResponse;

/**
 * @author howeye
 */
public interface PermissionService {

    /**
     * Add permission object
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<PermissionResponse> addPermission(AddPermissionRequest request) throws UnExpectedRequestException;

    /**
     * Delete permission object
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse deletePermission(DeletePermissionRequest request) throws UnExpectedRequestException;

    /**
     * Modify permission object
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse modifyPermission(ModifyPermissionRequest request) throws UnExpectedRequestException;

    /**
     * Paging get all permissions
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<PermissionResponse>> getAllPermission(PageRequest request) throws UnExpectedRequestException;

}
