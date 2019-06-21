package com.torresj.apisensorserver.jpa;

import com.torresj.apisensorserver.models.VariableSensorRelation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VariableSensorRelationRepository extends
    JpaRepository<VariableSensorRelation, Long> {

  void deleteByVariableId(Long variableId);

  void deleteBySensorId(Long sensorId);

  List<VariableSensorRelation> findByVariableId(Long variableId);

  List<VariableSensorRelation> findBySensorId(Long sensorId);
}
