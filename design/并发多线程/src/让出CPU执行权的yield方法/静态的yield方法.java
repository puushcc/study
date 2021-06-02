package 让出CPU执行权的yield方法;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2021年06月01日 18:08
 */
public class 静态的yield方法 {

    /**
     * 当一个线程调用yield方法时，实际就是在暗示线程调度器当前线程请求让出自己的CPU使用，但是线程调度器可以无条件忽略这个暗示。
     * 当一个线程调用yield方法时，当前线程会让出CPU使用权，然后处于就绪状态，
     * 线程调度器会从线程就绪队列里面获取一个线程优先级最高的线程，
     * 当然也有可能会调度到刚刚让出CPU的那个线程来获取CPU执行权。
     */

    static class YieldTest implements Runnable{

        YieldTest(){
            //创建启动线程
            Thread thread = new Thread(this);
            thread.start();
        }

        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                if ((i%5)== 0 ){
                    System.out.println(Thread.currentThread()+"yield");
                    Thread.yield();
                }

                System.out.println(Thread.currentThread()+"over");
            }
        }
    }

    public static void main(String[] args) {
        new YieldTest();
        new YieldTest();
        new YieldTest();
    }

}
