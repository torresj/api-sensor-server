package com.torresj.apisensorserver.jpa;

import com.torresj.apisensorserver.models.User;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

}