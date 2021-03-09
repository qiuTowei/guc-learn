package com.doublev.cas;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicStampedReference;

public class CASDemo2 {
    public static void main(String[] args) {
        // AtomicStampedReference 注意，如果泛型是一个包装类，注意对象的引用问题
        AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<>(1,1);

        new Thread(() -> {
            // 获得版本号
            int stamp = atomicStampedReference.getStamp();
            System.out.println(" a1 => " + stamp);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 带版本号CAS
            atomicStampedReference.compareAndSet(1, 2,
                    atomicStampedReference.getStamp(), atomicStampedReference.getStamp()+1);
            System.out.println(" a2 => " + atomicStampedReference.getStamp());

            atomicStampedReference.compareAndSet(2, 1,
                    atomicStampedReference.getStamp(), atomicStampedReference.getStamp()+1);
            System.out.println(" a3 => " + atomicStampedReference.getStamp());
        },"a").start();

        new Thread(() -> {
            // 获得版本号
            int stamp = atomicStampedReference.getStamp();
            System.out.println(" b1 => " + stamp);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 带版本号CAS
            atomicStampedReference.compareAndSet(1, 6,
                    stamp, stamp+1);
            System.out.println(" b2 => " + atomicStampedReference.getStamp());
        },"b").start();

    }
}
