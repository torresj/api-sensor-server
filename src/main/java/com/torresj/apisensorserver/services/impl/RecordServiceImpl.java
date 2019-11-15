package com.torresj.apisensorserver.services.impl;

import java.time.LocalDate;

import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.entities.Record;
import com.torresj.apisensorserver.models.entities.User;
import com.torresj.apisensorserver.repositories.HouseRepository;
import com.torresj.apisensorserver.repositories.RecordRepository;
import com.torresj.apisensorserver.repositories.SensorRepository;
import com.torresj.apisensorserver.repositories.UserHouseRelationRepository;
import com.torresj.apisensorserver.repositories.UserRepository;
import com.torresj.apisensorserver.repositories.VariableRepository;
import com.torresj.apisensorserver.services.RecordService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RecordServiceImpl implements RecordService {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(RecordServiceImpl.class);

    private RecordRepository recordRespository;

    private VariableRepository variableRepository;

    private SensorRepository sensorRepository;

    private UserRepository userRepository;

    private UserHouseRelationRepository userHouseRelationRepository;

    private HouseRepository houseRepository;

    private SimpMessagingTemplate template;

    public RecordServiceImpl(RecordRepository recordRespository,
            VariableRepository variableRepository, SensorRepository sensorRepository,
            UserRepository userRepository,
            UserHouseRelationRepository userHouseRelationRepository,
            HouseRepository houseRepository,
            SimpMessagingTemplate template) {
        this.recordRespository = recordRespository;
        this.variableRepository = variableRepository;
        this.sensorRepository = sensorRepository;
        this.userRepository = userRepository;
        this.userHouseRelationRepository = userHouseRelationRepository;
        this.houseRepository = houseRepository;
        this.template = template;
    }

    @Override
    public Record register(Record record) throws EntityNotFoundException {
        logger.debug("[RECORD - SERVICE] Service for save new record start. {} ", record);
        // Try to find variable and sensor
        logger.debug("[RECORD - SERVICE] Searching sensor {} ", record.getSensorId());
        sensorRepository.findById(record.getSensorId()).orElseThrow(EntityNotFoundException::new);
        logger.debug("[RECORD - SERVICE] Searching variable {} ", record.getVariableId());
        variableRepository.findById(record.getVariableId()).orElseThrow(EntityNotFoundException::new);

        Record entity = recordRespository.save(record);

        String destination = "/topic/station/" + record.getSensorId();
        logger.debug(
                "[RECORD - SERVICE] Sending data to destination /topic/station/" + record.getSensorId());
        template.convertAndSend(destination, record);

        logger.debug("[RECORD - SERVICE] Service for save new record end. {} ", record);
        return entity;
    }

    @Override
    public Page<Record> getRecords(long sensorId, long variableId, int pageNumber,
            int numberOfElements, LocalDate from, LocalDate to) {

        logger.debug(
                "[RECORD - SERVICE] Service for get records from sensor {} and variable {} start", sensorId,
                variableId);

        PageRequest pageRequest = PageRequest
                .of(pageNumber, numberOfElements, Sort.by("createAt").descending());

        Page<Record> page = recordRespository
                .findBySensorIdAndVariableIdAndCreateAtBetween(sensorId, variableId, from.atStartOfDay(),
                        to.atTime(23, 59),
                        pageRequest);

        logger.debug(
                "[RECORD - SERVICE] Service for get records from sensor {} and variable {} end. Records: ",
                sensorId,
                variableId, page.getContent());
        return page;

    }

    @Override
    public Record getRecord(long id) throws EntityNotFoundException {

        logger.debug("[RECORD - SERVICE]  Service for get record {} start", id);
        Record record = recordRespository.findById(id).orElseThrow(EntityNotFoundException::new);
        logger.debug("[RECORD - SERVICE]  Service for get record {} end. Record: ", id, record);
        return record;

    }

    @Override
    public boolean hasUserVisibilityRecord(String name, long id) throws EntityNotFoundException {
        logger
                .debug("[RECORD - SERVICE] Service for check if user {} has visibility for record {} start",
                        name, id);
        logger.debug("[RECORD - SERVICE] Searching user {}", name);
        User user = userRepository.findByUsername(name).get();
        logger.debug("[RECORD - SERVICE] Searching record {}", id);
        Record record = recordRespository.findById(id).orElseThrow(EntityNotFoundException::new);
        boolean hasVisibility = userHouseRelationRepository.findByUserId(user.getId()).stream()
                .map(userHouseRelation -> houseRepository.findById(userHouseRelation.getHouseId()).get())
                .flatMap(house -> sensorRepository.findByHouseId(house.getId()).stream())
                .anyMatch(sensor -> sensor.getId() == record.getSensorId());
        logger
                .debug(
                        "[RECORD - SERVICE] Service for check if user {} has visibility for record {} end. Result: {}",
                        name, id, hasVisibility);
        return hasVisibility;
    }

}