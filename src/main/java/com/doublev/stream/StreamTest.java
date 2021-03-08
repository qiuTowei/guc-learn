package com.doublev.stream;

import java.util.stream.Stream;

/**
 * stream 流式计算
 * 对下面5个用户进行筛选（一行代码，1分钟解决）
 * 1、ID必须为偶数
 * 2、年龄必须小于23岁
 * 3、用户名转为大写字母
 * 4、用户名字母倒序
 * 5、只输出一个用户
 */
public class StreamTest {
    public static void main(String[] args) {
        // 5个用户
        User a = new User(1, "a", 20);
        User b = new User(2, "b", 21);
        User c = new User(3, "c", 22);
        User d = new User(4, "d", 23);
        User e = new User(5, "e", 24);
        // 数据交给流处理
        Stream.of(a, b, c, d, e)
                // 过滤
                .filter(u -> u.getId() % 2 == 0 && u.getAge() < 23)
                //
                .peek(u -> u.setName(u.getName().toUpperCase()))
                // 排序
                .sorted((p1, p2) -> p2.getName().compareTo(p1.getName()))
                // 限制
                .limit(1)
                .forEach(System.out::println);
    }
}
