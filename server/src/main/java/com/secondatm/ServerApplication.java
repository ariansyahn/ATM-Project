package com.secondatm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {
        System.out.println("Server is Starting on Port 8082");
        SpringApplication.run(ServerApplication.class, args);
    }

}
