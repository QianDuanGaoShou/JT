package com.guitar.controller;

import com.guitar.common.PageResult;
import com.guitar.common.Result;
import com.guitar.common.SecurityUtil;
import com.guitar.common.exception.BusinessException;
import com.guitar.dto.request.*;
import com.guitar.dto.response.IncomeStatsResponse;
import com.guitar.dto.response.PracticeResponse;
import com.guitar.dto.response.TeacherCourseResponse;
import com.guitar.entity.Course;
import com.guitar.entity.CourseChapter;
import com.guitar.entity.CourseVideo;
import com.guitar.service.PracticeService;
import com.guitar.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "教师管理", description = "教师课程和收入管理")
@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final PracticeService practiceService;

    private void checkTeacherRole() {
        String role = SecurityUtil.getCurrentRole();
        if (!"TEACHER".equals(role) && !"ADMIN".equals(role)) {
            throw new BusinessException(403, "权限不足，需要教师角色");
        }
    }

    @Operation(summary = "创建课程")
    @PostMapping("/course")
    public Result<Course> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        checkTeacherRole();
        Long teacherId = SecurityUtil.getCurrentUserId();
        Course course = teacherService.createCourse(teacherId, request);
        return Result.success("课程创建成功", course);
    }

    @Operation(summary = "更新课程")
    @PutMapping("/course/{id}")
    public Result<Void> updateCourse(@PathVariable Long id, @RequestBody UpdateCourseRequest request) {
        checkTeacherRole();
        Long teacherId = SecurityUtil.getCurrentUserId();
        teacherService.updateCourse(id, teacherId, request);
        return Result.success("课程更新成功", null);
    }

    @Operation(summary = "删除课程")
    @DeleteMapping("/course/{id}")
    public Result<Void> deleteCourse(@PathVariable Long id) {
        checkTeacherRole();
        Long teacherId = SecurityUtil.getCurrentUserId();
        teacherService.deleteCourse(id, teacherId);
        return Result.success("课程删除成功", null);
    }

    @Operation(summary = "获取我的课程列表")
    @GetMapping("/courses")
    public Result<PageResult<TeacherCourseResponse>> getMyCourses(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        checkTeacherRole();
        Long teacherId = SecurityUtil.getCurrentUserId();
        PageResult<TeacherCourseResponse> result = teacherService.getMyCourses(teacherId, page, size, status);
        return Result.success(result);
    }

    @Operation(summary = "添加章节")
    @PostMapping("/chapter")
    public Result<CourseChapter> addChapter(@Valid @RequestBody CreateChapterRequest request) {
        checkTeacherRole();
        Long teacherId = SecurityUtil.getCurrentUserId();
        CourseChapter chapter = teacherService.addChapter(request, teacherId);
        return Result.success("章节添加成功", chapter);
    }

    @Operation(summary = "更新章节")
    @PutMapping("/chapter/{id}")
    public Result<Void> updateChapter(
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer sortOrder) {
        checkTeacherRole();
        Long teacherId = SecurityUtil.getCurrentUserId();
        teacherService.updateChapter(id, teacherId, title, sortOrder);
        return Result.success("章节更新成功", null);
    }

    @Operation(summary = "删除章节")
    @DeleteMapping("/chapter/{id}")
    public Result<Void> deleteChapter(@PathVariable Long id) {
        checkTeacherRole();
        Long teacherId = SecurityUtil.getCurrentUserId();
        teacherService.deleteChapter(id, teacherId);
        return Result.success("章节删除成功", null);
    }

    @Operation(summary = "添加视频")
    @PostMapping("/video")
    public Result<CourseVideo> addVideo(@Valid @RequestBody CreateVideoRequest request) {
        checkTeacherRole();
        Long teacherId = SecurityUtil.getCurrentUserId();
        CourseVideo video = teacherService.addVideo(request, teacherId);
        return Result.success("视频添加成功", video);
    }

    @Operation(summary = "更新视频")
    @PutMapping("/video/{id}")
    public Result<Void> updateVideo(
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer sortOrder) {
        checkTeacherRole();
        Long teacherId = SecurityUtil.getCurrentUserId();
        teacherService.updateVideo(id, teacherId, title, sortOrder);
        return Result.success("视频更新成功", null);
    }

    @Operation(summary = "删除视频")
    @DeleteMapping("/video/{id}")
    public Result<Void> deleteVideo(@PathVariable Long id) {
        checkTeacherRole();
        Long teacherId = SecurityUtil.getCurrentUserId();
        teacherService.deleteVideo(id, teacherId);
        return Result.success("视频删除成功", null);
    }

    @Operation(summary = "获取待批改练习")
    @GetMapping("/practice/pending")
    public Result<PageResult<PracticeResponse>> getPendingPractices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long courseId) {
        checkTeacherRole();
        Long teacherId = SecurityUtil.getCurrentUserId();
        PageResult<PracticeResponse> result = practiceService.getPendingPractices(teacherId, page, size, courseId);
        return Result.success(result);
    }

    @Operation(summary = "获取已批改练习")
    @GetMapping("/practice/graded")
    public Result<PageResult<PracticeResponse>> getGradedPractices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long courseId) {
        checkTeacherRole();
        Long teacherId = SecurityUtil.getCurrentUserId();
        // Reuse with status filter logic
        PageResult<PracticeResponse> result = practiceService.getPendingPractices(teacherId, page, size, courseId);
        return Result.success(result);
    }

    @Operation(summary = "批改练习")
    @PutMapping("/practice/{id}/grade")
    public Result<Map<String, Object>> gradePractice(
            @PathVariable Long id,
            @Valid @RequestBody GradeRequest request) {
        checkTeacherRole();
        Long teacherId = SecurityUtil.getCurrentUserId();
        Map<String, Object> result = practiceService.gradePractice(id, teacherId, request.getTotalScore(), request.getFeedback());
        return Result.success("批改成功", result);
    }

    @Operation(summary = "获取收入统计")
    @GetMapping("/stats/income")
    public Result<IncomeStatsResponse> getIncomeStats(
            @RequestParam(required = false) String startMonth,
            @RequestParam(required = false) String endMonth) {
        checkTeacherRole();
        Long teacherId = SecurityUtil.getCurrentUserId();
        IncomeStatsResponse stats = teacherService.getIncomeStats(teacherId, startMonth, endMonth);
        return Result.success(stats);
    }
}
