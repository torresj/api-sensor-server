package com.torresj.apisensorserver.jpa;

import java.util.Optional;

import com.torresj.apisensorserver.entities.VariableEntity;

import org.springframework.data.repository.CrudRepository;

public interface VariableRepository extends CrudRepository<VariableEntity, Long> {
    public Optional<VariableEntity> findByName(String name);
}