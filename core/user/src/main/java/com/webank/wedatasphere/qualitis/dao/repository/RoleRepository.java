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

import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.Role;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author howeye
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by name
     * @param roleName
     * @return
     */
    Role findByName(String roleName);

    /**
     * Find by department.
     * @param department
     * @return
     */
    @Query(value = "select r from Role r where r.department = ?1")
    Role findByDepartment(Department department);

    /**
     * Check template.
     * @param department
     * @return
     */
    @Query(value = "select count(td.template) from TemplateDepartment td where td.department = ?1")
    Long checkTemplate(Department department);
}
