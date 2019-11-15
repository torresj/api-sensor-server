package com.torresj.apisensorserver.models;

import lombok.Data;

@Data
public class LoginResponse {
    String username;

    String token;
}
