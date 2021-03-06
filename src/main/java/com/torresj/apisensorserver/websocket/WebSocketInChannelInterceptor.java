package com.torresj.apisensorserver.websocket;

import java.util.ArrayList;
import java.util.List;

import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.exceptions.WSConnectionException;
import com.torresj.apisensorserver.services.WSService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;

@Component
public class WebSocketInChannelInterceptor implements ChannelInterceptor {

    /* Logs */
    private static final Logger logger = LogManager.getLogger(WebSocketInChannelInterceptor.class);

    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.prefix}")
    private String prefix;

    private WSService wsService;

    public WebSocketInChannelInterceptor(
            WSService wsService) {
        this.wsService = wsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            setUserToken(accessor);
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            try {
                if (!wsService.checkSensorAndUserVisibility(accessor)) {
                    logger.error("[WebSocket interceptor] User hasn't visibility to this station");
                    throw new WSConnectionException("User hasn't visibility to this station");
                }
            } catch (EntityNotFoundException e) {
                logger.error("[WebSocket interceptor] Sensor not exists");
                throw new WSConnectionException("Sensor not exists");
            }
        }
        return message;
    }

    private void setUserToken(StompHeaderAccessor accessor) {
        List<String> headers = accessor.getNativeHeader("Authorization");
        if (headers != null && !headers.isEmpty()) {
            String token = headers.get(0);
            String user = Jwts.parser().setSigningKey(secret)
                    .parseClaimsJws(token.replace(prefix, "")).getBody()
                    .getSubject();
            if (user != null) {
                UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(
                        user, null, new ArrayList<>());
                accessor.setUser(userToken);
            }
        }
    }
}
