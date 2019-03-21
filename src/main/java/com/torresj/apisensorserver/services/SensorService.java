package com.torresj.apisensorserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torresj.apisensorserver.models.Sensor;
import com.torresj.apisensorserver.rabbitmq.Producer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class SensorService {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(SensorService.class);

    @Autowired
    private Producer producer;

    public void register(Sensor sensor) {
        logger.info("[SENSOR - REGISTER] Registering sensor on ServiceSensor");
        producer.produceMsg("Test");
    }
}