package com.doublev.function;

import java.util.function.Consumer;

/**
 *
 * 消费性接口 -> 一个参数，无返回值
 */
public class ConsumerDemo {
    public static void main(String[] args) {
        Consumer<String> consumer = new Consumer<String>() {
            /**
             * Performs this operation on the given argument.
             *
             * @param o the input argument
             */
            @Override
            public void accept(String o) {
                System.out.println("消费了 => " + o);
            }
        };
        consumer.accept("好多钱");
        Consumer<String> consumer1 = (str) -> System.out.println(str);
        consumer1.accept("传入参数");
    }
}
