package com.torresj.apisensorserver.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private WebSocketInChannelInterceptor webSocketInChannelInterceptor;
  private WebSocketOutChannelInterceptor webSocketOutChannelInterceptor;

  public WebSocketConfig(
      WebSocketInChannelInterceptor webSocketInChannelInterceptor,
      WebSocketOutChannelInterceptor webSocketOutChannelInterceptor) {
    this.webSocketInChannelInterceptor = webSocketInChannelInterceptor;
    this.webSocketOutChannelInterceptor = webSocketOutChannelInterceptor;
  }


  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws").setAllowedOrigins("*")
        .withSockJS();
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(webSocketInChannelInterceptor);
  }

  @Override
  public void configureClientOutboundChannel(ChannelRegistration registration) {
    registration.interceptors(webSocketOutChannelInterceptor);
  }
}