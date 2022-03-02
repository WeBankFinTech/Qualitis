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
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

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
