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


import com.webank.wedatasphere.qualitis.entity.ImsMetricAutoCollectRecord;

import java.util.List;

/**
 * @author allenzhou
 */
public interface ImsMetricAutoCollectRecordDao {

    /**
     * find by status.
     *
     * @param status
     * @return
     */
    List<ImsMetricAutoCollectRecord> findByStatus(Integer status);

    /**
     * save all
     *
     * @param noPermissionRecordList
     * @return
     */
    void saveAll(List<ImsMetricAutoCollectRecord> noPermissionRecordList);

    /**
     * save
     * @param imsMetricAutoCollectRecord
     */
    void save(ImsMetricAutoCollectRecord imsMetricAutoCollectRecord);

    /**
     * find by conditions
     * @param dbName
     * @param tableName
     * @param proxyUser
     * @return
     */
    ImsMetricAutoCollectRecord findByConditions(String dbName, String tableName, String proxyUser);

    /**
     * find by conditions
     * @param startDate
     * @param endDate
     * @param proxyUserNames
     * @return
     */
    List<ImsMetricAutoCollectRecord> findByConditions(String startDate, String endDate, List<String> proxyUserNames);
}
