package com.webank.wedatasphere.qualitis.bean;

import com.webank.wedatasphere.qualitis.pool.GeneralThreadPool;
import com.webank.wedatasphere.qualitis.pool.manager.AbstractThreadPoolManager;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author v_minminghe@webank.com
 * @date 2024-10-18 15:47
 * @description
 */
@Component
public class ThreadPoolShutdownManager implements DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolShutdownManager.class);

    @Autowired
    private AbstractThreadPoolManager threadPoolManager;

    @Value("${threadpool.shutdown-wait-seconds:30}")
    private int shutdownWaitSeconds;

    @Override
    public void destroy() {
        LOGGER.info("Getting ready to shutdown thread pools.");
        Collection<GeneralThreadPool> threadPools = threadPoolManager.getAllThreadPool();
        if (CollectionUtils.isEmpty(threadPools)) {
            LOGGER.warn("No thread pool to be managed.");
            return;
        }

        threadPoolManager.shutdown();
//        Calculate global end time for all thread pool
        long endTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(shutdownWaitSeconds);

        for (GeneralThreadPool generalThreadPool : threadPools) {
//            Calculate left time for each thread pool
            long timeLeft = endTime - System.currentTimeMillis();
            if (timeLeft <= 0) {
                LOGGER.warn("Total wait time exceeded before shutdown of all thread pools.");
                break;
            }

            try {
                if (!generalThreadPool.awaitTermination(timeLeft, TimeUnit.MILLISECONDS)) {
                    LOGGER.warn("Thread pool did not terminate within the specified wait time.");
                    List<Runnable> droppedTasks = generalThreadPool.shutdownNow();
                    LOGGER.warn("Forcibly shut down pool. {} tasks were not completed.", droppedTasks.size());
                }
            } catch (InterruptedException ex) {
                LOGGER.error("Thread pool shutdown interrupted.", ex);
                generalThreadPool.shutdownNow();
                Thread.currentThread().interrupt();
                break;
            }
        }

        LOGGER.info("All thread pools have shutdown.");
    }
}
