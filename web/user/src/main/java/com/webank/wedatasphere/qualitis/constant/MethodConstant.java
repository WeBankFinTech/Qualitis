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

package com.webank.wedatasphere.qualitis.constant;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * @author howeye
 */
public class MethodConstant {

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_PUT = "PUT";

    private MethodConstant() {
        // Default Constructor
    }

    private static final Set<String> METHOD_SET = ImmutableSet.of(METHOD_GET, METHOD_POST, METHOD_DELETE, METHOD_PUT);

    public static Set<String> getMethodSet() {
        return METHOD_SET;
    }

}
