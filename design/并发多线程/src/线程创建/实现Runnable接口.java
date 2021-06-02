package 线程创建;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2021年06月01日 16:04
 */
public class 实现Runnable接口 {

    public static class MyThread implements Runnable{

        @Override
        public void run() {
            System.out.println("使用实现Runnable接口的方式创建线程");
        }

    }

    public static void main(String[] args) {
        MyThread myThread = new MyThread();
        Thread thread = new Thread(myThread);
        thread.start();
        new Thread(myThread).start();
    }

}
