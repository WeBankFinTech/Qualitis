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

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author v_wblwyan
 * @date 2018-11-1
 */
public class HttpUtils {

  private HttpUtils() {
    // Default Constructor
  }

  private static Map<String, Object> getUser(HttpServletRequest request) {
    if (request == null || request.getSession() == null) {
      return null;
    }
    Object proxyUserObj = request.getSession().getAttribute("proxyUser");
    if (proxyUserObj != null) {
      return (Map<String, Object>) request.getSession().getAttribute("proxyUser");
    }
    return (Map<String, Object>) request.getSession().getAttribute("user");
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
}
