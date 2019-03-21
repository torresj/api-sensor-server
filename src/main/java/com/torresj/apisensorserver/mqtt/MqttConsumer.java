package com.torresj.apisensorserver.mqtt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class MqttConsumer {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(MqttConsumer.class);

    public void messageHandler(String message) {
        System.out.println(message);
    }

}