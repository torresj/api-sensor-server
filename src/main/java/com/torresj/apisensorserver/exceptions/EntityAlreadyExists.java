package com.torresj.apisensorserver.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(code = HttpStatus.OK, reason = "Entity already exists")
public class EntityAlreadyExists extends Exception {

    private static final long serialVersionUID = -7306413635068340585L;

}