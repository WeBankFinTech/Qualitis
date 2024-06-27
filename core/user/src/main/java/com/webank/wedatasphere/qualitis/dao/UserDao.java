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
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

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
     * save All User
     * @param users
     */
    void saveAllUser(List<User> users);

    /**
     * Find user by username
     * @param username
     * @return
     */
    User findByUsername(String username);

    /**
     * Find user by username list
     * @param usernameList
     * @return
     */
    List<User> findByUsernameList(List<String> usernameList);

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
     * @param userName
     * @param departmentCode
     * @param departmentSubId
     * @param page
     * @param size
     * @return
     */
    Page<User> findAllUser(String userName, String departmentCode, Long departmentSubId, int page, int size);

    /**
     * list all username
     * @return
     */
    List<Map<String, Object>> findAllUserIdAndName();

    /**
     * Find all user names
     * @return
     */
    List<String> findAllUserName();

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

    /**
     * find By Sub Department Code
     * @param subDepartmentCode
     * @return
     */
    List<User> findBySubDepartmentCode(Long subDepartmentCode);

    /**
     * find All User
     * @return
     */
    List<User> findAll();
}
