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

package com.webank.wedatasphere.qualitis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author howeye
 */
@Configuration
public class ThreadPoolConfig {
    @Value("${timer.thread.size}")
    private Integer size;
    @Value("${timer.check.period}")
    private Integer period;
    @Value("${timer.check.update_job_size}")
    private Integer updateJobSize;
    @Value("${timer.lock.zk.path}")
    private String lockZkPath;

    public ThreadPoolConfig() {
        // Default Constructor
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public String getLockZkPath() {
        return lockZkPath;
    }

    public void setLockZkPath(String lockZkPath) {
        this.lockZkPath = lockZkPath;
    }

    public Integer getUpdateJobSize() {
        return updateJobSize;
    }

    public void setUpdateJobSize(Integer updateJobSize) {
        this.updateJobSize = updateJobSize;
    }
}
