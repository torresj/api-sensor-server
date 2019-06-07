package com.torresj.apisensorserver.services;

import java.time.LocalDate;

import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.Record;

import org.springframework.data.domain.Page;

public interface RecordService {
    Record register(Record record) throws EntityNotFoundException;

    Page<Record> getRecords(int pageNumber, int numberOfElements, LocalDate from, LocalDate to);

    Record getRecord(long id) throws EntityNotFoundException;
}
