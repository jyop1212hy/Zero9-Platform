package com.zero9platform.common.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // 인프라 식별자 정의
    public static final String FEED_EXCHANGE = "activity.feed.exchange";
    public static final String FEED_QUEUE = "activity.feed.queue";
    public static final String FEED_ROUTING_KEY = "activity.feed.routing";

    /**
     * TopicExchange: Routing Key 패턴 매칭을 통해 메시지를 배정하는 Exchange
     */
    @Bean
    public TopicExchange feedExchange() {
        return new TopicExchange(FEED_EXCHANGE);
    }

    /**
     * Queue: 메시지 영속성(durable)을 보장하는 저장소
     */
    @Bean
    public Queue feedQueue() {
        return new Queue(FEED_QUEUE, true);
    }

    /**
     * Binding: Exchange와 Queue를 특정 Routing Key로 매핑
     */
    @Bean
    public Binding feedBinding(Queue feedQueue, TopicExchange feedExchange) {
        return BindingBuilder.bind(feedQueue).to(feedExchange).with(FEED_ROUTING_KEY);
    }

    /**
     * Message 객체를 JSON 문자열로 직렬화/역직렬화하기 위한 컨버터
     */
    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}