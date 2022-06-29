package com.webank.wedatasphere.qualitis.interceptor;

import com.webank.wedatasphere.dss.standard.app.sso.plugin.filter.HttpSessionUserInterceptor;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.service.LoginService;
import com.webank.wedatasphere.qualitis.service.UserService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.management.relation.RoleNotFoundException;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author allenzhou@webank.com
 * @date 2021/12/21 18:10
 */
@Component
public class QualitisHttpSessionUserInterceptor implements HttpSessionUserInterceptor {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserService userService;
    @Autowired
    private LoginService loginService;

    private static final Logger LOGGER = LoggerFactory.getLogger(QualitisHttpSessionUserInterceptor.class);

    @Override
    public void addUserToSession(String userName, HttpServletRequest request) {
        // 查询数据库，看用户是否存在
        User userInDb = userDao.findByUsername(userName);
        if (userInDb != null) {
            // 放入session
            LOGGER.info("User: {} succeed to login", userName);
            loginService.addToSession(userName, request);
        } else {
            // 自动创建用户
            LOGGER.warn("user: {}, do not exist, trying to create user", userName);
            try {
                userService.autoAddUser(userName);
                loginService.addToSession(userName, request);
            } catch (RoleNotFoundException e) {
                LOGGER.error("Failed to auto add user, cause by: Failed to get role [PROJECTOR]", e);
            }
        }
        HttpSession session = request.getSession();
        Cookie cookie = new Cookie("JSESSIONID", session.getId());
        cookie.setSecure(true);
        cookie.setPath("/");
        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request) {
            @Override
            public Cookie[] getCookies() {
                List<Cookie> cookieList = new ArrayList<>();
                cookieList.add(cookie);
                final Cookie[] cookies = (Cookie[]) cookieList.toArray();
                return cookies;
            }
        };

        request = (HttpServletRequest) requestWrapper.getRequest();
    }

    @Override
    public boolean isUserExistInSession(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession();
        Object permissionObj = session.getAttribute("permissions");
        Object user = session.getAttribute("user");
        if (null == permissionObj || null == user) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String getUser(HttpServletRequest httpServletRequest) {
        return HttpUtils.getUserName(httpServletRequest);
    }
}
