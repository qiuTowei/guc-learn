package com.doublev.lock8;

import java.util.concurrent.TimeUnit;

/**
 * 8锁，就是关于锁的8个问题
 * 1. 标准情况下，两个线程先输出打电话还是发短信？ 先发短信再打电话
 * 2. SendSms 延迟四秒，两个线程先输出什么结果？先发短信再打电话
 */
public class Test1 {
    public static void main(String[] args) {
        Phone1 phone = new Phone1();
        // 锁的存在，导致先打电话再发短信
        new Thread(() -> {
            phone.SendSms();
        },"A").start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            phone.call();
        },"B").start();
    }
}

class Phone1 {

    // synchronized 锁的对象是方法的调用者
    // 两个方法用的是同一个锁，谁先拿到谁执行

    // 打电话
    public synchronized  void call() {
        System.out.println("call");
    }

    // 发短信
    public synchronized void SendSms() {
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("sendSms");
    }
}
