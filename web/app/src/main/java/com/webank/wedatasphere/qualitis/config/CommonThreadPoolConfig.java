package com.webank.wedatasphere.qualitis.config;

import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author v_minminghe@webank.com
 * @date 2023-03-15 16:42
 * @description
 */
@Configuration
public class CommonThreadPoolConfig {

    /**
     * To update metadata from DMS to RuleDataSource
     * @return
     */
    @Bean(name = "dmsWorkerThreadPool")
    public ThreadPoolExecutor workerThreadPool() {
        return new ThreadPoolExecutor(50,
                Integer.MAX_VALUE,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                runnable -> new Thread(runnable, "MetadataOnRuleDataSourceUpdater-Worker-Thread-" + UuidGenerator.generate()),
                new ThreadPoolExecutor.DiscardPolicy());
    }

    /**
     * To Rule Execution
     * @return
     */
    @Bean(name = "ruleExecutionThreadPool")
    public ThreadPoolExecutor ruleExecutionThreadPool() {
        return new ThreadPoolExecutor(50,
                Integer.MAX_VALUE,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000),
                runnable -> new Thread(runnable, "Rule-Execution-Thread-" + UuidGenerator.generate()),
                new ThreadPoolExecutor.DiscardPolicy());
    }


}
