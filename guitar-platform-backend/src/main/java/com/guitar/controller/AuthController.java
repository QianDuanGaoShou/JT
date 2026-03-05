package com.guitar.controller;

import com.guitar.common.Result;
import com.guitar.common.SecurityUtil;
import com.guitar.dto.request.*;
import com.guitar.dto.response.LoginResponse;
import com.guitar.dto.response.UserInfoResponse;
import com.guitar.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证管理", description = "用户注册、登录、刷新Token")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return Result.success("注册成功", null);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success(response);
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refreshToken(request);
        return Result.success(response);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        Long userId = SecurityUtil.getCurrentUserId();
        authService.logout(userId);
        return Result.success("退出成功", null);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<UserInfoResponse> getCurrentUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        UserInfoResponse userInfo = authService.getCurrentUser(userId);
        return Result.success(userInfo);
    }

    @Operation(summary = "更新用户资料")
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody UpdateProfileRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        authService.updateProfile(userId, request);
        return Result.success("更新成功", null);
    }
}
