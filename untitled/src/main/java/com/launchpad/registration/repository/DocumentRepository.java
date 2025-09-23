package com.launchpad.registration.repository;

import com.launchpad.registration.model.DocumentFile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentRepository extends MongoRepository<DocumentFile, String> {
}
