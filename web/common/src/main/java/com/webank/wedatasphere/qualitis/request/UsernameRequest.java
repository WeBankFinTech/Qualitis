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

package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;

/**
 * @author howeye
 */
public class UsernameRequest {

    private String username;
    @JsonProperty("chinese_name")
    private String chineseName;

    public UsernameRequest() {
    }

    public UsernameRequest(String username, String chineseName) {
        this.username = username;
        this.chineseName = chineseName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public static void checkRequest(UsernameRequest request) throws UnExpectedRequestException {
        if (request.getUsername() == null || ("").equals(request.getUsername())) {
            throw new UnExpectedRequestException("Username {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }

        if (request.getChineseName() == null || ("").equals(request.getChineseName())) {
            throw new UnExpectedRequestException("Chinese_name {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }
}
