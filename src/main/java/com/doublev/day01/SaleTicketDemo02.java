package com.doublev.day01;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ Project: juc
 * @ Package: com.doublev.day01
 * @ Title 标题（要求能简洁地表达出类的功能和职责）
 * @ Description: 描述（简要描述类的职责、实现方式、使用注意事项等）
 * @ author : qw
 * @ CreateDate: 2021/3/4 10:14
 * @ Version: 1.0
 * @ Copyright: Copyright (c) 2021
 * @ History: 修订历史（历次修订内容、修订人、修订时间等）
 */
public class SaleTicketDemo02 {
    public static void main(String[] args) {
        // 并发，多线程操作同一个资源类，把资源类丢入线程
        Ticket2 ticket = new Ticket2();
        new Thread(()->{for (int i = 0; i < 10; i++)  ticket.sale();},"A").start();
        new Thread(()->{for (int i = 0; i < 10; i++)  ticket.sale();},"B").start();
        new Thread(()->{for (int i = 0; i < 10; i++)  ticket.sale();},"C").start();
    }

}

/**
 * Lock锁三部曲
 * 1. new ReentrantLock()
 * 2. lock.lock() 加锁
 * 3. lock.unlock() 解锁
 */
class Ticket2 {
    // 属性、方法
    private int number = 50;
    Lock lock = new ReentrantLock();
    // 买票的方式
    public  void sale() {
        // 1. 加锁
        lock.lock();
        try {
            // 2. 业务代码
            if (number > 0){
                System.out.println(Thread.currentThread().getName() + "卖出了" + (number--) + "票，剩余:" + number);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 3. 解锁
            lock.unlock();
        }
    }
}
