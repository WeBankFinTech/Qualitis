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

import com.webank.wedatasphere.qualitis.entity.MetaDataColumn;
import com.webank.wedatasphere.qualitis.entity.MetaDataTable;

import com.webank.wedatasphere.qualitis.entity.MetaDataColumn;
import com.webank.wedatasphere.qualitis.entity.MetaDataTable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author howeye
 */
public interface MetaDataColumnRepository extends JpaRepository<MetaDataColumn, String> {

    /**
     * Find metadata column by column name and table
     * @param columnName
     * @param metaDataTable
     * @return
     */
    MetaDataColumn findByColumnNameAndMetaDataTable(String columnName, MetaDataTable metaDataTable);

    /**
     * Find meta data column by table
     * @param metaDataTable
     * @return
     */
    List<MetaDataColumn> findByMetaDataTable(MetaDataTable metaDataTable);

    /**
     * Get all meta data tables information by table
     * @param dataTable
     * @param pageable
     * @return
     */
    List<MetaDataColumn> findByMetaDataTable(MetaDataTable dataTable, Pageable pageable);

    /**
     * Count all meta data tables information by table
     * @param dataTable
     * @return
     */
    long countByMetaDataTable(MetaDataTable dataTable);
}
