package com.doublev.lock8;

import java.util.concurrent.TimeUnit;

/**
 * 8锁，就是关于锁的8个问题
 * 7. 一个静态同步方法，一个普通同步方法，一个对象，先打印哪个？两个不同的锁，所以先输出打电话
 * 8. 一个静态同步方法，一个普通同步方法，两个对象，先打印哪个？先打电话
 */
public class Test4 {
    public static void main(String[] args) {
        // 两个对象，两个调用者，两把锁
        Phone4 phone1 = new Phone4();
        Phone4 phone2 = new Phone4();

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

class Phone4 {
    // synchronized 锁的对象是方法的调用者
    // 静态同步方法 类一加载就存在 class模板（全局唯一）

    // 发短信 静态同步方法  锁的是class类模板
    public static synchronized void SendSms() {
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("sendSms");
    }
    // 打电话 普通同步方法 锁的调用者
    public  synchronized  void call() {
        System.out.println("call");
    }

    // 没有锁，不是同步方法，不受锁的影响
    //public void hello() {
    //    System.out.println("hello");
    //}
}
