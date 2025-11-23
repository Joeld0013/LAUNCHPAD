package com.launchpad.admin.dto;

import java.time.LocalDateTime;

public class InvestorDocumentDTO {
    private String _id;
    private String fileName;
    private String fileType;
    private long fileSize;
    private LocalDateTime uploadedAt;
    private String documentCategory;

    public InvestorDocumentDTO() {}

    public InvestorDocumentDTO(String _id, String fileName, String fileType, long fileSize, LocalDateTime uploadedAt, String documentCategory) {
        this._id = _id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadedAt = uploadedAt;
        this.documentCategory = documentCategory;
    }

    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public String getDocumentCategory() { return documentCategory; }
    public void setDocumentCategory(String documentCategory) { this.documentCategory = documentCategory; }
}