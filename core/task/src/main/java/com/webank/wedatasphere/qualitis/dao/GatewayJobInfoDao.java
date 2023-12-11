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

import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.GatewayJobInfo;
import java.util.List;

/**
 * @author allenzhou
 */
public interface GatewayJobInfoDao {

  /**
   * Get all application own by job
   * @param jobId
   * @return
   */
    List<Application> getAllApplication(String jobId);

  /**
   * Get job info by job id
   * @param jobId
   * @return
   */
    GatewayJobInfo getByJobId(String jobId);

  /**
   * Save
   * @param gatewayJobInfo
   * @return
   */
    GatewayJobInfo save(GatewayJobInfo gatewayJobInfo);

    /**
   * Save and flush
   * @param gatewayJobInfo
   * @return
   */
    GatewayJobInfo saveAndFlush(GatewayJobInfo gatewayJobInfo);

}
