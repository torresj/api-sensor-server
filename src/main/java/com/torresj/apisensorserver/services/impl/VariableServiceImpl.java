package com.torresj.apisensorserver.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExistsException;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.entities.Sensor;
import com.torresj.apisensorserver.models.entities.User;
import com.torresj.apisensorserver.models.entities.Variable;
import com.torresj.apisensorserver.models.entities.VariableSensorRelation;
import com.torresj.apisensorserver.repositories.HouseRepository;
import com.torresj.apisensorserver.repositories.SensorRepository;
import com.torresj.apisensorserver.repositories.UserHouseRelationRepository;
import com.torresj.apisensorserver.repositories.UserRepository;
import com.torresj.apisensorserver.repositories.VariableRepository;
import com.torresj.apisensorserver.repositories.VariableSensorRelationRepository;
import com.torresj.apisensorserver.services.VariableService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class VariableServiceImpl implements VariableService {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(VariableServiceImpl.class);

    private VariableRepository variableRepository;

    private SensorRepository sensorRepository;

    private UserRepository userRepository;

    private VariableSensorRelationRepository variableSensorRelationRepository;

    private HouseRepository houseRepository;

    private UserHouseRelationRepository userHouseRelationRepository;

    public VariableServiceImpl(VariableRepository variableRepository,
            SensorRepository sensorRepository,
            UserRepository userService,
            VariableSensorRelationRepository variableSensorRelationRepository,
            HouseRepository houseRepository,
            UserHouseRelationRepository userHouseRelationRepository) {
        this.variableRepository = variableRepository;
        this.sensorRepository = sensorRepository;
        this.userRepository = userService;
        this.variableSensorRelationRepository = variableSensorRelationRepository;
        this.houseRepository = houseRepository;
        this.userHouseRelationRepository = userHouseRelationRepository;
    }

    @Override
    public Page<Variable> getVariables(int nPage, int elements) {
        logger.debug("[VARIABLE - SERVICE] Service for getting variables start");
        PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());
        Page<Variable> page = variableRepository.findAll(pageRequest);
        logger.debug("[VARIABLE - SERVICE] Service for getting variables end. Variables: {}",
                page.getContent());
        return page;
    }

    @Override
    public Page<Variable> getVariables(int nPage, int elements, String name) {
        logger.debug("[VARIABLE - SERVICE] Service for getting variables by name {} start", name);
        PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());
        Page<Variable> page = variableRepository.findByName(name, pageRequest);
        logger.debug("[VARIABLE - SERVICE] Service for getting variables by name {} end. Variables: {}",
                name, page.getContent());
        return page;
    }

    @Override
    public Variable getVariable(long id) throws EntityNotFoundException {
        logger.debug("[VARIABLE - SERVICE] Service for getting variable {} start", id);
        Variable variable = variableRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        logger.debug("[VARIABLE - SERVICE] Service for getting variable {} end. Variable:{}", id,
                variable);
        return variable;
    }

    @Override
    public Variable update(Variable variable) throws EntityNotFoundException {
        logger.debug("[VARIABLE - SERVICE] Service for update variable start. Variable: {}", variable);
        Variable entity = variableRepository.findByName(variable.getName())
                .orElseThrow(EntityNotFoundException::new);

        logger.debug("[VARIABLE - UPDATE] Variable exists. Updating ...");
        variable.setId(entity.getId());
        Variable variableUpdated = variableRepository.save(variable);
        logger.debug("[VARIABLE - SERVICE] Service for update variable end. Variable: {}",
                variableUpdated);
        return variableUpdated;
    }

    @Override
    public Variable register(Variable variable) throws EntityAlreadyExistsException {
        logger
                .debug("[VARIABLE - SERVICE] Service for register variable start. Variable: {}", variable);
        Optional<Variable> entity = variableRepository.findByName(variable.getName());
        if (entity.isPresent()) {
            throw new EntityAlreadyExistsException();
        } else {
            Variable variableSaved = variableRepository.save(variable);
            logger.debug("[VARIABLE - SERVICE] Service for register variable end. Variable: {}",
                    variableSaved);
            return variableSaved;
        }
    }

    @Override
    public Variable deleteVariable(long id) throws EntityNotFoundException {
        logger.debug("[VARIABLE - SERVICE] Service for delete variable {} start", id);

        Variable variable = variableRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        variableRepository.delete(variable);

        logger.debug("[VARIABLE - SERVICE] Delete sensor - variable relation");
        variableSensorRelationRepository.findByVariableId(id).stream()
                .forEach(variableSensorRelationRepository::delete);

        logger.debug("[VARIABLE - SERVICE] Service for delete variable {} end. Variable: {}", id,
                variable);
        return variable;
    }

    @Override
    public Page<Sensor> getSensors(long id, int nPage, int elements) {
        logger.debug("[VARIABLE - SERVICE] Service for getting variable {} sensors start", id);
        PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());
        List<Long> ids = variableSensorRelationRepository.findByVariableId(id).stream()
                .map(VariableSensorRelation::getSensorId).collect(
                        Collectors.toList());
        Page<Sensor> page = sensorRepository.findByIdIn(ids, pageRequest);
        logger
                .debug("[VARIABLE - SERVICE] Service for getting variable {} sensors end. Sensors: {}", id,
                        page.getContent());
        return page;
    }

    @Override
    public boolean hasUserVisibilityVariable(String name, long id) throws EntityNotFoundException {
        logger
                .debug("[USER - SERVICE] Service for check if user {} has visibility for variable {} start",
                        name, id);
        logger.debug("[USER - SERVICE] Searching user {}", name);
        User user = userRepository.findByUsername(name).get();
        logger.debug("[USER - SERVICE] Searching variable {}", id);
        variableRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        boolean hasVisibility = userHouseRelationRepository.findByUserId(user.getId()).stream()
                .map(userHouseRelation -> houseRepository.findById(userHouseRelation.getHouseId()).get())
                .flatMap(house -> sensorRepository.findByHouseId(house.getId()).stream())
                .flatMap(sensor -> variableSensorRelationRepository.findBySensorId(sensor.getId()).stream())
                .anyMatch(relation -> relation.getVariableId() == id);
        logger.debug(
                "[USER - SERVICE] Service for check if user {} has visibility for variable {} end. Result: {}",
                name, id, hasVisibility);
        return hasVisibility;
    }
}
