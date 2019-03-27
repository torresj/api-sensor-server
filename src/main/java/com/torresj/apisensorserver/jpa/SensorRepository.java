package com.torresj.apisensorserver.jpa;

import java.util.List;
import java.util.Optional;

import com.torresj.apisensorserver.models.Sensor;

import org.springframework.data.repository.CrudRepository;

public interface SensorRepository extends CrudRepository<Sensor, Long> {

    public Optional<Sensor> findByMac(String mac);

    public List<Sensor> findAll();

}