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

import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.GatewayJobInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author allenzhou
 */
public interface GatewayJobInfoRepository extends JpaRepository<GatewayJobInfo, Long> {

    /**
     * Get all applicatons own by job
     * @param jobId
     * @return
     */
    @Query(value = "SELECT a FROM Application a WHERE a.jobId = ?1 ORDER BY a.submitTime DESC")
    List<Application> getAllApplication(String jobId);

    /**
     * Get one job info by ID
     * @param jobId
     * @return
     */
    @Query(value = "SELECT g FROM GatewayJobInfo g where g.jobId = ?1")
    GatewayJobInfo getByJobId(String jobId);

}
