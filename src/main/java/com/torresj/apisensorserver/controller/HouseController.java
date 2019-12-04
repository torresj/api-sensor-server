package com.torresj.apisensorserver.controller;

import java.security.Principal;
import java.util.List;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExistsException;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.entities.House;
import com.torresj.apisensorserver.models.entities.Sensor;
import com.torresj.apisensorserver.models.entities.User;
import com.torresj.apisensorserver.models.entities.User.Role;
import com.torresj.apisensorserver.services.HouseService;
import com.torresj.apisensorserver.services.UserService;

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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("v1/houses")
@Api(value = "v1/houses")
public class HouseController {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(HouseController.class);

    /* Services */
    private HouseService houseService;

    private UserService userService;

    public HouseController(HouseService houseService,
            UserService userService) {
        this.houseService = houseService;
        this.userService = userService;
    }

    @GetMapping
    @ApiOperation(value = "Retrieve Houses", notes = "Pageable data are required and de maximum records per page are 100", response = House.class, responseContainer = "List")
    public ResponseEntity<Page<House>> getHouses(@RequestParam(value = "page") int nPage,
            @RequestParam(value = "elements") int elements,
            @RequestParam(value = "filter", required = false) String filter,
            Principal principal) {
        try {
            logger.info(
                    "[HOUSE - GET ALL] Getting houses from DB with filter {}, page {}, elements {} by user \"{}\"",
                    filter,nPage, elements, principal.getName());
            if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "User does not have permission for this endpoint");
            }

            Page<House> page = houseService.getHouses(filter,nPage, elements);

