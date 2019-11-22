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

import com.webank.wedatasphere.qualitis.dao.MetaDataTableDao;
import com.webank.wedatasphere.qualitis.dao.repository.MetaDataTableRepository;
import com.webank.wedatasphere.qualitis.entity.MetaDataDb;
import com.webank.wedatasphere.qualitis.entity.MetaDataTable;
import com.webank.wedatasphere.qualitis.dao.MetaDataTableDao;
import com.webank.wedatasphere.qualitis.entity.MetaDataDb;
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
public class MetaDataTableDaoImpl implements MetaDataTableDao {

    @Autowired
    private MetaDataTableRepository metaDataTableRepository;

    @Override
    public MetaDataTable findByTableNameAndDb(String tableName, MetaDataDb metaDataDb) {
        return metaDataTableRepository.findByTableNameAndMetaDataDb(tableName, metaDataDb);
    }

    @Override
    public MetaDataTable saveMeteDataTable(MetaDataTable metaDataTable) {
        return metaDataTableRepository.save(metaDataTable);
    }

    @Override
    public void deleteMetaDataTable(MetaDataTable metaDataTable) {
        metaDataTableRepository.delete(metaDataTable);
    }

    @Override
    public List<MetaDataTable> findAllByMetaDataDb(MetaDataDb metaDataDb) {
        return metaDataTableRepository.findByMetaDataDb(metaDataDb);
    }

    @Override
    public List<MetaDataTable> queryPageByDb(MetaDataDb metaDataDb, int page, int size) {
        Sort sort = new Sort(Sort.Direction.ASC, "tableName");
        Pageable pageable = PageRequest.of(page, size, sort);
        return metaDataTableRepository.findByMetaDataDb(metaDataDb, pageable);
    }

    @Override
    public long countByDb(MetaDataDb dbName) {
        return metaDataTableRepository.countByMetaDataDb(dbName);
    }

}
