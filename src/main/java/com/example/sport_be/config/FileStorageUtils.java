package com.example.sport_be.config;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Utility lưu file ảnh minh chứng cho đổi trả.
 * Lưu vào: uploads/hoan_tra/{id_doi_tra}/
 */
public class FileStorageUtils {

    private static final String BASE_DIR = "uploads/hoan_tra";

    /**
     * Lưu mảng file vào thư mục uploads/hoan_tra/{doiTraId}/
     * @param files mảng MultipartFile cần lưu
     * @param doiTraId ID đổi trả (dùng làm tên thư mục con)
     * @param baseUrl URL gốc của server (VD: http://localhost:8080)
     * @return mảng String đường dẫn public có thể truy cập
     */
    public static String[] saveFiles(MultipartFile[] files, Integer doiTraId, String baseUrl) {
        if (files == null || files.length == 0) {
            return new String[0];
        }

        // Tạo thư mục nếu chưa tồn tại
        Path dirPath = Paths.get(BASE_DIR, String.valueOf(doiTraId)).toAbsolutePath().normalize();
        try {
            Files.createDirectories(dirPath);
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục lưu ảnh: " + e.getMessage());
        }

        List<String> savedPaths = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            // Lấy extension gốc
            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }

            // Tạo tên file unique bằng UUID
            String newFileName = UUID.randomUUID().toString().substring(0, 12) + ext;
            Path filePath = dirPath.resolve(newFileName);

            try {
                file.transferTo(filePath.toFile());
                // Trả về URL public
                String publicUrl = baseUrl + "/uploads/hoan_tra/" + doiTraId + "/" + newFileName;
                savedPaths.add(publicUrl);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi lưu file: " + file.getOriginalFilename() + " - " + e.getMessage());
            }
        }

        return savedPaths.toArray(new String[0]);
    }
}
