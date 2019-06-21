package com.torresj.apisensorserver.services;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.Sensor;
import com.torresj.apisensorserver.models.Variable;
import org.springframework.data.domain.Page;

public interface VariableService {

  Page<Variable> getVariables(int nPage, int elements);

  Variable getVariable(long id) throws EntityNotFoundException;

  Variable update(Variable variable) throws EntityNotFoundException;

  Variable register(Variable variable) throws EntityAlreadyExists;

  Variable deleteVariable(long id) throws EntityNotFoundException;

  Page<Sensor> getSensors(long id, int nPage, int elements);
}
