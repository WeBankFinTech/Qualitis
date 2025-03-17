package com.webank.wedatasphere.qualitis.config;

//import cn.webank.bdp.wedatasphere.biz.concurrent.exception.ThreadPoolNotFoundException;
//import cn.webank.bdp.wedatasphere.biz.concurrent.pool.GeneralThreadPool;
//import cn.webank.bdp.wedatasphere.biz.utils.ThreadPoolMetricStatisticsUtils;
//import cn.webank.bdp.wedatasphere.biz.concurrent.pool.manager.AbstractThreadPoolManager;
//import com.google.common.collect.Lists;
//import com.webank.wedatasphere.qualitis.constants.ThreadPoolConstant;
//import io.micrometer.core.instrument.Gauge;
//import io.micrometer.core.instrument.MeterRegistry;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;

//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2024-10-16 16:19
 * @description 线程池上报指标名称格式：threadpool.${线程池名称简写}.${指标项}
 */
//@Component
public class ThreadPoolMonitorManager {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolMonitorManager.class);
//
//    private final String METRIC_NAME_TEMPLATE = "threadpool.%s.%s";
//
//
//    public ThreadPoolMonitorManager(MeterRegistry meterRegistry, AbstractThreadPoolManager threadPoolManager) throws ThreadPoolNotFoundException {
//
//        List<GeneralThreadPool> monitoredThreadPoolList = Lists.newArrayListWithExpectedSize(6);
//        monitoredThreadPoolList.add(threadPoolManager.getThreadPool(ThreadPoolConstant.TASK_RERUN));
//        monitoredThreadPoolList.add(threadPoolManager.getThreadPool(ThreadPoolConstant.DGSM));
//        monitoredThreadPoolList.add(threadPoolManager.getThreadPool(ThreadPoolConstant.RULE_EXECUTION));
//        monitoredThreadPoolList.add(threadPoolManager.getThreadPool(ThreadPoolConstant.DSS_RULE_NODE));
//
//        registryMetrics(meterRegistry, monitoredThreadPoolList);
//    }
//
//    /**
//     * registry thread pools to MeterRegistry
//     * @param monitoredThreadPoolList
//     */
//    private void registryMetrics(MeterRegistry meterRegistry, List<GeneralThreadPool> monitoredThreadPoolList) {
//        for (GeneralThreadPool generalThreadPool : monitoredThreadPoolList) {
//            String activeCountMetric = String.format(METRIC_NAME_TEMPLATE, generalThreadPool.getName(), "activeCount");
//            Gauge.builder(activeCountMetric, this, t -> this.getActiveCount(generalThreadPool)).register(meterRegistry);
//
//            String completedTaskCountMetric = String.format(METRIC_NAME_TEMPLATE, generalThreadPool.getName(), "completedTaskCount");
//            Gauge.builder(completedTaskCountMetric, this, t -> this.getCompletedTaskCount(generalThreadPool)).register(meterRegistry);
//
//            String waitingTaskCountMetric = String.format(METRIC_NAME_TEMPLATE, generalThreadPool.getName(), "waitingTaskCount");
//            Gauge.builder(waitingTaskCountMetric, this, t -> this.getWaitingTaskCount(generalThreadPool)).register(meterRegistry);
//
//            String poolSizeMetric = String.format(METRIC_NAME_TEMPLATE, generalThreadPool.getName(), "poolSize");
//            Gauge.builder(poolSizeMetric, this, t -> this.getPoolSize(generalThreadPool)).register(meterRegistry);
//
//            String rejectedTaskCountMetric = String.format(METRIC_NAME_TEMPLATE, generalThreadPool.getName(), "rejectedTaskCount");
//            Gauge.builder(rejectedTaskCountMetric, this, t -> this.getRejectedTaskCount(generalThreadPool)).register(meterRegistry);
//
//            String avgHandleDurationMetric = String.format(METRIC_NAME_TEMPLATE, generalThreadPool.getName(), "avgHandleDuration");
//            Gauge.builder(avgHandleDurationMetric, this, t -> this.getAvgHandleDuration(generalThreadPool)).register(meterRegistry);
//        }
//    }
//
//    /**
//     * 获取激活线程数
//     * @param generalThreadPool
//     * @return
//     */
//    public long getActiveCount(GeneralThreadPool generalThreadPool) {
//        long value = generalThreadPool.getActiveCount();
//        LOGGER.info("Collecting thread pool metric. metric name: {}; metric value: {}.", generalThreadPool.getName() + ".activeCount", value);
//        return value;
//    }
//
//    /**
//     * 获取自应用启动以来的已完成数
//     *
//     * @return
//     */
//    public long getCompletedTaskCount(GeneralThreadPool generalThreadPool) {
//        long value = generalThreadPool.getCompletedTaskCount();
//        LOGGER.info("Collecting thread pool metric. metric name: {}; metric value: {}.", generalThreadPool.getName() + ".completedTaskCount", value);
//        return value;
//    }
//
//    /**
//     * 获取等待任务数
//     * @param generalThreadPool
//     * @return
//     */
//    public long getWaitingTaskCount(GeneralThreadPool generalThreadPool) {
//        long value = generalThreadPool.getQueue().size();
//        LOGGER.info("Collecting thread pool metric. metric name: {}; metric value: {}.", generalThreadPool.getName() + ".waitingTaskCount", value);
//        return value;
//    }
//
//    /**
//     * 获取线程池数
//     * @param generalThreadPool
//     * @return
//     */
//    public long getPoolSize(GeneralThreadPool generalThreadPool) {
//        long value = generalThreadPool.getPoolSize();
//        LOGGER.info("Collecting thread pool metric. metric name: {}; metric value: {}.", generalThreadPool.getName() + ".poolSize", value);
//        return value;
//    }
//
//    /**
//     * @return  获取自应用启动以来的已拒绝数
//     */
//    public long getRejectedTaskCount(GeneralThreadPool generalThreadPool) {
//        long value = ThreadPoolMetricStatisticsUtils.getTotalRejectedTaskCount(generalThreadPool.getName());
//        LOGGER.info("Collecting thread pool metric. metric name: {}; metric value: {}.", generalThreadPool.getName() + ".rejectedTaskCount", value);
//        return value;
//    }
//
//    /**
//     * 从提交任务到执行完成，期间包含了位于等待队列的时间
//     * @return 单位: ms
//     */
//    public long getAvgHandleDuration(GeneralThreadPool generalThreadPool) {
//        long value = 0;
//        long totalHandledTime = ThreadPoolMetricStatisticsUtils.getTotalTaskDuration(generalThreadPool.getName());
//        long completedTaskCount = generalThreadPool.getCompletedTaskCount();
//        if (completedTaskCount > 0) {
//            BigDecimal avgHandleDuration = new BigDecimal(totalHandledTime).divide(new BigDecimal(completedTaskCount), 0, RoundingMode.HALF_UP);
//            value = avgHandleDuration.longValue();
//        }
//        LOGGER.info("Collecting thread pool metric. metric name: {}; metric value: {}.", generalThreadPool.getName() + ".avgHandleDuration", value);
//        return value;
//    }

}
