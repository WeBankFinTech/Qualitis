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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @author howeye
 */
@Configuration
@ConditionalOnProperty(name = "ha.enable", havingValue = "true")
public class ZkConfig {

    @Value("${zk.address}")
    private String zkAddress;

    @Value("${zk.base_sleep_time}")
    private Integer baseSleepTimeMs;

    @Value("${zk.max_retries}")
    private Integer maxRetries;

    @Value("${zk.session_time_out}")
    private Integer sessionTimeOutMs;

    @Value("${zk.connection_time_out}")
    private Integer connectionTimeOutMs;

    @Value("${zk.lock_wait_time}")
    private Integer lockWaitTimeMs;

    public ZkConfig() {
        // Default Constructor
    }

    public String getZkAddress() {
        return zkAddress;
    }

    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public Integer getBaseSleepTimeMs() {
        return baseSleepTimeMs;
    }

    public void setBaseSleepTimeMs(Integer baseSleepTimeMs) {
        this.baseSleepTimeMs = baseSleepTimeMs;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Integer getSessionTimeOutMs() {
        return sessionTimeOutMs;
    }

    public void setSessionTimeOutMs(Integer sessionTimeOutMs) {
        this.sessionTimeOutMs = sessionTimeOutMs;
    }

    public Integer getConnectionTimeOutMs() {
        return connectionTimeOutMs;
    }

    public void setConnectionTimeOutMs(Integer connectionTimeOutMs) {
        this.connectionTimeOutMs = connectionTimeOutMs;
    }

    public Integer getLockWaitTimeMs() {
        return lockWaitTimeMs;
    }

    public void setLockWaitTimeMs(Integer lockWaitTimeMs) {
        this.lockWaitTimeMs = lockWaitTimeMs;
    }
}
