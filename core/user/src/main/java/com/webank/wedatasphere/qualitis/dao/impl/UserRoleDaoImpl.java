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

import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.dao.repository.UserRoleRepository;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author howeye
 */
@Repository
public class UserRoleDaoImpl implements UserRoleDao {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    public UserRole findByUserAndRole(User user, Role role) {
        return userRoleRepository.findByUserAndRole(user, role);
    }

    @Override
    public UserRole saveUserRole(UserRole userRole) {
        return userRoleRepository.save(userRole);
    }

    @Override
    public UserRole findByUuid(String uuid) {
        return userRoleRepository.findById(uuid).orElse(null);
    }

    @Override
    public void deleteUserRole(UserRole userRole) {
        userRoleRepository.delete(userRole);
    }

    @Override
    public List<UserRole> findAllUserRole(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRoleRepository.findAll(pageable).getContent();
    }

    @Override
    public List<UserRole> findByUser(User user) {
        return userRoleRepository.findByUser(user);
    }

    @Override
    public List<UserRole> findByRole(Role role) {
        return userRoleRepository.findByRole(role);
    }

    @Override
    public long countAll() {
        return userRoleRepository.count();
    }
}
