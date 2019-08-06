package com.torresj.apisensorserver.controller;

import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.Record;
import com.torresj.apisensorserver.models.User.Role;
import com.torresj.apisensorserver.services.RecordService;
import com.torresj.apisensorserver.services.SensorService;
import com.torresj.apisensorserver.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import java.time.LocalDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("v1/records")
@Api(value = "v1/records")
public class RecordController {

  /* Logs */
  private static final Logger logger = LogManager.getLogger(RecordController.class);

  /* Services */
  private RecordService recordService;

  private UserService userService;

  private SensorService sensorService;

  public RecordController(RecordService recordService,
      UserService userService, SensorService sensorService) {
    this.recordService = recordService;
    this.userService = userService;
    this.sensorService = sensorService;
  }

  @GetMapping
  @ApiOperation(value = "Retrieve records", notes = "Pageable data are required and de maximum records per page are 100", response = Record.class, responseContainer = "List")
  public ResponseEntity<Page<Record>> getSensors(
      @RequestParam(value = "sensorId") int sensorId,
      @RequestParam(value = "variableId") int variableId,
      @RequestParam(value = "page") int nPage,
      @RequestParam(value = "elements") int elements,
      @RequestParam(value = "from") @DateTimeFormat(iso = ISO.DATE) LocalDate from,
      @RequestParam(value = "to") @DateTimeFormat(iso = ISO.DATE) LocalDate to,
      Principal principal) {
    try {
      logger.info(
          "[RECORD - GET] Getting records for sensor {}  and variable {} with page {}, elements {}, from {} to {} by user \"{}\"",
          sensorId, variableId, nPage, elements, from, to, principal.getName());
      if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)
          && !sensorService.hasUserVisibilitySensor(principal.getName(), sensorId)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "User does not have permission for this endpoint");
      }

      Page<Record> page = recordService.getRecords(sensorId, variableId, nPage, elements, from, to);

      logger.info(
          "[RECORD - GET] Request getting records for sensor {}  and variable {} with page {}, elements {}, from {} to {}, finished by user \"{}\"",
          sensorId, variableId, nPage, elements, from, to, principal.getName());
      return new ResponseEntity<>(page, HttpStatus.OK);
    } catch (ResponseStatusException e) {
      logger.error("[RECORD - GET] User does not have permission for this endpoint");
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          e.getReason(), e);
    } catch (Exception e) {
      logger.error("[RECORD - GET] Error getting records for sensor {}  and variable {}", sensorId,
          variableId, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @GetMapping(value = "/{id}")
  @ApiOperation(value = "Retrieve a record by id", response = Record.class)
  public ResponseEntity<Record> getSensor(@PathVariable("id") long id, Principal principal) {
    try {
      logger.info("[RECORD - GET] Getting record {} by user \"{}\"", id, principal.getName());
      if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)
          && !recordService.hasUserVisibilityRecord(principal.getName(), id)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "User does not have permission for this endpoint");
      }
      logger.info("[RECORD - GET] Get record from DB with id: " + id);
      Record record = recordService.getRecord(id);
      return new ResponseEntity<>(record, HttpStatus.OK);
    } catch (ResponseStatusException e) {
      logger.error("[RECORD - GET] User does not have permission for this endpoint");
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          e.getReason(), e);
    } catch (EntityNotFoundException e) {
      logger.error("[RECORD - GET] Error record not found", e);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "record not found", e);
    } catch (Exception e) {
      logger.error("[RECORD - GET] Error getting record {}", id, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }
}