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

package com.webank.wedatasphere.qualitis.request.permission;

import com.webank.wedatasphere.qualitis.constant.MethodConstant;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import org.apache.commons.lang.StringUtils;

/**
 * @author howeye
 */
public class AddPermissionRequest {

    private String url;
    private String method;

    public AddPermissionRequest() {
        // Default Constructor
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public static void checkRequest(AddPermissionRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        checkString(request.getMethod(), "method");
        checkString(request.getUrl(), "url");

        checkMethodContains(request.getMethod());
    }

    private static void checkMethodContains(String method) throws UnExpectedRequestException {
        if (!MethodConstant.getMethodSet().contains(method)) {
            throw new UnExpectedRequestException("method: [" + method + "] {&NOT_SUPPORT}");
        }
    }

    private static void checkString(String str, String strName) throws UnExpectedRequestException {
        if (StringUtils.isBlank(str)) {
            throw new UnExpectedRequestException(strName + " {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }

    @Override
    public String toString() {
        return "AddPermissionRequest{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                '}';
    }
}
