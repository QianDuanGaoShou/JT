package com.guitar.controller;

import com.guitar.common.PageResult;
import com.guitar.common.Result;
import com.guitar.common.SecurityUtil;
import com.guitar.dto.response.PracticeResponse;
import com.guitar.service.FileService;
import com.guitar.service.PracticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Tag(name = "练习管理", description = "练习提交和查看")
@RestController
@RequestMapping("/api/practice")
@RequiredArgsConstructor
public class PracticeController {

    private final PracticeService practiceService;
    private final FileService fileService;

    @Operation(summary = "提交练习视频")
    @PostMapping("/submit")
    public Result<PracticeResponse> submitPractice(
            @RequestParam("file") MultipartFile file,
            @RequestParam("courseId") Long courseId) {
        Long studentId = SecurityUtil.getCurrentUserId();

        // Upload video first
        Map<String, Object> uploadResult = fileService.uploadVideo(file, "practice");
        String videoUrl = (String) uploadResult.get("url");

        PracticeResponse response = practiceService.submitPractice(studentId, courseId, videoUrl);
        return Result.success("提交成功", response);
    }

    @Operation(summary = "获取我的练习列表")
    @GetMapping("/list")
    public Result<PageResult<PracticeResponse>> getMyPractices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        Long studentId = SecurityUtil.getCurrentUserId();
        PageResult<PracticeResponse> result = practiceService.getMyPractices(studentId, page, size, status);
        return Result.success(result);
    }

    @Operation(summary = "获取练习报告")
    @GetMapping("/{id}/report")
    public Result<PracticeResponse> getPracticeReport(@PathVariable Long id) {
        Long studentId = SecurityUtil.getCurrentUserId();
        PracticeResponse response = practiceService.getPracticeReport(id, studentId);
        return Result.success(response);
    }
}
