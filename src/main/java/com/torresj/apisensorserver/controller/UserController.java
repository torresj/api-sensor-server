package com.torresj.apisensorserver.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import com.torresj.apisensorserver.models.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("v1/users")
public class UserController {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(UserController.class);

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        try {
            logger.info("[USERS - GET ALL] Get all users from DB");
            List<User> users = null;
            return new ResponseEntity<List<User>>(users, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("[USERS - GET ALL] Error getting users from DB", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }
}