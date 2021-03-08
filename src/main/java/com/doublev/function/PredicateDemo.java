package com.doublev.function;

import java.util.function.Predicate;

/**
 * Predicate 断言函数式接口
 *
 */
public class PredicateDemo {
    public static void main(String[] args) {
        // 判断字符串是否为空
        Predicate<String> predicate = new Predicate<String>() {
            /**
             * Evaluates this predicate on the given argument.
             *
             * @param o the input argument
             * @return {@code true} if the input argument matches the predicate,
             * otherwise {@code false}
             */
            @Override
            public boolean test(String o) {
                return o.isEmpty();
            }
        };
        System.out.println(predicate.test(""));
        // lambda简化
        Predicate<String> predicate1 = (str) -> str.isEmpty();
        System.out.println(predicate1.test("hello"));

    }
}
