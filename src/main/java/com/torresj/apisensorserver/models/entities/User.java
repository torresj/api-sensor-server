package com.torresj.apisensorserver.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

  private static final long serialVersionUID = 608601372639930858L;

  public enum Role {
    USER,
    ADMIN,
    STATION
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
  private Role role;

  @Column(nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime createAt;

  @Column(nullable = false, updatable = false)
  @UpdateTimestamp
  private LocalDateTime lastConnection;

  @Column
  private String name;

  @Column
  private String lastName;

  @Column
  private String phoneNumber;

  @Column
  private String email;
}