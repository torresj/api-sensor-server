package com.torresj.apisensorserver.services;

import java.time.LocalDate;

import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.Record;
import com.torresj.apisensorserver.models.Sensor;

import org.springframework.data.domain.Page;

public interface SensorService {
    Sensor update(Sensor sensor) throws EntityNotFoundException;

    Sensor register(Sensor sensor);

    Page<Sensor> getSensors(int pageNumber, int numberOfElements);

    Page<Record> getRecords(long sensorId, long variableId, int pageNumber, int numberOfElements, LocalDate from,
            LocalDate to) throws EntityNotFoundException;

    Sensor getSensor(long id) throws EntityNotFoundException;

    Sensor removeSensor(long id) throws EntityNotFoundException;

    void reset(long id) throws EntityNotFoundException;

    void sendAction(long id, String action) throws EntityNotFoundException;
}
