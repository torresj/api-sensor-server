package com.torresj.apisensorserver.services.impl;

import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.WSResponse;
import com.torresj.apisensorserver.models.entities.User;
import com.torresj.apisensorserver.models.entities.User.Role;
import com.torresj.apisensorserver.services.SensorService;
import com.torresj.apisensorserver.services.UserService;
import com.torresj.apisensorserver.services.WSService;
import java.security.Principal;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;

@Service
public class WSServiceImpl implements WSService {

  /* Logs */
  private static final Logger logger = LogManager.getLogger(WSServiceImpl.class);

  private SensorService sensorService;

  private UserService userService;

  public WSServiceImpl(SensorService sensorService,
      UserService userService) {
    this.sensorService = sensorService;
    this.userService = userService;
  }

  @Override
  public WSResponse validateConnection(long stationId, Principal user) {
    logger.debug("[WSService - SERVICE] Validating request");
    WSResponse response = new WSResponse();
    try {
      sensorService.getSensor(stationId);
      if (user == null) {
        response.setSuccess(false);
        response.setText("Connection refused. User not allowed");
      } else if (userService.getUser(user.getName()).getRole() == Role.ADMIN) {
        response.setSuccess(true);
        response.setText("Connection established ");
      } else if (sensorService.hasUserVisibilitySensor(user.getName(), stationId)) {
        response.setSuccess(true);
        response.setText("Connection established ");
      } else {
        response.setSuccess(false);
        response.setText("Connection refused. Station doesn't exists");
      }

    } catch (EntityNotFoundException e) {
      response.setSuccess(false);
      response.setText("Connection refused. Station doesn't exists");
    }
    return response;
  }

  @Override
  public boolean checkSensorAndUserVisibility(StompHeaderAccessor accessor)
      throws EntityNotFoundException {
    boolean allowed = false;
    List<String> headers = accessor.getNativeHeader("destination");
    if (headers != null && !headers.isEmpty()) {
      String destination = headers.get(0);
      String idStr = destination.substring(destination.lastIndexOf('/') + 1);
      long id = Long.parseLong(idStr);
      sensorService.getSensor(id);
      User user = userService.getUser(accessor.getUser().getName());
      if (user != null && user.getRole() == Role.ADMIN) {
        allowed = true;
      } else {
        allowed = sensorService.hasUserVisibilitySensor(accessor.getUser().getName(), id);
      }
    }
    return allowed;
  }
}
