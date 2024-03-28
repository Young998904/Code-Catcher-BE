package com.lion.codecatcherbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CodeCatcherBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeCatcherBeApplication.class, args);
    }

}
