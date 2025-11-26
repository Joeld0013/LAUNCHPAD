package com.launchpad.dto;

import java.util.ArrayList;
import java.util.List;

public class CreatePostRequest {
    private String content;
    private List<String> mediaUrls;
    private String linkUrl;
    private List<String> tags;

    // Constructors
    public CreatePostRequest() {
        this.mediaUrls = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    public CreatePostRequest(String content, List<String> mediaUrls, String linkUrl, List<String> tags) {
        this.content = content;
        this.mediaUrls = mediaUrls != null ? mediaUrls : new ArrayList<>();
        this.linkUrl = linkUrl;
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(List<String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "CreatePostRequest{" +
                "content='" + content + '\'' +
                ", mediaUrls=" + mediaUrls +
                ", linkUrl='" + linkUrl + '\'' +
                ", tags=" + tags +
                '}';
    }
}