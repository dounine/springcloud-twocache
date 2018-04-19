package com.dounine.twocache.config;

import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

public class TwoLevelCacheManager extends RedisCacheManager {

    private String topic;
    private RedisTemplate<String,Object> redisTemplate;
    private Integer port;
    private String node = IpV4.get();

    public TwoLevelCacheManager(RedisTemplate<String,Object> redisTemplate,String topic, Integer port){
        super(redisTemplate);
        this.redisTemplate = redisTemplate;
        this.topic = topic;
        this.port = port;
    }

    @Override
    protected Cache decorateCache(Cache cache) {
        return new RedisAndLocalCache(this,(RedisCache) cache,node+":"+port);
    }

    protected void publishMessage(NotifyMsg notifyMsg){
        this.redisTemplate.convertAndSend(topic,notifyMsg);
    }

    public void receiver(byte[] body){
        NotifyMsg notifyMsg = (NotifyMsg)this.redisTemplate.getDefaultSerializer().deserialize(body);
        RedisAndLocalCache cache = (RedisAndLocalCache) this.getCache(notifyMsg.getCacheName());
        if(cache!=null){
            if(!notifyMsg.getNode().equals(node+":"+port)){
                if(notifyMsg.getNotifyType().equals(NotifyType.CLEAR)){
                    cache.clearLocal();
                }else if(notifyMsg.getNotifyType().equals(NotifyType.PUT)){
                    cache.put(notifyMsg.getKey(),notifyMsg.getResult());
                }else if(notifyMsg.getNotifyType().equals(NotifyType.EVICT)){
                    cache.evict(notifyMsg.getKey());
                }
            }else{
//                LOGGER.error("消息从自身发送,忽略处理");
            }
        }
    }
}
