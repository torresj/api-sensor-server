package com.torresj.apisensorserver.mqtt;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.MqttMessage;
import com.torresj.apisensorserver.models.entities.Record;
import com.torresj.apisensorserver.models.entities.Sensor;
import com.torresj.apisensorserver.services.RecordService;
import com.torresj.apisensorserver.services.SensorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class MqttConsumer {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(MqttConsumer.class);

    /* Messages Types */
    private static final String ERRORTYPE = "error";

    private static final String RECORDTYPE = "record";

    private RecordService recordService;

    private SensorService sensorService;

    private ObjectMapper objectMapper;

    public MqttConsumer(RecordService recordService, SensorService sensorService,
            ObjectMapper objectMapper) {
        this.recordService = recordService;
        this.sensorService = sensorService;
        this.objectMapper = objectMapper;

        objectMapper.registerModule(new JavaTimeModule());
    }

    public void messageHandler(String message) {
        try {
            logger.info("[MQTT - MESSAGE RECEIVE] Message receive from mqtt server: {}", message);

            MqttMessage mqttMsg = objectMapper.readValue(message, MqttMessage.class);
            switch (mqttMsg.getType()) {
            case ERRORTYPE:
                errorProcessor(mqttMsg);
                break;
            case RECORDTYPE:
                recordProcessor(message);
                break;
            default:
                logger.error("[MQTT - MESSAGE RECEIVE] Type not supported");
            }

            logger.info("[MQTT - MESSAGE RECEIVE] Message processed: {}", message);
        } catch (IOException e) {
            logger.error(e);
            logger.info("[MQTT - MESSAGE RECEIVE] Message not processed: {}", message);
        }
    }

    private void recordProcessor(String message) {
        try {
            Record record = objectMapper.readValue(message, Record.class);
            if (record.getDate() == null) {
                logger
                        .error("[MQTT - MESSAGE RECEIVE] Message not processed. Date not valid for message {}",
                                message);
            } else {
                recordService.register(record);
            }
        } catch (IOException e) {
            logger.error(e);
            logger.info("[MQTT - MESSAGE RECEIVE] Message not processed: {}", message);
        } catch (EntityNotFoundException e) {
            logger.error("[MQTT - MESSAGE RECEIVE] Entity not found for message {}", message, e);
        }
    }

    private void errorProcessor(MqttMessage message) {
        try {
            Sensor sensor = sensorService.getSensor(message.getSensorId());
            logger.error("[MQTT - MESSAGE RECEIVE] Error receive from sensor {}", sensor.getId());
            logger.error("[MQTT - MESSAGE RECEIVE] Error: {}", message.getMsg());
        } catch (EntityNotFoundException e) {
            logger.error("[MQTT - MESSAGE RECEIVE] Entity not found for message {}", message, e);
        }
    }
}