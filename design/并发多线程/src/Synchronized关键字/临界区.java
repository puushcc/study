package Synchronized关键字;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2021年06月02日 13:32
 *
 * 所谓“临界区”，指的是某一块代码区域，
 * 它同一时刻只能由一个线程执行。在上面的例子中，如果synchronized关键字在方法上，那临界区就是整个方法内部。
 * 而如果是使用synchronized代码块，那临界区就指的是代码块内部的区域。
 */
public class 临界区 {

    /**
     * 下面这两个写法其实是等价的作用：
     */
    // 关键字在实例方法上，锁为当前实例
    public synchronized void instanceLock() {
        // code
    }

    // 关键字在代码块上，锁为括号里面的对象
    public void blockLock() {
        synchronized (this) {
            // code
        }
    }

    /**
     * 下面这两个写法其实是等价的作用：
     */

    // 关键字在静态方法上，锁为当前Class对象
    public static synchronized void classLock() {
        // code
    }

    // 关键字在代码块上，锁为括号里面的对象
    public void blockLock2() {
        synchronized (this.getClass()) {
            // code
        }
    }

}
