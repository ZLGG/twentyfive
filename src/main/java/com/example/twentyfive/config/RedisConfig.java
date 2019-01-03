package com.example.twentyfive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {
    @Bean
    public JedisPool getJedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(20);
        poolConfig.setMaxTotal(30);
        poolConfig.setTestOnBorrow(false);
        poolConfig.setTestOnReturn(false);
        poolConfig.setMaxWaitMillis(3000);
        //JedisPool jedisPool = new JedisPool(poolConfig, "localhost", 6379,3000,"root",0);
        JedisPool jedisPool = new JedisPool(poolConfig, "127.0.0.1", 6379);
        return jedisPool;
    }

}
