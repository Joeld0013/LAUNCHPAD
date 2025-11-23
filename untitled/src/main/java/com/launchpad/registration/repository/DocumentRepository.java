package com.launchpad.registration.repository;

import com.launchpad.registration.model.DocumentFile;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface DocumentRepository extends MongoRepository<DocumentFile, String> {
    // ADD THIS METHOD
    List<DocumentFile> findByStartupId(String startupId);
}