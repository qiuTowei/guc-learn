package com.doublev.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * Executors 工具类，3大方法
 * 使用了线程池之后，使用线程池来新线程
 */
public class Demo01 {
    public static void main(String[] args) {
        // 单个线程
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        // 固定线程
        //ExecutorService executorService = Executors.newFixedThreadPool(5);
        // 可伸缩，视情况而定
        //ExecutorService executorService = Executors.newCachedThreadPool();

        try {
            for (int i = 0; i < 100; i++) {
                executorService.execute(() -> {
                    System.out.println(Thread.currentThread().getName() + " ok ");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 线程池用完需要手动关闭
            executorService.shutdown();
        }

    }
}
