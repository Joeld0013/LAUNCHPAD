package com.launchpad.registration.repository;

import com.launchpad.registration.model.StartupReg;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface StartupRepository extends MongoRepository<StartupReg, String> {
    Optional<StartupReg> findByEmail(String email);
    List<StartupReg> findByRegistrationStatus(String status);
    long countByRegistrationStatus(String status);
}
