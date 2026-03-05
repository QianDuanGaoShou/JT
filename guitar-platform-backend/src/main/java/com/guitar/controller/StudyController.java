package com.guitar.controller;

import com.guitar.common.PageResult;
import com.guitar.common.Result;
import com.guitar.common.SecurityUtil;
import com.guitar.dto.request.UpdateProgressRequest;
import com.guitar.dto.response.StudyRecordResponse;
import com.guitar.service.StudyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "学习管理", description = "学习记录和进度")
@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @Operation(summary = "获取学习记录")
    @GetMapping("/records")
    public Result<PageResult<StudyRecordResponse>> getStudyRecords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long studentId = SecurityUtil.getCurrentUserId();
        PageResult<StudyRecordResponse> result = studyService.getStudyRecords(studentId, page, size);
        return Result.success(result);
    }

    @Operation(summary = "更新学习进度")
    @PutMapping("/progress")
    public Result<Void> updateProgress(@Valid @RequestBody UpdateProgressRequest request) {
        Long studentId = SecurityUtil.getCurrentUserId();
        studyService.updateProgress(
                studentId,
                request.getCourseId(),
                request.getVideoId(),
                request.getProgress(),
                request.getLastPosition()
        );
        return Result.success("进度更新成功", null);
    }

    @Operation(summary = "获取学习统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStudyStats() {
        Long studentId = SecurityUtil.getCurrentUserId();
        Map<String, Object> stats = studyService.getStudyStats(studentId);
        return Result.success(stats);
    }
}
