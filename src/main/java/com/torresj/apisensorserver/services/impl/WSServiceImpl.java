package com.torresj.apisensorserver.services.impl;

import com.torresj.apisensorserver.models.Sensor;
import com.torresj.apisensorserver.models.WSResponse;
import com.torresj.apisensorserver.repositories.SensorRepository;
import com.torresj.apisensorserver.services.WSService;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class WSServiceImpl implements WSService {

  /* Logs */
  private static final Logger logger = LogManager.getLogger(WSServiceImpl.class);

  private SensorRepository sensorRepository;

  public WSServiceImpl(SensorRepository sensorRepository) {
    this.sensorRepository = sensorRepository;
  }

  @Override
  public WSResponse validateConnection(long stationId) {
    logger.debug("[WSService - VALIDATE] Validating request");
    WSResponse response = new WSResponse();
    Optional<Sensor> maybeSensor = sensorRepository.findById(stationId);

    if (maybeSensor.isPresent()) {
      response.setSuccess(true);
      response.setText("Connection established ");
    } else {
      response.setSuccess(false);
      response.setText("Connection refused. Station doesn't exists");
    }
    return response;
  }
}
