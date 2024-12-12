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

import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.ProxyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author howeye
 */
public interface ProxyUserRepository extends JpaRepository<ProxyUser, Long>, JpaSpecificationExecutor<ProxyUser> {

    /**
     * Find proxy user by proxy user name
     *
     * @param proxyUserName
     * @return
     */
    ProxyUser findByProxyUserName(String proxyUserName);

    /**
     * find By Department
     * @param department
     * @return
     */
    List<ProxyUser> findByDepartment(Department department);

    /**
     * Get all names
     * @return
     */
    @Query(value = "select pu.proxyUserName from ProxyUser pu where pu.proxyUserName is not null and length(pu.proxyUserName) > 0")
    List<String> getAllProxyUserName();
}
