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

package com.webank.wedatasphere.qualitis.zk;

import com.webank.wedatasphere.qualitis.config.ZkConfig;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author howeye
 */
@Component
public class ZookeeperCuratorManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperCuratorManager.class);

    @Autowired
    private ZkConfig zkConfig;

    public CuratorFramework createClient() {
        Integer baseSleepTimeMs = zkConfig.getBaseSleepTimeMs();
        Integer maxRetries = zkConfig.getMaxRetries();
        Integer sessionTimeOutMs = zkConfig.getSessionTimeOutMs();
        Integer connectionTimeOutMs = zkConfig.getConnectionTimeOutMs();

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkConfig.getZkAddress(), sessionTimeOutMs, connectionTimeOutMs, retryPolicy);

        LOGGER.info("Start to create zookeeper connection., url: {}", zkConfig.getZkAddress());
        client.start();
        LOGGER.info("Succeed to create zookeeper connection. url: {}", zkConfig.getZkAddress());
        return client;
    }

    public void closeClient(CuratorFramework client) {
        CloseableUtils.closeQuietly(client);
    }

}
