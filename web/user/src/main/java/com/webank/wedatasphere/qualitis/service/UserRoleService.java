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


import com.webank.wedatasphere.qualitis.request.userrole.AddUserRoleRequest;
import com.webank.wedatasphere.qualitis.request.userrole.DeleteUserRoleRequest;
import com.webank.wedatasphere.qualitis.request.userrole.ModifyUserRoleRequest;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.UserRoleResponse;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.userrole.AddUserRoleRequest;
import com.webank.wedatasphere.qualitis.request.userrole.DeleteUserRoleRequest;
import com.webank.wedatasphere.qualitis.request.userrole.ModifyUserRoleRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;

/**
 * @author howeye
 */
public interface UserRoleService {

    /**
     * Add user role
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<UserRoleResponse> addUserRole(AddUserRoleRequest request) throws UnExpectedRequestException;

    /**
     * Delete user role
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> deleteUserRole(DeleteUserRoleRequest request) throws UnExpectedRequestException;

    /**
     * Modify user role
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> modifyUserRole(ModifyUserRoleRequest request) throws UnExpectedRequestException;

    /**
     * Paging get all user roles
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<UserRoleResponse>> findAllUserRole(PageRequest request) throws UnExpectedRequestException;

}
