package com.sevtinge.hyperceiler.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolManager {
    private static final int NUM_THREADS = 5; // 定义线程池中线程的数量
    private static ExecutorService executor;

    // 获取线程池实例
    public static synchronized ExecutorService getInstance() {
        if (executor == null) {
            // 创建一个具有固定数量线程的线程池
            executor = Executors.newFixedThreadPool(NUM_THREADS);
        }
        if (executor.isShutdown()) {
            // 如果已经关机则重新创建
            executor = Executors.newFixedThreadPool(NUM_THREADS);
        }
        return executor;
    }

    // 关闭线程池
    public static synchronized void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            executor = null;
        }
    }
}
