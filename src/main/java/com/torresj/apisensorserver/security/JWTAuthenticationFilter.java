package com.torresj.apisensorserver.security;

import static com.torresj.apisensorserver.security.SecurityConstants.EXPIRATION_TIME;
import static com.torresj.apisensorserver.security.SecurityConstants.HEADER_STRING;
import static com.torresj.apisensorserver.security.SecurityConstants.ISSUER_INFO;
import static com.torresj.apisensorserver.security.SecurityConstants.SECRET;
import static com.torresj.apisensorserver.security.SecurityConstants.TOKEN_PREFIX;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.torresj.apisensorserver.models.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

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
    String token = Jwts.builder().setIssuedAt(new Date()).setIssuer(ISSUER_INFO)
        .setSubject(((CustomUserDetails) auth.getPrincipal())
            .getUsername())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(SignatureAlgorithm.HS512, SECRET).compact();
    res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + token);
  }
}