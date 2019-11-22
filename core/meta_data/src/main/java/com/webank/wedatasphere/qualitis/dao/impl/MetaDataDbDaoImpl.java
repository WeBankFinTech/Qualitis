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

import com.webank.wedatasphere.qualitis.dao.MetaDataClusterDao;
import com.webank.wedatasphere.qualitis.dao.MetaDataDbDao;
import com.webank.wedatasphere.qualitis.dao.repository.MetaDataDbRepository;
import com.webank.wedatasphere.qualitis.entity.MetaDataCluster;
import com.webank.wedatasphere.qualitis.entity.MetaDataDb;
import com.webank.wedatasphere.qualitis.dao.MetaDataClusterDao;
import com.webank.wedatasphere.qualitis.dao.MetaDataDbDao;
import com.webank.wedatasphere.qualitis.dao.repository.MetaDataDbRepository;
import com.webank.wedatasphere.qualitis.entity.MetaDataCluster;
import com.webank.wedatasphere.qualitis.entity.MetaDataDb;
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
public class MetaDataDbDaoImpl implements MetaDataDbDao {

    @Autowired
    private MetaDataDbRepository metaDataDbRepository;
    @Autowired
    private MetaDataClusterDao metaDataClusterDao;

    @Override
    public MetaDataDb findByDbNameAndCluster(String dbName, MetaDataCluster metaDataCluster) {
        return metaDataDbRepository.findByDbNameAndMetaDataCluster(dbName, metaDataCluster);
    }

    @Override
    public MetaDataDb saveMetaDataDb(MetaDataDb metaDataDb) {
        return metaDataDbRepository.save(metaDataDb);
    }

    @Override
    public void deleteMetaDataDb(MetaDataDb metaDataDb) {
        metaDataDbRepository.delete(metaDataDb);
    }

    @Override
    public List<MetaDataDb> findAllByCluster(MetaDataCluster metaDataCluster) {
        return metaDataDbRepository.findByMetaDataCluster(metaDataCluster);
    }

    @Override
    public List<MetaDataDb> queryPageByCluster(MetaDataCluster metaDataCluster, int page, int size) {
        Sort sort = new Sort(Sort.Direction.ASC, "dbName");
        Pageable pageable = PageRequest.of(page, size, sort);
        return metaDataDbRepository.findByMetaDataCluster(metaDataCluster, pageable);
    }

    @Override
    public long countByCluster(MetaDataCluster metaDataCluster) {
        return metaDataDbRepository.countByMetaDataCluster(metaDataCluster);
    }
}
