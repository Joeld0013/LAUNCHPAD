package com.launchpad.repository;

import com.launchpad.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {

    /**
     * Find all posts ordered by creation date (newest first)
     */
    List<Post> findAllByOrderByCreatedAtDesc();

    /**
     * Find posts by user ID ordered by creation date
     */
    List<Post> findByUserIdOrderByCreatedAtDesc(String userId);

    /**
     * Find posts by user type (STARTUP or INVESTOR)
     */
    List<Post> findByUserTypeOrderByCreatedAtDesc(String userType);

    /**
     * Find posts containing a specific tag
     */
    List<Post> findByTagsContainingOrderByCreatedAtDesc(String tag);

    /**
     * Find posts by user type and user ID
     */
    List<Post> findByUserTypeAndUserIdOrderByCreatedAtDesc(String userType, String userId);

    /**
     * Count posts by user ID
     */
    long countByUserId(String userId);

    /**
     * Count posts by user type
     */
    long countByUserType(String userType);

    /**
     * Find posts where content contains keyword
     */
    @Query("{ 'content': { $regex: ?0, $options: 'i' } }")
    List<Post> findByContentContaining(String keyword);

    /**
     * Delete all posts by user ID
     */
    void deleteByUserId(String userId);
}