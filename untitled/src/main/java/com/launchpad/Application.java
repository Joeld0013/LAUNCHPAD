package com.launchpad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);


System.out.println("front rnd runs in : http://localhost:8081/index.html");
System.out.println("front rnd runs in : http://localhost:8081/adminlogin.html");
    }
}