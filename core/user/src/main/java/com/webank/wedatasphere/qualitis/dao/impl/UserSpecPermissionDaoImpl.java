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

import com.webank.wedatasphere.qualitis.dao.UserSpecPermissionDao;
import com.webank.wedatasphere.qualitis.dao.repository.UserSpecPermissionRepository;
import com.webank.wedatasphere.qualitis.entity.Permission;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserSpecPermission;
import com.webank.wedatasphere.qualitis.dao.UserSpecPermissionDao;
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
public class UserSpecPermissionDaoImpl implements UserSpecPermissionDao {

    @Autowired
    private UserSpecPermissionRepository userSpecPermissionRepository;

    @Override
    public UserSpecPermission findByUserAndPermission(User user, Permission permission) {
        return userSpecPermissionRepository.findByUserAndPermission(user, permission);
    }

    @Override
    public UserSpecPermission saveUserSpecPermission(UserSpecPermission userSpecPermission) {
        return userSpecPermissionRepository.save(userSpecPermission);
    }

    @Override
    public UserSpecPermission findByUuid(String uuid) {
        return userSpecPermissionRepository.findById(uuid).orElse(null);
    }

    @Override
    public void deleteUserSpecPermission(UserSpecPermission userSpecPermission) {
        userSpecPermissionRepository.delete(userSpecPermission);
    }

    @Override
    public List<UserSpecPermission> findAllUserSpecPermission(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return userSpecPermissionRepository.findAll(pageable).getContent();
    }

    @Override
    public List<UserSpecPermission> findByUser(User user) {
        return userSpecPermissionRepository.findByUser(user);
    }

    @Override
    public List<UserSpecPermission> findByPermission(Permission permission) {
        return userSpecPermissionRepository.findByPermission(permission);
    }

    @Override
    public long countAll() {
        return userSpecPermissionRepository.count();
    }
}
