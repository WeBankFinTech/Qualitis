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
import com.webank.wedatasphere.qualitis.entity.ProxyUser;
import com.webank.wedatasphere.qualitis.entity.UserProxyUser;
import com.webank.wedatasphere.qualitis.dao.repository.ProxyUserRepository;
import com.webank.wedatasphere.qualitis.dao.repository.UserProxyUserRepository;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.DeleteUserProxyUserRequest;
import com.webank.wedatasphere.qualitis.response.AddUserProxyUserResponse;
import com.webank.wedatasphere.qualitis.request.AddUserProxyUserRequest;
import com.webank.wedatasphere.qualitis.service.UserProxyUserService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author howeye
 */
@Service
public class UserProxyUserServiceImpl implements UserProxyUserService {
    @Autowired
    private UserProxyUserRepository userProxyUserRepository;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ProxyUserRepository proxyUserRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserProxyUserServiceImpl.class);

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<AddUserProxyUserResponse> addUserProxyUser(AddUserProxyUserRequest request) throws UnExpectedRequestException {
        // Check Arguments
        AddUserProxyUserRequest.checkRequest(request);

        // Find user and proxy user by username
        User userInDb = userDao.findByUsername(request.getUsername());
        if (userInDb == null) {
            throw new UnExpectedRequestException("Username {&DOES_NOT_EXIST}, request: " + request);
        }
        ProxyUser proxyUserInDb = proxyUserRepository.findByProxyUserName(request.getProxyUserName());
        if (proxyUserInDb == null) {
            throw new UnExpectedRequestException("ProxyUser {&DOES_NOT_EXIST}, request: " + request);
        }

        UserProxyUser userProxyUserInDb = userProxyUserRepository.findByUserAndProxyUser(userInDb, proxyUserInDb);
        if (userProxyUserInDb != null) {
            throw new UnExpectedRequestException("User proxy user {&ALREADY_EXIST}, request: " + request);
        }

        UserProxyUser newUserProxyUser = new UserProxyUser();
        newUserProxyUser.setProxyUser(proxyUserInDb);
        newUserProxyUser.setUser(userInDb);
        UserProxyUser savedUserProxyUser = userProxyUserRepository.save(newUserProxyUser);
        LOGGER.info("Succeed to save user proxy user. user_proxy_user_id: {}", savedUserProxyUser.getId());

        AddUserProxyUserResponse response = new AddUserProxyUserResponse(savedUserProxyUser);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_SAVE_USER_PROXY_USER}", response);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse deleteUserProxyUser(DeleteUserProxyUserRequest request) throws UnExpectedRequestException {
        // Check Arguments
        DeleteUserProxyUserRequest.checkRequest(request);

        // Find user proxy user by id
        UserProxyUser userProxyUserInDb = userProxyUserRepository.findById(request.getUserProxyUserId()).orElse(null);
        if (userProxyUserInDb == null) {
            throw new UnExpectedRequestException("User proxy user id {&DOES_NOT_EXIST}, request: " + request);
        }

        userProxyUserRepository.delete(userProxyUserInDb);
        LOGGER.info("Succeed to delete user proxy user. user_proxy_user_id: {}", request.getUserProxyUserId());
        return new GeneralResponse<>("200", "{&SUCCEED_TO_DELETE_USER_PROXY_USER}", null);
    }

    @Override
    public GeneralResponse<GetAllResponse<AddUserProxyUserResponse>> getAllUserProxyUserByProxyUserName(String proxyUserName, PageRequest request) throws UnExpectedRequestException {
        // Check Arguments
        PageRequest.checkRequest(request);

        // Check existence of proxy user
        ProxyUser proxyUserInDb = proxyUserRepository.findByProxyUserName(proxyUserName);
        if (proxyUserInDb == null) {
            throw new UnExpectedRequestException("ProxyUser name {&DOES_NOT_EXIST}, request: " + request);
        }

        // Find user proxy user by proxy user
        int page = request.getPage();
        int size = request.getSize();
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);
        List<UserProxyUser> userProxyUsers = userProxyUserRepository.findByProxyUser(proxyUserInDb, pageable);
        long total = userProxyUserRepository.countByProxyUser(proxyUserInDb);

        List<AddUserProxyUserResponse> userProxyUserResponses = new ArrayList<>();
        for (UserProxyUser userProxyUser : userProxyUsers) {
            AddUserProxyUserResponse tmp = new AddUserProxyUserResponse(userProxyUser);
            userProxyUserResponses.add(tmp);
        }
        GetAllResponse<AddUserProxyUserResponse> response = new GetAllResponse<>();
        response.setTotal(total);
        response.setData(userProxyUserResponses);

        LOGGER.info("Succeed to find all user proxy users by proxy user name, response: {}", response);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_FIND_ALL_PROXY_USERS_BY_PROXY_USER_NAME}", response);
    }
}
