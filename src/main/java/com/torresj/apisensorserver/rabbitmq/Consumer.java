package com.torresj.apisensorserver.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Component
public class Consumer {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(Consumer.class);

    @RabbitListener(queues = "server.queue")
    public void onMessageFromRabbitMQ(final String messageFromRabbitMQ) {
        logger.info("Mensaje recibido: " + messageFromRabbitMQ);
    }
}