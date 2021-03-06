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
