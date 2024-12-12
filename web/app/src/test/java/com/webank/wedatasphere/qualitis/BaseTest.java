package com.webank.wedatasphere.qualitis;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.mockito.Spy;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.mockito.Mockito.when;

/**
 * @author v_minminghe@webank.com
 * @date 2023-03-23 14:38
 * @description
 */
public class BaseTest {

    @MockBean
    @Spy
    private HttpSession session;
    @MockBean
    @Spy
    private HttpServletRequest httpServletRequest;

    protected String defaultLoginUserName = "allenzhou";
    protected Long defaultLoginUserId = 2L;

    protected void initLoginUser(String loginUserName, Long loginUserId) {
        if (StringUtils.isEmpty(loginUserName)) {
            loginUserName = defaultLoginUserName;
        }
        if (Objects.isNull(defaultLoginUserId)) {
            loginUserId = defaultLoginUserId;
        }
        when(httpServletRequest.getSession()).thenReturn(session);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", loginUserName);
        userMap.put("userId", loginUserId);
        when(session.getAttribute("user")).thenReturn(userMap);
    }

}
