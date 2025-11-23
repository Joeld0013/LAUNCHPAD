package com.launchpad.registration.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "documents")
public class DocumentFile {

    @Id
    private String id;

    private String startupId;
    private String docType;  // Keep as String to handle both enum and string
    private String filePath;
    private String status;
    private LocalDateTime uploadedAt;

    public DocumentFile() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStartupId() { return startupId; }
    public void setStartupId(String startupId) { this.startupId = startupId; }

    public String getDocType() { return docType; }
    public void setDocType(String docType) { this.docType = docType; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    @Override
    public String toString() {
        return "DocumentFile{" +
                "id='" + id + '\'' +
                ", startupId='" + startupId + '\'' +
                ", docType='" + docType + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}