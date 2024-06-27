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

package com.webank.wedatasphere.qualitis.filter;

import cn.webank.bdp.microfrontend.utils.FilterUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wedatasphere.qualitis.config.AuthFilterUrlConfig;
import com.webank.wedatasphere.qualitis.config.FrontEndConfig;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.Permission;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.LoginService;
import com.webank.wedatasphere.qualitis.service.UserService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.AntPathMatcher;

import javax.management.relation.RoleNotFoundException;
import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
public class Filter1AuthorizationFilter implements Filter {

    private static Logger LOGGER = LoggerFactory.getLogger(Filter1AuthorizationFilter.class);

    private static final String NOT_FILTER_METHOD = "OPTIONS";

    private static final String FACADIS_PROXY_USER_ENTRANCE = "PROXY_USER_ENTRANCE";

    private static final String FACADIS_PROXY_USER_LOGIN = "PROXY_USER_LOGIN";

    private static final String TRICKY_FE_NULL_STRING = "null";

    @Value("${facade.gov-core.ips}")
    private String facadeGovCoreIPs;

    @Value("${dss.origin-urls}")
    private String dssOriginUrls;

    @Autowired
    private FrontEndConfig frontEndConfig;

    @Autowired
    private AuthFilterUrlConfig authFilterUrlConfig;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    @Autowired
    private LoginService loginService;

    private List<String> permitUrlList = null;

    private List<String> uploadUrlList = null;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init(FilterConfig filterConfig) {
        permitUrlList = authFilterUrlConfig.getUnFilterUrls();
        uploadUrlList = authFilterUrlConfig.getUploadUrls();
        if (permitUrlList == null) {
            permitUrlList = new ArrayList<>();
        }
        if (uploadUrlList == null) {
            uploadUrlList = new ArrayList<>();
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        String requestURI = httpServletRequest.getRequestURI();
        // Pass if file upload url accepted
        AntPathMatcher matcher = new AntPathMatcher();
        for (String uploadUrl : uploadUrlList) {
            if (matcher.match(uploadUrl, requestURI)) {
                filterChain.doFilter(httpServletRequest, servletResponse);
                return;
            }
        }

        printReceiveLog(httpServletRequest);
        if (NOT_FILTER_METHOD.equals(httpServletRequest.getMethod())) {
            filterChain.doFilter(httpServletRequest, servletResponse);
            return;
        }

        String username = FilterUtil.shouldPass(httpServletRequest, dssOriginUrls, facadeGovCoreIPs) ? getFacadisProxyUser(httpServletRequest) : getLoginUser(httpServletRequest);
        if (isLoginURI(requestURI)) {
            LOGGER.info("Login username: {}", username);
            if (StringUtils.isBlank(username)) {
                throw new ServletException("Intrusion without authentication when LOGIN.");
            }
        } else if (isApiURI(requestURI)) {
            LOGGER.debug("API username: {}", username);
            if (StringUtils.isBlank(username)) {
                throw new ServletException("Intrusion without authentication when API.");
            }
        } else if (isLogoutURI(requestURI)) {
            LOGGER.info("Logout username: {}", username);
            if (StringUtils.isBlank(username)) {
                throw new ServletException("Intrusion without authentication when LOGOUT.");
            }

            // Clean session
            HttpSession httpSession = httpServletRequest.getSession(false);
            if (httpSession != null) {
                httpSession.removeAttribute("permissions");
                httpSession.removeAttribute("user");
                httpSession.removeAttribute("proxyUser");
                httpSession.invalidate();
                LOGGER.info("Session for {} has been invalidated.", username);
            }
            // Clean cookies
            Cookie[] cookies = httpServletRequest.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    cookie.setValue(StringUtils.EMPTY);
                    cookie.setMaxAge(0);
                    httpServletResponse.addCookie(cookie);
                }
            }
            filterChain.doFilter(httpServletRequest, servletResponse);
            return;
        } else {
            throw new ServletException("Unrecognized url: " + requestURI);
        }

