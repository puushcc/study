package 线程创建;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2021年06月01日 15:58
 */
public class 继承Thread类 {

    public static class MyThread extends Thread {

        @Override
        public void run(){
            System.out.println("使用继承Thread的方式创建线程");
        }
    }

    public static void main(String[] args) {
        MyThread myThread = new MyThread();
        myThread.start();
    }

}
