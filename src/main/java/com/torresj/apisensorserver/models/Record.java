package com.torresj.apisensorserver.models;

import lombok.Data;

@Data
public class Record {
    private int variableId;
    private double value;
}