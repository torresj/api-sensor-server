package com.torresj.apisensorserver.services.impl;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.entities.House;
import com.torresj.apisensorserver.models.entities.Sensor;
import com.torresj.apisensorserver.models.entities.User;
import com.torresj.apisensorserver.repositories.HouseRepository;
import com.torresj.apisensorserver.repositories.SensorRepository;
import com.torresj.apisensorserver.repositories.UserHouseRelationRepository;
import com.torresj.apisensorserver.repositories.UserRepository;
import com.torresj.apisensorserver.services.HouseService;
import java.util.Optional;
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

  private SensorRepository sensorRepository;

  private UserRepository userRepository;

  private UserHouseRelationRepository userHouseRelationRepository;

  public HouseServiceImpl(HouseRepository houseRepository,
      SensorRepository sensorRepository,
      UserRepository userRepository,
      UserHouseRelationRepository userHouseRelationRepository) {
    this.houseRepository = houseRepository;
    this.sensorRepository = sensorRepository;
    this.userRepository = userRepository;
    this.userHouseRelationRepository = userHouseRelationRepository;
  }

  @Override
  public Page<House> getHouses(int nPage, int elements) {
    logger.debug("[HOUSE - SERVICE] Getting house service start");
    PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());
    Page<House> page = houseRepository.findAll(pageRequest);
    logger.debug("[HOUSE - SERVICE] Getting house service end. Houses: {}", page.getContent());
    return page;
  }

  @Override
  public House getHouse(long id) throws EntityNotFoundException {
    logger.debug("[HOUSE - SERVICE] Service for get house {} start", id);
    House house = houseRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    logger.debug("[HOUSE - SERVICE] Service for get house {} end. House: {}", id, house);
    return house;
  }

  @Override
  public Page<Sensor> getSensors(long id, int nPage, int elements) throws EntityNotFoundException {
    logger.debug("[HOUSE - SERVICE] Service for get house {} sensors start", id);
    houseRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());
    Page<Sensor> page = sensorRepository.findByHouseId(id, pageRequest);
    logger.debug("[HOUSE - SERVICE] Service for get house {} sensors end. Sensors: ", id,
        page.getContent());
    return page;
  }

  @Override
  public House update(House house) throws EntityNotFoundException {
    logger.debug("[HOUSE - SERVICE] Service for update house start. House: ", house);
    House entity = houseRepository.findByName(house.getName())
        .orElseThrow(EntityNotFoundException::new);
    house.setId(entity.getId());
    house = houseRepository.save(house);
    logger.debug("[HOUSE - SERVICE] Service for update house end. House: ", house);
    return house;
  }

  @Override
  public House register(House house) throws EntityAlreadyExists {
    logger.debug("[HOUSE - SERVICE] Service for register house start. House: {}", house);
    Optional<House> entity = houseRepository.findByName(house.getName());

    if (entity.isPresent()) {
      logger
          .error("[HOUSE - SERVICE] Error registering House. House already exists on db {} ",
              house);
      throw new EntityAlreadyExists();
    }
    house = houseRepository.save(house);
    logger.debug("[HOUSE - SERVICE] Service for register house end. House: {}", house);
    return house;
  }

  @Override
  public House removeHouse(long id) throws EntityNotFoundException {
    logger.debug("[HOUSE - SERVICE] Service for delete house {} start", id);
    House house = houseRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    logger.debug("[HOUSE - SERVICE] updating all sensor's house");
    sensorRepository.findByHouseId(id).stream().peek(sensor -> sensor.setHouseId(null))
        .forEach(sensorRepository::save);
    logger.debug("[HOUSE - SERVICE] Deleting all relation user-house for house {}", id);
    userHouseRelationRepository.findByHouseId(id).stream()
        .forEach(userHouseRelationRepository::delete);
    houseRepository.delete(house);
    logger.debug("[HOUSE - SERVICE] Service for delete house {} end", id);
    return house;
  }

  @Override
  public boolean hasUserVisibilityHouse(String name, long id) throws EntityNotFoundException {
    logger.debug("[HOUSE - SERVICE] Service for check if user {} has visibility for house {} start",
        name, id);
    logger.debug("[HOUSE - SERVICE] Searching user {}", name);
    User user = userRepository.findByUsername(name).get();
    logger.debug("[HOUSE - SERVICE] Searching house {}", id);
    houseRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    boolean hasVisibility = userHouseRelationRepository.findByUserId(user.getId()).stream()
        .map(userHouseRelation -> houseRepository.findById(userHouseRelation.getHouseId()).get())
        .anyMatch(house -> house.getId() == id);
    logger.debug(
        "[HOUSE - SERVICE] Service for check if user {} has visibility for house {} end. Result: {}",
        name, id, hasVisibility);
    return hasVisibility;
  }
}
