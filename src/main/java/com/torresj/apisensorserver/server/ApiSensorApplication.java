package com.torresj.apisensorserver.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.torresj")
public class ApiSensorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiSensorApplication.class, args);

	}

}
