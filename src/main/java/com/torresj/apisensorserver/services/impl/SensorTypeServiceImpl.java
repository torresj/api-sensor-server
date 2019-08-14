package com.torresj.apisensorserver.services.impl;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityHasRelationsException;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.entities.SensorType;
import com.torresj.apisensorserver.repositories.SensorRepository;
import com.torresj.apisensorserver.repositories.SensorTypeRepository;
import com.torresj.apisensorserver.services.SensorTypeService;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SensorTypeServiceImpl implements SensorTypeService {

  /* Logs */
  private static final Logger logger = LogManager.getLogger(SensorTypeServiceImpl.class);

  /* Respositories */
  private SensorTypeRepository sensorTypeRepository;

  private SensorRepository sensorRepository;

  public SensorTypeServiceImpl(SensorTypeRepository sensorTypeRepository,
      SensorRepository sensorRepository) {
    this.sensorTypeRepository = sensorTypeRepository;
    this.sensorRepository = sensorRepository;
  }

  @Override
  public Page<SensorType> getSensorTypes(int nPage, int elements) {
    logger.debug("[SENSOR TYPES - SERVICE] Service for getting types start");
    PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());
    Page<SensorType> page = sensorTypeRepository.findAll(pageRequest);
    logger
        .debug("[SENSOR TYPES - SERVICE] Service for getting types end. Types: {} ",
            page.getContent());
    return page;
  }

  @Override
  public Page<SensorType> getSensorTypes(int nPage, int elements, String name) {
    logger
        .debug("[SENSOR TYPES - SERVICE] Service for get type service by name \"{}\" start", name);
    PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());
    Page<SensorType> page = sensorTypeRepository.findByName(name, pageRequest);
    logger
        .debug("[SENSOR TYPES - SERVICE] Service for get type service by name \"{}\" end. Type: {}",
            name,
            page.getContent());
    return page;
  }

  @Override
  public SensorType getSensorType(long id) throws EntityNotFoundException {
    logger.debug("[SENSOR TYPES - SERVICE] Service for get type {} start", id);
    SensorType type = sensorTypeRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException());
    logger.debug("[SENSOR TYPES - SERVICE] Service for get type {} end. Type: {}", id, type);
    return type;
  }

  @Override
  public SensorType register(SensorType type) throws EntityAlreadyExists {
    logger.debug("[SENSOR TYPES - REGISTER] Register new sensor type " + type);
    Optional<SensorType> sensorType = sensorTypeRepository.findByName(type.getName());
    if (sensorType.isPresent()) {
      throw new EntityAlreadyExists();
    } else {
      return sensorTypeRepository.save(type);
    }
  }

  @Override
  public SensorType update(SensorType type) throws EntityNotFoundException {
    logger.debug("[SENSOR TYPES - UPDATE] Update new sensor type " + type);
    SensorType sensorTypeEntity = sensorTypeRepository.findByName(type.getName())
        .orElseThrow(EntityNotFoundException::new);
    type.setId(sensorTypeEntity.getId());
    return sensorTypeRepository.save(type);
  }

  @Override
  public SensorType remove(long id) throws EntityNotFoundException, EntityHasRelationsException {
    logger.debug("[SENSOR TYPES - REMOVE] Searching sensor type by " + id);
    SensorType sensorTypeEntity = sensorTypeRepository.findById(id)
        .orElseThrow(EntityNotFoundException::new);
    logger.debug("[SENSOR TYPES - REMOVE] Searching if exists sensors with this sensor type");
    PageRequest pageRequest = PageRequest.of(0, 1);
    Page response = sensorRepository.findBySensorTypeId(id, pageRequest);
    if (response != null && !response.getContent().isEmpty()) {
      throw new EntityHasRelationsException();
    } else {
      sensorTypeRepository.delete(sensorTypeEntity);
    }
    return sensorTypeEntity;
  }
}
