package com.torresj.apisensorserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.time.LocalDate;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.Variable;
import com.torresj.apisensorserver.services.VariableService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/v1/variables")
@Api(value = "v1/variables", description = "Operations about variables")
public class VariableController {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(VariableController.class);

    /* Services */
    @Autowired
    private VariableService variableService;

    @GetMapping
    @ApiOperation(value = "Retrieve variables", notes = "Pageable data are required and de maximum records per page are 100", response = Variable.class, responseContainer = "List")
    public ResponseEntity<Page<Variable>> getVariables(@RequestParam(value = "page") int nPage,
            @RequestParam(value = "elements") int elements) {
        try {
            logger.info("[VARIABLE - GET ALL] Get variables from DB with page " + nPage + ", elements " + elements);

            Page<Variable> page = variableService.getVariables(nPage, elements);

            return new ResponseEntity<Page<Variable>>(page, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("[VARIABLE - GET ALL] Error getting variables", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Retrieve variable by id", response = Variable.class)
    public ResponseEntity<Variable> getVariableById(@PathVariable("id") long id) {
        try {
            logger.info("[VARIABLE - GET] Get variable from DB with id: " + id);

            Variable variable = variableService.getVariable(id);

            return new ResponseEntity<Variable>(variable, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("[VARIABLE - GET] Error variable not found", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Variable not found", e);
        } catch (Exception e) {
            logger.error("[VARIABLE - GET] Error getting variable", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @PutMapping
    @ApiOperation(value = "Update variable", response = Variable.class)
    public ResponseEntity<Variable> update(@RequestBody(required = true) Variable variable) {
        try {
            logger.info("[VARIABLE - REGISTER] Register variable: " + variable);

            Variable variableRegister = variableService.update(variable);

            return new ResponseEntity<Variable>(variableRegister, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("[VARIABLE - GET] Error registering variable", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @PostMapping
    @ApiOperation(value = "Register variable", response = Variable.class)
    public ResponseEntity<Variable> register(@RequestBody(required = true) Variable variable) {
        try {
            logger.info("[VARIABLE - REGISTER] Register variable: " + variable);

            Variable variableRegister = variableService.register(variable);

            return new ResponseEntity<Variable>(variableRegister, HttpStatus.CREATED);
        } catch (EntityAlreadyExists e) {
            logger.error("[VARIABLE - REGISTER] Variable already exists", e);
            throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "Variable already exists", e);
        } catch (Exception e) {
            logger.error("[VARIABLE - REGISTER] Error registering variable", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Delete variable by id", response = Variable.class)
    public ResponseEntity<Variable> delete(@PathVariable("id") long id) {
        try {
            logger.info("[VARIABLE - DELETE] Delete variable with id: " + id);

            Variable variableRegister = variableService.deleteVariable(id);

            return new ResponseEntity<Variable>(variableRegister, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("[VARIABLE - DELETE] Variable not exists", e);
            throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "Variable not exists", e);
        } catch (Exception e) {
            logger.error("[VARIABLE - DELETE] Error deleting variable", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

}