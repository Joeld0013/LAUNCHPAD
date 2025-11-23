package com.launchpad.admin.repository;

import com.launchpad.registration.model.StartupReg;  // Use registration model
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminStartupRepository extends MongoRepository<StartupReg, String> {

    List<StartupReg> findByRegistrationStatus(String status);

    List<StartupReg> findByIndustry(String industry);

    List<StartupReg> findByStage(String stage);

    List<StartupReg> findByCountry(String country);

    Optional<StartupReg> findByEmail(String email);

    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    List<StartupReg> searchByName(String searchTerm);

    @Query("{ $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } }, { 'industry': { $regex: ?0, $options: 'i' } } ] }")
    List<StartupReg> searchStartups(String searchTerm);

    long countByRegistrationStatus(String status);
}