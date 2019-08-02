package com.torresj.apisensorserver.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.torresj.apisensorserver.models.Record;
import com.torresj.apisensorserver.services.WSService;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class WebSocketOutChannelInterceptor implements ChannelInterceptor {

  /* Logs */
  private static final Logger logger = LogManager.getLogger(WebSocketOutChannelInterceptor.class);

  private WSService wsService;

  public WebSocketOutChannelInterceptor(
      WSService wsService) {
    this.wsService = wsService;
  }

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      String jsonString = new String((byte[]) (message.getPayload()));
      Record record = objectMapper.readValue(jsonString, Record.class);
    } catch (IOException e) {

    }

    return message;
  }
}
