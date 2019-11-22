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

import com.webank.wedatasphere.qualitis.dao.repository.ProxyUserRepository;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.AddProxyUserRequest;
import com.webank.wedatasphere.qualitis.request.ModifyProxyUserRequest;
import com.webank.wedatasphere.qualitis.response.AddProxyUserResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.entity.ProxyUser;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.DeleteProxyUserRequest;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.service.ProxyUserService;
import com.webank.wedatasphere.qualitis.dao.repository.ProxyUserRepository;
import com.webank.wedatasphere.qualitis.entity.ProxyUser;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.AddProxyUserRequest;
import com.webank.wedatasphere.qualitis.request.ModifyProxyUserRequest;
import com.webank.wedatasphere.qualitis.response.AddProxyUserResponse;
import com.webank.wedatasphere.qualitis.response.DeleteProxyUserRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.ProxyUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
@Service
public class ProxyUserServiceImpl implements ProxyUserService {

    @Autowired
    private ProxyUserRepository proxyUserRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyUserServiceImpl.class);

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> addProxyUser(AddProxyUserRequest request) throws UnExpectedRequestException {
        // Check Arguments
        AddProxyUserRequest.checkRequest(request);

        // Check existence of proxy user
        ProxyUser proxyUserInDb = proxyUserRepository.findByProxyUserName(request.getProxyUserName());
        if (proxyUserInDb != null) {
            throw new UnExpectedRequestException("ProxyUser name: [" + request.getProxyUserName() +  "] {&ALREADY_EXIST}");
        }

        // Save proxy user
        ProxyUser proxyUser = new ProxyUser();
        proxyUser.setProxyUserName(request.getProxyUserName());
        ProxyUser savedProxyUser = proxyUserRepository.save(proxyUser);
        LOGGER.info("Succeed to save proxyUser. proxy_user: {}", savedProxyUser.getProxyUserName());

        AddProxyUserResponse response = new AddProxyUserResponse(savedProxyUser);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_SAVE_PROXYUSER}", response);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> deleteProxyUser(DeleteProxyUserRequest request) throws UnExpectedRequestException {
        // Check Arguments
        DeleteProxyUserRequest.checkRequest(request);


        // Check existence of proxy user
        ProxyUser proxyUserInDb = proxyUserRepository.findById(request.getProxyUserId()).orElse(null);
        if (proxyUserInDb == null) {
            throw new UnExpectedRequestException("ProxyUser id: " + request.getProxyUserId() + " {&DOES_NOT_EXIST}");
        }

        proxyUserRepository.delete(proxyUserInDb);
        LOGGER.info("Succeed to delete proxy user. proxy_user: {}", proxyUserInDb.getProxyUserName());

        return new GeneralResponse<>("200", "{&SUCCEED_TO_DELETE_PROXY_USER}", null);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> modifyProxyUser(ModifyProxyUserRequest request) throws UnExpectedRequestException {
        // Check Arguments
        ModifyProxyUserRequest.checkRequest(request);

        // Check existence of proxy user
        ProxyUser proxyUserInDb = proxyUserRepository.findById(request.getProxyUserId()).orElse(null);
        if (proxyUserInDb == null) {
            throw new UnExpectedRequestException("ProxyUser id: [" + request.getProxyUserId() + "] {&DOES_NOT_EXIST}");
        }
        String oldProxyUserName = proxyUserInDb.getProxyUserName();
        // Modify proxy user name
        proxyUserInDb.setProxyUserName(request.getProxyUserName());
        proxyUserRepository.save(proxyUserInDb);

        LOGGER.info("Succeed to modify proxy user. old proxy_user name: {}, new proxy_user name: {}", oldProxyUserName, proxyUserInDb.getProxyUserName());
        return new GeneralResponse<>("200", "{&SUCCEED_TO_MODIFY_PROXY_USER_NAME}", null);
    }

    @Override
    public GeneralResponse<?> getAllProxyUser(PageRequest request) throws UnExpectedRequestException {
        // Check Arguments
        PageRequest.checkRequest(request);

        // Query by page and size
        int page = request.getPage();
        int size = request.getSize();
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);
        List<ProxyUser> proxyUsers = proxyUserRepository.findAll(pageable).getContent();
        long total = proxyUserRepository.count();

        List<AddProxyUserResponse> proxyUserResponses = new ArrayList<>();
        for (ProxyUser proxyUser : proxyUsers) {
            AddProxyUserResponse tmp = new AddProxyUserResponse(proxyUser);
            proxyUserResponses.add(tmp);
        }
        GetAllResponse<AddProxyUserResponse> response = new GetAllResponse<>();
        response.setTotal(total);
        response.setData(proxyUserResponses);

        LOGGER.info("Succeed to find all proxyUsers, response: {}", response);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_FIND_ALL_PROXYUSERS}", response);
    }


}
