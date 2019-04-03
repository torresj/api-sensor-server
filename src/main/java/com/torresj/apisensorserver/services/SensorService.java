package com.torresj.apisensorserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.torresj.apisensorserver.models.Sensor;
import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.jpa.SensorRepository;
import com.torresj.apisensorserver.jpa.VariableRepository;
import com.torresj.apisensorserver.rabbitmq.Producer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;

@Service
public class SensorService {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(SensorService.class);

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private VariableRepository variableRepository;

    @Autowired
    private Producer producer;

    public Sensor update(Sensor sensor) throws EntityNotFoundException {

        logger.info("[SENSOR - REGISTER] Searching sensor on DB");
        Sensor entity = sensorRepository.findByMac(sensor.getMac()).orElseThrow(() -> new EntityNotFoundException());

        logger.info("[SENSOR - REGISTER] Sensor exists. Updating ...");
        sensor.setLastConnection(LocalDateTime.now());
        sensor.setId(entity.getId());
        sensor.setVariables(sensor.getVariables().stream()
                .map(v -> v = variableRepository.findByName(v.getName()).orElse(v)).collect(Collectors.toList()));
        sensor = sensorRepository.save(sensor);

        logger.info("[SENSOR - REGISTER] Sending data to frontend via AMPQ message");

        ObjectNode ampqMsg = new ObjectMapper().createObjectNode();
        ampqMsg.put("type", "Update");
        ampqMsg.put("model", "Sensor");
        ampqMsg.set("data",
                new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(sensor, JsonNode.class));

        producer.produceMsg(ampqMsg.toString());

        return sensor;
    }

    public Sensor register(Sensor sensor) throws EntityAlreadyExists {

        logger.info("[SENSOR - REGISTER] Searching sensor on DB");
        Optional<Sensor> entity = sensorRepository.findByMac(sensor.getMac());

        if (entity.isPresent()) {
            logger.info("[SENSOR - REGISTER] Sensor exists");
            throw new EntityAlreadyExists();
        } else {
            logger.info("[SENSOR - REGISTER] Registering new sensor ...");
            sensor.setLastConnection(LocalDateTime.now());
            sensor.setVariables(sensor.getVariables().stream()
                    .map(v -> v = variableRepository.findByName(v.getName()).orElse(v)).collect(Collectors.toList()));
            logger.info(sensor.getVariables());
            sensor = sensorRepository.save(sensor);

            logger.info("[SENSOR - REGISTER] Sending data to frontend via AMPQ message");

            ObjectNode ampqMsg = new ObjectMapper().createObjectNode();
            ampqMsg.put("type", "Create");
            ampqMsg.put("model", "Sensor");
            ampqMsg.set("data",
                    new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(sensor, JsonNode.class));

            producer.produceMsg(ampqMsg.toString());

            return sensor;
        }
    }

    public List<Sensor> getSensors() {
        ModelMapper mapper = new ModelMapper();
        List<Sensor> sensors = sensorRepository.findAll().stream().map(v -> mapper.map(v, Sensor.class))
                .collect(Collectors.toList());
        return sensors;
    }

    public Sensor getSensor(long id) throws EntityNotFoundException {
        logger.info("[SENSOR - GET SENSOR] Searching sensor by id: " + id);

        Sensor entity = sensorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
        return entity;
    }

    public Sensor removeSensor(long id) throws EntityNotFoundException {
        logger.info("[SENSOR - REMOVE SENSOR] Searching sensor by id: " + id);
        Sensor sensor = sensorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
        sensorRepository.delete(sensor);

        logger.info("[SENSOR - REGISTER] Sending data to frontend via AMPQ message");

        ObjectNode ampqMsg = new ObjectMapper().createObjectNode();
        ampqMsg.put("type", "Delete");
        ampqMsg.put("model", "Sensor");
        ampqMsg.set("data",
                new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(sensor, JsonNode.class));

        producer.produceMsg(ampqMsg.toString());

        return sensor;
    }
}