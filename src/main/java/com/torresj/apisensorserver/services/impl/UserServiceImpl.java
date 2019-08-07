package com.torresj.apisensorserver.services.impl;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.entities.House;
import com.torresj.apisensorserver.models.entities.User;
import com.torresj.apisensorserver.models.entities.User.Role;
import com.torresj.apisensorserver.models.entities.UserHouseRelation;
import com.torresj.apisensorserver.repositories.HouseRepository;
import com.torresj.apisensorserver.repositories.UserHouseRelationRepository;
import com.torresj.apisensorserver.repositories.UserRepository;
import com.torresj.apisensorserver.security.CustomUserDetails;
import com.torresj.apisensorserver.services.UserService;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  /* Logs */
  private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

  private UserRepository userRepository;

  private UserHouseRelationRepository userHouseRelationRepository;

  private HouseRepository houseRepository;

  public UserServiceImpl(UserRepository userRepository,
      UserHouseRelationRepository userHouseRelationRepository,
      HouseRepository houseRepository) {
    this.userRepository = userRepository;
    this.userHouseRelationRepository = userHouseRelationRepository;
    this.houseRepository = houseRepository;
  }

  @Override
  public Page<User> getUsers(int nPage, int elements) {
    logger.debug("[USER - GET] Getting users");
    PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());
    return userRepository.findAll(pageRequest);
  }

  @Override
  public User getUser(long id) throws EntityNotFoundException {
    logger.debug("[USER - GET USER] Searching user by id: " + id);
    return userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
  }

  @Override
  public User getUser(String name) throws EntityNotFoundException {
    logger.debug("[USER - GET USER] Searching user by name: " + name);
    return userRepository.findByUsername(name).orElseThrow(EntityNotFoundException::new);
  }

  @Override
  public User register(User user) throws EntityAlreadyExists {
    logger.debug("[USER - REGISTER] Registering user ");
    Optional<User> entity = userRepository.findByUsername(user.getUsername());

    if (entity.isPresent()) {
      logger.error("[USER - REGISTER] Error registering user");
      throw new EntityAlreadyExists();
    }

    return userRepository.save(user);
  }

  @Override
  public Page<House> getHouses(long id, int nPage, int elements) throws EntityNotFoundException {
    logger.debug("[USER HOUSES - GET] Getting user houses ");
    userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());
    List<Long> ids = userHouseRelationRepository.findByUserId(id).stream()
        .map(UserHouseRelation::getHouseId).collect(
            Collectors.toList());
    return houseRepository.findByIdIn(ids, pageRequest);
  }

  @Override
  public House addHouse(long userId, long houseId) throws EntityNotFoundException {
    logger.debug(
        "[USER HOUSE - ADD] Add house " + houseId + " to User houses " + userId + " list");
    userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
    House house = houseRepository.findById(houseId)
        .orElseThrow(EntityNotFoundException::new);
    UserHouseRelation relation = new UserHouseRelation();
    relation.setUserId(userId);
    relation.setHouseId(houseId);
    userHouseRelationRepository.save(relation);
    return house;
  }

  @Override
  public User update(User user) throws EntityNotFoundException {
    logger.debug("[USER - UPDATE] Updating user ");
    User entity = userRepository.findByUsername(user.getUsername())
        .orElseThrow(EntityNotFoundException::new);
    user.setId(entity.getId());
    return userRepository.save(user);
  }

  @Override
  public House removeHouse(long userId, long houseId) throws EntityNotFoundException {
    logger.debug(
        "[USER HOUSE - REMOVE] Remove house " + houseId + " from User houses " + userId + " list");
    userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
    House house = houseRepository.findById(houseId)
        .orElseThrow(EntityNotFoundException::new);
    userHouseRelationRepository.delete(
        userHouseRelationRepository.findByUserIdAndHouseId(userId, houseId)
            .orElseThrow(EntityNotFoundException::new));
    return house;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(username));
    CustomUserDetails userDetails = new CustomUserDetails(user);

    return userDetails;
  }

  @Override
  public User getLogginUser() throws EntityNotFoundException {
    Principal principal = SecurityContextHolder.getContext().getAuthentication();
    return userRepository.findByUsername(principal.getName())
        .orElseThrow(EntityNotFoundException::new);
  }

  @Override
  public boolean isUserAllowed(String userName, Role... roles) throws EntityNotFoundException {
    User user = getUser(userName);
    return Arrays.asList(roles).contains(user.getRole()) ? true : false;
  }

  @Override
  public boolean isSameUser(String userName, long userId) throws EntityNotFoundException {
    User principal = getUser(userName);
    User user = getUser(userId);
    return principal.equals(user);
  }

  @Override
  public boolean isSameUser(String userName, String userToFind) throws EntityNotFoundException {
    User principal = getUser(userName);
    User user = getUser(userToFind);
    return principal.equals(user);
  }

  @Override
  public User remove(long id) throws EntityNotFoundException {
    logger.debug("[USER - REMOVE] Removing user ");
    User entity = userRepository.findById(id)
        .orElseThrow(EntityNotFoundException::new);
    userHouseRelationRepository.findByUserId(id).stream()
        .forEach(userHouseRelationRepository::delete);
    userRepository.delete(entity);

    return entity;
  }
}
