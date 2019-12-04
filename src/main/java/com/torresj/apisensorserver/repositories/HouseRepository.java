package com.torresj.apisensorserver.repositories;

import java.util.List;
import java.util.Optional;

import com.torresj.apisensorserver.models.entities.House;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseRepository extends JpaRepository<House, Long> {

    Optional<House> findByName(String name);

    Page<House> findByIdIn(List<Long> ids, Pageable pageable);

    List<House> findByIdIn(List<Long> ids);

    Page<House> findByNameContaining(String name, Pageable pageable);
}