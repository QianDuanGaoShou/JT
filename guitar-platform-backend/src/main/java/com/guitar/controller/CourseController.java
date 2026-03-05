package com.guitar.controller;

import com.guitar.common.PageResult;
import com.guitar.common.Result;
import com.guitar.common.SecurityUtil;
import com.guitar.dto.response.CourseDetailResponse;
import com.guitar.dto.response.CourseListItemResponse;
import com.guitar.dto.response.OrderResponse;
import com.guitar.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "课程管理", description = "课程浏览和购买")
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "获取课程列表")
    @GetMapping
    public Result<PageResult<CourseListItemResponse>> getCourseList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        Long currentUserId = tryGetCurrentUserId();
        PageResult<CourseListItemResponse> result = courseService.getCourseList(page, size, category, keyword, currentUserId);
        return Result.success(result);
    }

    @Operation(summary = "获取课程详情")
    @GetMapping("/{id}")
    public Result<CourseDetailResponse> getCourseDetail(@PathVariable Long id) {
        Long currentUserId = tryGetCurrentUserId();
        CourseDetailResponse detail = courseService.getCourseDetail(id, currentUserId);
        return Result.success(detail);
    }

    @Operation(summary = "购买课程")
    @PostMapping("/{id}/purchase")
    public Result<Map<String, Object>> purchaseCourse(@PathVariable Long id) {
        Long studentId = SecurityUtil.getCurrentUserId();
        Map<String, Object> result = courseService.purchaseCourse(id, studentId);
        return Result.success("购买成功", result);
    }

    @Operation(summary = "获取我的订单")
    @GetMapping("/orders")
    public Result<PageResult<OrderResponse>> getMyOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long studentId = SecurityUtil.getCurrentUserId();
        PageResult<OrderResponse> result = courseService.getMyOrders(studentId, page, size);
        return Result.success(result);
    }

    private Long tryGetCurrentUserId() {
        try {
            return SecurityUtil.getCurrentUserId();
        } catch (Exception e) {
            return null;
        }
    }
}
