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

package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.PermissionDao;
import com.webank.wedatasphere.qualitis.dao.repository.PermissionRepository;
import com.webank.wedatasphere.qualitis.entity.Permission;
import com.webank.wedatasphere.qualitis.dao.PermissionDao;
import com.webank.wedatasphere.qualitis.entity.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author howeye
 */
@Repository
public class PermissionDaoImpl implements PermissionDao {

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public Permission findByMethodAndUrl(String method, String url) {
        return permissionRepository.findByMethodAndUrl(method, url);
    }

    @Override
    public Permission savePermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Override
    public Permission findById(long id) {
        return permissionRepository.findById(id).orElse(null);
    }

    @Override
    public void deletePermission(Permission permission) {
        permissionRepository.delete(permission);
    }

    @Override
    public List<Permission> findAllPermission(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return permissionRepository.findAll(pageable).getContent();
    }

    @Override
    public long countAll() {
        return permissionRepository.count();
    }
}
