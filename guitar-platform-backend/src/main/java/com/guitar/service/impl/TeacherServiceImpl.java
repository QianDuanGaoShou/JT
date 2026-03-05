package com.guitar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guitar.common.PageResult;
import com.guitar.common.exception.BusinessException;
import com.guitar.dto.request.CreateChapterRequest;
import com.guitar.dto.request.CreateCourseRequest;
import com.guitar.dto.request.CreateVideoRequest;
import com.guitar.dto.request.UpdateCourseRequest;
import com.guitar.dto.response.IncomeStatsResponse;
import com.guitar.dto.response.MonthlyIncomeData;
import com.guitar.dto.response.TeacherCourseResponse;
import com.guitar.entity.*;
import com.guitar.mapper.*;
import com.guitar.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final CourseMapper courseMapper;
    private final CourseChapterMapper chapterMapper;
    private final CourseVideoMapper videoMapper;
    private final CourseOrderMapper orderMapper;
    private final IncomeRecordMapper incomeRecordMapper;
    private final PracticeSubmissionMapper practiceMapper;
    private final SysUserMapper userMapper;

    @Override
    @Transactional
    public Course createCourse(Long teacherId, CreateCourseRequest request) {
        // Check teacher is approved
        SysUser teacher = userMapper.selectById(teacherId);
        if (teacher == null || !"APPROVED".equals(teacher.getAuditStatus())) {
            throw new BusinessException(403, "教师资质未通过审核，无法创建课程");
        }

        Course course = new Course();
        BeanUtils.copyProperties(request, course);
        course.setTeacherId(teacherId);
        course.setStatus("DRAFT");
        courseMapper.insert(course);
        return course;
    }

    @Override
    @Transactional
    public void updateCourse(Long courseId, Long teacherId, UpdateCourseRequest request) {
        Course course = getCourseByIdAndTeacher(courseId, teacherId);

        if (request.getTitle() != null) course.setTitle(request.getTitle());
        if (request.getCategory() != null) course.setCategory(request.getCategory());
        if (request.getPrice() != null) course.setPrice(request.getPrice());
        if (request.getCoverUrl() != null) course.setCoverUrl(request.getCoverUrl());
        if (request.getDescription() != null) course.setDescription(request.getDescription());
        if (request.getStatus() != null) course.setStatus(request.getStatus());

        courseMapper.updateById(course);
    }

    @Override
    @Transactional
    public void deleteCourse(Long courseId, Long teacherId) {
        Course course = getCourseByIdAndTeacher(courseId, teacherId);
        if ("PUBLISHED".equals(course.getStatus())) {
            throw new BusinessException(400, "已发布的课程无法删除，请先下线");
        }
        courseMapper.deleteById(courseId);
    }

    @Override
    public PageResult<TeacherCourseResponse> getMyCourses(Long teacherId, int page, int size, String status) {
        Page<Course> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<Course>()
                .eq(Course::getTeacherId, teacherId)
                .eq(status != null, Course::getStatus, status)
                .orderByDesc(Course::getCreatedAt);

        com.baomidou.mybatisplus.core.metadata.IPage<Course> result = courseMapper.selectPage(pageObj, wrapper);

        List<TeacherCourseResponse> list = result.getRecords().stream().map(course -> {
            TeacherCourseResponse resp = new TeacherCourseResponse();
            BeanUtils.copyProperties(course, resp);

            int studentCount = orderMapper.countByCourseId(course.getId());
            resp.setStudentCount(studentCount);

            BigDecimal totalIncome = incomeRecordMapper.sumTotalByTeacherId(teacherId);
            resp.setTotalIncome(totalIncome != null ? totalIncome : BigDecimal.ZERO);
            return resp;
        }).collect(Collectors.toList());

        return PageResult.of(result.getTotal(), list);
    }

    @Override
    @Transactional
    public CourseChapter addChapter(CreateChapterRequest request, Long teacherId) {
        getCourseByIdAndTeacher(request.getCourseId(), teacherId);

        CourseChapter chapter = new CourseChapter();
        chapter.setCourseId(request.getCourseId());
        chapter.setTitle(request.getTitle());
        chapter.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 1);
        chapterMapper.insert(chapter);
        return chapter;
    }

    @Override
    @Transactional
    public void updateChapter(Long chapterId, Long teacherId, String title, Integer sortOrder) {
        CourseChapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null) {
            throw new BusinessException(404, "章节不存在");
        }
        getCourseByIdAndTeacher(chapter.getCourseId(), teacherId);

        if (title != null) chapter.setTitle(title);
        if (sortOrder != null) chapter.setSortOrder(sortOrder);
        chapterMapper.updateById(chapter);
    }

    @Override
    @Transactional
    public void deleteChapter(Long chapterId, Long teacherId) {
        CourseChapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null) {
            throw new BusinessException(404, "章节不存在");
        }
        getCourseByIdAndTeacher(chapter.getCourseId(), teacherId);
        chapterMapper.deleteById(chapterId);
    }

    @Override
    @Transactional
    public CourseVideo addVideo(CreateVideoRequest request, Long teacherId) {
        CourseChapter chapter = chapterMapper.selectById(request.getChapterId());
        if (chapter == null) {
            throw new BusinessException(404, "章节不存在");
        }
        getCourseByIdAndTeacher(chapter.getCourseId(), teacherId);

        CourseVideo video = new CourseVideo();
        BeanUtils.copyProperties(request, video);
        videoMapper.insert(video);
        return video;
    }

    @Override
    @Transactional
    public void updateVideo(Long videoId, Long teacherId, String title, Integer sortOrder) {
        CourseVideo video = videoMapper.selectById(videoId);
        if (video == null) {
            throw new BusinessException(404, "视频不存在");
        }
        CourseChapter chapter = chapterMapper.selectById(video.getChapterId());
        getCourseByIdAndTeacher(chapter.getCourseId(), teacherId);

        if (title != null) video.setTitle(title);
        if (sortOrder != null) video.setSortOrder(sortOrder);
        videoMapper.updateById(video);
    }

    @Override
    @Transactional
    public void deleteVideo(Long videoId, Long teacherId) {
        CourseVideo video = videoMapper.selectById(videoId);
        if (video == null) {
            throw new BusinessException(404, "视频不存在");
        }
        CourseChapter chapter = chapterMapper.selectById(video.getChapterId());
        getCourseByIdAndTeacher(chapter.getCourseId(), teacherId);
        videoMapper.deleteById(videoId);
    }

    @Override
    public IncomeStatsResponse getIncomeStats(Long teacherId, String startMonth, String endMonth) {
        if (startMonth == null) {
            startMonth = LocalDateTime.now().minusMonths(5)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        if (endMonth == null) {
            endMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }

        BigDecimal totalIncome = incomeRecordMapper.sumTotalByTeacherId(teacherId);
        String currentMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        BigDecimal currentMonthIncome = incomeRecordMapper.sumByTeacherIdAndMonth(teacherId, currentMonth);

        int studentCount = orderMapper.selectCount(
                new LambdaQueryWrapper<CourseOrder>().in(CourseOrder::getCourseId,
                        courseMapper.selectList(
                                new LambdaQueryWrapper<Course>().eq(Course::getTeacherId, teacherId)
                                        .select(Course::getId)
                        ).stream().map(Course::getId).collect(Collectors.toList())
                )
        ).intValue();

        int pendingGradeCount = practiceMapper.countPendingByTeacherId(teacherId);

        List<Map<String, Object>> rawMonthly = incomeRecordMapper.selectMonthlyStats(teacherId, startMonth, endMonth);
        List<MonthlyIncomeData> monthlyData = rawMonthly.stream().map(m -> {
            MonthlyIncomeData md = new MonthlyIncomeData();
            md.setMonth(m.get("month").toString());
            md.setIncome(new BigDecimal(m.get("income").toString()));
            md.setStudentCount(Integer.parseInt(m.get("studentCount").toString()));
            return md;
        }).collect(Collectors.toList());

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", monthlyData.stream().map(MonthlyIncomeData::getMonth).collect(Collectors.toList()));
        chartData.put("incomeData", monthlyData.stream().map(MonthlyIncomeData::getIncome).collect(Collectors.toList()));

        IncomeStatsResponse response = new IncomeStatsResponse();
        response.setTotalIncome(totalIncome != null ? totalIncome : BigDecimal.ZERO);
        response.setCurrentMonthIncome(currentMonthIncome != null ? currentMonthIncome : BigDecimal.ZERO);
        response.setStudentCount(studentCount);
        response.setPendingGradeCount(pendingGradeCount);
        response.setMonthlyData(monthlyData);
        response.setChartData(chartData);

        return response;
    }

    private Course getCourseByIdAndTeacher(Long courseId, Long teacherId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }
        if (!course.getTeacherId().equals(teacherId)) {
            throw new BusinessException(403, "无权操作此课程");
        }
        return course;
    }
}
