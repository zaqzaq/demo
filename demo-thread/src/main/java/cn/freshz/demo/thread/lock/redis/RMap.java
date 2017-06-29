package cn.freshz.demo.thread.lock.redis;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;


/**
 * 基于redis实现 的Map
 *
 * @param <V> the type parameter
 * @author :<a href="mailto:yingjiezhang@ebnew.com">章英杰</a>
 * @date :2016-07-22 10:55:19
 */
public class RMap<V> implements ConcurrentMap<String, V>, Serializable {

    /**
     * 构造或set方法传入
     */
    private RedisHelper redisHelper;

    private String redis_key;

    public RMap(String redis_key){
        this.redis_key=redis_key;
        //TODO 可设置系统默认的  bidRedis
    }
    public RMap(String redis_key,RedisHelper redisHelper){
        this.redis_key=redis_key;
        this.redisHelper=redisHelper;
    }

    public void setRedisClient(RedisHelper redisHelper){
        this.redisHelper=redisHelper;
    }

    @Override
    public int size() {
        return redisHelper.hlen(redis_key).intValue();
    }

    @Override
    public boolean isEmpty() {
        if(size()==0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public V get(Object key) {
        return (V)redisHelper.hget(redis_key,key.toString());
    }

    @Override
    public boolean containsKey(Object key) {
        return redisHelper.hexists(redis_key,key.toString());
    }

    @Override
    public V remove(Object key) {
        V v=get(key);
        if(null!=v){
            redisHelper.hdel(redis_key,key.toString());
        }
        return v;
    }

    @Override
    public boolean remove(Object key, Object value) {
        if (containsKey(key) && get(key).equals(value)) {
            remove(key);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public V put(String key, V value) {
        if(null!=key&&null!=value){
            redisHelper.hset(redis_key,key,value);
            return value;
        }
        return null;
    }

    @Override
    public void putAll(Map m) {
        if(!m.isEmpty()){
            redisHelper.hmset(redis_key,m);
        }
    }

    @Override
    public void clear() {
        redisHelper.del(redis_key);
    }

    @Override
    public Set<String> keySet() {
        return redisHelper.hkeys(redis_key);
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException(" Do not need to complicated ");
    }

    @Override
    public V putIfAbsent(String key, V value) {
        throw new UnsupportedOperationException(" Do not need to complicated ");
    }

    @Override
    public boolean replace(String key, V oldValue, V newValue) {
        throw new UnsupportedOperationException(" Do not need to complicated ");
    }

    @Override
    public V replace(String key, V value) {
        throw new UnsupportedOperationException(" Do not need to complicated ");
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException(" Do not need to complicated ");
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        throw new UnsupportedOperationException(" Do not need to complicated ");
    }
}
