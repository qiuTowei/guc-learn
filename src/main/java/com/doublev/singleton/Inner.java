package com.doublev.singleton;

/**
 *
 * 静态内部类内部类
 */
public class Inner {
    private Inner() {

    }
    public static Inner getInstance() {
        return InnerClass.INNER;
    }
    public static class InnerClass {
        private static final Inner INNER = new Inner();
    }
}

