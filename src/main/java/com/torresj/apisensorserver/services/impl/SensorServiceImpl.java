package com.torresj.apisensorserver.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.jpa.HouseRepository;
import com.torresj.apisensorserver.jpa.SensorRepository;
import com.torresj.apisensorserver.jpa.SensorTypeRepository;
import com.torresj.apisensorserver.jpa.VariableRepository;
import com.torresj.apisensorserver.jpa.VariableSensorRelationRepository;
import com.torresj.apisensorserver.models.Sensor;
import com.torresj.apisensorserver.models.Variable;
import com.torresj.apisensorserver.models.VariableSensorRelation;
import com.torresj.apisensorserver.rabbitmq.Producer;
import com.torresj.apisensorserver.services.SensorService;
import java.time.LocalDateTime;
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
public class SensorServiceImpl implements SensorService {

  /* Logs */
  private static final Logger logger = LogManager.getLogger(SensorServiceImpl.class);

  private SensorRepository sensorRepository;

  private VariableRepository variableRepository;

  private VariableSensorRelationRepository variableSensorRelationRepository;

  private SensorTypeRepository sensorTypeRepository;

  private HouseRepository houseRepository;

  private Producer producer;

  public SensorServiceImpl(SensorRepository sensorRepository,
      VariableRepository variableRepository,
      VariableSensorRelationRepository variableSensorRelationRepository,
      SensorTypeRepository sensorTypeRepository,
      HouseRepository houseRepository, Producer producer) {
    this.sensorRepository = sensorRepository;
    this.variableRepository = variableRepository;
    this.variableSensorRelationRepository = variableSensorRelationRepository;
    this.sensorTypeRepository = sensorTypeRepository;
    this.houseRepository = houseRepository;
    this.producer = producer;
  }

  @Override
  public Page<Sensor> getSensors(int nPage, int elements) {
    logger.debug("[SENSOR - GET] Getting sensors");
    PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());
    return sensorRepository.findAll(pageRequest);
  }

  @Override
  public Page<Sensor> getSensors(int nPage, int elements, Long sensorTypeId)
      throws EntityNotFoundException {
    logger.debug("[SENSOR - GET] Getting sensors filter by sensor type " + sensorTypeId);
    sensorTypeRepository.findById(sensorTypeId).orElseThrow(EntityNotFoundException::new);
    PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());
    return sensorRepository.findBySensorTypeId(sensorTypeId, pageRequest);
  }

  @Override
  public Sensor getSensor(long id) throws EntityNotFoundException {
    logger.debug("[SENSOR - GET SENSOR] Searching sensor by id: " + id);
    return sensorRepository.findById(id).orElseThrow(EntityNotFoundException::new);
  }

  @Override
  public Page<Variable> getVariables(long id, int nPage, int elements)
      throws EntityNotFoundException {
    logger.debug("[SENSOR VARIABLES - GET] Searching variables sensor by id: " + id);
    sensorRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());
    List<Long> ids = variableSensorRelationRepository.findBySensorId(id).stream()
        .map(VariableSensorRelation::getVariableId).collect(
            Collectors.toList());

    return variableRepository.findByIdIn(ids, pageRequest);
  }

  @Override
  public Variable addVariable(long sensorId, long variableId) throws EntityNotFoundException {
    logger.debug(
        "[SENSOR VARIABLES - ADD] Add variable " + variableId + " to variables sensor " + sensorId
            + " list");
    sensorRepository.findById(sensorId).orElseThrow(EntityNotFoundException::new);
    Variable variable = variableRepository.findById(variableId)
        .orElseThrow(EntityNotFoundException::new);
    VariableSensorRelation relation = new VariableSensorRelation();
    relation.setSensorId(sensorId);
    relation.setVariableId(variableId);
    variableSensorRelationRepository.save(relation);
    return variable;
  }

  @Override
  public Sensor update(Sensor sensor) throws EntityNotFoundException {
    logger.debug("[SENSOR - REGISTER] Searching sensor on DB");

    Sensor entity = sensorRepository.findByMac(sensor.getMac())
        .orElseThrow(EntityNotFoundException::new);

    logger.debug("[SENSOR - REGISTER] Sensor exists. Updating ...");
    sensor.setLastConnection(LocalDateTime.now());
    sensor.setId(entity.getId());
    //check for house id and sensor type id
    houseRepository.findById(sensor.getHouseId()).orElseThrow(EntityNotFoundException::new);
    sensorTypeRepository.findById(sensor.getSensorTypeId())
        .orElseThrow(EntityNotFoundException::new);
    sensor = sensorRepository.save(sensor);

    logger.debug("[SENSOR - REGISTER] Sending data to frontend via AMPQ message");

    ObjectNode ampqMsg = new ObjectMapper().createObjectNode();
    ampqMsg.put("type", "Update");
    ampqMsg.put("model", "Sensor");
    ampqMsg.set("data",
        new ObjectMapper().registerModule(new JavaTimeModule())
            .convertValue(sensor, JsonNode.class));

    producer.produceMsg(ampqMsg.toString());

    return sensor;
  }

  @Override
  public Sensor register(Sensor sensor) throws EntityNotFoundException {
    logger.debug("[SENSOR - REGISTER] Searching sensor on DB");
    Optional<Sensor> entity = sensorRepository.findByMac(sensor.getMac());

    if (entity.isPresent()) {
      logger.debug("[SENSOR - REGISTER] Sensor exists");
      return entity.get();
    } else {
      logger.info("[SENSOR - REGISTER] Registering new sensor ...");
      //check for house id and sensor type id
      houseRepository.findById(sensor.getHouseId()).orElseThrow(EntityNotFoundException::new);
      sensorTypeRepository.findById(sensor.getSensorTypeId())
          .orElseThrow(EntityNotFoundException::new);
      sensor.setLastConnection(LocalDateTime.now());
      sensor = sensorRepository.save(sensor);

      logger.debug("[SENSOR - REGISTER] Sending data to frontend via AMPQ message");

      ObjectNode ampqMsg = new ObjectMapper().createObjectNode();
      ampqMsg.put("type", "Create");
      ampqMsg.put("model", "Sensor");
      ampqMsg.set("data",
          new ObjectMapper().registerModule(new JavaTimeModule())
              .convertValue(sensor, JsonNode.class));

      producer.produceMsg(ampqMsg.toString());

      return sensor;
    }
  }

  @Override
  public Sensor removeSensor(long id) throws EntityNotFoundException {
    logger.debug("[SENSOR - REMOVE SENSOR] Searching sensor by id: " + id);
    Sensor sensor = sensorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
    sensorRepository.delete(sensor);

    logger.debug("[SENSOR - REGISTER] Remove relations variable - sensor");
    variableSensorRelationRepository.deleteBySensorId(id);

    logger.debug("[SENSOR - REGISTER] Sending data to frontend via AMPQ message");

    ObjectNode ampqMsg = new ObjectMapper().createObjectNode();
    ampqMsg.put("type", "Delete");
    ampqMsg.put("model", "Sensor");
    ampqMsg.set("data",
        new ObjectMapper().registerModule(new JavaTimeModule())
            .convertValue(sensor, JsonNode.class));

    producer.produceMsg(ampqMsg.toString());

    return sensor;
  }
}
