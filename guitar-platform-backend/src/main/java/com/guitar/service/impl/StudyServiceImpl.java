package com.guitar.service.impl;

import com.guitar.common.PageResult;
import com.guitar.common.exception.BusinessException;
import com.guitar.dto.response.StudyRecordResponse;
import com.guitar.entity.StudyRecord;
import com.guitar.mapper.*;
import com.guitar.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudyServiceImpl implements StudyService {

    private final StudyRecordMapper studyRecordMapper;
    private final CourseOrderMapper courseOrderMapper;
    private final CourseVideoMapper courseVideoMapper;

    @Override
    public PageResult<StudyRecordResponse> getStudyRecords(Long studentId, int page, int size) {
        long offset = (long) (page - 1) * size;
        List<StudyRecordResponse> records = studyRecordMapper.selectStudyRecords(studentId, offset, size);
        long total = studyRecordMapper.countStudyRecords(studentId);
        return PageResult.of(total, records);
    }

    @Override
    @Transactional
    public void updateProgress(Long studentId, Long courseId, Long videoId, Integer progress, Integer lastPosition) {
        // Verify student has purchased the course
        int orderCount = courseOrderMapper.countByStudentIdAndCourseId(studentId, courseId);
        if (orderCount == 0) {
            throw new BusinessException(403, "您未购买此课程");
        }

        StudyRecord existing = studyRecordMapper.findByStudentCourseVideo(studentId, courseId, videoId);
        if (existing == null) {
            StudyRecord record = new StudyRecord();
            record.setStudentId(studentId);
            record.setCourseId(courseId);
            record.setVideoId(videoId);
            record.setProgress(progress);
            record.setLastPosition(lastPosition != null ? lastPosition : 0);
            studyRecordMapper.insert(record);
        } else {
            // Only update if new progress is greater
            if (progress > existing.getProgress()) {
                existing.setProgress(progress);
            }
            if (lastPosition != null) {
                existing.setLastPosition(lastPosition);
            }
            studyRecordMapper.updateById(existing);
        }
    }

    @Override
    public Map<String, Object> getStudyStats(Long studentId) {
        int courseCount = studyRecordMapper.countDistinctCoursesByStudentId(studentId);

        // Sum total duration of watched videos based on study records
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.guitar.entity.StudyRecord> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.guitar.entity.StudyRecord>()
                        .eq(com.guitar.entity.StudyRecord::getStudentId, studentId);
        java.util.List<com.guitar.entity.StudyRecord> records = studyRecordMapper.selectList(wrapper);
        int totalDuration = records.stream()
                .mapToInt(r -> {
                    com.guitar.entity.CourseVideo v = courseVideoMapper.selectById(r.getVideoId());
                    if (v != null && v.getDuration() != null) {
                        return (int) (v.getDuration() * r.getProgress() / 100.0);
                    }
                    return 0;
                }).sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("courseCount", courseCount);
        stats.put("totalDuration", totalDuration);
        stats.put("avgScore", 0);
        return stats;
    }
}
