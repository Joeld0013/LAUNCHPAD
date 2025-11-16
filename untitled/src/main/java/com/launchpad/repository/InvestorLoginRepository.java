package com.launchpad.repository;

import com.launchpad.model.InvestorLogin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestorLoginRepository extends MongoRepository<InvestorLogin, String> {

    Optional<InvestorLogin> findByEmail(String email);

    boolean existsByEmail(String email);
}