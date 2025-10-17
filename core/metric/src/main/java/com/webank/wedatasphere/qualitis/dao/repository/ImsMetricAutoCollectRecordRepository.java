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

import com.webank.wedatasphere.qualitis.entity.ImsMetricAutoCollectRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author allenzhou
 */
public interface ImsMetricAutoCollectRecordRepository extends JpaRepository<ImsMetricAutoCollectRecord, Long> {

    /**
     * Find by status.
     *
     * @param status
     * @return
     */
    @Query(value = "SELECT * FROM qualitis_auto_collect_record WHERE status = ?1", nativeQuery = true)
    List<ImsMetricAutoCollectRecord> findByStatus(Integer status);

    /**
     * Find by conditions
     * @param dbName
     * @param tableName
     * @param proxyUser
     * @return
     */
    @Query(value = "SELECT * FROM qualitis_auto_collect_record WHERE db_name = ?1 and table_name = ?2 and proxy_user = ?3", nativeQuery = true)
    ImsMetricAutoCollectRecord findByConditions(String dbName, String tableName, String proxyUser);

    /**
     * Find by conditions
     * @param startDate
     * @param endDate
     * @param proxyUserNames
     * @return
     */
    @Query(value = "SELECT * FROM qualitis_auto_collect_record WHERE create_date BETWEEN ?1 AND ?2 AND proxy_user IN ?3", nativeQuery = true)
    List<ImsMetricAutoCollectRecord> findByConditions(String startDate, String endDate, List<String> proxyUserNames);
}
