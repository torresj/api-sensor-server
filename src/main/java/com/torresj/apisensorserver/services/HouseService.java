package com.torresj.apisensorserver.services;

import java.util.List;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.House;
import com.torresj.apisensorserver.models.Sensor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class HouseService {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(HouseService.class);

    public Page<House> getHouses(int nPage, int elements) {
        return null;
    }

    public House getHouse(long id) throws EntityNotFoundException {
        return null;
    }

    public List<Sensor> getSensors(long id, int nPage, int elements) throws EntityNotFoundException {
        return null;
    }

    public House update(House house) throws EntityNotFoundException {
        return null;
    }

    public House register(House house) throws EntityAlreadyExists {
        return null;
    }

    public House removeHouse(long id) throws EntityNotFoundException {
        return null;
    }

}