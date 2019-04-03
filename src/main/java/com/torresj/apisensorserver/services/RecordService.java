package com.torresj.apisensorserver.services;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.jpa.RecordRepository;
import com.torresj.apisensorserver.jpa.SensorRepository;
import com.torresj.apisensorserver.jpa.VariableRepository;
import com.torresj.apisensorserver.models.Record;
import com.torresj.apisensorserver.rabbitmq.Producer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class RecordService {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(RecordService.class);

    @Autowired
    private RecordRepository recordRespository;

    @Autowired
    private VariableRepository variableRepository;

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private Producer producer;

    public Record register(Record record) throws EntityNotFoundException {
        logger.info("[RECORD - REGISTER] Saving new record: " + record);
        // Try to find variable and sensor
        sensorRepository.findById(record.getSensorId()).orElseThrow(() -> new EntityNotFoundException());
        variableRepository.findById(record.getVariableId()).orElseThrow(() -> new EntityNotFoundException());

        Record entity = recordRespository.save(record);

        logger.info("[RECORD - REGISTER] Sending data to frontend via AMPQ message");

        ObjectNode ampqMsg = new ObjectMapper().createObjectNode();
        ampqMsg.put("type", "Create");
        ampqMsg.put("model", "Record");
        ampqMsg.set("data",
                new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(entity, JsonNode.class));

        producer.produceMsg(ampqMsg.toString());

        return entity;
    }

    public Page<Record> getRecords(int pageNumber, int numberOfElements, LocalDate from, LocalDate to) {

        logger.info("[RECORD - GET] Getting records beetween: " + from + " and " + to);

        PageRequest pageRequest = PageRequest.of(pageNumber, numberOfElements, Sort.by("dateTime").descending());
        Page<Record> page = recordRespository.findByDateTimeBetween(from.atStartOfDay(), to.atStartOfDay(),
                pageRequest);

        return page;

    }

    public Record getRecord(long id) throws EntityNotFoundException {

        logger.info("[RECORD - GET] Getting record with id: " + id);
        return recordRespository.findById(id).orElseThrow(() -> new EntityNotFoundException());

    }

}