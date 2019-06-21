package com.torresj.apisensorserver.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;

@Entity
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VariableSensorRelation implements Serializable {

  private static final long serialVersionUID = -8753081719379854792L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(updatable = false)
  private Long id;

  @Column(updatable = false, nullable = false)
  private Long sensorId;

  @Column(updatable = false, nullable = false)
  private Long variableId;

}
