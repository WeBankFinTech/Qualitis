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

import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.TaskDataSource;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author howeye
 */
public interface TaskDataSourceRepository extends JpaRepository<TaskDataSource, Long>, JpaSpecificationExecutor<TaskDataSource> {

    /**
     * Paging find task datasource by creator and datasource.
     * @param createUser
     * @param clusteName
     * @param databaseName
     * @param tableName
     * @param pageable
     * @return
     */
    @Query("select j from TaskDataSource j where exists (select pu.id from ProjectUser pu where pu.project.id = j.projectId and pu.userName = ?1) and (LENGTH(?2) = 0 OR j.clusterName = ?2) and (LENGTH(?3) = 0 OR j.databaseName = ?3) and (LENGTH(?4) = 0 OR j.tableName = ?4)")
    Page<TaskDataSource> findByCreateUserAndDatasource(String createUser, String clusteName, String databaseName, String tableName, Pageable pageable);

    /**
     * Counting find task datasource by creator and datasource.
     * @param createUser
     * @param clusteName
     * @param databaseName
     * @param tableName
     * @return
     */
    @Query("select count(j) from TaskDataSource j where exists (select pu.id from ProjectUser pu where pu.project.id = j.projectId and pu.userName = ?1) and (LENGTH(?2) = 0 OR j.clusterName = ?2) and (LENGTH(?3) = 0 OR j.databaseName = ?3) and (LENGTH(?4) = 0 OR j.tableName = ?4)")
    Long countByCreateUserAndDatasource(String createUser, String clusteName, String databaseName, String tableName);

    /**
     * Finding task datasource by creator
     * @param createUser
     * @return
     */
    @Query("select new map(j.clusterName as cluster_name, j.databaseName as db_name, j.tableName as table_name) from TaskDataSource j, ProjectUser pu where j.projectId = pu.project and pu.userName = ?1")
    List<Map<String, String>> findByCreateUser(String createUser);

    /**
     * Find task datasource by creator
     * @param createUser
     * @return
     */
    @Query("select j from TaskDataSource j where exists (select pu.id from ProjectUser pu where pu.project.id = j.projectId and pu.userName = ?1) group by j.clusterName, j.databaseName, j.tableName")
    List<TaskDataSource> countByCreateUser(String createUser);

    /**
     * Find task datasource by task and rule
     * @param task
     * @param ruleId
     * @return
     */
    List<TaskDataSource> findByTaskAndRuleId(Task task, Long ruleId);
}
