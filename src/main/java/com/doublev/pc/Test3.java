package com.doublev.pc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 执行顺序
 * A -> B -> C -> A
 */
public class Test3 {
    public static void main(String[] args) {
        Data3 data3 = new Data3();
        new Thread(() -> {for (int i = 0; i < 10; i++) data3.printA();},"A").start();
        new Thread(() -> {for (int i = 0; i < 10; i++) data3.printB();},"B").start();
        new Thread(() -> {for (int i = 0; i < 10; i++) data3.printC();},"C").start();

    }
}
// 资源类
class Data3 {
    private Lock lock = new ReentrantLock();
    private Condition condition1 = lock.newCondition();
    private Condition condition2 = lock.newCondition();
    private Condition condition3 = lock.newCondition();
    // 1=>A 2=>B 3=>C
    private int num = 1;

    public void printA() {
        lock.lock();
        try {
            // 业务代码
            while (num != 1) {
                // 等待
                condition1.await();
            }
            System.out.println(Thread.currentThread().getName() + "=AAAAAA");
            num = 2;
            // 唤醒指定的监视器
            condition2.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
    public void printB() {
        lock.lock();
        try {
            // 业务代码
            while (num != 2) {
                // 等待
                condition2.await();
            }
            num = 3;
            System.out.println(Thread.currentThread().getName() + "=BBBBBB");
            // 唤醒指定的监视器
            condition3.signal();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
    public void printC() {
        lock.lock();
        try {
            // 业务代码
            while (num != 3) {
                // 等待
                condition3.await();
            }
            System.out.println(Thread.currentThread().getName() + "=CCCCCC");
            num = 1;
            // 唤醒指定的监视器
            condition1.signal();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

}
