package 线程中断;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2021年06月01日 18:29
 *
 * ● void interrupt（）方法 ：中断线程，
 * 例如，当线程A运行时，线程B可以调用线程A的interrupt（）方法来设置线程A的中断标志为true并立即返回。
 * 设置标志仅仅是设置标志，线程A实际并没有被中断，它会继续往下执行。
 * 如果线程A因为调用了wait系列函数、join方法或者sleep方法而被阻塞挂起，这时候若线程B调用线程A的interrupt（）方法，
 * 线程A会在调用这些方法的地方抛出InterruptedException异常而返回。
 *
 * ● boolean isInterrupted（）方法：检测当前线程是否被中断，如果是返回true，否则返回false。
 *
 * ● boolean interrupted（）方法：检测当前线程是否被中断，如果是返回true，否则返回false。
 * 与isInterrupted不同的是，该方法如果发现当前线程被中断，则会清除中断标志，并且该方法是static方法，可以通过Thread类直接调用。
 *
 */
public class 使用Interrupted优雅退出 {

    static class thread1 implements Runnable{

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()){
                    //如果线程被中断则退出循环
                    // to do some
                    System.out.println(Thread.currentThread() + "hello");
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                //
            }

        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new thread1());
        thread.start();
        Thread.sleep(1000);
        System.out.println("设置中断");
        //中断线程，
        thread.interrupt();
        thread.join();
        System.out.println("over");

    }

}
