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

import com.webank.wedatasphere.qualitis.entity.ImsMetricCollect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.Tuple;
import java.util.List;

/**
 * @author allenzhou
 */
public interface ImsMetricCollectRepository extends JpaRepository<ImsMetricCollect, Long> {

    /**
     * Find by exec ip.
     *
     * @param execIp
     * @return
     */
    List<ImsMetricCollect> findByExecIp(String execIp);

    /**
     * Find by datasource
     *
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param columnName
     * @return
     */
    @Query(value = "SELECT * FROM qualitis_imsmetric_collect qic " +
            "WHERE (?1 is null or qic.cluster_name = ?1) and (?2 is null or qic.db_name = ?2) AND (?3 is null or qic.table_name = ?3) AND (?4 is null or qic.column_name = ?4) AND (?5 is null or qic.filter = ?5)", nativeQuery = true)
    List<ImsMetricCollect> findByDatasource(String clusterName, String dbName, String tableName, String columnName, String filter);

    /**
     *
     * @param clusterName
     * @param dbName
     * @param tableName
     * @return
     */
    @Query(value = "SELECT DISTINCT qic.filter FROM qualitis_imsmetric_collect qic " +
            "WHERE (?1 is null or qic.cluster_name = ?1) and (?2 is null or qic.db_name = ?2) AND (?3 is null or qic.table_name = ?3)"
            , nativeQuery = true)
    List<String> findPartitionList(String clusterName, String dbName, String tableName);

    /**
     * @param templateEnName
     * @param templateCnName
     * @param cluster
     * @param db
     * @param table
     * @param partition
     * @param createUser
     * @param modifyUser
     * @param createStartTime
     * @param createEndTime
     * @param modifyStartTime
     * @param modifyEndTime
     * @param pageable
     * @return
     */
    @Query(value = "select qic.id, qic.cluster_name as clusterName,qic.db_name as dbName,qic.table_name as tableName,qic.`filter` as `partition`" +
            ", qic.column_name as columnName,qic.execution_parameters_name as executionParametersName, qic.create_user as createUser, qic.modify_user as modifyUser" +
            ", qic.create_time as createTime, qic.modify_time as modifyTime, qic.template_id as templateId,qt.name as templateCnName,qt.en_name as templateEnName" +
            ", qis.exec_freq as execFreq, qic.proxy_user as proxyUser " +
            "from qualitis_imsmetric_collect qic left join qualitis_template qt on qic.template_id = qt.id left join qualitis_imsmetric_scheduler qis on qis.db_name=qic.db_name and qis.table_name=qic.table_name and qis.partition_value=qic.filter " +
            "where (?1 is null or qt.en_name like CONCAT('%', CONCAT(?1, '%') ) ) " +
            "and (?2 is null or qt.name like CONCAT('%', CONCAT(?2, '%') ) ) " +
            "and (?3 is null or qic.cluster_name = ?3) " +
            "and (?4 is null or qic.db_name = ?4) " +
            "and (?5 is null or qic.table_name = ?5) " +
            "and (?14 is null or qic.column_name = ?14) " +
            "and (?6 is null or qic.filter = ?6) " +
            "and (?7 is null or qic.create_user = ?7) " +
            "and (?8 is null or qic.modify_user = ?8) " +
            "and (?9 is null or qic.create_time >= ?9) " +
            "and (?10 is null or qic.create_time <= ?10) " +
            "and (?11 is null or qic.modify_time >= ?11) " +
            "and (?12 is null or qic.modify_time <= ?12) " +
            "and qic.proxy_user in (?13)", nativeQuery = true)
    Page<Tuple> queryListWithPage(String templateEnName, String templateCnName, String cluster, String db, String table, String partition, String createUser, String modifyUser, String createStartTime, String createEndTime, String modifyStartTime, String modifyEndTime, List<String> proxyUsers, String column, Pageable pageable);

    @Query(value = "select distinct qic.proxy_user from qualitis_imsmetric_collect qic where qic.db_name = ?1 and qic.table_name = ?2", nativeQuery = true)
    List<String> findDistinctProxyUser(String db, String table);
}
