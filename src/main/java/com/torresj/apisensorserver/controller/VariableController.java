package com.torresj.apisensorserver.controller;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.entities.Sensor;
import com.torresj.apisensorserver.models.entities.User.Role;
import com.torresj.apisensorserver.models.entities.Variable;
import com.torresj.apisensorserver.services.UserService;
import com.torresj.apisensorserver.services.VariableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
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
@RequestMapping("/v1/variables")
@Api(value = "v1/variables")
public class VariableController {

  /* Logs */
  private static final Logger logger = LogManager.getLogger(VariableController.class);

  /* Services */
  private VariableService variableService;
  private UserService userService;

  public VariableController(VariableService variableService,
      UserService userService) {
    this.variableService = variableService;
    this.userService = userService;
  }

  @GetMapping
  @ApiOperation(
      value = "Retrieve variables",
      notes = "Pageable data are required and de maximum records per page are 100",
      response = Variable.class, responseContainer = "List")
  public ResponseEntity<Page<Variable>> getVariables(@RequestParam(value = "page") int nPage,
      @RequestParam(value = "elements") int elements,
      @RequestParam(value = "name", required = false) String name, Principal principal) {
    try {
      logger.info(
          "[VARIABLE - GET ALL] Getting variables with page {}, elements {} and name {} by user \"{}\"",
          nPage, elements, name, principal.getName());
      if (!userService.isUserAllowed(principal.getName(), Role.ADMIN, Role.STATION)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "User does not have permission for this endpoint");
      }

      Page<Variable> page = name == null ? variableService.getVariables(nPage, elements)
          : variableService.getVariables(nPage, elements, name);

      logger.info(
          "[VARIABLE - GET ALL] Request for getting variables with page {}, elements {} and name {} finished by user \"{}\"",
          nPage, elements, name, principal.getName());
      return new ResponseEntity<>(page, HttpStatus.OK);
    } catch (ResponseStatusException e) {
      logger.error("[VARIABLE - GET ALL] User does not have permission for this endpoint");
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          e.getReason(), e);
    } catch (Exception e) {
      logger.error("[VARIABLE - GET ALL] Error getting variables", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @GetMapping(value = "/{id}")
  @ApiOperation(value = "Retrieve variable by id", response = Variable.class)
  public ResponseEntity<Variable> getVariableById(@PathVariable("id") long id,
      Principal principal) {
    try {
      logger.info("[VARIABLE - GET] Getting variable {} by user \"{}\"", id, principal.getName());
      if (!userService.isUserAllowed(principal.getName(), Role.ADMIN, Role.STATION)
          && !variableService
          .hasUserVisibilityVariable(principal.getName(), id)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "User does not have permission for this endpoint");
      }

      Variable variable = variableService.getVariable(id);

      logger.info("[VARIABLE - GET] Request for getting variable {} finished by user \"{}\"", id,
          principal.getName());
      return new ResponseEntity<>(variable, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      logger.error("[VARIABLE - GET] Error variable not found", e);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Variable not found", e);
    } catch (ResponseStatusException e) {
      logger.error("[VARIABLE - GET] User does not have permission for this endpoint");
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          e.getReason(), e);
    } catch (Exception e) {
      logger.error("[VARIABLE - GET] Error getting variable {}", id, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @PutMapping
  @ApiOperation(value = "Update variable", response = Variable.class)
  public ResponseEntity<Variable> update(@RequestBody() Variable variable, Principal principal) {
    try {
      logger.info("[VARIABLE - UPDATE] Updating variable {} by user \"{}\"", variable,
          principal.getName());
      if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "User does not have permission for this endpoint");
      }

      Variable variableRegister = variableService.update(variable);

      logger.info("[VARIABLE - UPDATE] Updating variable {} finished by user \"{}\"", variable,
          principal.getName());
      return new ResponseEntity<>(variableRegister, HttpStatus.CREATED);
    } catch (ResponseStatusException e) {
      logger.error("[VARIABLE - UPDATE] User does not have permission for this endpoint");
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          e.getReason(), e);
    } catch (Exception e) {
      logger.error("[VARIABLE - UPDATE] Error Updating variable {}", variable, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @PostMapping
  @ApiOperation(value = "Register variable", response = Variable.class)
  public ResponseEntity<Variable> register(@RequestBody() Variable variable, Principal principal) {
    try {
      logger.info("[VARIABLE - REGISTER] Register variable {} by user \"{}\"", variable,
          principal.getName());
      if (!userService.isUserAllowed(principal.getName(), Role.ADMIN, Role.STATION)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "User does not have permission for this endpoint");
      }

      Variable variableRegister = variableService.register(variable);

      logger.info("[VARIABLE - REGISTER] Request for register variable {} finished by user \"{}\"",
          variable,
          principal.getName());
      return new ResponseEntity<>(variableRegister, HttpStatus.CREATED);
    } catch (EntityAlreadyExists e) {
      logger.error("[VARIABLE - REGISTER] Variable already exists", e);
      throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "Variable already exists", e);
    } catch (ResponseStatusException e) {
      logger.error("[VARIABLE - REGISTER] User does not have permission for this endpoint");
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          e.getReason(), e);
    } catch (Exception e) {
      logger.error("[VARIABLE - REGISTER] Error registering variable {}", variable, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @DeleteMapping(value = "/{id}")
  @ApiOperation(value = "Delete variable by id", response = Variable.class)
  public ResponseEntity<Variable> delete(@PathVariable("id") long id, Principal principal) {
    try {
      logger.info("[VARIABLE - DELETE] Delete variable {} by user \"{}\"", id, principal.getName());
      if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "User does not have permission for this endpoint");
      }

      Variable variableRegister = variableService.deleteVariable(id);

      logger
          .info("[VARIABLE - DELETE] Request for deleting variable {} finished by user \"{}\"", id,
              principal.getName());
      return new ResponseEntity<>(variableRegister, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      logger.error("[VARIABLE - DELETE] Variable not exists", e);
      throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "Variable not exists", e);
    } catch (ResponseStatusException e) {
      logger.error("[VARIABLE - DELETE] User does not have permission for this endpoint");
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          e.getReason(), e);
    } catch (Exception e) {
      logger.error("[VARIABLE - DELETE] Error deleting variable {}", id, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @GetMapping(value = "/{id}/sensors")
  @ApiOperation(value = "Retrieve all sensors witch have variable by id", response = Sensor.class, responseContainer = "List")
  public ResponseEntity<Page<Sensor>> getSensorsHaveVariableById(@PathVariable("id") long id,
      @RequestParam(value = "page") int nPage,
      @RequestParam(value = "elements") int elements, Principal principal) {
    try {
      logger.info(
          "[SENSORS HAVE VARIABLE - GET] Getting sensors contains variable {} by user \"{}\"", id,
          principal.getName());
      if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "user Not have permission for this endpoint");
      }

      Page<Sensor> sensors = variableService.getSensors(id, nPage, elements);

      logger.info(
          "[SENSORS HAVE VARIABLE - GET] Request getting sensors contains variable {} finished by user \"{}\"",
          id,
          principal.getName());
      return new ResponseEntity<>(sensors, HttpStatus.OK);
    } catch (ResponseStatusException e) {
      logger.error("[SENSORS HAVE VARIABLE - GET] User does not have permission for this endpoint");
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          e.getReason(), e);
    } catch (Exception e) {
      logger.error("[SENSORS HAVE VARIABLE - GET] Error getting sensors with variable {}", id, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

}