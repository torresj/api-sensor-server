package com.torresj.apisensorserver.mqtt;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MqttMessage implements Serializable {

  private static final long serialVersionUID = 5723323094505819557L;

  private String type;
  private int SensorId;
  private String msg;

}