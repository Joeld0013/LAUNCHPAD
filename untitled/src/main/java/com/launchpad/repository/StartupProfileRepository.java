package com.launchpad.repository;

import com.launchpad.model.Startup;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StartupProfileRepository extends MongoRepository<Startup, String> {
    Optional<Startup> findByEmail(String email);
}