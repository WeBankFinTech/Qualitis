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

import com.webank.wedatasphere.qualitis.entity.ImsMetricScheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author allenzhou
 */
public interface ImsMetricSchedulerRepository extends JpaRepository<ImsMetricScheduler, Long> {

    /**
     * Find by datasource
     * @param dbName
     * @param tableName
     * @return
     */
    @Query(value = "SELECT * FROM qualitis_imsmetric_scheduler qis WHERE qis.db_name = ?1 AND qis.table_name = ?2", nativeQuery = true)
    List<ImsMetricScheduler> findByDatasource(String dbName, String tableName);

    /**
     * get metric scheduler
     * @param dbName
     * @param tableName
     * @param partition
     * @return
     */
    @Query("SELECT ims from ImsMetricScheduler ims WHERE dbName=?1 AND tableName=?2 AND partition=?3")
    ImsMetricScheduler findByPartition(String dbName, String tableName, String partition);
}
