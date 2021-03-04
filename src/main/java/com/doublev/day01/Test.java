package com.doublev.day01;

import java.util.concurrent.TimeUnit;

/**
 * @ Project: juc
 * @ Package: com.doublev.day01
 * @ Title 标题（要求能简洁地表达出类的功能和职责）
 * @ Description: 描述（简要描述类的职责、实现方式、使用注意事项等）
 * @ author : qw
 * @ CreateDate: 2021/3/3 15:43
 * @ Version: 1.0
 * @ Copyright: Copyright (c) 2021
 * @ History: 修订历史（历次修订内容、修订人、修订时间等）
 */
public class Test {
    public static void main(String[] args) throws InterruptedException {
        System.out.println(Thread.activeCount());
        // 获取CPU合数
        // CPU密集型，IO密集型
        System.out.println(Runtime.getRuntime().availableProcessors());

        TimeUnit.SECONDS.sleep(2);
        Thread.sleep(100);
    }
}
