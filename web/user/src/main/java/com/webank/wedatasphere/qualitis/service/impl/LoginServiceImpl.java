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
import com.webank.wedatasphere.qualitis.config.FrontEndConfig;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.Permission;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.LoginFailedException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.LocalLoginRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.LoginService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author howeye
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private FrontEndConfig frontEndConfig;

    public LoginServiceImpl(@Context HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Context
    private HttpServletRequest httpRequest;

    private static final SecureRandom secureRandom = new SecureRandom();

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginServiceImpl.class);

    private static final SimpleDateFormat SDF =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public GeneralResponse<?> localLogin(LocalLoginRequest request) throws LoginFailedException, UnExpectedRequestException {
        // Check Arguments
        checkRequest(request);

        String username = request.getUsername();
        String password = request.getPassword();
        long currentLoginTime = System.currentTimeMillis();

        User userInDb = userDao.findByUsername(username);
        if (userInDb == null) {
            throw new LoginFailedException("{&USER_NOT_EXIST}");
        }
        if (userInDb.getLockTime() != null && (currentLoginTime - userInDb.getLockTime()) / (1000 * 60) < 10) {
            String lockTime = SDF.format(new Date(userInDb.getLockTime()));
            LOGGER.info("Login locked. user: {}, lock time: {}", username, lockTime);
            throw new LoginFailedException("{&LOGIN_LOCKED}" + lockTime);
        }

        if (localLogin(userInDb, password)) {
            addToSession(username, httpRequest);
            clearErrorLoginRecord(userInDb);
            userDao.saveUser(userInDb);
            LOGGER.info("Succeed to login. user: {}, current_user: {}", username, username);
            return new GeneralResponse<>("200", "{&LOGIN_SUCCESS}", null);
        } else {
            // Login failed in first time.
            if (userInDb.getLoginErrorTime() == null || userInDb.getLoginErrorCount() == null) {
                userInDb.setLoginErrorTime(currentLoginTime);
                userInDb.setLoginErrorCount(1);
            } else {
                // Check error count in 5 minutes decide to lock
                boolean consecutiveError = (currentLoginTime - userInDb.getLoginErrorTime()) / (1000 * 60) < 5;
                if (consecutiveError) {
                    userInDb.setLoginErrorCount(userInDb.getLoginErrorCount() + 1);
                    if (userInDb.getLoginErrorCount() >= 5) {
                        userInDb.setLockTime(currentLoginTime);
                    }
                } else {
                    userInDb.setLoginErrorTime(currentLoginTime);
                    userInDb.setLoginErrorCount(1);
                }
            }
            userDao.saveUser(userInDb);
            throw new LoginFailedException("{&LOGIN_FAILED}" + (5 - userInDb.getLoginErrorCount()));
        }
    }

    private void clearErrorLoginRecord(User userInDb) {
        userInDb.setLoginErrorCount(0);
    }

    @Override
    public GeneralResponse<?> logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        String username = HttpUtils.getUserName(httpServletRequest);
        HttpSession session = httpServletRequest.getSession();
        session.removeAttribute("permissions");
        session.removeAttribute("user");
        session.removeAttribute("proxyUser");

        Cookie[] cookies = httpServletRequest.getCookies();
        // Clear cookies.
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                if ("JSESSIONID".equals(cookieName)) {
                    cookieName = cookieName.replace("\r", "").replace("\n", "");
                    Cookie newCookie = new Cookie(cookieName, null);
                    newCookie.setSecure(true);
                    newCookie.setMaxAge(0);
                    newCookie.setPath("/");
                    httpServletResponse.addCookie(newCookie);
                }
            }
        }
        LOGGER.info("Succeed to logout, user: {}, current_user: {}", username, username);
        String logoutUrl = frontEndConfig.getDomainName() + "/#/home";
        httpServletResponse.sendRedirect(logoutUrl);
        return new GeneralResponse<>("200", "{&LOGOUT_SUCCESSFULLY}", null);
    }

    @Override
    public void addToSession(String username, HttpServletRequest httpServletRequest) {
        // Find user by username
        User userInDb = userDao.findByUsername(username);
        addUserToSession(userInDb, httpServletRequest);
        addPermissionsToSession(userInDb, httpServletRequest);
        addRandomToSession(httpServletRequest);
    }

    private void addUserToSession(User userInDb, HttpServletRequest httpServletRequest) {
        // Put user information into session
        HttpSession session = httpServletRequest.getSession();
        Map<String, Object> userMap = ImmutableMap.of("userId", userInDb.getId(), "username", userInDb.getUserName());
        session.setAttribute("user", userMap);
    }

    private void addPermissionsToSession(User userInDb, HttpServletRequest httpServletRequest) {
        List<Permission> userAllPermission = new ArrayList<>();
        // Add roles's permissions of user
        for (Role role : userInDb.getRoles()) {
            for (Permission permission : role.getPermissions()) {
                userAllPermission.add(permission);
            }
        }
        // Add permissions of user
        for (Permission permission : userInDb.getSpecPermissions()) {
            userAllPermission.add(permission);
        }

        // Put permissions into session
        HttpSession session = httpServletRequest.getSession();
        session.setAttribute("permissions", userAllPermission);
    }

    private void addRandomToSession(HttpServletRequest httpServletRequest) {
        // Put permissions into session
        HttpSession session = httpServletRequest.getSession();
        session.setAttribute("loginRandom", secureRandom.nextInt(10000));
    }

    private boolean localLogin(User userInDb, String password) {
        return password.equals(userInDb.getPassword());
    }

    private void checkRequest(LocalLoginRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        checkString(request.getPassword(), "password");
        checkString(request.getUsername(), "username");
    }

    private void checkString(String checkField, String fieldName) throws UnExpectedRequestException {
        if (StringUtils.isBlank(checkField)) {
            throw new UnExpectedRequestException(fieldName + " {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }
}
