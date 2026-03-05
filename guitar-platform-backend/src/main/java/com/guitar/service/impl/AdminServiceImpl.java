package com.guitar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guitar.common.PageResult;
import com.guitar.common.exception.BusinessException;
import com.guitar.dto.response.SettleReportResponse;
import com.guitar.dto.response.UserInfoResponse;
import com.guitar.entity.*;
import com.guitar.mapper.*;
import com.guitar.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final SysUserMapper userMapper;
    private final CourseMapper courseMapper;
    private final CourseOrderMapper orderMapper;
    private final IncomeRecordMapper incomeRecordMapper;
    private final PracticeSubmissionMapper practiceMapper;

    private static final BigDecimal PLATFORM_FEE_RATE = new BigDecimal("0.20");

    @Override
    public PageResult<UserInfoResponse> getPendingTeachers(int page, int size) {
        Page<SysUser> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getRole, "TEACHER")
                .eq(SysUser::getAuditStatus, "PENDING")
                .orderByDesc(SysUser::getCreatedAt);

        com.baomidou.mybatisplus.core.metadata.IPage<SysUser> result = userMapper.selectPage(pageObj, wrapper);
        List<UserInfoResponse> list = result.getRecords().stream()
                .map(this::toUserInfoResponse).collect(Collectors.toList());

        return PageResult.of(result.getTotal(), list);
    }

    @Override
    @Transactional
    public void auditTeacher(Long teacherId, String status, String reason) {
        SysUser teacher = userMapper.selectById(teacherId);
        if (teacher == null || !"TEACHER".equals(teacher.getRole())) {
            throw new BusinessException(404, "教师不存在");
        }

        teacher.setAuditStatus(status);
        if (reason != null) {
            teacher.setAuditReason(reason);
        }
        userMapper.updateById(teacher);
    }

    @Override
    public PageResult<UserInfoResponse> getAllTeachers(int page, int size) {
        Page<SysUser> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getRole, "TEACHER")
                .orderByDesc(SysUser::getCreatedAt);

        com.baomidou.mybatisplus.core.metadata.IPage<SysUser> result = userMapper.selectPage(pageObj, wrapper);
        List<UserInfoResponse> list = result.getRecords().stream()
                .map(this::toUserInfoResponse).collect(Collectors.toList());

        return PageResult.of(result.getTotal(), list);
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setEnabled(!Boolean.TRUE.equals(user.getEnabled()));
        userMapper.updateById(user);
    }

    @Override
    public PageResult<UserInfoResponse> getAllUsers(int page, int size, String role) {
        Page<SysUser> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .eq(role != null, SysUser::getRole, role)
                .orderByDesc(SysUser::getCreatedAt);

        com.baomidou.mybatisplus.core.metadata.IPage<SysUser> result = userMapper.selectPage(pageObj, wrapper);
        List<UserInfoResponse> list = result.getRecords().stream()
                .map(this::toUserInfoResponse).collect(Collectors.toList());

        return PageResult.of(result.getTotal(), list);
    }

    @Override
    @Transactional
    public Map<String, Object> rechargeBalance(Long studentId, BigDecimal amount, String remark) {
        SysUser student = userMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException(404, "用户不存在");
        }

        student.setBalance(student.getBalance().add(amount));
        userMapper.updateById(student);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", studentId);
        result.put("amount", amount);
        result.put("balance", student.getBalance());
        result.put("remark", remark);
        return result;
    }

    @Override
    public PageResult<SettleReportResponse> getSettleReport(String month, int page, int size) {
        if (month == null) {
            month = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }

        // Get all teachers
        Page<SysUser> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getRole, "TEACHER")
                .eq(SysUser::getAuditStatus, "APPROVED");
        com.baomidou.mybatisplus.core.metadata.IPage<SysUser> teachers = userMapper.selectPage(pageObj, wrapper);

        final String finalMonth = month;
        List<SettleReportResponse> list = teachers.getRecords().stream().map(teacher -> {
            SettleReportResponse report = new SettleReportResponse();
            report.setTeacherId(teacher.getId());
            report.setTeacherName(teacher.getNickname());

            long courseCount = courseMapper.selectCount(
                    new LambdaQueryWrapper<Course>().eq(Course::getTeacherId, teacher.getId())
            );
            report.setCourseCount((int) courseCount);

            List<Course> courses = courseMapper.selectList(
                    new LambdaQueryWrapper<Course>().eq(Course::getTeacherId, teacher.getId())
            );
            int studentCount = courses.stream()
                    .mapToInt(c -> orderMapper.countByCourseId(c.getId()))
                    .sum();
            report.setStudentCount(studentCount);

            BigDecimal totalIncome = incomeRecordMapper.sumAmountByTeacherAndMonth(teacher.getId(), finalMonth);
            if (totalIncome == null) totalIncome = BigDecimal.ZERO;
            report.setTotalIncome(totalIncome);

            BigDecimal platformFee = totalIncome.multiply(PLATFORM_FEE_RATE).setScale(2, RoundingMode.HALF_UP);
            report.setPlatformFee(platformFee);
            report.setTeacherIncome(totalIncome.subtract(platformFee));
            report.setSettleStatus("PENDING");

            return report;
        }).collect(Collectors.toList());

        return PageResult.of(teachers.getTotal(), list);
    }

    @Override
    public Map<String, Object> getPlatformStats() {
        long totalUsers = userMapper.selectCount(null);
        long totalStudents = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getRole, "STUDENT"));
        long totalTeachers = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getRole, "TEACHER"));
        long totalCourses = courseMapper.selectCount(null);
        long totalOrders = orderMapper.selectCount(null);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalStudents", totalStudents);
        stats.put("totalTeachers", totalTeachers);
        stats.put("totalCourses", totalCourses);
        stats.put("totalOrders", totalOrders);
        return stats;
    }

    private UserInfoResponse toUserInfoResponse(SysUser user) {
        UserInfoResponse resp = new UserInfoResponse();
        BeanUtils.copyProperties(user, resp);
        return resp;
    }
}
