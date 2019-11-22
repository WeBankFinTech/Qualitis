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

import com.webank.wedatasphere.qualitis.entity.ClusterInfo;

import java.util.List;

/**
 * @author howeye
 */
public interface ClusterInfoDao {

    /**
     * Find cluster info by cluster name
     * @param clusterName
     * @return
     */
    ClusterInfo findByClusterName(String clusterName);

    /**
     * Save cluster info
     * @param clusterInfo
     * @return
     */
    ClusterInfo saveClusterInfo(ClusterInfo clusterInfo);

    /**
     * Find cluster info by id
     * @param id
     * @return
     */
    ClusterInfo findById(long id);

    /**
     * Delete cluster info
     * @param clusterInfo
     */
    void deleteClusterInfo(ClusterInfo clusterInfo);

    /**
     * Paging find all cluster info
     * @param page
     * @param size
     * @return
     */
    List<ClusterInfo> findAllClusterInfo(int page, int size);

    /**
     * Count all cluster info
     * @return
     */
    Long countAll();

}
