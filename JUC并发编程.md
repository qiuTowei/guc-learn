

# JUC并发编程

### 1、什么是JUC

java.util 工具包

![image-20210304091458939](https://gitee.com/doubleV/cloudimage/raw/master/juc/hu7X3lEIjdrCz9x.png)

**业务：普通的线程代码 Thread**

**Runable** 没有返回值，效率相比Callable相对较低

![image-20210304092241264](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210304092241264.png)

<img src="https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210304092419872.png" alt="image-20210304092419872" style="zoom:80%;" />



### 2、线程和进程

> 线程和进程：如果不能用一句话说出来的技术可以认为不扎实

进程：一个程序；QQ.exe等程序的集合；

一个进程往往包含多个线程，至少包含一个

**java默认有两个线程：main,GC**

线程：开了一个进程Typora，会有多个线程配合（写字，自动保存等线程）

**java是否能够开启线程？开不了，最终调用的是本地方法**

```java
public synchronized void start() {
        /**
         * This method is not invoked for the main method thread or "system"
         * group threads created/set up by the VM. Any new functionality added
         * to this method in the future may have to also be added to the VM.
         *
         * A zero status value corresponds to state "NEW".
         */
        if (threadStatus != 0)
            throw new IllegalThreadStateException();

        /* Notify the group that this thread is about to be started
         * so that it can be added to the group's list of threads
         * and the group's unstarted count can be decremented. */
        group.add(this);

        boolean started = false;
        try {
            start0();
            started = true;
        } finally {
            try {
                if (!started) {
                    group.threadStartFailed(this);
                }
            } catch (Throwable ignore) {
                /* do nothing. If start0 threw a Throwable then
                  it will be passed up the call stack */
            }
        }
    }
	// 本地方法，调用底层的C++,java无法直接操作硬件（运行在虚拟机之上）
    private native void start0();
```



> 并发、并行

并发编程：并发、并行

并发（多个线程操作同一个资源）

* CPU一核，模拟出来多条线程，通过快速交替的切换线程的执行；

并行（多个人一起行走）

* CPU多核，多个线程可以同时执行；

```java
public class Test {
    public static void main(String[] args) {
        // 获取CPU合数
        // CPU密集型，IO密集型
        System.out.println(Runtime.getRuntime().availableProcessors());
    }
}
```

并发编程的本质：**充分利用CPU的资源**

> 线程有几个状态

```Java
public enum State {
        // 新生
        NEW,

       // 运行
        RUNNABLE,

        // 阻塞
        BLOCKED,

        // 等待，死死的等
        WAITING,

        // 超时等待
        TIMED_WAITING,

        // 终止
        TERMINATED;
    }
```

> wait、sleep的区别

1、**来自不同的类**

wait => Object

sleep => Thread

2、**关于锁的释放**

wait会释放锁；sleep，抱着锁睡着了，不会释放锁

3、**使用的范围不同**

wait：必须在同步代码块中

sleep ：可以在任何地方睡

4、**是否需要捕获异常**

wait不需要捕获异常

sleep必须要捕获异常

### 3、Lock锁（重点）

> 传统 synchronized

```java
package com.doublev.day01;

/**
 * @ Title 基本的卖票例子
 * @ Description:
 * 线程就是一个单独的资源类，没有任何的附属操作
 * 1、属性、方法
 * @ author : qw
 * @ CreateDate: 2021/3/4 9:53
 */
public class SaleTicketDemo01 {
    public static void main(String[] args) {
        // 并发，多线程操作同一个资源类，把资源类丢入线程
        Ticket ticket = new Ticket();

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                ticket.sale();
            }
        },"A").start();
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                ticket.sale();
            }
        },"B").start();
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                ticket.sale();
            }
        },"C").start();

    }

}
// 资源类 OOP
class Ticket {
    // 属性、方法
    private int number = 50;
    // 买票的方式
    // synchronized 本质：队列，锁
    public synchronized void sale() {
        if (number > 0){
            System.out.println(Thread.currentThread().getName() + "卖出了" + (number--) + "票，剩余:" + number);
        }
    }
}


```



> Lock 接口

![image-20210304101209613](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210304101209613.png)

![image-20210304101300756](C:\Users\ThinkPad\AppData\Roaming\Typora\typora-user-images\image-20210304101300756.png)

![image-20210304101858807](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210304101858807.png)

公平锁：公平，先来后到

**非公平锁：不公平，可以插队（默认）**

**默认那个非公平锁是为了公平（3h和3s的线程，系统使用非公平锁可进行合理调度）**

```java
package com.doublev.day01;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ Project: juc
 * @ Package: com.doublev.day01
 * @ Title 标题（要求能简洁地表达出类的功能和职责）
 * @ Description: 描述（简要描述类的职责、实现方式、使用注意事项等）
 * @ author : qw
 * @ CreateDate: 2021/3/4 10:14
 * @ Version: 1.0
 * @ Copyright: Copyright (c) 2021
 * @ History: 修订历史（历次修订内容、修订人、修订时间等）
 */
public class SaleTicketDemo02 {
    public static void main(String[] args) {
        // 并发，多线程操作同一个资源类，把资源类丢入线程
        Ticket2 ticket = new Ticket2();
        new Thread(()->{for (int i = 0; i < 10; i++)  ticket.sale();},"A").start();
        new Thread(()->{for (int i = 0; i < 10; i++)  ticket.sale();},"B").start();
        new Thread(()->{for (int i = 0; i < 10; i++)  ticket.sale();},"C").start();
    }

}

/**
 * Lock锁三部曲
 * 1. new ReentrantLock()
 * 2. lock.lock() 加锁
 * 3. lock.unlock() 解锁
 */
class Ticket2 {
    // 属性、方法
    private int number = 50;
    Lock lock = new ReentrantLock();
    // 买票的方式
    public  void sale() {
        // 1. 加锁
        lock.lock();
        try {
            // 2. 业务代码
            if (number > 0){
                System.out.println(Thread.currentThread().getName() + "卖出了" + (number--) + "票，剩余:" + number);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 3. 解锁
            lock.unlock();
        }
    }
}


```



> Synchronized 和 Lock的区别

1、Synchronized 内置的java关键字，Lock是一个java 类

2、Synchronized 无法判断获取锁的状态，Lock可以判断是否获取到锁

3、Synchronized 会自动释放锁，Lock必须要手动释放锁！如果不释放锁，死锁

4、Synchronized 线程1（获得锁，阻塞）、线程2（等待，傻傻的等）；Lock锁就不一定会等待下去

5、Synchronized 可重入锁，不可以中断的，非公平；Lock，可重入锁，可以判断锁，非公平（可以自己设置）

6、Synchronized 适合锁少量的代码同步问题，Lock适合锁大量的同步代码



> 什么是锁，如果判断锁的是谁

只要涉及到并发，必须要使用到锁



### 4、生产者和消费者问题

面试：单例模式、排序算法、生成者消费者问题、死锁

> 生产者和消费者问题 Synchronized版本

```java
package com.doublev.pc;

/**
 * 线程之间的通信问题：生产者和消费者问题 等待唤醒，等待通知
 * 线程交替执行  A、B 操作同一个变量 num=0
 * A num+1
 * B num-1
 *
 */
public class Test {
    public static void main(String[] args) {
        Data data = new Data();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    data.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"A").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    data.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"B").start();

    }

}
// 判断等待，业务，通知
class Data {

    private int number = 0;

    // +1
    public synchronized void increment() throws InterruptedException {
        if (number != 0) {
            // 等待
            this.wait();
        }
        number++;
        System.out.println(Thread.currentThread().getName() + "=>" + number);
        // 通知其他线程，+1已完毕
        this.notifyAll();
    }
    // -1
    public synchronized void decrement() throws InterruptedException {
        if (number == 0) {
            // 等待
            this.wait();
        }
        number--;
        System.out.println(Thread.currentThread().getName() + "=>" + number);
        // 通知其他线程，-1已完毕
        this.notifyAll();
    }


}
```

> 问题存在，超过两个线程之后，虚假唤醒问题



![image-20210304154200489](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210304154200489.png)

**if判断改成while循环**

```java
package com.doublev.pc;

/**
 * 线程之间的通信问题：生产者和消费者问题 等待唤醒，等待通知
 * 线程交替执行  A、B 操作同一个变量 num=0
 * A num+1
 * B num-1
 *
 */
public class Test {
    public static void main(String[] args) {
        Data data = new Data();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    data.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"A").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    data.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"B").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    data.increment();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"C").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    data.decrement();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"D").start();

    }

}
// 判断等待，业务，通知
class Data {

    private int number = 0;

    // +1
    public synchronized void increment() throws InterruptedException {
        while (number != 0) {
            // 等待
            this.wait();
        }
        number++;
        System.out.println(Thread.currentThread().getName() + "=>" + number);
        // 通知其他线程，+1已完毕
        this.notifyAll();
    }
    // -1
    public synchronized void decrement() throws InterruptedException {
        while (number == 0) {
            // 等待
            this.wait();
        }
        number--;
        System.out.println(Thread.currentThread().getName() + "=>" + number);
        // 通知其他线程，-1已完毕
        this.notifyAll();
    }


}
```

> JUC版生产者消费者问题

![image-20210304155257214](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210304155257214.png)

```Java
 class BoundedBuffer {
   final Lock lock = new ReentrantLock();
   final Condition notFull  = lock.newCondition(); 
   final Condition notEmpty = lock.newCondition(); 

   final Object[] items = new Object[100];
   int putptr, takeptr, count;

   public void put(Object x) throws InterruptedException {
     lock.lock();
     try {
       while (count == items.length)
         notFull.await();
       items[putptr] = x;
       if (++putptr == items.length) putptr = 0;
       ++count;
       notEmpty.signal();
     } finally {
       lock.unlock();
     }
   }

   public Object take() throws InterruptedException {
     lock.lock();
     try {
       while (count == 0)
         notEmpty.await();
       Object x = items[takeptr];
       if (++takeptr == items.length) takeptr = 0;
       --count;
       notFull.signal();
       return x;
     } finally {
       lock.unlock();
     }
   }
 }


```

代码实现

```Java
package com.doublev.pc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程之间的通信问题：生产者和消费者问题 等待唤醒，等待通知
 * 线程交替执行  A、B 操作同一个变量 num=0
 * A num+1
 * B num-1
 *
 */
public class Test {
    public static void main(String[] args) {
        Data data = new Data();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                data.increment();
            }
        },"A").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                data.decrement();
            }
        },"B").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                data.increment();
            }
        },"C").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                data.decrement();
            }
        },"D").start();

    }

}
// 判断等待，业务，通知
class Data {

    private int number = 0;
    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();
    // +1
    public void increment() {
        lock.lock();
        try {
            while (number != 0) {
                // 等待
                condition.await();
            }
            number++;
            System.out.println(Thread.currentThread().getName() + "=>" + number);
            // 通知其他线程，+1已完毕
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.lock();
        }

    }
    // -1
    public void decrement()  {
        lock.lock();
        try {
            while (number == 0) {
                // 等待
                condition.await();
            }
            number--;
            System.out.println(Thread.currentThread().getName() + "=>" + number);
            // 通知其他线程，-1已完毕
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
```

**任何一个新的技术，绝对不是仅仅只是覆盖原来的技术，一定会有优势和补充**

> Condition 的优势：精准的通知和唤醒线程

![image-20210304161432427](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210304161432427.png).

代码测试：

```Java
package com.doublev.pc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 执行顺序
 * A -> B -> C -> A
 */
public class Test3 {
    public static void main(String[] args) {
        Data3 data3 = new Data3();
        new Thread(() -> {for (int i = 0; i < 10; i++) data3.printA();},"A").start();
        new Thread(() -> {for (int i = 0; i < 10; i++) data3.printB();},"B").start();
        new Thread(() -> {for (int i = 0; i < 10; i++) data3.printC();},"C").start();

    }
}
// 资源类
class Data3 {
    private Lock lock = new ReentrantLock();
    private Condition condition1 = lock.newCondition();
    private Condition condition2 = lock.newCondition();
    private Condition condition3 = lock.newCondition();
    // 1=>A 2=>B 3=>C
    private int num = 1;

    public void printA() {
        lock.lock();
        try {
            // 业务代码
            while (num != 1) {
                // 等待
                condition1.await();
            }
            System.out.println(Thread.currentThread().getName() + "=AAAAAA");
            num = 2;
            // 唤醒指定的监视器
            condition2.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
    public void printB() {
        lock.lock();
        try {
            // 业务代码
            while (num != 2) {
                // 等待
                condition2.await();
            }
            num = 3;
            System.out.println(Thread.currentThread().getName() + "=BBBBBB");
            // 唤醒指定的监视器
            condition3.signal();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
    public void printC() {
        lock.lock();
        try {
            // 业务代码
            while (num != 3) {
                // 等待
                condition3.await();
            }
            System.out.println(Thread.currentThread().getName() + "=CCCCCC");
            num = 1;
            // 唤醒指定的监视器
            condition1.signal();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

}
```



### 5、8锁现象

如何判断锁的是谁，什么是锁

**深刻理解锁**

```Java
package com.doublev.lock8;

import java.util.concurrent.TimeUnit;

/**
 * 8锁，就是关于锁的8个问题
 * 1. 标准情况下，两个线程先输出打电话还是发短信？ 先发短信再打电话
 * 2. SendSms 延迟四秒，两个线程先输出什么结果？先发短信再打电话
 */
public class Test1 {
    public static void main(String[] args) {
        Phone1 phone = new Phone1();
        // 锁的存在，导致先打电话再发短信
        new Thread(() -> {
            phone.SendSms();
        },"A").start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            phone.call();
        },"B").start();
    }
}

class Phone1 {

    // synchronized 锁的对象是方法的调用者
    // 两个方法用的是同一个锁，谁先拿到谁执行

    // 打电话
    public synchronized  void call() {
        System.out.println("call");
    }

    // 发短信
    public synchronized void SendSms() {
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("sendSms");
    }
}
```

```java
package com.doublev.lock8;

import java.util.concurrent.TimeUnit;

/**
 * 8锁，就是关于锁的8个问题
 * 3. 增加一个普通方法hello，先执行发短信还是hello？先执行hello，非同步方法不受锁影响
 * 4. 两个对象，两个同步方法，先发短信还是打电话？先打电话后发短信
 */
public class Test2 {
    public static void main(String[] args) {
        // 两个对象，两个调用者，两把锁
        Phone2 phone1 = new Phone2();
        Phone2 phone2 = new Phone2();

        // 锁的存在，导致先打电话再发短信
        new Thread(() -> {
            phone1.SendSms();
        },"A").start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            phone2.call();
        },"B").start();
    }
}

class Phone2 {

    // synchronized 锁的对象是方法的调用者
    // 两个方法用的是同一个锁，谁先拿到谁执行

    // 发短信
    public synchronized void SendSms() {
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("sendSms");
    }
    // 打电话
    public synchronized  void call() {
        System.out.println("call");
    }
    // 没有锁，不是同步方法，不受锁的影响
    public void hello() {
        System.out.println("hello");
    }
}
```

```Java
package com.doublev.lock8;

import java.util.concurrent.TimeUnit;

/**
 * 8锁，就是关于锁的8个问题
 * 5. 增加两个静态同步方法，只有一个对象，先打印那个？先打印发短信，（加锁对象为Phone3.class模板）
 * 6. 增加两个静态同步方法，两个对象，先打印那个？先打印发短信，phone1，phone2都是同一个class类模板
 */
public class Test3 {
    public static void main(String[] args) {
        // 两个对象，两个调用者，两把锁
        Phone3 phone1 = new Phone3();
        Phone3 phone2 = new Phone3();

        // 锁的存在，导致先打电话再发短信
        new Thread(() -> {
            phone1.SendSms();
        },"A").start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            phone2.call();
        },"B").start();
    }
}

class Phone3 {
    // synchronized 锁的对象是方法的调用者
    // 静态同步方法 类一加载就存在 class模板（全局唯一）

    // 发短信
    public static synchronized void SendSms() {
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("sendSms");
    }
    // 打电话
    public static synchronized  void call() {
        System.out.println("call");
    }

    // 没有锁，不是同步方法，不受锁的影响
    //public void hello() {
    //    System.out.println("hello");
    //}
}

```



```Java
package com.doublev.lock8;

import java.util.concurrent.TimeUnit;

/**
 * 8锁，就是关于锁的8个问题
 * 7. 一个静态同步方法，一个普通同步方法，一个对象，先打印哪个？两个不同的锁，所以先输出打电话
 * 8. 一个静态同步方法，一个普通同步方法，两个对象，先打印哪个？先打电话
 */
public class Test4 {
    public static void main(String[] args) {
        // 两个对象，两个调用者，两把锁
        Phone4 phone1 = new Phone4();
        Phone4 phone2 = new Phone4();

        // 锁的存在，导致先打电话再发短信
        new Thread(() -> {
            phone1.SendSms();
        },"A").start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            phone2.call();
        },"B").start();
    }
}

class Phone4 {
    // synchronized 锁的对象是方法的调用者
    // 静态同步方法 类一加载就存在 class模板（全局唯一）

    // 发短信 静态同步方法  锁的是class类模板
    public static synchronized void SendSms() {
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("sendSms");
    }
    // 打电话 普通同步方法 锁的调用者
    public  synchronized  void call() {
        System.out.println("call");
    }

    // 没有锁，不是同步方法，不受锁的影响
    //public void hello() {
    //    System.out.println("hello");
    //}
}
```



> 小结

new 出来的对象

static 锁定 Class唯一的模板



### 6、集合类不安全

> List 不安全



```Java
package com.doublev.unsafe;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * ConcurrentModificationException 并发修改异常
 */
public class ListTest {
    public static void main(String[] args) {
        List<String> strings = Arrays.asList("1", "2", "3", "4");
        strings.forEach(System.out :: println);
        // 并发下ArrayList不安全

        /**
         * 解决方案：
         * 1. List<String> list = new Vector<>();
         * 2. List<String> list = Collections.synchronizedList(new ArrayList<>());
         * 3. List<String> list = new CopyOnWriteArrayList<>();
         */

        //List<String> list = new ArrayList<>();
        // Vector 安全 synchronized 锁
        //List<String> list = new Vector<>();

        //List<String> list = Collections.synchronizedList(new ArrayList<>());

        // copyOnWrite写入时复制，cow 计算机程序设计领域的一种优化策略
        // lock 锁
        List<String> list = new CopyOnWriteArrayList<>();

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                list.add(UUID.randomUUID().toString().substring(0,5));
                System.out.println(list);
            },String.valueOf(i)).start();
        }
    }
}
```

**学习方法：1. 先会用 2. 对比其他方法（货比三家）3. 分析底层源码**



> Set 不安全



```Java
package com.doublev.unsafe;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 *
 *
 */
public class SetTest {
    public static void main(String[] args) {
        Set<String> sets = new HashSet<>();
        for (int i = 0; i < 30; i++) {
            new Thread(() -> {
                // ConcurrentModificationException 并发修改异常
                sets.add(UUID.randomUUID().toString().substring(0,5));
                System.out.println(sets);
            },String.valueOf(i)).start();
        }
        // 解决方法
        /**
         * Collections.synchronizedSet(new HashSet<>());
         * new CopyOnWriteArraySet<>()
         */

    }
}
```

HashSet的底层是什么

```Java
public HashSet() {
        map = new HashMap<>();
}
// add
public boolean add(E e) {
        return map.put(e, PRESENT)==null;
}
```

> HashMap 不安全

```Java
package com.doublev.unsafe;

        import java.util.HashMap;
        import java.util.Map;
        import java.util.UUID;
        import java.util.concurrent.ConcurrentHashMap;

/**
 * @ Project: juc
 * @ Package: com.doublev.unsafe
 * @ Title 标题（要求能简洁地表达出类的功能和职责）
 * @ Description: 描述（简要描述类的职责、实现方式、使用注意事项等）
 * @ author : qw
 * @ CreateDate: 2021/3/5 14:39
 * @ Version: 1.0
 * @ Copyright: Copyright (c) 2021
 * @ History: 修订历史（历次修订内容、修订人、修订时间等）
 */
public class MapTest {
    public static void main(String[] args) {

        //Map<String,String> map = new HashMap<>();
        // 解决方案
        Map<String,String> map = new ConcurrentHashMap<>();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                // 依然有 ConcurrentModificationException并发修改异常
                map.put(Thread.currentThread().getName(), UUID.randomUUID().toString().substring(0,5));
                System.out.println(map);
            },String.valueOf(i)).start();
        }


    }
}
```





### 7、Callable（简单）



![image-20210305151118926](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210305151118926.png)

与Runnable差异

1、可以有返回值

2、可以抛出异常

3、方法不同



![image-20210305152549305](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210305152549305.png)

![image-20210305152658530](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210305152658530.png)





```Java
package com.doublev.callable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @ Project: juc
 * @ Package: com.doublev.callable
 * @ Title 标题（要求能简洁地表达出类的功能和职责）
 * @ Description: 描述（简要描述类的职责、实现方式、使用注意事项等）
 * @ author : qw
 * @ CreateDate: 2021/3/5 15:12
 * @ Version: 1.0
 * @ Copyright: Copyright (c) 2021
 * @ History: 修订历史（历次修订内容、修订人、修订时间等）
 */
public class CallableTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // 如何启动Callable
        //new Thread().start();
        //new Thread(new FutureTask<V>(Callable)).start();

        MyThread myThread = new MyThread();
        // 适配类
        FutureTask futureTask = new FutureTask(myThread);
        // 多个线程只会打印一个hello，线程缓存，提高效率
        new Thread(futureTask,"A").start();

        new Thread(futureTask,"B").start();


        // get方法可能会产生阻塞，需要等待线程结束
        // 把他放在最后，或者异步
        Object o = futureTask.get();
        System.out.println(o);

    }

}

class MyThread implements Callable<String> {

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public String call() throws Exception {
        System.out.println("hello");
        return "result";
    }
}
```

细节：

1、有缓存

2、结果需要等待，会阻塞



### 8、常用的辅助类

#### 8.1、CountDownLatch

![image-20210305161033350](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210305161033350.png)



```Java
package com.doublev.add;
import java.util.concurrent.CountDownLatch;

/**
 * 计数器
 */
public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(6);
        for (int i = 0; i < 6; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + " go out");
                // -1
                countDownLatch.countDown();
            },String.valueOf(i)).start();
        }
        // 等待计数器归零，然后再向下执行
        countDownLatch.await();

        System.out.println("close door");


    }
}
```

原理：

// -1

countDownLatch.countDown();

// 等待计数器归零，再往下执行

countDownLatch.await();

#### 8.2、CyclicBarrier

![image-20210305162725583](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210305162725583.png)

加法计数器

代码实现收集龙珠

```Java
package com.doublev.add;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @ Project: juc
 * @ Package: com.doublev.add
 * @ Title 标题（要求能简洁地表达出类的功能和职责）
 * @ Description: 描述（简要描述类的职责、实现方式、使用注意事项等）
 * @ author : qw
 * @ CreateDate: 2021/3/5 16:35
 * @ Version: 1.0
 * @ Copyright: Copyright (c) 2021
 * @ History: 修订历史（历次修订内容、修订人、修订时间等）
 */
public class CyclicBarrierDemo {
    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(7,() -> System.out.println("召唤神龙"));

        for (int i = 0; i < 7; i++) {
            // 临时final变量
            final int temp = i;
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "收集了" + temp + "颗龙珠");
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }

    }
}
```



#### 8.3、Semaphore

Semaphore：信号量

![image-20210305164647324](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210305164647324.png)

3车位 6辆车 抢车位限流

```Java
package com.doublev.add;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @ Project: juc
 * @ Package: com.doublev.add
 * @ Title 标题（要求能简洁地表达出类的功能和职责）
 * @ Description: 描述（简要描述类的职责、实现方式、使用注意事项等）
 * @ author : qw
 * @ CreateDate: 2021/3/5 16:45
 * @ Version: 1.0
 * @ Copyright: Copyright (c) 2021
 * @ History: 修订历史（历次修订内容、修订人、修订时间等）
 */
public class SemaphoreDemo {
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(3);
        // 3个停车位，6辆车抢车位 限流
        for (int i = 0; i < 6; i++) {
            new Thread(() -> {
                // acquire() 得到
                try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + " 抢到了车位");
                    TimeUnit.SECONDS.sleep(2);
                    System.out.println(Thread.currentThread().getName() + " 离开了车位");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // release() 释放
                    semaphore.release();
                }


            }).start();
        }
    }
}
```

**原理：**

`semaphore.acquire()`  获得，假设已经满了，等待，等待被释放为止

`semaphore.release() ` 释放，会将当前的信号量释放+1，然后唤醒等待的线程

作用：多个共享资源互斥的使用！并发限流，控制最大的线程数

### 9、读写锁

> ReadWriteLock



![image-20210305173217008](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210305173217008.png)

代码实现：

```Java
package com.doublev.readwrite;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 独占锁（写锁）：一次只能被一个线程占有
 * 共享锁（读锁）：多个线程可以同时占有
 * ReadWriteLock
 * 读-读： 可以共存
 * 读-写：不能共存
 * 写—写：不能共存
 *
 */
public class ReadWriteLockDemo {
    public static void main(String[] args) {
        MyCacheLock myCache = new MyCacheLock();

        // 写入
        for (int i = 0; i < 5; i++) {
            final int temp = i;
            new Thread(() -> {
                myCache.put(temp + "",temp);
            },String.valueOf(i)).start();
        }
        // 读取
        for (int i = 0; i < 5; i++) {
            final int temp = i;
            new Thread(() -> {
                myCache.get(String.valueOf(temp));
            },String.valueOf(i)).start();
        }
    }

}
// 加锁

class MyCacheLock {
    private volatile Map<String,Object> map = new HashMap<>();
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    // 存
    public void put(String key,Object value) {
        readWriteLock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "开始写入" + key);
            map.put(key, value);
            System.out.println(Thread.currentThread().getName() + "写入成功" + key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    // 取
    public void get(String key) {
        readWriteLock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "开始读取" + key);
            Object o = map.get(key);
            System.out.println(Thread.currentThread().getName() + "结束读取" + key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
}
/**
 * 自定义缓存
 */
class MyCache {
    private volatile Map<String,Object> map = new HashMap<>();

    // 存
    public void put(String key,Object value) {
        System.out.println(Thread.currentThread().getName() + "开始写入" + key);
        map.put(key, value);
        System.out.println(Thread.currentThread().getName() + "写入成功" + key);
    }

    // 取
    public Object get(String key) {
        System.out.println(Thread.currentThread().getName() + "开始读取" + key);
        Object o = map.get(key);
        System.out.println(Thread.currentThread().getName() + "结束读取" + key);
        return o;
    }
}

```



### 10、阻塞队列



阻塞、队列（FIFO先进先出）

进：写入，如果队列满了，就必须阻塞等待

出：读取，如果队列空，必须阻塞等待生成



阻塞队列：

![image-20210306100346614](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210306100346614.png)



**BlockingQueue**

![image-20210306101424267](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210306101424267.png)

什么情况会使用阻塞队列：多线程，线程池

**学会使用队列**

**四组API**

| 方式         | 抛出异常 | 有返回值，不抛出异常 | 阻塞 等待 | 超时等待     |
| ------------ | -------- | -------------------- | --------- | ------------ |
| 添加         | add      | offer                | put       | offer 有参数 |
| 移除         | remove   | poll                 | take      | poll 有参数  |
| 检测对手元素 | element  | peek                 | -         | -            |

1、抛出异常

```java
public static void  test01() {
        ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue<>(3);
        // 添加
        System.out.println(blockingQueue.add("a"));
        System.out.println(blockingQueue.add("b"));
        System.out.println(blockingQueue.add("c"));
        // 超过队列容量，再添加元素
        // 抛出 IllegalStateException: Queue full 异常
        //System.out.println(blockingQueue.add("d"));
        System.out.println("==============");
        // 查看队首元素
        System.out.println(blockingQueue.element());
        // 读取
        System.out.println(blockingQueue.remove());
        System.out.println(blockingQueue.remove());
        System.out.println(blockingQueue.remove());

        // 队列已为空，再读取
        // 抛出NoSuchElementException 异常
        System.out.println(blockingQueue.remove());

    }
```



2、不会抛出异常

```Java
public static void  test02() {
        ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue<>(3);
        // 写入
        System.out.println(blockingQueue.offer("a"));
        System.out.println(blockingQueue.offer("b"));
        System.out.println(blockingQueue.offer("c"));

        // 队列满了，再添加元素
        // 没有抛出异常，返回结果为false
        System.out.println(blockingQueue.offer("d"));
        System.out.println("=============");
        // 查看队首元素
        System.out.println(blockingQueue.peek());
        // 读取
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        // 队列为空，再读取
        // 没有异常，返回结果为null
        System.out.println(blockingQueue.poll());
    }
```



3、阻塞等待

```Java
public static void  test03() throws InterruptedException {
        ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue<>(3);
        // 写入
        blockingQueue.put("a");
        blockingQueue.put("b");
        blockingQueue.put("c");

        // 队列满了，再添加元素
        // 一直阻塞
        blockingQueue.put("d");
        System.out.println("=============");

        // 读取
        System.out.println(blockingQueue.take());
        System.out.println(blockingQueue.take());
        System.out.println(blockingQueue.take());
        // 队列为空，再读取
        // 一直阻塞
        //System.out.println(blockingQueue.take());
    }
```



4、超时等待

```Java
public static void  test04() throws InterruptedException {
        ArrayBlockingQueue blockingQueue = new ArrayBlockingQueue<>(3);
        // 写入
        System.out.println(blockingQueue.offer("a"));
        System.out.println(blockingQueue.offer("b"));
        System.out.println(blockingQueue.offer("c"));

        // 队列满了，再添加元素，添加超时参数
        // 等待超时退出
        System.out.println(blockingQueue.offer("d",2, TimeUnit.SECONDS));
        System.out.println("=============");
        // 查看队首元素
        System.out.println(blockingQueue.peek());
        // 读取
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        System.out.println(blockingQueue.poll());
        // 队列为空，再读取,添加超时参数
        // 超时等待后，退出
        System.out.println(blockingQueue.poll(2,TimeUnit.SECONDS));
    }
}
```



> SynchronousQueue 同步队列

不存储元素，没有容量

进去一个元素，必须等待里面的元素取出来之后才能放进去

```Java
package com.doublev.blockingqueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * 同步队列
 * 和其他的blockingQueue不同，SynchronousQueue 不存储元素
 * put了一个元素，必须take之后才能继续put
 */
public class SynchronousQueueDemo {
    public static void main(String[] args) {
        // 同步队列
        BlockingQueue<String> blockingQueue = new SynchronousQueue<>();

        new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + "put 1");
                blockingQueue.put("1");
                System.out.println(Thread.currentThread().getName() + "put 2");
                blockingQueue.put("2");
                System.out.println(Thread.currentThread().getName() + "put 3");
                blockingQueue.put("3");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"T1").start();
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName() + "=> take" + blockingQueue.take());
                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName() + "=> take" +blockingQueue.take());
                TimeUnit.SECONDS.sleep(3);
                System.out.println(Thread.currentThread().getName() + "=> take" +blockingQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"T2").start();
        /**
         * 输出结果：
         * T1put 1
         * T2=> take1
         * T1put 2
         * T2=> take2
         * T1put 3
         * T2=> take3
         */

    }
}
```

### 11、线程池（重点）

**线程池内容：三大方法，7大参数，4中拒绝策略**



> 池化技术

程序的运行，本质：占用系统的资源！所以必须涉及到资源使用的优化 => 池化技术

线程池、连接池、内存池、对象池等（由于创建、销毁十分浪费资源）

池化技术：事先准备好一些（设定值）资源，有人需要使用就来拿取资源使用，使用完再归还

**线程池的好处：**

1、降低资源的消耗

2、提供响应的速度

3、方便管理

**线程可以复用，可以控制最大并发数，可以管理线程**



> 线程池：三大方法

![image-20210306143926616](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210306143926616.png)

```Java
package com.doublev.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * Executors 工具类，3大方法
 * 使用了线程池之后，使用线程池来新线程
 */
public class Demo01 {
    public static void main(String[] args) {
        // 单个线程
        //ExecutorService executorService = Executors.newSingleThreadExecutor();
        // 固定线程
        //ExecutorService executorService = Executors.newFixedThreadPool(5);
        // 可伸缩，视情况而定
        ExecutorService executorService = Executors.newCachedThreadPool();

        try {
            for (int i = 0; i < 100; i++) {
                executorService.execute(() -> {
                    System.out.println(Thread.currentThread().getName() + " ok ");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 线程池用完需要手动关闭
            executorService.shutdown();
        }

    }
}
```



> 七大参数

三大方法源码分析：

```Java
public static ExecutorService newSingleThreadExecutor() {
        return new FinalizableDelegatedExecutorService
            (new ThreadPoolExecutor(1, 1,
                                    0L, TimeUnit.MILLISECONDS,
                                    new LinkedBlockingQueue<Runnable>()));
    }

public static ExecutorService newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                                      0L, TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>());
    }

public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                      60L, TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>());
    }
// 本质ThreadPoolExecutor()

public ThreadPoolExecutor(int corePoolSize, // 核心线程池大小
                              int maximumPoolSize, // 最大核心线程池大小
                              long keepAliveTime, // 超时没人调用就会释放
                              TimeUnit unit, // 超时单位
                              BlockingQueue<Runnable> workQueue, // 阻塞队列
                              ThreadFactory threadFactory, // 线程工厂，创建线程，一般不用动 
                              RejectedExecutionHandler handler // 拒绝策略
                         ) {
        if (corePoolSize < 0 ||
            maximumPoolSize <= 0 ||
            maximumPoolSize < corePoolSize ||
            keepAliveTime < 0)
            throw new IllegalArgumentException();
        if (workQueue == null || threadFactory == null || handler == null)
            throw new NullPointerException();
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = workQueue;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = threadFactory;
        this.handler = handler;
    }
```



分析：

![image-20210306145752916](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210306145752916.png)

> 手动创建一个线程池

```Java
package com.doublev.pool;

import java.util.concurrent.*;

/**
 *
 * 模拟银行办理业务
 */
public class Demo02 {
    public static void main(String[] args) {
        // 自定义线程池 熟记七大参数的含义
        ExecutorService executorService = new ThreadPoolExecutor(
                // 核心线程池大小
                2,
                // 最大线程池大小
                5,
                // 超时大小
                3,
                // 超时单位
                TimeUnit.SECONDS,
                // 阻塞队列
                new LinkedBlockingQueue(3),
                // 线程工厂
                Executors.defaultThreadFactory(),
                //
                /**
                 * 拒绝策略
                 * 1、AbortPolicy 超过最大承载，还有任务，不处理这个任务抛出异常 RejectedExecutionException
                 * 2、CallerRunsPolicy 超过最大承载，还有的任务，哪来的返回哪里处理
                 * 3、DiscardOldestPolicy 超过最大承载，还有的任务，尝试去跟最早的竞争，如果失败依旧丢失任务，不会抛出异常
                 * 4、DiscardPolicy 超过最大承载，还有的任务，不会抛出异常，丢失任务
                 */
                new ThreadPoolExecutor.CallerRunsPolicy());

        try {
            // 最大承载：Deque + max = 8
            // 超出最大承载，会被拒绝策略处理
            for (int i = 0; i < 9; i++) {
                executorService.execute(() -> {
                    System.out.println(Thread.currentThread().getName() + " ok ");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 线程池用完需要手动关闭
            executorService.shutdown();
        }

    }
}
```





> 四种拒绝策略

![image-20210306150153712](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210306150153977.png)

```Java
/**
 * 拒绝策略
* 1、AbortPolicy 超过最大承载，还有任务，不处理这个任务抛出异常 RejectedExecutionException
* 2、CallerRunsPolicy 超过最大承载，还有的任务，哪来的返回哪里处理
* 3、DiscardOldestPolicy 超过最大承载，还有的任务，尝试去跟最早的竞争，如果失败依旧丢失任务，不会抛出异常
* 4、DiscardPolicy 超过最大承载，还有的任务，不会抛出异常，丢失任务
*/
```



> 小结和拓展

线程池的大小如何设置：

需要了解：IO密集型、CPU密集型（调优）

```Java
/**
* 最大线程数如何定义
* 1、CPU密集型：几核cpu设置几个，可以保持cpu的效率最高
* 2、IO密集型：判断程序中十分消耗IO的线程数量（如果有15个大型任务，设置为15）
*/
// 获取CPU核数
int i1 = Runtime.getRuntime().availableProcessors();
```



### 12、四大函数式接口（必须要会）

> 函数式接口

```Java
@FunctionalInterface // 函数式接口注释
public interface Runnable {
    public abstract void run();
}
// 作用：简化编程模型
// 基础
// 泛型、枚举、反射
// lambda表达式、链式编程、函数式接口、Stream流式计算
```



![image-20210306153325787](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210306153325787.png).

> **Function 函数型接口**

![image-20210308102842838](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210308102842838.png)



```Java
package com.doublev.function;

        import java.util.function.Function;

/**
 * Function 函数式接口，有一个输入参数，一个输出
 *
 *
 */
public class FunctionDemo {
    public static void main(String[] args) {
        Function<String, String> function = new Function<String, String>() {
            /**
             * Applies this function to the given argument.
             *
             * @param o the function argument
             * @return the function result
             */
            @Override
            public String apply(String o) {
                return o;
            }
        };
        // lambda表达式简化
        Function<String,String> function1 = (str) -> str;
        System.out.println(function.apply("3333"));
        System.out.println(function1.apply("function1"));
    }
}
```

> **Predicate 断言型接口**

![image-20210308104638846](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210308104638846.png)

```Java
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

```



> **Consumer 消费性接口**

![image-20210308105041615](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210308105041615.png)



```Java
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
```



> **Supplier 供给型接口**

![image-20210308105745482](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210308105745482.png)



```Java
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
```



### 13、Stream 流式计算

> 什么是Stream流式计算

大数据：存储+计算

集合、MySQL本质是存储数据

流用来做计算

![image-20210308112539337](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210308112539337.png)

```Java
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
```



### 14、ForkJoin

> 什么是ForkJoin

ForkJoin是JDK1.7后出现，并行执行任务，提高程序效率（适合大数据量使用）

大任务拆分为小任务执行

![image-20210308112845605](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210308112845605.png)



**ForkJoin 特点：工作窃取（底层维护双端队列）**

如下图，A，B两个线程，B线程完成任务之后，会从A线程队列的另一端窃取任务

![image-20210308113117287](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210308113117287.png).



> ForkJoin操作

**ForkJoinPool**的执行方法

![image-20210308134816964](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210308134816964.png).

参数：**ForkJoinTask**

![image-20210308135346763](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210308135346763.png).

测试类

```Java
package com.doublev.forkjoin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.LongStream;

/**
 * 测试方法
 */
public class Test {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        long startTime = System.currentTimeMillis();
        /**
         * 结果 => 499999999500000000
         * 一共耗时 = 8755
         *
         */
        //Long sum = test1();

        /**
         * 结果 => 499934463999828390
         * 一共耗时 = 5227
         */
        //Long sum = test2();
        /**
         *结果 => 499999999500000000
         * 一共耗时 = 238
         */
        Long sum = test3();
        System.out.println("结果 => " + sum);
        System.out.println("一共耗时 = " + (System.currentTimeMillis() - startTime));

    }

    /**
     * 普通方法
     * @return long
     */
    public static Long test1() {
        Long sum = 0L;
        for (Long i = 1L; i <= 10_0000_0000; i++) {
            sum += i;
        }
        return sum;
    }

    /**
     * ForkJoin 方法
     * @return long
     */
    public static Long test2() throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinTask<Long> task = new ForkJoinDemo(0L, 10_0000_0000L);
        // 提交
        ForkJoinTask<Long> submit = forkJoinPool.submit(task);
        return submit.get();
    }

    /**
     * stream 并行流
     * @return long
     */
    public static Long test3() {
        return LongStream.range(0L, 10_0000_0000L).parallel().reduce(0, Long::sum);
    }
}
```

ForkJoin任务

```Java
package com.doublev.forkjoin;

import java.util.concurrent.RecursiveTask;

/**
 * 大数据量的求和任务
 * 如何使用 ForkJoin
 * ForkJoinPool
 * 计算任务 ：forkJoin.execute(ForkJoinTask<?> task)
 *
 */
public class ForkJoinDemo extends RecursiveTask<Long> {
    private Long start;
    private Long end;

    // 临界值
    private Long temp = 10000L;

    public ForkJoinDemo(Long start, Long end) {
        this.start = start;
        this.end = end;
    }

    /**
     * 实现的计算方法
     * @return Long
     */
    @Override
    protected Long compute() {
        if ((end-start) < temp) {
            Long sum = 0L;
            for (Long i = start; i < end; i++) {
                sum += i;
            }
            return sum;
        }else {
            // 使用ForkJoin
            // 求中间值
            Long middle = (start + end) / 2;
            // 拆分任务
            ForkJoinDemo task1 = new ForkJoinDemo(start, middle);
            ForkJoinDemo task2 = new ForkJoinDemo(middle + 1, end);
            // 任务压入线程队列
            task1.fork();
            task2.fork();
            // 获取结果
            return task1.join() + task2.join();
        }
    }
}
```

### 15、异步回调

> Future 设计的初衷，对将来的某个事件结果进行建模

![image-20210308150934612](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210308150934612.png)

![image-20210308150955669](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210308150955669.png).



```Java
package com.doublev.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 *
 * 异步调用：Ajax
 * 异步执行
 * 成功回调
 * 失败回调
 */
public class Demo01 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 发起一个请求
        // 没有返回值的异步回调
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println((Thread.currentThread().getName() + " runAsync => void"));
        });
        System.out.println(("1111"));
        completableFuture.get();

        // 有返回值的异步回调
        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println((Thread.currentThread().getName() + " supplyAsync => Integer"));
            int i = 10/0;
            return 1024;
        });
        System.out.println(integerCompletableFuture.whenComplete((t, u) -> {
            // 正确的返回结果
            System.out.println(t);
            // 错误信息
            System.out.println(u);
        }).exceptionally((e) -> {
            // 错误
            System.out.println(e.getMessage());
            // 错误的返回结果
            return 404;
        }).get());
    }
}
```

### 16、JMM

> 请你谈谈对volatile的理解

Volatile是Java虚拟机提供的**轻量级的同步机制**

1、保证可见性

2、**不保证原子性**

3、禁止指令重排

> 什么是JMM

JMM：Java内存模型，是一种约定，概念。并不实际存在

**关于JMM的一些同步约定：**

1、 线程解锁前，必须把共享变量**立刻**写回主存

2、线程加锁前，必须读取主存中最新值到工作内存中

3、加锁和解锁是同一把锁

线程 => 工作内存 => 主存

四组**八种操作**，如下图：

![image-20210308154056448](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210308154056448.png)

第一组：read，读取主存中的数据；load，加载到工作内存

第二组：use，工作引擎使用；assign，使用完放入工作内存

第三组：write，写入主存；store，存储数据

第四组：lock，加锁；unlock，解锁

![image-20210308154544204](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210308154544204.png)



**内存交互操作有8种，虚拟机实现必须保证每一个操作都是原子的，不可在分的（对于double和long类型的变量来说，load、store、read和write操作在某些平台上允许例外）**

* lock   （锁定）：作用于主内存的变量，把一个变量标识为线程独占状态

- unlock （解锁）：作用于主内存的变量，它把一个处于锁定状态的变量释放出来，释放后的变量才可以被其他线程锁定
- read  （读取）：作用于主内存变量，它把一个变量的值从主内存传输到线程的工作内存中，以便随后的load动作使用
- load   （载入）：作用于工作内存的变量，它把read操作从主存中变量放入工作内存中
- use   （使用）：作用于工作内存中的变量，它把工作内存中的变量传输给执行引擎，每当虚拟机遇到一个需要使用到变量的值，就会使用到这个指令
- assign （赋值）：作用于工作内存中的变量，它把一个从执行引擎中接受到的值放入工作内存的变量副本中
- store  （存储）：作用于主内存中的变量，它把一个从工作内存中一个变量的值传送到主内存中，以便后续的write使用
- write 　（写入）：作用于主内存中的变量，它把store操作从工作内存中得到的变量的值放入主内存的变量中

**JMM对这八种指令的使用，制定了如下规则：**

* 不允许read和load、store和write操作之一单独出现。即使用了read必须load，使用了store必须write

- 不允许线程丢弃他最近的assign操作，即工作变量的数据改变了之后，必须告知主存
- 不允许一个线程将没有assign的数据从工作内存同步回主内存
- 一个新的变量必须在主内存中诞生，不允许工作内存直接使用一个未被初始化的变量。就是怼变量实施use、store操作之前，必须经过assign和load操作
- 一个变量同一时间只有一个线程能对其进行lock。多次lock后，必须执行相同次数的unlock才能解锁
- 如果对一个变量进行lock操作，会清空所有工作内存中此变量的值，在执行引擎使用这个变量前，必须重新load或assign操作初始化变量的值
- 如果一个变量没有被lock，就不能对其进行unlock操作。也不能unlock一个被其他线程锁住的变量
- 对一个变量进行unlock操作之前，必须把此变量同步回主内存

　　JMM对这八种操作规则和对[volatile的一些特殊规则](https://www.cnblogs.com/null-qige/p/8569131.html)就能确定哪里操作是线程安全，哪些操作是线程不安全的了。但是这些规则实在复杂，很难在实践中直接分析。所以一般我们也不会通过上述规则进行分析。更多的时候，使用java的happen-before规则来进行分析。



![image-20210308155704932](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210308155704932.png)

问题：程序不知道主内存中值已经被修改

所以，引出volatile 



### 17、Volatile

> 可见性

```java
package com.doublev.jmm;

import java.util.concurrent.TimeUnit;

/**
 * volatile
 * 1、保证可见性
 *
 */
public class VolatileDemo01 {
    // 不加volatile,程序死循环
    // 加了volatile,保证了可见性
    private static volatile Integer num = 0;

    public static void main(String[] args) {
        new Thread(() -> {
            while (num == 0){

            }
        }).start();
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        num = 1;
        System.out.println(num);
    }
}
```

> 不保证原子性

原子性：不可分割，要么同时成功，要么同时失败

```java
package com.doublev.jmm;

/**
 * volatile
 * 不保证原子行
 */
public class VolatileDemo02 {
    private volatile static int num = 0;
    public static void add() {
        num++;
    }

    public static void main(String[] args) {
        // 理论上结果为20000
        // 实际结果为小于20000的随机值
        // volatile 不保证原子性
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    add();
                }
            }).start();
        }
        while (Thread.activeCount() > 2) {
            // main gc 线程
            Thread.yield();
        }
        System.out.println((Thread.currentThread().getName() + num));
    }
}
```

上述的代码，如果不加lock和synchronized，怎么保证原子性？

**使用原子类来处理**

![image-20210308163536212](https://gitee.com/doubleV/cloudimage/raw/master/juc/image-20210308163536212.png).

**这些类的底层都直接与操作系统操作**

```java
package com.doublev.jmm;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * volatile
 * 不保证原子行
 */
public class VolatileDemo02 {

    private volatile static AtomicInteger num = new AtomicInteger();
    public static void add() {
        //num++;
        // AtomicInteger +1操作  CAS
        num.getAndIncrement();
}

    public static void main(String[] args) {
        // 理论上结果为20000
        // 实际结果为小于20000的随机值
        // volatile 不保证原子性
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    add();
                }
            }).start();
        }
        while (Thread.activeCount() > 2) {
            // main gc 线程
            Thread.yield();
        }
        System.out.println((Thread.currentThread().getName() + num));
    }
}
```



> 指令重排

处理器会按照自有逻辑重新排列代码的执行顺序

进行指令重排的时候会考虑**数据之间的依赖性**





### 19、彻底玩转单例模式

### 20、深入理解CAS

### 21、原子引用

#### 1、公平锁，非公平锁

#### 2、可重入锁

#### 3、自旋锁

#### 4、死锁