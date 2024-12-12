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
import com.webank.wedatasphere.qualitis.entity.User;

import java.util.List;

/**
 * @author howeye
 */
public interface UserDao {

    /**
     * Save user
     * @param user
     * @return
     */
    User saveUser(User user);

    /**
     * Find user by username
     * @param username
     * @return
     */
    User findByUsername(String username);

    /**
     * Find user by id
     * @param id
     * @return
     */
    User findById(long id);

    /**
     * Delete user
     * @param user
     * @return
     */
    void deleteUser(User user);


    /**
     * Paging find all users
     * @param page
     * @param size
     * @return
     */
    List<User> findAllUser(int page, int size);


    /**
     * Count all user
     * @return
     */
    Long countAll();

    /**
     * Find all users
     * @return
     */
    List<User> findAllUser();

    /**
     * Find user by department.
     * @param departmentInDb
     * @return
     */
    List<User> findByDepartment(Department departmentInDb);

    /**
     * Check template.
     * @param userInDb
     * @return
     */
    boolean checkTemplate(User userInDb);
}
