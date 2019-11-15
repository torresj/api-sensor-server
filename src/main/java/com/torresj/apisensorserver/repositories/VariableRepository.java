package com.torresj.apisensorserver.repositories;

import java.util.List;
import java.util.Optional;

import com.torresj.apisensorserver.models.entities.Variable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VariableRepository extends JpaRepository<Variable, Long> {

    Optional<Variable> findByName(String name);

    Page<Variable> findByIdIn(List<Long> ids, Pageable pageable);

    Page<Variable> findByName(String name, Pageable pageable);
}