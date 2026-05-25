package com.logistics.platform.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.logistics.platform.dto.ApiResponse;
import com.logistics.platform.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final UserRepository userRepository;

    @Value("${app.upload.dir:uploads/profile-photos}")
    private String uploadDir;

    @PostMapping("/document")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadDocument(
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("No file provided"));
        }

        String originalName = file.getOriginalFilename();
        String ext = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf('.'))
                : ".pdf";

        String filename = "doc_" + UUID.randomUUID() + ext;

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String fileUrl = "http://localhost:8080/api/v1/upload/files/" + filename;

        return ResponseEntity.ok(ApiResponse.success("Document uploaded successfully",
                Map.of("url", fileUrl, "filename", filename)));
    }

    /**
     * Upload a profile photo and update the user's profilePhotoUrl.
     * Returns the accessible URL for the uploaded image.
     */
    @PostMapping("/profile-photo")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadProfilePhoto(
            @RequestParam("file") MultipartFile file) throws IOException {

        // Validate file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("No file provided"));
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Only image files are allowed"));
        }

        // Get file extension
        String originalName = file.getOriginalFilename();
        String ext = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf('.'))
                : ".jpg";

        // Generate unique filename
        String filename = UUID.randomUUID() + ext;

        // Ensure upload directory exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Build the URL that the frontend will use to access this image
        String fileUrl = "http://localhost:8080/api/v1/upload/files/" + filename;

        // Update user's profile photo URL
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        userRepository.findByEmail(auth.getName()).ifPresent(u -> {
            u.setProfilePhotoUrl(fileUrl);
            userRepository.save(u);
        });

        return ResponseEntity.ok(ApiResponse.success("Profile photo uploaded successfully",
                Map.of("url", fileUrl, "filename", filename)));
    }

    /**
     * Serve uploaded files — accessible without auth so avatars load properly.
     */
    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable("filename") String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // Detect media type from filename
            String contentType = "application/octet-stream";
            if (filename.toLowerCase().endsWith(".png")) contentType = "image/png";
            else if (filename.toLowerCase().endsWith(".gif")) contentType = "image/gif";
            else if (filename.toLowerCase().endsWith(".webp")) contentType = "image/webp";
            else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) contentType = "image/jpeg";
            else if (filename.toLowerCase().endsWith(".pdf")) contentType = "application/pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
