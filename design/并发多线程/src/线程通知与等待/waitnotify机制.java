package 线程通知与等待;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2021年06月01日 16:44
 * https://www.cnblogs.com/hapjin/p/5492619.html
 */
public class waitnotify机制 {
    private static List<String> list = new ArrayList<String>();

    public static void add() {
        list.add("anyString");
    }

    public static int size() {
        return list.size();
    }


    public static class ThreadA extends Thread {

        private Object lock;

        public ThreadA(Object lock) {
            super();
            this.lock = lock;
        }

        @Override
        public void run() {
            try {
                synchronized (lock) {
                    if (waitnotify机制.size() != 5) {
                        System.out.println("wait begin "
                                + System.currentTimeMillis());
                        lock.wait();
                        System.out.println("wait end  "
                                + System.currentTimeMillis());
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static class ThreadB extends Thread {
        private Object lock;

        public ThreadB(Object lock) {
            super();
            this.lock = lock;
        }

        @Override
        public void run() {
            try {
                synchronized (lock) {
                    for (int i = 0; i < 10; i++) {
                        waitnotify机制.add();
                        if (waitnotify机制.size() == 5) {
                            lock.notify();
                            System.out.println("已经发出了通知");
                        }
                        System.out.println("添加了" + (i + 1) + "个元素!");
                        Thread.sleep(1000);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Run {

        public static void main(String[] args) {

            try {
                Object lock = new Object();

                ThreadA a = new ThreadA(lock);
                a.start();

                Thread.sleep(50);

                ThreadB b = new ThreadB(lock);
                b.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
