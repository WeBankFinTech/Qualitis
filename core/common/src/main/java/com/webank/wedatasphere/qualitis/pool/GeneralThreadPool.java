package com.webank.wedatasphere.qualitis.pool;

import java.util.concurrent.*;

public class GeneralThreadPool extends ThreadPoolExecutor {

    protected String name;

    public GeneralThreadPool(String name,
                             int corePoolSize,
                             int maximumPoolSize,
                             long keepAliveTime,
                             TimeUnit unit,
                             BlockingQueue<Runnable> workQueue,
                             ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, new RejectHandler());
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void execute(Runnable task) {
        super.execute(task);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(task);
    }

    private static class RejectHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            throw new RejectedExecutionException();
        }

    }

}
