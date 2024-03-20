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

import com.webank.wedatasphere.qualitis.entity.ServiceInfo;
import com.webank.wedatasphere.qualitis.entity.TenantUser;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author allenzhou
 */
public interface ServiceInfoRepository extends JpaRepository<ServiceInfo, Long> {
    /**
     * Page
     *
     * @param ip
     * @param pageable
     * @return
     */
    @Query(value = "SELECT si FROM ServiceInfo si WHERE (si.ip like ?1 or length(?1) = 0)")
    Page<ServiceInfo> pageServiceInfo(String ip, Pageable pageable);

    /**
     * Count by ip
     * @param ip
     * @return
     */
    @Query(value = "select count(si.ip) from ServiceInfo si where si.ip like ?1 or length(?1) = 0")
    long countByIp(String ip);

    /**
     * Find by status
     * @param code
     * @return
     */
    List<ServiceInfo> findByStatus(Integer code);

    /**
     * Find by tenant user
     * @param tenantUser
     * @return
     */
    List<ServiceInfo> findByTenantUser(TenantUser tenantUser);
}
