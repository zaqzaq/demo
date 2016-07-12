package cn.freshz.demo.thread.lock;

/**
 * 锁测试
 *
 * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
 * @date :2016-07-12 11:12:13
 */
public class LockTest{
//    UnReentrantLock lock = new UnReentrantLock();
    ReentrantLock lock = new ReentrantLock();
    public void outer() throws InterruptedException {
        lock.lock();
        inner();
        lock.unlock();
    }
    public void inner() throws InterruptedException {
        lock.lock();
        //do something
        System.out.println("inner runing");
        lock.unlock();
    }

    public static void main(String[] args) throws InterruptedException {
        LockTest lockTest=new LockTest();
        lockTest.outer();
    }
}