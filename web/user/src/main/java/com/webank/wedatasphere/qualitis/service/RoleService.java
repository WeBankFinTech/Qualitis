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

import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.UserRole;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.request.role.RoleAddRequest;
import com.webank.wedatasphere.qualitis.request.role.RoleModifyRequest;
import com.webank.wedatasphere.qualitis.request.role.RoleRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.RoleResponse;

import com.webank.wedatasphere.qualitis.response.UserAndRoleResponse;
import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public interface RoleService {


    /**
     * Add role
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<RoleResponse> addRole(RoleAddRequest request) throws UnExpectedRequestException;

    /**
     * Delete role
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<Object> deleteRole(RoleRequest request) throws UnExpectedRequestException;

    /**
     * Modify role information
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<Object> modifyRole(RoleModifyRequest request) throws UnExpectedRequestException;

    /**
     * Paging get all roles
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<RoleResponse>> getAllRole(PageRequest request) throws UnExpectedRequestException;

    /**
     * Find all roles of user
     * @return
     */
    GeneralResponse<UserAndRoleResponse> getRoleByUser();

    /**
     * Find all proxy users of user
     * @return
     */
    GeneralResponse<List<String>> getProxyUserByUser();

    /**
     * Get role Type
     * @param userRoles
     * @return
     */
    Integer getRoleType(List<UserRole> userRoles);


    /**
     * get All Role Type enum
     *
     * @return
     */
    List<Map<String, Object>> getAllRoleTypeEnum();

    /**
     * get role list by id
     * @param ids
     * @return
     */
    List<Role> getAllById(List<Long> ids);

}
