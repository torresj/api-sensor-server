package com.torresj.apisensorserver.controller;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.time.LocalDate;

import com.torresj.apisensorserver.models.House;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("v1/houses")
@Api(value = "v1/houses", description = "Operations about houses")
public class HouseController {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(HouseController.class);

    @GetMapping
    @ApiOperation(value = "Retrieve Houses", notes = "Pageable data are required and de maximum records per page are 100", response = House.class, responseContainer = "List")
    public ResponseEntity<Page<House>> getSensors(@RequestParam(value = "page") int nPage,
            @RequestParam(value = "elements") int elements) {
        try {
            logger.info("[SENSOR - GET ALL] Get sensors from DB with page " + nPage + ", elements " + elements);

            Page<House> page = null;

            return new ResponseEntity<Page<House>>(page, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("[SENSOR - GET ALL] Error getting sensors from DB", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }
}