package com.dounine.twocache.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.io.UnsupportedEncodingException;

@Configuration
@ConditionalOnMissingBean(CacheManager.class)
@ConditionalOnBean({RedisTemplate.class})
@ConditionalOnProperty(name = "twocache.enable",havingValue = "true")
@EnableCaching
public class TwoCacheConfig {

    @Value("${twocache.redis.topic:towcache}")
    private String topic;
    @Value("${server.port}")
    private Integer port;

    @Bean
    @ConditionalOnMissingBean(JedisConnectionFactory.class)
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter,new PatternTopic(topic));
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(final TwoLevelCacheManager cacheManager){
        return new MessageListenerAdapter(new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                try {
                    String topic = new String(message.getChannel(),"utf-8");
                    cacheManager.receiver(message.getBody());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Bean
    public TwoLevelCacheManager cacheManager(RedisTemplate redisTemplate){
        return new TwoLevelCacheManager(redisTemplate,topic,port);
    }
}
