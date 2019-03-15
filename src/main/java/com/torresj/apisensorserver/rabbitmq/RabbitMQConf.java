package com.torresj.apisensorserver.rabbitmq;

import com.torresj.apisensorserver.services.RabbitMQService;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RabbitMQConf {
    private static final boolean IS_DURABLE_QUEUE = false;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routingkey}")
    private String routingKey;

    @Value("${rabbitmq.queue}")
    private String queue;

    @Bean
    Queue queue() {
        return new Queue(queue, IS_DURABLE_QUEUE);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    Binding binding(Queue bqueue, TopicExchange bexchange) {
        return BindingBuilder.bind(bqueue).to(bexchange).with(routingKey);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter) {
        final SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queue);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    RabbitMQService receiver() {
        return new RabbitMQService();
    }

    @Bean
    MessageListenerAdapter listenerAdapter(RabbitMQService receiver) {
        return new MessageListenerAdapter(receiver, RabbitMQService.RECEIVE_METHOD_NAME);
    }
}