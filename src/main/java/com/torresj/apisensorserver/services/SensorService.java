package com.torresj.apisensorserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.torresj.apisensorserver.models.Record;
import com.torresj.apisensorserver.models.Sensor;
import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.jpa.RecordRepository;
import com.torresj.apisensorserver.jpa.SensorRepository;
import com.torresj.apisensorserver.jpa.VariableRepository;
import com.torresj.apisensorserver.rabbitmq.Producer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class SensorService {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(SensorService.class);

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private VariableRepository variableRepository;

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private Producer producer;

    public Sensor update(Sensor sensor) throws EntityNotFoundException {

        logger.debug("[SENSOR - REGISTER] Searching sensor on DB");

        Sensor entity = sensorRepository.findByMac(sensor.getMac()).orElseThrow(() -> new EntityNotFoundException());

        logger.debug("[SENSOR - REGISTER] Sensor exists. Updating ...");
        sensor.setLastConnection(LocalDateTime.now());
        sensor.setId(entity.getId());
        sensor.setVariables(sensor.getVariables().stream()
                .map(v -> v = variableRepository.findByName(v.getName()).orElse(v)).collect(Collectors.toList()));
        sensor = sensorRepository.save(sensor);

        logger.debug("[SENSOR - REGISTER] Sending data to frontend via AMPQ message");

        ObjectNode ampqMsg = new ObjectMapper().createObjectNode();
        ampqMsg.put("type", "Update");
        ampqMsg.put("model", "Sensor");
        ampqMsg.set("data",
                new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(sensor, JsonNode.class));

        producer.produceMsg(ampqMsg.toString());

        return sensor;
    }

    public Sensor register(Sensor sensor) throws EntityAlreadyExists {

        logger.debug("[SENSOR - REGISTER] Searching sensor on DB");
        Optional<Sensor> entity = sensorRepository.findByMac(sensor.getMac());

        if (entity.isPresent()) {
            logger.debug("[SENSOR - REGISTER] Sensor exists");
            return entity.get();
        } else {
            logger.info("[SENSOR - REGISTER] Registering new sensor ...");
            sensor.setLastConnection(LocalDateTime.now());
            sensor.setVariables(sensor.getVariables().stream()
                    .map(v -> v = variableRepository.findByName(v.getName()).orElse(v)).collect(Collectors.toList()));

            sensor = sensorRepository.save(sensor);

            logger.debug("[SENSOR - REGISTER] Sending data to frontend via AMPQ message");

            ObjectNode ampqMsg = new ObjectMapper().createObjectNode();
            ampqMsg.put("type", "Create");
            ampqMsg.put("model", "Sensor");
            ampqMsg.set("data",
                    new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(sensor, JsonNode.class));

            producer.produceMsg(ampqMsg.toString());

            return sensor;
        }
    }

    public Page<Sensor> getSensors(int pageNumber, int numberOfElements) {
        logger.debug("[SENSOR - GET] Getting sensors");
        PageRequest pageRequest = PageRequest.of(pageNumber, numberOfElements, Sort.by("createAt").descending());
        Page<Sensor> page = sensorRepository.findAll(pageRequest);

        return page;
    }

    public Page<Record> getRecords(long sensorId, long variableId, int pageNumber, int numberOfElements, LocalDate from,
            LocalDate to) throws EntityNotFoundException {
        logger.debug("[SENSOR - VARIABLE - RECORDS] Getting records beetween: " + from + " and " + to);

        // Try to find variable and sensor
        sensorRepository.findById(sensorId).orElseThrow(() -> new EntityNotFoundException());
        variableRepository.findById(variableId).orElseThrow(() -> new EntityNotFoundException());

        PageRequest pageRequest = PageRequest.of(pageNumber, numberOfElements, Sort.by("createAt").descending());
        Page<Record> page = recordRepository.findBySensorIdAndVariableIdAndCreateAtBetween(sensorId, variableId,
                from.atStartOfDay(), to.atStartOfDay(), pageRequest);

        return page;
    }

    public Sensor getSensor(long id) throws EntityNotFoundException {
        logger.debug("[SENSOR - GET SENSOR] Searching sensor by id: " + id);

        return sensorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
    }

    public Sensor removeSensor(long id) throws EntityNotFoundException {
        logger.debug("[SENSOR - REMOVE SENSOR] Searching sensor by id: " + id);
        Sensor sensor = sensorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
        sensorRepository.delete(sensor);

        logger.debug("[SENSOR - REGISTER] Sending data to frontend via AMPQ message");

        ObjectNode ampqMsg = new ObjectMapper().createObjectNode();
        ampqMsg.put("type", "Delete");
        ampqMsg.put("model", "Sensor");
        ampqMsg.set("data",
                new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(sensor, JsonNode.class));

        producer.produceMsg(ampqMsg.toString());

        return sensor;
    }
}