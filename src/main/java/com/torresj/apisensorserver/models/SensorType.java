package com.torresj.apisensorserver.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;

@Data
@Entity
public class SensorType implements Serializable {

    private static final long serialVersionUID = -3992426688582455846L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    @ElementCollection
    @CollectionTable(name = "actions")
    private List<String> tags = new ArrayList<String>();

    @OneToMany(mappedBy = "type")
    private List<Sensor> sensors;

}