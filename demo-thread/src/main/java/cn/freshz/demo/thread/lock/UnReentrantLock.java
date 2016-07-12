package cn.freshz.demo.thread.lock;

/**
 * 模拟 不可重入锁
 *
 * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
 * @date :2016-07-12 11:12:02
 */
public class UnReentrantLock {
    private boolean isLocked = false;
    public synchronized void lock() throws InterruptedException{
        while(isLocked){    //不用if，而用while，是为了防止假唤醒
            wait();
        }
        isLocked = true;
    }
    public synchronized void unlock(){
        isLocked = false;
        notify();
    }
}