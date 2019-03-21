package com.torresj.apisensorserver.models;

import lombok.Data;

@Data
public class Variable {
    private int id;
    private String name;
    private String units;
    private String description;
}