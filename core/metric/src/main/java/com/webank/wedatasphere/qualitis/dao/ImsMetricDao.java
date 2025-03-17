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


import com.webank.wedatasphere.qualitis.dto.ImsMetricCollectDto;
import com.webank.wedatasphere.qualitis.entity.ImsMetric;
import org.springframework.data.domain.Page;

import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author allenzhou
 */
public interface ImsMetricDao {
//
//    /**
//     *
//     * @param metricId
//     * @param metricValue
//     * @param datasourceType
//     * @param clusterName
//     * @param dbName
//     * @param tableName
//     * @param columnName
//     * @param templateId
//     * @param dataStartDate
//     * @param dataEndDate
//     * @param dataUser
//     * @param page
//     * @param size
//     * @return
//     */
//    Page<ImsMetricCollectDto> findImsMetricInAdvance(Long metricId, BigDecimal metricValue, Integer datasourceType, String clusterName, String dbName, String tableName, String columnName, Long templateId, Long dataStartDate, Long dataEndDate, String dataUser, List<String> proxyUsers, int page, int size);
//
//    /**
//     *
//     * @param clusterName
//     * @param dbName
//     * @param tableName
//     * @param columnName
//     * @param calcuUnitName
//     * @return
//     */
//    List<ImsMetricCollectDto> findImsMetric(String clusterName, String dbName, String tableName, String columnName, String calcuUnitName);
//
//    /**
//     * get data by id
//     * @param metricIds
//     * @return
//     */
//    List<ImsMetric> findByIds(List<Long> metricIds);
//
//    /**
//     * @param metricId
//     * @return
//     */
//    Tuple getMetricIdentifyById(String metricId);
//
//    /**
//     * @param dbName
//     * @param tableName
//     * @param calcuUnitNameList
//     * @return
//     */
//    List<Tuple> getMetricIdentify(String dbName, String tableName, List<String> calcuUnitNameList);
//
//
//    /**
//     *
//     * @param page
//     * @param size
//     * @return
//     */
//    Page<ImsMetric> findWithPage(int page, int size);

}