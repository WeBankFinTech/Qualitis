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

import com.webank.wedatasphere.qualitis.entity.Permission;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserSpecPermission;
import com.webank.wedatasphere.qualitis.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author howeye
 */
public interface UserSpecPermissionRepository extends JpaRepository<UserSpecPermission, String> {

    /**
     * Find user permission by user and permission
     * @param user
     * @param permission
     * @return
     */
    UserSpecPermission findByUserAndPermission(User user, Permission permission);

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

}
