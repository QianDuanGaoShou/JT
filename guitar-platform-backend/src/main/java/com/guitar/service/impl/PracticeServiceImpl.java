package com.guitar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guitar.common.PageResult;
import com.guitar.common.exception.BusinessException;
import com.guitar.dto.response.PracticeResponse;
import com.guitar.entity.Course;
import com.guitar.entity.PracticeSubmission;
import com.guitar.entity.SysUser;
import com.guitar.mapper.CourseMapper;
import com.guitar.mapper.CourseOrderMapper;
import com.guitar.mapper.PracticeSubmissionMapper;
import com.guitar.mapper.SysUserMapper;
import com.guitar.service.PracticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PracticeServiceImpl implements PracticeService {

    private final PracticeSubmissionMapper practiceMapper;
    private final CourseMapper courseMapper;
    private final SysUserMapper userMapper;
    private final CourseOrderMapper orderMapper;

    @Override
    @Transactional
    public PracticeResponse submitPractice(Long studentId, Long courseId, String videoUrl) {
        int orderCount = orderMapper.countByStudentIdAndCourseId(studentId, courseId);
        if (orderCount == 0) {
            throw new BusinessException(403, "您未购买此课程，无法提交练习");
        }

        PracticeSubmission submission = new PracticeSubmission();
        submission.setStudentId(studentId);
        submission.setCourseId(courseId);
        submission.setVideoUrl(videoUrl);
        submission.setStatus("PENDING");
        practiceMapper.insert(submission);

        return toPracticeResponse(submission);
    }

    @Override
    public PageResult<PracticeResponse> getMyPractices(Long studentId, int page, int size, String status) {
        Page<PracticeSubmission> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<PracticeSubmission> wrapper = new LambdaQueryWrapper<PracticeSubmission>()
                .eq(PracticeSubmission::getStudentId, studentId)
                .eq(status != null, PracticeSubmission::getStatus, status)
                .orderByDesc(PracticeSubmission::getCreatedAt);

        com.baomidou.mybatisplus.core.metadata.IPage<PracticeSubmission> result = practiceMapper.selectPage(pageObj, wrapper);
        List<PracticeResponse> list = result.getRecords().stream()
                .map(this::toPracticeResponse).collect(Collectors.toList());

        return PageResult.of(result.getTotal(), list);
    }

    @Override
    public PracticeResponse getPracticeReport(Long practiceId, Long studentId) {
        PracticeSubmission submission = practiceMapper.selectById(practiceId);
        if (submission == null) {
            throw new BusinessException(404, "练习记录不存在");
        }
        if (!submission.getStudentId().equals(studentId)) {
            throw new BusinessException(403, "无权查看此练习记录");
        }
        return toPracticeResponse(submission);
    }

    @Override
    public PageResult<PracticeResponse> getPendingPractices(Long teacherId, int page, int size, Long courseId) {
        Page<PracticeSubmission> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<PracticeSubmission> wrapper = new LambdaQueryWrapper<PracticeSubmission>()
                .eq(courseId != null, PracticeSubmission::getCourseId, courseId)
                .orderByDesc(PracticeSubmission::getCreatedAt);

        // Filter by teacher's courses
        if (courseId == null) {
            List<Course> teacherCourses = courseMapper.selectList(
                    new LambdaQueryWrapper<Course>().eq(Course::getTeacherId, teacherId)
            );
            if (teacherCourses.isEmpty()) {
                return PageResult.of(0, List.of());
            }
            List<Long> courseIds = teacherCourses.stream().map(Course::getId).collect(Collectors.toList());
            wrapper.in(PracticeSubmission::getCourseId, courseIds);
        }

        com.baomidou.mybatisplus.core.metadata.IPage<PracticeSubmission> result = practiceMapper.selectPage(pageObj, wrapper);
        List<PracticeResponse> list = result.getRecords().stream()
                .map(this::toPracticeResponse).collect(Collectors.toList());

        return PageResult.of(result.getTotal(), list);
    }

    @Override
    @Transactional
    public Map<String, Object> gradePractice(Long practiceId, Long teacherId, BigDecimal totalScore, String feedback) {
        PracticeSubmission submission = practiceMapper.selectById(practiceId);
        if (submission == null) {
            throw new BusinessException(404, "练习记录不存在");
        }

        Course course = courseMapper.selectById(submission.getCourseId());
        if (course == null || !course.getTeacherId().equals(teacherId)) {
            throw new BusinessException(403, "无权评分此练习");
        }

        submission.setTotalScore(totalScore);
        submission.setTeacherFeedback(feedback);
        submission.setStatus("GRADED");
        submission.setGradedAt(LocalDateTime.now());
        practiceMapper.updateById(submission);

        Map<String, Object> result = new HashMap<>();
        result.put("practiceId", practiceId);
        result.put("totalScore", totalScore);
        result.put("status", "GRADED");
        return result;
    }

    private PracticeResponse toPracticeResponse(PracticeSubmission submission) {
        PracticeResponse resp = new PracticeResponse();
        BeanUtils.copyProperties(submission, resp);

        Course course = courseMapper.selectById(submission.getCourseId());
        if (course != null) {
            resp.setCourseTitle(course.getTitle());
        }

        SysUser student = userMapper.selectById(submission.getStudentId());
        if (student != null) {
            resp.setStudentName(student.getNickname());
            resp.setStudentAvatar(student.getAvatar());
        }

        return resp;
    }
}
