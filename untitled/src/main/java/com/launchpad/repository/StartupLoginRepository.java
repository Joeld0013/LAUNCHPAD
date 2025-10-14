package com.launchpad.repository;

import com.launchpad.model.StartupLogin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StartupLoginRepository extends MongoRepository<StartupLogin, String> {

    Optional<StartupLogin> findByEmail(String email);

    boolean existsByEmail(String email);
}