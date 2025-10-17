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
import com.webank.wedatasphere.qualitis.entity.ImsMetricCollect;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * @author allenzhou
 */
public interface ImsMetricCollectDao {
    /**
     * Find by exec ip.
     *
     * @param execIp
     * @return
     */
    List<ImsMetricCollect> findByExecIp(String execIp);

    /**
     * Find by datasource
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param columnName
     * @return
     */
    List<ImsMetricCollect> findByDatasource(String clusterName, String dbName, String tableName, String columnName, String filter);

    /**
     *
     * @param clusterName
     * @param dbName
     * @param tableName
     * @return
     */
    List<String> findPartitionList(String clusterName, String dbName, String tableName);

    /**
     * batch save
     *
     * @param imsMetricCollectList
     */
    void saveAll(List<ImsMetricCollect> imsMetricCollectList);

    /**
     * get all collects
     *
     * @param ids
     * @return
     */
    List<ImsMetricCollect> findByIds(List<Long> ids);

    /**
     * get collect
     * @param id
     * @return
     */
    Optional<ImsMetricCollect> findById(Long id);


    /**
     * delete by ids
     * @param ids
     */
    void deleteByIds(List<Long> ids);



    /**
     *
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
     * @param proxyUser
     * @param page
     * @param size
     * @return
     */
    Page<ImsMetricCollectDto> queryListWithPage(String templateEnName, String templateCnName, String cluster, String db,String table,String column, String partition,String createUser, String modifyUser, String createStartTime, String createEndTime, String modifyStartTime, String modifyEndTime, List<String> proxyUser, int page, int size);

    /**
     * check if same proxy user
     * @param db
     * @param table
     * @return
     */
    List<String> queryDistinctProxyUser(String db, String table);
}
