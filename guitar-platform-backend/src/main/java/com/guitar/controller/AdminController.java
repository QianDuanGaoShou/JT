package com.guitar.controller;

import com.guitar.common.PageResult;
import com.guitar.common.Result;
import com.guitar.common.SecurityUtil;
import com.guitar.common.exception.BusinessException;
import com.guitar.dto.request.AuditRequest;
import com.guitar.dto.request.RechargeRequest;
import com.guitar.dto.response.SettleReportResponse;
import com.guitar.dto.response.UserInfoResponse;
import com.guitar.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "管理员管理", description = "平台管理功能")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    private void checkAdminRole() {
        String role = SecurityUtil.getCurrentRole();
        if (!"ADMIN".equals(role)) {
            throw new BusinessException(403, "权限不足，需要管理员角色");
        }
    }

    @Operation(summary = "获取待审核教师列表")
    @GetMapping("/teachers/pending")
    public Result<PageResult<UserInfoResponse>> getPendingTeachers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        checkAdminRole();
        return Result.success(adminService.getPendingTeachers(page, size));
    }

    @Operation(summary = "审核教师")
    @PutMapping("/teacher/{id}/audit")
    public Result<Void> auditTeacher(
            @PathVariable Long id,
            @Valid @RequestBody AuditRequest request) {
        checkAdminRole();
        adminService.auditTeacher(id, request.getStatus(), request.getReason());
        return Result.success("审核操作成功", null);
    }

    @Operation(summary = "获取所有教师")
    @GetMapping("/teachers")
    public Result<PageResult<UserInfoResponse>> getAllTeachers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        checkAdminRole();
        return Result.success(adminService.getAllTeachers(page, size));
    }

    @Operation(summary = "切换用户状态")
    @PutMapping("/user/{id}/toggle-status")
    public Result<Void> toggleUserStatus(@PathVariable Long id) {
        checkAdminRole();
        adminService.toggleUserStatus(id);
        return Result.success("操作成功", null);
    }

    @Operation(summary = "获取所有用户")
    @GetMapping("/users")
    public Result<PageResult<UserInfoResponse>> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role) {
        checkAdminRole();
        return Result.success(adminService.getAllUsers(page, size, role));
    }

    @Operation(summary = "给用户充值")
    @PostMapping("/user/{id}/recharge")
    public Result<Map<String, Object>> rechargeBalance(
            @PathVariable Long id,
            @Valid @RequestBody RechargeRequest request) {
        checkAdminRole();
        Map<String, Object> result = adminService.rechargeBalance(id, request.getAmount(), request.getRemark());
        return Result.success("充值成功", result);
    }

    @Operation(summary = "获取结算报告")
    @GetMapping("/report/settle")
    public Result<PageResult<SettleReportResponse>> getSettleReport(
            @RequestParam(required = false) String month,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        checkAdminRole();
        return Result.success(adminService.getSettleReport(month, page, size));
    }

    @Operation(summary = "获取平台统计")
    @GetMapping("/stats/platform")
    public Result<Map<String, Object>> getPlatformStats() {
        checkAdminRole();
        return Result.success(adminService.getPlatformStats());
    }
}
