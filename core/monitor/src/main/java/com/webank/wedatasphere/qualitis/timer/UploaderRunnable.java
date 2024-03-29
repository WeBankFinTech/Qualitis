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

import com.webank.wedatasphere.qualitis.ha.AbstractServiceCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author allenzhou
 */
public class UploaderRunnable implements Runnable {
    private IChecker iChecker;
    private AbstractServiceCoordinator abstractServiceCoordinator;

    private static final Logger LOGGER = LoggerFactory.getLogger(UploaderRunnable.class);


    public UploaderRunnable(IChecker iChecker, AbstractServiceCoordinator abstractServiceCoordinator) {
        this.abstractServiceCoordinator = abstractServiceCoordinator;
        this.iChecker = iChecker;
    }

    @Override
    public void run() {
        try {
            LOGGER.info(Thread.currentThread().getName() + " start to upload rules.");
            abstractServiceCoordinator.coordinate();
            iChecker.abnormalDataRecordAlarm();
        } catch (Exception e) {
            LOGGER.error("Failed to upload rules, caused by: {}", e.getMessage(), e);
        } finally {
            abstractServiceCoordinator.release();
        }
    }

}
