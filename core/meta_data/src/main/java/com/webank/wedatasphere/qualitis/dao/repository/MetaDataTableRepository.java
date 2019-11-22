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

import com.webank.wedatasphere.qualitis.entity.MetaDataDb;
import com.webank.wedatasphere.qualitis.entity.MetaDataTable;

import com.webank.wedatasphere.qualitis.entity.MetaDataDb;
import com.webank.wedatasphere.qualitis.entity.MetaDataTable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author howeye
 */
public interface MetaDataTableRepository extends JpaRepository<MetaDataTable, String> {

    /**
     * Find meta data table by table name and db
     * @param tableName
     * @param metaDataDb
     * @return
     */
    MetaDataTable findByTableNameAndMetaDataDb(String tableName, MetaDataDb metaDataDb);

    /**
     * Find meta data table by db
     * @param metaDataDb
     * @return
     */
    List<MetaDataTable> findByMetaDataDb(MetaDataDb metaDataDb);

    /**
     * Find meta data table by db
     * @param dataDb
     * @param pageable
     * @return
     */
    List<MetaDataTable> findByMetaDataDb(MetaDataDb dataDb, Pageable pageable);

    /**
     * Count meta data table by db
     * @param dataDb
     * @return
     */
    int countByMetaDataDb(MetaDataDb dataDb);
}
