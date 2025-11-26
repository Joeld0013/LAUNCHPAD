package com.launchpad.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "comments")
public class Comment {

    @Id
    private String id;

    // Post reference
    private String postId;

    // User Information - IMPORTANT FOR DISPLAY
    private String userId;
    private String userType;        // "STARTUP" or "INVESTOR"
    private String userName;        // User's name from database
    private String userEmail;       // User's email from database
    private String userProfilePic;  // User's profile picture URL

    // Comment content
    private String content;

    // Timestamp
    private LocalDateTime createdAt;

    public Comment() {
        this.createdAt = LocalDateTime.now();
        this.userName = "";
        this.userEmail = "";
        this.userProfilePic = "";
        this.userType = "";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}