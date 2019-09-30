package com.torresj.apisensorserver.models;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LoginResponse {
    String username;
    String token;
}
