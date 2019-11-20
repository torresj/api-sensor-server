package com.torresj.apisensorserver.services;

import java.util.List;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExistsException;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.entities.House;
import com.torresj.apisensorserver.models.entities.User;
import com.torresj.apisensorserver.models.entities.User.Role;

import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    Page<User> getUsers(String filter, Role role, int nPage, int elements);

    User getUser(long id) throws EntityNotFoundException;

    User getUser(String name) throws EntityNotFoundException;

    User register(User user) throws EntityAlreadyExistsException;

    User getLogginUser() throws EntityNotFoundException;

    List<House> getHouses(long id) throws EntityNotFoundException;

    House addHouse(long userId, long houseId) throws EntityNotFoundException;

    List<House> setHouses(long userId, List<Long> houses) throws EntityNotFoundException;

    User update(User user) throws EntityNotFoundException;

    House removeHouse(long id, long houseId) throws EntityNotFoundException;

    boolean isUserAllowed(String userName, Role... roles) throws EntityNotFoundException;

    boolean isSameUser(String userName, long userId) throws EntityNotFoundException;

    boolean isSameUser(String userName, String userNAme) throws EntityNotFoundException;

    User remove(long id) throws EntityNotFoundException;
}
