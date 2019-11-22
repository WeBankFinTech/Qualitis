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

import com.webank.wedatasphere.qualitis.entity.Permission;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserSpecPermission;
import com.webank.wedatasphere.qualitis.entity.Permission;

import java.util.List;

/**
 * @author howeye
 */
public interface UserSpecPermissionDao {

    /**
     * Find user permission by user and permission
     * @param user
     * @param permission
     * @return
     */
    UserSpecPermission findByUserAndPermission(User user, Permission permission);

    /**
     * Save user permission
     * @param userSpecPermission
     * @return
     */
    UserSpecPermission saveUserSpecPermission(UserSpecPermission userSpecPermission);

    /**
     * Find user permission by id
     * @param uuid
     * @return
     */
    UserSpecPermission findByUuid(String uuid);

    /**
     * Delete user permission
     * @param userSpecPermission
     */
    void deleteUserSpecPermission(UserSpecPermission userSpecPermission);

    /**
     * Paging get all user permission
     * @param page
     * @param size
     * @return
     */
    List<UserSpecPermission> findAllUserSpecPermission(int page, int size);

    /**
     * Find user permission by user
     * @param user
     * @return
     */
    List<UserSpecPermission> findByUser(User user);

    /**
     * Find user permission by permission
     * @param permission
     * @return
     */
    List<UserSpecPermission> findByPermission(Permission permission);

    /**
     * Count all user permission
     * @return
     */
    long countAll();

}
