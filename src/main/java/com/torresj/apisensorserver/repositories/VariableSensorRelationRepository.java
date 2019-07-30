package com.torresj.apisensorserver.repositories;

import com.torresj.apisensorserver.models.VariableSensorRelation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VariableSensorRelationRepository extends
    JpaRepository<VariableSensorRelation, Long> {

  void deleteByVariableId(Long variableId);

  void deleteBySensorId(Long sensorId);

  List<VariableSensorRelation> findByVariableId(Long variableId);

  List<VariableSensorRelation> findBySensorId(Long sensorId);

  Optional<VariableSensorRelation> findBySensorIdAndVariableId(long sensorId, long variableId);

  void deleteBySensorIdAndVariableId(long sensorId, long variableId);
}
