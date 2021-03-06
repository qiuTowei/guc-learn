package com.doublev.lock8;

import java.util.concurrent.TimeUnit;

/**
 * 8锁，就是关于锁的8个问题
 * 5. 增加两个静态同步方法，只有一个对象，先打印那个？先打印发短信，（加锁对象为Phone3.class模板）
 * 6. 增加两个静态同步方法，两个对象，先打印那个？先打印发短信，phone1，phone2都是同一个class类模板
 */
public class Test3 {
    public static void main(String[] args) {
        // 两个对象，两个调用者，两把锁
        Phone3 phone1 = new Phone3();
        Phone3 phone2 = new Phone3();

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

class Phone3 {
    // synchronized 锁的对象是方法的调用者
    // 静态同步方法 类一加载就存在 class模板（全局唯一）

    // 发短信
    public static synchronized void SendSms() {
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("sendSms");
    }
    // 打电话
    public static synchronized  void call() {
        System.out.println("call");
    }

    // 没有锁，不是同步方法，不受锁的影响
    //public void hello() {
    //    System.out.println("hello");
    //}
}
