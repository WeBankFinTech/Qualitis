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

package com.webank.wedatasphere.qualitis.dao;

import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserRole;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author howeye
 */
public interface UserRoleDao {

    /**
     * Find user role by user and role
     * @param user
     * @param role
     * @return
     */
    UserRole findByUserAndRole(User user, Role role);

    /**
     * Save user role
     * @param userRole
     * @return
     */
    UserRole saveUserRole(UserRole userRole);

    /**
     * Find user role by id
     * @param uuid
     * @return
     */
    UserRole findByUuid(String uuid);

    /**
     * Delete user role
     * @param userRole
     */
    void deleteUserRole(UserRole userRole);

    /**
     * Delete user role by user
     * @param user
     */
    void deleteByUser(User user);

    /**
     * Paging find all user role
     * @param userName
     * @param page
     * @param size
     * @return
     */
    List<UserRole> findAllUserRole(String userName,int page, int size);

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

    /**
     * Find user role by role List
     * @param role
     * @return
     */
    List<UserRole> findByRoleList(List<Role> role);

    /**
     * Count by user role
     * @param userName
     * @return
     */
    long countAllUserRole(String userName);

    /**
     * count Postition Role
     *
     * @param id
     * @param roleType
     * @return
     */
    long countPositionRole(Long id,Integer roleType);

    /**
     * find By user Id
     *
     * @param user
     * @return
     */
    List<UserRole> findByUserId(User user);
}
