package com.webank.wedatasphere.qualitis.concurrent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.webank.wedatasphere.qualitis.pool.GeneralThreadPool;
import com.webank.wedatasphere.qualitis.pool.MetricMonitorThreadPool;
import com.webank.wedatasphere.qualitis.pool.handler.RecordRejectedExecutionHandler;
import com.webank.wedatasphere.qualitis.pool.manager.AbstractThreadPoolManager;
import com.webank.wedatasphere.qualitis.constants.ThreadPoolConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author v_minminghe@webank.com
 * @date 2024-10-18 14:15
 * @description
 */
public class QualitisThreadPoolManager extends AbstractThreadPoolManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(QualitisThreadPoolManager.class);

    public QualitisThreadPoolManager() {
        LOGGER.info("Getting ready to initialize thread pools.");
        initThreadPools();
    }

    @Override
    public void initThreadPools() {
        initMonitoredThreadPools();
        initGeneralThreadPools();
    }

    /**
     * Building monitored thread pools that record execution information as metrics to report to the IMS system.
     */
    private void initMonitoredThreadPools() {

        String taskRerunName = ThreadPoolConstant.TASK_RERUN;
        this.buildMonitoredThreadPool(taskRerunName, 50,
                Integer.MAX_VALUE,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                new RecordRejectedExecutionHandler(taskRerunName));

        String dgsmName = ThreadPoolConstant.DGSM;
        this.buildMonitoredThreadPool(dgsmName, 50,
                Integer.MAX_VALUE,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                new RecordRejectedExecutionHandler(dgsmName));

        String ruleExecutionName = ThreadPoolConstant.RULE_EXECUTION;
        this.buildMonitoredThreadPool(ruleExecutionName, 50,
                Integer.MAX_VALUE,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                new RecordRejectedExecutionHandler(ruleExecutionName));

        String dssRuleNodeName = ThreadPoolConstant.DSS_RULE_NODE;
        this.buildMonitoredThreadPool(dssRuleNodeName, 50,
                Integer.MAX_VALUE,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                new RecordRejectedExecutionHandler(dssRuleNodeName));
    }

    /**
     * Building general thread pools that are designed for uniform management.
     */
    private void initGeneralThreadPools() {

        this.buildThreadPool(ThreadPoolConstant.RULE_UPDATE, 50,
                Integer.MAX_VALUE,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                new ThreadPoolExecutor.DiscardPolicy());

        this.buildThreadPool(ThreadPoolConstant.DMS_METADATA_RULE_DATASOURCE_UPDATER, 50,
                Integer.MAX_VALUE,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                new ThreadPoolExecutor.DiscardPolicy());

        this.buildThreadPool(ThreadPoolConstant.OUTER_RULE_EXECUTION, 50,
                Integer.MAX_VALUE,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                new ThreadPoolExecutor.DiscardPolicy());

        this.buildThreadPool(ThreadPoolConstant.RULE_BASH_UPDATE, 100,
                Integer.MAX_VALUE,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                new ThreadPoolExecutor.DiscardPolicy());

        this.buildThreadPool(ThreadPoolConstant.SYNC_METADATA, 1,
                1,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.DiscardPolicy());

        this.buildThreadPool(ThreadPoolConstant.OPERATE_PROJECT_IN_BATCH, 10,
                50,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * A monitored thread pool that is used to monitor execution information of thread pool, such as the handle duration of task
     *
     * @param threadPoolName
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime
     * @param unit
     * @param workQueue
     * @param rejectedExecutionHandler
     * @return
     */
    public GeneralThreadPool buildMonitoredThreadPool(String threadPoolName, int corePoolSize,
                                                      int maximumPoolSize,
                                                      long keepAliveTime,
                                                      TimeUnit unit,
                                                      BlockingQueue<Runnable> workQueue,
                                                      RejectedExecutionHandler rejectedExecutionHandler) {
        ThreadFactory threadFactory = (new ThreadFactoryBuilder()).setNameFormat(threadPoolName + "-%d").setDaemon(false).build();
        GeneralThreadPool generalThreadPool = new MetricMonitorThreadPool(threadPoolName, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        generalThreadPool.setRejectedExecutionHandler(rejectedExecutionHandler);
        super.addThreadPool(generalThreadPool);
        return generalThreadPool;
    }

}
