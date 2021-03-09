package com.doublev.singleton;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * 反射破坏单例
 *
 */
public class Reflection2Singleton {
    public static void main(String[] args) throws Exception {
        // 正常方法获取懒汉式实例
        //Lazy instance1 = Lazy.getInstance2();
        // 反射获取
        Field doublev = Lazy.class.getDeclaredField("doublev");
        doublev.setAccessible(true);
        Constructor<Lazy> declaredConstructors = Lazy.class.getDeclaredConstructor(null);
        declaredConstructors.setAccessible(true);

        Lazy instance1 = declaredConstructors.newInstance();
        doublev.set(instance1,false);
        Lazy instance2 = declaredConstructors.newInstance();
        // 输出比较
        System.out.println(instance1);
        System.out.println(instance2);
        /**
         * com.doublev.singleton.Lazy@1540e19d
         * com.doublev.singleton.Lazy@677327b6
         * 输出结果不同，说明已破坏了单例
         * 1、解决方案：
         * 可以在私有方法加锁加判断，三重检查
         * 但是，如果都使用反射new的对象，则三重判断也不生效
         * 2、解决方案：
         * 通过定义标志位的方式做判断（加密判断）
         * 但是，如果通过反编译等方式，找到对应的标志位内容，通过反射设置Field还是能够破坏该方案
         *
         * 道高一尺魔高一丈，那么如果解决反射破坏单例的问题呢？
         * 通过反射的 newInstance() 方法可以得知，枚举类无法被破坏
         *
         */

        // 枚举测试
        EnumSinleton instance11 = EnumSinleton.INSTANCE;
        Constructor<EnumSinleton> declaredConstructor = EnumSinleton.class.getDeclaredConstructor(null);
        declaredConstructor.setAccessible(true);
        EnumSinleton instance22 = declaredConstructor.newInstance();
        System.out.println(instance11);
        System.out.println(instance22);
        /**
         * 通过反射破坏枚举出现错误
         * Exception in thread "main" java.lang.NoSuchMethodException: com.doublev.singleton.EnumSinleton.<init>()
         * 但是错误不是我们想要的结果（throw new IllegalArgumentException("Cannot reflectively create enum objects");）
         * 通过分析枚举类的编译代码，发现枚举类的私有构造方法为有参构造
         */
        Constructor<EnumSinleton> declaredConstructor2 = EnumSinleton.class.getDeclaredConstructor(String.class,int.class);
        declaredConstructor2.setAccessible(true);
        EnumSinleton instance33 = declaredConstructor2.newInstance();
        System.out.println(instance33);
        // 此时才是我们想要的结果
        // Exception in thread "main" java.lang.IllegalArgumentException: Cannot reflectively create enum objects
        //	at java.lang.reflect.Constructor.newInstance(Constructor.java:417)
    }
}
