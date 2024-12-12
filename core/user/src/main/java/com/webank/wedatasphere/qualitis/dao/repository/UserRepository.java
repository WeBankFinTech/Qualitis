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
import com.webank.wedatasphere.qualitis.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author howeye
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find user by username
     * @param username
     * @return
     */
    User findByUserName(String username);

    /**
     * Find by department id.
     * @param department
     * @return
     */
    @Query(value = "select u from User u where u.department = ?1")
    List<User> findByDepartment(Department department);

    /**
     * Check template
     * @param userInDb
     * @return
     */
    @Query(value = "select count(tu.template) from TemplateUser tu where tu.user = ?1")
    Long checkTemplate(User userInDb);
}
