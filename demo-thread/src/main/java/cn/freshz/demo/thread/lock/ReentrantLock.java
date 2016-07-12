package cn.freshz.demo.thread.lock;

/**
 * 模拟 可重入锁
 *
 * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
 * @date :2016-07-12 11:11:35
 */
public class ReentrantLock {

    boolean isLocked = false;
    Thread  lockedBy = null;
    int lockedCount = 0;
    public synchronized void lock()
            throws InterruptedException{
        Thread callingThread = Thread.currentThread();
        while(isLocked && lockedBy != callingThread){
            wait();
        }
        isLocked = true;
        lockedCount++;
        lockedBy = callingThread;
    }
    public synchronized void unlock(){
        if(Thread.currentThread() == this.lockedBy){
            lockedCount--;
            if(lockedCount == 0){
                isLocked = false;
                notify();
            }
        }
    }
}

