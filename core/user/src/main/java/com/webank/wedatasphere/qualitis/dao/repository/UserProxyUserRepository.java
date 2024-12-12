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

package com.webank.wedatasphere.qualitis.dao.repository;

import com.webank.wedatasphere.qualitis.entity.UserProxyUser;
import com.webank.wedatasphere.qualitis.entity.ProxyUser;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.ProxyUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author howeye
 */
public interface UserProxyUserRepository extends JpaRepository<UserProxyUser, Long> {

    /**
     * Find user proxy user by user and proxy user
     * @param user
     * @param proxyUser
     * @return
     */
    UserProxyUser findByUserAndProxyUser(User user, ProxyUser proxyUser);

    /**
     * Find user proxy user by proxy user
     * @param proxyUser
     * @param pageable
     * @return
     */
    List<UserProxyUser> findByProxyUser(ProxyUser proxyUser, Pageable pageable);

    /**
     * Count by proxy user
     * @param proxyUser
     * @return
     */
    long countByProxyUser(ProxyUser proxyUser);

}
