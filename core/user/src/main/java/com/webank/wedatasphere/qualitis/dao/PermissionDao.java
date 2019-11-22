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
import com.webank.wedatasphere.qualitis.entity.Permission;

import java.util.List;

/**
 * @author howeye
 */
public interface PermissionDao {

    /**
     * Find permission by method and url
     * @param method
     * @param url
     * @return
     */
    Permission findByMethodAndUrl(String method, String url);

    /**
     * Save permission
     * @param permission
     * @return
     */
    Permission savePermission(Permission permission);

    /**
     * Find permission by id
     * @param id
     * @return
     */
    Permission findById(long id);

    /**
     * Delete permission
     * @param permission
     */
    void deletePermission(Permission permission);

    /**
     * Find all permission
     * @param page
     * @param size
     * @return
     */
    List<Permission> findAllPermission(int page, int size);

    /**
     * Count all permission
     * @return
     */
    long countAll();

}
