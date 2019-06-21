package com.torresj.apisensorserver.jpa;

import com.torresj.apisensorserver.models.Record;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {

  Page<Record> findBySensorIdAndVariableIdAndCreateAtBetween(long sensorId, long variableId,
      LocalDateTime from,
      LocalDateTime to, Pageable pageable);
}