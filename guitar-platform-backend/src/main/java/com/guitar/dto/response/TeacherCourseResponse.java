package com.guitar.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TeacherCourseResponse {

    private Long id;
    private String title;
    private String category;
    private BigDecimal price;
    private String status;
    private Integer studentCount;
    private BigDecimal totalIncome;
    private LocalDateTime createdAt;
}
