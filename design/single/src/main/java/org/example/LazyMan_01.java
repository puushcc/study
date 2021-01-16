package org.example;

/**
 * @Description: 懒汉模式(线程不安全)
 * @author:
 * @date: 2021年01月16日 15:27
 */
public class LazyMan_01 {

    private static LazyMan_01 instance;

    private LazyMan_01(){

    }

    public static LazyMan_01 getInstance(){
        if (null != instance ){
            return instance;
        }
        instance = new LazyMan_01();
        return instance;
    }


}
