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

import com.webank.wedatasphere.qualitis.entity.ImsMetricHistoryScheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author allenzhou
 */
public interface ImsMetricHistorySchedulerRepository extends JpaRepository<ImsMetricHistoryScheduler, Long> {


    /**
     * Find last
     * @return
     */
    @Query(value = "SELECT * FROM qualitis_imsmetric_history_scheduler WHERE id = (SELECT MAX(id) FROM qualitis_imsmetric_history_scheduler)", nativeQuery = true)
    ImsMetricHistoryScheduler findLast();


    /**
     * Count the number of records that are within the time period of the last day.
     * @return
     */
    @Query(value = "SELECT COUNT(1) FROM qualitis_imsmetric_history_scheduler WHERE submit_time > ?1 AND submit_time < 2", nativeQuery = true)
    int countWithLastDay(String startTime, String endTime);

    /**
     *
     * @param db
     * @param table
     * @param collectStatus
     * @return
     */
    @Query(value = "SELECT * FROM qualitis_imsmetric_history_scheduler WHERE db_name=?1 AND table_name=?2 AND collect_status=?3", nativeQuery = true)
    List<ImsMetricHistoryScheduler> findByCollectStatus(String db, String table, Integer collectStatus);
}
