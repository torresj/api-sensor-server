package com.torresj.apisensorserver.jpa;

import java.util.Optional;

import com.torresj.apisensorserver.entities.SensorEntity;

import org.springframework.data.repository.CrudRepository;

public interface SensorRepository extends CrudRepository<SensorEntity, Long> {

    public Optional<SensorEntity> findByMac(String mac);

}