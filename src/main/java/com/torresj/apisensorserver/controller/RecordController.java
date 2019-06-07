package com.torresj.apisensorserver.controller;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.time.LocalDate;

import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.Record;
import com.torresj.apisensorserver.services.RecordService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;

@RestController
@RequestMapping("v1/records")
@Api(value = "v1/records")
public class RecordController {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(RecordController.class);

    /* Services */
    private RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @GetMapping
    @ApiOperation(value = "Retrieve records", notes = "Pageable data are required and de maximum records per page are 100", response = Record.class, responseContainer = "List")
    public ResponseEntity<Page<Record>> getSensors(@RequestParam(value = "page") int nPage,
            @RequestParam(value = "elements") int elements,
            @RequestParam(value = "from") @DateTimeFormat(iso = ISO.DATE) LocalDate from,
            @RequestParam(value = "to") @DateTimeFormat(iso = ISO.DATE) LocalDate to) {
        try {
            logger.info("[RECORD - GET] Get records from DB with page " + nPage + ", elements " + elements + ", from "
                    + from + " to " + to);
            Page<Record> page = recordService.getRecords(nPage, elements, from, to);
            return new ResponseEntity<>(page, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("[RECORD - GET] Error getting records from DB", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Retrieve a record by id", response = Record.class)
    public ResponseEntity<Record> getSensor(@PathVariable("id") long id) {
        try {
            logger.info("[RECORD - GET] Get record from DB with id: " + id);
            Record record = recordService.getRecord(id);
            return new ResponseEntity<>(record, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("[RECORD - GET] Error record not found", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "record not found", e);
        } catch (Exception e) {
            logger.error("[RECORD - GET] Error getting records from DB", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }
}