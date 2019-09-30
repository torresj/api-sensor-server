package com.torresj.apisensorserver.security;

import static com.torresj.apisensorserver.security.SecurityConstants.EXPIRATION_TIME;
import static com.torresj.apisensorserver.security.SecurityConstants.HEADER_STRING;
import static com.torresj.apisensorserver.security.SecurityConstants.ISSUER_INFO;
import static com.torresj.apisensorserver.security.SecurityConstants.SECRET;
import static com.torresj.apisensorserver.security.SecurityConstants.TOKEN_PREFIX;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torresj.apisensorserver.models.LoginResponse;
import com.torresj.apisensorserver.models.entities.User;
import com.torresj.apisensorserver.services.impl.RecordServiceImpl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  /* Logs */
  private static final Logger logger = LogManager.getLogger(JWTAuthenticationFilter.class);

  private final AuthenticationManager authenticationManager;

  public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
      throws AuthenticationException {
    try {
      User user = new ObjectMapper().readValue(req.getInputStream(), User.class);

      return authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res,
      FilterChain chain,
      Authentication auth) {
    String userName = ((CustomUserDetails) auth.getPrincipal())
            .getUsername();
    String token = Jwts.builder().setIssuedAt(new Date()).setIssuer(ISSUER_INFO)
        .setSubject(userName)
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(SignatureAlgorithm.HS512, SECRET).compact();
    res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + token);
    LoginResponse userData = new LoginResponse();
    userData.setToken(token);
    userData.setUsername(userName);
    try {
      String json = new ObjectMapper().writeValueAsString(userData);
      PrintWriter out = res.getWriter();
      out.print(json);
      out.flush();
    } catch (IOException e) {
      logger.error("[JWTAuthenticationFilter] Error generating user data response in login request", e);
    }
  }
}