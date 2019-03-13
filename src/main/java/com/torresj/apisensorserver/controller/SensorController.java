package com.torresj.apisensorserver.controller;

import org.springframework.web.bind.annotation.RestController;

import com.torresj.apisensorserver.dtos.BaseResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/sensor")
public class SensorController {

    @PutMapping(value = "/register")
    public BaseResponse register() {
        return new BaseResponse();
    }

}