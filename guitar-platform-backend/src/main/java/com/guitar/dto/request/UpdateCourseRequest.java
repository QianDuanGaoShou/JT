package com.guitar.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateCourseRequest {

    private String title;
    private String category;
    private BigDecimal price;
    private String coverUrl;
    private String description;
    private String status; // DRAFT, PUBLISHED, OFFLINE
}
