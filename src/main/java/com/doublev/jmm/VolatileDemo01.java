package com.doublev.jmm;

import java.util.concurrent.TimeUnit;

/**
 * volatile
 * 1、保证可见性
 * 2、不保证原子性
 * 3、禁止指令重排
 *
 */
public class VolatileDemo01 {
    // 不加volatile,程序死循环
    // 加了volatile,保证了可见性
    private static volatile Integer num = 0;

    public static void main(String[] args) {
        new Thread(() -> {
            while (num == 0){

            }
        }).start();
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        num = 1;
        System.out.println(num);
    }
}
