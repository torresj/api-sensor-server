package com.torresj.apisensorserver.services;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityHasRelationsException;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.SensorType;
import org.springframework.data.domain.Page;

public interface SensorTypeService {

  Page<SensorType> getSensorTypes(int nPage, int elements);

  SensorType getSensorType(long id) throws EntityNotFoundException;

  SensorType register(SensorType type) throws EntityAlreadyExists;

  SensorType update(SensorType type) throws EntityNotFoundException;

  SensorType remove(long id) throws EntityNotFoundException, EntityHasRelationsException;
}