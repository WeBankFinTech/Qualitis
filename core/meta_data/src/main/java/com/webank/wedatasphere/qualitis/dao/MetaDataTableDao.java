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

import com.webank.wedatasphere.qualitis.entity.MetaDataDb;
import com.webank.wedatasphere.qualitis.entity.MetaDataTable;
import com.webank.wedatasphere.qualitis.entity.MetaDataDb;
import com.webank.wedatasphere.qualitis.entity.MetaDataTable;

import java.util.List;

/**
 * @author howeye
 */
public interface MetaDataTableDao {

    /**
     * Find meta data table by table name and meta data db
     * @param tableName
     * @param metaDataDb
     * @return
     */
    MetaDataTable findByTableNameAndDb(String tableName, MetaDataDb metaDataDb);

    /**
     * Save metaDataTable
     * @param metaDataTable
     * @return
     */
    MetaDataTable saveMeteDataTable(MetaDataTable metaDataTable);

    /**
     * Delete metaDataTable
     * @param metaDataTable
     */
    void deleteMetaDataTable(MetaDataTable metaDataTable);

    /**
     * Find MetaDataTable by metaDataDb
     * @param metaDataDb
     * @return
     */
    List<MetaDataTable> findAllByMetaDataDb(MetaDataDb metaDataDb);

    /**
     * Query meta data table by db
     * @param metaDataDb
     * @param page
     * @param size
     * @return
     */
    List<MetaDataTable> queryPageByDb(MetaDataDb metaDataDb, int page, int size);

    /**
     * Count all
     * @param dbName
     * @return
     */
    long countByDb(MetaDataDb dbName);

}
