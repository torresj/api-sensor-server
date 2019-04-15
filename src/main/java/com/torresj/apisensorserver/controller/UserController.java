package com.torresj.apisensorserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.User;
import com.torresj.apisensorserver.services.UserService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("v1/users")
@Api(value = "v1/users", description = "Operations about users")
public class UserController {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(UserController.class);

    /* Services */
    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping
    @ApiOperation(value = "Retrieve Users", notes = "Pageable data are required and de maximum records per page are 100", response = User.class, responseContainer = "List")
    public ResponseEntity<Page<User>> getUsers(@RequestParam(value = "page") int nPage,
            @RequestParam(value = "elements") int elements) {
        try {
            logger.info("[USER - GET ALL] Get users from DB with page " + nPage + ", elements " + elements);

            Page<User> page = userService.getUsers(nPage, elements);

            return new ResponseEntity<Page<User>>(page, HttpStatus.OK);
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

            return new ResponseEntity<User>(user, HttpStatus.OK);
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

            return new ResponseEntity<User>(user, HttpStatus.OK);
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
    public ResponseEntity<User> register(@RequestBody(required = true) User user) {
        try {
            logger.info("[USER - REGISTER] Registering user");
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            User userRegister = userService.register(user);

            return new ResponseEntity<User>(userRegister, HttpStatus.CREATED);
        } catch (EntityAlreadyExists e) {
            logger.error("[HOUSE - REGISTER] House already exists", e);
            throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "House already exists", e);
        } catch (Exception e) {
            logger.error("[HOUSE - REGISTER] Error registering house", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

}