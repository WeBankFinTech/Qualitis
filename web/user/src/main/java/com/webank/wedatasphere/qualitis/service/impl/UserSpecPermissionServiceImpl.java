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

import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.Permission;
import com.webank.wedatasphere.qualitis.dao.PermissionDao;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.dao.UserSpecPermissionDao;
import com.webank.wedatasphere.qualitis.entity.UserSpecPermission;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.userpermission.AddUserSpecPermissionRequest;
import com.webank.wedatasphere.qualitis.request.userpermission.DeleteUserSpecPermissionRequest;
import com.webank.wedatasphere.qualitis.request.userpermission.ModifyUserSpecPermissionRequest;
import com.webank.wedatasphere.qualitis.response.UserSpecPermissionResponse;
import com.webank.wedatasphere.qualitis.service.UserSpecPermissionService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.util.DateUtils;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author howeye
 */
@Service
public class UserSpecPermissionServiceImpl implements UserSpecPermissionService {

    @Autowired
    private UserSpecPermissionDao userSpecPermissionDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PermissionDao permissionDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSpecPermissionServiceImpl.class);
    private HttpServletRequest httpServletRequest;

    public UserSpecPermissionServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<UserSpecPermissionResponse> addUserSpecPermission(AddUserSpecPermissionRequest request) throws UnExpectedRequestException {
        // Check Arguments
        checkRequest(request);

        // Check existence of user permission
        long userId = request.getUserId();
        long permissionId = request.getPermissionId();
        User userInDb = userDao.findById(userId);
        if (userInDb == null) {
            throw new UnExpectedRequestException("user id {&DOES_NOT_EXIST}, request: " + request);
        }
        Permission permissionInDb = permissionDao.findById(permissionId);
        if (permissionInDb == null) {
            throw new UnExpectedRequestException("permission id {&DOES_NOT_EXIST}, request: " + request);
        }
        UserSpecPermission userSpecPermissionInDb = userSpecPermissionDao.findByUserAndPermission(userInDb, permissionInDb);
        if (userSpecPermissionInDb != null) {
            throw new UnExpectedRequestException("user id and permission id {&ALREADY_EXIST}, request: " + request);
        }

        // Save new user permissions
        UserSpecPermission newUserSpecPermission = new UserSpecPermission();
        newUserSpecPermission.setUser(userInDb);
        newUserSpecPermission.setPermission(permissionInDb);
        newUserSpecPermission.setId(UuidGenerator.generate());
        newUserSpecPermission.setCreateUser(HttpUtils.getUserName(httpServletRequest));
        newUserSpecPermission.setCreateTime(DateUtils.now());
        UserSpecPermission savedUserSpecPermission = userSpecPermissionDao.saveUserSpecPermission(newUserSpecPermission);
        UserSpecPermissionResponse response = new UserSpecPermissionResponse(savedUserSpecPermission);

        LOGGER.info("Succeed to add user_permission, response: {}, current_user: {}", response, HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&ADD_USER_SPEC_PERMISSION_SUCCESSFULLY}", response);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse deleteUserSpecPermission(DeleteUserSpecPermissionRequest request) throws UnExpectedRequestException {
        // Check Arguments
        checkRequest(request);

        // Check existence of user permission by id
        String uuid = request.getUuid();
        UserSpecPermission userSpecPermissionInDb = userSpecPermissionDao.findByUuid(uuid);
        if (userSpecPermissionInDb == null) {
            throw new UnExpectedRequestException("user_spec_permission id {&DOES_NOT_EXIST}, request: " + request);
        }

        // Delete user permission
        userSpecPermissionDao.deleteUserSpecPermission(userSpecPermissionInDb);
        LOGGER.info("Succeed to delete user_permission, uuid: {}, current_user: {}", uuid, HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&DELETE_USER_SPEC_PERMISSION_SUCCESSFULLY}", null);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse modifyUserSpecPermission(ModifyUserSpecPermissionRequest request) throws UnExpectedRequestException {
        // Check Arguments
        checkRequest(request);

        // Find user permission by id
        String uuid = request.getUuid();
        UserSpecPermission userSpecPermissionInDb = userSpecPermissionDao.findByUuid(uuid);
        if (userSpecPermissionInDb == null) {
            throw new UnExpectedRequestException("user_spec_permission id {&DOES_NOT_EXIST}, request: " + request);
        }
        LOGGER.info("Succeed to find user_permission. uuid: {}, user_id: {}, permission_id: {}, current_user: {}", uuid,
                userSpecPermissionInDb.getUser().getId(), userSpecPermissionInDb.getPermission().getId(), HttpUtils.getUserName(httpServletRequest));

        // Check existence of user and permission
        long userId = request.getUserId();
        long permissionId = request.getPermissionId();

        User userInDb = userDao.findById(userId);
        if (userInDb == null) {
            throw new UnExpectedRequestException("user id {&DOES_NOT_EXIST}, request: " + request);
        }
        Permission permissionInDb = permissionDao.findById(permissionId);
        if (permissionInDb == null) {
            throw new UnExpectedRequestException("permission id {&DOES_NOT_EXIST}, request: " + request);
        }
        UserSpecPermission userIdAndPermissionIdInDb = userSpecPermissionDao.findByUserAndPermission(userInDb, permissionInDb);
        if (userIdAndPermissionIdInDb != null) {
            throw new UnExpectedRequestException("user id and permission id {&ALREADY_EXIST}, request: " + request);
        }

        // Save user permission
        userSpecPermissionInDb.setUser(userInDb);
        userSpecPermissionInDb.setPermission(permissionInDb);
        userSpecPermissionInDb.setModifyUser(HttpUtils.getUserName(httpServletRequest));
        userSpecPermissionInDb.setModifyTime(DateUtils.now());
        UserSpecPermission savedUserSpecPermission = userSpecPermissionDao.saveUserSpecPermission(userSpecPermissionInDb);

        LOGGER.info("Succeed to find user_permission. uuid: {}, user_id: {}, permission_id: {}, current_user: {}", uuid,
                savedUserSpecPermission.getUser().getId(), savedUserSpecPermission.getPermission().getId(), HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&MODIFY_USER_SPEC_PERMISSION_SUCCESSFULLY}", null);
    }

    @Override
    public GeneralResponse<GetAllResponse<UserSpecPermissionResponse>> findAllUserSpecPermission(PageRequest request) throws UnExpectedRequestException {
        // Check Arguments
        PageRequest.checkRequest(request);

        int page = request.getPage();
        int size = request.getSize();
        List<UserSpecPermission> userSpecPermissions = userSpecPermissionDao.findAllUserSpecPermission(page, size);
        long total = userSpecPermissionDao.countAll();
        List<UserSpecPermissionResponse> userSpecPermissionResponses = new ArrayList<>();
        for (UserSpecPermission userSpecPermission : userSpecPermissions) {
            UserSpecPermissionResponse tmp = new UserSpecPermissionResponse(userSpecPermission);
            userSpecPermissionResponses.add(tmp);
        }
        GetAllResponse<UserSpecPermissionResponse> responses = new GetAllResponse<>();
        responses.setTotal(total);
        responses.setData(userSpecPermissionResponses);

        LOGGER.info("Succeed to find user_permission. page: {}, size: {}, response: {}, current_user: {}", page, size, responses, HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&FIND_USER_SPEC_PERMISSION_SUCCESSFULLY}", responses);
    }

    private void checkRequest(AddUserSpecPermissionRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        checkId(request.getPermissionId(), "permissionId");
        checkId(request.getUserId(), "userId");
    }

    private void checkRequest(DeleteUserSpecPermissionRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        checkUuid(request.getUuid());
    }

    private void checkUuid(String uuid) throws UnExpectedRequestException {
        if (StringUtils.isBlank(uuid)) {
            throw new UnExpectedRequestException("uuid {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }

    private void checkRequest(ModifyUserSpecPermissionRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        checkUuid(request.getUuid());
        checkId(request.getPermissionId(), "permissionId");
        checkId(request.getUserId(), "userId");
    }

    private void checkId(Long id, String idName) throws UnExpectedRequestException {
        if (null == id) {
            throw new UnExpectedRequestException(idName + " {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }
}
