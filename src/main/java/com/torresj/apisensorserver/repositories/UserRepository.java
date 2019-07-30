package com.torresj.apisensorserver.repositories;

import com.torresj.apisensorserver.models.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUsername(String username);
}