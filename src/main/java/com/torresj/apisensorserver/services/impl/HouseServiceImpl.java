package com.torresj.apisensorserver.services.impl;

import java.util.List;
import java.util.Optional;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.House;
import com.torresj.apisensorserver.models.Sensor;
import com.torresj.apisensorserver.jpa.HouseRepository;
import com.torresj.apisensorserver.services.HouseService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class HouseServiceImpl implements HouseService {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(HouseServiceImpl.class);

    private HouseRepository houseRepository;

    public HouseServiceImpl(HouseRepository houseRepository) {
        this.houseRepository = houseRepository;
    }

    @Override
    public Page<House> getHouses(int nPage, int elements) {
        logger.debug("[HOUSE - GET] Getting houses");
        PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());

        return houseRepository.findAll(pageRequest);
    }

    @Override
    public House getHouse(long id) throws EntityNotFoundException {
        logger.debug("[HOUSE - GET HOUSE] Searching house by id: " + id);

        return houseRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public List<Sensor> getSensors(long id, int nPage, int elements) throws EntityNotFoundException {
        logger.debug("[HOUSE - SENSOR] Searching sensors  by house id: " + id + ", nPage " + nPage + " and elements "
                + elements);

        House house = houseRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        return house.getSensors();
    }

    @Override
    public House update(House house) throws EntityNotFoundException {
        logger.debug("[HOUSE - UPDATE] Updating House " + house);
        House entity = houseRepository.findByName(house.getName()).orElseThrow(EntityNotFoundException::new);
        house.setId(entity.getId());
        houseRepository.save(house);
        return house;
    }

    @Override
    public House register(House house) throws EntityAlreadyExists {
        logger.debug("[HOUSE - REGISTER] Registering House " + house);
        Optional<House> entity = houseRepository.findByName(house.getName());

        if (entity.isPresent()) {
            logger.error("[HOUSE - REGISTER] Error registering House " + house);
            throw new EntityAlreadyExists();
        }

        return houseRepository.save(house);
    }

    @Override
    public House removeHouse(long id) throws EntityNotFoundException {
        logger.debug("[HOUSE - REMOVE HOUSE] Searching house by id: " + id);
        House house = houseRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        houseRepository.delete(house);
        return house;
    }

}