package com.guitar.controller;

import com.guitar.common.Result;
import com.guitar.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Tag(name = "文件上传", description = "视频和图片上传")
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final FileService fileService;

    @Operation(summary = "上传视频")
    @PostMapping("/video")
    public Result<Map<String, Object>> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", required = false, defaultValue = "videos") String type) {
        Map<String, Object> result = fileService.uploadVideo(file, type);
        return Result.success("视频上传成功", result);
    }

    @Operation(summary = "上传图片")
    @PostMapping("/image")
    public Result<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", required = false, defaultValue = "images") String type) {
        Map<String, Object> result = fileService.uploadImage(file, type);
        return Result.success("图片上传成功", result);
    }
}
