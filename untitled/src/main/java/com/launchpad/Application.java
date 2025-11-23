package com.launchpad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);


System.out.println("front rnd runs in : http://localhost:8080/index.html");
System.out.println("front rnd runs in : http://localhost:8080/adminlogin.html");
        System.out.println("GET http://localhost:8080/api/admin/startups/verify/68d2619ad80bef501c93f12f");
    }
}