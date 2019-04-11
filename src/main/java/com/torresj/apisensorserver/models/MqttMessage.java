package com.torresj.apisensorserver.models;

import java.io.Serializable;

import lombok.Data;

@Data
public class MqttMessage implements Serializable {

    private static final long serialVersionUID = 5723323094505819557L;

    private String type;
    private int SensorId;
    private String msg;

}