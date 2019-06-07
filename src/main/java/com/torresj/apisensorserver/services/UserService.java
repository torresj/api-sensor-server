package com.torresj.apisensorserver.services;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.User;

import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {
    Page<User> getUsers(int nPage, int elements);

    User getUser(long id) throws EntityNotFoundException;

    User getUser(String name) throws EntityNotFoundException;

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    User register(User user) throws EntityAlreadyExists;

    User getLogginUser() throws EntityNotFoundException;
}
