package com.torresj.apisensorserver.models;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class User {
    private String username;
    private String password;
    private LocalDateTime createAt;
    private LocalDateTime lastConnection;
}