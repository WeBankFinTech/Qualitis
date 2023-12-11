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

package com.webank.wedatasphere.qualitis.timer;

import com.webank.wedatasphere.qualitis.config.ImsConfig;
import com.webank.wedatasphere.qualitis.config.ThreadPoolConfig;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.service.OuterExecutionService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author allenzhou
 */
@Component
public class PendingJobCheckTimer {
    @Autowired
    private ApplicationDao applicationDao;
    @Autowired
    private OuterExecutionService outerExecutionService;
    @Autowired
    ImsConfig imsConfig;
    @Autowired
    ThreadPoolConfig threadPoolConfig;

    @PostConstruct
    public void init() {
        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(5, new PendingJobCheckThreadFactory());
        executor.scheduleWithFixedDelay(new PendingJobCheckerRunnable(applicationDao, outerExecutionService, QualitisConstants.QUALITIS_SERVER_HOST),
            0, threadPoolConfig.getPendingPeriod(), TimeUnit.MILLISECONDS);
    }
}
