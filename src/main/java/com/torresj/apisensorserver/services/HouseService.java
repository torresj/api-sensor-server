package com.torresj.apisensorserver.services;

import java.util.List;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.House;
import com.torresj.apisensorserver.models.Sensor;

import org.springframework.data.domain.Page;

public interface HouseService {
    Page<House> getHouses(int nPage, int elements);

    House getHouse(long id) throws EntityNotFoundException;

    List<Sensor> getSensors(long id, int nPage, int elements) throws EntityNotFoundException;

    House update(House house) throws EntityNotFoundException;

    House register(House house) throws EntityAlreadyExists;

    House removeHouse(long id) throws EntityNotFoundException;
}
