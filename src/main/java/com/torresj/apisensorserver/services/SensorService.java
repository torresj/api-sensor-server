package com.torresj.apisensorserver.services;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.torresj.apisensorserver.exceptions.ActionException;
import com.torresj.apisensorserver.exceptions.EntityAlreadyExistsException;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.entities.Sensor;
import com.torresj.apisensorserver.models.entities.Variable;

import org.springframework.data.domain.Page;

public interface SensorService {

    Page<Sensor> getSensors(int nPage, int elements);

    Page<Sensor> getSensors(int nPage, int elements, Long sensorTypeId, String name)
            throws EntityNotFoundException;

    List<Sensor> getSensors();

    Sensor getSensor(long id) throws EntityNotFoundException;

    Page<Variable> getVariables(long id, int nPage, int elements) throws EntityNotFoundException;

    Variable addVariable(long id, long variableId) throws EntityNotFoundException;

    Sensor update(Sensor sensor) throws EntityNotFoundException;

    Sensor register(Sensor sensor) throws EntityNotFoundException, EntityAlreadyExistsException;

    Sensor removeSensor(long id) throws EntityNotFoundException;

    Variable removeVariable(long id, long variableId) throws EntityNotFoundException;

    boolean hasUserVisibilitySensor(String name, long id) throws EntityNotFoundException;

    void reset(long id) throws EntityNotFoundException, JsonProcessingException;

    void sendAction(long id, String action)
            throws EntityNotFoundException, JsonProcessingException, ActionException;

    List<Sensor> getSensors(long sensorTypeId);
}
