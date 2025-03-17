package com.webank.wedatasphere.qualitis.pool.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author v_minminghe@webank.com
 * @date 2024-10-21 11:25
 * @description A handler that is used to record the count of rejected task, and not throw exception
 */
public class RecordRejectedExecutionHandler implements RejectedExecutionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordRejectedExecutionHandler.class);


    private String threadPoolName;

    public RecordRejectedExecutionHandler(String threadPoolName) {
        this.threadPoolName = threadPoolName;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        LOGGER.warn("Thread [{}] was rejected.", threadPoolName);
    }

}
