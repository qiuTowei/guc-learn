package com.doublev.lock8;

import java.util.concurrent.TimeUnit;

/**
 * 8锁，就是关于锁的8个问题
 * 1. 标准情况下，先输出打电话还是发短信
 */
public class Test1 {
    public static void main(String[] args) {
        Phone phone = new Phone();
        // 锁的存在，导致先打电话再发短信
        new Thread(() -> {
            phone.call();
        },"A").start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            phone.SendSms();
        },"B").start();
    }
}

class Phone {

    // synchronized 锁的对象是方法的调用者
    // 两个方法用的是同一个锁，谁先拿到谁执行

    // 打电话
    public synchronized  void call() {
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("call");
    }

    // 发短信
    public synchronized void SendSms() {
        System.out.println("sendSms");
    }
}
