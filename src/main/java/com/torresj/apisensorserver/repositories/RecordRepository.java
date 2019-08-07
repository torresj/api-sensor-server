package com.torresj.apisensorserver.repositories;

import com.torresj.apisensorserver.models.entities.Record;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {

  Page<Record> findBySensorIdAndVariableIdAndCreateAtBetween(long sensorId, long variableId,
      LocalDateTime from,
      LocalDateTime to, Pageable pageable);

  Optional<Record> findBySensorIdAndVariableIdAndDate(long sensorId, long variableId,
      LocalDateTime date);
}