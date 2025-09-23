package com.launchpad.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
public class
DbDiagnosticController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/api/startup/db-info")
    public ResponseEntity<?> dbInfo() {
        String dbName = mongoTemplate.getDb().getName();
        Set<String> collections = mongoTemplate.getCollectionNames();
        return ResponseEntity.ok(Map.of("db", dbName, "collections", collections));
    }
}
