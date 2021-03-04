package com.doublev.day01;

/**
 * @ Project: juc
 * @ Package: com.doublev.day01
 * @ Title 基本的卖票例子
 * @ Description:
 * 线程就是一个单独的资源类，没有任何的附属操作
 * 1、属性、方法
 * @ author : qw
 * @ CreateDate: 2021/3/4 9:53
 * @ Version: 1.0
 * @ Copyright: Copyright (c) 2021
 * @ History: 修订历史（历次修订内容、修订人、修订时间等）
 */
public class SaleTicketDemo01 {
    public static void main(String[] args) {
        // 并发，多线程操作同一个资源类，把资源类丢入线程
        Ticket ticket = new Ticket();

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                ticket.sale();
            }
        },"A").start();
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                ticket.sale();
            }
        },"B").start();
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                ticket.sale();
            }
        },"C").start();

    }

}
// 资源类 OOP
class Ticket {
    // 属性、方法
    private int number = 50;
    // 买票的方式
    // synchronized 本质：队列，锁
    public synchronized void sale() {
        if (number > 0){
            System.out.println(Thread.currentThread().getName() + "卖出了" + (number--) + "票，剩余:" + number);
        }
    }
}
