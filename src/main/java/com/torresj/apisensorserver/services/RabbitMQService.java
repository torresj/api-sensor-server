package com.torresj.apisensorserver.services;

import org.springframework.stereotype.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class RabbitMQService {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(SensorService.class);

    public static final String RECEIVE_METHOD_NAME = "receiveMessage";

    public void messageProcessor(String msg) {
        logger.info("[RabbitMQService - MessageProcessor] Processing message");
    }

}