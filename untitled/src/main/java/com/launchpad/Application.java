// src/main/java/com/launchpad/LaunchpadApplication.java
package com.launchpad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);


//System.out.println("front rnd runs in : http://localhost:8080/index.html");
        System.out.println("front rnd runs in : http://localhost:8080/adminlogin.html");
    }
}