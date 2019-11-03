package com.torresj.apisensorserver.repositories;

import com.torresj.apisensorserver.models.entities.User;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUsername(String username);
  Page<User> findByUsernameContaining(String username, Pageable pageable);
  Page<User> findByUsernameContainingAndRole(String username, User.Role role, Pageable pageable);
  Page<User> findByRole(User.Role role, Pageable pageable);
}