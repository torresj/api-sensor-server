package com.torresj.apisensorserver.controller;

import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.Sensor;
import com.torresj.apisensorserver.models.Variable;
import com.torresj.apisensorserver.services.SensorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("v1/sensors")
@Api(value = "v1/sensors")
public class SensorController {

  /* Logs */
  private static final Logger logger = LogManager.getLogger(SensorController.class);

  /* Services */
  private SensorService sensorService;

  public SensorController(SensorService sensorService) {
    this.sensorService = sensorService;
  }

  @GetMapping
  @ApiOperation(value = "Retrieve sensors", notes = "Pageable data are required and de maximum records per page are 100", response = Sensor.class, responseContainer = "List")
  public ResponseEntity<Page<Sensor>> getSensors(
      @RequestParam(value = "page") int nPage,
      @RequestParam(value = "elements") int elements,
      @RequestParam(value = "sensorTypeId", required = false) Long sensorTypeId
  ) {
    try {
      logger.info(
          "[SENSOR - GET ALL] Get sensors from DB with page " + nPage + ", elements " + elements
              + ", sensorTypeId " + sensorTypeId);

      Page<Sensor> page = sensorTypeId == null ? sensorService.getSensors(nPage, elements)
          : sensorService.getSensors(nPage, elements, sensorTypeId);

      return new ResponseEntity<>(page, HttpStatus.OK);
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

      return new ResponseEntity<>(sensor, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      logger.error("[SENSOR - GET] Sensor not found", e);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor not found", e);
    } catch (Exception e) {
      logger.error("[SENSOR - GET] Error getting sensors from DB", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @GetMapping(value = "/{id}/variables")
  @ApiOperation(value = "Retrieve variables sensor by id", response = Variable.class, responseContainer = "List")
  public ResponseEntity<Page<Variable>> getVariablesSensorByID(@PathVariable("id") long id,
      @RequestParam(value = "page") int nPage,
      @RequestParam(value = "elements") int elements) {
    try {
      logger.info(
          "[SENSOR VARIABLES - GET] Get variables sensor from DB with id: " + id + " page: " + nPage
              + ", elements: " + elements);
      ;

      Page<Variable> page = sensorService.getVariables(id, nPage, elements);

      return new ResponseEntity<>(page, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      logger.error("[SENSOR VARIABLES - GET] Sensor not found", e);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor not found", e);
    } catch (Exception e) {
      logger.error("[SENSOR VARIABLES - GET] Error getting variables from DB", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @PutMapping(value = "/{id}/variables")
  @ApiOperation(value = "Add variable to sensor variables list", response = Variable.class, notes = "Variable must exist")
  public ResponseEntity<Variable> addVariablesSensorByID(@PathVariable("id") long id,
      @RequestBody() long variableId) {
    try {
      logger.info(
          "[SENSOR VARIABLES - ADD] Add variable " + variableId + " to variables sensor " + id
              + " list");

      Variable variable = sensorService.addVariable(id, variableId);

      return new ResponseEntity<>(variable, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      logger.error("[SENSOR - GET] Sensor or variable not found", e);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor or variable not found", e);
    } catch (Exception e) {
      logger.error("[SENSOR - GET] Error getting sensors from DB", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @PutMapping
  @ApiOperation(value = "Update sensor", response = Sensor.class, notes = "SensorType and House must exist. House can be null")
  public ResponseEntity<Sensor> update(@RequestBody() Sensor sensor) {
    try {
      logger.info("[SENSOR - UPDDATE] Updating sensor -> " + sensor);
      Sensor sensorRegister = sensorService.update(sensor);

      return new ResponseEntity<>(sensorRegister, HttpStatus.CREATED);
    } catch (Exception e) {
      logger.error("[SENSOR - UPDATE] Error updating sensor", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @PostMapping
  @ApiOperation(value = "Register sensor", response = Sensor.class)
  public ResponseEntity<Sensor> register(@RequestBody() Sensor sensor) {
    try {
      logger.info("[SENSOR - REGISTER] Registering sensor -> " + sensor);
      Sensor sensorRegister = sensorService.register(sensor);

      return new ResponseEntity<>(sensorRegister, HttpStatus.CREATED);
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

      return new ResponseEntity<>(sensor, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      logger.error("[SENSOR - REMOVE] Sensor not found", e);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor not found", e);
    } catch (Exception e) {
      logger.error("[SENSOR - REMOVE] Error removing sensors from DB", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }
}