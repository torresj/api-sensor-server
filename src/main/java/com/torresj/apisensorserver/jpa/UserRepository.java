package com.torresj.apisensorserver.jpa;

import com.torresj.apisensorserver.models.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}