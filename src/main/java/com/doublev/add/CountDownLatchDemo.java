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
                System.out.println(Thread.currentThread().getName() + " go out  " + countDownLatch.getCount());
                // -1
                countDownLatch.countDown();
            },String.valueOf(i)).start();
        }
        // 等待计数器归零，然后再向下执行
        countDownLatch.await();

        System.out.println("close door");


    }
}
