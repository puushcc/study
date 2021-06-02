package 让线程睡眠的sleep方法;

import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2021年06月01日 17:16
 *
 * Thread类中有一个静态的sleep方法，当一个执行中的线程调用了Thread的sleep方法后，调用线程会暂时让出指定时间的执行权，
 * 也就是在这期间不参与CPU的调度，但是该线程所拥有的监视器资源，比如锁还是持有不让出的。
 * 指定的睡眠时间到了后该函数会正常返回，线程就处于就绪状态，然后参与CPU的调度，获取到CPU资源后就可以继续运行了。
 * 如果在睡眠期间其他线程调用了该线程的interrupt（）方法中断了该线程，则该线程会在调用sleep方法的地方抛出InterruptedException异常而返回。
 *
 */
public class 静态的sleep方法 {

    private static final Lock LOCK = new ReentrantLock();

    public static void main(String[] args) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                LOCK.lock();
                try {
                    System.out.println("A begin");
                    Thread.sleep(2000);
                    System.out.println("A End");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    LOCK.unlock();
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                LOCK.lock();
                try {
                    System.out.println("B begin");
                    Thread.sleep(2000);
                    System.out.println("B End");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    LOCK.unlock();
                }
            }
        });

        new Thread(thread).start();
        new Thread(thread2).start();

    }

}
