package cn.freshz.demo.thread.pool;


import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;

/**
 * 线程池 Thread pool.
 *
 * @author :<a href="mailto:zyj@frehsz.cn">章英杰</a>
 * @date :2016-06-07 16:07:54
 */
//@Component("threadPool")
public class ThreadPool {
    /**
     * 线程池大小 default size = 10
     */
//    @Value("thread.pool.size")
    private Integer poolSize;
    /**
     * 线程 生成  名称 编号 控制器
     */
    private Map<String, AtomicLong> threadNOMap;
    /**
     * 线程池
     */
    private ExecutorService executor;

    /**
     * 自定义线程名称 的 扩展Runnable
     *
     * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
     * @date :2016-06-27 16:05:26
     */
    private class  MyRunnable implements Runnable {
        /**
         * 线程 名称前缀
         *
         * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
         * @date :2016-06-27 16:05:22
         */
        private String threadNamePrefix;
        private Runnable runnable;

        public MyRunnable(Runnable runnable, String threadNamePrefix) {
            this.runnable = runnable;
            this.threadNamePrefix = threadNamePrefix;
        }

        public String getThreadNamePrefix() {
            return threadNamePrefix;
        }

        @Override
        public void run() {
            runnable.run();
        }
    }

    /**
     * 固定大小的的线程池
     * FIXME 为解决线程重用 而名称不改变BUG ,重写ThreadPoolExecutor 的 beforeExecute方法 修改线程的名称,线程重命名规则:thread-nyc-pool- + *
     *
     * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
     * @date :2016-06-27 14:33:22
     */
   private class MyFixedThreadPoolExecutor extends ThreadPoolExecutor {
        public MyFixedThreadPoolExecutor(int corePoolSize) {
            //模拟 Executors.newFixedThreadPool
            super(corePoolSize, corePoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        }

        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            if (r instanceof MyRunnable) {
                MyRunnable myRunnable=(MyRunnable) r;
                String threadNamePrefix=myRunnable.getThreadNamePrefix();
                if (!threadNOMap.containsKey(threadNamePrefix)) {
                    threadNOMap.put(threadNamePrefix, new AtomicLong());
                }
                t.setName("thread-nyc-pool-" + threadNamePrefix + "_" + threadNOMap.get(threadNamePrefix).incrementAndGet());
            } else {
                t.setName("thread-nyc-pool-" + r.hashCode());
            }
            super.beforeExecute(t, r);
        }
    }

    /**
     * 初始化线程池
     *
     * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
     * @date :2016-06-08 14:22:05
     */
    @PostConstruct
    private void init() {
        threadNOMap = new ConcurrentHashMap();
        //创建一个固定大小的线程池 线程名称自定义化
        executor = new MyFixedThreadPoolExecutor(poolSize);
    }

    /**
     * 执行需要自定义名称的线程
     *
     * @param runnable         the runnable
     * @param threadNamePrefix 自定义线程的名称
     * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
     * @date :2016-06-08 13:45:46
     */
    public void exec(Runnable runnable, String threadNamePrefix) {
        executor.execute(new MyRunnable(runnable,threadNamePrefix));
    }


    /**
     * 执行不需要自定义名称的线程
     *
     * @param runnable the runnable
     * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
     * @date :2016-06-08 13:47:32
     */
    public void exec(Runnable runnable) {
        executor.execute(runnable);
    }

}