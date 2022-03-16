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
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.bean.JobChecker;

/**
 * @author howeye
 */
public interface IChecker {
    /**
     * Check status of task
     * @param jobChecker
     * @throws ClusterInfoNotConfigException
     */
    void checkTaskStatus(JobChecker jobChecker);

    /**
     * Check if last job
     * @param applicationInDb
     * @param finish
     * @param isPass
     * @param isNotExist
     */
    void checkIfLastJob(Application applicationInDb, boolean finish, boolean isPass, boolean isNotExist);
}
