package com.torresj.apisensorserver.services;

import static java.util.Collections.emptyList;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.jpa.UserRepository;
import com.torresj.apisensorserver.models.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class UserService implements UserDetailsService {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(RecordService.class);

    @Autowired
    private UserRepository userRepository;

    public Page<User> getUsers(int nPage, int elements) {
        return null;
    }

    public User getUser(long id) throws EntityNotFoundException {
        return null;
    }

    public User getUser(String name) throws EntityNotFoundException {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                emptyList());
    }

    public User register(User user) throws EntityAlreadyExists {
        logger.debug("[USER - REGISTER] Registering user ");
        Optional<User> entity = userRepository.findByUsername(user.getUsername());

        if (entity.isPresent()) {
            logger.error("[USER - REGISTER] Error registering user");
            throw new EntityAlreadyExists();
        }

        return userRepository.save(user);
    }

}