package com.torresj.apisensorserver.controller;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.House;
import com.torresj.apisensorserver.models.User;
import com.torresj.apisensorserver.models.User.Role;
import com.torresj.apisensorserver.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
@RequestMapping("v1/users")
@Api(value = "v1/users")
public class UserController {

  /* Logs */
  private static final Logger logger = LogManager.getLogger(UserController.class);

  /* Services */
  private UserService userService;

  private BCryptPasswordEncoder bCryptPasswordEncoder;

  public UserController(UserService userService,
      BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.userService = userService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  @GetMapping
  @ApiOperation(value = "Retrieve Users", notes = "Pageable data are required and de maximum records per page are 100", response = User.class, responseContainer = "List")
  public ResponseEntity<Page<User>> getUsers(@RequestParam(value = "page") int nPage,
      @RequestParam(value = "elements") int elements, Principal principal) {
    try {
      logger.info("[USER - GET ALL] Check user permission");
      if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "user Not have permission for this endpoint");
      }
      logger
          .info("[USER - GET ALL] Get users from DB with page " + nPage + ", elements " + elements);

      Page<User> page = userService.getUsers(nPage, elements);

      return new ResponseEntity<>(page, HttpStatus.OK);
    } catch (ResponseStatusException e) {
      logger.error("[USER - GET ALL] user Not have permission for this endpoint");
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          e.getReason(), e);
    } catch (Exception e) {
      logger.error("[USER - GET ALL] Error getting users from DB", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @GetMapping(value = "/{id}")
  @ApiOperation(value = "Retrieve user by id", response = User.class)
  public ResponseEntity<User> getHouseByID(@PathVariable("id") long id) {
    try {
      logger.info("[USER - GET] Get users from DB with id: " + id);

      User user = userService.getUser(id);

      return new ResponseEntity<>(user, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      logger.error("[USER - GET] User not found", e);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", e);
    } catch (Exception e) {
      logger.error("[USER - GET] Error getting users from DB", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @GetMapping(value = "/{id}/houses")
  @ApiOperation(value = "Retrieve user by id", response = User.class)
  public ResponseEntity<Page<House>> getHouseSensorsByID(@PathVariable("id") long id,
      @RequestParam(value = "page") int nPage,
      @RequestParam(value = "elements") int elements) {
    try {
      logger.info("[USER HOUSE - GET] Get user houses from DB with id: " + id);

      Page<House> page = userService.getHouses(id, nPage, elements);

      return new ResponseEntity<>(page, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      logger.error("[USER - GET] User not found", e);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", e);
    } catch (Exception e) {
      logger.error("[USER - GET] Error getting users from DB", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @GetMapping(value = "/{name}")
  @ApiOperation(value = "Retrieve user by user name", response = User.class)
  public ResponseEntity<User> getHouseByName(@PathVariable("name") String name) {
    try {
      logger.info("[USER - GET] Get users from DB with user name: " + name);

      User user = userService.getUser(name);

      return new ResponseEntity<>(user, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      logger.error("[USER - GET] User not found", e);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", e);
    } catch (Exception e) {
      logger.error("[USER - GET] Error getting users from DB", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @PostMapping
  @ApiOperation(value = "Register user", response = User.class)
  public ResponseEntity<User> register(@RequestBody() User user, Principal principal) {
    try {
      logger.info("[USER - REGISTER] Check user permission");
      if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "user Not have permission for this endpoint");
      }
      logger.info("[USER - REGISTER] Registering user");
      user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
      User userRegister = userService.register(user);

      return new ResponseEntity<>(userRegister, HttpStatus.CREATED);
    } catch (EntityAlreadyExists e) {
      logger.error("[USER - REGISTER] user already exists", e);
      throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "User already exists", e);
    } catch (ResponseStatusException e) {
      logger.error("[USER - REGISTER] user Not have permission for this endpoint");
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          e.getReason(), e);
    } catch (Exception e) {
      logger.error("[USER - REGISTER] Error registering user", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @PutMapping
  @ApiOperation(value = "Update user", response = User.class)
  public ResponseEntity<User> update(@RequestBody() User user) {
    try {
      logger.info("[USER - UPDATE] Registering user");
      user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
      User userRegister = userService.update(user);

      return new ResponseEntity<>(userRegister, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      logger.error("[USER - UPDATE] User not found", e);
      throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "User not found", e);
    } catch (Exception e) {
      logger.error("[USER - UPDATE] Error registering user", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @PutMapping("/{id}/houses")
  @ApiOperation(value = "Add house", response = House.class, notes = "House must exist")
  public ResponseEntity<House> addUserHouseById(@PathVariable("id") long id,
      @RequestBody() long houseId) {
    try {
      logger.info("[USER HOUSE - ADD] Add new house to user");
      House house = userService.addHouse(id, houseId);
      return new ResponseEntity<>(house, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      logger.error("[USER HOUSE - ADD] User not found", e);
      throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "User or house not found", e);
    } catch (Exception e) {
      logger.error("[USER HOUSE - ADD] Error adding house", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @DeleteMapping("/{id}/houses")
  @ApiOperation(value = "Delete relation house-user", response = House.class, notes = "House must exist")
  public ResponseEntity<House> removeUserHouseById(@PathVariable("id") long id,
      @RequestBody() long houseId) {
    try {
      logger.info("[USER HOUSE - REMOVE] Remove house to user");
      House house = userService.removeHouse(id, houseId);
      return new ResponseEntity<>(house, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      logger.error("[USER HOUSE - REMOVE] User not found", e);
      throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "User or house not found", e);
    } catch (Exception e) {
      logger.error("[USER HOUSE - REMOVE] Error removing house", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

}