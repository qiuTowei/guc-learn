package com.doublev.lock8;

import java.util.concurrent.TimeUnit;

/**
 * 8锁，就是关于锁的8个问题
 * 3. 增加一个普通方法hello，先执行发短信还是hello？先执行hello，非同步方法不受锁影响
 * 4. 两个对象，两个同步方法，先发短信还是打电话？先打电话后发短信
 */
public class Test2 {
    public static void main(String[] args) {
        // 两个对象，两个调用者，两把锁
        Phone2 phone1 = new Phone2();
        Phone2 phone2 = new Phone2();

        // 锁的存在，导致先打电话再发短信
        new Thread(() -> {
            phone1.SendSms();
        },"A").start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            phone2.call();
        },"B").start();
    }
}

class Phone2 {

    // synchronized 锁的对象是方法的调用者
    // 两个方法用的是同一个锁，谁先拿到谁执行

    // 发短信
    public synchronized void SendSms() {
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("sendSms");
    }
    // 打电话
    public synchronized  void call() {
        System.out.println("call");
    }
    // 没有锁，不是同步方法，不受锁的影响
    public void hello() {
        System.out.println("hello");
    }
}
