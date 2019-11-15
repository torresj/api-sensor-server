package com.torresj.apisensorserver.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.entities.User;
import com.torresj.apisensorserver.services.UserService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Jwts;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private String secret;

    private String prefix;

    private String header;

    private UserService userService;

    public JWTAuthorizationFilter(AuthenticationManager authManager, UserService userService, String secret, String prefix,
            String header) {
        super(authManager);
        this.secret = secret;
        this.prefix = prefix;
        this.header = header;
        this.userService = userService;
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

        String userName = (String) authentication.getPrincipal();

        if (userName != null) {
            try {
                User user = userService.getUser(userName);
                user.setLastConnection(LocalDateTime.now());
                userService.update(user);
            } catch (EntityNotFoundException e) {
                logger.error("[JWTAuthorizationFilter] Error updating number of logins in login request", e);
            }
        }

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
