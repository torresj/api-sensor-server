package com.torresj.apisensorserver.mqtt;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.Record;
import com.torresj.apisensorserver.services.RecordService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqttConsumer {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(MqttConsumer.class);

    @Autowired
    private RecordService recordService;

    public void messageHandler(String message) {
        try {
            logger.info("[MQTT - MESSAGE RECIVE] Message recive from mqtt server :" + message);

            ObjectMapper objectMapper = new ObjectMapper();
            Record record = objectMapper.readValue(message, Record.class);
            recordService.register(record);

        } catch (IOException e) {
            logger.info("Message not processed: " + message);
        } catch (EntityNotFoundException e) {
            logger.error("Error registering record: " + message, e);
        }
    }

}