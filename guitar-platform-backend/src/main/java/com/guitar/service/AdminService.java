package com.guitar.service;

import com.guitar.common.PageResult;
import com.guitar.dto.response.SettleReportResponse;
import com.guitar.dto.response.UserInfoResponse;

import java.math.BigDecimal;
import java.util.Map;

public interface AdminService {

    PageResult<UserInfoResponse> getPendingTeachers(int page, int size);

    void auditTeacher(Long teacherId, String status, String reason);

    PageResult<UserInfoResponse> getAllTeachers(int page, int size);

    void toggleUserStatus(Long userId);

    PageResult<UserInfoResponse> getAllUsers(int page, int size, String role);

    Map<String, Object> rechargeBalance(Long studentId, BigDecimal amount, String remark);

    PageResult<SettleReportResponse> getSettleReport(String month, int page, int size);

    Map<String, Object> getPlatformStats();
}
