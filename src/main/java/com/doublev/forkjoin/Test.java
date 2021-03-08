package com.doublev.forkjoin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.LongStream;

/**
 * 测试方法
 */
public class Test {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        long startTime = System.currentTimeMillis();
        /**
         * 结果 => 499999999500000000
         * 一共耗时 = 8755
         *
         */
        //Long sum = test1();

        /**
         * 结果 => 499934463999828390
         * 一共耗时 = 5227
         */
        //Long sum = test2();
        /**
         *结果 => 499999999500000000
         * 一共耗时 = 238
         */
        Long sum = test3();
        System.out.println("结果 => " + sum);
        System.out.println("一共耗时 = " + (System.currentTimeMillis() - startTime));

    }

    /**
     * 普通方法
     * @return long
     */
    public static Long test1() {
        Long sum = 0L;
        for (Long i = 1L; i <= 10_0000_0000; i++) {
            sum += i;
        }
        return sum;
    }

    /**
     * ForkJoin 方法
     * @return long
     */
    public static Long test2() throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinTask<Long> task = new ForkJoinDemo(0L, 10_0000_0000L);
        // 提交
        ForkJoinTask<Long> submit = forkJoinPool.submit(task);
        return submit.get();
    }

    /**
     * stream 并行流
     * @return long
     */
    public static Long test3() {
        return LongStream.range(0L, 10_0000_0000L).parallel().reduce(0, Long::sum);
    }
}
