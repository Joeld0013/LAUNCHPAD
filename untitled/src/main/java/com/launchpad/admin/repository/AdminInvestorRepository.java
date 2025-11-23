// InvestorRepository.java
package com.launchpad.admin.repository;

import com.launchpad.admin.model.InvestorAdmin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdminInvestorRepository extends MongoRepository<InvestorAdmin, String> {
    List<InvestorAdmin> findByRegistrationStatus(String status);
    long countByRegistrationStatus(String status);
}