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
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

  private static final long serialVersionUID = 608601372639930858L;
  private static final String ROL_ADMIN = "admin";
  private static final String ROL_USER = "user";
  private static final String ROL_SENSOR = "sensor";

  public enum Rol {
    USER,
    ADMIN
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(updatable = false)
  private Long id;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private Rol rol;

  @Column(nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime createAt;

  @Column(nullable = false, updatable = false)
  @UpdateTimestamp
  private LocalDateTime lastConnection;
}