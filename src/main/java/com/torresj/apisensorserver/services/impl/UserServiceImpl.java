package com.torresj.apisensorserver.services.impl;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.jpa.HouseRepository;
import com.torresj.apisensorserver.jpa.UserHouseRelationRepository;
import com.torresj.apisensorserver.jpa.UserRepository;
import com.torresj.apisensorserver.models.House;
import com.torresj.apisensorserver.models.User;
import com.torresj.apisensorserver.models.User.Role;
import com.torresj.apisensorserver.models.UserHouseRelation;
import com.torresj.apisensorserver.services.UserService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
        "[USER HOUSE - REM|OVE] Remove house " + houseId + " from User houses " + userId + " list");
    userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
    House house = houseRepository.findById(houseId)
        .orElseThrow(EntityNotFoundException::new);
    userHouseRelationRepository.deleteByHouseIdAndUserId(houseId, userId);
    return house;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(username));

    return new org.springframework.security.core.userdetails.User(user.getUsername(),
        user.getPassword(), getAuthorities(user.getRole()));
  }

  @Override
  public User getLogginUser() throws EntityNotFoundException {
    Principal principal = SecurityContextHolder.getContext().getAuthentication();
    return userRepository.findByUsername(principal.getName())
        .orElseThrow(EntityNotFoundException::new);
  }

  private Set<? extends GrantedAuthority> getAuthorities(User.Role userRole) {
    List<Role> roles = new ArrayList<>();
    roles.add(userRole);
    Set<SimpleGrantedAuthority> authorities = new HashSet<>();
    roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name())));
    return authorities;
  }
}
