package cn.freshz.demo.thread.lock;

import java.util.concurrent.atomic.AtomicReference;

/**
* 最粗糙的 自旋锁
*/
public class SpinLock {

  private AtomicReference<Thread> sign =new AtomicReference<>();

  public void lock(){
    Thread current = Thread.currentThread();
    // 如果锁未被占用，则设置当前线程为锁的拥有者
    while(!sign .compareAndSet(null, current)){
    }
  }

  public void unlock (){
    Thread current = Thread.currentThread();
     // 只有锁的拥有者才能释放锁
    sign .compareAndSet(current, null);
  }
}
