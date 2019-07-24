package com.torresj.apisensorserver.services;

import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.Sensor;
import com.torresj.apisensorserver.models.Variable;
import org.springframework.data.domain.Page;

public interface SensorService {

  Page<Sensor> getSensors(int nPage, int elements);

  Page<Sensor> getSensors(int nPage, int elements, Long sensorTypeId)
      throws EntityNotFoundException;

  Sensor getSensor(long id) throws EntityNotFoundException;

  Page<Variable> getVariables(long id, int nPage, int elements) throws EntityNotFoundException;

  Variable addVariable(long id, long variableId) throws EntityNotFoundException;

  Sensor update(Sensor sensor) throws EntityNotFoundException;

  Sensor register(Sensor sensor) throws EntityNotFoundException;

  Sensor removeSensor(long id) throws EntityNotFoundException;

  Variable removeVariable(long id, long variableId) throws EntityNotFoundException;

  boolean hasUserVisibilitySensor(String name, long id) throws EntityNotFoundException;
}
