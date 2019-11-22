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
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.RolePermission;
import com.webank.wedatasphere.qualitis.entity.Permission;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.RolePermission;

import java.util.List;

/**
 * @author howeye
 */
public interface RolePermissionDao {

    /**
     * Find role permission by role and permission
     * @param role
     * @param permission
     * @return
     */
    RolePermission findByRoleAndPermission(Role role, Permission permission);

    /**
     * Save role permisison
     * @param rolePermission
     * @return
     */
    RolePermission saveRolePermission(RolePermission rolePermission);

    /**
     * Find role permission by id
     * @param uuid
     * @return
     */
    RolePermission findByUuid(String uuid);

    /**
     * Delete role permission
     * 删除rolePermission
     * @param rolePermission
     */
    void deleteRolePermission(RolePermission rolePermission);

    /**
     * Paging find role permission
     * @param page
     * @param size
     * @return
     */
    List<RolePermission> findAllRolePermission(int page, int size);

    /**
     * Find role permission by role
     * @param role
     * @return
     */
    List<RolePermission> findByRole(Role role);

    /**
     * Find role permission by permission
     * @param permission
     * @return
     */
    List<RolePermission> findByPermission(Permission permission);

    /**
     * Count all role permission
     * @return
     */
    long countAll();

}
