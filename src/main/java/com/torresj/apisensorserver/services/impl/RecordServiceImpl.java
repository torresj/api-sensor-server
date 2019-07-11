package com.torresj.apisensorserver.services.impl;

import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.jpa.RecordRepository;
import com.torresj.apisensorserver.jpa.SensorRepository;
import com.torresj.apisensorserver.jpa.VariableRepository;
import com.torresj.apisensorserver.models.Record;
import com.torresj.apisensorserver.services.RecordService;
import java.time.LocalDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class RecordServiceImpl implements RecordService {

  /* Logs */
  private static final Logger logger = LogManager.getLogger(RecordServiceImpl.class);


  private RecordRepository recordRespository;

  private VariableRepository variableRepository;

  private SensorRepository sensorRepository;

  public RecordServiceImpl(RecordRepository recordRespository,
      VariableRepository variableRepository, SensorRepository sensorRepository) {
    this.recordRespository = recordRespository;
    this.variableRepository = variableRepository;
    this.sensorRepository = sensorRepository;
  }

  @Override
  public Record register(Record record) throws EntityNotFoundException {
    logger.debug("[RECORD - REGISTER] Saving new record: " + record);
    // Try to find variable and sensor
    sensorRepository.findById(record.getSensorId()).orElseThrow(EntityNotFoundException::new);
    variableRepository.findById(record.getVariableId()).orElseThrow(EntityNotFoundException::new);

    Record entity = recordRespository.save(record);

    return entity;
  }

  @Override
  public Page<Record> getRecords(long sensorId, long variableId, int pageNumber,
      int numberOfElements, LocalDate from, LocalDate to) {

    logger.debug(
        "[RECORD - GET] Getting records for sensor " + sensorId + " and variable " + variableId
            + " beetween: " + from + " and " + to);

    PageRequest pageRequest = PageRequest
        .of(pageNumber, numberOfElements, Sort.by("createAt").descending());

    return recordRespository
        .findBySensorIdAndVariableIdAndCreateAtBetween(sensorId, variableId, from.atStartOfDay(),
            to.atStartOfDay(),
            pageRequest);

  }

  @Override
  public Record getRecord(long id) throws EntityNotFoundException {

    logger.debug("[RECORD - GET] Getting record with id: " + id);
    return recordRespository.findById(id).orElseThrow(EntityNotFoundException::new);

  }

}