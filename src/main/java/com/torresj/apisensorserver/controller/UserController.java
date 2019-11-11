package com.torresj.apisensorserver.controller;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExistsException;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.entities.House;
import com.torresj.apisensorserver.models.entities.User;
import com.torresj.apisensorserver.models.entities.User.Role;
import com.torresj.apisensorserver.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import java.util.List;

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
  public ResponseEntity<Page<User>> getUsers(
          @RequestParam(value = "page") int nPage,
          @RequestParam(value = "elements") int elements,
          @RequestParam(value = "filter", required = false) String filter,
          @RequestParam(value = "role", required = false) Role role,
          Principal principal) {
    try {
      logger
          .info("[USER - GET ALL] Getting users with filter {}, role{}, page {}, elements {} by user \"{}\"",filter, role, nPage,
              elements, principal.getName());
      if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "User does not have permission for this endpoint");
      }

      Page<User> page = userService.getUsers(filter,role,nPage, elements);

      logger
          .info(
              "[USER - GET ALL] Request for getting users with filter {}, role {}, page {}, elements {} finished by user \"{}\"", filter, role,
              nPage,
              elements, principal.getName());
      return new ResponseEntity<>(page, HttpStatus.OK);
    } catch (ResponseStatusException e) {
      logger.error("[USER - GET ALL] User does not have permission for this endpoint");
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          e.getReason(), e);
    } catch (Exception e) {
      logger.error("[USER - GET ALL] Error getting users", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @GetMapping(value = "/{id}")
  @ApiOperation(value = "Retrieve user by id", response = User.class)
  public ResponseEntity<User> getUserByID(@PathVariable("id") long id, Principal principal) {
    try {
      logger.info("[USER - GET] Getting user {} by user \"{}\"", id, principal.getName());
      if (!userService.isUserAllowed(principal.getName(), Role.ADMIN) && !userService
          .isSameUser(principal.getName(), id)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "User does not have permission for this endpoint");
      }

      User user = userService.getUser(id);

      logger.info("[USER - GET] Request getting user {} finished by user \"{}\"", id,
          principal.getName());
      return new ResponseEntity<>(user, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      logger.error("[USER - GET] User not found", e);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", e);
    } catch (ResponseStatusException e) {
      logger.error("[USER - GET] User does not have permission for this endpoint");
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          e.getReason(), e);
    } catch (Exception e) {
      logger.error("[USER - GET] Error getting user {}", id, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @GetMapping(value = "/logged")
  @ApiOperation(value = "Retrieve logged user data", response = User.class)
  public ResponseEntity<User> getUserLogged(Principal principal) {
    try {
      logger.info("[USER - LOGGED] Getting user logged {}", principal.getName());

      User user = userService.getUser(principal.getName());

      logger
          .info("[USER - LOGGED] Request for getting user logged {} finished", principal.getName());
      return new ResponseEntity<>(user, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      logger.error("[USER - LOGGED] User not found", e);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", e);
    } catch (Exception e) {
      logger.error("[USER - LOGGED] Error getting user {}", principal.getName(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @GetMapping(value = "/{id}/houses")
  @ApiOperation(value = "Retrieve user by id", response = User.class)
  public ResponseEntity<List<House>> getHouseSensorsByID(@PathVariable("id") long id, Principal principal) {
    try {
      logger.info("[USER HOUSE - GET] Getting houses sensor {} list by user \"{}\"", id,
          principal.getName());
      if (!userService.isUserAllowed(principal.getName(), Role.ADMIN) && !userService
          .isSameUser(principal.getName(), id)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "User does not have permission for this endpoint");
      }

      List<House> houses = userService.getHouses(id);

      logger.info("[USER HOUSE - GET] Request for getting houses sensor {} list by user \"{}\"", id,
          principal.getName());
      return new ResponseEntity<>(houses, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      logger.error("[USER HOUSE - GET] User not found", e);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", e);
    } catch (ResponseStatusException e) {
      logger.error("[USER HOUSE - GET] User does not have permission for this endpoint");
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          e.getReason(), e);
    } catch (Exception e) {
      logger.error("[USER HOUSE - GET] Error getting houses sensor {} list", id, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @PostMapping
  @ApiOperation(value = "Register user", response = User.class)
  public ResponseEntity<User> register(@RequestBody() User user, Principal principal) {
    try {
      logger
          .info("[USER - REGISTER] Registering user {} by user \"{}\"", user, principal.getName());
      if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "User does not have permission for this endpoint");
      }

      user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
      User userRegister = userService.register(user);

      logger.info("[USER - REGISTER] Request for registering user {} finished by user \"{}\"", user,
          principal.getName());
      return new ResponseEntity<>(userRegister, HttpStatus.CREATED);
    } catch (EntityAlreadyExistsException e) {
      logger.error("[USER - REGISTER] user already exists", e);
      throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "User already exists", e);
    } catch (ResponseStatusException e) {
      logger.error("[USER - REGISTER] User does not have permission for this endpoint");
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          e.getReason(), e);
    } catch (Exception e) {
      logger.error("[USER - REGISTER] Error registering user {}", user, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @PutMapping
  @ApiOperation(value = "Update user", response = User.class)
  public ResponseEntity<User> update(@RequestBody() User user, Principal principal) {
    try {
      logger
          .info("[USER - UPDATE] Updating user {} by user \"{}\"", user, principal.getName());
      if (!userService.isUserAllowed(principal.getName(), Role.ADMIN) && !userService
          .isSameUser(principal.getName(), user.getUsername())) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "User does not have permission for this endpoint");
      }
      if(user.getPassword()!= null)
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

      User userRegister = userService.update(user);

      logger.info("[USER - UPDATE] Request for updating user {} finished by user \"{}\"", user,
          principal.getName());
      return new ResponseEntity<>(userRegister, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      logger.error("[USER - UPDATE] User not found", e);
      throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "User not found", e);
    } catch (ResponseStatusException e) {
      logger.error("[USER - UPDATE] User does not have permission for this endpoint");
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          e.getReason(), e);
    } catch (Exception e) {
      logger.error("[USER - UPDATE] Error registering user", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @DeleteMapping("/{id}")
  @ApiOperation(value = "Remove user", response = User.class)
  public ResponseEntity<User> remove(@PathVariable("id") long id, Principal principal) {
    try {
      logger.info("[USER - REMOVE] Removing user {} by user \"{}\"", id, principal.getName());
      if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "User does not have permission for this endpoint");
      }

      User user = userService.remove(id);

      logger.info("[USER - REMOVE] Request for removing user {} finished by user \"{}\"", id,
          principal.getName());
      return new ResponseEntity<>(user, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      logger.error("[USER - REMOVE] User not found", e);
      throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "User not found", e);
    } catch (ResponseStatusException e) {
      logger.error("[USER - REMOVE] User does not have permission for this endpoint");
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          e.getReason(), e);
    } catch (Exception e) {
      logger.error("[USER - REMOVE] Error removing user {}", id, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @PutMapping("/{id}/houses/{houseId}")
  @ApiOperation(value = "Add house", response = House.class, notes = "House must exist")
  public ResponseEntity<House> addUserHouseById(@PathVariable("id") long id,
      @PathVariable() long houseId, Principal principal) {
    try {
      logger.info("[USER HOUSE - ADD] Add house {} to user {} by user \"{}\"", houseId, id,
          principal.getName());
      if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "User does not have permission for this endpoint");
      }

      House house = userService.addHouse(id, houseId);

      logger.info("[USER HOUSE - ADD] Request add house {} to user {} finished by user \"{}\"",
          houseId, id,
          principal.getName());
      return new ResponseEntity<>(house, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      logger.error("[USER HOUSE - ADD] User not found", e);
      throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "User or house not found", e);
    } catch (ResponseStatusException e) {
      logger.error("[USER HOUSE - ADD] User does not have permission for this endpoint");
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          e.getReason(), e);
    } catch (Exception e) {
      logger.error("[USER HOUSE - ADD] Error adding house {} to user {}", houseId, id, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

  @DeleteMapping("/{id}/houses/{houseId}")
  @ApiOperation(value = "Delete relation house-user", response = House.class, notes = "House must exist")
  public ResponseEntity<House> removeUserHouseById(@PathVariable("id") long id,
      @PathVariable() long houseId, Principal principal) {
    try {
      logger.info("[USER HOUSE - REMOVE] Removing house {} to user {} by user \"{}\"", houseId, id,
          principal.getName());
      if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "User does not have permission for this endpoint");
      }

      logger.info(
          "[USER HOUSE - REMOVE] Request removing house {} to user {} finished by user \"{}\"",
          houseId, id,
          principal.getName());
      House house = userService.removeHouse(id, houseId);
      return new ResponseEntity<>(house, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      logger.error("[USER HOUSE - REMOVE] User not found", e);
      throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "User or house not found", e);
    } catch (ResponseStatusException e) {
      logger.error("[USER HOUSE - REMOVE] User does not have permission for this endpoint");
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          e.getReason(), e);
    } catch (Exception e) {
      logger.error("[USER HOUSE - REMOVE] Error removing house {} to user {}", houseId, id, e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
    }
  }

}