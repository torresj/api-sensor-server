package com.torresj.apisensorserver.controller;

import com.torresj.apisensorserver.models.WSResponse;
import com.torresj.apisensorserver.services.WSService;
import java.security.Principal;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WSController {

  private WSService wsService;

  public WSController(WSService wsService) {
    this.wsService = wsService;
  }

  @MessageMapping("/station/{stationId}")
  @SendTo("/topic/station/{stationId}")
  public WSResponse connection(@DestinationVariable("stationId") long stationId,
      Principal principal) {
    return wsService.validateConnection(stationId, principal);
  }
}