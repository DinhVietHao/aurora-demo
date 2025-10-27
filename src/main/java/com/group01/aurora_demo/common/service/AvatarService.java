package com.group01.aurora_demo.common.service;

import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class AvatarService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXTENSIONS = { ".jpg", ".jpeg", ".png", ".webp", ".gif" };

    /**
     * Upload avatar và trả về tên file đã lưu
     * 
     * @param filePart   Part từ multipart/form-data
     * @param uploadDir  Thư mục lưu file (absolute path)
     * @param avatarType "customer" hoặc "shop"
     * @return Tên file đã lưu (không bao gồm path)
     * @throws IOException, IllegalArgumentException
     */
    public static String uploadAvatar(Part filePart, String uploadDir, String avatarType) throws IOException {
        // 1. Validate file part
        if (filePart == null || filePart.getSize() == 0) {
            throw new IllegalArgumentException("File không được để trống");
        }

        // 2. Validate file size
        if (filePart.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Kích thước file vượt quá 5MB");
        }

        // 3. Validate content type
        String contentType = filePart.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File phải là hình ảnh");
        }

        // 4. Validate file extension
        String originalFilename = getOriginalFilename(filePart);
        String extension = getFileExtension(originalFilename);

        if (!isAllowedExtension(extension)) {
            throw new IllegalArgumentException("Chỉ chấp nhận file: jpg, jpeg, png, webp, gif");
        }

        // 5. Tạo tên file unique
        String newFilename = generateUniqueFilename(avatarType, extension);

        // 6. Đảm bảo thư mục tồn tại
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        // 7. Lưu file
        Path filePath = Paths.get(uploadDir, newFilename);
        try {
            filePart.write(filePath.toString());
        } catch (IOException e) {
            throw new IOException("Không thể lưu file: " + e.getMessage());
        }

        return newFilename;
    }

    /**
     * Xóa avatar cũ nếu tồn tại
     */
    public static boolean deleteOldAvatar(String uploadDir, String oldFilename) {
        if (oldFilename == null || oldFilename.isEmpty()) {
            return true;
        }

        try {
            Path filePath = Paths.get(uploadDir, oldFilename);
            Files.deleteIfExists(filePath);
            return true;
        } catch (IOException e) {
            System.err.println("[WARN] Cannot delete old avatar: " + oldFilename);
            return false;
        }
    }

    /**
     * Lấy tên file gốc từ Part
     */
    private static String getOriginalFilename(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String content : contentDisposition.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return "unknown";
    }

    /**
     * Lấy extension từ filename
     */
    private static String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex).toLowerCase();
    }

    /**
     * Kiểm tra extension có hợp lệ không
     */
    private static boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tạo tên file unique
     * Format: {type}_{timestamp}_{uuid}.{ext}
     * VD: customer_1737363600000_abc123.jpg
     */
    private static String generateUniqueFilename(String avatarType, String extension) {
        long timestamp = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("%s_%d_%s%s", avatarType, timestamp, uuid, extension);
    }
}