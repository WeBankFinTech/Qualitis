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

import com.webank.wedatasphere.qualitis.request.rolepermission.AddRolePermissionRequest;
import com.webank.wedatasphere.qualitis.request.rolepermission.DeleteRolePermissionRequest;
import com.webank.wedatasphere.qualitis.request.rolepermission.ModifyRolePermissionRequest;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.RolePermissionResponse;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.rolepermission.AddRolePermissionRequest;
import com.webank.wedatasphere.qualitis.request.rolepermission.DeleteRolePermissionRequest;
import com.webank.wedatasphere.qualitis.request.rolepermission.ModifyRolePermissionRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;

/**
 * @author howeye
 */
public interface RolePermissionService {

    /**
     * Add role permission
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<RolePermissionResponse> addRolePermission(AddRolePermissionRequest request) throws UnExpectedRequestException;

    /**
     * Delete role permission
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> deleteRolePermission(DeleteRolePermissionRequest request) throws UnExpectedRequestException;

    /**
     * Modify role permission
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> modifyRolePermission(ModifyRolePermissionRequest request) throws UnExpectedRequestException;

    /**
     * Paging get all role permissions
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<RolePermissionResponse>> findAllRolePermission(PageRequest request) throws UnExpectedRequestException;

}
