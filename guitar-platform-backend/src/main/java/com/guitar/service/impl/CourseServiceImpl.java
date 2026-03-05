package com.guitar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guitar.common.PageResult;
import com.guitar.common.exception.BusinessException;
import com.guitar.dto.response.*;
import com.guitar.entity.*;
import com.guitar.mapper.*;
import com.guitar.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;
    private final CourseChapterMapper chapterMapper;
    private final CourseVideoMapper videoMapper;
    private final CourseOrderMapper orderMapper;
    private final SysUserMapper userMapper;
    private final IncomeRecordMapper incomeRecordMapper;

    @Override
    public PageResult<CourseListItemResponse> getCourseList(int page, int size, String category, String keyword, Long currentUserId) {
        Page<CourseListItemResponse> pageObj = new Page<>(page, size);
        com.baomidou.mybatisplus.core.metadata.IPage<CourseListItemResponse> result =
                courseMapper.selectCourseList(pageObj, category, keyword, currentUserId, "PUBLISHED");
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    public CourseDetailResponse getCourseDetail(Long id, Long currentUserId) {
        CourseListItemResponse base = courseMapper.selectCourseDetail(id, currentUserId);
        if (base == null) {
            throw new BusinessException(404, "课程不存在");
        }

        Course course = courseMapper.selectById(id);

        CourseDetailResponse detail = new CourseDetailResponse();
        BeanUtils.copyProperties(base, detail);
        detail.setDescription(course.getDescription());

        // Load chapters and videos
        List<CourseChapter> chapters = chapterMapper.findByCourseId(id);
        List<ChapterResponse> chapterResponses = chapters.stream().map(chapter -> {
            ChapterResponse cr = new ChapterResponse();
            cr.setId(chapter.getId());
            cr.setTitle(chapter.getTitle());
            cr.setSortOrder(chapter.getSortOrder());

            List<CourseVideo> videos = videoMapper.findByChapterId(chapter.getId());
            List<VideoResponse> videoResponses = videos.stream().map(v -> {
                VideoResponse vr = new VideoResponse();
                BeanUtils.copyProperties(v, vr);
                return vr;
            }).collect(Collectors.toList());
            cr.setVideos(videoResponses);
            return cr;
        }).collect(Collectors.toList());

        detail.setChapters(chapterResponses);

        // Teacher info
        SysUser teacher = userMapper.selectById(course.getTeacherId());
        if (teacher != null) {
            UserInfoResponse teacherInfo = new UserInfoResponse();
            BeanUtils.copyProperties(teacher, teacherInfo);
            detail.setTeacherInfo(teacherInfo);
        }

        // Study progress
        if (currentUserId != null) {
            Map<String, Object> progress = new HashMap<>();
            progress.put("purchased", base.getIsPurchased());
            detail.setStudyProgress(progress);
        }

        return detail;
    }

    @Override
    @Transactional
    public Map<String, Object> purchaseCourse(Long courseId, Long studentId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null || !"PUBLISHED".equals(course.getStatus())) {
            throw new BusinessException(404, "课程不存在或未发布");
        }

        // Check already purchased
        int count = orderMapper.countByStudentIdAndCourseId(studentId, courseId);
        if (count > 0) {
            throw new BusinessException(400, "您已购买此课程");
        }

        SysUser student = userMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException(404, "用户不存在");
        }

        BigDecimal price = course.getPrice();
        if (student.getBalance().compareTo(price) < 0) {
            throw new BusinessException(400, "余额不足，请充值");
        }

        // Deduct balance
        student.setBalance(student.getBalance().subtract(price));
        userMapper.updateById(student);

        // Create order
        CourseOrder order = new CourseOrder();
        order.setOrderNo(generateOrderNo());
        order.setStudentId(studentId);
        order.setCourseId(courseId);
        order.setAmount(price);
        orderMapper.insert(order);

        // Create income record for teacher
        IncomeRecord incomeRecord = new IncomeRecord();
        incomeRecord.setTeacherId(course.getTeacherId());
        incomeRecord.setCourseId(courseId);
        incomeRecord.setOrderId(order.getId());
        incomeRecord.setAmount(price);
        incomeRecord.setType("COURSE_PURCHASE");
        incomeRecord.setSettleMonth(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")
                .format(LocalDateTime.now()));
        incomeRecordMapper.insert(incomeRecord);

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getId());
        result.put("orderNo", order.getOrderNo());
        result.put("amount", price);
        result.put("balance", student.getBalance());
        return result;
    }

    @Override
    public PageResult<OrderResponse> getMyOrders(Long studentId, int page, int size) {
        Page<CourseOrder> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<CourseOrder> wrapper = new LambdaQueryWrapper<CourseOrder>()
                .eq(CourseOrder::getStudentId, studentId)
                .orderByDesc(CourseOrder::getCreatedAt);

        com.baomidou.mybatisplus.core.metadata.IPage<CourseOrder> result = orderMapper.selectPage(pageObj, wrapper);

        List<OrderResponse> list = result.getRecords().stream().map(order -> {
            OrderResponse resp = new OrderResponse();
            resp.setOrderNo(order.getOrderNo());
            resp.setCourseId(order.getCourseId());
            resp.setAmount(order.getAmount());
            resp.setCreatedAt(order.getCreatedAt());

            Course course = courseMapper.selectById(order.getCourseId());
            if (course != null) {
                resp.setCourseTitle(course.getTitle());
            }
            return resp;
        }).collect(Collectors.toList());

        return PageResult.of(result.getTotal(), list);
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + (int) (Math.random() * 1000);
    }
}
