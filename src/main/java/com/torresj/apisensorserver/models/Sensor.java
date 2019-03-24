package com.torresj.apisensorserver.models;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class Sensor {

    /* Sensor types */
    public static final String WHEATHER = "WHEATHER";
    public static final String BLIND = "BLIND";

    private String id;
    private String type;
    private String mac;
    private String ip;
    private LocalDateTime createAt;
    private LocalDateTime lastConnection;
    private List<Variable> variables;

}