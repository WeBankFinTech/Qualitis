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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author allenzhou
 */
public interface TenantUserRepository extends JpaRepository<TenantUser, Long> {

    /**
     * Find proxy user by tenant user name
     * @param tenantUserName
     * @return
     */
    TenantUser findByTenantName(String tenantUserName);

    /**
     * Find proxy user by tenant user name with page
     * @param tenantUserName
     * @param pageable
     * @return
     */
    @Query(value = "select tu from TenantUser tu where length(?1) = 0 OR tu.tenantName like ?1")
    Page<TenantUser> findByTenantNameWithPage(String tenantUserName, Pageable pageable);

}
