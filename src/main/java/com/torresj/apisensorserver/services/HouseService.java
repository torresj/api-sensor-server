package com.torresj.apisensorserver.services;

import java.util.List;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExistsException;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.entities.House;
import com.torresj.apisensorserver.models.entities.Sensor;
import com.torresj.apisensorserver.models.entities.User;

import org.springframework.data.domain.Page;

public interface HouseService {

    Page<House> getHouses(String filter, int nPage, int elements);

    List<House> getHouses();

    House getHouse(long id) throws EntityNotFoundException;

    List<Sensor> getSensors(long id) throws EntityNotFoundException;

    House update(House house) throws EntityNotFoundException;

    House register(House house) throws EntityAlreadyExistsException;

    House removeHouse(long id) throws EntityNotFoundException;

    boolean hasUserVisibilityHouse(String name, long id) throws EntityNotFoundException;

    List<User> getHouseUsers(long id) throws EntityNotFoundException;

    List<Sensor> updateSensors(long houseId, List<Long> sensorIds) throws EntityNotFoundException;
}
