package com.zero9platform.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

    /**
     * Redis 연결 설정
     */
    @Value("${spring.data.redis.host:localhost}") // 값이 없으면 localhost 사용
    private String host;

    @Value("${spring.data.redis.port:6379}")
    private int port;

    /**
     * Redis 연결 설정
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // application.yml 에서 spring.redis.* 설정을 자동 사용
        return new LettuceConnectionFactory(host, port); // 비동기 커넥션
    }

    /**
     * Key/Value를 String 고정으로 쓰는 도구 (직렬화 직접 설정 X)
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
}