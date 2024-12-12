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
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.RolePermission;
import com.webank.wedatasphere.qualitis.entity.Permission;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author howeye
 */
public interface RolePermissionRepository extends JpaRepository<RolePermission, String> {

    /**
     * Find role permission by role and permission
     * @param role
     * @param permission
     * @return
     */
    RolePermission findByRoleAndPermission(Role role, Permission permission);

    /**
     * Find role permission by role
     * @param role
     * @return
     */
    List<RolePermission> findByRole(Role role);

    /**
     * Find role permission by permission
     * 根据permission查找RolePermission
     * @param permission
     * @return
     */
    List<RolePermission> findByPermission(Permission permission);

}
