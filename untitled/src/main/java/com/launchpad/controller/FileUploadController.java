package com.launchpad.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    // Configuration
    private final Path fileStorageLocation;
    private final Path imagesLocation;
    private final Path videosLocation;
    private final Path othersLocation;

    // Constants
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // Increased to 50MB for videos
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS =
            new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "svg"));
    private static final Set<String> ALLOWED_VIDEO_EXTENSIONS =
            new HashSet<>(Arrays.asList("mp4", "webm", "ogg", "mov", "avi", "mkv"));

    // Constructor to initialize directories
    public FileUploadController(@Value("${file.upload-dir:uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.imagesLocation = this.fileStorageLocation.resolve("images");
        this.videosLocation = this.fileStorageLocation.resolve("videos");
        this.othersLocation = this.fileStorageLocation.resolve("others");

        try {
            Files.createDirectories(this.fileStorageLocation);
            Files.createDirectories(this.imagesLocation);
            Files.createDirectories(this.videosLocation);
            Files.createDirectories(this.othersLocation);
            logger.info("File upload directories created at: {}", this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create upload directories!", ex);
        }
    }

    // ===================================================================================
    // UPLOAD ENDPOINTS
    // ===================================================================================

    /**
     * Generic Multi-File Upload
     * Auto-sorts files into 'images', 'videos', or 'others' based on extension.
     * POST /api/files/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        logger.info("Received generic upload request for {} file(s)", files.length);

        List<String> uploadedUrls = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                if (file.isEmpty()) {
                    errors.add("File is empty: " + file.getOriginalFilename());
                    continue;
                }
                if (file.getSize() > MAX_FILE_SIZE) {
                    errors.add("File too large: " + file.getOriginalFilename());
                    continue;
                }

                String type = determineFileType(file);
                Path targetFolder = getTargetFolder(type);
                String uniqueFilename = generateUniqueFilename(file.getOriginalFilename());

                // Save
                Files.copy(file.getInputStream(), targetFolder.resolve(uniqueFilename), StandardCopyOption.REPLACE_EXISTING);

                // Generate URL: /api/files/{type}/{filename}
                String fileUrl = "/api/files/" + type + "/" + uniqueFilename;
                uploadedUrls.add(fileUrl);

            } catch (IOException ex) {
                logger.error("Failed to upload file: {}", file.getOriginalFilename(), ex);
                errors.add("Failed to upload: " + file.getOriginalFilename());
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("urls", uploadedUrls);
        response.put("uploadedCount", uploadedUrls.size());
        response.put("errors", errors);

        return ResponseEntity.ok(response);
    }

    /**
     * Specific Image Upload (Strict Validation)
     * POST /api/files/upload/image
     */
    @PostMapping("/upload/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            validateFile(file, ALLOWED_IMAGE_EXTENSIONS, "image");

            String uniqueFilename = generateUniqueFilename(file.getOriginalFilename());
            Path targetPath = this.imagesLocation.resolve(uniqueFilename);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/api/files/images/" + uniqueFilename;

            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            response.put("filename", uniqueFilename);
            response.put("originalName", file.getOriginalFilename());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload image"));
        }
    }

    /**
     * Specific Video Upload (Strict Validation)
     * POST /api/files/upload/video
     */
    @PostMapping("/upload/video")
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            validateFile(file, ALLOWED_VIDEO_EXTENSIONS, "video");

            String uniqueFilename = generateUniqueFilename(file.getOriginalFilename());
            Path targetPath = this.videosLocation.resolve(uniqueFilename);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/api/files/videos/" + uniqueFilename;

            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            response.put("filename", uniqueFilename);
            response.put("originalName", file.getOriginalFilename());
            response.put("size", String.valueOf(file.getSize()));

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload video"));
        }
    }

    // ===================================================================================
    // SERVE & DELETE ENDPOINTS
    // ===================================================================================

    /**
     * Serve File
     * GET /api/files/{type}/{filename}
     * Types: images, videos, others
     */
    @GetMapping("/{type}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String type, @PathVariable String filename) {
        try {
            Path folder = getTargetFolder(type);
            Path filePath = folder.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException ex) {
            return ResponseEntity.badRequest().build();
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete File
     * DELETE /api/files/{type}/{filename}
     */
    @DeleteMapping("/{type}/{filename:.+}")
    public ResponseEntity<?> deleteFile(@PathVariable String type, @PathVariable String filename) {
        try {
            Path folder = getTargetFolder(type);
            Path filePath = folder.resolve(filename).normalize();

            boolean deleted = Files.deleteIfExists(filePath);

            if (deleted) {
                logger.info("File deleted: {}/{}", type, filename);
                return ResponseEntity.ok(Map.of("message", "File deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "File not found"));
            }
        } catch (Exception ex) {
            logger.error("Failed to delete file", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete file"));
        }
    }

    // ===================================================================================
    // HELPERS
    // ===================================================================================

    private void validateFile(MultipartFile file, Set<String> allowedExtensions, String typeName) {
        if (file.isEmpty()) throw new IllegalArgumentException("File is empty");
        if (file.getSize() > MAX_FILE_SIZE) throw new IllegalArgumentException("File too large");

        String extension = getFileExtension(file.getOriginalFilename());
        if (!allowedExtensions.contains(extension)) {
            throw new IllegalArgumentException("Invalid file type. Expected " + typeName);
        }
    }

    private String determineFileType(MultipartFile file) {
        String extension = getFileExtension(file.getOriginalFilename());
        if (ALLOWED_IMAGE_EXTENSIONS.contains(extension)) return "images";
        if (ALLOWED_VIDEO_EXTENSIONS.contains(extension)) return "videos";
        return "others";
    }

    private Path getTargetFolder(String type) {
        switch (type.toLowerCase()) {
            case "images": return this.imagesLocation;
            case "videos": return this.videosLocation;
            case "others": return this.othersLocation;
            default: throw new IllegalArgumentException("Invalid file type category: " + type);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private String generateUniqueFilename(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }
}