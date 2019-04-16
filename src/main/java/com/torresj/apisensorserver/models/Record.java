package com.torresj.apisensorserver.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Entity
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Record implements Serializable {

    private static final long serialVersionUID = 3094710057682194602L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    private long sensorId;

    @Column(nullable = false)
    private long variableId;

    @Column(nullable = false)
    private double value;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createAt;

}