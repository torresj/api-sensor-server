package com.torresj.apisensorserver.services.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torresj.apisensorserver.exceptions.ActionException;
import com.torresj.apisensorserver.exceptions.EntityAlreadyExistsException;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.SocketMessage;
import com.torresj.apisensorserver.models.entities.Sensor;
import com.torresj.apisensorserver.models.entities.SensorType;
import com.torresj.apisensorserver.models.entities.User;
import com.torresj.apisensorserver.models.entities.Variable;
import com.torresj.apisensorserver.models.entities.VariableSensorRelation;
import com.torresj.apisensorserver.repositories.HouseRepository;
import com.torresj.apisensorserver.repositories.SensorRepository;
import com.torresj.apisensorserver.repositories.SensorTypeRepository;
import com.torresj.apisensorserver.repositories.UserHouseRelationRepository;
import com.torresj.apisensorserver.repositories.UserRepository;
import com.torresj.apisensorserver.repositories.VariableRepository;
import com.torresj.apisensorserver.repositories.VariableSensorRelationRepository;
import com.torresj.apisensorserver.services.SensorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SensorServiceImpl implements SensorService {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(SensorServiceImpl.class);

    private static final String RESET = "reset";

    @Value("${socket.port}")
    private int socketPort;

    private SensorRepository sensorRepository;

    private VariableRepository variableRepository;

    private VariableSensorRelationRepository variableSensorRelationRepository;

    private SensorTypeRepository sensorTypeRepository;

    private HouseRepository houseRepository;

    private UserRepository userRepository;

    private UserHouseRelationRepository userHouseRelationRepository;

    public SensorServiceImpl(SensorRepository sensorRepository,
            VariableRepository variableRepository,
            VariableSensorRelationRepository variableSensorRelationRepository,
            SensorTypeRepository sensorTypeRepository,
            HouseRepository houseRepository,
            UserRepository userRepository,
            UserHouseRelationRepository userHouseRelationRepository) {
        this.sensorRepository = sensorRepository;
        this.variableRepository = variableRepository;
        this.variableSensorRelationRepository = variableSensorRelationRepository;
        this.sensorTypeRepository = sensorTypeRepository;
        this.houseRepository = houseRepository;
        this.userRepository = userRepository;
        this.userHouseRelationRepository = userHouseRelationRepository;
    }

    @Override
    public Page<Sensor> getSensors(int nPage, int elements) {
        logger.debug("[SENSOR - SERVICE] Service for getting sensors start");
        PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());
        Page<Sensor> page = sensorRepository.findAll(pageRequest);
        logger.debug("[SENSOR - SERVICE] Service for getting sensors end. Sensors: {}",
                page.getContent());
        return page;
    }

    @Override
    public List<Sensor> getSensors() {
        logger.debug("[SENSOR - SERVICE] Service for getting sensors start");
        List<Sensor> sensors = sensorRepository.findAll();
        logger.debug("[SENSOR - SERVICE] Service for getting sensors end. Sensors: {}",
                sensors);
        return sensors;
    }

    @Override
    public List<Sensor> getSensors(long sensorTypeId) {
        logger.debug("[SENSOR - SERVICE] Service for getting sensors by sensor type id {} start", sensorTypeId);
        List<Sensor> sensors = sensorRepository.findBySensorTypeId(sensorTypeId);
        logger.debug("[SENSOR - SERVICE] Service for getting sensors end. Sensors: {}",
                sensors);
        return sensors;
    }

    @Override
    public Page<Sensor> getSensors(int nPage, int elements, Long sensorTypeId, String name)
            throws EntityNotFoundException {
        logger.debug(
                "[SENSOR - SERVICE] Service for getting sensors filtered by type {} and/or name {} start",
                sensorTypeId, name);
        Page<Sensor> page;
        PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());
        if (sensorTypeId != null && name != null) {
            logger.debug(
                    "[SENSOR - SERVICE] filtering by type {} and name {}",
                    sensorTypeId, name);
            sensorTypeRepository.findById(sensorTypeId).orElseThrow(EntityNotFoundException::new);
            page = sensorRepository.findBySensorTypeIdAndName(sensorTypeId, name, pageRequest);
        } else if (sensorTypeId != null) {
            logger.debug(
                    "[SENSOR - SERVICE] filtering by type {}",
                    sensorTypeId);
            page = sensorRepository.findBySensorTypeId(sensorTypeId, pageRequest);
        } else {
            logger.debug(
                    "[SENSOR - SERVICE] filtering by name {}", name);
            page = sensorRepository.findByName(name, pageRequest);
        }

        logger.debug(
                "[SENSOR - SERVICE] Service for getting sensors filtered by type {} and/or name {} end. Sensors: {}",
                sensorTypeId, name, page != null ? page.getContent() : page);
        return page;
    }

    @Override
    public Sensor getSensor(long id) throws EntityNotFoundException {
        logger.debug("[SENSOR - SERVICE] Service for getting sensor {} start", id);
        Sensor sensor = sensorRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        logger.debug("[SENSOR - SERVICE] Service for getting sensor {} end. Sensor: {}", id, sensor);
        return sensor;
    }

    @Override
    public Page<Variable> getVariables(long id, int nPage, int elements)
            throws EntityNotFoundException {
        logger.debug("[SENSOR - SERVICE] Service for getting sensor {} variables start", id);
        sensorRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());
        List<Long> ids = variableSensorRelationRepository.findBySensorId(id).stream()
                .map(VariableSensorRelation::getVariableId).collect(
                        Collectors.toList());
        Page<Variable> page = variableRepository.findByIdIn(ids, pageRequest);
        logger
                .debug("[SENSOR - SERVICE] Service for getting sensor {} variables end. Variables: {}", id,
                        page.getContent());
        return page;
    }

    @Override
    public Variable addVariable(long sensorId, long variableId) throws EntityNotFoundException {
        logger.debug(
                "[SENSOR - SERVICE] Service for add variable {} to sensor {} start", variableId, sensorId);
        sensorRepository.findById(sensorId).orElseThrow(EntityNotFoundException::new);
        Variable variable = variableRepository.findById(variableId)
                .orElseThrow(EntityNotFoundException::new);
        if (!variableSensorRelationRepository.findBySensorIdAndVariableId(sensorId, variableId)
                .isPresent()) {
            logger.debug(
                    "[SENSOR - SERVICE] Variable {} not present yet. Adding to sensor {}", variableId,
                    sensorId);
            VariableSensorRelation relation = new VariableSensorRelation();
            relation.setSensorId(sensorId);
            relation.setVariableId(variableId);
            variableSensorRelationRepository.save(relation);
        }
        logger.debug(
                "[SENSOR - SERVICE] Service for add variable {} to sensor {} end. Variable: {}", variableId,
                sensorId, variable);
        return variable;
    }

    @Override
    public Sensor update(Sensor sensor) throws EntityNotFoundException {
        logger.debug("[SENSOR - SERVICE] Service for updating sensor start. Sensor: {}", sensor);

        Sensor entity = sensorRepository.findByMac(sensor.getMac())
                .orElseThrow(EntityNotFoundException::new);

        sensor.setLastConnection(LocalDateTime.now());
        sensor.setId(entity.getId());
        //check for house id and sensor type id
        if (sensor.getHouseId() != null) {
            logger.debug("[SENSOR - SERVICE] Searching if house {} exists", sensor.getHouseId());
            houseRepository.findById(sensor.getHouseId()).orElseThrow(EntityNotFoundException::new);
        }
        logger.debug("[SENSOR - SERVICE] Searching if sensor type {} exists", sensor.getId());
        sensorTypeRepository.findById(sensor.getSensorTypeId())
                .orElseThrow(EntityNotFoundException::new);
        sensor = sensorRepository.save(sensor);

        logger.debug("[SENSOR - SERVICE] Service for updating sensor end. Sensor: {}", sensor);
        return sensor;
    }

    @Override
    public Sensor register(Sensor sensor)
            throws EntityNotFoundException, EntityAlreadyExistsException {
        logger.debug("[SENSOR - SERVICE] Service for register sensor start. Sensor: {}", sensor);
        Optional<Sensor> entity = sensorRepository.findByMac(sensor.getMac());

        if (entity.isPresent()) {
            logger.debug("[SENSOR - SERVICE] Service for register sensor end. Sensor {} exists",
                    sensor.getId());
            throw new EntityAlreadyExistsException(entity.get());
        } else {
            //check for house id and sensor type id
            if (sensor.getHouseId() != null) {
                logger.debug("[SENSOR - SERVICE] Searching if house {} exists", sensor.getHouseId());
                houseRepository.findById(sensor.getHouseId()).orElseThrow(EntityNotFoundException::new);
            }
            logger.debug("[SENSOR - SERVICE] Searching if sensor type {} exists", sensor.getId());
            sensorTypeRepository.findById(sensor.getSensorTypeId())
                    .orElseThrow(EntityNotFoundException::new);
            sensor.setLastConnection(LocalDateTime.now());
            sensor = sensorRepository.save(sensor);

            logger.debug("[SENSOR - SERVICE] Service for register sensor end. Sensor: {}", sensor);
            return sensor;
        }
    }

    @Override
    public Sensor removeSensor(long id) throws EntityNotFoundException {
        logger.debug("[SENSOR - SERVICE] Service for remove sensor {} start", id);
        Sensor sensor = sensorRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        sensorRepository.delete(sensor);

        logger.debug("[SENSOR - SERVICE] Removing relations variable - sensor");
        variableSensorRelationRepository.findBySensorId(id).stream()
                .forEach(variableSensorRelationRepository::delete);

        logger.debug("[SENSOR - SERVICE] Service for remove sensor {} end", id);
        return sensor;
    }

    @Override
    public Variable removeVariable(long sensorId, long variableId) throws EntityNotFoundException {
        logger.debug(
                "[SENSOR - SERVICE] Service for remove variable {} from sensor {} start", variableId,
                sensorId);
        logger.debug("[SENSOR - SERVICE] Searching if sensor {} exists", sensorId);
        sensorRepository.findById(sensorId).orElseThrow(EntityNotFoundException::new);

        logger.debug("[SENSOR - SERVICE] Searching if variable {} exists", variableId);
        Variable variable = variableRepository.findById(variableId)
                .orElseThrow(EntityNotFoundException::new);
        variableSensorRelationRepository
                .delete(variableSensorRelationRepository.findBySensorIdAndVariableId(sensorId, variableId)
                        .orElseThrow(EntityNotFoundException::new));
        logger.debug(
                "[SENSOR - SERVICE] Service for remove variable {} from sensor {} end", variableId,
                sensorId);
        return variable;
    }

    @Override
    public boolean hasUserVisibilitySensor(String name, long id) throws EntityNotFoundException {
        logger
                .debug("[SENSOR - SERVICE] Service for check if user {} has visibility for sensor {} start",
                        name, id);
        logger.debug("[SENSOR - SERVICE] Searching user {}", name);
        User user = userRepository.findByUsername(name).get();
        logger.debug("[SENSOR - SERVICE] Searching if sensor {} exists", id);
        sensorRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        boolean hasVisibility = userHouseRelationRepository.findByUserId(user.getId()).stream()
                .map(userHouseRelation -> houseRepository.findById(userHouseRelation.getHouseId()).get())
                .flatMap(house -> sensorRepository.findByHouseId(house.getId()).stream())
                .anyMatch(sensor -> sensor.getId() == id);

        logger.debug(
                "[SENSOR - SERVICE] Service for check if user {} has visibility for user {} end. Result: {}",
                name, id, hasVisibility);
        return hasVisibility;
    }

    @Override
    public void reset(long id) throws EntityNotFoundException, JsonProcessingException {
        logger.debug("[SENSOR - SERVICE] Service for send reset action to sensor {} start", id);
        Sensor sensor = sensorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
        sendAsyncMessage(sensor, RESET);
        logger.debug("[SENSOR - SERVICE] Service for send reset action to sensor {} end", id);
    }

    @Override
    public void sendAction(long id, String action)
            throws EntityNotFoundException, JsonProcessingException, ActionException {
        logger.debug("[SENSOR - SERVICE] Service for send {} action to sensor {} start", action, id);
        Sensor sensor = sensorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
        checkAction(action, sensor.getSensorTypeId());
        sendAsyncMessage(sensor, action);
        logger.debug("[SENSOR - SERVICE] Service for send {} action to sensor {} end", action, id);
    }

    private void checkAction(String action, Long typeId)
            throws EntityNotFoundException, ActionException {
        if (!action.equals(RESET)) {
            SensorType type = sensorTypeRepository.findById(typeId)
                    .orElseThrow(EntityNotFoundException::new);
            List<String> actions = Arrays.asList(type.getActions().split(","));
            if (!actions.contains(action)) {
                throw new ActionException();
            }
        }
    }

    private void sendAsyncMessage(Sensor sensor, String action)
            throws JsonProcessingException {
        String socketMessage = new ObjectMapper()
                .writeValueAsString(new SocketMessage(sensor.getPrivateIp(), action));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                logger
                        .debug("[SENSOR - SERVICE] Opening socket to {}:{}", sensor.getPublicIp(), socketPort);
                InetAddress ip = InetAddress.getByName(sensor.getPublicIp());
                Socket socket = new Socket(ip, socketPort);
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                logger.debug("[SENSOR - SERVICE] Sending message: {}", socketMessage);
                writer.println(socketMessage);
                socket.close();
            } catch (IOException e) {
                logger
                        .error("[SENSOR - ACTION] error sending action {} to sensor {}", action, sensor.getId(),
                                e);
            }
        });
    }
}
