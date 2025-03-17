package com.webank.wedatasphere.qualitis.pool.manager;

public interface ThreadPoolManager {

    // Try to shutdown thread pool manager
    void shutdown();

    // Shutdown thread pool manager in blocking mode
    void shutdownBlock() throws InterruptedException;

    // Statistics for thread pool manager
    void stat();

    // Check if thread pool manager is terminated
    boolean isTerminated();

}
