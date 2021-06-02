package 守护线程;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2021年06月01日 19:35
 *
 * Java中的线程分为两类，分别为daemon线程（守护线程）和user线程（用户线程）。
 * 在JVM启动时会调用main函数，main函数所在的线程就是一个用户线程，其实在JVM内部同时还启动了好多守护线程，比如垃圾回收线程。
 * 那么守护线程和用户线程有什么区别呢？
 * 区别之一是当最后一个非守护线程结束时，JVM会正常退出，而不管当前是否有守护线程，也就是说守护线程是否结束并不影响JVM的退出。
 * 言外之意，只要有一个用户线程还没结束，正常情况下JVM就不会退出。
 *
 *
 *
 *
 * 如果你希望在主线程结束后JVM进程马上结束，那么在创建线程时可以将其设置为守护线程，
 * 如果你希望在主线程结束后子线程继续工作，等子线程结束后再让JVM进程结束，那么就将子线程设置为用户线程。
 */
public class 创建守护线程 {

    public static void main(String[] args) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });

        thread.setDaemon(true);
        thread.start();
    }
}
