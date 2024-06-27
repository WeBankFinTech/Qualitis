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

package com.webank.wedatasphere.qualitis.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author v_wblwyan
 * @date 2018-11-1
 */
public class HttpUtils {

    private static Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

    private HttpUtils() {
        // Default Constructor
    }

    public static Map<String, Object> getUser(HttpServletRequest request) {
        if (request == null) {
            LOGGER.warn("No request provided!~");
            return null;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            LOGGER.warn("No current session found!~");
            return null;
        }

        Object proxyUserObj = session.getAttribute("proxyUser");
        if (proxyUserObj != null) {
            return (Map<String, Object>) session.getAttribute("proxyUser");
        }
        return (Map<String, Object>) session.getAttribute("user");
    }

    public static Object getPermissions(HttpServletRequest request) {
        if (request == null) {
            LOGGER.warn("No request provided!~");
            return null;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            LOGGER.warn("No current session found!~");
            return null;
        }

        return session.getAttribute("permissions");
    }

    public static Long getUserId(HttpServletRequest request) {
        Map<String, Object> user = getUser(request);
        if (user == null) {
            return null;
        }
        return (Long) user.get("userId");
    }

    public static String getUserName(HttpServletRequest request) {
        Map<String, Object> user = getUser(request);
        if (user == null) {
            return null;
        }
        return (String) user.get("username");
    }

    /**
     * 把字符串IP转换成Integer
     *
     * @param ipStr 字符串IP
     * @return IP对应的int值
     */
    public static Integer ip2Integer(String ipStr) {
        String[] ip = ipStr.split("\\.");
        return Integer.valueOf(ip[0]) * 256 * 256 * 256 + Integer.valueOf(ip[1]) * 256 * 256 + Integer.valueOf(ip[2]) * 256 + Integer.valueOf(ip[3]);
    }
}
