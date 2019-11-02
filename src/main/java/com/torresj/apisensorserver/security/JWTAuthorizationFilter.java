package com.torresj.apisensorserver.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Jwts;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

  private String secret;

  private String prefix;

  private String header;


  public JWTAuthorizationFilter(AuthenticationManager authManager, String secret, String prefix, String header) {
    super(authManager);
    this.secret = secret;
    this.prefix = prefix;
    this.header = header;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
      FilterChain chain)
      throws IOException, ServletException {
    String jwt_header = req.getHeader(header);

    if (jwt_header == null || !jwt_header.startsWith(prefix)) {
      chain.doFilter(req, res);
      return;
    }

    UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

    SecurityContextHolder.getContext().setAuthentication(authentication);
    chain.doFilter(req, res);
  }

  private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
    String token = request.getHeader(header);
    if (token != null) {
      // parse the token.
      String user = Jwts.parser().setSigningKey(secret)
          .parseClaimsJws(token.replace(prefix, "")).getBody()
          .getSubject();

      if (user != null) {
        return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
      }
      return null;
    }
    return null;
  }
}
