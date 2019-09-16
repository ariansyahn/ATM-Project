package com.switching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SwitchingApplication {
    public static void main(String[] args) {
        System.out.println("Server is Starting on Port 8083");
        SpringApplication.run(SwitchingApplication.class, args);
    }
}
