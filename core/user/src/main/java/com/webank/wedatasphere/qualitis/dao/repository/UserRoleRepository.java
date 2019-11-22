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

import com.webank.wedatasphere.qualitis.entity.UserRole;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author howeye
 */
public interface UserRoleRepository extends JpaRepository<UserRole, String> {

    /**
     * Find user role by user and role
     * @param user
     * @param role
     * @return
     */
    UserRole findByUserAndRole(User user, Role role);

    /**
     * Find user role by user
     * @param user
     * @return
     */
    List<UserRole> findByUser(User user);

    /**
     * Find user role by role
     * @param role
     * @return
     */
    List<UserRole> findByRole(Role role);

}
