package com.torresj.apisensorserver.jpa;

import com.torresj.apisensorserver.entities.RecordEntity;

import org.springframework.data.repository.CrudRepository;

public interface RecordRepository extends CrudRepository<RecordEntity, Long> {

}