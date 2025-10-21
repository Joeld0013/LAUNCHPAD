package com.launchpad.registration.repository;

import com.launchpad.registration.model.InvestorReg;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface InvestorRepository extends MongoRepository<InvestorReg, String> {
    Optional<InvestorReg> findByEmail(String email);
    List<InvestorReg> findByRegistrationStatus(String status);
    long countByRegistrationStatus(String status);
    List<InvestorReg> findByInvestorType(String investorType);
}