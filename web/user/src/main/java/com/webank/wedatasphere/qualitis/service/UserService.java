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
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.request.user.ModifyDepartmentRequest;
import com.webank.wedatasphere.qualitis.request.user.ModifyPasswordRequest;
import com.webank.wedatasphere.qualitis.request.user.UserAddRequest;
import com.webank.wedatasphere.qualitis.request.user.UserRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.user.AddUserResponse;
import com.webank.wedatasphere.qualitis.response.user.UserResponse;
import javax.management.relation.RoleNotFoundException;

/**
 * @author howeye
 */
public interface UserService {


    /**
     * Add user
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<AddUserResponse> addUser(UserAddRequest request) throws UnExpectedRequestException;


    /**
     * Delete user
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> deleteUser(UserRequest request) throws UnExpectedRequestException;

    /**
     * Initial password of user
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<String> initPassword(UserRequest request) throws UnExpectedRequestException;

    /**
     * Paging get all users
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<UserResponse>> findAllUser(PageRequest request) throws UnExpectedRequestException;


    /**
     * Modify user password
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> modifyPassword(ModifyPasswordRequest request)  throws UnExpectedRequestException;

    /**
     * Auto register user
     * @param username
     * @throws RoleNotFoundException
     */
    void autoAddUser(String username) throws RoleNotFoundException;

    /**
     * Modify user department
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> modifyDepartment(ModifyDepartmentRequest request) throws UnExpectedRequestException;
}
