package com.torresj.apisensorserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.torresj.apisensorserver.entities.VariableEntity;
import com.torresj.apisensorserver.exceptions.ApiException;
import com.torresj.apisensorserver.jpa.VariableRepository;
import com.torresj.apisensorserver.models.Variable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;

@Service
public class VariableService {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(VariableService.class);

    @Autowired
    private VariableRepository variableRepository;

    public void registerVariable(Variable variable) {
        VariableEntity newEntity = new ModelMapper().map(variable, VariableEntity.class);
        logger.info("[VARIABLE - REGISTER] Registering variable: " + variable);
        Optional<VariableEntity> entity = variableRepository.findByName(variable.getName());
        if (entity.isPresent()) {
            newEntity.setId(entity.get().getId());
            variableRepository.save(newEntity);
        } else {
            newEntity.setCreateAt(LocalDateTime.now());
            variableRepository.save(newEntity);
        }
    }

    public Variable getVariable(Long id) throws ApiException {
        logger.info("[VARIABLE - GET VARIABLE] Searching variable by id: " + id);

        VariableEntity entity = variableRepository.findById(id)
                .orElseThrow(() -> new ApiException("Variable not found", 201));
        return new ModelMapper().map(entity, Variable.class);
    }
}