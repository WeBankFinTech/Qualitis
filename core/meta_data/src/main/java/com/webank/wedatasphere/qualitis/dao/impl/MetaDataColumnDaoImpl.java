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

package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.MetaDataColumnDao;
import com.webank.wedatasphere.qualitis.dao.repository.MetaDataColumnRepository;
import com.webank.wedatasphere.qualitis.entity.MetaDataColumn;
import com.webank.wedatasphere.qualitis.entity.MetaDataTable;

import com.webank.wedatasphere.qualitis.dao.MetaDataColumnDao;
import com.webank.wedatasphere.qualitis.dao.repository.MetaDataColumnRepository;
import com.webank.wedatasphere.qualitis.entity.MetaDataColumn;
import com.webank.wedatasphere.qualitis.entity.MetaDataTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author howeye
 */
@Repository
public class MetaDataColumnDaoImpl implements MetaDataColumnDao {

    @Autowired
    private MetaDataColumnRepository metaDataColumnRepository;

    @Override
    public MetaDataColumn findByColumnNameAndTable(String columnName, MetaDataTable metaDataTable) {
        return metaDataColumnRepository.findByColumnNameAndMetaDataTable(columnName, metaDataTable);
    }

    @Override
    public MetaDataColumn saveMetaDataColumn(MetaDataColumn metaDataColumn) {
        return metaDataColumnRepository.save(metaDataColumn);
    }

    @Override
    public void deleteMetaDataColumn(MetaDataColumn metaDataColumn) {
        metaDataColumnRepository.delete(metaDataColumn);
    }

    @Override
    public List<MetaDataColumn> findAllByMetaDataTable(MetaDataTable metaDataTable) {
        return metaDataColumnRepository.findByMetaDataTable(metaDataTable);
    }

    @Override
    public List<MetaDataColumn> queryPageByTable(MetaDataTable metaDataTable, int page, int size) {
        Sort sort = new Sort(Sort.Direction.ASC, "columnName");
        Pageable pageable = PageRequest.of(page, size, sort);
        return metaDataColumnRepository.findByMetaDataTable(metaDataTable, pageable);
    }

    @Override
    public long countByTable(MetaDataTable tableName) {
        return metaDataColumnRepository.countByMetaDataTable(tableName);
    }
}
