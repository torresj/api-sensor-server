package com.torresj.apisensorserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
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

    public Variable registerOrUpdate(Variable variable) {
        logger.info("[VARIABLE - REGISTER] Registering variable: " + variable);

        return variableRepository.save(variable);
    }

    public Variable register(Variable variable) throws EntityAlreadyExists {
        logger.info("[VARIABLE - REGISTER] Registering variable: " + variable);
        Optional<Variable> entity = variableRepository.findByName(variable.getName());
        if (entity.isPresent()) {
            throw new EntityAlreadyExists();
        } else {
            variableRepository.save(variable);
        }

        return variable;
    }

    public Variable getVariable(Long id) throws EntityNotFoundException {
        logger.info("[VARIABLE - GET VARIABLE] Searching variable by id: " + id);

        Variable entity = variableRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
        return entity;
    }

    public List<Variable> getVariables() {
        ModelMapper mapper = new ModelMapper();
        List<Variable> variables = variableRepository.findAll().stream().map(v -> mapper.map(v, Variable.class))
                .collect(Collectors.toList());
        ;
        return variables;
    }
}