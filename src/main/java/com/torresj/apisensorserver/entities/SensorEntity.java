package com.torresj.apisensorserver.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;

@Entity
@Data
public class SensorEntity implements Serializable {

    private static final long serialVersionUID = -8753081269379854792L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private char type;

    @Column(nullable = false, unique = true)
    private String mac;

    @Column(nullable = false)
    private String ip;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(nullable = false)
    private LocalDateTime lastConnection;

    @OneToMany(targetEntity = VariableEntity.class)
    private List variables;
}