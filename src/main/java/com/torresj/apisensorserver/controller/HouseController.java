package com.torresj.apisensorserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.List;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.House;
import com.torresj.apisensorserver.models.Sensor;
import com.torresj.apisensorserver.services.HouseService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("v1/houses")
@Api(value = "v1/houses", description = "Operations about houses")
public class HouseController {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(HouseController.class);

    /* Services */
    @Autowired
    private HouseService houseService;

    @GetMapping
    @ApiOperation(value = "Retrieve Houses", notes = "Pageable data are required and de maximum records per page are 100", response = House.class, responseContainer = "List")
    public ResponseEntity<Page<House>> getHouses(@RequestParam(value = "page") int nPage,
            @RequestParam(value = "elements") int elements) {
        try {
            logger.info("[HOUSE - GET ALL] Get houses from DB with page " + nPage + ", elements " + elements);

            Page<House> page = houseService.getHouses(nPage, elements);

            return new ResponseEntity<Page<House>>(page, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("[HOUSE - GET ALL] Error getting houses from DB", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Retrieve house by id", response = House.class)
    public ResponseEntity<House> getHouseByID(@PathVariable("id") long id) {
        try {
            logger.info("[HOUSE - GET] Get houses from DB with id: " + id);

            House house = houseService.getHouse(id);

            return new ResponseEntity<House>(house, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("[HOUSE - GET] House not found", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "House not found", e);
        } catch (Exception e) {
            logger.error("[HOUSE - GET] Error getting houses from DB", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @GetMapping(value = "/{houseId}/sensors")
    @ApiOperation(value = "Retrieve Sensors from house", notes = "Pageable data are required and de maximum records per page are 100", response = Sensor.class, responseContainer = "List")
    public ResponseEntity<List<Sensor>> getSensorsByHouseID(@PathVariable("houseId") long id,
            @RequestParam(value = "page") int nPage, @RequestParam(value = "elements") int elements) {
        try {
            logger.info("[HOUSE - SENSORS] Get sensors from DB with house id: " + id);

            List<Sensor> sensors = houseService.getSensors(id, nPage, elements);

            return new ResponseEntity<List<Sensor>>(sensors, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("[HOUSE - GET] House not found", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "House not found", e);
        } catch (Exception e) {
            logger.error("[HOUSE - GET] Error getting houses from DB", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @PutMapping
    @ApiOperation(value = "Update house", response = House.class)
    public ResponseEntity<House> update(@RequestBody(required = true) House house) {
        try {
            logger.info("[HOUSE - UPDDATE] Updating house -> " + house);
            House houseRegister = houseService.update(house);

            return new ResponseEntity<House>(houseRegister, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("[HOUSE - UPDATE] Error updating house", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @PostMapping
    @ApiOperation(value = "Register house", response = House.class)
    public ResponseEntity<House> register(@RequestBody(required = true) House house) {
        try {
            logger.info("[HOUSE - REGISTER] Registering house -> " + house);
            House houseRegister = houseService.register(house);

            return new ResponseEntity<House>(houseRegister, HttpStatus.CREATED);
        } catch (EntityAlreadyExists e) {
            logger.error("[HOUSE - REGISTER] House already exists", e);
            throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "House already exists", e);
        } catch (Exception e) {
            logger.error("[HOUSE - REGISTER] Error registering house", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Delete house", response = House.class)
    public ResponseEntity<House> delete(@PathVariable("id") long id) {
        try {
            logger.info("[HOUSE - REMOVE] Remove house from DB with id: " + id);

            House house = houseService.removeHouse(id);

            return new ResponseEntity<House>(house, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error("[HOUSE - REMOVE] House not found", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "House not found", e);
        } catch (Exception e) {
            logger.error("[HOUSE - REMOVE] Error removing houses from DB", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }
}