package com.torresj.apisensorserver.services.impl;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExistsException;
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
import java.time.LocalDateTime;
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
  public Page<User> getUsers(String filter, Role role, int nPage, int elements) {
    logger.debug("[USER - SERVICE] Service for getting users start");
    PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());
    Page<User> page = null;
    if(filter == null && role == null){
      page = userRepository.findAll(pageRequest);
    }else if(filter != null && role == null){
      page = userRepository.findByUsernameContaining(filter,pageRequest);
    }else if(filter != null && role != null){
      page = userRepository.findByUsernameContainingAndRole(filter,role,pageRequest);
    }else if(filter == null && role != null){
      page = userRepository.findByRole(role,pageRequest);
    }
    logger.debug("[USER - SERVICE] Service for getting users end. Users: {}", page.getContent());
    return page;
  }

  @Override
  public User getUser(long id) throws EntityNotFoundException {
    logger.debug("[USER - SERVICE] Service for getting user {} start", id);
    User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    logger.debug("[USER - SERVICE] Service for getting user {} end. User: {}", id, user);
    return user;
  }

  @Override
  public User getUser(String name) throws EntityNotFoundException {
    logger.debug("[USER - SERVICE] Service for getting user {} start", name);
    User user = userRepository.findByUsername(name).orElseThrow(EntityNotFoundException::new);
    logger.debug("[USER - SERVICE] Service for getting user {} end. User: {}", name, user);
    return user;
  }

  @Override
  public User register(User user) throws EntityAlreadyExistsException {
    logger.debug("[USER - SERVICE] Service for register user start. User: {}", user);
    Optional<User> entity = userRepository.findByUsername(user.getUsername());

    if (entity.isPresent()) {
      logger.error("[USER - SERVICE] Error registering user {}", user);
      throw new EntityAlreadyExistsException();
    }
    User userRegstered = userRepository.save(user);
    logger.debug("[USER - SERVICE] Service for register user end. User: {}", userRegstered);
    return userRegstered;
  }

  @Override
  public Page<House> getHouses(long id, int nPage, int elements) throws EntityNotFoundException {
    logger.debug("[USER - SERVICES] Service for get user {} houses start", id);
    userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    PageRequest pageRequest = PageRequest.of(nPage, elements, Sort.by("createAt").descending());
    List<Long> ids = userHouseRelationRepository.findByUserId(id).stream()
        .map(UserHouseRelation::getHouseId).collect(
            Collectors.toList());
    Page<House> page = houseRepository.findByIdIn(ids, pageRequest);
    logger.debug("[USER - SERVICES] Service for get user {} houses end. Houses: {}", id,
        page.getContent());
    return page;
  }

  @Override
  public House addHouse(long userId, long houseId) throws EntityNotFoundException {
    logger.debug(
        "[USER - SERVICE] Service for adding house {} to user {} start", houseId, userId);
    userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
    House house = houseRepository.findById(houseId)
        .orElseThrow(EntityNotFoundException::new);
    UserHouseRelation relation = new UserHouseRelation();
    relation.setUserId(userId);
    relation.setHouseId(houseId);
    userHouseRelationRepository.save(relation);
    logger.debug(
        "[USER - SERVICE] Service for adding house {} to user {} end", houseId, userId);
    return house;
  }

  @Override
  public User update(User user) throws EntityNotFoundException {
    logger.debug("[USER - SERVICE] Service for update user start. User: {}", user);
    User entity = userRepository.findByUsername(user.getUsername())
        .orElseThrow(EntityNotFoundException::new);
    user.setId(entity.getId());
    User userUpdated = userRepository.save(user);
    logger.debug("[USER - SERVICE] Service for update user end. User: {}", userUpdated);
    return userUpdated;
  }

  @Override
  public House removeHouse(long userId, long houseId) throws EntityNotFoundException {
    logger.debug(
        "[USER - SERVICE] Service for remove house {} from user {} start", houseId, userId);
    userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
    House house = houseRepository.findById(houseId)
        .orElseThrow(EntityNotFoundException::new);
    userHouseRelationRepository.delete(
        userHouseRelationRepository.findByUserIdAndHouseId(userId, houseId)
            .orElseThrow(EntityNotFoundException::new));
    logger.debug(
        "[USER - SERVICE] Service for remove house {} from user {} end", houseId, userId);
    return house;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    logger.debug(
        "[USER - SERVICE] Service for find user {} start", username);
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(username));
    CustomUserDetails userDetails = new CustomUserDetails(user);
    logger.debug(
        "[USER - SERVICE] Service for find user {} end. User found", username);
    return userDetails;
  }

  @Override
  public User getLogginUser() throws EntityNotFoundException {
    logger.debug(
        "[USER - SERVICE] Service for find  loggin user {} start");
    Principal principal = SecurityContextHolder.getContext().getAuthentication();
    User user = userRepository.findByUsername(principal.getName())
        .orElseThrow(EntityNotFoundException::new);
    logger.debug(
        "[USER - SERVICE] Service for find  loggin user {} end. User logged {}",
        user.getUsername());
    return user;
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
    logger.debug(
        "[USER - SERVICE] Service for remove user {} start");
    User entity = userRepository.findById(id)
        .orElseThrow(EntityNotFoundException::new);
    userHouseRelationRepository.findByUserId(id).stream()
        .forEach(userHouseRelationRepository::delete);
    userRepository.delete(entity);
    logger.debug(
        "[USER - SERVICE] Service for remove user {} end");
    return entity;
  }
}
