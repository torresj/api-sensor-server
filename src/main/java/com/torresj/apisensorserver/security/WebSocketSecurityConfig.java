package com.torresj.apisensorserver.security;

import static org.springframework.messaging.simp.SimpMessageType.CONNECT;
import static org.springframework.messaging.simp.SimpMessageType.DISCONNECT;
import static org.springframework.messaging.simp.SimpMessageType.HEARTBEAT;
import static org.springframework.messaging.simp.SimpMessageType.UNSUBSCRIBE;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

  @Override
  protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
    messages.simpTypeMatchers(CONNECT, UNSUBSCRIBE, DISCONNECT, HEARTBEAT).permitAll()
        .simpDestMatchers("/app/**", "/topic/**").authenticated()
        .simpSubscribeDestMatchers("/topic/**").authenticated()
        .anyMessage().denyAll();
  }

  @Override
  protected boolean sameOriginDisabled() {
    return true;
  }
}