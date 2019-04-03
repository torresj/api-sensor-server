package com.torresj.apisensorserver.jpa;

import java.util.List;
import java.util.Optional;

import com.torresj.apisensorserver.models.Variable;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VariableRepository extends JpaRepository<Variable, Long> {
    public Optional<Variable> findByName(String name);

    public List<Variable> findAll();
}