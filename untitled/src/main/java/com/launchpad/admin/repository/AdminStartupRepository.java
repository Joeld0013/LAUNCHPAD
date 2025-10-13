package com.launchpad.admin.repository;

import com.launchpad.registration.model.Startup;  // Use registration model
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminStartupRepository extends MongoRepository<Startup, String> {

    List<Startup> findByRegistrationStatus(String status);

    List<Startup> findByIndustry(String industry);

    List<Startup> findByStage(String stage);

    List<Startup> findByCountry(String country);

    Optional<Startup> findByEmail(String email);

    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    List<Startup> searchByName(String searchTerm);

    @Query("{ $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } }, { 'industry': { $regex: ?0, $options: 'i' } } ] }")
    List<Startup> searchStartups(String searchTerm);

    long countByRegistrationStatus(String status);
}