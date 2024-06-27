package com.webank.wedatasphere.qualitis.filter;

import com.webank.wedatasphere.qualitis.service.LoginService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author v_minminghe@webank.com
 * @date 2023-04-19 9:41
 * @description
 */
public class LocalAuthorizationFilter implements Filter {

    @Autowired
    private LoginService loginService;

    @Value("${local.username: allenzhou}")
    private String testUsername;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String username = HttpUtils.getUserName(httpServletRequest);
        if (StringUtils.isBlank(username)) {
            loginService.addToSession(testUsername, httpServletRequest);
        }
        chain.doFilter(request, response);
    }
}
