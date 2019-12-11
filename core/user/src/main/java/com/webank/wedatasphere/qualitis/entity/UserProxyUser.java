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
import java.util.Objects;

/**
 * @author howeye
 */
@Entity
@Table(name = "qualitis_auth_user_proxy_user")
public class UserProxyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private ProxyUser proxyUser;

    public UserProxyUser() {
        // Default Constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ProxyUser getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(ProxyUser proxyUser) {
        this.proxyUser = proxyUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        UserProxyUser userProxyUser = (UserProxyUser) o;
        return Objects.equals(id, userProxyUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
