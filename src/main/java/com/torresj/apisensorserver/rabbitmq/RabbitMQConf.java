package com.torresj.apisensorserver.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
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
  DirectExchange exchange() {
    return new DirectExchange(exchange);
  }

  @Bean
  Binding binding(Queue bqueue, DirectExchange bexchange) {
    return BindingBuilder.bind(bqueue).to(bexchange).with(routingKey);
  }

  @Bean
  public MessageConverter jsonMessageCnverter() {
    return new Jackson2JsonMessageConverter();
  }
}