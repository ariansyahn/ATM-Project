package com.atm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class AtmApplication {
    public static void main(String[] args) {
        System.out.println("Server is Starting on Port 8084");
        SpringApplication.run(AtmApplication.class, args);
    }
}
