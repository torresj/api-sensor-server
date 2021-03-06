package com.torresj.apisensorserver.services.impl;

import java.util.List;
import java.util.Optional;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExistsException;
import com.torresj.apisensorserver.exceptions.EntityHasRelationsException;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.entities.SensorType;
import com.torresj.apisensorserver.repositories.SensorRepository;
import com.torresj.apisensorserver.repositories.SensorTypeRepository;
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
        logger.debug("[SENSOR TYPES - SERVICE] Service for getting types start");
        PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());
        Page<SensorType> page = sensorTypeRepository.findAll(pageRequest);
        logger
                .debug("[SENSOR TYPES - SERVICE] Service for getting types end. Types: {} ",
                        page.getContent());
        return page;
    }

    @Override
    public List<SensorType> getSensorTypes() {
        logger.debug("[SENSOR TYPES - SERVICE] Service for getting types start");
        List<SensorType> types = sensorTypeRepository.findAll();
        logger
                .debug("[SENSOR TYPES - SERVICE] Service for getting types end. Types: {} ",
                        types);
        return types;
    }

    @Override
    public Page<SensorType> getSensorTypes(int nPage, int elements, String filter) {
        logger.debug("[SENSOR TYPES - SERVICE] Service for getting types with filter start");
        PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());
        Page<SensorType> page = sensorTypeRepository.findByNameContaining(filter,pageRequest);
        logger
                .debug("[SENSOR TYPES - SERVICE] Service for getting types with filter end. Types: {} ",
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
    public SensorType register(SensorType type) throws EntityAlreadyExistsException {
        logger.debug("[SENSOR TYPES - SERVICE] Service for register type start. type: {}", type);
        Optional<SensorType> sensorType = sensorTypeRepository.findByName(type.getName());
        if (sensorType.isPresent()) {
            throw new EntityAlreadyExistsException();
        } else {
            SensorType typeSaved = sensorTypeRepository.save(type);
            logger.debug("[SENSOR TYPES - SERVICE] Service for register type end. type: {}", typeSaved);
            return typeSaved;
        }
    }

    @Override
    public SensorType update(SensorType type) throws EntityNotFoundException {
        logger.debug("[SENSOR TYPES - SERVICE] Service for update type start. type: {}", type);
        SensorType sensorTypeEntity = sensorTypeRepository.findByName(type.getName())
                .orElseThrow(EntityNotFoundException::new);
        type.setId(sensorTypeEntity.getId());
        SensorType typeSaved = sensorTypeRepository.save(type);
        logger.debug("[SENSOR TYPES - SERVICE] Service for update type end. type: {}", typeSaved);
        return typeSaved;
    }

    @Override
    public SensorType remove(long id) throws EntityNotFoundException, EntityHasRelationsException {
        logger.debug("[SENSOR TYPES - SERVICE] Service for delete type {} start", id);
        SensorType sensorTypeEntity = sensorTypeRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        logger.debug("[SENSOR TYPES - SERVICE] Searching if exists sensors with type {}", id);
        PageRequest pageRequest = PageRequest.of(0, 1);
        Page response = sensorRepository.findBySensorTypeId(id, pageRequest);
        if (response != null && !response.getContent().isEmpty()) {
            throw new EntityHasRelationsException();
        } else {
            sensorTypeRepository.delete(sensorTypeEntity);
        }
        logger.debug("[SENSOR TYPES - SERVICE] Service for delete type {} end", id);
        return sensorTypeEntity;
    }
}
