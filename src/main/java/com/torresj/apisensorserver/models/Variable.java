package com.torresj.apisensorserver.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Entity
@Data
public class Variable implements Serializable {

    private static final long serialVersionUID = 4696253582022194805L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String units;

    @Column
    private String description;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createAt;
}