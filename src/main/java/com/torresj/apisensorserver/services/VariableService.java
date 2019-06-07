package com.torresj.apisensorserver.services;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.Variable;

import org.springframework.data.domain.Page;

public interface VariableService {
    Variable update(Variable variable) throws EntityNotFoundException;

    Variable register(Variable variable) throws EntityAlreadyExists;

    Variable getVariable(Long id) throws EntityNotFoundException;

    Page<Variable> getVariables(int pageNumber, int numberOfElements);

    Variable deleteVariable(Long id) throws EntityNotFoundException;
}
