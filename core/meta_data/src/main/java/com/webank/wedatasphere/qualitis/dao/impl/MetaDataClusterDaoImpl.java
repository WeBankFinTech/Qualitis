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
import com.webank.wedatasphere.qualitis.dao.repository.MetaDataClusterRepository;
import com.webank.wedatasphere.qualitis.entity.MetaDataCluster;
import com.webank.wedatasphere.qualitis.entity.MetaDataCluster;
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
public class MetaDataClusterDaoImpl implements MetaDataClusterDao {

    @Autowired
    private MetaDataClusterRepository metaDataClusterRepository;

    @Override
    public MetaDataCluster findByClusterName(String clusterName) {
        return metaDataClusterRepository.findById(clusterName).orElse(null);
    }

    @Override
    public MetaDataCluster saveMetaDataCluster(MetaDataCluster metaDataCluster) {
        return metaDataClusterRepository.save(metaDataCluster);
    }

    @Override
    public void deleteMetaDataCluster(MetaDataCluster metaDataCluster) {
        metaDataClusterRepository.delete(metaDataCluster);
    }

    @Override
    public List<MetaDataCluster> findAll() {
        return metaDataClusterRepository.findAll();
    }

    @Override
    public List<MetaDataCluster> queryByPage(int page, int size) {
        Sort sort = new Sort(Sort.Direction.ASC, "clusterName");
        Pageable pageable = PageRequest.of(page, size, sort);
        return metaDataClusterRepository.findAll(pageable).getContent();
    }

    @Override
    public long countAll() {
        return metaDataClusterRepository.count();
    }

}
