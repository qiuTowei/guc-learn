package com.doublev.blockingqueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * @ Project: juc
 * @ Package: com.doublev.blockingqueue
 * @ Title 标题（要求能简洁地表达出类的功能和职责）
 * @ Description: 描述（简要描述类的职责、实现方式、使用注意事项等）
 * @ author : qw
 * @ CreateDate: 2021/3/6 10:05
 * @ Version: 1.0
 * @ Copyright: Copyright (c) 2021
 * @ History: 修订历史（历次修订内容、修订人、修订时间等）
 */
public class Test1 {
    public static void main(String[] args) throws InterruptedException {
        test04();
    }

    public static void  test01() {
        ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue<>(3);
        // 添加
        System.out.println(blockingQueue.add("a"));
        System.out.println(blockingQueue.add("b"));
        System.out.println(blockingQueue.add("c"));
        // 超过队列容量，再添加元素
        // 抛出 IllegalStateException: Queue full 异常
        //System.out.println(blockingQueue.add("d"));
        System.out.println("==============");
        // 查看队首元素
        System.out.println(blockingQueue.element());
        // 读取
        System.out.println(blockingQueue.remove());
        System.out.println(blockingQueue.remove());
        System.out.println(blockingQueue.remove());

        // 队列已为空，再读取
        // 抛出NoSuchElementException 异常
        System.out.println(blockingQueue.remove());

    }
    public static void  test02() {
        ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue<>(3);
        // 写入
        System.out.println(blockingQueue.offer("a"));
        System.out.println(blockingQueue.offer("b"));
        System.out.println(blockingQueue.offer("c"));

        // 队列满了，再添加元素
        // 没有抛出异常，返回结果为false
        System.out.println(blockingQueue.offer("d"));
        System.out.println("=============");
        // 查看队首元素
        System.out.println(blockingQueue.peek());
        // 读取
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        // 队列为空，再读取
        // 没有异常，返回结果为null
        System.out.println(blockingQueue.poll());
    }
    public static void  test03() throws InterruptedException {
        ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue<>(3);
        // 写入
        blockingQueue.put("a");
        blockingQueue.put("b");
        blockingQueue.put("c");

        // 队列满了，再添加元素
        // 一直阻塞
        blockingQueue.put("d");
        System.out.println("=============");

        // 读取
        System.out.println(blockingQueue.take());
        System.out.println(blockingQueue.take());
        System.out.println(blockingQueue.take());
        // 队列为空，再读取
        // 一直阻塞
        //System.out.println(blockingQueue.take());
    }

    public static void  test04() throws InterruptedException {
        ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue<>(3);
        // 写入
        System.out.println(blockingQueue.offer("a"));
        System.out.println(blockingQueue.offer("b"));
        System.out.println(blockingQueue.offer("c"));

        // 队列满了，再添加元素，添加超时参数
        // 等待超时退出
        System.out.println(blockingQueue.offer("d",2, TimeUnit.SECONDS));
        System.out.println("=============");
        // 查看队首元素
        System.out.println(blockingQueue.peek());
        // 读取
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        // 队列为空，再读取,添加超时参数
        // 超时等待后，退出
        System.out.println(blockingQueue.poll(2,TimeUnit.SECONDS));
    }
}
