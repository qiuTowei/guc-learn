package com.doublev.unsafe;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * ConcurrentModificationException 并发修改异常
 */
public class ListTest {
    public static void main(String[] args) {
        List<String> strings = Arrays.asList("1", "2", "3", "4");
        strings.forEach(System.out :: println);
        // 并发下ArrayList不安全

        /**
         * 解决方案：
         * 1. List<String> list = new Vector<>();
         * 2. List<String> list = Collections.synchronizedList(new ArrayList<>());
         * 3. List<String> list = new CopyOnWriteArrayList<>();
         */

        //List<String> list = new ArrayList<>();
        // Vector 安全 synchronized 锁
        //List<String> list = new Vector<>();

        //List<String> list = Collections.synchronizedList(new ArrayList<>());

        // copyOnWrite写入时复制，cow 计算机程序设计领域的一种优化策略
        // lock 锁
        List<String> list = new CopyOnWriteArrayList<>();

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                list.add(UUID.randomUUID().toString().substring(0,5));
                System.out.println(list);
            },String.valueOf(i)).start();
        }
    }
}
