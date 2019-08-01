package com.torresj.apisensorserver.services;

import com.torresj.apisensorserver.models.WSResponse;

public interface WSService {

  WSResponse validateConnection(long stationId);
}
