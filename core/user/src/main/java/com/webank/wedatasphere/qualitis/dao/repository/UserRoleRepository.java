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

import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author howeye
 */
public interface UserRoleRepository extends JpaRepository<UserRole, String> {

    /**
     * Find user role by user and role
     * @param user
     * @param role
     * @return
     */
    UserRole findByUserAndRole(User user, Role role);

    /**
     * Find user role by user
     * @param user
     * @return
     */
    List<UserRole> findByUser(User user);

    /**
     * Find user role by role
     * @param role
     * @return
     */
    List<UserRole> findByRole(Role role);

    /**
     * Find user role by role
     * @param role
     * @return
     */
    @Query(value = "select u from UserRole u where u.role in ?1")
    List<UserRole> findByRoleList(List<Role> role);

    /**
     * count Position Role
     *
     * @param id
     * @param roleType
     * @return
     */
    @Query(value = "select count(a.id) from qualitis_auth_user_role a left join qualitis_auth_role b on a.role_id =b.id where a.user_id =?1 and b.role_type =?2", nativeQuery = true)
    Long countPositionRole(Long id, Integer roleType);


    /**
     * find All UserRole
     *
     * @param userName
     * @param pageable
     * @return
     */
    @Query(value = "select a.* from qualitis_auth_user_role a left join qualitis_auth_user b on a.user_id=b.id  where 1=1 AND if(nullif(?1,'')!='', b.username like ?1,1=1) ",
            countQuery = "select count(*) from qualitis_auth_user_role a left join qualitis_auth_user b on a.user_id=b.id  where 1=1 AND if(nullif(?1,'')!='', b.username like ?1,1=1)", nativeQuery = true)
    Page<UserRole> findAllUserRole(String userName, Pageable pageable);

    /**
     * count All UserRole
     *
     * @param userName
     * @return
     */
    @Query(value = "select count(*) from qualitis_auth_user_role a left join qualitis_auth_user b on a.user_id=b.id  where 1=1 AND if(nullif(?1,'')!='', b.username like ?1,1=1)", nativeQuery = true)
    Long countAllUserRole(String userName);

}
