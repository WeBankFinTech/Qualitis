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


import com.webank.wedatasphere.qualitis.entity.ServiceInfo;
import com.webank.wedatasphere.qualitis.entity.TenantUser;
import java.util.List;
import java.util.Map;

/**
 * @author allenzhou
 */
public interface ServiceInfoDao {

    /**
     * Save
     * @param serviceInfo
     * @return
     */
    ServiceInfo save(ServiceInfo serviceInfo);

    /**
     * Delete
     * @param serviceInfoId
     * @return
     */
    void delete(Long serviceInfoId);

    /**
     * Find
     * @param serviceId
     * @return
     */
    ServiceInfo findById(Long serviceId);

    /**
     * Page
     *
     * @param ip
     * @param page
     * @param size
     * @return
     */
    List<ServiceInfo> pageServiceInfo(String ip, int page, int size);

    /**
     * Count
     * @return
     * @param ip
     */
    long countAll(String ip);

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
