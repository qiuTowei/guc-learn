package com.doublev.pool;

import java.util.concurrent.*;

/**
 *
 * 模拟银行办理业务
 */
public class Demo02 {
    public static void main(String[] args) {
        // 自定义线程池 熟记七大参数的含义
        ExecutorService executorService = new ThreadPoolExecutor(
                // 核心线程池大小
                2,
                // 最大线程池大小
                5,
                // 超时大小
                3,
                // 超时单位
                TimeUnit.SECONDS,
                // 阻塞队列
                new LinkedBlockingQueue(3),
                // 线程工厂
                Executors.defaultThreadFactory(),
                //
                /**
                 * 拒绝策略
                 * 1、AbortPolicy 超过最大承载，还有任务，不处理这个任务抛出异常 RejectedExecutionException
                 * 2、CallerRunsPolicy 超过最大承载，还有的任务，哪来的返回哪里处理
                 * 3、DiscardOldestPolicy 超过最大承载，还有的任务，尝试去跟最早的竞争，如果失败依旧丢失任务，不会抛出异常
                 * 4、DiscardPolicy 超过最大承载，还有的任务，不会抛出异常，丢失任务
                 */
                new ThreadPoolExecutor.CallerRunsPolicy());

        /**
         * 最大线程数如何定义
         * 1、CPU密集型：几核cpu设置几个，可以保持cpu的效率最高
         * 2、IO密集型：判断程序中十分消耗IO的线程数量（如果有15个大型任务，设置为15）
         */
        // 获取CPU核数
        int i1 = Runtime.getRuntime().availableProcessors();

        try {
            // 最大承载：Deque + max = 8
            // 超出最大承载，会被拒绝策略处理
            for (int i = 0; i < 9; i++) {
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
