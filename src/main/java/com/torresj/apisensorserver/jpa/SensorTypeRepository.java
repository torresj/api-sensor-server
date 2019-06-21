package com.torresj.apisensorserver.jpa;

import com.torresj.apisensorserver.models.SensorType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorTypeRepository extends JpaRepository<SensorType, Long> {

  Optional<SensorType> findByName(String name);
}
