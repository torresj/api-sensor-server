package com.torresj.apisensorserver.exceptions;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class ApiException extends Exception {
    private static final long serialVersionUID = 3220555322315675841L;

    public ApiException(String error, int code) {
        this.error = error;
        this.code = code;
    }

    private String error;
    private int code;

}