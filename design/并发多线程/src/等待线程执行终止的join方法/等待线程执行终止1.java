package 等待线程执行终止的join方法;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2021年06月01日 17:06
 *
 *
 */
public class 等待线程执行终止1 {

    /**
     * 在项目实践中经常会遇到一个场景，就是需要等待某几件事情完成后才能继续往下执行，
     * 比如多个线程加载资源，需要等待多个线程全部加载完毕再汇总处理。Thread类中有一个join方法就可以做这个事情，
     * 前面介绍的等待通知方法是Object类中的方法，而join方法则是Thread类直接提供的
     *
     *join()方法是Thread类的一个实例方法。它的作用是让当前线程陷入“等待”状态，等join的这个线程执行完成后，再继续执行当前线程。
     *
     * 有时候，主线程创建并启动了子线程，如果子线程中需要进行大量的耗时运算，主线程往往将早于子线程结束之前结束。
     *
     * 如果主线程想等待子线程执行完毕后，获得子线程中的处理完的某个数据，就要用到join方法了。
     *
     */
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("1 over");
            }
        });
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("2 over");
            }
        });
        thread.start();
        thread1.start();
        System.out.println("======");
        thread.join();
        thread1.join();
        System.out.println("======");
        System.out.println("如果不加join方法，我会先被打出来，加了就不一样了");
    }

}
