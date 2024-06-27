package com.webank.wedatasphere.qualitis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author v_gaojiedeng@webank.com
 */
@Configuration
@EnableAsync
public class ThreadPoolTaskConfig {
    @Value("${report.cron_enable}")
    private Boolean reportCronEnable;
    @Value("${report.daily.core_pool_size}")
    private int dailyCorePoolSize;
    @Value("${report.daily.max_pool_size}")
    private int dailyMaxPoolSize;
    @Value("${report.daily.queue_capacity}")
    private int dailyQueueCapacity;

    @Value("${report.weekly.core_pool_size}")
    private int weeklyCorePoolSize;
    @Value("${report.weekly.max_pool_size}")
    private int weeklyMaxPoolSize;
    @Value("${report.weekly.queue_capacity}")
    private int weeklyQueueCapacity;

    @Bean("threadPoolTaskDailyExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //线程池创建的核心线程数，线程池维护线程的最少数量，即使没有任务需要执行，也会一直存活
        //如果设置allowCoreThreadTimeout=true（默认false）时，核心线程会超时关闭
        executor.setCorePoolSize(dailyCorePoolSize);

        //队列容量（队列只存在任务，不存在线程）
        executor.setQueueCapacity(dailyQueueCapacity);

        //最大线程数；
        //当corePoolSize 以及queueCapacity 满了以后，会在线程中额外创建线程.最大线程数指的是当前存在的最大的线程数。队列中的不属于
        executor.setMaxPoolSize(dailyMaxPoolSize);

        //当线程空闲时间达到keepAliveTime时，线程会退出，直到线程数量=corePoolSize
        //如果allowCoreThreadTimeout=true，则会直到线程数量=0
        executor.setKeepAliveSeconds(60);
        //优雅关闭
        executor.setWaitForTasksToCompleteOnShutdown(true);

        //线程名前缀
        executor.setThreadNamePrefix("Every-Day-Report-Execution-Thread-");

        //当线程数满MaxPoolSize时，可采用以下拒绝策略

        //CallerRunsPolicy()：交由调用方线程运行，比如 main 线程；如果添加到线程池失败，那么主线程会自己去执行该任务，不会等待线程池中的线程去执行
        //AbortPolicy()：该策略是线程池的默认策略，如果线程池队列满了丢掉这个任务并且抛出RejectedExecutionException异常。
        //DiscardPolicy()：如果线程池队列满了，会直接丢掉这个任务并且不会有任何异常
        //DiscardOldestPolicy()：丢弃队列中最老的任务，队列满了，会将最早进入队列的任务删掉腾出空间，再尝试加入队列
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }

    @Bean("threadPoolTaskWeeklyExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskWeeklyExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //线程池创建的核心线程数，线程池维护线程的最少数量，即使没有任务需要执行，也会一直存活
        //如果设置allowCoreThreadTimeout=true（默认false）时，核心线程会超时关闭
        executor.setCorePoolSize(weeklyCorePoolSize);

        //队列容量（队列只存在任务，不存在线程）
        executor.setQueueCapacity(weeklyQueueCapacity);

        //最大线程数；
        //当corePoolSize 以及queueCapacity 满了以后，会在线程中额外创建线程.最大线程数指的是当前存在的最大的线程数。队列中的不属于
        executor.setMaxPoolSize(weeklyMaxPoolSize);

        //当线程空闲时间达到keepAliveTime时，线程会退出，直到线程数量=corePoolSize
        //如果allowCoreThreadTimeout=true，则会直到线程数量=0
        executor.setKeepAliveSeconds(60);
        //优雅关闭
        executor.setWaitForTasksToCompleteOnShutdown(true);

        //线程名前缀
        executor.setThreadNamePrefix("Weekly-Report-Execution-Thread-");

        //当线程数满MaxPoolSize时，可采用以下拒绝策略

        //CallerRunsPolicy()：交由调用方线程运行，比如 main 线程；如果添加到线程池失败，那么主线程会自己去执行该任务，不会等待线程池中的线程去执行
        //AbortPolicy()：该策略是线程池的默认策略，如果线程池队列满了丢掉这个任务并且抛出RejectedExecutionException异常。
        //DiscardPolicy()：如果线程池队列满了，会直接丢掉这个任务并且不会有任何异常
        //DiscardOldestPolicy()：丢弃队列中最老的任务，队列满了，会将最早进入队列的任务删掉腾出空间，再尝试加入队列
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }

    public Boolean getReportCronEnable() {
        return reportCronEnable;
    }

    public void setReportCronEnable(Boolean reportCronEnable) {
        this.reportCronEnable = reportCronEnable;
    }
}
