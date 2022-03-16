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

import com.google.common.collect.ImmutableMap;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.TransferUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import java.util.Map;

/**
 * @author howeye
 */
@Service
public class TransferUserServiceImpl implements TransferUserService {

    @Autowired
    private UserDao userDao;

    private HttpServletRequest httpServletRequest;

    public TransferUserServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public GeneralResponse<?> transferUser(String user) throws UnExpectedRequestException {
        User userInDb = userDao.findByUsername(user);

        if (userInDb == null) {
            throw new UnExpectedRequestException("ProxyUser: " + user + " {&DOES_NOT_EXIST}");
        }

        Map<String, Object> userMap = ImmutableMap.of("userId", userInDb.getId(), "username", userInDb.getUserName());
        HttpSession httpSession = httpServletRequest.getSession();
        httpSession.setAttribute("proxyUser", userMap);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_TRANSFER_TO_PROXYUSER}", null);
    }

    @Override
    public GeneralResponse<?> exitUser() {
        HttpSession httpSession = httpServletRequest.getSession();
        httpSession.setAttribute("proxyUser", null);
        return new GeneralResponse<>("200", "{&SUCCEED_TO_EXIT_USER}", null);
    }
}
