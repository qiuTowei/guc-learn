package com.doublev.singleton;

/**
 * 懒汉式
 * 单线程下没有问题，多线程会出现问题
 *
 */
public class Lazy {

    private static boolean doublev = false;
    private Lazy() {
        // 防止反射破坏
        synchronized(Lazy.class) {
            if (!doublev){
                doublev = true;
            }else{
                throw new RuntimeException("不要试图破坏单例");
            }
        }
    }
    private volatile static Lazy LAZY;

    public static Lazy getInstance1() {
        if (LAZY == null) {
            LAZY = new Lazy();
        }
        return LAZY;
    }
    // 双重检测锁 DCL
    public static Lazy getInstance2() {
        if (LAZY == null) {
            synchronized (Lazy.class) {
                if (LAZY == null) {
                    LAZY = new Lazy();
                    /**
                     * DCL 依然还是会有问题
                     * new Lazy() 不是一个原子性的操作
                     * 1、分配内存空间
                     * 2、执行构造方法，初始化对象
                     * 3、把这个对象指向这个空间
                     *
                     * 指令重排导致执行顺序：123、132
                     * 在LAZY 加Volatile
                     */
                }
            }
        }
        return LAZY;
    }


    // 多线程并发下会出现问题
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                Lazy.getInstance1();
            }).start();
        }
    }
}
