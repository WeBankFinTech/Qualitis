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

import com.webank.wedatasphere.qualitis.entity.ImsMetric;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author allenzhou
 */
public interface ImsMetricRepository extends JpaRepository<ImsMetric, Long> {

    /**
     *
     * @param datasourceType
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param columnName
     * @param templateId
     * @param dataStartDate
     * @param dataEndDate
     * @param dataUser
     * @param metricId
     * @param metricValue
     * @param pageable
     * @return
     */
    @Query(value = "SELECT metric_id, data_date, MAX(update_time) as max_update_time FROM qualitis_imsmetric_data_new qid " +
            "INNER JOIN qualitis_imsmetric qi ON qi.id = qid.metric_id " +
            "INNER JOIN qualitis_imsmetric_collect qic ON qi.metric_collect_id = qic.id " +
            "WHERE (?1 is null or qic.datasource_type = ?1) " +
            "AND (?2 is null or qic.cluster_name = ?2) " +
            "AND (?3 is null or qic.db_name = ?3) " +
            "AND (?4 is null or qic.table_name = ?4) " +
            "AND (?5 is null or qic.column_name = ?5) " +
            "AND (?6 is null or qic.template_id = ?6) " +
            "AND (?7 is null or qid.data_date >= ?7) " +
            "AND (?8 is null or qid.data_date <= ?8) " +
            "AND (?9 is null or qid.datasource_user = ?9) " +
            "AND (?10 is null or qi.id = ?10) " +
            "AND (?11 is null or qid.metric_value = ?11) " +
            "AND qid.datasource_user in (?12) " +
            "GROUP BY metric_id order by qi.id desc", nativeQuery = true)
    Page<Tuple> findImsMetricInAdvance(Integer datasourceType, String clusterName, String dbName, String tableName, String columnName
            , Long templateId, Long dataStartDate, Long dataEndDate, String dataUser, Long metricId, BigDecimal metricValue, List<String> proxyUsers, Pageable pageable);

    /**
     *
     * @param metricId
     * @param dataDate
     * @return
     */
    @Query(value = "SELECT qid2.metric_id,qid2.metric_value,qi.name as metric_name,qi.identify_value as identify_value,qid2.datasource_user,qid2.data_date,qid2.update_time ,qic.datasource_type, qic.cluster_name,qic.db_name,qic.table_name,qic.column_name,qic.template_id, qi.metric_collect_id " +
            "FROM qualitis_imsmetric qi JOIN qualitis_imsmetric_collect qic ON qi.metric_collect_id = qic.id " +
            "INNER JOIN qualitis_imsmetric_data_new qid2 ON qid2.metric_id = qi.id " +
            "WHERE qi.id=?1 AND qid2.data_date=?2", nativeQuery = true)
    Tuple findImsMetricResultByIdAndDataDate(Long metricId, Long dataDate);

    /**
     * get ImsMetric by cluster,db,table,column
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param columnName
     * @param calcuUnitName
     * @return
     */
    @Query(value = "select qi.id as metric_id,qcu.name as calcu_unit_name,qic.column_type, qic.cluster_name ,qic.db_name " +
            ",qic.table_name ,qic.column_name, qic.proxy_user, qi.identify_value from qualitis_imsmetric qi " +
            "inner join qualitis_imsmetric_collect qic on qi.metric_collect_id = qic.id " +
            "inner join qualitis_template qt on qt.id = qic.template_id " +
            "inner join qualitis_calcu_unit qcu on qcu.id = qt.calcu_unit_id " +
            "where (?1 is null or qic.cluster_name = ?1) and (?2 is null or qic.db_name = ?2) " +
            "and (?3 is null or qic.table_name = ?3) and (?4 is null or qic.column_name = ?4)" +
            "and (?5 is null or qcu.name = ?5)", nativeQuery = true)
    List<Tuple> findImsMetric(String clusterName, String dbName, String tableName, String columnName, String calcuUnitName);

    /**
     * @param metricId
     * @return
     */
    @Query(value = "SELECT qi.id metric_id,qic.db_name,qic.table_name,qic.column_name,qcu.name calcu_unit_name,qic.proxy_user," +
            "qi.identify_value FROM qualitis_imsmetric qi " +
            " LEFT JOIN qualitis_imsmetric_collect qic ON qic.id = qi.metric_collect_id " +
            " LEFT JOIN qualitis_template qt ON qic.template_id =qt.id AND qt.template_type=5 " +
            " LEFT JOIN qualitis_calcu_unit qcu ON qt.calcu_unit_id=qcu.id  " +
            " WHERE qi.id = ?1", nativeQuery = true)
    Tuple getMetricIdentifyById(String metricId);

    /**
     * @param dbName
     * @param tableName
     * @return count
     */
    @Query(value = "SELECT qi.id metric_id, qcu.name calcu_unit_name FROM qualitis_imsmetric qi " +
            " LEFT JOIN qualitis_imsmetric_collect qic ON qic.id = qi.metric_collect_id " +
            " LEFT JOIN qualitis_template qt ON qic.template_id =qt.id AND qt.template_type=5 " +
            " LEFT JOIN qualitis_calcu_unit qcu ON qt.calcu_unit_id=qcu.id  " +
            " WHERE qic.db_name = ?1 AND qic.table_name = ?2 AND qcu.name in (?3) ", nativeQuery = true)
    List<Tuple> getMetricIdentify(String dbName, String tableName, List<String> calcuUnitNameList);

    /**
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Query(value = "SELECT * FROM qualitis_imsmetric qi WHERE qi.modify_time > ?1 AND qi.modify_time <= ?2", nativeQuery = true)
    List<ImsMetric> findByBetweenModifyTime(String startTime, String endTime);

}
