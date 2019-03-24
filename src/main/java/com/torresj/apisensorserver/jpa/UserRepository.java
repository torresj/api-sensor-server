package com.torresj.apisensorserver.jpa;

import com.torresj.apisensorserver.entities.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

}