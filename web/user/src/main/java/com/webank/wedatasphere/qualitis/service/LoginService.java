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

import com.webank.wedatasphere.qualitis.request.LocalLoginRequest;
import com.webank.wedatasphere.qualitis.exception.LoginFailedException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.LocalLoginRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author howeye
 */
public interface LoginService {

    /**
     * Login according to username and password
     * @param request
     * @return
     * @throws LoginFailedException
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> localLogin(LocalLoginRequest request) throws LoginFailedException, UnExpectedRequestException;

    /**
     * Logout
     * @param request
     * @param httpServletResponse
     * @return
     * @throws IOException
     */
    GeneralResponse<?> logout(HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException;

    /**
     * Add user and permission information into session
     * @param username
     * @param httpServletRequest
     */
    void addToSession(String username, HttpServletRequest httpServletRequest);

}
