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
import com.webank.wedatasphere.qualitis.client.config.DataMapConfig;
import com.webank.wedatasphere.qualitis.config.FrontEndConfig;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.dao.RoleDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.entity.Permission;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.LoginFailedException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.LocalLoginRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.LoginService;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.jasig.cas.client.util.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private RoleDao roleDao;

    @Value("${dss.origin-urls}")
    private String dssOriginUrls;

    @Autowired
    private DataMapConfig dataMapConfig;

    @Autowired
    private FrontEndConfig frontEndConfig;

    @Context
    private HttpServletRequest httpRequest;
    @Context
    private HttpServletResponse httpResponse;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginServiceImpl.class);

    public static final FastDateFormat PRINT_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private static final String ENV_FLAG = "envFlag";
    private static final String ENV_FLAG_VALUE = "prod";
    private static final String DM_DGSA_LOGIN_STATUS = "DM_DGSA_LOGIN_STATUS";
    private static final String LOGIN_USER_ID = "LOGIN_USER_ID";
    private static final String TGC_SF = "tgc_sf";
    private static final Integer TWO_HUNDRED = 200;
    private static final String USER_ID = "userId";

    public LoginServiceImpl(@Context HttpServletRequest httpRequest, @Context HttpServletResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
    }

    @Override
    public GeneralResponse localLogin(LocalLoginRequest request) throws LoginFailedException, UnExpectedRequestException, RoleNotFoundException {
        // Check Arguments
        checkRequest(request);

        String username = request.getUsername();
        String password = request.getPassword();
        if (localLogin(username, password)) {
            addToSession(username, httpRequest);
            LOGGER.info("Succeed to login. user: {}, current_user: {}", username, username);
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&LOGIN_SUCCESS}", null);
        }

        throw new LoginFailedException("{&LOGIN_FAILED}");
    }

    @Override
    public GeneralResponse logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        String logoutUrl = ResourceLoader.getInstance().getPropFromFS("sso.client.properties").getProperty("sso.client.logoutUrl");
        Map<String, Object> data = new HashMap<>();
        data.put("retCode", 3001);
        data.put("redirectUrl", logoutUrl);
        return new GeneralResponse<>(ResponseStatusConstants.OK, "success", data);
    }

    @Override
    public void addToSession(String username, HttpServletRequest httpServletRequest) {
        // Find user by username
        User userInDb = userDao.findByUsername(username);
        addUserToSession(userInDb, httpServletRequest);
    }

    @Override
    public void addDssUrlToSession(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        session.setAttribute("entrance_origin", dssOriginUrls);
    }

    private void addUserToSession(User userInDb, HttpServletRequest httpServletRequest) {
        // Put user information into session
        HttpSession session = httpServletRequest.getSession();
        Map<String, Object> userMap = ImmutableMap.of("userId", userInDb.getId(), "username", userInDb.getUsername());
        session.setAttribute("user", userMap);

        List<Permission> userAllPermission = new ArrayList<>();
        // Add roles's permissions of user
        for (Role role : userInDb.getRoles()) {
            for (Permission permission : role.getPermissions()) {
                if (! userAllPermission.contains(permission)) {
                    userAllPermission.add(permission);
                }
            }
        }
        // Add permissions of user
        for (Permission permission : userInDb.getSpecPermissions()) {
            if (! userAllPermission.contains(permission)) {
                userAllPermission.add(permission);
            }
        }

        // Put permissions into session
        session.setAttribute("permissions", userAllPermission.stream().distinct().collect(Collectors.toList()));
    }

    private boolean localLogin(String username, String password) {
        User userInDb = userDao.findByUsername(username);
        if (userInDb == null) {
            return false;
        }
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
