package com.torresj.apisensorserver.models;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Record {
    private int id;
    private int variableId;
    private int SensorId;
    private double value;
    private LocalDateTime date;
}