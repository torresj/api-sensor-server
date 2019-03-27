package com.torresj.apisensorserver.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Entity not found")
public class EntityNotFoundException extends Exception {

    private static final long serialVersionUID = 3423975341815748230L;

}