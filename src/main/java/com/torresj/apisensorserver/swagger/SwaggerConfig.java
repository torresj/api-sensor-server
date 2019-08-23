package com.torresj.apisensorserver.swagger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

  @Bean
  public Docket api() {
    ParameterBuilder parameterBuilder = new ParameterBuilder();
    Parameter param = parameterBuilder.name("Authorization").modelRef(new ModelRef("string"))
        .parameterType("header").required(true).build();
    List<Parameter> parameters = new ArrayList<>();
    parameters.add(param);

    return new Docket(DocumentationType.SWAGGER_2).select()
        .apis(RequestHandlerSelectors.basePackage("com.torresj.apisensorserver.controller"))
        .paths(PathSelectors.any()).build().apiInfo((apiInfo()))
        .globalOperationParameters(parameters);

  }

  private ApiInfo apiInfo() {
    return new ApiInfo("Api Sensor", "Api for manage sensor information", "1.0.0",
        "https://raw.githubusercontent.com/torresj/api-sensor-server/master/LICENSE",
        new Contact("Jaime Torres", "https://github.com/torresj", "jtbenavente@gmail.com"),
        "GNU General Public License v3.0",
        "https://raw.githubusercontent.com/torresj/api-sensor-server/master/LICENSE",
        Collections.emptyList());
  }
}
