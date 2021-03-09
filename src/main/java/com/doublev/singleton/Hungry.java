package com.doublev.singleton;

/**
 * 饿汉式
 * 造成内存的浪费
 * 一上来就加载所有的对象
 *
 */
public class Hungry {
    private Hungry() {

    }
    private static final Hungry HUNGRY = new Hungry();
    public static Hungry getInstance() {
        return HUNGRY;
    }
}
