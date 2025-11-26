    package com.launchpad.services;



    import com.launchpad.dto.CreatePostRequest;

    import com.launchpad.dto.PostResponse;

    import com.launchpad.model.Investor;

    import com.launchpad.model.Post;

    import com.launchpad.model.Startup;

    import com.launchpad.repository.CommentRepository;

    import com.launchpad.repository.InvestorRepository;

    import com.launchpad.repository.PostRepository;

    import com.launchpad.repository.StartupProfileRepository;

    import org.slf4j.Logger;

    import org.slf4j.LoggerFactory;

    import org.springframework.beans.factory.annotation.Autowired;

    import org.springframework.stereotype.Service;

    import org.springframework.transaction.annotation.Transactional;



    import java.util.List;

    import java.util.Optional;

    import java.util.stream.Collectors;



    @Service

    public class PostService {



        private static final Logger logger = LoggerFactory.getLogger(PostService.class);



        @Autowired

        private PostRepository postRepository;



        @Autowired

        private StartupProfileRepository startupRepository;



        @Autowired

        private InvestorRepository investorRepository;



        @Autowired

        private CommentRepository commentRepository;



        /**

         * Creates a new post and populates user details from the database

         * FIXED: Now properly detects user type by checking database

         */

        @Transactional

        public PostResponse createPost(CreatePostRequest request, String userId, String userType) {

            logger.info("═══════════════════════════════════");

            logger.info("Creating Post");

            logger.info("═══════════════════════════════════");

            logger.info("User ID from token: {}", userId);

            logger.info("User Type from token: {}", userType);



            Post post = new Post();

            post.setUserId(userId);

            post.setContent(request.getContent());

            post.setMediaUrls(request.getMediaUrls() != null ? request.getMediaUrls() : List.of());

            post.setLinkUrl(request.getLinkUrl());

            post.setTags(request.getTags() != null ? request.getTags() : List.of());



    // CRITICAL FIX: Try to find user in database to determine actual type

            boolean userFound = false;



    // First, try to find in STARTUP collection

            logger.info("→ Searching in STARTUPS collection...");

            Optional<Startup> startupOpt = startupRepository.findById(userId);

            if (startupOpt.isPresent()) {

                Startup startup = startupOpt.get();



    // Set all user details

                post.setUserType("STARTUP");

                post.setUserName(startup.getName() != null && !startup.getName().trim().isEmpty()

                        ? startup.getName()

                        : "Unknown Startup");

                post.setUserEmail(startup.getEmail() != null ? startup.getEmail() : "");

                post.setUserProfilePic(startup.getProfilePicture() != null ? startup.getProfilePicture() : "");



                userFound = true;

                logger.info("✓✓✓ STARTUP FOUND ✓✓✓");

                logger.info(" → ID: {}", startup.getId());

                logger.info(" → Name: {}", post.getUserName());

                logger.info(" → Email: {}", post.getUserEmail());

                logger.info(" → ProfilePic: {}", post.getUserProfilePic() != null && !post.getUserProfilePic().isEmpty() ? "YES (" + post.getUserProfilePic().substring(0, 50) + "...)" : "NO");

            }



    // If not found in startups, try INVESTOR collection

            if (!userFound) {

                logger.info("→ Not found in startups, searching in INVESTORS collection...");

                Optional<Investor> investorOpt = investorRepository.findById(userId);

                if (investorOpt.isPresent()) {

                    Investor investor = investorOpt.get();



    // Set all user details

                    post.setUserType("INVESTOR");

                    post.setUserName(investor.getName() != null && !investor.getName().trim().isEmpty()

                            ? investor.getName()

                            : "Unknown Investor");

                    post.setUserEmail(investor.getEmail() != null ? investor.getEmail() : "");

                    post.setUserProfilePic(investor.getProfilePicture() != null ? investor.getProfilePicture() : "");



                    userFound = true;

                    logger.info("✓✓✓ INVESTOR FOUND ✓✓✓");

                    logger.info(" → ID: {}", investor.getId());

                    logger.info(" → Name: {}", post.getUserName());

                    logger.info(" → Email: {}", post.getUserEmail());

                    logger.info(" → ProfilePic: {}", post.getUserProfilePic() != null && !post.getUserProfilePic().isEmpty() ? "YES (" + post.getUserProfilePic().substring(0, 50) + "...)" : "NO");

                }

            }



    // If still not found, set defaults

            if (!userFound) {

                logger.error("✗✗✗ USER NOT FOUND IN ANY COLLECTION ✗✗✗");

                logger.error(" → Searched ID: {}", userId);

                logger.error(" → Token userType was: {}", userType);

                logger.error(" → Setting defaults...");



                post.setUserType("UNKNOWN");

                post.setUserName("Unknown User");

                post.setUserEmail(userType); // Use the token value as fallback

                post.setUserProfilePic("");

            }



    // Save the post

            Post savedPost = postRepository.save(post);



            logger.info("═══════════════════════════════════");

            logger.info("✓ POST SAVED TO DATABASE");

            logger.info("═══════════════════════════════════");

            logger.info(" → Post ID: {}", savedPost.getId());

            logger.info(" → User ID: {}", savedPost.getUserId());

            logger.info(" → User Type: {}", savedPost.getUserType());

            logger.info(" → User Name: {}", savedPost.getUserName());

            logger.info(" → User Email: {}", savedPost.getUserEmail());

            logger.info(" → Profile Pic: {}", savedPost.getUserProfilePic() != null && !savedPost.getUserProfilePic().isEmpty() ? "YES" : "NO");

            logger.info(" → Content: {}", savedPost.getContent());

            logger.info("═══════════════════════════════════");



            return PostResponse.fromPost(savedPost, userId);

        }



        /**

         * Gets posts by filter (all, startup, investor)

         */

        public List<PostResponse> getPostsByFilter(String filter, String currentUserId) {

            logger.info("═══ Getting Posts ═══");

            logger.info("Filter: {}", filter);

            logger.info("Current User: {}", currentUserId);



            List<Post> posts;

            String normalizedFilter = filter.trim().toLowerCase();



            switch (normalizedFilter) {

                case "startup":

                    posts = postRepository.findByUserTypeOrderByCreatedAtDesc("STARTUP");

                    logger.info("✓ Fetched {} STARTUP posts", posts.size());

                    break;

                case "investor":

                    posts = postRepository.findByUserTypeOrderByCreatedAtDesc("INVESTOR");

                    logger.info("✓ Fetched {} INVESTOR posts", posts.size());

                    break;

                default:

                    posts = postRepository.findAllByOrderByCreatedAtDesc();

                    logger.info("✓ Fetched {} ALL posts", posts.size());

                    break;

            }



    // Update comment counts for each post

            posts.forEach(post -> {

                long commentCount = commentRepository.countByPostId(post.getId());

                post.setCommentsCount((int) commentCount);



                logger.info("Post: {} | Type: {} | Name: {} | Pic: {}",

                        post.getId(),

                        post.getUserType(),

                        post.getUserName(),

                        post.getUserProfilePic() != null && !post.getUserProfilePic().isEmpty() ? "YES" : "NO");

            });



            return posts.stream()

                    .map(post -> PostResponse.fromPost(post, currentUserId))

                    .collect(Collectors.toList());

        }



        /**

         * Deletes a post (only if user is the owner)

         */

        @Transactional

        public boolean deletePost(String postId, String userId) {

            logger.info("═══ Deleting Post ═══");

            logger.info("Post ID: {}", postId);

            logger.info("User ID: {}", userId);



            Optional<Post> postOpt = postRepository.findById(postId);

            if (postOpt.isPresent()) {

                Post post = postOpt.get();

                logger.info("Post Owner: {}", post.getUserId());



                if (post.getUserId().equals(userId)) {

                    postRepository.deleteById(postId);

                    logger.info("✓ Post deleted successfully");

                    return true;

                } else {

                    logger.warn("✗ User is NOT the owner");

                    return false;

                }

            }



            logger.warn("✗ Post not found");

            return false;

        }



        /**

         * Likes a post

         */

        @Transactional

        public PostResponse likePost(String postId, String userId) {

            logger.info("═══ Liking Post ═══");

            logger.info("Post ID: {}", postId);

            logger.info("User ID: {}", userId);



            Optional<Post> postOpt = postRepository.findById(postId);

            if (postOpt.isPresent()) {

                Post post = postOpt.get();



                if (post.getLikedByUsers().add(userId)) {

                    post.setLikesCount(post.getLikedByUsers().size());

                    Post savedPost = postRepository.save(post);

                    logger.info("✓ Post liked. Total likes: {}", savedPost.getLikesCount());

                    return PostResponse.fromPost(savedPost, userId);

                } else {

                    logger.info("User already liked this post");

                    return PostResponse.fromPost(post, userId);

                }

            }



            logger.warn("✗ Post not found");

            return null;

        }



        /**

         * Unlikes a post

         */

        @Transactional

        public PostResponse unlikePost(String postId, String userId) {

            logger.info("═══ Unliking Post ═══");

            logger.info("Post ID: {}", postId);

            logger.info("User ID: {}", userId);



            Optional<Post> postOpt = postRepository.findById(postId);

            if (postOpt.isPresent()) {

                Post post = postOpt.get();



                if (post.getLikedByUsers().remove(userId)) {

                    post.setLikesCount(post.getLikedByUsers().size());

                    Post savedPost = postRepository.save(post);

                    logger.info("✓ Post unliked. Total likes: {}", savedPost.getLikesCount());

                    return PostResponse.fromPost(savedPost, userId);

                } else {

                    logger.info("User hasn't liked this post");

                    return PostResponse.fromPost(post, userId);

                }

            }



            logger.warn("✗ Post not found");

            return null;

        }

    }