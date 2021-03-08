package com.doublev.forkjoin;

import java.util.concurrent.RecursiveTask;

/**
 * 大数据量的求和任务
 * 如何使用 ForkJoin
 * ForkJoinPool
 * 计算任务 ：forkJoin.execute(ForkJoinTask<?> task)
 *
 */
public class ForkJoinDemo extends RecursiveTask<Long> {
    private Long start;
    private Long end;

    // 临界值
    private Long temp = 10000L;

    public ForkJoinDemo(Long start, Long end) {
        this.start = start;
        this.end = end;
    }

    /**
     * 实现的计算方法
     * @return Long
     */
    @Override
    protected Long compute() {
        if ((end-start) < temp) {
            Long sum = 0L;
            for (Long i = start; i < end; i++) {
                sum += i;
            }
            return sum;
        }else {
            // 使用ForkJoin
            // 求中间值
            Long middle = (start + end) / 2;
            // 拆分任务
            ForkJoinDemo task1 = new ForkJoinDemo(start, middle);
            ForkJoinDemo task2 = new ForkJoinDemo(middle + 1, end);
            // 任务压入线程队列
            task1.fork();
            task2.fork();
            // 获取结果
            return task1.join() + task2.join();
        }
    }
}
