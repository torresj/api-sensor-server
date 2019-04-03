package com.torresj.apisensorserver.jpa;

import java.time.LocalDateTime;

import com.torresj.apisensorserver.models.Record;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {

    Page<Record> findByDateTimeBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

}