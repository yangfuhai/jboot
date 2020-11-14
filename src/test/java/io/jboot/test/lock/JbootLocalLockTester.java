package io.jboot.test.lock;

import io.jboot.app.JbootApplication;
import io.jboot.objects.lock.JbootLock;
import io.jboot.objects.lock.JbootLockManager;

public class JbootLocalLockTester {

    static int  index = 0;

    public static void main(String[] args) {

        JbootApplication.setBootArg("jboot.object.lock.type","local");

        JbootLock lock = JbootLockManager.me().create("myLock");

        for (int i=0;i<10;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        lock.lock();
                        JbootLocalLockTester.run();
                    }finally {
                        lock.unlock();
                    }
                }
            }).start();
        }

    }

    private static void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        index += 1;
        System.out.println("run " + index);
    }
}
