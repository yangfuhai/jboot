package io.jboot.test.lock;

public class NoneLockTester {

    static int  index = 0;

    public static void main(String[] args) {


        for (int i=0;i<100;i++){
            new Thread(() -> run()).start();
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
