package com.webank.wedatasphere.qualitis.pool.manager;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.webank.wedatasphere.qualitis.pool.GeneralThreadPool;
import com.webank.wedatasphere.qualitis.pool.exception.ThreadPoolNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.*;

public abstract class AbstractThreadPoolManager implements ThreadPoolManager {

    private static Logger LOGGER = LoggerFactory.getLogger(AbstractThreadPoolManager.class);

    private String name;

    private Map<String, GeneralThreadPool> threadPools = new ConcurrentHashMap<>();

    public abstract void initThreadPools();

    @PreDestroy
    public void destroy() throws InterruptedException {
        shutdownBlock();
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected GeneralThreadPool buildThreadPool(String threadPoolName,
                                                int poolCoreSize,
                                                int poolMaxSize,
                                                String threadNameFormat) {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(threadNameFormat).setDaemon(false).build();
        GeneralThreadPool threadPool = new GeneralThreadPool(threadPoolName, poolCoreSize, poolMaxSize,
                1000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), threadFactory);
        addThreadPool(threadPool);
        return threadPool;
    }

    public GeneralThreadPool buildThreadPool(String threadPoolName, int corePoolSize,
                                             int maximumPoolSize,
                                             long keepAliveTime,
                                             TimeUnit unit,
                                             BlockingQueue<Runnable> workQueue,
                                             RejectedExecutionHandler rejectedExecutionHandler) {
        ThreadFactory threadFactory = (new ThreadFactoryBuilder()).setNameFormat(threadPoolName + "-%d").setDaemon(false).build();
        GeneralThreadPool generalThreadPool = new GeneralThreadPool(threadPoolName, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        generalThreadPool.setRejectedExecutionHandler(rejectedExecutionHandler);
        addThreadPool(generalThreadPool);
        return generalThreadPool;
    }

    protected void addThreadPool(GeneralThreadPool threadPool) {
        LOGGER.info("Adding thread pool [{}]. Core size [{}]. Max size [{}].", threadPool.getName(), threadPool.getCorePoolSize(), threadPool.getMaximumPoolSize());
        threadPools.put(threadPool.getName(), threadPool);
    }

    public GeneralThreadPool getThreadPool(String name) throws ThreadPoolNotFoundException {
        LOGGER.info("Getting thread pool by [{}]", name);
        if (!threadPools.containsKey(name)) {
            throw new ThreadPoolNotFoundException("Unable to found the thread pool: " + name);
        }
        return threadPools.get(name);
    }

    public Collection<GeneralThreadPool> getAllThreadPool() {
        LOGGER.info("Getting all thread pool.");
        return threadPools.values();
    }

    @Override
    public void shutdown() {
        if (threadPools.isEmpty()) {
            LOGGER.warn("Nothing to shutdown for [{}] thread pool manager.", name);
        }

        LOGGER.info("Shutdown thread pool manager [{}]...", name);
        for (Map.Entry<String, GeneralThreadPool> entry : threadPools.entrySet()) {
            entry.getValue().shutdown();
        }
    }

    @Override
    public void shutdownBlock() throws InterruptedException {
        shutdown();
        while (!isTerminated()) {
            Thread.sleep(1000L * 60);
        }
    }

    @Override
    public void stat() {
        if (threadPools.isEmpty()) {
            LOGGER.warn("Nothing to stat for [{}] thread pool manager.", name);
        }

        LOGGER.info("=================== Stat for [{}] thread pool manager ===================", name);
        for (Map.Entry<String, GeneralThreadPool> entry : threadPools.entrySet()) {
            LOGGER.info("Thread pool [{}] stat - isTerminated: {}, activeCount: {}", entry.getValue().getName(), entry.getValue().isTerminated(), entry.getValue().getActiveCount());
        }
    }

    @Override
    public boolean isTerminated() {
        if (threadPools.isEmpty()) {
            return true;
        }

        for (Map.Entry<String, GeneralThreadPool> entry : threadPools.entrySet()) {
            if (!entry.getValue().isTerminated()) {
                stat();
                return false;
            }
        }

        LOGGER.info("Thread pool manager [{}] has been terminated.", name);
        return true;
    }

}
