package com.guitar.service.impl;

import com.guitar.common.exception.BusinessException;
import com.guitar.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload.base-path:/uploads}")
    private String basePath;

    @Value("${server.port:8080}")
    private String serverPort;

    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
            "video/mp4", "video/avi", "video/mov", "video/wmv", "video/mkv"
    );

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    private static final long MAX_VIDEO_SIZE = 500L * 1024 * 1024; // 500MB
    private static final long MAX_IMAGE_SIZE = 10L * 1024 * 1024;  // 10MB

    @Override
    public Map<String, Object> uploadVideo(MultipartFile file, String type) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "文件不能为空");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_VIDEO_TYPES.contains(contentType)) {
            throw new BusinessException(400, "不支持的视频格式，支持: mp4, avi, mov, wmv, mkv");
        }

        if (file.getSize() > MAX_VIDEO_SIZE) {
            throw new BusinessException(400, "视频文件大小不能超过500MB");
        }

        String subDir = type != null ? type : "videos";
        String fileUrl = saveFile(file, subDir);

        Map<String, Object> result = new HashMap<>();
        result.put("url", fileUrl);
        result.put("duration", 0); // Would need ffprobe or similar to get actual duration
        result.put("size", file.getSize());
        result.put("originalName", file.getOriginalFilename());
        return result;
    }

    @Override
    public Map<String, Object> uploadImage(MultipartFile file, String type) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "文件不能为空");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new BusinessException(400, "不支持的图片格式，支持: jpg, jpeg, png, gif, webp");
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new BusinessException(400, "图片文件大小不能超过10MB");
        }

        String subDir = type != null ? type : "images";
        String fileUrl = saveFile(file, subDir);

        Map<String, Object> result = new HashMap<>();
        result.put("url", fileUrl);
        result.put("originalName", file.getOriginalFilename());
        return result;
    }

    private String saveFile(MultipartFile file, String subDir) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        String relativePath = subDir + File.separator + fileName;
        Path targetPath = Paths.get(basePath, subDir, fileName);

        try {
            Files.createDirectories(targetPath.getParent());
            Files.copy(file.getInputStream(), targetPath);
        } catch (IOException e) {
            log.error("Failed to save file: {}", e.getMessage(), e);
            throw new BusinessException(500, "文件上传失败");
        }

        return "/files/" + subDir + "/" + fileName;
    }
}
