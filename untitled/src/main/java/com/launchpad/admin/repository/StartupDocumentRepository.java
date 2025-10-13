package com.launchpad.admin.repository;

import com.launchpad.registration.model.DocumentFile;  // Use registration model
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StartupDocumentRepository extends MongoRepository<DocumentFile, String> {

    List<DocumentFile> findByStartupId(String startupId);

    void deleteByStartupId(String startupId);
}