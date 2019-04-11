package com.torresj.apisensorserver.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.time.LocalDate;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.Record;
import com.torresj.apisensorserver.models.Sensor;
import com.torresj.apisensorserver.services.SensorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("v1/sensors")
@Api(value = "v1/sensors", description = "Operations about sensors")
public class SensorController {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(SensorController.class);

    /* Services */
    @Autowired
    private SensorService sensorService;

    @GetMapping
    @ApiOperation(value = "Retrieve sensors", notes = "Pageable data are required and de maximum records per page are 100", response = Sensor.class, responseContainer = "List")
    public ResponseEntity<Page<Sensor>> getSensors(@RequestParam(value = "page") int nPage,
            @RequestParam(value = "elements") int elements) {
        try {
            logger.info("[SENSOR - GET ALL] Get sensors from DB with page " + nPage + ", elements " + elements);

            Page<Sensor> page = sensorService.getSensors(nPage, elements);

            return new ResponseEntity<Page<Sensor>>(page, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("[SENSOR - GET ALL] Error getting sensors from DB", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Retrieve sensor by id", response = Sensor.class)
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

    @GetMapping(value = "/{sensorId}/variables/{variableId}/records")
    @ApiOperation(value = "Retrieve records by sensor and variable", response = Record.class)
    public ResponseEntity<Page<Record>> getRecordsFromVariableFromSensor(@PathVariable("sensorId") long sensorId,
            @PathVariable("variableId") long variableId, @RequestParam(value = "page") int nPage,
            @RequestParam(value = "elements") int elements,
            @RequestParam(value = "from") @DateTimeFormat(iso = ISO.DATE) LocalDate from,
            @RequestParam(value = "to") @DateTimeFormat(iso = ISO.DATE) LocalDate to) {
        try {
            logger.info("[SENSOR - VARIABLE - RECORDS] Get records from sensor " + sensorId + " from variable "
                    + variableId + " from DB with page " + nPage + ", elements " + elements + ", from " + from + " to "
                    + to);

            Page<Record> page = sensorService.getRecords(sensorId, variableId, nPage, elements, from, to);

            return new ResponseEntity<Page<Record>>(page, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("[SENSOR - VARIABLE - RECORDS] Sensor or variable not  found", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor or variable not  found", e);
        } catch (Exception e) {
            logger.error("[SENSOR - VARIABLE - RECORDS] Error getting records from DB", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @PutMapping
    @ApiOperation(value = "Update sensor", response = Sensor.class)
    public ResponseEntity<Sensor> update(@RequestBody(required = true) Sensor sensor) {
        try {
            logger.info("[SENSOR - UPDDATE] Updating sensor -> " + sensor);
            Sensor sensorRegister = sensorService.update(sensor);

            return new ResponseEntity<Sensor>(sensorRegister, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("[SENSOR - UPDATE] Error updating sensor", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @PostMapping
    @ApiOperation(value = "Register sensor", response = Sensor.class)
    public ResponseEntity<Sensor> register(@RequestBody(required = true) Sensor sensor) {
        try {
            logger.info("[SENSOR - REGISTER] Registering sensor -> " + sensor);
            Sensor sensorRegister = sensorService.register(sensor);

            return new ResponseEntity<Sensor>(sensorRegister, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("[SENSOR - REGISTER] Error registering sensor", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Delete sensor", response = Sensor.class)
    public ResponseEntity<Sensor> delete(@PathVariable("id") long id) {
        try {
            logger.info("[SENSOR - REMOVE] Remove sensor from DB with id: " + id);

            Sensor sensor = sensorService.removeSensor(id);

            return new ResponseEntity<Sensor>(sensor, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("[SENSOR - REMOVE] Sensor not found", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor not found", e);
        } catch (Exception e) {
            logger.error("[SENSOR - REMOVE] Error removing sensors from DB", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }
}