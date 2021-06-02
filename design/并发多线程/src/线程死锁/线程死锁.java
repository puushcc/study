package 线程死锁;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2021年06月01日 19:11
 *
 * 死锁是指两个或两个以上的线程在执行过程中，因争夺资源而造成的互相等待的现象，在无外力作用的情况下，这些线程会一直相互等待而无法继续运行下去
 */
public class 线程死锁 {

    private static Object ResourceA = new Object();
    private static Object ResourceB = new Object();

    public static void main(String[] args) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (ResourceA) {
                    System.out.println(Thread.currentThread() + "get Re A");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread() + "wait Re B");
                    synchronized (ResourceB) {
                        System.out.println(Thread.currentThread() + "get Re B");
                    }
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (ResourceB) {
                    System.out.println(Thread.currentThread() + "get Re B");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread() + "wait Re A");
                    synchronized (ResourceA) {
                        System.out.println(Thread.currentThread() + "get Re A");
                    }
                }
            }
        });


        /**
         *   避免线程死锁
         * Thread thread2 = new Thread(new Runnable() {
         *             @Override
         *             public void run() {
         *                 synchronized (ResourceA) {
         *                     System.out.println(Thread.currentThread() + "get Re B");
         *                     try {
         *                         Thread.sleep(2000);
         *                     } catch (InterruptedException e) {
         *                         e.printStackTrace();
         *                     }
         *                     System.out.println(Thread.currentThread() + "wait Re A");
         *                     synchronized (ResourceB) {
         *                         System.out.println(Thread.currentThread() + "get Re A");
         *                     }
         *                 }
         *             }
         *         });
         *   资源的有序性破坏了资源的请求并持有条件和环路等待条件，因此避免了死锁
         *
         */

        thread.start();
        thread2.start();


    }

}
