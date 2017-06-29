package cn.freshz.demo.thread.lock.redis;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;


/**
 * 基于redis 的分布式锁 实现 <不公平的实现>
 *
 * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
 * @date :2016-07-25 18:34:11
 */
public class RLock implements Lock , Serializable{
    /**
     * lock 值 的分隔符
     */
    private final static String SPLIT_VAL="_";
    /**
     * 生成本机UUID 实现可重入锁
     */
    private final static String UUID_LOCAL=UUID.randomUUID().toString();
    /**
     *lock key 生存的最大时间 防止死锁 单位：毫秒
     */
    private final long  LOCK_MS_MAX_ALIVE;
    /**
     * 自旋 周期时长 单位：毫秒
     */
    private final long LOCK_MS_PERIOD;
    /**
     * 构造或set方法传入
     */
    private RedisHelper redisHelper;

    /**
     * 上锁key的前缀
     */
    private String lock_redis_key="saas:lock:";


    public RLock(String redis_key){
        this.lock_redis_key+=redis_key;
        this.LOCK_MS_PERIOD=RConstants.DEFAULT_LOCK_MS_PERIOD;
        this.LOCK_MS_MAX_ALIVE=TimeUnit.MILLISECONDS.convert(RConstants.DEFAULT_LOCK_S_MAX_ALIVE,TimeUnit.SECONDS);
        //TODO 可设置系统默认的  bidRedis
    }
    public RLock(String redis_key,RedisHelper redisHelper){
        this.lock_redis_key+=redis_key;
        this.redisHelper=redisHelper;
        this.LOCK_MS_PERIOD=RConstants.DEFAULT_LOCK_MS_PERIOD;
        this.LOCK_MS_MAX_ALIVE=TimeUnit.MILLISECONDS.convert(RConstants.DEFAULT_LOCK_S_MAX_ALIVE,TimeUnit.SECONDS);
    }

    public RLock(String redis_key,RedisHelper redisHelper,long periodMS,long lockMaxS){
        this.lock_redis_key+=redis_key;
        this.redisHelper=redisHelper;
        this.LOCK_MS_PERIOD=periodMS;
        this.LOCK_MS_MAX_ALIVE=TimeUnit.MILLISECONDS.convert(lockMaxS,TimeUnit.SECONDS);
    }

    public void setRedisHelper(RedisHelper redisHelper){
        this.redisHelper=redisHelper;
    }


    @Override
    public boolean tryLock() {
        /*********************************************************************************************************
         * FIXME 当前应用的UUID + 线程的ID + (当前时间+超时时间)+0(第0次重入)
         *********************************************************************************************************/
        String newVal=UUID_LOCAL+SPLIT_VAL+Thread.currentThread().getId()+SPLIT_VAL+(System.currentTimeMillis()+LOCK_MS_MAX_ALIVE)+SPLIT_VAL+0;

        if(1==redisHelper.setnx(lock_redis_key,newVal)){
            return true;
        } else {
            String oldVal= redisHelper.get(lock_redis_key);
            if(null==oldVal){
                return false;
            }

            String[] oldVals= oldVal.split(SPLIT_VAL);
            if(oldVals.length!=4){
                return false;
            }
            /********************************************************************************
             * FIXME 1.超过锁住的LOCK_MS_MAX 最大时间
             *       2.自己设置的最新值 获取锁
             ********************************************************************************/
            if((System.currentTimeMillis()>Long.parseLong(oldVals[2]))
                            && oldVal.equals(redisHelper.getSet(lock_redis_key,newVal))){
                return true;
            }
            /************************************************************************************************
             *FIXME 当前线程 重入了  设置重入次数+1    本线程持有lock 故下面的操作 安全
             ************************************************************************************************/
            if((Long.parseLong(oldVals[1])==Thread.currentThread().getId())&&oldVals[0].equals(UUID_LOCAL)){
                redisHelper.set(lock_redis_key, incrReenterVal(oldVal));
                return true;
            }

            return false;
        }
    }

    /**
     * 生成新的重入lock的值 +1
     * @param oldVal
     * @return
     */
    private String incrReenterVal(String oldVal){
        return genReenterVal(oldVal,1);
    }
    /**
     * 生成新的重入lock的值 -1
     * @param oldVal
     * @return
     */
    private String decrReenterVal(String oldVal){
        return genReenterVal(oldVal,-1);
    }

    private String genReenterVal(String oldVal,int arg){
        return oldVal.substring(0,oldVal.length()-2)+(Long.parseLong(oldVal.substring(oldVal.length()-1))+arg);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        long timeNS= TimeUnit.NANOSECONDS.convert(time,unit);
        long startNS=System.nanoTime();

        while(!tryLock()){
            if(time>0&&System.nanoTime()-startNS>timeNS){
                return false;
            }
            //自旋
            TimeUnit.MILLISECONDS.sleep(LOCK_MS_PERIOD);
        }

        return true;
    }

    @Override
    public void lock() {
        try {
            lockInterruptibly();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        //无限等待直到获取锁
        tryLock(-1,TimeUnit.NANOSECONDS);
    }

    @Override
    public void unlock() {
        /************************************************************************************************************
         * FIXME    防止超时操作 清除了当前 其它获得 lock的线程
         ************************************************************************************************************/
        String nowVal= redisHelper.get(lock_redis_key);
        if(null==nowVal){
            return;
        }else{
            String[] nowVals= nowVal.split(SPLIT_VAL);

            /********************************************************************************************************************************
             * FIXME 1.在1000ms后将超时线程 不清除lock ，给定1秒钟 内 时间掇 的误差 ;
             *          防止 其它线程 在此时刻 因超时获取了锁 ，而下面的操作又清除了;TODO 这种情况下 key 在下次 获取lock之前 不会消失(无影响)
             *          2.只能释放当前线程所持有的lock
             ********************************************************************************************************************************/
            if(nowVals.length==4&&(System.currentTimeMillis()<Long.parseLong(nowVals[2])-1000)&&
                    (Long.parseLong(nowVals[1])==Thread.currentThread().getId())&&nowVals[0].equals(UUID_LOCAL)){

                if(Long.parseLong(nowVals[3])<=0){
                    //清除lock
                    redisHelper.del(lock_redis_key);
                }else{
                    /********************************
                     * FIXME 重入 锁 -1
                     ********************************/
                    redisHelper.set(lock_redis_key,decrReenterVal(nowVal));
                }

            }
        }

    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException(" Do not need to complicated ");
    }

}
