package com.dounine.twocache.config;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.data.redis.cache.RedisCache;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class RedisAndLocalCache implements Cache {

    private ConcurrentHashMap<Object,ValueWrapper> local = new ConcurrentHashMap<>();
    private RedisCache redisCache;
    private TwoLevelCacheManager cacheManager;
    private String node;

    public RedisAndLocalCache(TwoLevelCacheManager twoLevelCacheManager,RedisCache redisCache,String node){
        this.cacheManager = twoLevelCacheManager;
        this.redisCache = redisCache;
        this.node = node;
    }

    @Override
    public String getName() {
        return redisCache.getName();
    }

    @Override
    public Object getNativeCache() {
        return redisCache.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper valueWrapper = local.get(key);
        if(valueWrapper!=null){
            return valueWrapper;
        }else{
            valueWrapper = redisCache.get(key);
            if(valueWrapper!=null){
                local.put(key,valueWrapper);
            }
            return valueWrapper;
        }
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        ValueWrapper valueWrapper = local.get(key);
        if(valueWrapper!=null){
            return (T)valueWrapper.get();
        }else{
            valueWrapper = redisCache.get(key);
            if(valueWrapper!=null){
                local.put(key,valueWrapper);
            }
            return (T)valueWrapper.get();
        }
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return null;
    }

    @Override
    public void put(Object key, Object value) {
        this.local.put(key,new SimpleValueWrapper(value));
        this.redisCache.put(key,value);
        this.notifyNodes(new NotifyMsg(NotifyType.PUT,node,key,value));
    }

    private void notifyNodes(NotifyMsg notifyType){
        notifyType.setCacheName(redisCache.getName());
        cacheManager.publishMessage(notifyType);
    }


    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return null;
    }

    @Override
    public void evict(Object key) {
        redisCache.evict(key);
        this.notifyNodes(new NotifyMsg(NotifyType.EVICT,node,key,null));
    }

    public void clearLocal(){
        local.clear();
    }

    @Override
    public void clear() {
        redisCache.clear();
        this.notifyNodes(new NotifyMsg(NotifyType.CLEAR,node,null,null));
    }
}
