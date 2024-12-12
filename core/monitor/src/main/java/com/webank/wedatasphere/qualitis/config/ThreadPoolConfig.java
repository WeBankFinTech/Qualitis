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
    @Value("${timer.thread.async_execution_core_size}")
    private Integer executionCorePoolSize;
    @Value("${timer.thread.async_execution_max_size}")
    private Integer executionMaxPoolSize;
    @Value("${timer.check.update_core_size}")
    private Integer updateCorePoolSize;
    @Value("${timer.check.update_max_size}")
    private Integer updateMaxPoolSize;
    @Value("${timer.check.period}")
    private Integer period;
    @Value("${timer.check.pending_period}")
    private Integer pendingPeriod;
    @Value("${timer.check.update_job_size}")
    private Integer updateJobSize;
    @Value("${timer.lock.zk.path}")
    private String lockZkPath;
    @Value("${timer.abnormal_data_record_alarm.cron}")
    private String abnormalDataRedordAlarmCron;
    @Value("${timer.abnormal_data_record_alarm.cron_enable}")
    private Boolean abnormalDataRedordAlarmCronEnable;

    public ThreadPoolConfig() {
        // Default Constructor
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getExecutionCorePoolSize() {
        return executionCorePoolSize;
    }

    public void setExecutionCorePoolSize(Integer executionCorePoolSize) {
        this.executionCorePoolSize = executionCorePoolSize;
    }

    public Integer getUpdateCorePoolSize() {
        return updateCorePoolSize;
    }

    public void setUpdateCorePoolSize(Integer updateCorePoolSize) {
        this.updateCorePoolSize = updateCorePoolSize;
    }

    public Integer getUpdateMaxPoolSize() {
        return updateMaxPoolSize;
    }

    public void setUpdateMaxPoolSize(Integer updateMaxPoolSize) {
        this.updateMaxPoolSize = updateMaxPoolSize;
    }

    public Integer getExecutionMaxPoolSize() {
        return executionMaxPoolSize;
    }

    public void setExecutionMaxPoolSize(Integer executionMaxPoolSize) {
        this.executionMaxPoolSize = executionMaxPoolSize;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Integer getPendingPeriod() {
        return pendingPeriod;
    }

    public void setPendingPeriod(Integer pendingPeriod) {
        this.pendingPeriod = pendingPeriod;
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

    public String getAbnormalDataRedordAlarmCron() {
        return abnormalDataRedordAlarmCron;
    }

    public void setAbnormalDataRedordAlarmCron(String abnormalDataRedordAlarmCron) {
        this.abnormalDataRedordAlarmCron = abnormalDataRedordAlarmCron;
    }

    public Boolean getAbnormalDataRedordAlarmCronEnable() {
        return abnormalDataRedordAlarmCronEnable;
    }

    public void setAbnormalDataRedordAlarmCronEnable(Boolean abnormalDataRedordAlarmCronEnable) {
        this.abnormalDataRedordAlarmCronEnable = abnormalDataRedordAlarmCronEnable;
    }
}
