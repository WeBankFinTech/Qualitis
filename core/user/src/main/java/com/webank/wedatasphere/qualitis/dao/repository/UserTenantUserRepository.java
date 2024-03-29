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

import com.webank.wedatasphere.qualitis.entity.TenantUser;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserTenantUser;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author allenzhou
 */
public interface UserTenantUserRepository extends JpaRepository<UserTenantUser, Long> {

    /**
     * Find user tenant user by user and tenant user
     * @param user
     * @param tenantUser
     * @return
     */
    UserTenantUser findByUserAndTenantUser(User user, TenantUser tenantUser);

    /**
     * Find user tenant user by tenant user
     * @param tenantUser
     * @param pageable
     * @return
     */
    List<UserTenantUser> findByTenantUser(TenantUser tenantUser, Pageable pageable);

    /**
     * Count by tenant user
     * @param tenantUser
     * @return
     */
    long countByTenantUser(TenantUser tenantUser);

    /**
     * Find user tenant user by user
     * @param user
     * @return
     */
    @Query(value = "SELECT qutu FROM UserTenantUser qutu where qutu.user.username = ?1")
    UserTenantUser findByUser(String user);
}
