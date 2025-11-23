package com.launchpad.admin.repository;

import com.launchpad.admin.model.InvestorDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InvestorDocumentRepository extends MongoRepository<InvestorDocument, String> {
    List<InvestorDocument> findByInvestorId(String investorId);
    void deleteByInvestorId(String investorId);
}