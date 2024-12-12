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

package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * @author howeye
 */
public class UserAndRoleResponse {
    @JsonProperty("login_random")
    private Integer loginRandom;
    private List<String> roles;
    private String username;

    public UserAndRoleResponse() {
        // Default Constructor
    }

    public Integer getLoginRandom() {
        return loginRandom;
    }

    public void setLoginRandom(Integer loginRandom) {
        this.loginRandom = loginRandom;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
