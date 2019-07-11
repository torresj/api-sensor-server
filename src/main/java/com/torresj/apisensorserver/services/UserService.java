package com.torresj.apisensorserver.services;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.House;
import com.torresj.apisensorserver.models.User;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

  Page<User> getUsers(int nPage, int elements);

  User getUser(long id) throws EntityNotFoundException;

  User getUser(String name) throws EntityNotFoundException;

  User register(User user) throws EntityAlreadyExists;

  User getLogginUser() throws EntityNotFoundException;

  Page<House> getHouses(long id, int nPage, int elements) throws EntityNotFoundException;

  House addHouse(long userId, long houseId) throws EntityNotFoundException;

  User update(User user) throws EntityNotFoundException;

  House removeHouse(long id, long houseId) throws EntityNotFoundException;

}
