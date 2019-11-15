package com.torresj.apisensorserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.torresj")
@EnableJpaRepositories("com.torresj.apisensorserver.repositories")
@EntityScan("com.torresj.apisensorserver.models.entities")
public class ApiSensorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiSensorApplication.class, args);

    }

}
