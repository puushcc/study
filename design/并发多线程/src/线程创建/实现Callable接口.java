package 线程创建;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2021年06月01日 16:10
 */
public class 实现Callable接口 {

    public static class MyCallable implements Callable<String>{

        @Override
        public String call() throws Exception {
            System.out.println("使用实现Callable接口的方式创建线程");
            return "success";
        }
    }

    public static void main(String[] args) throws InterruptedException {
        FutureTask<String> stringFutureTask = new FutureTask<>(new MyCallable());
        new Thread(stringFutureTask).start();
        try {
            String s = stringFutureTask.get();
            System.out.println(s);
        }catch (ExecutionException e){
            e.printStackTrace();
        }
    }
}
