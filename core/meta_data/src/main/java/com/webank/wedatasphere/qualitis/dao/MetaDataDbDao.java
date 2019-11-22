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

import com.webank.wedatasphere.qualitis.entity.MetaDataCluster;
import com.webank.wedatasphere.qualitis.entity.MetaDataDb;
import com.webank.wedatasphere.qualitis.entity.MetaDataCluster;
import com.webank.wedatasphere.qualitis.entity.MetaDataDb;

import java.util.List;

/**
 * @author howeye
 */
public interface MetaDataDbDao {

    /**
     * Find meta data by db name and cluster
     * @param dbName
     * @param metaDataCluster
     * @return
     */
    MetaDataDb findByDbNameAndCluster(String dbName, MetaDataCluster metaDataCluster);

    /**
     * Save metaDataDb
     * @param metaDataDb
     * @return
     */
    MetaDataDb saveMetaDataDb(MetaDataDb metaDataDb);

    /**
     * Delete metaDataDb
     * @param metaDataDb
     */
    void deleteMetaDataDb(MetaDataDb metaDataDb);

    /**
     * Find meta data db by cluster
     * @param metaDataCluster
     * @return
     */
    List<MetaDataDb> findAllByCluster(MetaDataCluster metaDataCluster);

    /**
     * Query meta data db by cluster
     * @param metaDataCluster
     * @param page
     * @param size
     * @return
     */
    List<MetaDataDb> queryPageByCluster(MetaDataCluster metaDataCluster, int page, int size);

    /**
     * Count all
     * @param metaDataCluster
     * @return
     */
    long countByCluster(MetaDataCluster metaDataCluster);
}
