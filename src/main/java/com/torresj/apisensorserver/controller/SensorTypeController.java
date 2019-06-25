package com.torresj.apisensorserver.controller;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityHasRelationsException;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.SensorType;
import com.torresj.apisensorserver.services.SensorTypeService;
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
@RequestMapping("v1/sensortypes")
@Api(value = "v1/sensortypes")
public class SensorTypeController {

  /* Logs */
  private static final Logger logger = LogManager.getLogger(SensorTypeController.class);

  private SensorTypeService service;

  public SensorTypeController(SensorTypeService service) {
    this.service = service;
  }

  @GetMapping
  @ApiOperation(value = "Retrieve sensor types", notes = "Pageable data are required and de maximum records per page are 100", response = SensorType.class, responseContainer = "List")
  public ResponseEntity<Page<SensorType>> getSensorTypes(@RequestParam(value = "page") int nPage,
      @RequestParam(value = "elements") int elements) {
    try {
      logger.info(
          "[SENSOR TYPES - GET ALL] Get sensor types from DB with page " + nPage + ", elements "
              + elements);
      Page<SensorType> page = service.getSensorTypes(nPage, elements);

      return new ResponseEntity<>(page, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("[SENSOR TYPES - GET ALL] Error getting sensor types from DB", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @GetMapping(value = "/{id}")
  @ApiOperation(value = "Retrieve sensor type by id", response = SensorType.class)
  public ResponseEntity<SensorType> getSensorByID(@PathVariable("id") long id) {
    try {
      logger.info("[SENSOR TYPE - GET] Get sensor from DB with id: " + id);

      SensorType sensorType = service.getSensorType(id);

      return new ResponseEntity<>(sensorType, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      logger.error("[SENSOR TYPE - GET] Sensor type not found", e);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor type not found", e);
    } catch (Exception e) {
      logger.error("[SENSOR TYPE - GET] Error getting sensor type from DB", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @PostMapping()
  @ApiOperation(value = "Save new sensor type", response = SensorType.class)
  public ResponseEntity<SensorType> register(@RequestBody() SensorType type) {
    try {
      logger.info("[SENSOR TYPE - REGISTER] Register sensor type: " + type);

      SensorType sensorType = service.register(type);

      return new ResponseEntity<>(sensorType, HttpStatus.CREATED);
    } catch (EntityAlreadyExists e) {
      logger.error("[SENSOR TYPE - REGISTER] Sensor type already exists", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    } catch (Exception e) {
      logger.error("[SENSOR TYPE - REGISTER] Error registering sensor type from DB", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @PutMapping()
  @ApiOperation(value = "Update new sensor type", response = SensorType.class)
  public ResponseEntity<SensorType> update(@RequestBody() SensorType type) {
    try {
      logger.info("[SENSOR TYPE - UPDATE] Update sensor type: " + type);

      SensorType sensorType = service.update(type);

      return new ResponseEntity<>(sensorType, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      logger.error("[SENSOR TYPE - UPDATE] Sensor type not found", e);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor type not found", e);
    } catch (Exception e) {
      logger.error("[SENSOR TYPE - UPDATE] Error getting sensor type from DB", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @DeleteMapping(value = "/{id}")
  @ApiOperation(value = "Delete sensor type", response = SensorType.class)
  public ResponseEntity<SensorType> remove(@PathVariable("id") long id) {
    try {
      logger.info("[SENSOR TYPE - REMOVE] Remove sensor type: " + id);

      SensorType sensorType = service.remove(id);

      return new ResponseEntity<>(sensorType, HttpStatus.OK);
    } catch (EntityHasRelationsException e) {
      logger.error(
          "[SENSOR TYPE - REMOVE] Sensor type has relation with existing sensors. You need to change each sensor with sensor type "
              + id + " before remove sensor type", e);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "Sensor type has relation with existing sensors", e);
    } catch (EntityNotFoundException e) {
      logger.error("[SENSOR TYPE - REMOVE] Sensor type not found", e);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor type not found", e);
    } catch (Exception e) {
      logger.error("[SENSOR TYPE - REMOVE] Error removing sensor type from DB", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }
}
