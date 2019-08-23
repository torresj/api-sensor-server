package com.torresj.apisensorserver.services;

import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.WSResponse;
import java.security.Principal;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

public interface WSService {

  WSResponse validateConnection(long stationId, Principal user);

  boolean checkSensorAndUserVisibility(StompHeaderAccessor accessor)
      throws EntityNotFoundException;
}
