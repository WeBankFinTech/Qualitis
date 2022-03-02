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

import com.webank.wedatasphere.qualitis.config.JerseyConfig;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.service.LoginService;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author howeye
 */
public class UnFilterUrlFilter implements Filter {

    private static final String LOGOUT_URL = JerseyConfig.APPLICATION_PATH + "/api/v1/logout";
    private static final String LOGIN_RANDOM = "loginRandom";

    @Autowired
    private LoginService loginService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String requestUrl = ((HttpServletRequest) request).getRequestURI();
        if (requestUrl.equals(LOGOUT_URL)) {
            HttpSession session = ((HttpServletRequest) request).getSession();
            Integer loginRandom = (Integer) session.getAttribute(LOGIN_RANDOM);
            String queryParam = ((HttpServletRequest) request).getQueryString();
            if (loginRandom != null && queryParam != null && queryParam.equals(LOGIN_RANDOM + SpecCharEnum.EQUAL.getValue() + loginRandom.intValue())) {
                loginService.logout(httpServletRequest, httpServletResponse);
            } else {
                return;
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
