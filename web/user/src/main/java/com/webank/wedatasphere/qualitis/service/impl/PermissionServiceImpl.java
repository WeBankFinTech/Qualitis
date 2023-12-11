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
import com.webank.wedatasphere.qualitis.dao.RolePermissionDao;
import com.webank.wedatasphere.qualitis.dao.UserSpecPermissionDao;
import com.webank.wedatasphere.qualitis.request.permission.AddPermissionRequest;
import com.webank.wedatasphere.qualitis.request.permission.DeletePermissionRequest;
import com.webank.wedatasphere.qualitis.request.permission.ModifyPermissionRequest;
import com.webank.wedatasphere.qualitis.service.PermissionService;
import com.webank.wedatasphere.qualitis.entity.Permission;
import com.webank.wedatasphere.qualitis.entity.RolePermission;
import com.webank.wedatasphere.qualitis.entity.UserSpecPermission;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.PermissionResponse;
import com.webank.wedatasphere.qualitis.util.DateUtils;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private RolePermissionDao rolePermissionDao;

    @Autowired
    private UserSpecPermissionDao userSpecPermissionDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionServiceImpl.class);
    private HttpServletRequest httpServletRequest;

    public PermissionServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<PermissionResponse> addPermission(AddPermissionRequest request) throws UnExpectedRequestException {
        // Check Arguments
        AddPermissionRequest.checkRequest(request);
        String method = request.getMethod().trim();
        String url = request.getUrl().trim();

        // Find permission by method and url
        Permission permissionInDb = permissionDao.findByMethodAndUrl(method, url);
        if (permissionInDb != null) {
            throw new UnExpectedRequestException("{&METHOD_AND_URL_ALREADY_EXIST}, request: " + request);
        }

        Permission newPermission = new Permission();
        newPermission.setMethod(method);
        newPermission.setUrl(url);
        newPermission.setCreateUser(HttpUtils.getUserName(httpServletRequest));
        newPermission.setCreateTime(DateUtils.now());
        Permission savedPermission = permissionDao.savePermission(newPermission);

        LOGGER.info("Succeed to add permission, id: {}, method: {}, url: {}, current_user: {}", savedPermission.getId(), method, url, HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&ADD_PERMISSION_SUCCESSFULLY}", new PermissionResponse(savedPermission));
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse deletePermission(DeletePermissionRequest request) throws UnExpectedRequestException {
        // Check Arguments
        checkRequest(request);

        Long permissionId = request.getPermissionId();
        // Find permissions by permission id
        Permission permissionInDb = permissionDao.findById(permissionId);
        if (permissionInDb == null) {
            throw new UnExpectedRequestException("{&PERMISSION_ID_NOT_EXIST}, request: " + request);
        }
        List<RolePermission> rolePermissionsInDb = rolePermissionDao.findByPermission(permissionInDb);
        if (null != rolePermissionsInDb && !rolePermissionsInDb.isEmpty()) {
            throw new UnExpectedRequestException("{&DELETE_ERROR_ROLE_PERMISSION_HAS_FOREIGN_KEY}");
        }
        List<UserSpecPermission> userSpecPermissionsInDb = userSpecPermissionDao.findByPermission(permissionInDb);
        if (null != userSpecPermissionsInDb && !userSpecPermissionsInDb.isEmpty()) {
            throw new UnExpectedRequestException("{&DELETE_ERROR_USER_SPEC_PERMISSION_HAS_FOREIGN_KEY}");
        }

        permissionDao.deletePermission(permissionInDb);

        LOGGER.info("Succeed to delete permission, permissionId: {}, method: {}, url: {}, current_user: {}", permissionId, permissionInDb.getMethod(), permissionInDb.getUrl(), HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&DELETE_PERMISSION_SUCCESSFULLY}", null);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse modifyPermission(ModifyPermissionRequest request) throws UnExpectedRequestException {
        // Check Arguments
        checkRequest(request);

        Long id = request.getPermissionId();
        String method = request.getMethod().trim();
        String url = request.getUrl().trim();
        Permission permissionInDb = permissionDao.findById(id);
        if (permissionInDb == null) {
            throw new UnExpectedRequestException("{&PERMISSION_ID_NOT_EXIST}, request: " + request);
        }
        LOGGER.info("Succeed to find permission, permissionId: {}, method: {}, url: {}, current_user: {}", permissionInDb.getId(), permissionInDb.getMethod(),
                permissionInDb.getUrl(), HttpUtils.getUserName(httpServletRequest));

        permissionInDb.setUrl(url);
        permissionInDb.setMethod(method);
        permissionInDb.setModifyUser(HttpUtils.getUserName(httpServletRequest));
        permissionInDb.setModifyTime(DateUtils.now());
        permissionDao.savePermission(permissionInDb);
        LOGGER.info("Succeed to modify permission, permissionId: {}, method: {}, url: {}, current_user: {}", id, method, url, HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&MODIFY_PERMISSION_SUCCESSFULLY}", null);
    }

    @Override
    public GeneralResponse<GetAllResponse<PermissionResponse>> getAllPermission(PageRequest request) throws UnExpectedRequestException {
        // Check Arguments
        PageRequest.checkRequest(request);

        int page = request.getPage();
        int size = request.getSize();
        List<Permission> permissions = permissionDao.findAllPermission(page, size);
        long total = permissionDao.countAll();
        GetAllResponse<PermissionResponse> getAllPermissionResponse = new GetAllResponse<>();
        getAllPermissionResponse.setTotal(total);
        getAllPermissionResponse.setData(permissions.stream().map(p -> new PermissionResponse(p)).collect(Collectors.toList()));

        LOGGER.info("Succeed to get all permission, page: {}, size: {}, permissions: {}, current_user: {}", page, size, getAllPermissionResponse, HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&FIND_ALL_PERMISSIONS_SUCCESSFULLY}", getAllPermissionResponse);
    }

    private void checkRequest(DeletePermissionRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        checkId(request.getPermissionId());
    }

    private void checkRequest(ModifyPermissionRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        checkId(request.getPermissionId());
        checkMethod(request.getMethod());
        checkUrl(request.getUrl());
    }

    private void checkId(Long id) throws UnExpectedRequestException {
        if (null == id) {
            throw new UnExpectedRequestException("id " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }

    private void checkMethod(String method) throws UnExpectedRequestException {
        if (StringUtils.isBlank(method)) {
            throw new UnExpectedRequestException("method " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }

    private void checkUrl(String url) throws UnExpectedRequestException {
        if (StringUtils.isBlank(url)) {
            throw new UnExpectedRequestException("url " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }
}
