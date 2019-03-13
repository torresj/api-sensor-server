package com.torresj.apisensorserver.models;

import lombok.Data;

@Data
public class Sensor {

    /* Sensor types */
    public static final String WHEATHER = "WHEATHER";
    public static final String BLIND = "BLIND";

    private String id;
    private String type;
    private ConnectionData connData;
}