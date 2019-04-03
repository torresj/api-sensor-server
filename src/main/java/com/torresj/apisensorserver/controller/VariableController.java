package com.torresj.apisensorserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
public class VariableController {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(VariableController.class);

    /* Services */
    @Autowired
    private VariableService variableService;

    @GetMapping
    public ResponseEntity<List<Variable>> getVariables() {
        try {
            logger.info("[VARIABLE - GET ALL] Get all variables from DB");

            List<Variable> variables = variableService.getVariables();

            return new ResponseEntity<List<Variable>>(variables, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("[VARIABLE - GET ALL] Error getting variables", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @GetMapping(value = "/{id}")
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
    public ResponseEntity<Variable> delete(@PathVariable("id") long id) {
        try {
            logger.info("[VARIABLE - DELETE] Delete variable with id: " + id);

            Variable variableRegister = variableService.deleteVariable(id);

            return new ResponseEntity<Variable>(variableRegister, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("[VARIABLE - DELETE] Variable already exists", e);
            throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "Sensor already exists", e);
        } catch (Exception e) {
            logger.error("[VARIABLE - DELETE] Error deleting variable", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

}