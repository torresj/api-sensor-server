package com.torresj.apisensorserver.services;

import org.springframework.stereotype.Service;

import com.torresj.apisensorserver.models.Sensor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class SensorService {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(SensorService.class);

    public void register(Sensor sensor) {
        logger.debug("[SENSOR - REGISTER] Registering sensor on ServiceSensor");
    }
}