            logger.info("[HOUSE - GET ALL] Request for houses finished by user \"{}\"",
                    principal.getName());
            return new ResponseEntity<>(page, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            logger.error("[HOUSE - GET ALL] User does not have permission for this endpoint");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.getReason(), e);
        } catch (Exception e) {
            logger.error("[HOUSE - GET ALL] Error getting houses from DB", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @GetMapping(value = "/all")
    @ApiOperation(value = "Retrieve Houses", response = House.class, responseContainer = "List")
    public ResponseEntity<List<House>> getHousesAll(Principal principal) {
        try {
            logger.info(
                    "[HOUSE - GET ALL] Getting houses from DB by user \"{}\"",
                    principal.getName());
            if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "User does not have permission for this endpoint");
            }

            List<House> houses = houseService.getHouses();

            logger.info("[HOUSE - GET ALL] Request for houses finished by user \"{}\"",
                    principal.getName());
            return new ResponseEntity<>(houses, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            logger.error("[HOUSE - GET ALL] User does not have permission for this endpoint");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.getReason(), e);
        } catch (Exception e) {
            logger.error("[HOUSE - GET ALL] Error getting houses from DB", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Retrieve house by id", response = House.class)
    public ResponseEntity<House> getHouseByID(@PathVariable("id") long id, Principal principal) {
        try {
            logger.info("[HOUSE - GET] Getting house {} by user \"{}\"", id,
                    principal.getName());
            if (!userService.isUserAllowed(principal.getName(), Role.ADMIN, Role.STATION)
                    && !houseService
                    .hasUserVisibilityHouse(principal.getName(), id)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "User does not have permission for this endpoint");
            }

            House house = houseService.getHouse(id);

            logger.info("[HOUSE - GET] Request for house {} finished by user \"{}\"", id,
                    principal.getName());
            return new ResponseEntity<>(house, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            logger.error("[HOUSE - GET] User does not have permission for this endpoint");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.getReason(), e);
        } catch (EntityNotFoundException e) {
            logger.error("[HOUSE - GET] House not found", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "House not found", e);
        } catch (Exception e) {
            logger.error("[HOUSE - GET] Error getting house {}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @GetMapping(value = "/{id}/users")
    @ApiOperation(value = "Retrieve Users who have visibility of house by id", response = User.class, responseContainer = "List")
    public ResponseEntity<List<User>> getHouseUsersByID(@PathVariable("id") long id, Principal principal) {
        try {
            logger.info("[HOUSE - GET USERS] Getting users of house {} by user \"{}\"", id,
                    principal.getName());
            if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "User does not have permission for this endpoint");
            }

            List<User> users = houseService.getHouseUsers(id);

            logger.info("[HOUSE - GET USERS] Request for getting users of house {} finished by user \"{}\"", id,
                    principal.getName());
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            logger.error("[HOUSE - GET USERS] User does not have permission for this endpoint");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.getReason(), e);
        } catch (EntityNotFoundException e) {
            logger.error("[HOUSE - GET USERS] House not found", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "House not found", e);
        } catch (Exception e) {
            logger.error("[HOUSE - GET USERS] Error getting users of house {}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @GetMapping(value = "/{houseId}/sensors")
    @ApiOperation(value = "Retrieve Sensors from house", response = Sensor.class, responseContainer = "List")
    public ResponseEntity<List<Sensor>> getSensorsByHouseID(@PathVariable("houseId") long id,
            Principal principal) {
        try {
            logger.info("[HOUSE - SENSORS] Getting house {} sensors by user \"{}\"", id,
                    principal.getName());
            if (!userService.isUserAllowed(principal.getName(), Role.ADMIN, Role.STATION)
                    && !houseService
                    .hasUserVisibilityHouse(principal.getName(), id)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "User does not have permission for this endpoint");
            }

            List<Sensor> sensors = houseService.getSensors(id);

            logger.info("[HOUSE - SENSORS] Request for house {} sensors finished by user \"{}\"", id,
                    principal.getName());
            return new ResponseEntity<>(sensors, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            logger.error("[HOUSE - SENSORS] User does not have permission for this endpoint");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.getReason(), e);
        } catch (EntityNotFoundException e) {
            logger.error("[HOUSE - SENSORS] House not found", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "House not found", e);
        } catch (Exception e) {
            logger.error("[HOUSE - SENSORS] Error getting house {} sensors", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @PutMapping
    @ApiOperation(value = "Update house", response = House.class)
    public ResponseEntity<House> update(@RequestBody House house, Principal principal) {
        try {
            logger.info("[HOUSE - UPDATE] Updating house {} by user \"{}\"", house, principal.getName());
            if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "User does not have permission for this endpoint");
            }

            House houseRegister = houseService.update(house);

            logger.info("[HOUSE - UPDATE] Request for updating house {} finished by user \"{}\"",
                    houseRegister.getId(), principal.getName());
            return new ResponseEntity<>(houseRegister, HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            logger.error("[HOUSE - UPDATE] User does not have permission for this endpoint");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.getReason(), e);
        } catch (Exception e) {
            logger.error("[HOUSE - UPDATE] Error updating house {}", house, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @PostMapping
    @ApiOperation(value = "Register house", response = House.class)
    public ResponseEntity<House> register(@RequestBody House house, Principal principal) {
        try {
            logger.info("[HOUSE - REGISTER] Registering house {} by user \"{}\"", house,
                    principal.getName());
            if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "User does not have permission for this endpoint");
            }

            House houseRegister = houseService.register(house);
            logger.info("[HOUSE - REGISTER] Request for registering house {} finished by user \"{}\"",
                    houseRegister.getId(),
                    principal.getName());
            return new ResponseEntity<>(houseRegister, HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            logger.error("[HOUSE - REGISTER] User does not have permission for this endpoint");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.getReason(), e);
        } catch (EntityAlreadyExistsException e) {
            logger.error("[HOUSE - REGISTER] House already exists", e);
            throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "House already exists", e);
        } catch (Exception e) {
            logger.error("[HOUSE - REGISTER] Error registering house {}", house, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Delete house", response = House.class)
    public ResponseEntity<House> delete(@PathVariable("id") long id, Principal principal) {
        try {
            logger.info("[HOUSE - REMOVE] Removing house {} by user \"{}\"", id,
                    principal.getName());
            if (!userService.isUserAllowed(principal.getName(), Role.ADMIN)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "User does not have permission for this endpoint");
            }

            House house = houseService.removeHouse(id);

            logger.info("[HOUSE - REMOVE] Request for removing house {} finished by user \"{}\"", id,
                    principal.getName());
            return new ResponseEntity<>(house, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            logger.error("[HOUSE - REMOVE] User does not have permission for this endpoint");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.getReason(), e);
        } catch (EntityNotFoundException e) {
            logger.error("[HOUSE - REMOVE] House not found", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "House not found", e);
        } catch (Exception e) {
            logger.error("[HOUSE - REMOVE] Error removing house {}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e);
        }
    }
}