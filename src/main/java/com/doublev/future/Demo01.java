package com.doublev.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 *
 * 异步调用：Ajax
 * 异步执行
 * 成功回调
 * 失败回调
 */
public class Demo01 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 发起一个请求
        // 没有返回值的异步回调
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println((Thread.currentThread().getName() + " runAsync => void"));
        });
        System.out.println(("1111"));
        completableFuture.get();

        // 有返回值的异步回调
        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println((Thread.currentThread().getName() + " supplyAsync => Integer"));
            int i = 10/0;
            return 1024;
        });
        System.out.println(integerCompletableFuture.whenComplete((t, u) -> {
            // 正确的返回结果
            System.out.println(t);
            // 错误信息
            System.out.println(u);
        }).exceptionally((e) -> {
            // 错误
            System.out.println(e.getMessage());
            // 错误的返回结果
            return 404;
        }).get());


    }


}
