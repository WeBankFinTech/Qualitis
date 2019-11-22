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
import com.webank.wedatasphere.qualitis.entity.MetaDataCluster;

import java.util.List;

/**
 * @author howeye
 */
public interface MetaDataClusterDao {

    /**
     * Find MetaDataCluster by cluster name
     * @param clusterName
     * @return
     */
    MetaDataCluster findByClusterName(String clusterName);

    /**
     * Save metaDataCluster
     * @param metaDataCluster
     * @return
     */
    MetaDataCluster saveMetaDataCluster(MetaDataCluster metaDataCluster);

    /**
     * Delete metaDataCluster
     * @param metaDataCluster
     */
    void deleteMetaDataCluster(MetaDataCluster metaDataCluster);

    /**
     * Get all meta data Cluster
     * @return
     */
    List<MetaDataCluster> findAll();

    /**
     *  Query meta data cluster
     * @param page
     * @param size
     * @return
     */
    List<MetaDataCluster> queryByPage(int page, int size);

    /**
     * Count all
     * @return
     */
    long countAll();

}
