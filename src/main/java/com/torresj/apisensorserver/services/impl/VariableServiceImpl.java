package com.torresj.apisensorserver.services.impl;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.Sensor;
import com.torresj.apisensorserver.models.User;
import com.torresj.apisensorserver.models.Variable;
import com.torresj.apisensorserver.models.VariableSensorRelation;
import com.torresj.apisensorserver.repositories.HouseRepository;
import com.torresj.apisensorserver.repositories.SensorRepository;
import com.torresj.apisensorserver.repositories.UserHouseRelationRepository;
import com.torresj.apisensorserver.repositories.UserRepository;
import com.torresj.apisensorserver.repositories.VariableRepository;
import com.torresj.apisensorserver.repositories.VariableSensorRelationRepository;
import com.torresj.apisensorserver.services.VariableService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
    logger.debug("[VARIABLE - GET] Getting variables");
    PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());

    return variableRepository.findAll(pageRequest);
  }

  @Override
  public Page<Variable> getVariables(int nPage, int elements, String name) {
    logger.debug("[VARIABLE - GET] Getting variables");
    PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());

    return variableRepository.findByName(name, pageRequest);
  }

  @Override
  public Variable getVariable(long id) throws EntityNotFoundException {
    logger.debug("[VARIABLE - GET VARIABLE] Searching variable by id: " + id);

    return variableRepository.findById(id).orElseThrow(EntityNotFoundException::new);
  }

  @Override
  public Variable update(Variable variable) throws EntityNotFoundException {
    logger.debug("[VARIABLE - UPDATE] Updating variable: " + variable);
    Variable entity = variableRepository.findByName(variable.getName())
        .orElseThrow(EntityNotFoundException::new);

    logger.debug("[VARIABLE - UPDATE] Variable exists. Updating ...");
    variable.setId(entity.getId());
    variableRepository.save(variable);
    return variable;
  }

  @Override
  public Variable register(Variable variable) throws EntityAlreadyExists {
    logger.debug("[VARIABLE - REGISTER] Registering variable: " + variable);
    Optional<Variable> entity = variableRepository.findByName(variable.getName());
    if (entity.isPresent()) {
      throw new EntityAlreadyExists();
    } else {
      Variable variableSaved = variableRepository.save(variable);
      return variableSaved;
    }
  }

  @Override
  public Variable deleteVariable(long id) throws EntityNotFoundException {
    logger.debug("[VARIABLE - DELETE] Searching variable by id: " + id);

    Variable variable = variableRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    variableRepository.delete(variable);

    logger.debug("[VARIABLE - DELETE] Delete sensor - variable relation");
    variableSensorRelationRepository.findByVariableId(id).stream()
        .forEach(variableSensorRelationRepository::delete);

    return variable;
  }

  @Override
  public Page<Sensor> getSensors(long id, int nPage, int elements) {

    logger.debug("[SENSORS HAVE VARIABLE - GET] Searching sensors whit variable by id: " + id);
    PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());
    List<Long> ids = variableSensorRelationRepository.findByVariableId(id).stream()
        .map(VariableSensorRelation::getSensorId).collect(
            Collectors.toList());

    return sensorRepository.findByIdIn(ids, pageRequest);
  }

  @Override
  public boolean hasUserVisibilityVariable(String name, long id) throws EntityNotFoundException {
    User user = userRepository.findByUsername(name).get();
    variableRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    return userHouseRelationRepository.findByUserId(user.getId()).stream()
        .map(userHouseRelation -> houseRepository.findById(userHouseRelation.getHouseId()).get())
        .flatMap(house -> sensorRepository.findByHouseId(house.getId()).stream())
        .flatMap(sensor -> variableSensorRelationRepository.findBySensorId(sensor.getId()).stream())
        .anyMatch(relation -> relation.getVariableId() == id);
  }
}
