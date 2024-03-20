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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.webank.wedatasphere.qualitis.client.config.DataMapConfig;
import com.webank.wedatasphere.qualitis.config.FrontEndConfig;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.RoleDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.entity.Permission;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.LoginFailedException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.model.DmTicketValue;
import com.webank.wedatasphere.qualitis.request.LocalLoginRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.LoginService;
import com.webank.wedatasphere.qualitis.util.CookieTokenManager;
import com.webank.wedatasphere.qualitis.util.HttpDmUtils;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    @Autowired
    private DataMapConfig dataMapConfig;

    @Autowired
    private FrontEndConfig frontEndConfig;

    @Context
    private HttpServletRequest httpRequest;
    @Context
    private HttpServletResponse httpResponse;

    private static final SecureRandom secureRandom = new SecureRandom();

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginServiceImpl.class);

    public static final FastDateFormat PRINT_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    private static final String URL_REGEX = "^(https?|ftp|file)://.*$";

    private static final String ENV_FLAG = "envFlag";
    private static final String ENV_FLAG_VALUE = "prod";
    private static final String DM_DGSA_LOGIN_STATUS = "DM_DGSA_LOGIN_STATUS";
    private static final String LOGIN_USER_ID = "LOGIN_USER_ID";
    private static final String TGC_SF = "tgc_sf";
    private static final Integer TWO_HUNDRED  = 200;

    public LoginServiceImpl(@Context HttpServletRequest httpRequest, @Context HttpServletResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
    }

    @Override
    public GeneralResponse<?> localLogin(LocalLoginRequest request) throws LoginFailedException, UnExpectedRequestException, RoleNotFoundException {
        // Check Arguments
        checkRequest(request);

        String username = request.getUsername();
        if (localLogin(username, request.getPassword())) {
            addToSession(username, httpRequest);
            LOGGER.info("Succeed to login. user: {}, current_user: {}", username, username);
            return new GeneralResponse<>("200", "{&LOGIN_SUCCESS}", null);
        }

        throw new LoginFailedException("{&LOGIN_FAILED}");
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
        for (Cookie cookie : cookies) {
            String cookieName = cookie.getName ();
            if ("JSESSIONID".equals(cookieName)) {
                cookieName = cookieName.replace("\r", "").replace("\n", "");
                Cookie newCookie = new Cookie (cookieName, null);
                newCookie.setSecure(true);
                newCookie.setMaxAge(0);
                newCookie.setPath("/");
                httpServletResponse.addCookie(newCookie);
            }
        }

        LOGGER.info("Succeed to logout, user: {}, current_user: {}", username, username);
        String logoutUrl = frontEndConfig.getDomainName().replace("{IP}", QualitisConstants.getPublicIp()) + "/#/home";
        boolean valid = isValid(logoutUrl);
        if (!valid) {
            LOGGER.error("Verify if the url is legal", logoutUrl);
        }
        httpServletResponse.sendRedirect(logoutUrl);
        return new GeneralResponse<>("200", "{&LOGOUT_SUCCESSFULLY}", null);
    }

    public static boolean isValid(String url) throws UnsupportedEncodingException {
        String encodeUrl = URLEncoder.encode(url, "UTF-8");
        Pattern pattern = Pattern.compile(URL_REGEX);
        Matcher matcher = pattern.matcher(encodeUrl);
        return matcher.matches();
    }

    @Override
    public void addToSession(String username, HttpServletRequest httpServletRequest) {
        // Find user by username
        User userInDb = userDao.findByUsername(username);
        addUserToSession(userInDb, httpServletRequest);
        addPermissionsToSession(userInDb, httpServletRequest);
        addRandomToSession(httpServletRequest);
    }

    @Override
    public void addDmsUserCheckCookie(String username, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            List<String> cookieNames = Arrays.asList(httpServletRequest.getCookies()).stream().map(cookie -> cookie.getName()).collect(Collectors.toList());
            if (! cookieNames.contains(ENV_FLAG)) {
                Cookie cookie = new Cookie(ENV_FLAG, URLEncoder.encode(ENV_FLAG_VALUE, "UTF-8"));
                cookie.setPath("/");
                httpServletResponse.addCookie(cookie);
            }
            if (! cookieNames.contains(DM_DGSA_LOGIN_STATUS)) {
                Cookie cookie = new Cookie(DM_DGSA_LOGIN_STATUS, URLEncoder.encode("1", "UTF-8"));
                cookie.setPath("/");
                httpServletResponse.addCookie(cookie);
            }
            if (! cookieNames.contains(LOGIN_USER_ID) || cookieNames.contains(TGC_SF) ) {
                CookieTokenManager cookieTokenManager = new CookieTokenManager("/", TGC_SF, dataMapConfig.getRandomHashSalt());
                DmTicketValue fullNameTicket = cookieTokenManager.buildDmTicketValue(username, "");
                long expires = System.currentTimeMillis() + CookieTokenManager.SESSION_MAX_IDLE_MILLIS;
                String userIdSignature = cookieTokenManager.calcCookieValue(fullNameTicket, expires);
                String fetchUserId = fetchUserId(username, userIdSignature);
                if (null == fetchUserId) {
                    return;
                }
                Cookie cookie = new Cookie(LOGIN_USER_ID, URLEncoder.encode(fetchUserId, "UTF-8"));
                cookie.setPath("/");
                httpServletResponse.addCookie(cookie);
                Cookie cookieTgc = new Cookie(TGC_SF, URLEncoder.encode(userIdSignature, "UTF-8"));
                cookieTgc.setPath("/");
                httpServletResponse.addCookie(cookieTgc);
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("URL encoder error", e);
        }
    }

    private String fetchUserId(String username, String signature) {
        String url = dataMapConfig.getAddress() + dataMapConfig.getUserId() + username;
        LOGGER.info("Fetching userId from DMS, url: {}", url);
        String result = null;
        try {
            result = HttpDmUtils.httpFetch(HttpMethod.GET, url, null, ENV_FLAG_VALUE, signature, null);
        } catch (Exception e) {
            LOGGER.error("Failed to fetch userId from DMS", e);
        }
        JsonObject resultJson = processInfoResult4Datamap(result);
        if (resultJson == null || StringUtils.isEmpty(resultJson.get("userId").getAsString())) {
            LOGGER.warn("There is no userId in DMS for username: {}", username);
            return null;
        }
        return resultJson.get("userId").getAsString();
    }

    protected JsonObject processInfoResult4Datamap(String result) {
        JsonObject resultJson;
        try {
            resultJson = str2JsonObject(result);
        } catch (Exception e) {
            LOGGER.error("Fail to processInfoResult4Datamap", e.getMessage(), e);
            return null;
        }
        if (null != resultJson) {
            int retCode = resultJson.get("code").getAsInt();
            if (retCode == TWO_HUNDRED) {
                return getJsonObject(resultJson, "data");
            }
            LOGGER.error("Fail to processInfoResult4Datamap, retCode: {}, msg: {}", retCode, resultJson.get("msg").getAsString());
            return null;
        }
        return null;
    }

    public JsonObject getJsonObject(JsonObject jsono, String key){
        JsonObject resultJson = null;
        try {
            if (null != jsono && jsono.keySet().contains(key)){
                if (jsono.get(key).isJsonObject()){
                    resultJson = jsono.get(key).getAsJsonObject();
                }else if (jsono.get(key).isJsonPrimitive()){
                    String resultStr = jsono.get(key).getAsString();
                    resultJson = str2JsonObject(resultStr);
                }
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            resultJson = getJsonObjectWithUn(jsono, key);
        }
        return resultJson;
    }

    public JsonObject getJsonObjectWithUn(JsonObject jsono, String key){
        JsonObject resultJson = null;
        try {
            if (null != jsono && jsono.keySet().contains(key)){
                if (jsono.get(key).isJsonObject()){
                    resultJson = jsono.get(key).getAsJsonObject();
                }else if (jsono.get(key).isJsonPrimitive()){
                    String resultStr = jsono.get(key).getAsString();
                    String resultStr2 = StringEscapeUtils.unescapeJavaScript(resultStr);
                    resultJson = str2JsonObject(resultStr2);
                }
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
        }
        return resultJson;
    }

    public JsonObject str2JsonObject(String str){
        JsonObject jsonObject = null;
        try {
            if (StringUtils.isNotEmpty(str)){
                JsonParser parser = new JsonParser();
                jsonObject = parser.parse(str).getAsJsonObject();
            }

        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
        }
        return jsonObject;
    }

    private void addUserToSession(User userInDb, HttpServletRequest httpServletRequest) {
        // Put user information into session
        HttpSession session = httpServletRequest.getSession();
        Map<String, Object> userMap = ImmutableMap.of("userId", userInDb.getId(), "username", userInDb.getUsername());
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
