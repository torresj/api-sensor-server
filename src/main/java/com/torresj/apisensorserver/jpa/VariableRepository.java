package com.torresj.apisensorserver.jpa;

import com.torresj.apisensorserver.models.Variable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VariableRepository extends JpaRepository<Variable, Long> {

  Optional<Variable> findByName(String name);

  Page<Variable> findByIdIn(List<Long> ids, Pageable pageable);
}