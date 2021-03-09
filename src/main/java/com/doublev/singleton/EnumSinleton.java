package com.doublev.singleton;

/**
 * 枚举类 JDK1.5
 * 本身也是一个class类
 */
public enum  EnumSinleton {
    INSTANCE;
    public EnumSinleton getInstance() {
        return INSTANCE;
    }

}
