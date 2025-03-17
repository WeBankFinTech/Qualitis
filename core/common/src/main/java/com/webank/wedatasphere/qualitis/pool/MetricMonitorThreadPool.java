package com.webank.wedatasphere.qualitis.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author v_minminghe@webank.com
 * @date 2024-10-22 15:48 A class that is used to monitor the resources of thread pool, such as the duration from submit a task to its completed(contain the waiting duration)
 * @description
 */
public class MetricMonitorThreadPool extends GeneralThreadPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricMonitorThreadPool.class);

    public MetricMonitorThreadPool(String name, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(name, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    @Override
    public void execute(Runnable task) {
        LOGGER.info("-----A task in the thread pool [{}] has submitted----", super.name);
        MetricMonitorRunnable runnableWrapper = new MetricMonitorRunnable(super.name, task);
        super.execute(runnableWrapper);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        MetricMonitorRunnable metricMonitorRunnable = (MetricMonitorRunnable) r;
        metricMonitorRunnable.countTaskDuration();
        LOGGER.info("-----A task in the thread pool [{}] has completed----", super.name);
    }

    public class MetricMonitorRunnable implements Runnable {

        protected String threadPoolName;
        private long start = System.currentTimeMillis();
        private Runnable runnable;

        public MetricMonitorRunnable(String threadPoolName, Runnable runnable) {
            this.threadPoolName = threadPoolName;
            this.runnable = runnable;
        }

        /**
         * By default, the class counts the duration from submitting a task to its completion.
         */
        protected void countTaskDuration() {
            long end = System.currentTimeMillis();
        }

        @Override
        public void run() {
            this.runnable.run();
        }
    }

}
