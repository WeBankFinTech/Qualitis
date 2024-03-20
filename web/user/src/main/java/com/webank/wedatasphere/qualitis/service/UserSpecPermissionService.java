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
import com.webank.wedatasphere.qualitis.request.userpermission.AddUserSpecPermissionRequest;
import com.webank.wedatasphere.qualitis.request.userpermission.DeleteUserSpecPermissionRequest;
import com.webank.wedatasphere.qualitis.request.userpermission.ModifyUserSpecPermissionRequest;
import com.webank.wedatasphere.qualitis.response.UserSpecPermissionResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.request.PageRequest;

/**
 * @author howeye
 */
public interface UserSpecPermissionService {

    /**
     * Add user permission
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<UserSpecPermissionResponse> addUserSpecPermission(AddUserSpecPermissionRequest request) throws UnExpectedRequestException;

    /**
     * Delete user permission
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<Object> deleteUserSpecPermission(DeleteUserSpecPermissionRequest request) throws UnExpectedRequestException ;

    /**
     * Modify user permission
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<Object> modifyUserSpecPermission(ModifyUserSpecPermissionRequest request) throws UnExpectedRequestException;

    /**
     * Paging get all user permissions
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<UserSpecPermissionResponse>> findAllUserSpecPermission(PageRequest request) throws UnExpectedRequestException ;

}
