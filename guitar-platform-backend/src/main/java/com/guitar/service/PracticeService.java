package com.guitar.service;

import com.guitar.common.PageResult;
import com.guitar.dto.response.PracticeResponse;

import java.math.BigDecimal;
import java.util.Map;

public interface PracticeService {

    PracticeResponse submitPractice(Long studentId, Long courseId, String videoUrl);

    PageResult<PracticeResponse> getMyPractices(Long studentId, int page, int size, String status);

    PracticeResponse getPracticeReport(Long practiceId, Long studentId);

    PageResult<PracticeResponse> getPendingPractices(Long teacherId, int page, int size, Long courseId);

    Map<String, Object> gradePractice(Long practiceId, Long teacherId, BigDecimal totalScore, String feedback);
}
