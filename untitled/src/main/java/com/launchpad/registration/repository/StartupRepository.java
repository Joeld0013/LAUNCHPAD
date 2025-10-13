package com.launchpad.registration.repository;

import com.launchpad.registration.model.Startup;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface StartupRepository extends MongoRepository<Startup, String> {
    Optional<Startup> findByEmail(String email);
    List<Startup> findByRegistrationStatus(String status);
    long countByRegistrationStatus(String status);
}
