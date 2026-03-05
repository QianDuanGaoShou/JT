package com.guitar.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CourseListItemResponse {

    private Long id;
    private String title;
    private String category;
    private BigDecimal price;
    private String coverUrl;
    private Integer totalDuration;
    private Integer chapterCount;
    private String teacherName;
    private Long teacherId;
    private String status;
    private LocalDateTime createdAt;
    private Boolean isPurchased;
}
