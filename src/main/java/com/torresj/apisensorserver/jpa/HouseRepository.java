package com.torresj.apisensorserver.jpa;

import java.util.Optional;

import com.torresj.apisensorserver.models.House;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseRepository extends JpaRepository<House, Long> {
    Optional<House> findByName(String name);
}