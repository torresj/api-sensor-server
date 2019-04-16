package com.torresj.apisensorserver.mqtt;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.MqttMessage;
import com.torresj.apisensorserver.models.Record;
import com.torresj.apisensorserver.models.Sensor;
import com.torresj.apisensorserver.services.RecordService;
import com.torresj.apisensorserver.services.SensorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqttConsumer {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(MqttConsumer.class);

    /* Messages Types */
    private static final String ERRORTYPE = "error";
    private static final String RECORDTYPE = "record";

    @Autowired
    private RecordService recordService;

    @Autowired
    private SensorService sensorService;

    public void messageHandler(String message) {
        try {
            logger.info("[MQTT - MESSAGE RECIVE] Message recive from mqtt server :" + message);

            ObjectMapper objectMapper = new ObjectMapper();
            MqttMessage mqttMsg = objectMapper.readValue(message, MqttMessage.class);
            switch (mqttMsg.getType()) {
            case ERRORTYPE:
                errorProcessor(mqttMsg);
                break;
            case RECORDTYPE:
                recordProcessor(message);
                break;
            default:
                logger.error("Type not supported");
            }

        } catch (IOException e) {
            logger.error(e);
            logger.info("Message not processed: " + message);
        }
    }

    private void recordProcessor(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Record record = objectMapper.readValue(message, Record.class);
            recordService.register(record);
        } catch (IOException e) {
            logger.error(e);
            logger.info("Message not processed: " + message);
        } catch (EntityNotFoundException e) {
            logger.error("[ERROR] Entity not found: " + message);
        }
    }

    private void errorProcessor(MqttMessage message) {
        try {
            Sensor sensor = sensorService.getSensor(message.getSensorId());
            logger.error("[ERROR] Error recive from sensor => " + sensor);
            logger.error(message);
        } catch (EntityNotFoundException e) {
            logger.error("[ERROR] Entity not found: " + message);
        }
    }
}