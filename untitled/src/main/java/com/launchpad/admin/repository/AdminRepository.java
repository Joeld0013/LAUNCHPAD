package com.launchpad.admin.repository;

import com.launchpad.admin.model.Adminlogin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends MongoRepository<Adminlogin, String> {

    Optional<Adminlogin> findByEmail(String email);

    Optional<Adminlogin> findByEmailAndActive(String email, boolean active);

    boolean existsByEmail(String email);
}