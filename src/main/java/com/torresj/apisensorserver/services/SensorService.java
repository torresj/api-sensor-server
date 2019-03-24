package com.torresj.apisensorserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.torresj.apisensorserver.entities.SensorEntity;
import com.torresj.apisensorserver.jpa.SensorRepository;
import com.torresj.apisensorserver.models.Sensor;
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
    private Producer producer;

    public void register(Sensor sensor) {

        ModelMapper mapper = new ModelMapper();
        SensorEntity newEntity = mapper.map(sensor, SensorEntity.class);

        logger.info("[SENSOR - REGISTER] Searching sensor on DB");
        Optional<SensorEntity> entity = sensorRepository.findByMac(sensor.getMac());

        if (entity.isPresent()) {
            logger.info("[SENSOR - REGISTER] Sensor exists. Updating ...");
            newEntity.setLastConnection(LocalDateTime.now());
            newEntity.setId(entity.get().getId());
            newEntity.setCreateAt(entity.get().getCreateAt());
            sensorRepository.save(newEntity);
        } else {
            logger.info("[SENSOR - REGISTER] Registering new sensor ...");
            newEntity.setCreateAt(LocalDateTime.now());
            newEntity.setLastConnection(LocalDateTime.now());
            sensorRepository.save(newEntity);
        }

        logger.info("[SENSOR - REGISTER] Sensor registered on ServiceSensor");

        logger.info("[SENSOR - REGISTER] Sending data to frontend via AMPQ message");

        ObjectNode ampqMsg = new ObjectMapper().createObjectNode();
        ampqMsg.put("type", "Sensor");
        ampqMsg.put("data", new ObjectMapper().convertValue(newEntity, JsonNode.class));

        producer.produceMsg(ampqMsg.toString());
    }
}