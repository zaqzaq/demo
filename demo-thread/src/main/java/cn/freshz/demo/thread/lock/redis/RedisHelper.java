package cn.freshz.demo.thread.lock.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bidlink.framework.redis.config.ShardedRedisPool;
import cn.bidlink.framework.redis.serializer.SerializerFactory;
import cn.bidlink.framework.redis.utils.EmptyUtils;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.SafeEncoder;

/**
 * Redis 扩展帮助类
 * ************************************
 * FIXME 垃圾代码不要看
 * ************************************
 *
 * @date :2016-07-26 14:34:13
 */
public class RedisHelper {
    private Logger logger = Logger.getLogger(getClass());
    /**
     * 注入连接池
     *
     * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
     * @date :2016-07-26 14:34:13
     */
    @Autowired
    private ShardedRedisPool shardedJedisPool;

    /**
     * 操作hash类型的数据 原子+1
     *
     * @param key   the key
     * @param field the field
     * @return 执行 HINCRBY 命令之后，哈希表 key 中域 field 的值。
     * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
     * @date :2016-07-26 14:34:13
     */
    public Long hincr(String key, final String field){
        if (EmptyUtils.isEmpty(key)) {
            logger.error("Key can not be empty!");
            return null;
        }
        if (EmptyUtils.isEmpty(field)) {
            logger.error("field can not be empty!");
            return null;
        }

        Long begin = System.currentTimeMillis();
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = getResource();
            if (logger.isInfoEnabled()) {
                logger.debug("Redis hincr (" + key + "):" + shardedJedis.getShardInfo(key));
            }
            return shardedJedis.hincrBy(key,field,1);
        } catch (JedisConnectionException jce) {
            returnBrokenResource(shardedJedis);
        } catch (Exception e) {
            logger.error("Redis hincr", e);
        } finally {
            returnResource(shardedJedis);
            logger.debug("Redis hincr (" + key + ") took " + (System.currentTimeMillis() - begin) + " ms");
        }
        return null;
    }

    public String set(String key,String value){
        if (EmptyUtils.isEmpty(key)) {
            logger.error("Key can not be empty!");
            return null;
        }

        Long begin = System.currentTimeMillis();
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = getResource();
            if (logger.isInfoEnabled()) {
                logger.debug("Redis set (" + key + "):" + shardedJedis.getShardInfo(key));
            }
            return shardedJedis.set(key,value);
        } catch (JedisConnectionException jce) {
            returnBrokenResource(shardedJedis);
        } catch (Exception e) {
            logger.error("Redis set", e);
        } finally {
            returnResource(shardedJedis);
            logger.debug("Redis set (" + key + ") took " + (System.currentTimeMillis() - begin) + " ms");
        }

        return null;
    }

    /**
     * 如果key 不存在则 设置成value
     *
     * @param key   the key
     * @param value the value
     * @return 设置成功，返回 1 。
                设置失败，返回 0 。
                返回基本类型 解决空指针异常
     * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
     * @date :2016-07-26 14:34:13
     */
    public long setnx(String key,String value){
        if (EmptyUtils.isEmpty(key)) {
            logger.error("Key can not be empty!");
            return 0;
        }

        Long begin = System.currentTimeMillis();
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = getResource();
            if (logger.isInfoEnabled()) {
                logger.debug("Redis setnx (" + key + "):" + shardedJedis.getShardInfo(key));
            }
            return shardedJedis.setnx(key,value);
        } catch (JedisConnectionException jce) {
            returnBrokenResource(shardedJedis);
        } catch (Exception e) {
            logger.error("Redis setnx", e);
        } finally {
            returnResource(shardedJedis);
            logger.debug("Redis setnx (" + key + ") took " + (System.currentTimeMillis() - begin) + " ms");
        }

        return 0;
    }

    /**
     * 获取 原值 并设置 新值 为value
     *
     * @param key   the key
     * @param value the value
     * @return 原值
     * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
     * @date :2016-07-26 14:34:13
     */
    public String getSet(String key ,String value){
        if (EmptyUtils.isEmpty(key)) {
            logger.error("Key can not be empty!");
            return null;
        }

        Long begin = System.currentTimeMillis();
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = getResource();
            if (logger.isInfoEnabled()) {
                logger.debug("Redis getSet (" + key + "):" + shardedJedis.getShardInfo(key));
            }
            return shardedJedis.getSet(key,value);
        } catch (JedisConnectionException jce) {
            returnBrokenResource(shardedJedis);
        } catch (Exception e) {
            logger.error("Redis getSet", e);
        } finally {
            returnResource(shardedJedis);
            logger.debug("Redis getSet (" + key + ") took " + (System.currentTimeMillis() - begin) + " ms");
        }

        return null;
    }

    /**
     * 获取值
     *
     * @param key the key
     * @return the string
     * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
     * @date :2016-07-26 14:34:13
     */
    public String get(String key ){
        if (EmptyUtils.isEmpty(key)) {
            logger.error("Key can not be empty!");
            return null;
        }

        Long begin = System.currentTimeMillis();
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = getResource();
            if (logger.isInfoEnabled()) {
                logger.debug("Redis get (" + key + "):" + shardedJedis.getShardInfo(key));
            }
            return shardedJedis.get(key);
        } catch (JedisConnectionException jce) {
            returnBrokenResource(shardedJedis);
        } catch (Exception e) {
            logger.error("Redis get", e);
        } finally {
            returnResource(shardedJedis);
            logger.debug("Redis get (" + key + ") took " + (System.currentTimeMillis() - begin) + " ms");
        }

        return null;
    }

    /**
     * 删除key
     *
     * @param key the key
     * @return 被删除 key 的数量。 :key存在返回1 ，不存在返回0
     * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
     * @date :2016-07-26 14:34:13
     */
    public Long del(String key ){
        if (EmptyUtils.isEmpty(key)) {
            logger.error("Key can not be empty!");
            return null;
        }

        Long begin = System.currentTimeMillis();
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = getResource();
            if (logger.isInfoEnabled()) {
                logger.debug("Redis del (" + key + "):" + shardedJedis.getShardInfo(key));
            }
            return shardedJedis.del(key);
        } catch (JedisConnectionException jce) {
            returnBrokenResource(shardedJedis);
        } catch (Exception e) {
            logger.error("Redis del", e);
        } finally {
            returnResource(shardedJedis);
            logger.debug("Redis del (" + key + ") took " + (System.currentTimeMillis() - begin) + " ms");
        }

        return null;
    }

    /**
     * 获取key的有效生存时间
     *
     * @param key the key
     * @return the long
     * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
     * @date :2016-07-26 15:08:26
     */
    public Long ttl(String key) {
        if (EmptyUtils.isEmpty(key)) {
            logger.error("Key can not be empty!");
            return 0L;
        }

        Long begin = System.currentTimeMillis();
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = getResource();
            if (logger.isInfoEnabled()) {
                logger.debug("Redis ttl (" + key + "):" + shardedJedis.getShardInfo(key));
            }
            return shardedJedis.ttl(SafeEncoder.encode(key));
        } catch (JedisConnectionException jce) {
            returnBrokenResource(shardedJedis);
        } catch (Exception e) {
            logger.error("Redis ttl", e);
        } finally {
            returnResource(shardedJedis);
            logger.debug("Redis ttl (" + key + ") took " + (System.currentTimeMillis() - begin) + " ms");
        }
        return 0L;
    }

    /**
     * 用key 设置过期时间
     * @param key
     * @param seconds 单位:秒
     * @return 如果生存时间设置成功，返回 1 。
                当 key 不存在或没办法设置生存时间，返回 0 。
     */
    public Long expire(String key, int seconds) {
        if (EmptyUtils.isEmpty(key)) {
            logger.error("Key can not be empty!");
            return 0L;
        }

        Long begin = System.currentTimeMillis();
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = getResource();
            if (logger.isInfoEnabled()) {
                logger.debug("Redis expire (" + key + "):" + shardedJedis.getShardInfo(key));
            }
            return shardedJedis.expire(key,seconds);
        } catch (JedisConnectionException jce) {
            returnBrokenResource(shardedJedis);
        } catch (Exception e) {
            logger.error("Redis expire", e);
        } finally {
            returnResource(shardedJedis);
            logger.debug("Redis expire (" + key + ") took " + (System.currentTimeMillis() - begin) + " ms");
        }
        return 0L;
    }

    /**
     * 原子性的 操作key 的value +1
     *
     * @param key the key
     * @return +1后的值
     * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
     * @date :2016-07-26 17:08:36
     */
    public Long incr(String key) {
        if (EmptyUtils.isEmpty(key)) {
            logger.error("Key can not be empty!");
            return null;
        }

        Long begin = System.currentTimeMillis();
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = getResource();
            if (logger.isInfoEnabled()) {
                logger.debug("Redis incr (" + key + "):" + shardedJedis.getShardInfo(key));
            }
            return shardedJedis.incr(key);
        } catch (JedisConnectionException jce) {
            returnBrokenResource(shardedJedis);
        } catch (Exception e) {
            logger.error("Redis incr", e);
        } finally {
            returnResource(shardedJedis);
            logger.debug("Redis incr (" + key + ") took " + (System.currentTimeMillis() - begin) + " ms");
        }
        return null;
    }

    /**
     * 原子性的 操作key 的value -1
     *
     * @param key the key
     * @return -1后的值
     * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
     * @date :2016-07-26 17:29:29
     */
    public Long decr(String key) {
        if (EmptyUtils.isEmpty(key)) {
            logger.error("Key can not be empty!");
            return null;
        }

        Long begin = System.currentTimeMillis();
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = getResource();
            if (logger.isInfoEnabled()) {
                logger.debug("Redis decr (" + key + "):" + shardedJedis.getShardInfo(key));
            }
            return shardedJedis.decr(key);
        } catch (JedisConnectionException jce) {
            returnBrokenResource(shardedJedis);
        } catch (Exception e) {
            logger.error("Redis decr", e);
        } finally {
            returnResource(shardedJedis);
            logger.debug("Redis decr (" + key + ") took " + (System.currentTimeMillis() - begin) + " ms");
        }
        return null;
    }

    /**
     * 往Hash 中 放置 新元素
     * @param key
     * @param field
     * @param value
     * @return
     */
    public Long hset(String key, final String field, final Object value) {
        if (EmptyUtils.isEmpty(key)) {
            logger.debug("Key can not be empty!");
            return null;
        }
        if (EmptyUtils.isEmpty(field)) {
            logger.debug("Field can not be empty!");
            return null;
        }
        if (EmptyUtils.isEmpty(value)) {
            logger.debug("Value can not be empty!");
            return null;
        }

        Long start = System.currentTimeMillis();
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = getResource();
            if (logger.isInfoEnabled()) {
                logger.debug("Redis hset (" + key + "):" + shardedJedis.getShardInfo(key));
            }

            return shardedJedis.hset(SafeEncoder.encode(key), SafeEncoder.encode(field), serialize(value));
        } catch (JedisConnectionException jce) {
            returnBrokenResource(shardedJedis);
        } catch (Exception e) {
            logger.error("Redis hset", e);
        } finally {
            returnResource(shardedJedis);
            logger.debug("Redis hset (" + key + ") took " + (System.currentTimeMillis() - start) + " ms");
        }
        return null;
    }

    /**
     * 往 批量 Hash 中 放置 新元素
     * @param key
     * @return
     */
    public Boolean hmset(String key, final Map<String, Object> map) {
        if (EmptyUtils.isEmpty(key)) {
            logger.debug("Key can not be empty!");
            return false;
        }
        if (EmptyUtils.isEmpty(map)) {
            logger.debug("Map can not be empty!");
            return false;
        }

        Long start = System.currentTimeMillis();
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = getResource();
            if (logger.isInfoEnabled()) {
                logger.debug("Redis hmset (" + key + "):" + shardedJedis.getShardInfo(key));
            }

            Map<byte[], byte[]> hash = new HashMap<byte[], byte[]>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                hash.put(SafeEncoder.encode(entry.getKey()), serialize(entry.getValue()));
            }
            shardedJedis.hmset(SafeEncoder.encode(key), hash);
            return true;
        } catch (JedisConnectionException jce) {
            returnBrokenResource(shardedJedis);
        } catch (Exception e) {
            logger.error("Redis hmset", e);
        } finally {
            returnResource(shardedJedis);
            logger.debug("Redis hmset (" + key + ") took " + (System.currentTimeMillis() - start) + " ms");
        }
        return false;
    }

    /**
     * 获取Hash 中的元素field 的值 值为对象类型
     * @param key
     * @param field
     * @return
     */
    public Object hget(String key, final String field) {
        if (EmptyUtils.isEmpty(key)) {
            logger.debug("Key can not be empty!");
            return null;
        }
        if (EmptyUtils.isEmpty(field)) {
            logger.debug("Field can not be empty!");
            return null;
        }

        Long start = System.currentTimeMillis();
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = getResource();
            if (logger.isInfoEnabled()) {
                logger.debug("Redis hget (" + key + "):" + shardedJedis.getShardInfo(key));
            }
            return deserialize(shardedJedis.hget(SafeEncoder.encode(key), SafeEncoder.encode(field)));
        } catch (JedisConnectionException jce) {
            returnBrokenResource(shardedJedis);
        } catch (Exception e) {
            logger.error("Redis hget", e);
        } finally {
            returnResource(shardedJedis);
            logger.debug("Redis hget (" + key + ") took " + (System.currentTimeMillis() - start) + " ms");
        }
        return null;
    }

    /**
     * 获取Hash 的所有元素  值为对象类型
     * @param key
     * @return
     */
    public Map<String, Object> hgetAll(String key) {
        if (EmptyUtils.isEmpty(key)) {
            logger.debug("Key can not be empty!");
            return null;
        }

        Long start = System.currentTimeMillis();
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = getResource();
            if (logger.isInfoEnabled()) {
                logger.debug("Redis hgetAll (" + key + "):" + shardedJedis.getShardInfo(key));
            }
            Map<byte[], byte[]> bMap = shardedJedis.hgetAll(SafeEncoder.encode(key));
            if (EmptyUtils.isEmpty(bMap)) {
                return null;
            }
            Map<String, Object> map = new HashMap<String, Object>(bMap.size());
            for (Map.Entry<byte[], byte[]> entry : bMap.entrySet()) {
                map.put(SafeEncoder.encode(entry.getKey()), deserialize(entry.getValue()));
            }
            return map;
        } catch (JedisConnectionException jce) {
            returnBrokenResource(shardedJedis);
        } catch (Exception e) {
            logger.error("Redis hgetAll", e);
        } finally {
            returnResource(shardedJedis);
            logger.debug("Redis hgetAll (" + key + ") took " + (System.currentTimeMillis() - start) + " ms");
        }
        return null;
    }

    /**
     * 判断Hash 中是否存在在field元素
     * @param key
     * @param field
     * @return
     */
    public Boolean hexists(String key, final String field) {
        if (EmptyUtils.isEmpty(key)) {
            logger.debug("Key can not be empty!");
            return false;
        }
        if (EmptyUtils.isEmpty(field)) {
            logger.debug("Field can not be empty!");
            return false;
        }

        Long start = System.currentTimeMillis();
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = getResource();
            if (logger.isInfoEnabled()) {
                logger.debug("Redis hexists (" + key + "):" + shardedJedis.getShardInfo(key));
            }
            return shardedJedis.hexists(SafeEncoder.encode(key), SafeEncoder.encode(field));
        } catch (JedisConnectionException jce) {
            returnBrokenResource(shardedJedis);
        } catch (Exception e) {
            logger.error("Redis hexists", e);
        } finally {
            returnResource(shardedJedis);
            logger.debug("Redis hexists (" + key + ") took " + (System.currentTimeMillis() - start) + " ms");
        }
        return false;
    }

    /**
     * 批量删除Hash 中的元素
     * @param key
     * @param fields
     * @return 删除的个数
     */
    public Long hdel(String key, String... fields) {
        if (EmptyUtils.isEmpty(key)) {
            logger.debug("Key can not be empty!");
            return 0L;
        }
        if (EmptyUtils.isEmpty(fields)) {
            logger.debug("Fields can not be empty!");
            return 0L;
        }

        Long start = System.currentTimeMillis();
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = getResource();
            if (logger.isInfoEnabled()) {
                logger.debug("Redis hdel (" + key + "):" + shardedJedis.getShardInfo(key));
            }
            final byte[][] bfields = new byte[fields.length][];
            for (int i = 0; i < bfields.length; i++) {
                bfields[i] = SafeEncoder.encode(fields[i]);
            }
            return shardedJedis.hdel(SafeEncoder.encode(key), bfields);
        } catch (JedisConnectionException jce) {
            returnBrokenResource(shardedJedis);
        } catch (Exception e) {
            logger.error("Redis hdel", e);
        } finally {
            returnResource(shardedJedis);
            logger.debug("Redis hdel (" + key + ") took " + (System.currentTimeMillis() - start) + " ms");
        }
        return 0L;
    }

    /**
     * 获取Hash 的元素个数
     * @param key
     * @return
     */
    public Long hlen(String key) {
        if (EmptyUtils.isEmpty(key)) {
            logger.debug("Key can not be empty!");
            return 0L;
        }

        Long start = System.currentTimeMillis();
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = getResource();
            if (logger.isInfoEnabled()) {
                logger.debug("Redis hlen (" + key + "):" + shardedJedis.getShardInfo(key));
            }
            return shardedJedis.hlen(SafeEncoder.encode(key));
        } catch (JedisConnectionException jce) {
            returnBrokenResource(shardedJedis);
        } catch (Exception e) {
            logger.error("Redis hlen", e);
        } finally {
            returnResource(shardedJedis);
            logger.debug("Redis hlen (" + key + ") took " + (System.currentTimeMillis() - start) + " ms");
        }
        return 0L;
    }

    /**
     * 获取Hash　中所有 的key
     * @param key
     * @return
     */
    public Set<String> hkeys(String key) {
        if (EmptyUtils.isEmpty(key)) {
            logger.debug("Key can not be empty!");
            return null;
        }

        Long start = System.currentTimeMillis();
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = getResource();
            if (logger.isInfoEnabled()) {
                logger.debug("Redis hkeys (" + key + "):" + shardedJedis.getShardInfo(key));
            }
            return shardedJedis.hkeys(key);
        } catch (JedisConnectionException jce) {
            returnBrokenResource(shardedJedis);
        } catch (Exception e) {
            logger.error("Redis hkeys", e);
        } finally {
            returnResource(shardedJedis);
            logger.debug("Redis hkeys (" + key + ") took " + (System.currentTimeMillis() - start) + " ms");
        }
        return null;
    }

    /**
     * Gets resource.
     *
     * @return the resource
     * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
     * @date :2016-07-26 14:34:13
     */
    private ShardedJedis getResource() {
        try {
            return shardedJedisPool.getResource();
        } catch (Exception e) {
            logger.error("getResource", e);
            throw new JedisConnectionException(e);
        }
    }

    /**
     * Return resource.
     *
     * @param shardedJedis the sharded jedis
     * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
     * @date :2016-07-26 14:34:13
     */
    private void returnResource(ShardedJedis shardedJedis) {
        if (null != shardedJedis) {
            try {
                shardedJedisPool.returnResourceObject(shardedJedis);
            } catch (Exception e) {
                logger.error("returnResource", e);
            }
        }
    }

    /**
     * Return broken resource.
     *
     * @param shardedJedis the sharded jedis
     * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
     * @date :2016-07-26 14:34:13
     */
    private void returnBrokenResource(ShardedJedis shardedJedis) {
        if (null != shardedJedis) {
            try {
                shardedJedisPool.returnBrokenResourceObject(shardedJedis);
            } catch (Exception e) {
                logger.error("returnBrokenResource", e);
            } finally {
                shardedJedis = null;
            }
        }
    }

    private byte[] serialize(Object obj) {
        return SerializerFactory.serialize(obj);
    }

    private Object deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return SerializerFactory.deserialize(bytes);
    }

}
