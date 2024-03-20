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

import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.dao.repository.ClusterInfoRepository;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
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
public class ClusterInfoDaoImpl implements ClusterInfoDao {

    @Autowired
    private ClusterInfoRepository clusterInfoRepository;

    @Override
    public ClusterInfo findByClusterName(String clusterName) {
        return clusterInfoRepository.findByClusterName(clusterName);
    }

    @Override
    public ClusterInfo saveClusterInfo(ClusterInfo clusterInfo) {
        return clusterInfoRepository.save(clusterInfo);
    }

    @Override
    public ClusterInfo findById(long id) {
        return clusterInfoRepository.findById(id).orElse(null);
    }

    @Override
    public List<ClusterInfo> findByClusterNames(List<String> clusterNames) {
        return clusterInfoRepository.findByClusterNameIn(clusterNames);
    }

    @Override
    public void deleteClusterInfo(ClusterInfo clusterInfo) {
        clusterInfoRepository.delete(clusterInfo);
    }

    @Override
    public List<ClusterInfo> findAllClusterInfo(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "clusterName");
        Pageable pageable = PageRequest.of(page, size, sort);
        return clusterInfoRepository.findAll(pageable).getContent();
    }

    @Override
    public Long countAll() {
        return clusterInfoRepository.count();
    }

    @Override
    public List<ClusterInfo> findClusterInfoLikeName(String clusterName, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return clusterInfoRepository.findLikeClusterName(clusterName + "%", pageable).getContent();
    }

    @Override
    public int countTotalByName(String clusterName) {
        return clusterInfoRepository.countTotalByName(clusterName);
    }

}
