package com.torresj.apisensorserver.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Component
public class Producer {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(Producer.class);

    @Autowired
    private RabbitMessagingTemplate rabbitMessagingTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routingkey}")
    private String routingKey;

    public void produceMsg(String msg) {
        logger.info("[RabbitMQ - Producer] Sending message: " + msg);
        rabbitMessagingTemplate.convertAndSend(exchange, routingKey, msg);
    }
}