package com.doublev.readwrite;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 独占锁（写锁）：一次只能被一个线程占有
 * 共享锁（读锁）：多个线程可以同时占有
 * ReadWriteLock
 * 读-读： 可以共存
 * 读-写：不能共存
 * 写—写：不能共存
 *
 */
public class ReadWriteLockDemo {
    public static void main(String[] args) {
        MyCacheLock myCache = new MyCacheLock();

        // 写入
        for (int i = 0; i < 5; i++) {
            final int temp = i;
            new Thread(() -> {
                myCache.put(temp + "",temp);
            },String.valueOf(i)).start();
        }
        // 读取
        for (int i = 0; i < 5; i++) {
            final int temp = i;
            new Thread(() -> {
                myCache.get(String.valueOf(temp));
            },String.valueOf(i)).start();
        }
    }

}
// 加锁

class MyCacheLock {
    private volatile Map<String,Object> map = new HashMap<>();
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    // 存
    public void put(String key,Object value) {
        readWriteLock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "开始写入" + key);
            map.put(key, value);
            System.out.println(Thread.currentThread().getName() + "写入成功" + key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    // 取
    public void get(String key) {
        readWriteLock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "开始读取" + key);
            Object o = map.get(key);
            System.out.println(Thread.currentThread().getName() + "结束读取" + key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
}



/**
 * 自定义缓存
 */
class MyCache {
    private volatile Map<String,Object> map = new HashMap<>();

    // 存
    public void put(String key,Object value) {
        System.out.println(Thread.currentThread().getName() + "开始写入" + key);
        map.put(key, value);
        System.out.println(Thread.currentThread().getName() + "写入成功" + key);
    }

    // 取
    public Object get(String key) {
        System.out.println(Thread.currentThread().getName() + "开始读取" + key);
        Object o = map.get(key);
        System.out.println(Thread.currentThread().getName() + "结束读取" + key);
        return o;
    }
}

