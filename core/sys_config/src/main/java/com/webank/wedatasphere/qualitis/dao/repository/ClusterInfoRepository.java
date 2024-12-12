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

import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author howeye
 */
public interface ClusterInfoRepository extends JpaRepository<ClusterInfo, Long> {

    /**
     * Find clutser info by cluster name
     * @param clusterName
     * @return
     */
    ClusterInfo findByClusterName(String clusterName);

    /**
     * Find cluster like name
     * @param clusterName
     * @param pageable
     * @return
     */
    @Query(value = "select ci from ClusterInfo ci where ci.clusterName like ?1")
    Page<ClusterInfo> findLikeClusterName(String clusterName, Pageable pageable);

    /**
     * Count by name for page
     * @param clusterName
     * @return
     */
    @Query(value = "SELECT COUNT(ci.id) from ClusterInfo ci where ci.clusterName like ?1")
    int countTotalByName(String clusterName);

    /**
     * Find clutser info List by cluster name
     * @param clusterNameList
     * @return
     */
    List<ClusterInfo> findByClusterNameIn(List<String> clusterNameList);

}
