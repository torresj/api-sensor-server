package com.torresj.apisensorserver.controller;

import java.security.Principal;
import java.util.List;

import com.torresj.apisensorserver.exceptions.ActionException;
import com.torresj.apisensorserver.exceptions.EntityAlreadyExistsException;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.entities.Sensor;
import com.torresj.apisensorserver.models.entities.User.Role;
import com.torresj.apisensorserver.models.entities.Variable;
import com.torresj.apisensorserver.services.SensorService;
import com.torresj.apisensorserver.services.UserService;

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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("v1/sensors")
@Api(value = "v1/sensors")
public class SensorController {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(SensorController.class);

    /* Services */
    private SensorService sensorService;

    private UserService userService;

    public SensorController(SensorService sensorService,
            UserService userService) {
        this.sensorService = sensorService;
        this.userService = userService;
    }

    @GetMapping
    @ApiOperation(value = "Retrieve sensors", notes = "Pageable data are required and de maximum records per page are 100", response = Sensor.class, responseContainer = "List")
    public ResponseEntity<Page<Sensor>> getSensors(
            @RequestParam(value = "page") int nPage,
            @RequestParam(value = "elements") int elements,
            @RequestParam(value = "sensorTypeId", required = false) Long sensorTypeId,
            @RequestParam(value = "name", required = false) String name,
            Principal principal
    ) {
        try {
            logger.info(
                    "[SENSOR - GET ALL] Getting sensors with page {}, elements {}, sensorTypeId {} by user \"{}\"",
                    nPage, elements, sensorTypeId, principal.getName());
            if (!userService.isUserAllowed(principal.getName(), Role.ADMIN, Role.STATION)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "User does not have permission for this endpoint");
            }

            Page<Sensor> page =
                    sensorTypeId == null && name == null ? sensorService.getSensors(nPage, elements)
                            : sensorService.getSensors(nPage, elements, sensorTypeId, name);

            logger.info(
                    "[SENSOR - GET ALL] Request for getting sensors with page {}, elements {}, sensorTypeId {} finished by user \"{}\"",
                    nPage, elements, sensorTypeId, principal.getName());
            return new ResponseEntity<>(page, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            logger.error("[SENSOR - GET ALL] User does not have permission for this endpoint");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.getReason(), e);
        } catch (Exception e) {
            logger.error("[SENSOR - GET ALL] Error getting sensors from DB", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @GetMapping(value = "/all")
    @ApiOperation(value = "Retrieve sensors without pagination", response = Sensor.class, responseContainer = "List")
    public ResponseEntity<List<Sensor>> getSensors(@RequestParam(value = "sensorTypeId", required = false) Long sensorTypeId,
            Principal principal
    ) {
        try {
            logger.info(
                    "[SENSOR - GET ALL] Getting sensors by user \"{}\"", principal.getName());
            if (!userService.isUserAllowed(principal.getName(), Role.ADMIN, Role.STATION)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "User does not have permission for this endpoint");
            }

            List<Sensor> sensors =
                    sensorTypeId == null ? sensorService.getSensors() : sensorService.getSensors(sensorTypeId);

            logger.info(
                    "[SENSOR - GET ALL] Request for getting sensors finished by user \"{}\"", principal.getName());
            return new ResponseEntity<>(sensors, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            logger.error("[SENSOR - GET ALL] User does not have permission for this endpoint");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.getReason(), e);
        } catch (Exception e) {
            logger.error("[SENSOR - GET ALL] Error getting sensors from DB", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Retrieve sensor by id", response = Sensor.class)
    public ResponseEntity<Sensor> getSensorByID(@PathVariable("id") long id,
            Principal principal) {
        try {
            logger.info("[SENSOR - GET] Getting sensor {} by user \"{}\"", id, principal.getName());
            if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)
                    && !sensorService.hasUserVisibilitySensor(principal.getName(), id)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "User does not have permission for this endpoint");
            }

            Sensor sensor = sensorService.getSensor(id);

            logger.info("[SENSOR - GET] Request for getting sensor {} finished by user \"{}\"", id,
                    principal.getName());
            return new ResponseEntity<>(sensor, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            logger.error("[SENSOR - GET] User does not have permission for this endpoint");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.getReason(), e);
        } catch (EntityNotFoundException e) {
            logger.error("[SENSOR - GET] Sensor not found", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor not found", e);
        } catch (Exception e) {
            logger.error("[SENSOR - GET] Error getting sensor {}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @GetMapping(value = "/{id}/variables")
    @ApiOperation(value = "Retrieve variables sensor by id", response = Variable.class, responseContainer = "List")
    public ResponseEntity<Page<Variable>> getVariablesSensorByID(@PathVariable("id") long id,
            @RequestParam(value = "page") int nPage,
            @RequestParam(value = "elements") int elements,
            Principal principal) {
        try {
            logger.info(
                    "[SENSOR VARIABLES - GET] Getting sensor {} variables with page {}, elements {} by user \"{}\"",
                    id, nPage, elements, principal.getName());
            ;
            if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)
                    && !sensorService.hasUserVisibilitySensor(principal.getName(), id)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "User does not have permission for this endpoint");
            }

            Page<Variable> page = sensorService.getVariables(id, nPage, elements);

            logger.info(
                    "[SENSOR VARIABLES - GET] Request for getting sensor {} variables with page {}, elements {} finished by user \"{}\"",
                    id, nPage, elements, principal.getName());
            return new ResponseEntity<>(page, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            logger.error("[SENSOR VARIABLES - GET] User does not have permission for this endpoint");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.getReason(), e);
        } catch (EntityNotFoundException e) {
            logger.error("[SENSOR VARIABLES - GET] Sensor not found", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor not found", e);
        } catch (Exception e) {
            logger.error("[SENSOR VARIABLES - GET] Error getting sensor {} variables ", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @PutMapping(value = "/{id}/variables/{variableId}")
    @ApiOperation(value = "Add variable to sensor variables list", response = Variable.class, notes = "Variable must exist")
    public ResponseEntity<Variable> addVariablesSensorByID(@PathVariable("id") long id,
            @PathVariable("variableId") long variableId, Principal principal) {
        try {
            logger.info(
                    "[SENSOR VARIABLES - ADD] Adding variable {} to variables sensor {} list by user \"{}\"",
                    variableId, id, principal.getName());
            if (!userService.isUserAllowed(principal.getName(), Role.ADMIN, Role.STATION)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "User does not have permission for this endpoint");
            }

            Variable variable = sensorService.addVariable(id, variableId);

            logger.info(
                    "[SENSOR VARIABLES - ADD] Request for adding variable {} to variables sensor {} list finished by user \"{}\"",
                    variableId, id, principal.getName());
            return new ResponseEntity<>(variable, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            logger.error("[SENSOR VARIABLES - ADD] User does not have permission for this endpoint");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.getReason(), e);
        } catch (EntityNotFoundException e) {
            logger.error("[SENSOR VARIABLES - ADD] Sensor or variable not found", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor or variable not found", e);
        } catch (Exception e) {
            logger.error("[SENSOR VARIABLES - ADD] Error adding variable {} to sensor {} variable list",
                    variableId, id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @DeleteMapping(value = "/{id}/variables/{variableId}")
    @ApiOperation(value = "Delete variable from sensor variables list", response = Variable.class, notes = "Variable must exist")
    public ResponseEntity<Variable> deleteVariablesSensorByID(@PathVariable("id") long id,
            @PathVariable("variableId") long variableId, Principal principal) {
        try {
            logger.info(
                    "[SENSOR VARIABLES - REMOVE] Removing variable {} to variables sensor {} list by user \"{}\"",
                    variableId, id, principal.getName());
            if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "User does not have permission for this endpoint");
            }

            Variable variable = sensorService.removeVariable(id, variableId);

            logger.info(
                    "[SENSOR VARIABLES - REMOVE] Request for removing variable {} to variables sensor {} list finished by user \"{}\"",
                    variableId, id, principal.getName());
            return new ResponseEntity<>(variable, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            logger.error("[SENSOR VARIABLES - REMOVE] User does not have permission for this endpoint");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.getReason(), e);
        } catch (EntityNotFoundException e) {
            logger.error("[SENSOR VARIABLES - REMOVE] Sensor or variable not found", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor or variable not found", e);
        } catch (Exception e) {
            logger.error(
                    "[SENSOR VARIABLES - REMOVE] Error removing variable {} to variables sensor {} list",
                    variableId, id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @PutMapping
    @ApiOperation(value = "Update sensor", response = Sensor.class, notes = "SensorType and House must exist. House can be null")
    public ResponseEntity<Sensor> update(@RequestBody() Sensor sensor, Principal principal) {
        try {
            logger
                    .info("[SENSOR - UPDATE] Updating sensor {} by user \"{}\"", sensor, principal.getName());
            if (!userService.isUserAllowed(principal.getName(), Role.ADMIN, Role.STATION)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "User does not have permission for this endpoint");
            }

            Sensor sensorRegister = sensorService.update(sensor);

            logger
                    .info("[SENSOR - UPDATE] Request for updating sensor {} finished by user \"{}\"", sensor,
                            principal.getName());
            return new ResponseEntity<>(sensorRegister, HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            logger.error("[SENSOR - UPDATE] User does not have permission for this endpoint");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.getReason(), e);
        } catch (Exception e) {
            logger.error("[SENSOR - UPDATE] Error updating sensor {}", sensor, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @PostMapping
    @ApiOperation(value = "Register sensor", response = Sensor.class)
    public ResponseEntity<Sensor> register(@RequestBody() Sensor sensor, Principal principal) {
        try {
            logger
                    .info("[SENSOR - REGISTER] Registering sensor {} by user \"{}\"", sensor,
                            principal.getName());
            if (!userService.isUserAllowed(principal.getName(), Role.ADMIN, Role.STATION)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "User does not have permission for this endpoint");
            }

            Sensor sensorRegister = sensorService.register(sensor);
            logger
                    .info("[SENSOR - REGISTER] Request for registering sensor {} finished by user \"{}\"",
                            sensor,
                            principal.getName());
            return new ResponseEntity<>(sensorRegister, HttpStatus.CREATED);
        } catch (EntityAlreadyExistsException e) {
            logger.info("[SENSOR - REGISTER] Entity already exists and it hasn't been modified/created");
            return new ResponseEntity<>((Sensor) e.getObject(), HttpStatus.ACCEPTED);
        } catch (ResponseStatusException e) {
            logger.error("[SENSOR - REGISTER] User does not have permission for this endpoint");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.getReason(), e);
        } catch (Exception e) {
            logger.error("[SENSOR - REGISTER] Error registering sensor {}", sensor, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Delete sensor", response = Sensor.class)
    public ResponseEntity<Sensor> delete(@PathVariable("id") long id, Principal principal) {
        try {
            logger.info("[SENSOR - REMOVE] Removing sensor {} by user \"{}\"", id, principal.getName());
            if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "User does not have permission for this endpoint");
            }

            Sensor sensor = sensorService.removeSensor(id);

            logger.info("[SENSOR - REMOVE] Request removing sensor {} finished by user \"{}\"", id,
                    principal.getName());
            return new ResponseEntity<>(sensor, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            logger.error("[SENSOR - REMOVE] User does not have permission for this endpoint");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.getReason(), e);
        } catch (EntityNotFoundException e) {
            logger.error("[SENSOR - REMOVE] Sensor not found", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor not found", e);
        } catch (Exception e) {
            logger.error("[SENSOR - REMOVE] Error removing sensor {}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @PostMapping(value = "/{id}/reset")
    @ApiOperation(value = "Reset sensor by id", response = Void.class)
    public ResponseEntity<Void> sendResetToSensor(@PathVariable("id") long id,
            Principal principal) {
        try {
            logger.info(
                    "[SENSOR ACTIONS - RESET] Sending reset to sensor {} by user \"{}\"",
                    id, principal.getName());
            if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)
                    && !sensorService.hasUserVisibilitySensor(principal.getName(), id)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "User does not have permission for this endpoint");
            }

            sensorService.reset(id);

            logger.info(
                    "[SENSOR ACTIONS - RESET] Request for sending reset to sensor {} finished by user \"{}\"",
                    id, principal.getName());
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            logger.error("[SENSOR ACTIONS - RESET] User does not have permission for this endpoint");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.getReason(), e);
        } catch (EntityNotFoundException e) {
            logger.error("[[SENSOR ACTIONS - RESET] - GET] Sensor not found", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor not found", e);
        } catch (Exception e) {
            logger.error("[[SENSOR ACTIONS - RESET]] Error sending reset to sensor {}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @PostMapping(value = "/{id}/actions/{action}")
    @ApiOperation(value = "Send action to sensor by id", response = Void.class)
    public ResponseEntity<Void> sendActionToSensor(@PathVariable("id") long id,
            @PathVariable("action") String action,
            Principal principal) {
        try {
            logger.info(
                    "[SENSOR ACTIONS] Sending {} action to sensor {} by user \"{}\"",
                    action, id, principal.getName());
            if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)
                    && !sensorService.hasUserVisibilitySensor(principal.getName(), id)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "User does not have permission for this endpoint");
            }

            sensorService.sendAction(id, action);

            logger.info(
                    "[SENSOR ACTIONS] Request sending {} action to sensor {} finished by user \"{}\"",
                    action, id, principal.getName());
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            logger.error("[SENSOR ACTIONS] User does not have permission for this endpoint");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.getReason(), e);
        } catch (EntityNotFoundException e) {
            logger.error("[SENSOR ACTIONS] Error sending action \"{}\"", action, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor not found", e);
        } catch (ActionException e) {
            logger.error("[SENSOR ACTIONS] Sensor not found", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensor not found", e);
        } catch (Exception e) {
            logger.error("[SENSOR ACTIONS] Error sending {} action to sensor {}", action, id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }
}