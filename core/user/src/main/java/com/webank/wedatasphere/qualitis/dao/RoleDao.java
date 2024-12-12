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

import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.Role;

import java.util.List;

/**
 * @author howeye
 */
public interface RoleDao {

    /**
     * Find role by role name
     * @param roleName
     * @return
     */
    Role findByRoleName(String roleName);

    /**
     * Save role
     * @param role
     * @return
     */
    Role saveRole(Role role);

    /**
     * Find role by id
     * @param id
     * @return
     */
    Role findById(Long id);

    /**
     * Delete role
     * @param role
     */
    void deleteRole(Role role);

    /**
     * Find all roles
     * @param page
     * @param size
     * @return
     */
    List<Role> findAllRole(int page, int size);

    /**
     * Count all role
     * @return
     */
    long countAll();

    /**
     * Find by department.
     * @return
     * @param departmentInDb
     */
    Role findByDepartment(Department departmentInDb);

    /**
     * Check template.
     * @return
     * @param roleInDb
     */
    boolean checkTemplate(Role roleInDb);
}