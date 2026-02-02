package com.zero9platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Zero9PlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(Zero9PlatformApplication.class, args);
    }

}
