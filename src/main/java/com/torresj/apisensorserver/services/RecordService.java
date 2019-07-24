package com.torresj.apisensorserver.services;

import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.Record;
import java.time.LocalDate;
import org.springframework.data.domain.Page;

public interface RecordService {

  Record register(Record record) throws EntityNotFoundException;

  Page<Record> getRecords(long sensorId, long variableId, int pageNumber, int numberOfElements,
      LocalDate from, LocalDate to);

  Record getRecord(long id) throws EntityNotFoundException;

  boolean hasUserVisibilityRecord(String name, long id) throws EntityNotFoundException;
}
