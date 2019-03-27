package com.torresj.apisensorserver.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.Sensor;
import com.torresj.apisensorserver.services.SensorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("v1/sensors")
public class SensorController {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(SensorController.class);

    /* Services */
    @Autowired
    private SensorService sensorService;

    @GetMapping
    public ResponseEntity<List<Sensor>> getSensors() {
        try {
            logger.info("[SENSOR - GET ALL] Get all sensors from DB");

            List<Sensor> sensors = sensorService.getSensors();

            return new ResponseEntity<List<Sensor>>(sensors, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("[SENSOR - GET ALL] Error getting sensors from DB", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Sensor> getSensorByID(@PathVariable("id") long id) {
        try {
            logger.info("[SENSOR - GET] Get sensor from DB with id: " + id);

            Sensor sensor = sensorService.getSensor(id);

            return new ResponseEntity<Sensor>(sensor, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("[SENSOR - GET] Sensor not found", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor not found", e);
        } catch (Exception e) {
            logger.error("[SENSOR - GET] Error getting sensors from DB", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @PutMapping
    public ResponseEntity<Sensor> registerOrUpdate(@RequestBody(required = true) Sensor sensor) {
        try {
            logger.info("[SENSOR - REGISTER] Registering sensor -> " + sensor);
            Sensor sensorRegister = sensorService.registerOrUpdate(sensor);

            return new ResponseEntity<Sensor>(sensorRegister, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("[SENSOR - REGISTER] Error registering sensor", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @PostMapping
    public ResponseEntity<Sensor> register(@RequestBody(required = true) Sensor sensor) {
        try {
            logger.info("[SENSOR - REGISTER] Registering sensor -> " + sensor);
            Sensor sensorRegister = sensorService.register(sensor);

            return new ResponseEntity<Sensor>(sensorRegister, HttpStatus.CREATED);
        } catch (EntityAlreadyExists e) {
            logger.error("[SENSOR - GET] Sensor already exists", e);
            throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "Sensor already exists", e);
        } catch (Exception e) {
            logger.error("[SENSOR - REGISTER] Error registering sensor", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

}