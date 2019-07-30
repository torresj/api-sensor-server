package com.torresj.apisensorserver.repositories;

import com.torresj.apisensorserver.models.SensorType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorTypeRepository extends JpaRepository<SensorType, Long> {

  Optional<SensorType> findByName(String name);

  Page<SensorType> findByName(String name, Pageable pageable);
}
