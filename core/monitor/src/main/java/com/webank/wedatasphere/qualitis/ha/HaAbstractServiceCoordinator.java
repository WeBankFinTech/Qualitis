package com.webank.wedatasphere.qualitis.ha;

import com.webank.wedatasphere.qualitis.config.ThreadPoolConfig;
import com.webank.wedatasphere.qualitis.config.ZkConfig;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author howeye
 */
@Component
@ConditionalOnProperty(name = "ha.enable", havingValue = "true")
public class HaAbstractServiceCoordinator extends AbstractServiceCoordinator {

    private static final Logger LOGGER = LoggerFactory.getLogger(HaAbstractServiceCoordinator.class);

    @Autowired
    private ZkConfig zkConfig;

    @Autowired
    private ThreadPoolConfig threadPoolConfig;

    private String lockPath;
    private InterProcessLock lock;
    private boolean lockFlag = false;

    @Override
    public void init() {
        LOGGER.info("Start to create zookeeper client");
        CuratorFramework client = createClient();
        lockPath = threadPoolConfig.getLockZkPath();
        lock = new InterProcessMutex(client, lockPath);
    }

    @Override
    public void coordinate() {
        try {
            LOGGER.info("Trying to acquire lock of zk, lock_path: {}, lock: {}", lockPath, lock.hashCode());
            lock.acquire();
            lockFlag = true;
            LOGGER.info("Succeed to acquire lock of zk, lock_path: {}, lock: {}", lockPath, lock.hashCode());
        } catch (Exception e) {
            LOGGER.error("Failed to get lock of zk, lock_path: {}, caused by: {}", lockPath, e.getMessage(), e);
        }
    }

    @Override
    public void release() {
        if (lockFlag) {
            lockFlag = false;
            try {
                lock.release();
            } catch (Exception e) {
                LOGGER.error("Failed to release lock of zookeeper", e);
            }
        }
    }

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
