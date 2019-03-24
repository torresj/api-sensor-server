package com.torresj.apisensorserver.jpa;

import com.torresj.apisensorserver.entities.UserEntity;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

}