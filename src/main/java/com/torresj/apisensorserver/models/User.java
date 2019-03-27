package com.torresj.apisensorserver.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 608601372639930858L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime lastConnection;
}