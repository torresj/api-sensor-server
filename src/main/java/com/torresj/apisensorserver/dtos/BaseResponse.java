package com.torresj.apisensorserver.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BaseResponse {

    private LocalDateTime date;
    private int code;
    private String error;

    public BaseResponse() {
        this.date = LocalDateTime.now();
    }

    public BaseResponse(int code, String error) {
        this.code = code;
        this.error = error;
    }
}
