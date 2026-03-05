package com.guitar.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PracticeResponse {

    private Long id;
    private String courseTitle;
    private String videoUrl;
    private String status;
    private BigDecimal totalScore;
    private String teacherFeedback;
    private String studentName;
    private String studentAvatar;
    private LocalDateTime createdAt;
    private LocalDateTime gradedAt;
    private Long studentId;
    private Long courseId;
}
