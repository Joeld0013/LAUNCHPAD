package com.launchpad.controller;



import com.launchpad.dto.CreatePostRequest;

import com.launchpad.dto.PostResponse;

import com.launchpad.model.Comment;

import com.launchpad.model.Investor;

import com.launchpad.model.Post;

import com.launchpad.model.Startup;

import com.launchpad.repository.CommentRepository;

import com.launchpad.repository.InvestorRepository;

import com.launchpad.repository.PostRepository;

import com.launchpad.repository.StartupProfileRepository;

import com.launchpad.services.PostService;

import com.launchpad.shared.utils.JwtUtil;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;



import java.util.*;



@RestController

@RequestMapping("/api/posts")

@CrossOrigin(origins = "*", allowedHeaders = "*")

public class PostController {



    private static final Logger logger = LoggerFactory.getLogger(PostController.class);



    @Autowired

    private PostService postService;



    @Autowired

    private JwtUtil jwtUtil;



    @Autowired

    private CommentRepository commentRepository;



    @Autowired

    private PostRepository postRepository;



    @Autowired

    private StartupProfileRepository startupRepository;



    @Autowired

    private InvestorRepository investorRepository;



    @PostMapping

    public ResponseEntity<?> createPost(@RequestBody CreatePostRequest request,

                                        @RequestHeader("Authorization") String authHeader) {

        try {

            logger.info("═══════════════════════════════════");

            logger.info("📝 CREATE POST REQUEST RECEIVED");

            logger.info("═══════════════════════════════════");



            String token = authHeader.replace("Bearer ", "");

            String userId = jwtUtil.extractUserId(token);

            String userType = jwtUtil.extractUserType(token);



            logger.info("Token Info:");

            logger.info(" → User ID: {}", userId);

            logger.info(" → User Type: {}", userType);

            logger.info(" → Content: {}", request.getContent());



            if (userId == null || userType == null) {

                logger.error("❌ Invalid token - missing userId or userType");

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)

                        .body(Map.of("message", "Invalid token"));

            }



            PostResponse post = postService.createPost(request, userId, userType);



            logger.info("✓ Post created successfully");

            logger.info(" → Post ID: {}", post.getId());

            logger.info(" → User Name in Post: {}", post.getUserName());

            logger.info("═══════════════════════════════════");



            return ResponseEntity.ok(post);

        } catch (Exception e) {

            logger.error("❌ Error creating post: ", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)

                    .body(Map.of("message", "Failed to create post: " + e.getMessage()));

        }

    }



    @GetMapping

    public ResponseEntity<?> getPosts(@RequestParam(required = false, defaultValue = "all") String filter,

                                      @RequestHeader("Authorization") String authHeader) {

        try {

            String token = authHeader.replace("Bearer ", "");

            String userId = jwtUtil.extractUserId(token);



            if (userId == null) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)

                        .body(Map.of("message", "Invalid token"));

            }



            List<PostResponse> posts = postService.getPostsByFilter(filter, userId);

            return ResponseEntity.ok(posts);

        } catch (Exception e) {

            logger.error("❌ Error fetching posts: ", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)

                    .body(Map.of("message", "Failed to fetch posts"));

        }

    }



    @DeleteMapping("/{postId}")

    public ResponseEntity<?> deletePost(@PathVariable String postId,

                                        @RequestHeader("Authorization") String authHeader) {

        try {

            String token = authHeader.replace("Bearer ", "");

            String userId = jwtUtil.extractUserId(token);



            if (userId == null) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)

                        .body(Map.of("message", "Invalid token"));

            }



            boolean deleted = postService.deletePost(postId, userId);



            if (deleted) {

                return ResponseEntity.ok(Map.of("message", "Post deleted", "success", true));

            } else {

                return ResponseEntity.status(HttpStatus.FORBIDDEN)

                        .body(Map.of("message", "Not authorized", "success", false));

            }

        } catch (Exception e) {

            logger.error("❌ Error deleting post: ", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)

                    .body(Map.of("message", "Failed to delete post", "success", false));

        }

    }



    @PostMapping("/{postId}/like")

    public ResponseEntity<?> likePost(@PathVariable String postId,

                                      @RequestHeader("Authorization") String authHeader) {

        try {

            String token = authHeader.replace("Bearer ", "");

            String userId = jwtUtil.extractUserId(token);



            if (userId == null) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)

                        .body(Map.of("message", "Invalid token"));

            }



            PostResponse post = postService.likePost(postId, userId);

            if (post != null) {

                return ResponseEntity.ok(post);

            } else {

                return ResponseEntity.status(HttpStatus.NOT_FOUND)

                        .body(Map.of("message", "Post not found"));

            }

        } catch (Exception e) {

            logger.error("❌ Error liking post: ", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)

                    .body(Map.of("message", "Failed to like post"));

        }

    }



    @PostMapping("/{postId}/unlike")

    public ResponseEntity<?> unlikePost(@PathVariable String postId,

                                        @RequestHeader("Authorization") String authHeader) {

        try {

            String token = authHeader.replace("Bearer ", "");

            String userId = jwtUtil.extractUserId(token);



            if (userId == null) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)

                        .body(Map.of("message", "Invalid token"));

            }



            PostResponse post = postService.unlikePost(postId, userId);

            if (post != null) {

                return ResponseEntity.ok(post);

            } else {

                return ResponseEntity.status(HttpStatus.NOT_FOUND)

                        .body(Map.of("message", "Post not found"));

            }

        } catch (Exception e) {

            logger.error("❌ Error unliking post: ", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)

                    .body(Map.of("message", "Failed to unlike post"));

        }

    }



    @PostMapping("/{postId}/comment")

    public ResponseEntity<?> addComment(@PathVariable String postId,

                                        @RequestBody Map<String, String> body,

                                        @RequestHeader("Authorization") String authHeader) {

        try {

            logger.info("═══ Adding Comment ═══");



            String token = authHeader.replace("Bearer ", "");

            String userId = jwtUtil.extractUserId(token);

            String userType = jwtUtil.extractUserType(token);



            logger.info(" → User ID: {}", userId);

            logger.info(" → User Type: {}", userType);



            if (userId == null || userType == null) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)

                        .body(Map.of("message", "Invalid token"));

            }



            String content = body.get("content");

            if (content == null || content.trim().isEmpty()) {

                return ResponseEntity.badRequest()

                        .body(Map.of("message", "Comment content required"));

            }



            Comment comment = new Comment();

            comment.setPostId(postId);

            comment.setUserId(userId);

            comment.setUserType(userType);

            comment.setContent(content);



