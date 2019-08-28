package com.torresj.apisensorserver.services;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExistsException;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.entities.Sensor;
import com.torresj.apisensorserver.models.entities.Variable;
import org.springframework.data.domain.Page;

public interface VariableService {

  Page<Variable> getVariables(int nPage, int elements);

  Variable getVariable(long id) throws EntityNotFoundException;

  Variable update(Variable variable) throws EntityNotFoundException;

  Variable register(Variable variable) throws EntityAlreadyExistsException;

  Variable deleteVariable(long id) throws EntityNotFoundException;

  Page<Sensor> getSensors(long id, int nPage, int elements);

  boolean hasUserVisibilityVariable(String name, long id) throws EntityNotFoundException;

  Page<Variable> getVariables(int nPage, int elements, String name);
}
