package com.doublev.lock;

/**
 * 可重入锁
 * synchronized
 */
public class Demo01 {
    public static void main(String[] args) {
        Phone1 phone1 = new Phone1();
        new Thread(() -> {
            phone1.sms();
        },"A").start();
        new Thread(() -> {
            phone1.sms();
        },"B").start();

    }
}

class Phone1 {
    public synchronized void sms() {
        System.out.println(Thread.currentThread().getName() + " => sms");
        call();
    }
    public synchronized void call() {
        System.out.println(Thread.currentThread().getName() + " => call");
    }
}
