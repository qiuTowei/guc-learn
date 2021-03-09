package com.doublev.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 可重入锁
 * Lock
 */
public class Demo02 {
    public static void main(String[] args) {
        Phone2 phone1 = new Phone2();
        new Thread(() -> {
            phone1.sms();
        },"A").start();
        new Thread(() -> {
            phone1.sms();
        },"B").start();

    }
}
class Phone2 {
    Lock lock = new ReentrantLock();
    public  void sms() {
        lock.lock();
        // 需要注意，lock锁必须成对出现，有lock() ,必须要unlock(),否则会死锁
        try {
            System.out.println(Thread.currentThread().getName() + " => sms");
            call();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
    public  void call() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " => call");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
