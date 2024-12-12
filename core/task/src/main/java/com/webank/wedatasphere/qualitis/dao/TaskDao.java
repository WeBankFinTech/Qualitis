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

import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.Task;

import java.util.List;

/**
 * @author howeye
 */
public interface TaskDao {

    /**
     * Save task
     * @param task
     * @return
     */
    Task save(Task task);

    /**
     * Find task by id
     * @param taskId
     * @return
     */
    Task findById(Long taskId);

    /**
     *  Find task by remote id and cluster name.
     * @param remoteTaskId
     * @param clusterName
     * @return
     */
    Task findByRemoteTaskIdAndClusterName(Long remoteTaskId, String clusterName);

    /**
     * Find task by application
     * @param application
     * @return
     */
    List<Task> findByApplication(Application application);

    /**
     * Save all tasks
     * @param tasks
     */
    void saveAll(List<Task> tasks);

    /**
     * Find task by application and status and remote id
     * @param application
     * @param statusList
     * @return
     */
    List<Task> findByApplicationAndStatusInAndTaskRemoteIdNotNull(Application application, List<Integer> statusList);

    /**
     * Find with time interval and datasource.
     * @param startTime
     * @param endTime
     * @param clusterName
     * @param databaseName
     * @param tableName
     * @return
     */
    List<Task> findWithSubmitTimeAndDatasource(String startTime, String endTime, String clusterName, String databaseName, String tableName);
}
