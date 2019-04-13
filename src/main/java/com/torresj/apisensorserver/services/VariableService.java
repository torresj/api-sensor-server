package com.torresj.apisensorserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.jpa.VariableRepository;
import com.torresj.apisensorserver.models.Variable;
import com.torresj.apisensorserver.rabbitmq.Producer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class VariableService {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(VariableService.class);

    @Autowired
    private VariableRepository variableRepository;

    @Autowired
    private Producer producer;

    public Variable update(Variable variable) throws EntityNotFoundException {
        logger.debug("[VARIABLE - UPDATE] Updating variable: " + variable);
        Variable entity = variableRepository.findByName(variable.getName())
                .orElseThrow(() -> new EntityNotFoundException());

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

    public Variable getVariable(Long id) throws EntityNotFoundException {
        logger.debug("[VARIABLE - GET VARIABLE] Searching variable by id: " + id);

        return variableRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
    }

    public Page<Variable> getVariables(int pageNumber, int numberOfElements) {
        logger.debug("[VARIABLE - GET] Getting variables beetween");
        PageRequest pageRequest = PageRequest.of(pageNumber, numberOfElements, Sort.by("createAt").descending());
        Page<Variable> page = variableRepository.findAll(pageRequest);

        return page;
    }

    public Variable deleteVariable(Long id) throws EntityNotFoundException {
        logger.debug("[VARIABLE - DELETE] Searching variable by id: " + id);

        Variable variable = variableRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
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