package com.torresj.apisensorserver.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sensor implements Serializable {

  private static final long serialVersionUID = -8753081269379854792L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(updatable = false)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private Long sensorTypeId;

  @Column()
  private Long houseId;

  @Column(nullable = false, unique = true)
  private String mac;

  @Column(nullable = false)
  private String ip;

  @Column(nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime createAt;

  @Column(nullable = false)
  private LocalDateTime lastConnection;
}