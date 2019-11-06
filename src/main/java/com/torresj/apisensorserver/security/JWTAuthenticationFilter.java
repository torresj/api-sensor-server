package com.torresj.apisensorserver.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.torresj.apisensorserver.exceptions.EntityAlreadyExistsException;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.LoginResponse;
import com.torresj.apisensorserver.models.entities.User;
import com.torresj.apisensorserver.services.UserService;

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

  private String secret;

  private String expiration;

  private String prefix;

  private String header;

  private String issuer;

  private final AuthenticationManager authenticationManager;

  private final UserService userService;

  public JWTAuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, String secret, String expiration, String prefix,
          String header, String issuer) {
    this.secret = secret;
    this.expiration = expiration;
    this.prefix = prefix;
    this.header = header;
    this.issuer = issuer;
    this.authenticationManager = authenticationManager;
    this.userService = userService;
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
    String token = Jwts.builder().setIssuedAt(new Date()).setIssuer(issuer)
        .setSubject(userName)
        .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(expiration)))
        .signWith(SignatureAlgorithm.HS512, secret).compact();
    res.addHeader(header, prefix + " " + token);
    LoginResponse userData = new LoginResponse();
    userData.setToken(token);
    userData.setUsername(userName);

    try {
      User user = userService.getUser(userName);
      user.setNumLogins(user.getNumLogins() == null ? 1 :user.getNumLogins()+1);
      userService.update(user);

      String json = new ObjectMapper().writeValueAsString(userData);
      PrintWriter out = res.getWriter();
      out.print(json);
      out.flush();
    } catch (IOException e) {
      logger.error("[JWTAuthenticationFilter] Error generating user data response in login request", e);
    } catch (EntityNotFoundException e) {
      logger.error("[JWTAuthenticationFilter] Error saving user login numbers login request", e);
    }
  }
}