// CRITICAL: Get user details from database

// First try STARTUP

            Optional<Startup> startupOpt = startupRepository.findById(userId);

            if (startupOpt.isPresent()) {

                Startup startup = startupOpt.get();

                comment.setUserType("STARTUP");

                comment.setUserName(startup.getName() != null ? startup.getName() : "Unknown Startup");

                comment.setUserEmail(startup.getEmail() != null ? startup.getEmail() : "");

                comment.setUserProfilePic(startup.getProfilePicture() != null ? startup.getProfilePicture() : "");



                logger.info("✓ Comment by STARTUP: {}", startup.getName());

            } else {

// Try INVESTOR

                Optional<Investor> investorOpt = investorRepository.findById(userId);

                if (investorOpt.isPresent()) {

                    Investor investor = investorOpt.get();

                    comment.setUserType("INVESTOR");

                    comment.setUserName(investor.getName() != null ? investor.getName() : "Unknown Investor");

                    comment.setUserEmail(investor.getEmail() != null ? investor.getEmail() : "");

                    comment.setUserProfilePic(investor.getProfilePicture() != null ? investor.getProfilePicture() : "");



                    logger.info("✓ Comment by INVESTOR: {}", investor.getName());

                } else {

                    logger.warn("⚠️ User not found in database: {}", userId);

                    comment.setUserName("Unknown User");

                    comment.setUserEmail("");

                    comment.setUserProfilePic("");

                }

            }



            Comment savedComment = commentRepository.save(comment);



// Update post comment count

            Optional<Post> postOpt = postRepository.findById(postId);

            if (postOpt.isPresent()) {

                Post post = postOpt.get();

                long count = commentRepository.countByPostId(postId);

                post.setCommentsCount((int) count);

                postRepository.save(post);

            }



            logger.info("✓ Comment added successfully");

            return ResponseEntity.ok(savedComment);

        } catch (Exception e) {

            logger.error("❌ Error adding comment: ", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)

                    .body(Map.of("message", "Failed to add comment"));

        }

    }



    @GetMapping("/{postId}/comments")

    public ResponseEntity<?> getComments(@PathVariable String postId,

                                         @RequestHeader("Authorization") String authHeader) {

        try {

            String token = authHeader.replace("Bearer ", "");

            String userId = jwtUtil.extractUserId(token);



            if (userId == null) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)

                        .body(Map.of("message", "Invalid token"));

            }



            List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtDesc(postId);

            return ResponseEntity.ok(comments);

        } catch (Exception e) {

            logger.error("❌ Error fetching comments: ", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)

                    .body(Map.of("message", "Failed to fetch comments"));

        }

    }

}

