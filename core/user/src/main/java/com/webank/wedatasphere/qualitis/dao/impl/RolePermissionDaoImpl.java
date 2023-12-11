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

import com.webank.wedatasphere.qualitis.dao.RolePermissionDao;
import com.webank.wedatasphere.qualitis.dao.repository.RolePermissionRepository;
import com.webank.wedatasphere.qualitis.entity.Permission;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.RolePermission;
import com.webank.wedatasphere.qualitis.dao.RolePermissionDao;
import com.webank.wedatasphere.qualitis.entity.Permission;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.RolePermission;
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
public class RolePermissionDaoImpl implements RolePermissionDao {

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Override
    public RolePermission findByRoleAndPermission(Role role, Permission permission) {
        return rolePermissionRepository.findByRoleAndPermission(role, permission);
    }

    @Override
    public RolePermission saveRolePermission(RolePermission rolePermission) {
        return rolePermissionRepository.save(rolePermission);
    }

    @Override
    public RolePermission findByUuid(String uuid) {
        return rolePermissionRepository.findById(uuid).orElse(null);
    }

    @Override
    public void deleteRolePermission(RolePermission rolePermission) {
        rolePermissionRepository.delete(rolePermission);
    }

    @Override
    public List<RolePermission> findAllRolePermission(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "role");
        Pageable pageable = PageRequest.of(page, size, sort);
        return rolePermissionRepository.findAll(pageable).getContent();
    }

    @Override
    public List<RolePermission> findByRole(Role role) {
        return rolePermissionRepository.findByRole(role);
    }

    @Override
    public List<RolePermission> findByPermission(Permission permission) {
        return rolePermissionRepository.findByPermission(permission);
    }

    @Override
    public long countAll() {
        return rolePermissionRepository.count();
    }
}
