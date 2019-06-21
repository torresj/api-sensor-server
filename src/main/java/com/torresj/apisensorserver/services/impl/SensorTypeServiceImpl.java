package com.torresj.apisensorserver.services.impl;

import com.torresj.apisensorserver.exceptions.EntityHasRelationsException;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.jpa.SensorRepository;
import com.torresj.apisensorserver.jpa.SensorTypeRepository;
import com.torresj.apisensorserver.models.SensorType;
import com.torresj.apisensorserver.services.SensorTypeService;
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
    logger.debug("[SENSOR TYPES - GET ALL] Getting sensor types");
    PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());
    return sensorTypeRepository.findAll(pageRequest);
  }

  @Override
  public SensorType getSensorType(long id) throws EntityNotFoundException {
    logger.debug("[SENSOR TYPES - GET] Searching sensor type by id: " + id);
    return sensorTypeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
  }

  @Override
  public SensorType register(SensorType type) {
    logger.debug("[SENSOR TYPES - REGISTER] Register new sensor type " + type);
    return sensorTypeRepository.findByName(type.getName()).orElse(sensorTypeRepository.save(type));
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
    if (!sensorRepository.findBySensorTypeId(id, pageRequest).getContent().isEmpty()) {
      throw new EntityHasRelationsException();
    } else {
      sensorTypeRepository.delete(sensorTypeEntity);
    }
    return sensorTypeEntity;
  }
}
