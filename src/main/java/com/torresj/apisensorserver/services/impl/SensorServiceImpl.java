package com.torresj.apisensorserver.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.torresj.apisensorserver.models.Record;
import com.torresj.apisensorserver.models.Sensor;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.jpa.RecordRepository;
import com.torresj.apisensorserver.jpa.SensorRepository;
import com.torresj.apisensorserver.jpa.VariableRepository;
import com.torresj.apisensorserver.rabbitmq.Producer;
import com.torresj.apisensorserver.services.SensorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class SensorServiceImpl implements SensorService {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(SensorServiceImpl.class);

    private static final String RESET = "reset";

    @Value("${socket.port}")
    private int socketPort;

    private SensorRepository sensorRepository;

    private VariableRepository variableRepository;

    private RecordRepository recordRepository;

    private Producer producer;

    public SensorServiceImpl(SensorRepository sensorRepository,
            VariableRepository variableRepository, RecordRepository recordRepository,
            Producer producer) {
        this.sensorRepository = sensorRepository;
        this.variableRepository = variableRepository;
        this.recordRepository = recordRepository;
        this.producer = producer;
    }

    @Override
    public Sensor update(Sensor sensor) throws EntityNotFoundException {

        logger.debug("[SENSOR - REGISTER] Searching sensor on DB");

        Sensor entity = sensorRepository.findByMac(sensor.getMac()).orElseThrow(EntityNotFoundException::new);

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

    @Override
    public Sensor register(Sensor sensor) {

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

    @Override
    public Page<Sensor> getSensors(int pageNumber, int numberOfElements) {
        logger.debug("[SENSOR - GET] Getting sensors");
        PageRequest pageRequest = PageRequest.of(pageNumber, numberOfElements, Sort.by("createAt").descending());

        return sensorRepository.findAll(pageRequest);
    }

    @Override
    public Page<Record> getRecords(long sensorId, long variableId, int pageNumber, int numberOfElements, LocalDate from,
            LocalDate to) throws EntityNotFoundException {
        logger.debug("[SENSOR - VARIABLE - RECORDS] Getting records beetween: " + from + " and " + to);

        // Try to find variable and sensor
        sensorRepository.findById(sensorId).orElseThrow(EntityNotFoundException::new);
        variableRepository.findById(variableId).orElseThrow(EntityNotFoundException::new);

        PageRequest pageRequest = PageRequest.of(pageNumber, numberOfElements, Sort.by("createAt").descending());

        return recordRepository.findBySensorIdAndVariableIdAndCreateAtBetween(sensorId, variableId,
                from.atStartOfDay(), to.atStartOfDay(), pageRequest);
    }

    @Override
    public Sensor getSensor(long id) throws EntityNotFoundException {
        logger.debug("[SENSOR - GET SENSOR] Searching sensor by id: " + id);

        return sensorRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Sensor removeSensor(long id) throws EntityNotFoundException {
        logger.debug("[SENSOR - REMOVE SENSOR] Searching sensor by id: " + id);
        Sensor sensor = sensorRepository.findById(id).orElseThrow(EntityNotFoundException::new);
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

    @Override
    public void reset(long id) throws EntityNotFoundException {
        logger.debug("[SENSOR - RESET] Searching sensor by id: " + id);
        Sensor sensor = sensorRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                InetAddress ip = InetAddress.getByName(sensor.getIp());
                Socket socket = new Socket(ip, socketPort);
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                writer.println(RESET);
                socket.close();
            } catch (IOException e) {
                logger.error("[SENSOR - RESET] error reseting sensor id: " + id, e);
            }
        });
    }

    @Override
    public void sendAction(long id, String action) throws EntityNotFoundException {
        logger.debug("[SENSOR - SEND ACTION] Searching sensor by id: " + id);
        Sensor sensor = sensorRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                InetAddress ip = InetAddress.getByName(sensor.getIp());
                Socket socket = new Socket(ip, socketPort);
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                writer.println(action);
                socket.close();
            } catch (IOException e) {
                logger.error("[SENSOR - ACTION] error sending action " + action + " to sensor id: " + id, e);
            }
        });
    }
}