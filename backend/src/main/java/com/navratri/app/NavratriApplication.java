package com.navratri.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class NavratriApplication {
    public static void main(String[] args) {
        SpringApplication.run(NavratriApplication.class, args);
    }
}
