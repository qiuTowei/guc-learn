package com.doublev.function;

import java.util.function.Supplier;

/**
 * 供给型 无参数，有返回值
 *
 */
public class SupplierDemo {
    public static void main(String[] args) {
        Supplier<String> supplier = new Supplier<String>() {
            /**
             * Gets a result.
             *
             * @return a result
             */
            @Override
            public String get() {
                return "返回供给";
            }
        };
        System.out.println(supplier.get());
        // lambda简化
        Supplier<String> supplier1 = () -> "再次返回供给";
        System.out.println(supplier1.get());

    }
}
