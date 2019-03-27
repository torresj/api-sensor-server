package com.torresj.apisensorserver.jpa;

import com.torresj.apisensorserver.models.Record;

import org.springframework.data.repository.CrudRepository;

public interface RecordRepository extends CrudRepository<Record, Long> {

}