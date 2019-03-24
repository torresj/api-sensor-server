package com.torresj.apisensorserver.controller;

import org.springframework.web.bind.annotation.RestController;

import com.torresj.apisensorserver.dtos.BaseResponse;
import com.torresj.apisensorserver.jpa.UserRepository;
import com.torresj.apisensorserver.models.Sensor;
import com.torresj.apisensorserver.services.SensorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/sensor")
public class SensorController {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(SensorController.class);

    /* Services */
    @Autowired
    private SensorService sensorService;

    @PutMapping(value = "/register")
    public ResponseEntity<BaseResponse> register(@RequestBody(required = true) Sensor sensor) {
        // Create log header
        logger.info("[SENSOR - REGISTER] Registering sensor -> " + sensor);

        // Call Service
        sensorService.register(sensor);

        return new ResponseEntity<BaseResponse>(new BaseResponse(), HttpStatus.OK);
    }

}