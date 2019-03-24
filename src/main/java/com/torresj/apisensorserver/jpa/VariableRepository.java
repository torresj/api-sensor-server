package com.torresj.apisensorserver.jpa;

import com.torresj.apisensorserver.entities.VariableEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VariableRepository extends JpaRepository<VariableEntity, Long> {

}