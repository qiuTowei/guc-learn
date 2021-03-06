package com.doublev.unsafe;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 *
 *
 */
public class SetTest {
    public static void main(String[] args) {
        Set<String> sets = new HashSet<>();
        for (int i = 0; i < 30; i++) {
            new Thread(() -> {
                // ConcurrentModificationException 并发修改异常
                sets.add(UUID.randomUUID().toString().substring(0,5));
                System.out.println(sets);
            },String.valueOf(i)).start();
        }
        // 解决方法
        /**
         * Collections.synchronizedSet(new HashSet<>());
         * new CopyOnWriteArraySet<>()
         */

    }
}
