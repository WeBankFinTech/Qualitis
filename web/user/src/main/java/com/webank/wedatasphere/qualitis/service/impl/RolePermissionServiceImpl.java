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

package com.webank.wedatasphere.qualitis.service.impl;

import com.webank.wedatasphere.qualitis.dao.PermissionDao;
import com.webank.wedatasphere.qualitis.dao.RoleDao;
import com.webank.wedatasphere.qualitis.dao.RolePermissionDao;
import com.webank.wedatasphere.qualitis.request.rolepermission.AddRolePermissionRequest;
import com.webank.wedatasphere.qualitis.request.rolepermission.DeleteRolePermissionRequest;
import com.webank.wedatasphere.qualitis.request.rolepermission.ModifyRolePermissionRequest;
import com.webank.wedatasphere.qualitis.service.RolePermissionService;
import com.webank.wedatasphere.qualitis.entity.Permission;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.RolePermission;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.RolePermissionResponse;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import com.webank.wedatasphere.qualitis.dao.PermissionDao;
import com.webank.wedatasphere.qualitis.dao.RoleDao;
import com.webank.wedatasphere.qualitis.dao.RolePermissionDao;
import com.webank.wedatasphere.qualitis.entity.Permission;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.RolePermission;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.rolepermission.AddRolePermissionRequest;
import com.webank.wedatasphere.qualitis.request.rolepermission.DeleteRolePermissionRequest;
import com.webank.wedatasphere.qualitis.request.rolepermission.ModifyRolePermissionRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.RolePermissionResponse;
import com.webank.wedatasphere.qualitis.service.RolePermissionService;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    @Autowired
    private RolePermissionDao rolePermissionDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PermissionDao permissionDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(RolePermissionServiceImpl.class);
    private HttpServletRequest httpServletRequest;

    public RolePermissionServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<RolePermissionResponse> addRolePermission(AddRolePermissionRequest request) throws UnExpectedRequestException {
        // Check Arguments
        checkRequest(request);

        // Check existence of role and permission
        long roleId = request.getRoleId();
        long permissionId = request.getPermissionId();
        Role roleInDb = roleDao.findById(roleId);
        if (roleInDb == null) {
            throw new UnExpectedRequestException("role id {&DOES_NOT_EXIST}, request: " + request);
        }
        Permission permissionInDb = permissionDao.findById(permissionId);
        if (permissionInDb == null) {
            throw new UnExpectedRequestException("permission id {&DOES_NOT_EXIST}, request: " + request);
        }
        RolePermission rolePermissionInDb = rolePermissionDao.findByRoleAndPermission(roleInDb, permissionInDb);
        if (rolePermissionInDb != null) {
            throw new UnExpectedRequestException("role and permission {&ALREADY_EXIST}, request: " + request);
        }

        // Save new role permission
        RolePermission newRolePermission = new RolePermission();
        newRolePermission.setPermission(permissionInDb);
        newRolePermission.setRole(roleInDb);
        newRolePermission.setId(UuidGenerator.generate());
        RolePermission savedRolePermission = rolePermissionDao.saveRolePermission(newRolePermission);
        RolePermissionResponse response = new RolePermissionResponse(savedRolePermission);

        LOGGER.info("Succeed to add role_permission, response: {}, current_user: {}", response, HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&ADD_ROLE_PERMISSION_SUCCESSFULLY}", response);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> deleteRolePermission(DeleteRolePermissionRequest request) throws UnExpectedRequestException {
        // Check Arguments
        checkRequest(request);

        // Find role permission by id
        String uuid = request.getUuid();
        RolePermission rolePermissionInDb = rolePermissionDao.findByUuid(uuid);
        if (rolePermissionInDb == null) {
            throw new UnExpectedRequestException("role permission id {&DOES_NOT_EXIST}, request: " + request);
        }

        // Delete role permission
        rolePermissionDao.deleteRolePermission(rolePermissionInDb);
        LOGGER.info("Succeed to delete role_permission, uuid: {}, role_id: {}, permission_id: {}, current_user: {}", uuid, rolePermissionInDb.getRole().getId(),
                rolePermissionInDb.getPermission().getId(), HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&DELETE_ROLE_PERMISSION_SUCCESSFULLY}", null);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> modifyRolePermission(ModifyRolePermissionRequest request) throws UnExpectedRequestException {
        // Check Arguments
        checkRequest(request);

        // Check existence of role permission
        String uuid = request.getUuid();
        RolePermission rolePermissionInDb = rolePermissionDao.findByUuid(uuid);
        if (rolePermissionInDb == null) {
            throw new UnExpectedRequestException("role permission id {&DOES_NOT_EXIST}, request: " + request);
        }
        LOGGER.info("Succeed to find role_permission, uuid: {}, role_id: {}, permission_id: {}, current_user: {}", uuid, rolePermissionInDb.getRole().getId(),
                rolePermissionInDb.getPermission().getId(), HttpUtils.getUserName(httpServletRequest));

        long roleId = request.getRoleId();
        long permissionId = request.getPermissionId();
        Role roleInDb = roleDao.findById(roleId);
        if (roleInDb == null) {
            throw new UnExpectedRequestException("role id {&DOES_NOT_EXIST}, request: " + request);
        }
        Permission permissionInDb = permissionDao.findById(permissionId);
        if (permissionInDb == null) {
            throw new UnExpectedRequestException("permission id {&DOES_NOT_EXIST}, request: " + request);
        }
        RolePermission roleIdAndPermissionIdInDb = rolePermissionDao.findByRoleAndPermission(roleInDb, permissionInDb);
        if (roleIdAndPermissionIdInDb != null) {
            throw new UnExpectedRequestException("role and permission {&ALREADY_EXIST}, request: " + request);
        }

        // Save role permission
        rolePermissionInDb.setRole(roleInDb);
        rolePermissionInDb.setPermission(permissionInDb);
        RolePermission savedRolePermission = rolePermissionDao.saveRolePermission(rolePermissionInDb);
        LOGGER.info("Succeed to modify role_permission, uuid: {}, role_id: {}, permission_id: {}, current_user: {}", uuid, savedRolePermission.getRole().getId(),
                savedRolePermission.getPermission().getId(), HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&MODIFY_ROLE_PERMISSION_SUCCESSFULLY}", null);
    }

    @Override
    public GeneralResponse<GetAllResponse<RolePermissionResponse>> findAllRolePermission(PageRequest request) throws UnExpectedRequestException {
        // Check Arguments
        PageRequest.checkRequest(request);

        int page = request.getPage();
        int size = request.getSize();
        List<RolePermission> rolePermissions = rolePermissionDao.findAllRolePermission(page, size);
        long total = rolePermissionDao.countAll();
        List<RolePermissionResponse> rolePermissionResponses = new ArrayList<>();
        for (RolePermission rolePermission : rolePermissions) {
            RolePermissionResponse tmp = new RolePermissionResponse(rolePermission);
            rolePermissionResponses.add(tmp);
        }
        GetAllResponse<RolePermissionResponse> responses = new GetAllResponse<>();
        responses.setData(rolePermissionResponses);
        responses.setTotal(total);

        LOGGER.info("Succeed to find all role_permission, response: {}, current_user: {}", responses, HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&FIND_ALL_ROLE_PERMISSION_SUCCESSFULLY}", responses);
    }

    private void checkRequest(AddRolePermissionRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        checkId(request.getPermissionId(), "permissionId");
        checkId(request.getRoleId(), "roleId");
    }

    private void checkRequest(DeleteRolePermissionRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        checkUuid(request.getUuid());
    }

    private void checkRequest(ModifyRolePermissionRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        checkUuid(request.getUuid());
        checkId(request.getPermissionId(), "permissionId");
        checkId(request.getRoleId(), "roleId");
    }

    private void checkUuid(String uuid) throws UnExpectedRequestException {
        if (StringUtils.isBlank(uuid)) {
            throw new UnExpectedRequestException("uuid {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }

    private void checkId(Long id, String idName) throws UnExpectedRequestException {
        if (id == null) {
            throw new UnExpectedRequestException(idName + " {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }
}
