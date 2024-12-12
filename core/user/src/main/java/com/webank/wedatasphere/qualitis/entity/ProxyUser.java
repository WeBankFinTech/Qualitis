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

package com.webank.wedatasphere.qualitis.entity;

import javax.persistence.*;
import java.util.List;

/**
 * @author howeye
 */
@Entity
@Table(name = "qualitis_auth_proxy_user")
public class ProxyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "proxy_user_name", length = 20)
    private String proxyUserName;

    @OneToMany(mappedBy = "proxyUser", cascade = CascadeType.REMOVE)
    private List<UserProxyUser> userProxyUsers;

    public ProxyUser() {
    }

    public ProxyUser(String proxyUserName) {
        this.proxyUserName = proxyUserName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProxyUserName() {
        return proxyUserName;
    }

    public void setProxyUserName(String proxyUserName) {
        this.proxyUserName = proxyUserName;
    }

    public List<UserProxyUser> getUserProxyUsers() {
        return userProxyUsers;
    }

    public void setUserProxyUsers(List<UserProxyUser> userProxyUsers) {
        this.userProxyUsers = userProxyUsers;
    }
}
