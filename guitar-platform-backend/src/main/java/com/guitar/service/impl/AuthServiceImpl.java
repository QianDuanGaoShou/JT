package com.guitar.service.impl;

import com.guitar.common.JwtUtil;
import com.guitar.common.exception.BusinessException;
import com.guitar.dto.request.LoginRequest;
import com.guitar.dto.request.RefreshTokenRequest;
import com.guitar.dto.request.RegisterRequest;
import com.guitar.dto.request.UpdateProfileRequest;
import com.guitar.dto.response.LoginResponse;
import com.guitar.dto.response.UserInfoResponse;
import com.guitar.entity.SysUser;
import com.guitar.entity.UserRefreshToken;
import com.guitar.mapper.SysUserMapper;
import com.guitar.mapper.UserRefreshTokenMapper;
import com.guitar.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final UserRefreshTokenMapper refreshTokenMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (sysUserMapper.countByUsername(request.getUsername()) > 0) {
            throw new BusinessException(400, "用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setRole(request.getRole());
        user.setEnabled(true);
        user.setBalance(BigDecimal.ZERO);

        if ("TEACHER".equals(request.getRole())) {
            user.setQualificationUrl(request.getQualificationUrl());
            user.setAuditStatus("PENDING");
        }

        sysUserMapper.insert(user);
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        SysUser user = sysUserMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(400, "用户名或密码错误");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(400, "用户名或密码错误");
        }
        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new BusinessException(403, "账号已被禁用");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // Delete old refresh tokens and save new one
        refreshTokenMapper.deleteByUserId(user.getId());
        UserRefreshToken tokenEntity = new UserRefreshToken();
        tokenEntity.setUserId(user.getId());
        tokenEntity.setRefreshToken(refreshToken);
        tokenEntity.setExpiresAt(LocalDateTime.now().plusDays(30));
        refreshTokenMapper.insert(tokenEntity);

        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(7200L);
        response.setUserInfo(toUserInfoResponse(user));

        return response;
    }

    @Override
    @Transactional
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        UserRefreshToken tokenEntity = refreshTokenMapper.findByRefreshToken(request.getRefreshToken());
        if (tokenEntity == null || tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(401, "refreshToken已过期，请重新登录");
        }

        SysUser user = sysUserMapper.selectById(tokenEntity.getUserId());
        if (user == null || !Boolean.TRUE.equals(user.getEnabled())) {
            throw new BusinessException(401, "用户不存在或已被禁用");
        }

        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole());

        LoginResponse response = new LoginResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(request.getRefreshToken());
        response.setExpiresIn(7200L);
        response.setUserInfo(toUserInfoResponse(user));

        return response;
    }

    @Override
    @Transactional
    public void logout(Long userId) {
        refreshTokenMapper.deleteByUserId(userId);
    }

    @Override
    public UserInfoResponse getCurrentUser(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return toUserInfoResponse(user);
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        sysUserMapper.updateById(user);
    }

    private UserInfoResponse toUserInfoResponse(SysUser user) {
        UserInfoResponse resp = new UserInfoResponse();
        BeanUtils.copyProperties(user, resp);
        return resp;
    }
}
