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

import com.webank.wedatasphere.qualitis.entity.MetaDataCluster;
import com.webank.wedatasphere.qualitis.entity.MetaDataDb;

import com.webank.wedatasphere.qualitis.entity.MetaDataCluster;
import com.webank.wedatasphere.qualitis.entity.MetaDataDb;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author howeye
 */
public interface MetaDataDbRepository extends JpaRepository<MetaDataDb, String> {

    /**
     * Find meta data db by db name and cluster
     * @param dbName
     * @param metaDataCluster
     * @return
     */
    MetaDataDb findByDbNameAndMetaDataCluster(String dbName, MetaDataCluster metaDataCluster);

    /**
     * Find meta data db by cluster
     * @param metaDataCluster
     * @return
     */
    List<MetaDataDb> findByMetaDataCluster(MetaDataCluster metaDataCluster);

    /**
     * Find all meta data db by cluster
     * @param cluster
     * @param pageable
     * @return
     */
    List<MetaDataDb> findByMetaDataCluster(MetaDataCluster cluster, Pageable pageable);

    /**
     * Count all meta data db by cluster
     * @param cluster
     * @return
     */
    long countByMetaDataCluster(MetaDataCluster cluster);
}
