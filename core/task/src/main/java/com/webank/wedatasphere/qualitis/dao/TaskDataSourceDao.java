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

import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.TaskDataSource;

import java.util.List;

/**
 * @author howeye
 */
public interface TaskDataSourceDao {

    /**
     * Find task datasource by user
     * @param username
     * @param page
     * @param size
     * @return
     */
    List<TaskDataSource> findByUser(String username, Integer page, Integer size);

    /**
     * Count task datasource by user
     * @param username
     * @return
     */
    int countByUser(String username);

    /**
     * Find task datasource by user and datasource(cluster, database, table)
     * @param username
     * @param page
     * @param clusterName
     * @param databaseName
     * @param tableName
     * @param size
     * @return
     */
    List<TaskDataSource> findByUserAndDataSource(String username, String clusterName, String databaseName, String tableName, Integer page, Integer size);

    /**
     * Count task datasource by user and datasource(cluster, database, table)
     * @param username
     * @param clusterName
     * @param databaseName
     * @param tableName
     * @return
     */
    long countByUserAndDataSource(String username, String clusterName, String databaseName, String tableName);

    /**
     * Find task datasource by task and rule id
     * @param task
     * @param ruleId
     * @return
     */
    List<TaskDataSource> findByTaskAndRuleId(Task task, Long ruleId);

}
