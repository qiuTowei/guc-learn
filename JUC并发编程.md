# JUC并发编程

### 1、什么是JUC

java.util 工具包

![image-20210304091458939](C:\Users\ThinkPad\AppData\Roaming\Typora\typora-user-images\image-20210304091458939.png)

**业务：普通的线程代码 Thread**

**Runable** 没有返回值，效率相比Callable相对较低

![image-20210304092241264](C:\Users\ThinkPad\AppData\Roaming\Typora\typora-user-images\image-20210304092241264.png)

<img src="C:\Users\ThinkPad\AppData\Roaming\Typora\typora-user-images\image-20210304092419872.png" alt="image-20210304092419872" style="zoom:80%;" />



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

![image-20210304101209613](C:\Users\ThinkPad\AppData\Roaming\Typora\typora-user-images\image-20210304101209613.png)

![image-20210304101300756](C:\Users\ThinkPad\AppData\Roaming\Typora\typora-user-images\image-20210304101300756.png)

![image-20210304101858807](C:\Users\ThinkPad\AppData\Roaming\Typora\typora-user-images\image-20210304101858807.png)

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



![image-20210304154200489](C:\Users\ThinkPad\AppData\Roaming\Typora\typora-user-images\image-20210304154200489.png)

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

![image-20210304155257214](C:\Users\ThinkPad\AppData\Roaming\Typora\typora-user-images\image-20210304155257214.png)

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

![image-20210304161432427](C:\Users\ThinkPad\AppData\Roaming\Typora\typora-user-images\image-20210304161432427.png).

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

