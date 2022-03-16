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

import com.webank.wedatasphere.qualitis.bean.JobChecker;
import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.ha.AbstractServiceCoordinator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author allenzhou
 */
public class UpdaterRunnable implements Runnable {
    private IChecker iChecker;
    private CountDownLatch latch;
    private List<JobChecker> jobCheckers;

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdaterRunnable.class);

    public UpdaterRunnable(IChecker iChecker, List<JobChecker> jobCheckers, CountDownLatch latch) {
        this.iChecker = iChecker;
        this.jobCheckers = jobCheckers;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            LOGGER.info(Thread.currentThread().getName() + " start to update applications.");
            for (JobChecker jobChecker : this.jobCheckers) {
                try {
                    iChecker.checkTaskStatus(jobChecker);
                } catch (Exception t) {
                    LOGGER.error("Failed to check task status, application_id: {}, task ID: {}", jobChecker.getApplicationId(), jobChecker.getTaskId(), t);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to update application, caused by: {}", e.getMessage(), e);
        } finally {
            latch.countDown();
        }
    }

}