        if (!permitUrlList.contains(requestURI)) {
            if (HttpUtils.getUser(httpServletRequest) == null || StringUtils.isBlank(HttpUtils.getUserName(httpServletRequest))
                    || !StringUtils.equals(username, HttpUtils.getUserName(httpServletRequest)) || HttpUtils.getPermissions(httpServletRequest) == null) {
                if (StringUtils.isBlank(HttpUtils.getUserName(httpServletRequest))) {
                    LOGGER.warn("No available session at present or EMPTY user name stored, refresh session!~");
                } else if (StringUtils.equals(TRICKY_FE_NULL_STRING, HttpUtils.getUserName(httpServletRequest))) {
                    LOGGER.warn("Tricky [null] user name stored in session, refresh session!~");
                } else if (StringUtils.equals(TRICKY_FE_NULL_STRING, username)) {
                    LOGGER.warn("Tricky [null] user name from FE, skip it!~");
                    return;
                }

                // 查询数据库，看用户是否存在
                LOGGER.info("Achieving stored user data in DB with name: {}", username);
                User userInDb = userDao.findByUsername(username);
                if (userInDb != null) {
                    // 放入session
                    loginService.addToSession(username, httpServletRequest);
                } else {
                    // 自动创建用户
                    LOGGER.warn("user: {}, do not exist, trying to create user", username);
                    try {
                        userService.autoAddUser(username);
                        loginService.addToSession(username, httpServletRequest);
                    } catch (RoleNotFoundException e) {
                        LOGGER.error("Failed to auto add user, cause by: Failed to get role [PROJECTOR]", e);
                        return;
                    }
                }
            }

            // Check login users' permissions
            Object permissionObj = HttpUtils.getPermissions(httpServletRequest);
            List<Permission> permissions = (List<Permission>) permissionObj;
            String method = httpServletRequest.getMethod();
            if (!checkPermission(requestURI, method, permissions)) {
                writeForbidden("no permissions", servletResponse);
                LOGGER.warn("User: {} failed to access url: {}, caused by: No permissions", HttpUtils.getUser(httpServletRequest), httpServletRequest.getRequestURI());
                return;
            }
            LOGGER.info("User: {} succeed to access url: {}", HttpUtils.getUser(httpServletRequest), httpServletRequest.getRequestURI());
        } else {
            LOGGER.info("Permitted url: {}", httpServletRequest.getRequestURI());
        }
        filterChain.doFilter(httpServletRequest, servletResponse);
    }

    private void printReceiveLog(HttpServletRequest request) {
        String requestUrl = request.getRequestURI();
        LOGGER.info("Receive request:[{}], method:[{}]", requestUrl, request.getMethod());
    }

    private void writeForbidden(String message, ServletResponse response) throws IOException {
        ((HttpServletResponse) response).setStatus(Response.Status.FORBIDDEN.getStatusCode());
        ServletOutputStream out = response.getOutputStream();
        GeneralResponse generalResponse = new GeneralResponse<>("403", message, null);
        out.write(objectMapper.writeValueAsBytes(generalResponse));
        out.flush();
    }


    /**
     * Return true if pass permissions, otherwise return false
     *
     * @param url
     * @param method
     * @param permissions
     * @return
     */
    private boolean checkPermission(String url, String method, List<Permission> permissions) {
        AntPathMatcher matcher = new AntPathMatcher();
        List<Permission> left = permissions.stream().filter(
                (Permission p) -> matcher.match(p.getUrl(), url) && method.equals(p.getMethod())
        ).collect(Collectors.toList());
        return !left.isEmpty();
    }

    private String getFacadisProxyUser(HttpServletRequest httpServletRequest) {
        // Generally requests come from Facadis
        String username = StringUtils.EMPTY;
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (StringUtils.equals(FACADIS_PROXY_USER_ENTRANCE, cookie.getName()) || StringUtils.equals(FACADIS_PROXY_USER_LOGIN, cookie.getName())) {
                    username = cookie.getValue();
                    break;
                }
            }
        }
        // Fxxking shit request come from damn DSS AppConn
        if (StringUtils.isBlank(username)) {
            LOGGER.warn("Maybe shit request from DSS AppConn, try hard to achieving user from session.");
            username = HttpUtils.getUserName(httpServletRequest);
        }
        return username;
    }

    private String getLoginUser(HttpServletRequest httpServletRequest) {
        String username = httpServletRequest.getRemoteUser();
        if (StringUtils.isBlank(username)) {
            username = HttpUtils.getUserName(httpServletRequest);
            if (StringUtils.isBlank(username)) {
                Cookie[] cookies = httpServletRequest.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if (StringUtils.equals(FACADIS_PROXY_USER_ENTRANCE, cookie.getName()) || StringUtils.equals(FACADIS_PROXY_USER_LOGIN, cookie.getName())) {
                            username = cookie.getValue();
                            break;
                        }
                    }
                }
            }
        }
        return username;
    }

    private boolean isLoginURI(String requestURI) {
        return StringUtils.contains(requestURI, "/auth/common/projector/role");
    }

    private boolean isApiURI(String requestURI) {
        return StringUtils.contains(requestURI, "/api/v1");
    }

    private boolean isLogoutURI(String requestURI) {
        return StringUtils.contains(requestURI, "/auth/common/logout");
    }

    @Override
    public void destroy() {
        // destroy operation
    }

}
