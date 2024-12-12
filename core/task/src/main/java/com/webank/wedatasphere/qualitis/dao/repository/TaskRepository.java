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

import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.Task;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author howeye
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find task by application
     * @param application
     * @return
     */
    List<Task> findByApplication(Application application);

    /**
     * Find task by application pageable
     * @param application
     * @param isNonPassStatus
     * @param pageable
     * @return
     */
    @Query(value = "SELECT * FROM qualitis_application_task t WHERE t.application_id = ?1 AND IF(?2 = true, t.status != 5, 1=1)", nativeQuery = true)
    List<Task> findByApplicationPageable(String application, boolean isNonPassStatus, Pageable pageable);

    /**
     * Count
     * @param application
     * @return
     */
    @Query(value = "SELECT COUNT(t.id) FROM Task t WHERE t.application = ?1")
    int countByApplication(Application application);

    /**
     * findByApplicationIn
     * @param applicationList
     * @return
     */
    List<Task> findByApplicationIn(List<Application> applicationList);

    /**
     * Find task by application and status and remote id
     * @param application
     * @param statusList
     * @return
     */
    List<Task> findByApplicationAndStatusInAndTaskRemoteIdNotNull(Application application, List<Integer> statusList);

    /**
     * Find task by remote id and cluster name
     * @param taskRemoteId
     * @param clusterName
     * @return
     */
    Task findByTaskRemoteIdAndClusterName(Long taskRemoteId, String clusterName);

    /**
     * Find with time interval.
     *
     * @param start
     * @param end
     * @param clusterName
     * @param startTime
     * @param endTime
     * @return
     */
    @Query(value = "SELECT t FROM Task t INNER JOIN TaskDataSource tds ON (t = tds.task) WHERE t.beginTime BETWEEN ?1 AND ?2 and (tds.clusterName = ?3 and tds.databaseName = ?4 and (LENGTH(?5) = 0 or tds.tableName = ?5))")
    List<Task> findWithSubmitTimeAndDatasource(String start, String end, String clusterName, String startTime, String endTime);
}
