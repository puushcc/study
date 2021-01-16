package org.example;

/**
 * @Description: 懒汉模式(线程安全)
 * 此种模式虽然是安全的，但由于把锁加到⽅法上后，所有的访问都因需要锁占⽤导致资源的浪费。
 * 如果不是特殊情况下，不建议此种⽅式实现单例模式。
 * @author: scott
 * @date: 2021年01月16日 15:57
 */
public class LazyMan_02 {

    private static LazyMan_02 instance;

    private LazyMan_02() {
    }

    public static synchronized LazyMan_02 getInstance(){
        if (null != instance){
            return instance;
        }
        instance = new LazyMan_02();
        return instance;
    }

}
