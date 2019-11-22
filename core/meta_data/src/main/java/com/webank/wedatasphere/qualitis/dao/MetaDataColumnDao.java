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

import com.webank.wedatasphere.qualitis.entity.MetaDataColumn;
import com.webank.wedatasphere.qualitis.entity.MetaDataTable;
import com.webank.wedatasphere.qualitis.entity.MetaDataColumn;
import com.webank.wedatasphere.qualitis.entity.MetaDataTable;

import java.util.List;

/**
 * @author howeye
 */
public interface MetaDataColumnDao {

    /**
     * Find meta data column by column name and table
     * @param columnName
     * @param metaDataTable
     * @return
     */
    MetaDataColumn findByColumnNameAndTable(String columnName, MetaDataTable metaDataTable);

    /**
     * Save metaDataColumn
     * @param metaDataColumn
     * @return
     */
    MetaDataColumn saveMetaDataColumn(MetaDataColumn metaDataColumn);

    /**
     * Delete metaDataColumn
     * @param metaDataColumn
     */
    void deleteMetaDataColumn(MetaDataColumn metaDataColumn);

    /**
     * Find MetaDataColumn by metaDataTable
     * @param metaDataTable
     * @return
     */
    List<MetaDataColumn> findAllByMetaDataTable(MetaDataTable metaDataTable);

    /**
     * Query meta data column by table
     * @param metaDataTable
     * @param page
     * @param size
     * @return
     */
    List<MetaDataColumn> queryPageByTable(MetaDataTable metaDataTable, int page, int size);

    /**
     * Count meta data column by table
     * @param tableName
     * @return
     */
    long countByTable(MetaDataTable tableName);
}
