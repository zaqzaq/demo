import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
/**
* 解决  TicketLock 每次都要查询一个serviceNum 服务号，影响性能（必须要到主内存读取，并阻止其他cpu修改）。
*CLHLock 和MCSLock 则是两种类型相似的公平锁，采用链表的形式进行排序 
* 
* CLHlock是不停的查询前驱变量， 导致不适合在NUMA 架构下使用（在这种结构下，每个线程分布在不同的物理内存区域）
*/
public class CLHLock {
    public static class CLHNode {
        private volatile boolean isLocked = true;// 默认是在等待锁
    }

    @SuppressWarnings("unused")
    private volatile CLHNode                                           tail;
    private static final ThreadLocal<CLHNode>                          LOCAL   = new ThreadLocal<CLHNode>();
    private static final AtomicReferenceFieldUpdater<CLHLock, CLHNode> UPDATER = AtomicReferenceFieldUpdater.newUpdater(CLHLock.class,
                                                                                   CLHNode.class, "tail");

    public void lock() {
        CLHNode node = new CLHNode();
        LOCAL.set(node);
        CLHNode preNode = UPDATER.getAndSet(this, node);//  把this里的"tail" 值设置成currentThreadCLHNode
        if (preNode != null) {
            //已有线程占用了锁，进入自旋
            while (preNode.isLocked) {
            }
            preNode = null;
            LOCAL.set(node);
        }
    }

    public void unlock() {
        CLHNode node = LOCAL.get();
        if (!UPDATER.compareAndSet(this, node, null)) {
            node.isLocked = false;// 改变状态，让后续线程结束自旋
        }
         // 如果队列里只有当前线程，则释放对当前线程的引用（for GC）。
        node = null;
    }
}
