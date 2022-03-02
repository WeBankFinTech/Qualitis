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

package com.webank.wedatasphere.qualitis.service.impl;

import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.JobKillException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.request.DataSourceExecutionRequest;
import com.webank.wedatasphere.qualitis.request.GeneralExecutionRequest;
import com.webank.wedatasphere.qualitis.request.GroupExecutionRequest;
import com.webank.wedatasphere.qualitis.request.KillApplicationsRequest;
import com.webank.wedatasphere.qualitis.request.ProjectExecutionRequest;
import com.webank.wedatasphere.qualitis.request.RuleListExecutionRequest;
import com.webank.wedatasphere.qualitis.service.OuterExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author howeye
 */
@Service
public class OuterAsyncExecutionService {
    @Autowired
    private OuterExecutionService outerExecutionService;

    private static final Logger LOGGER = LoggerFactory.getLogger(OuterAsyncExecutionService.class);

    @Async
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public void killApplications(KillApplicationsRequest request) throws UnExpectedRequestException {
        KillApplicationsRequest.checkRequest(request);

        for (String applicationId : request.getApplicationIds()) {
            try {
                LOGGER.info("Start to kill Application[ID=" + applicationId + "]");
                outerExecutionService.killApplication(applicationId, request.getLoginUser());
            } catch (JobKillException e) {
                LOGGER.error(e.getMessage(), e);
            } catch (ClusterInfoNotConfigException e) {
                LOGGER.error(e.getMessage(), e);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
