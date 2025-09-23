package com.launchpad.registration.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "documents")
public class DocumentFile {
    @Id
    private String id;

    private String startupId; // link back to startup
    private DocType docType;
    private String filePath;
    private DocumentStatus status = DocumentStatus.PENDING;
    private LocalDateTime uploadedAt = LocalDateTime.now();

    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStartupId() { return startupId; }
    public void setStartupId(String startupId) { this.startupId = startupId; }
    public DocType getDocType() { return docType; }
    public void setDocType(DocType docType) { this.docType = docType; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public DocumentStatus getStatus() { return status; }
    public void setStatus(DocumentStatus status) { this.status = status; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
