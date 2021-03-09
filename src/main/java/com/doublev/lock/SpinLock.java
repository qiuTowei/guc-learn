package com.doublev.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 自旋锁 使用CAS
 */
public class SpinLock {
    AtomicReference<Thread> atomicReference = new AtomicReference<>();
    // 加锁
    public void myLock() {
        Thread thread = Thread.currentThread();
        System.out.println((thread.getName() + "===> myLock"));

        // 自旋锁
        while (!atomicReference.compareAndSet(null, thread)) {
            // 等待
        }
    }
    // 解锁
    public void myUnlock() {
        Thread thread = Thread.currentThread();
        System.out.println((thread.getName() + "===> myUnlock"));
        atomicReference.compareAndSet(thread, null);
    }

    public static void main(String[] args) {
        // 测试

        SpinLock spinLock = new SpinLock();

        new Thread(() -> {
            spinLock.myLock();
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                spinLock.myUnlock();
            }

        },"T1").start();
        new Thread(() -> {
            spinLock.myLock();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                spinLock.myUnlock();
            }

        },"T2").start();

    }
}
