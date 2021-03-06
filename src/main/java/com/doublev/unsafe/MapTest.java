package com.doublev.unsafe;

        import java.util.HashMap;
        import java.util.Map;
        import java.util.UUID;
        import java.util.concurrent.ConcurrentHashMap;

/**
 * @ Project: juc
 * @ Package: com.doublev.unsafe
 * @ Title 标题（要求能简洁地表达出类的功能和职责）
 * @ Description: 描述（简要描述类的职责、实现方式、使用注意事项等）
 * @ author : qw
 * @ CreateDate: 2021/3/5 14:39
 * @ Version: 1.0
 * @ Copyright: Copyright (c) 2021
 * @ History: 修订历史（历次修订内容、修订人、修订时间等）
 */
public class MapTest {
    public static void main(String[] args) {

        //Map<String,String> map = new HashMap<>();
        // 解决方案
        Map<String,String> map = new ConcurrentHashMap<>();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                // 依然有 ConcurrentModificationException并发修改异常
                map.put(Thread.currentThread().getName(), UUID.randomUUID().toString().substring(0,5));
                System.out.println(map);
            },String.valueOf(i)).start();
        }


    }
}
