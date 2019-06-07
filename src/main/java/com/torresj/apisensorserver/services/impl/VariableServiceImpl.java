package com.torresj.apisensorserver.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.jpa.VariableRepository;
import com.torresj.apisensorserver.models.Variable;
import com.torresj.apisensorserver.rabbitmq.Producer;
import com.torresj.apisensorserver.services.VariableService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class VariableServiceImpl implements VariableService {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(VariableServiceImpl.class);

    private VariableRepository variableRepository;

    private Producer producer;

    public VariableServiceImpl(VariableRepository variableRepository, Producer producer) {
        this.variableRepository = variableRepository;
        this.producer = producer;
    }

    @Override
    public Variable update(Variable variable) throws EntityNotFoundException {
        logger.debug("[VARIABLE - UPDATE] Updating variable: " + variable);
        Variable entity = variableRepository.findByName(variable.getName())
                .orElseThrow(EntityNotFoundException::new);

        logger.debug("[VARIABLE - UPDATE] Sensor exists. Updating ...");
        variable.setId(entity.getId());

        logger.debug("[VARIABLE - UPDATE] Sending data to frontend via AMPQ message");

        ObjectNode ampqMsg = new ObjectMapper().createObjectNode();
        ampqMsg.put("type", "update");
        ampqMsg.put("model", "Variable");
        ampqMsg.set("data",
                new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(variable, JsonNode.class));

        producer.produceMsg(ampqMsg.toString());

        return entity;
    }

    @Override
    public Variable register(Variable variable) throws EntityAlreadyExists {
        logger.debug("[VARIABLE - REGISTER] Registering variable: " + variable);
        Optional<Variable> entity = variableRepository.findByName(variable.getName());
        if (entity.isPresent())
            throw new EntityAlreadyExists();
        else {
            Variable variableSaved = variableRepository.save(variable);

            logger.debug("[VARIABLE - REGISTER] Sending data to frontend via AMPQ message");

            ObjectNode ampqMsg = new ObjectMapper().createObjectNode();
            ampqMsg.put("type", "Create");
            ampqMsg.put("model", "Variable");
            ampqMsg.set("data", new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(variableSaved,
                    JsonNode.class));

            producer.produceMsg(ampqMsg.toString());
            return variableSaved;
        }
    }

    @Override
    public Variable getVariable(Long id) throws EntityNotFoundException {
        logger.debug("[VARIABLE - GET VARIABLE] Searching variable by id: " + id);

        return variableRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Page<Variable> getVariables(int pageNumber, int numberOfElements) {
        logger.debug("[VARIABLE - GET] Getting variables beetween");
        PageRequest pageRequest = PageRequest.of(pageNumber, numberOfElements, Sort.by("createAt").descending());

        return variableRepository.findAll(pageRequest);
    }

    @Override
    public Variable deleteVariable(Long id) throws EntityNotFoundException {
        logger.debug("[VARIABLE - DELETE] Searching variable by id: " + id);

        Variable variable = variableRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        variableRepository.delete(variable);

        logger.debug("[VARIABLE - REGISTER] Sending data to frontend via AMPQ message");

        ObjectNode ampqMsg = new ObjectMapper().createObjectNode();
        ampqMsg.put("type", "Delete");
        ampqMsg.put("model", "Variable");
        ampqMsg.set("data",
                new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(variable, JsonNode.class));

        producer.produceMsg(ampqMsg.toString());

        return variable;
    }
}