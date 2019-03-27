package com.torresj.apisensorserver.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Entity
@Data
public class Record implements Serializable {

    private static final long serialVersionUID = 3094710057682194602L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Sensor sensor;

    @ManyToOne
    private Variable variable;

    @Column(nullable = false)
    private double value;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime date;

}