package com.torresj.apisensorserver.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class RecordEntity implements Serializable {

    private static final long serialVersionUID = 3094710057682194602L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    private SensorEntity sensor;

    @ManyToOne
    private VariableEntity variable;

    @Column(nullable = false)
    private double value;

    @Column(nullable = false)
    private LocalDateTime date;

}