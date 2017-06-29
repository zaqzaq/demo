package cn.freshz.demo.thread.lock.redis;

/**
 * 常量表
 *
 * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
 * @date :2016-07-22 11:39:43
 */
public interface RConstants {
    /**
     * SaaS同步中心failover Redis 中的 Key
     *
     * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
     * @date :2016-07-22 11:39:43
     */
    public final static String KEY_FAILOVER_SYNC_CENTER="saas:failover:sync_center";
    /**
     * 默认的 自旋 周期时长 单位：毫秒
     */
    public final static long DEFAULT_LOCK_MS_PERIOD=100;
    /**
     * 默认的 锁 获取 的最大时间 单位：秒
     */
    public final static long DEFAULT_LOCK_S_MAX_ALIVE=10;
}
