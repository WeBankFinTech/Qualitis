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

package com.webank.wedatasphere.qualitis.rule.service.impl;

import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.rule.service.RuleLimitationService;

import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.rule.service.RuleLimitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author howeye
 */
@Service
public class RuleLimitationServiceImpl implements RuleLimitationService {

    @Autowired
    private ClusterInfoDao clusterInfoDao;

    /**
     * Get supported cluster
     * @return
     */
    @Override
    public Set<String> getLimitClusters() {
        List<ClusterInfo> clusters = clusterInfoDao.findAllClusterInfo(0, Integer.MAX_VALUE);
        if (clusters == null || clusters.isEmpty()){
            return null;
        }
        Set<String> results = new HashSet<>();
        for (ClusterInfo info : clusters){
            results.add(info.getClusterName());
        }
        return results;
    }
}
