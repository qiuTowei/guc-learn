package com.doublev.jmm;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * volatile
 * 不保证原子行
 */
public class VolatileDemo02 {

    private volatile static AtomicInteger num = new AtomicInteger();
    public static void add() {
        //num++;
        // AtomicInteger +1操作  CAS
        num.getAndIncrement();
}

    public static void main(String[] args) {
        // 理论上结果为20000
        // 实际结果为小于20000的随机值
        // volatile 不保证原子性
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    add();
                }
            }).start();
        }
        while (Thread.activeCount() > 2) {
            // main gc 线程
            Thread.yield();
        }
        System.out.println((Thread.currentThread().getName() + num));
    }
}
