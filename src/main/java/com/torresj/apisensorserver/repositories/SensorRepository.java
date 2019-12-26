package com.torresj.apisensorserver.repositories;

import java.util.List;
import java.util.Optional;

import com.torresj.apisensorserver.models.entities.Sensor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<Sensor, Long> {

    Optional<Sensor> findByMac(String mac);

    Page<Sensor> findByIdIn(List<Long> ids, Pageable pageable);

    Page<Sensor> findBySensorTypeId(Long sensorTypeId, Pageable pageable);

    List<Sensor> findBySensorTypeId(Long sensorTypeId);

    Page<Sensor> findBySensorTypeIdAndNameContainingOrMacContaining(Long sensorTypeId, String name, String mac, Pageable pageable);

    Page<Sensor> findByNameContainingOrMacContaining(String name, String mac, Pageable pageable);

    Page<Sensor> findByName(String name, Pageable pageable);

    Page<Sensor> findByHouseId(Long houseId, Pageable pageable);

    List<Sensor> findByHouseId(Long houseId);
}