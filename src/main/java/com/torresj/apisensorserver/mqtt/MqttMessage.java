package com.torresj.apisensorserver.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MqttMessage implements Serializable {

  private static final long serialVersionUID = 5723323094505819557L;

  private String type;
  private long sensorId;
  private String msg;

}