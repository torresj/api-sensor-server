package com.torresj.apisensorserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SocketMessage {

  private String privateIp;
  private String action;
}
