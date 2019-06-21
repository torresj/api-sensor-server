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
public class Variable implements Serializable {

  private static final long serialVersionUID = 4696253582022144805L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(updatable = false, nullable = false)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column
  private String units;

  @Column
  private String description;

  @Column(nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime createAt;
}