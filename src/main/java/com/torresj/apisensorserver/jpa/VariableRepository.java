package com.torresj.apisensorserver.jpa;

import java.util.List;
import java.util.Optional;

import com.torresj.apisensorserver.models.Variable;

import org.springframework.data.repository.CrudRepository;

public interface VariableRepository extends CrudRepository<Variable, Long> {
    public Optional<Variable> findByName(String name);

    public List<Variable> findAll();
}