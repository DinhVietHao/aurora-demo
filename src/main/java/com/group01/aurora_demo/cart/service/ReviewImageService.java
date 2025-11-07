package com.group01.aurora_demo.cart.service;

import jakarta.servlet.http.Part;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.UUID;
import java.io.File;

public class ReviewImageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final String[] ALLOWED_EXTENSIONS = { ".jpg", ".jpeg", ".png", ".webp", ".gif" };
    private static final String UPLOAD_SUBDIR = "reviews";

    public static String uploadReviewImage(Part filePart, String baseUploadDir) throws IOException {
        if (filePart == null || filePart.getSize() == 0) {
            throw new IllegalArgumentException("File không được để trống");
        }

        if (filePart.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Kích thước file vượt quá 5MB");
        }

        String contentType = filePart.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File phải là hình ảnh");
        }

        String originalFilename = getOriginalFilename(filePart);
        String extension = getFileExtension(originalFilename);

        if (!isAllowedExtension(extension)) {
            throw new IllegalArgumentException("Chỉ chấp nhận file: jpg, jpeg, png, webp, gif");
        }

        String newFilename = generateUniqueFilename(extension);

        String fullUploadDir = baseUploadDir + File.separator + UPLOAD_SUBDIR;
        File uploadDirFile = new File(fullUploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        Path filePath = Paths.get(fullUploadDir, newFilename);
        try {
            filePart.write(filePath.toString());
        } catch (IOException e) {
            throw new IOException("Không thể lưu file: " + e.getMessage());
        }

        return newFilename;
    }

    public static boolean deleteOldReviewImage(String baseUploadDir, String oldFilename) {
        if (oldFilename == null || oldFilename.isEmpty()) {
            return true;
        }

        try {
            Path filePath = Paths.get(baseUploadDir, UPLOAD_SUBDIR, oldFilename);
            Files.deleteIfExists(filePath);
            return true;
        } catch (IOException e) {
            System.err.println("[WARN] Cannot delete old review image: " + oldFilename);
            return false;
        }
    }

    private static String getOriginalFilename(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String content : contentDisposition.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return "unknown";
    }

    private static String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex).toLowerCase();
    }

    private static boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    private static String generateUniqueFilename(String extension) {
        long timestamp = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("review_%d_%s%s", timestamp, uuid, extension);
    }
}